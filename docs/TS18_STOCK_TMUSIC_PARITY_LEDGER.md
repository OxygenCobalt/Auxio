# TS18 Stock/t-music Parity Ledger

Evidence-confidence and porting-decision labels are mandatory.

| Area | Stock/t-music behavior | Evidence source | Auxio Tier 1 equivalent | Known gap | Scenario | Native relevance | Confidence | Porting decision | Next action |
|---|---|---|---|---|---|---|---|---|---|
| Metadata publication | Stock publishes active metadata across surfaces. | docs/evidence/t-music-snapshot + TS18 docs | MediaSession metadata publication | Runtime parity on TS18 not yet proven | TS18-STD-001 | Investigate only on proven failure | Requires TS18 validation | Directly reusable requirement | Capture evidence pack |
| Notification controls | Stock mirrors playback controls in notification. | TMUSIC comparison + snapshot | Auxio media-style notification | Device-specific visibility uncertain | TS18-STD-002 | Candidate only if controls absent with valid session | Requires TS18 validation | Directly reusable requirement | Validate with dumpsys + screenshot |
| Media keys/steering wheel | Stock routes hardware controls via known command surfaces. | Device profile + diagnostics insights | Standard media key handling | Unknown drop conditions on TS18 launchers | TS18-STD-003 | High relevance if Tier 1 fails repeatedly | Requires TS18 validation | Requires TS18 runtime validation | Gather repeated failure evidence |
| Widget/desktop behavior | Stock home/desktop integration is launcher/theme-sensitive. | TWTHEME ecosystem sources + snapshot | Auxio widget/provider path | Metadata-refresh parity unknown | TS18-STD-011 | Candidate if widget placeable but stale with valid session | Inferred | Requires TS18 runtime validation | Validate with before/after captures |
| ZLink/TLink coexistence | Stock coexists in projection-heavy stacks. | Ecosystem map + diagnostics | Focus/session coexistence via standard APIs | Not validated | TS18-STD-005 | Candidate only if unrecoverable focus/session loss | Inferred | Reusable validation idea | Run coexistence evidence pass |
