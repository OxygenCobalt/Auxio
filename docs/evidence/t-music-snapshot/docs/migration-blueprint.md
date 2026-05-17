# com.tw.music Migration Blueprint + Unified Execution Tracker

Last refreshed: 2026-05-08 (UTC)
Canonical live execution tracker for sequencing, gate decisions, and phase status.

> **Authoritative source:** This is the only live execution board. Other planning docs are supporting narrative/evidence and must defer to this file for current phase/gate readiness.

---

## 1) Program status at a glance

| Workstream | Phase | Status | Current blocker | Next action |
|---|---|---|---|---|
| Gate and workflow alignment | A | IN PROGRESS | Workflow docs were inconsistent about required static smali validation | Keep this file authoritative and keep workflow docs synced to required checks |
| Readability / ownership (Gate R) | B | IN PROGRESS | Remaining non-critical readability ownership backlog; not all deferred items closed | Continue targeted readability closure only where implementation-gating |
| Source-shim/runtime import (Gate S) | C | PASS | none (static gate satisfied) | Keep guarded by required static validation set on every smali/runtime PR |
| MusicService static wiring (Gate M) | D | IN PROGRESS | TS18 runtime evidence not captured yet | Run Gate V device validation and confirm static wiring behavior at runtime |
| TS18 runtime validation (Gate V) | E | BLOCKED | No TS18 runtime evidence bundle merged yet | Execute manual runbook and ingest dumpsys/logcat/install evidence |
| Release candidate readiness (Gate RC) | F | BLOCKED | Gate V not passed; RC checklist lacks device-backed evidence | Prepare RC checklist only after Gate V PASS evidence is merged |

---

## 2) Gate model (authoritative)

### Gate R — Readability / ownership readiness
- **Status:** IN PROGRESS
- **Evidence already present:** Readability reports and closure tooling are in place (`tools/readability/07_validate_readability_reports.py`, readability reports under `docs/reports/`).
- **Remaining blocker:** Some ownership/readability backlog remains and is not fully closed.
- **Next action:** Continue targeted, evidence-backed readability closure for implementation-critical areas only.
- **Human/manual input required:** No immediate manual device action required; human review is required for semantic rename decisions.

### Gate S — Source-shim/runtime import readiness
- **Status:** PASS
- **Evidence already present:** Runtime bridge smali exists under `app/apktool/smali_classes3/com/tw/music/media/`; bridge descriptor grep is expected to resolve; source-shim pipeline exists.
- **Remaining blocker:** None for static gate status.
- **Next action:** Preserve status by running full required static validation set after smali/runtime touches.
- **Human/manual input required:** None for gate pass maintenance.

### Gate M — MusicService static wiring readiness
- **Status:** IN PROGRESS
- **Evidence already present:** Static bridge wiring and callback-path smali repair work landed (see recent commits and implementation notes).
- **Remaining blocker:** No TS18 runtime proof yet that static wiring behaves correctly end-to-end.
- **Next action:** Execute Gate V and use runtime evidence to close or reopen specific static wiring tasks.
- **Human/manual input required:** Yes — TS18 manual runtime execution/evidence capture.

### Gate V — TS18 runtime validation readiness
- **Status:** BLOCKED
- **Evidence already present:** Static checks/docs exist; runtime acceptance checklist exists.
- **Remaining blocker:** Missing device-backed evidence (`dumpsys media_session`, `dumpsys notification`, TW command parity, media dispatch parity, widget/hardware notes, install/update proof).
- **Next action:** Follow `docs/manual-validation-runbook.md` and attach full evidence bundle.
- **Human/manual input required:** Yes — required on physical TS18 target.

### Gate RC — Release-candidate readiness
- **Status:** BLOCKED
- **Evidence already present:** Release validation matrix/results docs exist.
- **Remaining blocker:** Gate V unresolved; RC evidence set incomplete.
- **Next action:** Keep RC blocked until Gate V passes, then complete RC checklist with proven evidence only.
- **Human/manual input required:** Yes — TS18 validation + reviewer sign-off.

---

## 3) Required static validation set for smali/runtime work

Run from repo root:

```bash
bash -n scripts/codex/setup_readability_env.sh
bash -n scripts/codex/maintain_readability_env.sh
bash -n scripts/build_source_shim.sh
python3 tools/readability/07_validate_readability_reports.py
python3 tools/smali/validate_smali_static.py
bash scripts/08_verify_vendor_tokens.sh
bash tools/readability/06_diff_size_guard.sh
git diff --stat
git diff --numstat
git status --short
```

Optional local Apktool check when tool exists:

```bash
apktool b app/apktool -o .local/<task-name>-check.apk
```

**Do not commit `.local` APK outputs.**

---

## 4) Next-step sequence (execution order)

1. Finish/merge static smali repair sweep (if any targeted fixes remain).
2. Run local static validation including `python3 tools/smali/validate_smali_static.py`.
3. Run optional Apktool build check if `apktool` exists.
4. Execute Gate V TS18 runtime validation.
5. If Gate V fails, create targeted bug-fix PR(s) from captured evidence.
6. If Gate V passes, ingest evidence docs and mark Gate V PASS.
7. Prepare Gate RC checklist.
8. Only then proceed to RC hardening and broader UX/media integration work.

---

## 5) Supporting documents (non-authoritative for live status)

- `docs/status.md` — pointer/status summary only.
- `docs/agent-milestones.md` — roadmap/reference.
- `docs/development-readiness-next-steps.md` — next-action narrative.
- `docs/release-validation-results-2026-05.md` — evidence log.
