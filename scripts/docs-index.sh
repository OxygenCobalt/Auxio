#!/usr/bin/env sh
set -eu

INCLUDE_EVIDENCE="${DOCS_INDEX_INCLUDE_EVIDENCE:-0}"
INCLUDE_ARCHIVE="${DOCS_INDEX_INCLUDE_ARCHIVE:-0}"
STRICT="${DOCS_INDEX_STRICT:-0}"

printf '%s\n' '== Canonical live doc presence =='
missing=0
for f in \
  docs/README.md \
  docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md \
  docs/TW_ECOSYSTEM_SOURCE_MAP.md \
  docs/TS18_REQUIREMENTS.md \
  docs/TS18_INTEGRATION_ARCHITECTURE.md \
  docs/TS18_VALIDATION_RUNBOOK.md \
  docs/DEVELOPMENT_ROADMAP.md \
  docs/CODEX_TASK_PROMPTS.md \
  docs/archive/README.md
 do
  if [ -f "$f" ]; then printf 'OK  %s\n' "$f"; else printf 'MISSING  %s\n' "$f"; missing=1; fi
 done

printf '\n%s\n' '== Policy markers =='
rg -n "live docs|evidence snapshots|docs/evidence|docs/archive" docs/README.md || true

printf '\n%s\n' '== Docs scope for stale-term scan =='
DOC_FILES="$(find docs -type f ! -path 'docs/evidence/*' ! -path 'docs/archive/*' | sort)"
if [ "$INCLUDE_EVIDENCE" = "1" ]; then
  DOC_FILES="$(find docs -type f ! -path 'docs/archive/*' | sort)"
fi
if [ "$INCLUDE_ARCHIVE" = "1" ]; then
  if [ "$INCLUDE_EVIDENCE" = "1" ]; then
    DOC_FILES="$(find docs -type f | sort)"
  else
    DOC_FILES="$(find docs -type f ! -path 'docs/evidence/*' | sort)"
  fi
fi
printf '%s\n' "$DOC_FILES"

printf '\n%s\n' '== Stale active-strategy terms =='
PATTERN="probe-first|diagnostics-first|default-off adapter|default-off probe|runtime contract registry|TS18IntegrationProbe|TS18EnvironmentSnapshot|TS18ContractRegistry|TS18AdapterCapability|TWUtil scanner|TWClient scanner|vendor package scanner"
if [ -n "$DOC_FILES" ]; then
  # shellcheck disable=SC2086
  MATCHES=$(rg -n "$PATTERN" $DOC_FILES || true)
else
  MATCHES=""
fi
printf '%s\n' "$MATCHES"

if [ "$STRICT" = "1" ] && [ -n "$MATCHES" ]; then
  echo "STRICT: suspicious terms found" >&2
  exit 2
fi

if [ "$missing" -ne 0 ]; then
  exit 1
fi
