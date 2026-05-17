# AGENTS.md — Auxio-TS coding authority

## 1) Project purpose
Auxio-TS is a TS18-focused, maintainable fork of Auxio.

- Keep upstream Auxio architecture and Android media correctness first.
- Treat TS18/TW/TWTHEME work as adapter-layer compatibility work.
- Treat `docs/evidence/t-music-snapshot/` as evidence and prior research, not implementation source.

## 2) Evidence + porting classification discipline
Use **both** labels on TS18 claims:

### Evidence confidence label
- **Observed**
- **Inferred**
- **Hypothesis**
- **Requires TS18 validation**
- **Unsupported**

### Auxio-TS reuse decision label
- **Directly reusable requirement**
- **Reusable validation idea**
- **Useful as evidence only**
- **Obsolete due to Auxio architecture**
- **Requires TS18 runtime validation**
- **Unsafe to port**
- **Should be explicitly avoided**

Do not claim compatibility without reproducible TS18 runtime evidence.

## 3) Source hierarchy
1. Android official docs + upstream Auxio source (implementation authority)
2. `diagnostics/redacted/*` (device evidence authority)
3. `docs/evidence/t-music-snapshot/*` (private corpus snapshot; requirement/validation evidence)
4. Public TW/Topway repos (`TWUtil`/`TWClient` etc.) (research inputs only)

## 4) Mandatory architectural separation
```text
Auxio upstream core
        |
Android media integration layer
        |
Auxio-TS adapter/facade layer
        |
optional TS18-specific modules:
  - launcher/media-widget adapter
  - TW broadcast adapter
  - TW service adapter
  - TWTHEME/resource compatibility notes
  - ZLink/TLink validation hooks
  - stock com.tw.music comparison harness
```

## 5) Hard constraints
- Do not change package identity to `com.tw.music`.
- Do not require `android.uid.system`, privileged UID, platform signing, or private keys.
- Do not copy decompiled smali into Auxio app implementation.
- Do not scatter TS18 conditionals through core playback/library code.
- Do not present TW private contracts as stable unless proven on TS18 hardware.

## 6) Specific findings from `t-music` snapshot to treat as high-risk
- `AndroidManifest.xml` shows `package="com.tw.music"` + `android:sharedUserId="android.uid.system"`.
- Snapshot includes explicit TW actions (`com.tw.music.action.cmd|prev|next|pp`).
- Snapshot includes TW service/AIDL/token surfaces (`com.tw.service.xt.aidl.*`) and TW theme/resource coupling.

These are evidence inputs; they are not direct implementation requirements for Auxio-TS.

## 7) Validation requirements
At minimum run (or document exact blocker):
- `./gradlew tasks`
- `./gradlew assembleDebug`
- `./gradlew test`
- `./gradlew lint`
- `find scripts -type f -name '*.sh' -print -exec sh -n {} \;`

For evidence passes also run:
- `find docs/evidence/t-music-snapshot -type f \( -iname '*.apk' -o -iname '*.dex' -o -iname '*.zip' -o -iname '*.jar' -o -iname '*.png' -o -iname '*.jpg' -o -iname '*.webp' \) -print`

## 8) PR reporting requirements
Every TS18-facing PR must include:
- claims + evidence/porting labels,
- exact commands run and results,
- unresolved risks/blockers,
- TS18 runtime checks still required,
- next recommended one-variable PR.
