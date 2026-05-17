# `t-music` evidence snapshot for Auxio-TS

This directory is a curated, text-only evidence snapshot from `cbkii/t-music`.

Purpose:

- help Auxio-TS agents understand prior `com.tw.music` / TS18 / TWTHEME investigation;
- preserve useful findings from the decompiled stock music-app analysis;
- support development of Auxio-TS as a maintainable TS18/twmusic-equivalent player;
- avoid requiring Copilot/Codex access to the private `cbkii/t-music` repository.

This snapshot is evidence, not implementation source.

Do not blindly port code from here into Auxio-TS. Classify each finding as:

- directly reusable requirement;
- evidence only;
- obsolete due to Auxio architecture;
- needs TS18 runtime validation;
- should be avoided.

Excluded intentionally:

- APKs;
- DEX files;
- generated build outputs;
- large binaries/images/resources;
- private/secrets material.

Important areas:

- `docs/`
- `mappings/`
- `scripts/`
- `tools/`
- `app/apktool/AndroidManifest.xml`
- focused smali under `app/apktool/smali*/com/tw/music/`
- focused smali under `app/apktool/smali*/com/eckom/xtlibrary/`
