#!/usr/bin/env sh
set -eu

INCLUDE_EVIDENCE="${DOCS_INDEX_INCLUDE_EVIDENCE:-0}"
INCLUDE_ARCHIVE="${DOCS_INDEX_INCLUDE_ARCHIVE:-0}"
STRICT="${DOCS_INDEX_STRICT:-0}"

missing=0
suspicious=0

printf '%s\n' '== Canonical live doc presence =='
for f in \
  docs/README.md \
  docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md \
  docs/TW_ECOSYSTEM_SOURCE_MAP.md \
  docs/TS18_REQUIREMENTS.md \
  docs/TS18_INTEGRATION_ARCHITECTURE.md \
  docs/TS18_VALIDATION_RUNBOOK.md \
  docs/TS18_NATIVE_CONTRACTS.md \
  docs/DEVELOPMENT_ROADMAP.md \
  docs/CODEX_TASK_PROMPTS.md \
  docs/RESEARCH_SOURCES.md
 do
  if [ -f "$f" ]; then printf 'OK  %s\n' "$f"; else printf 'MISSING  %s\n' "$f"; missing=1; fi
 done

printf '\n%s\n' '== Evidence policy markers =='
grep -nE "evidence only|docs/evidence|active source-of-truth|Routine guardrail scans" docs/README.md || true

printf '\n%s\n' '== Obsolete-path checks =='
for p in docs/TS18_EXECUTION_PACK_PHASE1_4.md docs/TS18_PUBLIC_REFERENCE_RESEARCH.md; do
  if [ -f "$p" ]; then
    echo "WARN obsolete file still exists: $p"
    suspicious=1
  else
    echo "OK removed: $p"
  fi
done
if [ -d docs/pr-drafts ]; then
  echo "WARN obsolete directory still exists: docs/pr-drafts"
  suspicious=1
else
  echo "OK removed: docs/pr-drafts"
fi

printf '\n%s\n' '== Docs scope for stale-term scan =='
DOC_FILES="$(find docs -type f ! -path 'docs/evidence/*' ! -path 'docs/archive/*' | sort)"
if [ "$INCLUDE_EVIDENCE" = "1" ]; then
  DOC_FILES="$(find docs -type f ! -path 'docs/archive/*' | sort)"
fi
if [ "$INCLUDE_ARCHIVE" = "1" ] && [ -d docs/archive ]; then
  if [ "$INCLUDE_EVIDENCE" = "1" ]; then
    DOC_FILES="$(find docs -type f | sort)"
  else
    DOC_FILES="$(find docs -type f ! -path 'docs/evidence/*' | sort)"
  fi
fi
printf '%s\n' "$DOC_FILES"

printf '\n%s\n' '== Stale active-strategy terms =='
PATTERN="probe-first|diagnostics-first|default-off[- ]adapter|default-off[- ]probe|runtime contract registry|TS18IntegrationProbe|TS18EnvironmentSnapshot|TS18ContractRegistry|TS18AdapterCapability|TWUtil scanner|TWClient scanner|vendor package scanner"
MATCHES=""
filtered_doc_stream() {
  # Exclude historical/negative-guidance sections so strict mode only flags active usage.
  # shellcheck disable=SC2086
  for f in $DOC_FILES; do
    awk '
      /^#+[[:space:]]/ {
        skip = ($0 ~ /^#+[[:space:]](Explicit non-targets|Superseded|Rejected|Historical|Archived|Archive|Obsolete|Deprecated)([[:space:]]|$)/)
      }
      !skip {
        printf "%s:%d:%s\n", FILENAME, FNR, $0
      }
    ' "$f"
  done
}
if [ -n "$DOC_FILES" ]; then
  MATCHES=$(filtered_doc_stream | grep -E "$PATTERN" || true)
fi
printf '%s\n' "$MATCHES"

if [ "$STRICT" = "1" ]; then
  if [ -n "$MATCHES" ] || [ "$suspicious" -ne 0 ]; then
    echo "STRICT: stale strategy terms or obsolete docs detected" >&2
    exit 2
  fi
fi

if [ "$missing" -ne 0 ]; then
  exit 1
fi
