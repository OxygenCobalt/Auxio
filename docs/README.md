# Auxio-TS Documentation Index

Auxio-TS is a TS18/Topway/DoFun Variety-targeted Auxio variant for the observed DoFun/Topway stock-music contract. Start here for current product, CI, release, and compatibility guidance.

[Evidence confidence: Requires TS18 validation] [Porting decision: Requires TS18 runtime validation]

## Current docs

- [`DEVELOPMENT.md`](DEVELOPMENT.md) — local setup, CI workflow coverage, Roborazzi UI workflow, and deleted-workflow audit notes.
- [`RELEASE_WORKFLOW.md`](RELEASE_WORKFLOW.md) — manual signed release flow and expected APK assets.
- [`DOFUN_VARIETY_COMPATIBILITY.md`](DOFUN_VARIETY_COMPATIBILITY.md) — DoFun/Topway compatibility contract and private-hook boundaries.
- [`TS18_APK_REFERENCE.md`](TS18_APK_REFERENCE.md) — compact APK-derived reference evidence for DoFun Variety and stock `twmusic`.
- [`TS18_RUNTIME_VALIDATION.md`](TS18_RUNTIME_VALIDATION.md) — on-device TS18 validation checklist and evidence expectations.
- [`topway/README.md`](topway/README.md) — local Topway decompile/source-led compatibility notes.

## DoFun Variety / TS18 APK reference baseline

Use the APK reference docs before changing package identity, Topway broadcasts, media/session wiring, release workflows, or guardrail scripts.

[Evidence confidence: Observed APK/reference evidence] [Porting decision: Directly reusable as compatibility requirements and guardrails]

Primary compatibility target:

- DoFun Variety Theme: `com.dofun.variety`

[Evidence confidence: Observed in DoFun APK metadata/config] [Porting decision: Primary launcher/theme target]

Primary replacement contract:

- stock `twmusic` / `com.tw.music`
- release package/application ID: `com.tw.music`
- launcher/activity component: `com.tw.music.MusicActivity`
- release variant: `topwayTwMusicRelease`

[Evidence confidence: Observed in DoFun APK config and stock twmusic APK references] [Porting decision: Directly reusable replacement contract]

Observed Cardoor/private services and vendor hooks are evidence only, not production implementation. They are not for production by default and require the formal gap-and-promotion process before any native/private investigation can become product code.

[Evidence confidence: Observed APK/string evidence] [Porting decision: Evidence only; do not implement without proven protocol]

## CI entry points

- `.github/workflows/android.yml` builds standard and Topway/DoFun APKs and runs DoFun compatibility checks on relevant PR/push changes.
- `.github/workflows/lint.yml` runs workflow/shell syntax checks, formatting, unit tests, Android lint, and head-unit safety/DoFun guardrails.
- `.github/workflows/manual-release.yml` builds, signs, verifies, and uploads standard plus Topway/DoFun release APKs.
- `.github/workflows/ui-screenshots.yml` provides manually triggered Roborazzi screenshots/reports for UI review.

Local preflight:

```sh
bash scripts/prepare-ci-environment.sh
bash scripts/check-ts18-apk-reference-contracts.sh
bash scripts/check-dofun-topway-compat.sh
bash scripts/check-headunit-compat-safety.sh
find scripts -type f -name '*.sh' -print -exec bash -n {} \;
ruby -e 'require "yaml"; ARGV.each { |f| Psych.safe_load(File.read(f), permitted_classes: [], permitted_symbols: [], aliases: false); puts "OK #{f}" }' .github/workflows/*.yml
```
