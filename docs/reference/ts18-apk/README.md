# TS18 APK reference materials

This directory contains a compact, repo-safe reference set extracted from the user-provided APKs:

| APK | Purpose | SHA-256 |
| --- | --- | --- |
| `com.dofun.variety_V9.7.2.367.260312.apk` | DoFun Variety Theme launcher/widget target | `75e7ea9b46d68754253aa385e6ac750aae957a5b72196fec5449ccf2782c60b1` |
| `com.tw.music_TW_THEME.20240715.apk` | Stock Topway `twmusic` compatibility reference | `4f5495e270a7c86bab232e2b7ee2ecd2d71f3450f6f20ed5f36feaa4229c1518` |

The original APK binaries are **not** committed here. These files are small extracted/reference artefacts only.

## How to use these files

Use this directory as evidence for Auxio-TS development and agent tasks. It defines the compatibility target that CI/docs/scripts should protect.

Primary files:

- `reference-contracts.json` — machine-readable summary of package identities, safe public broadcast surfaces, and forbidden private/vendor surfaces.
- `dofun-variety/apps_match_config.music-excerpts.json` — DoFun hotseat/widget music matching evidence.
- `dofun-variety/apps_config.music-excerpts.json` — DoFun icon/app matching evidence for music/video entries.
- `dofun-variety/manifest.string-hits.txt` — manifest string-pool hits for DoFun package/private media services.
- `twmusic/manifest.string-hits.txt` — stock Topway music manifest string-pool hits.
- `twmusic/classes.string-hits.txt` — stock Topway music class/string hits relevant to media/session/broadcast/vendor hooks.

## Evidence labels

- DoFun music matching to `com.tw.music` / `com.tw.music.MusicActivity`: **Observed**; **directly reusable requirement**.
- Topway broadcast/action/extras: **Observed**; **directly reusable requirement** when kept inside the isolated Topway bridge.
- Cardoor private media services: **Observed**; **useful as evidence only** until a concrete binder/AIDL protocol is recovered.
- `android.uid.system`, `android.tw.john.TWUtil`, and `com.tw.service.xt.aidl.*`: **Observed**; **should be explicitly avoided** in Auxio-TS product code.

## Rules

- Do not copy decompiled source/smali into Auxio-TS.
- Do not commit full APKs or bulk decompile trees.
- Keep reference material concise; update this directory only when a new APK changes the compatibility contract.
- Keep product code compatibility in Android-standard surfaces and the isolated Topway bridge.
