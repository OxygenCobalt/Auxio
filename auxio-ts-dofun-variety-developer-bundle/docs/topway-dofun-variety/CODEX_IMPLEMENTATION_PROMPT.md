# Codex implementation prompt: DoFun/Variety Topway compatibility

```markdown
You need strong Android/Kotlin/Gradle, Android media/session, launcher compatibility, manifest/source-set, CI/release, and reverse-engineering-oriented integration skills.

Implement comprehensive `com.dofun.variety` / Topway launcher-theme compatibility for Auxio-TS without modifying DoFun Theme.

Use `docs/DOFUN_VARIETY_COMPATIBILITY.md` and `docs/topway-dofun-variety/reference/` as the source evidence. Do not rely on memory or guesses when the extracted files answer the question.

Primary target:
- Add a dedicated Android product flavour/source set named `topwayTwMusic` or an equivalent clear name.
- Keep the standard Auxio-TS APK identity unchanged.
- Build the Topway-compatible variant as `applicationId = com.tw.music`.
- Expose `com.tw.music.MusicActivity` as an `activity-alias` targeting `org.oxycblt.auxio.MainActivity`.
- Use label `Music` for the Topway-compatible variant.
- Preserve Android media/session/browser service support and existing Topway broadcast bridge support.
- Keep CoverProvider authorities coherent for standard, debug, Topway, and Topway debug variants.
- Add CI/release support so the Topway APK is built and published as a separate artefact/asset.
- Add or wire a static check using `scripts/check-dofun-topway-compat.sh`, adapting the script if needed.

Do not implement a fake `cn.cardoor.libs.media.RemoteMediaService` unless you recover and document a concrete binder/AIDL contract. The current extracted evidence proves the action exists but not the client/server protocol.

Before finishing:
1. Run formatting/lint/tests/builds that are practical in this repo.
2. Build the normal APK and the Topway-compatible APK.
3. Inspect merged manifests or APK badging to confirm package/activity/provider/media-service identities.
4. Summarise files changed, commands run, results, and remaining head-unit validation steps.
```
