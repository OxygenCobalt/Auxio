# TS18 Validation Execution Plan (Phase 5G/6A)

## User flow
1. Build/install APK.
2. Confirm ADB connection.
3. Capture baseline pack.
4. Run scenario action/manual steps.
5. Capture after pack.
6. Validate pack.
7. Summarise pack.
8. Classify gap candidates.
9. Generate matrix proposal.
10. Zip/share outputs.

## Commands
All outputs are file-based under `validation-output/`.

```bash
scripts/ts18-validation-workflow.sh capture --out "validation-output" --scenario TS18-STD-001 --label run-a --before
scripts/ts18-validation-workflow.sh capture --out "validation-output" --scenario TS18-STD-001 --label run-a --after --zip
scripts/ts18-validation-workflow.sh validate --pack "validation-output/<pack>"
scripts/ts18-validation-workflow.sh summarise --pack "validation-output/<pack>"
scripts/ts18-validation-workflow.sh classify --pack "validation-output/<pack>"
scripts/ts18-validation-workflow.sh propose-matrix --pack "validation-output/<pack>"
```

Tier 3/native-private is not for production by default; requires formal gap-and-promotion process.
