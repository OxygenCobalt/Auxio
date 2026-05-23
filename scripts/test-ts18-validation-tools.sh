#!/usr/bin/env bash
set -euo pipefail
python3 scripts/ts18-validate-scenario-map.py docs/templates/TS18_VALIDATION_SCENARIO_MAP.json
python3 -m py_compile scripts/ts18-*.py
packs=(
"docs/templates/fixtures/fixture-pass-core-media"
"docs/templates/fixtures/fixture-partial-widget-gap"
"docs/templates/fixtures/fixture-missing-required-files"
"docs/templates/fixtures/fixture-blocked-no-auxio-session"
"docs/templates/fixtures/fixture-fail-media-session-missing"
"docs/templates/fixtures/fixture-partial-shortcuts-present-session-missing"
"docs/templates/fixtures/fixture-real-pack-shape-redacted"
)
for p in "${packs[@]}"; do
  python3 scripts/ts18-validate-evidence-pack.py "$p"
  python3 scripts/ts18-summarise-validation-pack.py "$p"
  python3 scripts/ts18-classify-parity-gaps.py "$p" docs/templates/TS18_VALIDATION_SCENARIO_MAP.json
  python3 scripts/ts18-propose-matrix-update.py "$p"
done
sp="docs/templates/fixtures/fixture pass space"
rm -rf "$sp" && cp -r docs/templates/fixtures/fixture-pass-core-media "$sp"
python3 scripts/ts18-validate-evidence-pack.py "$sp"
bash scripts/ts18-validation-workflow.sh --help >/dev/null
bash scripts/ts18-validation-workflow.sh validate --pack docs/templates/fixtures/fixture-pass-core-media
bash scripts/ts18-validation-workflow.sh summarise --pack docs/templates/fixtures/fixture-pass-core-media
if [ -d docs/templates/fixtures/pack-minimal-pass ]; then
  bash scripts/ts18-validation-workflow.sh summarise --pack docs/templates/fixtures/pack-minimal-pass >/dev/null
  bash scripts/ts18-validation-workflow.sh propose-matrix --pack docs/templates/fixtures/pack-minimal-pass >/dev/null
fi
bash scripts/ts18-capture-validation-pack.sh --out "validation-output test" --label "space" --scenario TS18-STD-001 --single --dry-run --zip >/dev/null
find "validation-output test" -maxdepth 1 -name '*.zip' -type f | grep -q .
echo "ts18 validation tool tests passed"
