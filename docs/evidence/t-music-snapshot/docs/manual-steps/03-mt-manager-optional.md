# Optional manual step: MT Manager normalisation

MT Manager can anti-obfuscate dex/resource names before JADX decompiles them,
producing more readable Java references.

## Recommended workflow
1. Open the original APK in MT Manager
2. Use "Dex anti-obfuscation" selectively on first-party code only
3. Use "Resource anti-obfuscation" only if res/ IDs are still unreadable
4. Export the modified APK somewhere accessible (e.g. `/sdcard/mt-normalised.apk`)
5. Import it:
```bash
bash scripts/06_import_mt_normalised.sh /sdcard/mt-normalised.apk
```

**Do not replace `app/apktool/` with the MT output.** Use it as a readable
reference only — MT changes smali in ways that may not rebuild cleanly.
