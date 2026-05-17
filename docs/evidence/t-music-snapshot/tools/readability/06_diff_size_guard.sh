#!/usr/bin/env bash
set -euo pipefail

MAX_CHANGED_FILES="${MAX_CHANGED_FILES:-40}"
MAX_ADDED_DELETED="${MAX_ADDED_DELETED:-4000}"
MAX_REPORT_CHURN="${MAX_REPORT_CHURN:-2500}"

if ! git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  echo "STOP: tools/readability/06_diff_size_guard.sh must run inside a git checkout."
  exit 1
fi

unstaged="$(git diff --name-only)"
staged="$(git diff --cached --name-only)"
untracked="$(git ls-files --others --exclude-standard)"
all_files="$(printf "%s\n%s\n%s\n" "$unstaged" "$staged" "$untracked" | sed '/^$/d' | sort -u)"

echo "[diff-size] git diff --stat"; git diff --stat || true
echo "[diff-size] git diff --cached --stat"; git diff --cached --stat || true

changed_files="$(printf "%s\n" "$all_files" | sed '/^$/d' | wc -l | tr -d ' ')"
added_deleted="$( (git diff --numstat; git diff --cached --numstat) | awk '{if ($1 ~ /^[0-9]+$/) a+=$1; if ($2 ~ /^[0-9]+$/) d+=$2} END {print a+d+0}')"
report_churn="$( (git diff --numstat docs/reports || true; git diff --cached --numstat docs/reports || true) | awk '{if ($1 ~ /^[0-9]+$/) a+=$1; if ($2 ~ /^[0-9]+$/) d+=$2} END {print a+d+0}')"

if [ "$changed_files" -gt "$MAX_CHANGED_FILES" ] || [ "$added_deleted" -gt "$MAX_ADDED_DELETED" ]; then
  echo "STOP: diff too large (files=$changed_files lines=$added_deleted)."
  exit 1
fi
if [ "$report_churn" -gt "$MAX_REPORT_CHURN" ]; then
  echo "STOP: oversized generated report churn in docs/reports ($report_churn lines)."
  exit 1
fi

if printf "%s\n" "$all_files" | grep -qE '\.(apk|apks|aab|dex|jar|zip|tar|gz|7z|rar|xz|bin|so|o|class|pdf)$'; then
  echo "STOP: forbidden binary/archive/decompiler artifact in change set:" 
  printf "%s\n" "$all_files" | grep -E '\.(apk|apks|aab|dex|jar|zip|tar|gz|7z|rar|xz|bin|so|o|class|pdf)$'
  exit 1
fi

echo "[diff-size] OK"
