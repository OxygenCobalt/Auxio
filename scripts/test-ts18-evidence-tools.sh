#!/usr/bin/env bash
set -euo pipefail
python3 -m py_compile scripts/ts18-summarise-evidence-pack.py scripts/ts18-propose-gap-matrix-update.py scripts/ts18-generate-native-candidates.py
python3 -m json.tool docs/templates/TS18_EVIDENCE_MANIFEST.schema.json >/dev/null
python3 -m json.tool docs/templates/TS18_EVIDENCE_MANIFEST.example.json >/dev/null
python3 -m json.tool docs/templates/TS18_VALIDATION_SCENARIO_MAP.json >/dev/null

for PACK in docs/templates/fixtures/pack-minimal-pass docs/templates/fixtures/pack-partial-widget-gap docs/templates/fixtures/pack-missing-evidence; do
  python3 scripts/ts18-summarise-evidence-pack.py "$PACK"
  python3 scripts/ts18-propose-gap-matrix-update.py "$PACK"
  python3 scripts/ts18-generate-native-candidates.py "$PACK"
  [ -f "$PACK/derived/validation-summary.md" ]
  [ -f "$PACK/derived/scenario-results.json" ]
  [ -f "$PACK/derived/parity-gap-matrix-update.md" ]
done

# pass fixture: no candidates expected
python3 - <<'PY'
import json
from pathlib import Path
p=Path('docs/templates/fixtures/pack-minimal-pass/derived/native-candidate-index.json')
d=json.loads(p.read_text())
assert len(d['generated'])==0, d
PY
# partial widget gap: expect candidate
python3 - <<'PY'
import json
from pathlib import Path
p=Path('docs/templates/fixtures/pack-partial-widget-gap/derived/native-candidate-index.json')
d=json.loads(p.read_text())
assert len(d['generated'])>=1, d
PY
# missing evidence alone: no candidates
python3 - <<'PY'
import json
from pathlib import Path
p=Path('docs/templates/fixtures/pack-missing-evidence/derived/native-candidate-index.json')
d=json.loads(p.read_text())
assert len(d['generated'])==0, d
PY

bash scripts/redact_ts18_evidence_summary.sh docs/templates/fixtures/pack-minimal-pass /tmp/ts18-redacted-test
[ -f /tmp/ts18-redacted-test/raw/media_session.txt ]
bash scripts/ts18-evidence-workflow.sh --help >/dev/null

echo "TS18 evidence tooling fixture test passed"
