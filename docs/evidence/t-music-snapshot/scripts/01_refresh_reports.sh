#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
mkdir -p docs/maps docs/reports docs/deobf

# File inventories
find app/apktool -path 'app/apktool/build' -prune -o -type f -print | sort > docs/maps/all-apktool-files.txt
find app/apktool -path 'app/apktool/build' -prune -o -type f -path '*/smali*/*.smali' -print | sort > docs/maps/all-smali-files.txt
find reference/jadx-raw -type f | sort > docs/maps/all-jadx-files.txt 2>/dev/null || true
find reference/jadx-aliased -type f | sort > docs/maps/all-jadx-aliased-files.txt 2>/dev/null || true

# App-specific subsets
find app/apktool -path 'app/apktool/build' -prune -o -type f -path '*/com/tw/music/*' -print | sort \
  > docs/maps/app-smali-files.txt 2>/dev/null || true
find app/apktool -path 'app/apktool/build' -prune -o -type f -path '*/com/eckom/*' -print | sort \
  > docs/maps/xtlibrary-smali-files.txt 2>/dev/null || true

# JADX-side app subsets (requires ripgrep; skip gracefully if absent)
if command -v rg >/dev/null 2>&1; then
  rg -l '^package\s+.*music;' reference/jadx-raw 2>/dev/null \
    | sort > docs/maps/app-jadx-files.txt || true
  rg -l '^package\s+com\.eckom\.' reference/jadx-raw 2>/dev/null \
    | sort > docs/maps/xtlibrary-jadx-files.txt || true
fi

# Manifest component extraction
if command -v python3 >/dev/null 2>&1; then
  python3 - <<'PY'
from pathlib import Path
import re
manifest = Path('app/apktool/AndroidManifest.xml')
if not manifest.exists():
    raise SystemExit(0)
m = manifest.read_text(encoding='utf-8', errors='ignore')
patterns = {
    'activities':   r'<activity[^>]*android:name="([^"]+)"',
    'services':     r'<service[^>]*android:name="([^"]+)"',
    'receivers':    r'<receiver[^>]*android:name="([^"]+)"',
    'providers':    r'<provider[^>]*android:name="([^"]+)"',
    'permissions':  r'<uses-permission[^>]*android:name="([^"]+)"',
}
for name, pat in patterns.items():
    vals = sorted(set(re.findall(pat, m)))
    Path(f'docs/maps/{name}.txt').write_text(
        '\n'.join(vals) + ('\n' if vals else ''), encoding='utf-8')
PY
fi

# JADX problem report
if command -v rg >/dev/null 2>&1; then
  rg -n 'Code decompiled incorrectly|JADX ERROR|Method dump skipped|Unsupported multi-entry loop' \
    reference/jadx-raw > docs/reports/jadx-problems.txt 2>/dev/null || true
  # Vendor/system hook cross-references
  rg -n 'com\.tw\.service\.xt|com\.tw\.music\.action|com\.tw\.radio|persist\.|EQActivity|VideoActivity' \
    reference/jadx-raw app/apktool > docs/reports/vendor-hooks.txt 2>/dev/null || true
else
  echo "ripgrep not available; skipping jadx-problems.txt and vendor-hooks.txt" \
    > docs/reports/jadx-problems.txt
fi

echo "Reports refreshed."
