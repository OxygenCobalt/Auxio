# Codec Capability Matrix (Planning and Validation)

## Purpose

This matrix separates **discoverability intent** (scanner/file-extension filtering) from **proven runtime decode capability** on TS18 Android 13.

Important rules:

- File extension presence does **not** prove decode support.
- `m4a` discoverability does **not** prove ALAC support.
- ALAC remains unproven until explicit runtime/device validation.
- FLAC must be validated before claiming complete support in UI/docs.
- TW/IJK playback stack is a compatibility boundary and must not be replaced casually.

## Current planning matrix

| Format | Extension(s) | Currently discoverable by scanner/UI? | Decode support proven? | Metadata/tag support proven? | Seek support proven? | Album art support proven? | Validation status | Notes |
|---|---|---|---|---|---|---|---|---|
| MP3 | `.mp3` | Yes (expected) | Partially proven (legacy baseline) | Partially proven | Partially proven | Partially proven | Needs refreshed TS18 evidence | Existing playback baseline appears stable but requires current-cycle evidence capture. |
| AAC | `.aac` | Yes (expected) | Partially proven (legacy baseline) | Partially proven | Partially proven | Partially proven | Needs refreshed TS18 evidence | Validate both raw AAC and containerized variants. |
| M4A | `.m4a` | Yes (expected) | Partially proven for common AAC-in-M4A | Partially proven | Partially proven | Partially proven | Needs refreshed TS18 evidence | Container discoverable; codec subtype (AAC vs ALAC) must be distinguished during validation. |
| WAV | `.wav` | Yes (expected) | Unproven in current validation cycle | Unproven | Unproven | Unproven | Blocked pending device run | Confirm PCM variants, duration parsing, and seek behaviour. |
| FLAC | `.flac` | Yes (expected) | Unproven in current validation cycle | Unproven | Unproven | Unproven | Priority validation required | Validate 16-bit and 24-bit samples before support claims. |
| APE | `.ape` | Possibly discoverable | Unproven | Unproven | Unproven | Unproven | Investigation required | Discoverability and decode may diverge depending on IJK/TW build options. |
| DTS | `.dts` | Possibly discoverable | Unproven | Unproven | Unproven | Unproven | Investigation required | High risk of hardware/firmware dependency; do not claim until playback proven. |
| AIFF | `.aiff`, `.aif` | Possibly discoverable | Unproven | Unproven | Unproven | Unproven | Investigation required | Validate parser compatibility and metadata handling. |
| ALAC | `.m4a`, `.alac` (if present) | Maybe via `.m4a` path | **Unproven** | Unproven | Unproven | Unproven | Explicit ALAC test required | M4A presence does not imply ALAC decode; must be runtime-tested with known ALAC samples. |

## Evidence policy

Only mark a cell as proven when the validation artifact set exists:

1. Test media sample inventory (file type/profile documented).
2. Runtime playback evidence on TS18 Android 13.
3. Logcat evidence for codec/open/seek behaviour.
4. User-visible evidence for metadata/artwork/duration correctness.
