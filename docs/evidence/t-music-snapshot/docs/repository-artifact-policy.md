# Repository Artifact Policy

## Commit/track these surfaces

- `app/apktool/`
- `reference/jadx-raw/`
- `reference/jadx-aliased/`
- `reference/firstparty-jadx/`
- `reference/vendor-jadx/`
- `mappings/`
- `docs/`
- CI/workflow definitions under `.github/workflows/`

## Normally exclude from commits

- `dist/*.apk` outputs
- DEX/APK/AAB/JAR and other build artefacts
- Downloaded tool binaries
- Keystores or any signing material
- Raw temporary JADX exports outside tracked reference surfaces
- Local caches and machine-specific temp files

## Identity and compatibility safety

- Do not alter runtime package identity (`com.tw.music`) or `android.uid.system` ownership outside explicitly scoped tasks.
- Do not treat JADX alias package names as runtime package changes.
