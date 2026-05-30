# Topway / DoFun / Variety compatibility reference

This directory records the extracted evidence and implementation guidance for making Auxio-TS recognisable by `com.dofun.variety` launcher/theme music widgets and panels.

Start with:

- `../DOFUN_VARIETY_COMPATIBILITY.md`

Key evidence:

- `reference/dofun-variety/apps_match_config.music-excerpts.json`
- `reference/dofun-variety/apps_config.music-excerpts.json`
- `reference/dofun-variety/AndroidManifest.filtered-relevant.xml`
- `reference/auxio/topway-bridge/TopwayMusicContract.kt`

Templates are deliberately kept under `docs/` so they do not silently become active app code when this bundle is unzipped. Agents should copy/adapt them into the real Android source tree only as part of an implementation PR.
