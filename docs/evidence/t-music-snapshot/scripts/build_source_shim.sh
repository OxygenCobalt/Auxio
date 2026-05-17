#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
if [[ "$(pwd)" != "$ROOT_DIR" ]]; then
  echo "[shim-build] run this script from repo root: $ROOT_DIR" >&2
  exit 10
fi

SRC_DIR="$ROOT_DIR/src-shim/java"
OUT_DIR="$ROOT_DIR/.local/source-shim-build"
CLASSES_DIR="$OUT_DIR/classes"
DEX_DIR="$OUT_DIR/dex"
SMALI_DIR="$OUT_DIR/smali"
CLASSPATH_DIR="$OUT_DIR/classpath"
SOURCES_LIST="$OUT_DIR/java_sources.list"
REQUIRED_SUPPORT_CLASSES=(
  "android/support/v4/media/MediaMetadataCompat.class"
  "android/support/v4/media/session/MediaSessionCompat.class"
  "android/support/v4/media/session/PlaybackStateCompat.class"
  "android/support/v4/app/NotificationCompat.class"
  "android/support/v4/media/app/NotificationCompat\$MediaStyle.class"
)

ANDROID_JAR="${ANDROID_JAR:-}"
SUPPORT_CLASSPATH="${SUPPORT_CLASSPATH:-}"
SUPPORT_V4_JAR="${SUPPORT_V4_JAR:-}"
SUPPORT_AARS="${SUPPORT_AARS:-}"
SEARCH_ROOTS=("$ROOT_DIR")

if [[ -n "${ANDROID_HOME:-}" ]]; then
  SEARCH_ROOTS+=("$ANDROID_HOME")
fi
if [[ -n "${ANDROID_SDK_ROOT:-}" ]]; then
  SEARCH_ROOTS+=("$ANDROID_SDK_ROOT")
fi
if [[ -d "$HOME/Android/Sdk" ]]; then
  SEARCH_ROOTS+=("$HOME/Android/Sdk")
fi
if [[ -d "$HOME/Library/Android/sdk" ]]; then
  SEARCH_ROOTS+=("$HOME/Library/Android/sdk")
fi

find_android_jar() {
  local candidate
  while IFS= read -r candidate; do
    [[ -f "$candidate" ]] || continue
    printf '%s\n' "$candidate"
    return 0
  done < <(find "${SEARCH_ROOTS[@]}" -maxdepth 6 -type f -name android.jar 2>/dev/null)
  return 1
}

auto_detect_support_classpath() {
  local -a candidates=()
  local candidate
  while IFS= read -r candidate; do
    [[ -f "$candidate" ]] || continue
    candidates+=("$candidate")
  done < <(find "${SEARCH_ROOTS[@]}" -maxdepth 8 -type f \( -iname '*support-v4*.jar' -o -iname '*support-compat*.jar' -o -iname '*support-media-compat*.jar' -o -iname '*media-compat*.jar' -o -iname '*androidx.media*.jar' -o -iname '*androidx.core*.jar' \) 2>/dev/null | sort)

  if [[ ${#candidates[@]} -eq 0 ]]; then
    return 1
  fi

  local joined=""
  local c
  for c in "${candidates[@]}"; do
    joined+="${joined:+:}$c"
  done
  printf '%s\n' "$joined"
}

validate_classpath_entries() {
  local label="$1"
  local cp="$2"
  local -a entries=()
  local entry

  if [[ -z "$cp" ]]; then
    echo "[shim-build] $label is empty." >&2
    return 1
  fi

  IFS=':' read -r -a entries <<< "$cp"
  for entry in "${entries[@]}"; do
    if [[ -z "$entry" ]]; then
      echo "[shim-build] $label contains an empty entry (double colon or trailing colon)." >&2
      return 1
    fi
    if [[ ! -f "$entry" ]]; then
      echo "[shim-build] $label entry does not exist: $entry" >&2
      return 1
    fi
  done
  return 0
}

append_to_classpath() {
  local current="$1"
  local extra="$2"
  if [[ -z "$extra" ]]; then
    printf '%s\n' "$current"
  elif [[ -z "$current" ]]; then
    printf '%s\n' "$extra"
  else
    printf '%s:%s\n' "$current" "$extra"
  fi
}

verify_required_support_classes() {
  local cp="$1"
  local missing=0
  local -a cp_entries=()
  local -a dir_entries=()
  local entry
  local required_class

  if ! command -v jar >/dev/null 2>&1; then
    echo "[shim-build] missing required tool: jar (needed for support class verification)." >&2
    echo "[shim-build] provide a preinstalled JDK containing jar and retry." >&2
    return 1
  fi

  local all_jar_classes
  all_jar_classes="$(mktemp)"
  trap 'rm -f "$all_jar_classes"' RETURN

  IFS=':' read -r -a cp_entries <<< "$cp"
  for entry in "${cp_entries[@]}"; do
    if [[ -z "$entry" ]]; then
      continue
    fi
    if [[ "$entry" == *.jar ]]; then
      jar tf "$entry" 2>/dev/null >> "$all_jar_classes"
    elif [[ -d "$entry" ]]; then
      dir_entries+=("$entry")
    fi
  done

  for required_class in "${REQUIRED_SUPPORT_CLASSES[@]}"; do
    if grep -Fxq "$required_class" "$all_jar_classes"; then
      continue
    fi

    local found_in_dir=0
    for entry in "${dir_entries[@]}"; do
      if [[ -f "$entry/$required_class" ]]; then
        found_in_dir=1
        break
      fi
    done

    if (( found_in_dir )); then
      continue
    fi

    echo "[shim-build] missing required support class: $required_class" >&2
    missing=1
  done

  if [[ "$missing" -ne 0 ]]; then
    echo "[shim-build] support class verification failed." >&2
    echo "[shim-build] ensure SUPPORT_CLASSPATH/SUPPORT_AARS contains legacy android.support v4 media/app classes." >&2
    return 1
  fi

  echo "[shim-build] support class verification passed."
  return 0
}

extract_aars_to_classpath() {
  local aars_cp="$1"
  local -a aar_entries=()
  local aar
  local idx=0
  local extracted_cp=""

  mkdir -p "$CLASSPATH_DIR"
  IFS=':' read -r -a aar_entries <<< "$aars_cp"

  for aar in "${aar_entries[@]}"; do
    if [[ -z "$aar" ]]; then
      echo "[shim-build] SUPPORT_AARS contains an empty entry." >&2
      return 1
    fi
    if [[ ! -f "$aar" ]]; then
      echo "[shim-build] SUPPORT_AARS entry does not exist: $aar" >&2
      return 1
    fi

    idx=$((idx + 1))
    local dest_dir="$CLASSPATH_DIR/aar_$idx"
    mkdir -p "$dest_dir"

    if ! command -v unzip >/dev/null 2>&1; then
      echo "[shim-build] unzip is required to process SUPPORT_AARS but was not found." >&2
      return 1
    fi

    unzip -p "$aar" classes.jar > "$dest_dir/classes.jar" || {
      echo "[shim-build] failed to extract classes.jar from AAR: $aar" >&2
      return 1
    }

    extracted_cp="$(append_to_classpath "$extracted_cp" "$dest_dir/classes.jar")"
  done

  printf '%s\n' "$extracted_cp"
}

validate_aars_inputs() {
  local aars_cp="$1"
  local -a aar_entries=()
  local aar

  IFS=':' read -r -a aar_entries <<< "$aars_cp"
  for aar in "${aar_entries[@]}"; do
    if [[ -z "$aar" ]]; then
      echo "[shim-build] SUPPORT_AARS contains an empty entry." >&2
      return 1
    fi
    if [[ ! -f "$aar" ]]; then
      echo "[shim-build] SUPPORT_AARS entry does not exist: $aar" >&2
      return 1
    fi
  done

  if ! command -v unzip >/dev/null 2>&1; then
    echo "[shim-build] unzip is required to process SUPPORT_AARS but was not found." >&2
    return 1
  fi

  return 0
}

if [[ ! -d "$SRC_DIR" ]]; then
  echo "[shim-build] missing source directory: $SRC_DIR" >&2
  exit 11
fi

if ! command -v javac >/dev/null 2>&1; then
  echo "[shim-build] missing required tool: javac" >&2
  echo "[shim-build] install nothing from this script; provide a preinstalled JDK and retry." >&2
  exit 12
fi

if [[ -z "$ANDROID_JAR" ]]; then
  ANDROID_JAR="$(find_android_jar || true)"
fi
if [[ -z "$ANDROID_JAR" || ! -f "$ANDROID_JAR" ]]; then
  echo "[shim-build] missing android.jar." >&2
  echo "[shim-build] set ANDROID_JAR=/absolute/path/to/android-29/android.jar" >&2
  echo "[shim-build] script searches repo/home automatically but found none." >&2
  exit 13
fi

if [[ -z "$SUPPORT_CLASSPATH" && -n "$SUPPORT_V4_JAR" ]]; then
  SUPPORT_CLASSPATH="$SUPPORT_V4_JAR"
fi
if [[ -z "$SUPPORT_CLASSPATH" ]]; then
  SUPPORT_CLASSPATH="$(auto_detect_support_classpath || true)"
fi

if [[ -n "$SUPPORT_CLASSPATH" ]] && ! validate_classpath_entries "SUPPORT_CLASSPATH" "$SUPPORT_CLASSPATH"; then
  echo "[shim-build] missing support classpath entries." >&2
  echo "[shim-build] provide one of:" >&2
  echo "[shim-build]   SUPPORT_CLASSPATH=/abs/a.jar:/abs/b.jar" >&2
  echo "[shim-build]   SUPPORT_AARS=/abs/support-compat.aar:/abs/support-media-compat.aar" >&2
  echo "[shim-build] backward-compatible fallback: SUPPORT_V4_JAR=/abs/single-support.jar" >&2
  exit 14
fi

if [[ -n "$SUPPORT_AARS" ]] && ! validate_aars_inputs "$SUPPORT_AARS"; then
  echo "[shim-build] failed to validate SUPPORT_AARS inputs." >&2
  exit 15
fi

if [[ -z "$SUPPORT_CLASSPATH" && -z "$SUPPORT_AARS" ]]; then
  echo "[shim-build] missing support classpath entries." >&2
  echo "[shim-build] provide one of:" >&2
  echo "[shim-build]   SUPPORT_CLASSPATH=/abs/a.jar:/abs/b.jar" >&2
  echo "[shim-build]   SUPPORT_AARS=/abs/support-compat.aar:/abs/support-media-compat.aar" >&2
  echo "[shim-build] backward-compatible fallback: SUPPORT_V4_JAR=/abs/single-support.jar" >&2
  exit 14
fi

rm -rf "$OUT_DIR"
mkdir -p "$CLASSES_DIR" "$DEX_DIR" "$SMALI_DIR" "$CLASSPATH_DIR"

if [[ -n "$SUPPORT_AARS" ]]; then
  extracted_aars_cp="$(extract_aars_to_classpath "$SUPPORT_AARS")" || {
    echo "[shim-build] failed to build classpath from SUPPORT_AARS." >&2
    exit 15
  }
  SUPPORT_CLASSPATH="$(append_to_classpath "$SUPPORT_CLASSPATH" "$extracted_aars_cp")"
fi

if ! validate_classpath_entries "SUPPORT_CLASSPATH" "$SUPPORT_CLASSPATH"; then
  echo "[shim-build] missing support classpath entries." >&2
  echo "[shim-build] provide one of:" >&2
  echo "[shim-build]   SUPPORT_CLASSPATH=/abs/a.jar:/abs/b.jar" >&2
  echo "[shim-build]   SUPPORT_AARS=/abs/support-compat.aar:/abs/support-media-compat.aar" >&2
  echo "[shim-build] backward-compatible fallback: SUPPORT_V4_JAR=/abs/single-support.jar" >&2
  exit 14
fi

if ! verify_required_support_classes "$SUPPORT_CLASSPATH"; then
  exit 16
fi

find "$SRC_DIR" -name '*.java' -print | sort > "$SOURCES_LIST"

FULL_CLASSPATH="$ANDROID_JAR:$SUPPORT_CLASSPATH"

javac -classpath "$FULL_CLASSPATH" -d "$CLASSES_DIR" @"$SOURCES_LIST"

echo "[shim-build] compiled classes at: $CLASSES_DIR"

REPO_ROOT="${ROOT:-$PWD}"
SHIM_BUILD_ROOT="${BUILD_DIR:-${OUT_DIR:-${SOURCE_SHIM_BUILD_DIR:-${LOCAL_BUILD_DIR:-$REPO_ROOT/.local/source-shim-build}}}}"
SHIM_CLASS_DIR="${CLASS_DIR:-${CLASSES_DIR:-${CLASS_OUTPUT_DIR:-$SHIM_BUILD_ROOT/classes}}}"

dex_path=""
if command -v d8 >/dev/null 2>&1; then
  command -v jar >/dev/null 2>&1 || fail "jar is required to package compiled source-shim classes before d8"

  [ -d "$SHIM_CLASS_DIR" ] || fail "compiled source-shim class directory missing: $SHIM_CLASS_DIR"

  PROGRAM_JAR="$SHIM_BUILD_ROOT/source-shim-program.jar"
  rm -f "$PROGRAM_JAR"
  (
    cd "$SHIM_CLASS_DIR"
    jar cf "$PROGRAM_JAR" .
  )

  mkdir -p "$DEX_DIR"

  D8_ARGS=(--min-api 29 --lib "$ANDROID_JAR" --output "$DEX_DIR")

  if [ -n "${EFFECTIVE_SUPPORT_CLASSPATH:-}" ]; then
    IFS=':' read -r -a SUPPORT_CP_ENTRIES <<< "$EFFECTIVE_SUPPORT_CLASSPATH"
    for cp_entry in "${SUPPORT_CP_ENTRIES[@]}"; do
      [ -n "$cp_entry" ] && D8_ARGS+=(--classpath "$cp_entry")
    done
  fi

  D8_ARGS+=("$PROGRAM_JAR")
  d8 "${D8_ARGS[@]}"
  dex_path="$DEX_DIR/classes.dex"
elif command -v dx >/dev/null 2>&1; then
  command -v jar >/dev/null 2>&1 || fail "jar is required to package compiled source-shim classes before dx"

  [ -d "$SHIM_CLASS_DIR" ] || fail "compiled source-shim class directory missing: $SHIM_CLASS_DIR"

  PROGRAM_JAR="$SHIM_BUILD_ROOT/source-shim-program.jar"
  rm -f "$PROGRAM_JAR"
  (
    cd "$SHIM_CLASS_DIR"
    jar cf "$PROGRAM_JAR" .
  )

  mkdir -p "$DEX_DIR"
  dx --dex --output="$DEX_DIR/classes.dex" "$PROGRAM_JAR"
  dex_path="$DEX_DIR/classes.dex"
else
  log "d8/dx not found; skipping dex generation."
fi

if [[ -n "$dex_path" ]]; then
  if command -v baksmali >/dev/null 2>&1; then
    baksmali disassemble "$dex_path" -o "$SMALI_DIR" >/dev/null
    echo "[shim-build] smali generated with baksmali: $SMALI_DIR"
  elif command -v java >/dev/null 2>&1; then
    baksmali_jar="$(find "${SEARCH_ROOTS[@]}" -maxdepth 8 -type f -iname '*baksmali*.jar' 2>/dev/null | head -n 1 || true)"
    if [[ -n "$baksmali_jar" ]]; then
      java -jar "$baksmali_jar" disassemble "$dex_path" -o "$SMALI_DIR" >/dev/null
      echo "[shim-build] smali generated with baksmali jar: $SMALI_DIR"
    else
      echo "[shim-build] baksmali not found; skipping smali generation." >&2
    fi
  else
    echo "[shim-build] java not found; cannot run baksmali jar fallback." >&2
  fi
fi

echo "[shim-build] outputs remain local-only under: $OUT_DIR"
echo "[shim-build] do not commit .class/.dex/.jar/.aar/generated smali outputs."
echo "[shim-build] next manual import steps:"
echo "  1) review generated com/tw/music/media/*.smali under $SMALI_DIR"
echo "  2) copy reviewed files into app/apktool/smali_classes*/com/tw/music/media/"
echo "  3) run static gate checks before any MusicService wiring"

if [ -f "$DEX_DIR/classes.dex" ]; then
  rm -rf "$SMALI_DIR"
  mkdir -p "$SMALI_DIR"

  if command -v baksmali >/dev/null 2>&1; then
    baksmali disassemble "$DEX_DIR/classes.dex" -o "$SMALI_DIR"
  elif [ -n "${BAKSMALI_JAR:-}" ] && [ -f "$BAKSMALI_JAR" ]; then
    java -jar "$BAKSMALI_JAR" disassemble "$DEX_DIR/classes.dex" -o "$SMALI_DIR"
  else
    log "baksmali not found; skipping smali generation."
  fi
fi
