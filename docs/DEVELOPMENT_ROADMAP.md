# Auxio-TS Development Roadmap

One primary variable per PR.

| Phase | Goal | Likely files touched | Validation | Stop conditions | Human TS18 checks | Expected artifacts |
|---|---|---|---|---|---|---|
| 0 | Documentation/source-corpus hardening | `docs/*.md`, `AGENTS.md`, `.github/copilot-instructions.md` | docs review + script syntax | evidence taxonomy contradictions | No | updated source map/contracts/requirements |
| 1 | Build/install baseline | docs + minimal non-functional fixes | `./gradlew tasks assembleDebug test lint` + script syntax | environment/toolchain blockers | Optional install sanity | baseline build blocker report |
| 2 | Stock `com.tw.music` baseline capture | runbook/docs/scripts | stock capture completeness | missing reproducibility | Mandatory | stock evidence bundle + summary |
| 3 | Auxio-TS Android-native validation on TS18 | runbook/docs (+ minimal observability if needed) | session/notification/focus/key checks | Android-native path unstable | Mandatory | Auxio baseline evidence bundle |
| 4 | Stock vs Auxio comparison | comparison docs/scripts | A/B parity matrix | ambiguous/non-repeatable deltas | Mandatory | ranked gap matrix |
| 5 | Launcher/widget/TWTHEME evidence capture | source-map/contracts/runbook updates | launcher/widget/theme scenarios | no reproducible launcher scenarios | Mandatory | launcher/TWTHEME report |
| 6 | TS18 adapter skeleton | isolated adapter package + docs/tests | no behavior change when disabled | leakage into core playback/library | Recommended | default-off facade/registry PR |
| 7 | Targeted TW contract experiments | one adapter module + diagnostics | pre/post evidence for one contract | privileged requirement or regressions | Mandatory | per-contract experiment report + rollback plan |
| 8 | FLAC/audio-quality/sleep-resume validation | validation docs/tests + minimal guarded fixes | FLAC matrix + navigation-mixing + sleep/resume checks | mainstream regression risk | Mandatory | pass/fail validation matrix |
| 9 | Release-candidate hardening | release docs/checklists | full command rerun + latest TS18 evidence replay | unresolved high-risk hypotheses | Final sign-off | release-readiness dossier |

## Snapshot-driven sequencing notes
- `t-music` snapshot findings are now integrated as evidence for phases 2/4/5/7.
- Do not start phase 7 until phases 3 and 4 produce clear, reproducible gaps.
