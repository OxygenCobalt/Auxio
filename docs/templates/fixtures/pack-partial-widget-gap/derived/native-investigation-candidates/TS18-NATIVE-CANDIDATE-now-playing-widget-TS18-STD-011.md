# TS18 Native/Private Investigation Candidate

- Parity gap summary: TS18-STD-011 in area 'Now-playing widget' has status 'requires manual review'.
- User-visible impact: degrades head-unit UX
- Affected TS18/TWTHEME surface: Now-playing widget
- Evidence pack link: docs/templates/fixtures/pack-partial-widget-gap
- Failed Tier 1 mechanism: Android-standard Tier 1 path did not fully satisfy parity signal.
- Candidate native/private contract: Evidence-only placeholder; manual triage required against inventory.
- Source/evidence basis: docs/templates/fixtures/pack-partial-widget-gap/derived/scenario-results.json and docs/templates/fixtures/pack-partial-widget-gap/derived/parity-gap-matrix-update.md
- Risk classification: Requires human review before any implementation.
- Privilege requirements: Unknown; assume non-privileged only unless proven otherwise.
- Package identity requirements: Must not require com.tw.music impersonation.
- Non-impersonation feasibility: Required.
- Fallback behavior: Retain Tier 1 Android-standard behavior.
- Non-TS18 behavior impact: Must remain unchanged.
- Validation plan: External-only validation on TS18 hardware.
- Rollback plan: Do not merge production code without separate approved design PR.

## Generated metadata
- Scenario: TS18-STD-011
- Suggested priority: medium
- Confidence: Requires TS18 validation
- Porting decision: Requires TS18 runtime validation
- Manual review required: yes