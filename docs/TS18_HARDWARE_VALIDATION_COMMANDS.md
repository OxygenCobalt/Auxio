# TS18 Hardware Validation Commands (Laptop/Termux compatible)

```bash
mkdir -p validation-output
adb devices > validation-output/adb-devices.txt
adb install -r app/build/outputs/apk/debug/app-debug.apk > validation-output/install.txt 2>&1
```

```bash
scripts/ts18-capture-validation-pack.sh --out "validation-output" --label hu1 --scenario TS18-STD-008 --before
scripts/ts18-capture-validation-pack.sh --out "validation-output" --label hu1 --scenario TS18-STD-008 --action launch-now-playing
scripts/ts18-capture-validation-pack.sh --out "validation-output" --label hu1 --scenario TS18-STD-008 --after --zip
```

```bash
scripts/ts18-validation-workflow.sh validate --pack "validation-output/<pack>" > "validation-output/<pack>/derived/validate.log" 2>&1
scripts/ts18-validation-workflow.sh summarise --pack "validation-output/<pack>" > "validation-output/<pack>/derived/summarise.log" 2>&1
scripts/ts18-validation-workflow.sh classify --pack "validation-output/<pack>" > "validation-output/<pack>/derived/classify.log" 2>&1
scripts/ts18-validation-workflow.sh propose-matrix --pack "validation-output/<pack>" > "validation-output/<pack>/derived/propose.log" 2>&1
```

No root required.
