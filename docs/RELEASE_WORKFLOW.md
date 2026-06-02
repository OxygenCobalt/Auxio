# Release workflow

Build release APKs with the shared CI preparation step first:

```sh
bash ./scripts/prepare-ci-environment.sh
./gradlew :app:assembleStandardRelease
./gradlew :app:assembleTopwayTwMusicRelease
./gradlew :app:assembleTopwayTwMediaRelease
```

The manual release workflow (`.github/workflows/manual-release.yml`) uses repository secrets:

- `KEYSTORE_BASE64`
- `KEYSTORE_PASSWORD`
- `KEY_ALIAS`
- `KEY_PASSWORD`

It builds, signs, verifies, renames, and uploads three APK assets:

| Asset | Package | Audience | Installation constraint |
| --- | --- | --- | --- |
| `Auxio-TS-vX.Y.Z-standard-release.apk` | `org.oxycblt.auxio` | Normal Auxio-TS users and baseline validation | Does not replace stock TS18 music packages |
| `Auxio-TS-vX.Y.Z-topway-twmusic-release.apk` | `com.tw.music` | Exact Topway/DoFun stock replacement target | Conflicts with stock `com.tw.music` system priv-app unless package state/signing is managed |
| `Auxio-TS-vX.Y.Z-topway-twmedia-release.apk` | `com.tw.media` | DoFun alternate fixed entry, `com.tw.media/com.tw.music.MusicActivity` | Not a guaranteed no-root bypass; `com.tw.media` may conflict on some firmware |

Do not publish or document either Topway-compatible APK as universally installable on locked stock firmware. See `docs/TS18_INSTALLATION_CONSTRAINTS.md` for package-state recovery and install-lane details.

Before tagging a release, run the compatibility guardrails:

```sh
bash scripts/check-ts18-apk-reference-contracts.sh
bash scripts/check-dofun-topway-compat.sh
bash scripts/check-headunit-compat-safety.sh
```
