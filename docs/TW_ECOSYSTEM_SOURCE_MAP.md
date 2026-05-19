# TW Ecosystem Source Map (source-led)

## Source classes
- **official Android docs**: implementation authority.
- **public equivalent app/source repo**: architecture/compatibility precedent.
- **firmware/tooling/community source**: ecosystem context and test-matrix input.
- **local TS18 diagnostics**: target device evidence.
- **t-music snapshot**: stock compatibility clues.
- **hypothesis**: unproven assumptions pending runtime validation.

## Mapped sources

| Source | URL/path | Source class | Specific evidence extracted | Limits | Implementation implication |
|---|---|---|---|---|---|
| Media3 background/session content + cars + audio focus docs | developer.android.com / source.android.com | official Android docs | Canonical behavior for session, library, controls, car surfaces, focus | No private TW contract guarantees | Prioritize standards-based features in product code |
| OpenRadioFM | https://github.com/kapi21/OpenRadioFM | public equivalent app/source repo | Multi-platform separation pattern and OEM intent context | Does not prove TS18 requirements for Auxio | Use as architecture precedent only |
| FytHWOneKey | https://github.com/hvdwolf/FytHWOneKey | public equivalent app/source repo | Hardware-button routing via package/intent/keyevent/default-app flow | Not TS18-specific media-app contract | Strengthen standard media key compatibility/validation |
| Display-Media-Titles | https://github.com/vasyl91/Display-Media-Titles | public equivalent app/source repo | FYT stock metadata routing can be package-targeted | No generic TS18 API rule | Track package-coupling risk; avoid impersonation |
| RK3066-headunit-service | https://github.com/petrows/RK3066-headunit-service | public equivalent app/source repo | Legacy HU service/key remap precedent | Legacy platform/version | Historical context only |
| FET + FYTuis7862BinRepo + topwaytool | GitHub repos | firmware/tooling/community source | Firmware/tooling ecosystem and variance are real | Not direct app API contract proof | Use for validation planning, not app code |
| DoFun channel + Mekede TS10-TS18 + XDA TS18 thread | Telegram/web/XDA | firmware/tooling/community source | Variant naming/family diversity and deployment reality | Anecdotal/marketing variability | Include firmware/theme variant fields in test evidence |
| `diagnostics/redacted/ts18_device_profile.json` | local path | local TS18 diagnostics | Target package/theme ecosystem markers | No causality of required integrations | Prioritize acceptance scenarios |
| `docs/evidence/t-music-snapshot/` | local path | t-music snapshot | Stock-private contracts and behavior clues | Not an implementation template for Auxio | Use as validation/reference-only evidence |

## Current hypotheses (not implementation requirements)
- Some TS18 launchers may privilege package-targeted metadata pathways.
- Some vendor stacks may alter focus/ownership behavior under projection.
- TWTHEME variance may change visual expectations without changing media API requirements.
