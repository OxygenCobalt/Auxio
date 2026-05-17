# Readability Unsafe-Symbol Queue (R1)

Last updated: 2026-05-03 (UTC)

Purpose: record symbols/areas where descriptor rename is unsafe pending deeper evidence.

## Classification defaults used in this queue

- `unsafe due to reflection/JNI/serialization/resource/runtime coupling`
- `mapping-only candidate`
- `unknown: requires more evidence`

## Seed unsafe areas

1. **MediaSession/PlaybackState/MediaMetadata publication owners (unresolved wrappers)**
   - Why unsafe: static scan found callback-level publication patterns, but not direct framework symbol owners in canonical smali.
   - Current status: unresolved static ownership chain bounded in deobf notes.
   - Initial classification: `unknown` -> likely `mapping-only` until owner proof is complete.

2. **Notification MediaStyle + foreground promotion owners**
   - Why unsafe: no direct non-support-library `NotificationCompat.Builder` / `startForeground` owner located in current static pass.
   - Current status: bounded unresolved candidate set.
   - Initial classification: `unknown`.

3. **Vendor AIDL stub transaction surfaces (`com.tw.service.xt.aidl.*`)**
   - Why unsafe: token and transaction coupling risk.
   - Initial classification: `vendor token / external contract: do not rename` (mapping-only annotations allowed).

4. **TW/Ijk property-gated playback wrappers (`persist.tw.ijk*`, `persist.media.*`)**
   - Why unsafe: runtime behavior coupled to vendor firmware properties and potentially hidden wrappers.
   - Initial classification: `mapping-only candidate` unless exhaustive coupling proof exists.

5. **Potential reflection/string-coupled class loading zones**
   - Why unsafe: class-name string coupling may break descriptor renames.
   - Action: keep in queue until class-loading/string evidence is inventoried in R2+.

## Exit criteria from unsafe queue

A symbol exits this queue only when all are satisfied:
- high-confidence behavior evidence,
- external contract risk ruled out,
- descriptor/reference rewrite scope enumerated,
- post-change checks pass (`build` when required + vendor token guard).
