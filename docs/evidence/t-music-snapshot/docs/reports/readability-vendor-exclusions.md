# Readability Vendor/External-Contract Exclusion List (R1)

Last updated: 2026-05-03 (UTC)

Purpose: enumerate symbol/token families that are out-of-scope for speculative rename and require mapping-only or explicit compatibility proof.

## Excluded token families (do not alter semantics)

1. `com.tw.music.action.cmd`, `com.tw.music.action.prev`, `com.tw.music.action.next`, `com.tw.music.action.pp`
2. AIDL interface descriptors under `com.tw.service.xt.aidl.*`
3. TW/media properties: `persist.tw.*`, relevant `persist.media.*`
4. EQ launch component: `com.tw.eq/.EQActivity`
5. Radio integration surfaces under `com.tw.radio.*`
6. Theme compatibility hooks tied to TW theme selection and `@style/AppTheme`

## Excluded symbol classes (classification default)

- Category: `vendor token / external contract: do not rename`
- Applies to classes/methods/fields where behavior depends on exact external strings/tokens/components.
- Preferred readability action: mapping-only + evidence notes + safety comments.

## Evidence anchors in current repo

- Vendor hook enumeration source: `docs/reports/vendor-hooks.txt`.
- Canonical runtime contract boundaries and hard rules: `AGENTS.md`, `docs/target-device-ts18-android13.md`, `docs/deobf/enigma-notes.md`.
- Existing enforcement check: `scripts/08_verify_vendor_tokens.sh`.

## Next actions

- Link concrete class owners from `docs/reports/vendor-hook-owners.md` into this list as R1 coverage matures.
- Keep this file synchronized with any new vendor-token discoveries from refreshed hook scans.
