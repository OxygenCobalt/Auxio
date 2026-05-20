# TS18 Execution Pack — Phase 1–4 (Historical)

> Status: **Superseded / historical reference**.
>
> This document is retained for provenance of Phase 1–4 execution planning. Active TS18/TW/TWTHEME work must follow:
> `TS18_SOURCE_LED_INTEGRATION_STRATEGY.md`, `TW_ECOSYSTEM_SOURCE_MAP.md`, `TS18_REQUIREMENTS.md`, and `TS18_VALIDATION_RUNBOOK.md`.
>
> Do not treat probe/default-off-adapter content here as active implementation guidance.

## 1) Purpose and scope

This document is a **historical execution-ready operator/agent guide for Phase 1–4 only**.

Phase scope:
- **Phase 1:** repo/build baseline.
- **Phase 2:** stock `com.tw.music` baseline evidence capture.
- **Phase 3:** Auxio-TS standard Android media validation.
- **Phase 4:** stock-vs-Auxio parity comparison and decision gate.

This execution pack is self-contained for new contributors running planning, validation, and evidence tasks.

**STOP:** Do not begin Phase 5+ (including TS18 adapter implementation) until all Phase 1–4 gates are passed or formally blocked with reproducible evidence and classification.

---

## 2) Historical precedence map

This section avoids taxonomy drift and clarifies where each decision type is authoritative.

- **Execution sequence authority:** `docs/DEVELOPMENT_ROADMAP.md` remains phase sequencing authority; this execution pack defines canonical Phase 1–4 operational gates and stop criteria.
- **Architecture authority:** `docs/TS18_INTEGRATION_ARCHITECTURE.md` remains detailed architecture authority; this execution pack constrains pre-implementation evidence obligations.
- **Validation procedure authority:** `docs/TS18_VALIDATION_RUNBOOK.md` remains expanded runtime operator procedure; this execution pack defines canonical scenario IDs, artifact paths, and gate outcomes.
- **Requirements authority:** `docs/TS18_REQUIREMENTS.md` remains requirement authority; this execution pack maps requirement proof obligations to Phase 1–4 checks.
- **Native contracts authority:** `docs/TS18_NATIVE_CONTRACTS.md` remains contract catalogue authority; this execution pack maps contract evidence to adapter backlog seeds.
- **Device facts authority:** `docs/TS18_DEVICE_PROFILE.md` and `docs/TS18_DIAGNOSTICS_INSIGHTS.md` remain fact/evidence authority for observed device properties and known unknowns.
- **Evidence label authority:** `AGENTS.md` and `.github/copilot-instructions.md` labels are canonical; this pack reuses them unchanged.
- **Future task prompts authority:** `docs/CODEX_TASK_PROMPTS.md` remains task prompt authority; this pack defines readiness gate expectations those tasks must satisfy.

---

## 3) Evidence labelling standard (copy-ready template)

For every TS18/TW/TWTHEME claim, include both required labels using existing repo taxonomy.

### Confidence label (required)
- **Observed**
- **Inferred**
- **Hypothesis**
- **Requires TS18 validation**
- **Unsupported**

### Porting decision label (required)
- **Directly reusable requirement**
- **Reusable validation idea**
- **Useful as evidence only**
- **Obsolete due to Auxio architecture**
- **Requires TS18 runtime validation**
- **Unsafe to port**
- **Should be explicitly avoided**

### Claim record template

```text
claim_id:
scenario:
phase:
timestamp_utc:
commit_sha:
package_under_test:
command_evidence:
observed_result:
confidence_label:
porting_decision_label:
unresolved_risk:
TS18_runtime_status:
follow_up_action:
evidence_artifact_path:
```

Notes:
- Do not claim compatibility from inference-only records.
- Any claim lacking command evidence + artifact path cannot drive implementation.

---

## 4) Standard evidence output locations

Canonical deterministic path convention for Phase 1–4:

```text
reports/ts18/<YYYY-MM-DD>/<scenario-id>/
```

Required scenario folder contents (where applicable):

```text
README.md
commands.txt
environment.txt
media_session.txt
audio.txt
package_dump.txt
logcat_excerpt.txt
manual_observations.md
summary.json
```

Redacted sharing path:

```text
reports/ts18/<YYYY-MM-DD>/<scenario-id>/redacted/
```

Conventions:
- `<scenario-id>` must match canonical IDs in Section 6.
- `commands.txt` must include exact commands and exit status.
- `summary.json` must include both required labels and unresolved risks.

---

## 5) Phase 1 baseline gate matrix

| Gate ID | Command | Purpose | Owner role | Required artifacts | Pass criteria | Fail criteria | Blocker classification template | Blocks Phase 2 |
|---|---|---|---|---|---|---|---|---|
| P1-G01 | `./gradlew tasks` | Validate Gradle/bootstrap task graph. | Codex/Copilot | `reports/ts18/<YYYY-MM-DD>/TS18-P1-BASELINE-002/commands.txt` | Exit code 0; task list rendered. | Non-zero exit; toolchain/bootstrap failure. | `BLOCKER[P1-G01]: env=<...>; symptom=<...>; root_cause=<toolchain/network/jdk/sdk/other>; reproducible=<yes/no>; next_action=<...>` | Yes |
| P1-G02 | `./gradlew assembleDebug` | Validate debug assembly path. | Codex/Copilot | `reports/ts18/<YYYY-MM-DD>/TS18-P1-BASELINE-003/` | APK assembles successfully. | Build failure. | same format | Yes |
| P1-G03 | `./gradlew test` | Validate baseline unit/integration tests. | Codex/Copilot | `reports/ts18/<YYYY-MM-DD>/TS18-P1-BASELINE-004/` | Tests pass (or expected skips documented). | Failing tests without accepted baseline rationale. | same format | Yes |
| P1-G04 | `./gradlew lint` | Validate lint baseline. | Codex/Copilot | `reports/ts18/<YYYY-MM-DD>/TS18-P1-BASELINE-005/` | Lint completes; findings triaged/documented. | Lint execution failure or untriaged blocking issues. | same format | Yes |
| P1-G05 | `find scripts -type f -name '*.sh' -print -exec sh -n {} \;` | Validate shell script syntax. | Codex/Copilot | `reports/ts18/<YYYY-MM-DD>/TS18-P1-BASELINE-006/` | No syntax errors. | Any syntax errors. | same format | Yes |

Environment blocker handling:
- If Gradle gates fail due to cloud-agent JDK/SDK/toolchain download constraints, classify as **environment blocker** with reproducible command output.
- This is not automatically a project failure, but still a phase blocker until resolved or explicitly accepted by human operator.

**STOP:** do not proceed to Phase 2 until all P1 gates pass or are formally classified with approved blocker disposition.

---

## 6) Phase 1–4 checklist (strict)

## Phase 1 — Repo/build baseline

| Scenario ID | Exact command(s) | Before capture | During capture | Timestamp + SHA requirement | Required artifacts | Pass/fail criteria | Confidence label | Porting decision label | Follow-up action |
|---|---|---|---|---|---|---|---|---|---|
| TS18-P1-BASELINE-001 | `git rev-parse --abbrev-ref HEAD && git rev-parse HEAD` | Confirm intended branch. | Record output. | Required in `environment.txt`. | `reports/ts18/<YYYY-MM-DD>/TS18-P1-BASELINE-001/environment.txt` | Pass: branch+SHA captured. Fail: missing provenance. | Observed | Directly reusable requirement | Re-run and capture provenance. |
| TS18-P1-BASELINE-002 | `./gradlew tasks` | Ensure local toolchain configured. | Capture full command output. | Required. | `commands.txt`, `README.md` | Pass: exit 0. Fail: non-zero => blocker record. | Observed/Requires TS18 validation (if blocked) | Directly reusable requirement | Classify blocker or proceed. |
| TS18-P1-BASELINE-003 | `./gradlew assembleDebug` | Clean working tree for baseline. | Capture output + artifacts. | Required. | `commands.txt`, build output ref | Pass: assemble success. Fail: blocker record. | Observed | Directly reusable requirement | Fix build or classify blocker. |
| TS18-P1-BASELINE-004 | `./gradlew test` | None. | Capture output. | Required. | `commands.txt`, test report ref | Pass: tests pass or documented baseline status. | Observed | Directly reusable requirement | Triage failures. |
| TS18-P1-BASELINE-005 | `./gradlew lint` | None. | Capture output. | Required. | `commands.txt`, lint report ref | Pass: lint completes + triage. | Observed | Directly reusable requirement | Triage lint blockers. |
| TS18-P1-BASELINE-006 | `find scripts -type f -name '*.sh' -print -exec sh -n {} \;` | Ensure scripts dir present. | Capture output. | Required. | `commands.txt` | Pass: no syntax errors. | Observed | Reusable validation idea | Fix script syntax if needed. |

**STOP:** Phase 1 incomplete => do not begin Phase 2.

## Phase 2 — Stock `com.tw.music` baseline

| Scenario ID | Exact command(s) | Human before command | Human during capture window | Timestamp + SHA requirement | Required artifacts | Pass/fail criteria | Confidence label | Porting decision label | Follow-up action |
|---|---|---|---|---|---|---|---|---|---|
| TS18-STOCK-001 | `AUXIO_TS_PACKAGE=com.tw.music ./scripts/ts18_collect_auxio_ts_evidence.sh stock-idle` | Ensure stock app idle/no active playback. | Observe launcher/widget/notification idle state. | Required. | Scenario folder bundle + manual notes. | Pass: baseline idle captures complete. | Observed | Useful as evidence only | Continue to playing state. |
| TS18-STOCK-002 | `AUXIO_TS_PACKAGE=com.tw.music ./scripts/ts18_collect_auxio_ts_evidence.sh stock-playing-local` | Start known local track in stock app. | Observe metadata freshness + controls. | Required. | `media_session.txt`, `audio.txt`, notes. | Pass: active playback evidence captured. | Observed | Useful as evidence only | Compare with Auxio in Phase 4. |
| TS18-STOCK-003 | `adb shell dumpsys notification` | Place launcher/home widget in view. | Exercise widget controls if available. | Required. | notification/logcat/manual notes. | Pass: widget/control behavior documented. | Observed/Requires TS18 validation | Requires TS18 runtime validation | Define parity target. |
| TS18-STOCK-004 | `adb shell dumpsys media_session && adb shell dumpsys audio` | Prepare steering-wheel/media keys. | Trigger keys repeatedly and log behavior. | Required. | media_session/audio/logcat/manual. | Pass: route classification documented. | Observed/Requires TS18 validation | Requires TS18 runtime validation | Map route type for parity. |
| TS18-STOCK-005 | `adb shell dumpsys audio` | Start navigation prompt scenario if safe. | Observe duck/mute/recover. | Required. | audio + manual observations. | Pass: focus/nav-mixing behavior recorded. | Observed | Reusable validation idea | Use same path for Auxio. |
| TS18-STOCK-006 | `AUXIO_TS_PACKAGE=com.tw.music ./scripts/ts18_collect_auxio_ts_evidence.sh stock-sleep-resume` | Prepare sleep/resume cycle. | Perform sleep/resume and observe continuity. | Required. | evidence bundle + notes. | Pass: resume continuity documented. | Observed/Requires TS18 validation | Requires TS18 runtime validation | Build parity checklist. |

**STOP:** Missing stock baseline scenarios block Phase 3/4 parity confidence.

## Phase 3 — Auxio-TS standard Android media validation

| Scenario ID | Exact command(s) | Human before command | Human during capture window | Timestamp + SHA requirement | Required artifacts | Pass/fail criteria | Confidence label | Porting decision label | Follow-up action |
|---|---|---|---|---|---|---|---|---|---|
| TS18-AUXIO-001 | `AUXIO_TS_PACKAGE=org.oxycblt.auxio ./scripts/ts18_collect_auxio_ts_evidence.sh auxio-idle` | Install current Auxio-TS build. | Confirm idle baseline. | Required. | scenario bundle. | Pass: idle baseline complete. | Observed | Directly reusable requirement | Continue playback scenarios. |
| TS18-AUXIO-002 | `AUXIO_TS_PACKAGE=org.oxycblt.auxio ./scripts/ts18_collect_auxio_ts_evidence.sh auxio-mp3-playing` | Start MP3 playback. | Observe session/notification controls. | Required. | media_session/audio/notification. | Pass: MP3 behavior captured. | Observed | Directly reusable requirement | Compare with stock. |
| TS18-AUXIO-003 | `AUXIO_TS_PACKAGE=org.oxycblt.auxio ./scripts/ts18_collect_auxio_ts_evidence.sh auxio-flac-playing` | Start FLAC playback. | Observe decode/seek/resume behavior. | Required. | bundle + notes. | Pass: FLAC evidence captured. | Observed | Directly reusable requirement | Feed parity matrix. |
| TS18-AUXIO-004 | `adb shell dumpsys notification` | Ensure notification visible. | Repeatedly test play/pause/next/prev. | Required. | notification + manual notes. | Pass: control reliability documented. | Observed | Directly reusable requirement | Identify control deltas. |
| TS18-AUXIO-005 | `adb shell dumpsys media_session && adb shell dumpsys audio` | Prepare key events. | Trigger keys, classify route/outcome. | Required. | dumps + logcat/manual. | Pass: key path documented. | Observed/Requires TS18 validation | Requires TS18 runtime validation | Decide if gap exists. |
| TS18-AUXIO-006 | `AUXIO_TS_PACKAGE=org.oxycblt.auxio ./scripts/ts18_collect_auxio_ts_evidence.sh auxio-zlink-tlink` | Set ZLink/TLink idle then active. | Record focus/session ownership transitions. | Required. | paired evidence sets. | Pass: coexistence behavior documented. | Observed/Requires TS18 validation | Requires TS18 runtime validation | Flag adapter need only with proof. |
| TS18-AUXIO-007 | `adb shell dumpsys audio` | Prepare navigation prompt. | Observe duck/mix/recover behavior. | Required. | audio + notes. | Pass: nav-mixing recorded. | Observed | Reusable validation idea | Compare stock/Auxio. |
| TS18-AUXIO-008 | `AUXIO_TS_PACKAGE=org.oxycblt.auxio ./scripts/ts18_collect_auxio_ts_evidence.sh auxio-sleep-resume` | Prepare sleep/resume cycle. | Perform cycle and observe restoration. | Required. | bundle + notes. | Pass: restoration behavior captured. | Observed/Requires TS18 validation | Requires TS18 runtime validation | Add parity decision item. |

**STOP:** Phase 3 incomplete => do not claim TS18 compatibility; do not start adapter work.

## Phase 4 — Stock-vs-Auxio parity comparison

| Scenario ID | Stock input path | Auxio input path | Comparison method | Output artifact | Decision | Adapter implication | Stop condition |
|---|---|---|---|---|---|---|---|
| TS18-PARITY-001 | `reports/ts18/<YYYY-MM-DD>/TS18-STOCK-002/` | `reports/ts18/<YYYY-MM-DD>/TS18-AUXIO-002/` | Capture comparator outputs per package (`./scripts/ts18_compare_media_sessions.sh com.tw.music` and `./scripts/ts18_compare_media_sessions.sh org.oxycblt.auxio`) then diff saved outputs. | `parity_media_session.md` | Session owner/state/actions/metadata parity? | If no, investigate standard path first. | Missing comparable captures => STOP. |
| TS18-PARITY-002 | `reports/ts18/<YYYY-MM-DD>/TS18-STOCK-002/` + `reports/ts18/<YYYY-MM-DD>/TS18-STOCK-003/` | `reports/ts18/<YYYY-MM-DD>/TS18-AUXIO-004/` | Manual + notification dump diff | `parity_notification_controls.md` | Control reliability parity? | Adapter not allowed unless reproducible standard-path gap. | Non-repeatable result => STOP. |
| TS18-PARITY-003 | `reports/ts18/<YYYY-MM-DD>/TS18-STOCK-003/` | `reports/ts18/<YYYY-MM-DD>/TS18-AUXIO-004/` | Manual launcher/widget A/B matrix | `parity_launcher_widget.md` | Widget metadata/control parity? | Candidate `launcher/widget` adapter only if proven gap. | No launcher scenario reproducibility => STOP. |
| TS18-PARITY-004 | `reports/ts18/<YYYY-MM-DD>/TS18-STOCK-004/` | `reports/ts18/<YYYY-MM-DD>/TS18-AUXIO-005/` | Key-route classification comparison | `parity_media_keys.md` | Same route class and success profile? | Candidate `broadcast` adapter only with proof. | Route ambiguous => STOP. |
| TS18-PARITY-005 | `reports/ts18/<YYYY-MM-DD>/TS18-STOCK-005/` | `reports/ts18/<YYYY-MM-DD>/TS18-AUXIO-007/` | Audio focus/nav-mix comparison | `parity_audio_focus_nav.md` | Equivalent duck/mix/recover? | Candidate `service` hook only with proof. | Missing focus traces => STOP. |
| TS18-PARITY-006 | `reports/ts18/<YYYY-MM-DD>/TS18-STOCK-001/` + `reports/ts18/<YYYY-MM-DD>/TS18-STOCK-006/` | `reports/ts18/<YYYY-MM-DD>/TS18-AUXIO-006/` | Idle-vs-active coexistence comparison | `parity_zlink_tlink.md` | Coexistence acceptable? | Candidate `zlink/tlink hooks` only if required. | No active projection evidence => STOP. |
| TS18-PARITY-007 | `reports/ts18/<YYYY-MM-DD>/TS18-STOCK-002/` | `reports/ts18/<YYYY-MM-DD>/TS18-AUXIO-003/` | FLAC + metadata behavior matrix | `parity_flac_metadata.md` | FLAC and metadata parity acceptable? | No adapter by default; fix standard stack first. | Incomplete FLAC matrix => STOP. |
| TS18-PARITY-008 | `reports/ts18/<YYYY-MM-DD>/TS18-STOCK-006/` | `reports/ts18/<YYYY-MM-DD>/TS18-AUXIO-008/` | Sleep/resume behavior matrix | `parity_sleep_resume.md` | Resume continuity parity acceptable? | Candidate adapter only if reproducible TS18-specific gap. | No repeated cycle evidence => STOP. |

**STOP:** Phase 4 incomplete => no compatibility claims, no adapter implementation PR.

---

## 7) “Cannot claim compatibility yet” checkpoints

TS18 compatibility cannot be claimed until reproducible evidence exists for:
- build/install baseline;
- active MediaSession behavior;
- media notification controls;
- launcher/widget behavior;
- media key or steering wheel control path;
- local playback including FLAC;
- audio focus/navigation mixing behavior;
- sleep/resume behavior;
- ZLink/TLink coexistence (if applicable);
- stock-vs-Auxio parity comparison.

Missing evidence blocks:
- user-facing release claims;
- adapter implementation;
- draft releases with TS18 compatibility language;
- replacement of stock `com.tw.music`;
- disabling/removing stock app.

---

## 8) Contract-to-adapter backlog seeding

Adapter candidates are constrained to:
- `broadcast`
- `service`
- `launcher/widget`
- `theme probe`
- `zlink/tlink hooks`
- `no adapter`

| Contract name | Evidence source | Risk | Adapter candidate module | Default state | Required TS18 runtime proof before implementation | Capture phase | Implementation status |
|---|---|---|---|---|---|---|---|
| `com.tw.music` package + `android.uid.system` | `docs/TS18_NATIVE_CONTRACTS.md`, diagnostics/profile docs | High | no adapter | off | Demonstrate unavoidable requirement without privileged path alternative (unlikely). | 2/4 | Evidence-only (do not implement) |
| `com.tw.music.action.cmd\|prev\|next\|pp` | `docs/TS18_NATIVE_CONTRACTS.md` | High | broadcast | off | Reproducible standard media-key/control failure + proven broadcast remediation on TS18 (literal actions: `cmd\|prev\|next\|pp`). | 2/3/4 | Evidence-only until proven |
| `com.tw.service` | contracts + diagnostics | High | service | off | Demonstrate reproducible focus/control gap solved only by optional service mediation. | 3/4 | Evidence-only until proven |
| `com.tw.service.xt` / `CommandService.Bind` | contracts doc | High | service | off | Demonstrate service availability and safe non-privileged integration benefit. | 2/3/4 | Evidence-only until proven |
| `TWTHEME` / `MusicTheme.apk` | contracts + diagnostics insights | High | theme probe | off | Prove launcher/theme behavior gap and probe-only mitigation. | 2/4 | Evidence-only until proven |
| ZLink (`com.zjinnova.zlink`) coexistence | contracts + diagnostics/profile docs | High | zlink/tlink hooks | off | Show reproducible coexistence problem not solved by standard Android path. | 3/4 | Evidence gathering only |
| Launcher/home widget non-standard behavior | runbook/contracts | High | launcher/widget | off | Reproducible A/B widget gap under controlled scenarios. | 2/4 | Evidence-only until proven |

High-risk items remain evidence-only unless validated:
- `com.tw.music.action.*`
- `com.tw.service.xt`
- privileged identity patterns
- `android.uid.system`
- `com.tw.music` package impersonation

---

## 9) First implementation entry criteria

Before Phase 6 / adapter skeleton work starts, all conditions below must be true:
- Phase 1 build baseline completed **or** blocker formally classified and accepted.
- Phase 2 stock baseline captured.
- Phase 3 Auxio-TS baseline captured.
- Phase 4 parity matrix completed.
- At least one adapter module has clear evidence-backed need.
- Each candidate adapter has an **off-by-default** design.
- Rollback/coexistence plan documented.
- Human TS18 operator confirmed relevant runtime behavior.

**STOP:** do not start adapter implementation until these criteria are met.

---

## 10) One-variable PR policy

Aligned with `docs/DEVELOPMENT_ROADMAP.md` one-primary-variable rule.

Acceptable Phase 1–4 PR scopes (single variable):
- documentation-only execution pack refinement;
- build baseline fix only;
- evidence script reliability fix only;
- stock baseline evidence import only;
- Auxio-TS baseline evidence import only;
- parity matrix update only.

Too-broad scopes (reject/split):
- evidence import + adapter implementation in same PR;
- package identity change + media controls change;
- launcher integration + ZLink changes together;
- Gradle versioning + TS18 service hooks together;
- broad UI rewrite during validation phases.

---

## 11) Prioritized next PR candidates

1) **PR title:** Phase 1 build/toolchain baseline hardening  
   **Phase:** 1  
   **Scope boundaries:** baseline command reliability and blocker taxonomy only.  
   **Likely files/modules:** docs/runbook references, build scripts, CI notes.  
   **Non-goals:** TS18 adapters, media behavior changes.  
   **Required preconditions:** none.  
   **Expected artifacts:** `TS18-P1-BASELINE-00x` outputs under `reports/ts18/<YYYY-MM-DD>/...`  
   **Stop condition:** unresolved unclassified Gradle blockers.

2) **PR title:** Phase 2 stock `com.tw.music` baseline evidence import  
   **Phase:** 2  
   **Scope boundaries:** evidence capture + redacted summaries only.  
   **Likely files/modules:** `reports/ts18/...`, docs evidence summaries.  
   **Non-goals:** Auxio behavior edits.  
   **Required preconditions:** Phase 1 baseline complete/classified.  
   **Expected artifacts:** `TS18-STOCK-001..006` folders + summaries.  
   **Stop condition:** missing reproducible stock capture set.

3) **PR title:** Phase 3 Auxio-TS MediaSession/notification evidence import  
   **Phase:** 3  
   **Scope boundaries:** Auxio runtime evidence only.  
   **Likely files/modules:** evidence folders + summary docs.  
   **Non-goals:** adapter code, package/signature changes.  
   **Required preconditions:** Phase 1 + Phase 2 complete/classified.  
   **Expected artifacts:** `TS18-AUXIO-001..008` folders + summaries.  
   **Stop condition:** incomplete MP3/FLAC/session/notification captures.

4) **PR title:** Phase 4 stock-vs-Auxio parity matrix  
   **Phase:** 4  
   **Scope boundaries:** parity documents and comparator outputs only.  
   **Likely files/modules:** parity reports, docs matrices.  
   **Non-goals:** adapter implementation.  
   **Required preconditions:** complete Phase 2 and Phase 3 evidence bundles.  
   **Expected artifacts:** `parity_*.md` outputs for `TS18-PARITY-001..008`.  
   **Stop condition:** ambiguous or non-repeatable deltas.

5) **PR title:** Phase 6 adapter skeleton readiness decision (gate-only)  
   **Phase:** 5/6 boundary  
   **Scope boundaries:** decision memo only, no implementation.  
   **Likely files/modules:** roadmap/status docs.  
   **Non-goals:** enabling TW contracts.  
   **Required preconditions:** Phase 4 complete with at least one evidence-backed gap.  
   **Expected artifacts:** readiness decision document with off-by-default candidate(s).  
   **Stop condition:** no evidence-backed adapter necessity.

---

## 12) Handoff block (copy-ready)

```text
NEXT CODEX TASK HANDOFF

Phase:
- <Phase 1 | Phase 2 | Phase 3 | Phase 4>

Single variable to tackle:
- <one bounded variable only>

Inspect first:
1) docs/TS18_EXECUTION_PACK_PHASE1_4.md
2) reports/ts18/<YYYY-MM-DD>/<scenario-id>/summary.json
3) reports/ts18/<YYYY-MM-DD>/<scenario-id>/commands.txt
4) docs/TS18_NATIVE_CONTRACTS.md

Do not change:
- Android package identity/signing/privileged UID assumptions
- Core playback/library architecture
- Adapter implementation (unless explicit gate says Phase 6 entry criteria passed)

Evidence to produce:
- Scenario folders under reports/ts18/<YYYY-MM-DD>/<scenario-id>/
- Claim records with confidence + porting-decision labels
- Clear pass/fail + unresolved risks

Stop when:
- scenario artifacts are complete and reproducible, OR
- blocker is formally classified with exact command evidence
```
