#!/usr/bin/env bash
# Optional helper for Codex/Copilot-like environments with gh auth.
# Pulls recent failed GitHub Actions logs and extracts first-error summaries.

set -u
set -o pipefail

REPO="${1:-cbkii/Auxio-TS}"
LIMIT="${LIMIT:-15}"
OUT_DIR=".codex/actions"
mkdir -p "$OUT_DIR"

if ! command -v gh >/dev/null 2>&1; then
  echo "gh is not installed; cannot inspect GitHub Actions." >&2
  exit 0
fi

if ! gh auth status >/dev/null 2>&1; then
  echo "gh is not authenticated; cannot inspect GitHub Actions." >&2
  exit 0
fi

echo "# Failed Actions summary for $REPO"
echo
echo "Generated: $(date -u +"%Y-%m-%dT%H:%M:%SZ")"
echo

gh run list --repo "$REPO" --status failure --limit "$LIMIT" \
  --json databaseId,displayTitle,workflowName,headBranch,headSha,conclusion,createdAt,url \
  > "$OUT_DIR/failed-runs.json"

jq -r '.[] | [.databaseId,.workflowName,.headBranch,.headSha,.createdAt,.url] | @tsv' "$OUT_DIR/failed-runs.json" |
while IFS=$'\t' read -r run_id workflow branch sha created url; do
  log_file="$OUT_DIR/run-${run_id}.log"
  echo "## $workflow — $branch — $run_id"
  echo
  echo "- SHA: \`$sha\`"
  echo "- Created: $created"
  echo "- URL: $url"
  echo

  gh run view "$run_id" --repo "$REPO" --log-failed > "$log_file" 2>&1 || true

  echo '```text'
  grep -nEi 'FAILURE:|BUILD FAILED|FAILED|error:|exception|unresolved reference|cannot find symbol|lint found|Task .* failed|Process completed with exit code|Could not|No such file|not found|permission denied|deprecated|warning' "$log_file" | head -80 || true
  echo '```'
  echo
done
