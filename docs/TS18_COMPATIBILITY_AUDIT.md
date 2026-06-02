# TS18 Compatibility Audit — 2026-06 Hardening Pass

This audit records the repo-wide TS18/Topway/DoFun compatibility scan performed after the redacted `s9863a1h10` Android 10 device-profile seed. It classifies implementation surfaces without claiming real TS18 runtime success.

## Classification summary

| Surface | Classification | Result |
| --- | --- | --- |
| `standard` package identity | Correct product code | Remains `org.oxycblt.auxio`; no stock-package identity is used by the standard flavor. |
| `topwayTwMusicRelease` package identity | Correct wrapper/bridge code | Dedicated exact replacement identity: `com.tw.music`. Requires package-state/signing management on stock firmware. |
| `topwayTwMediaRelease` package identity | Correct wrapper/bridge code | Dedicated DoFun alternate fixed-entry identity: `com.tw.media`; not a no-root bypass. |
| `com.tw.music.MusicActivity` alias | Correct wrapper/bridge code | Shared wrapper manifest exposes the stock-compatible activity alias for both Topway-compatible packages. |
| CoverProvider authorities | Correct product code | Manifest uses `${applicationId}.image.CoverProvider`; variant resources cover release/debug authorities. |
| `com.tw.music.MusicService` | Correct wrapper/bridge code | Canonical exported Topway-compatible MediaBrowserService wrapper; delegates to Auxio-owned `AuxioService`. |
| `org.oxycblt.auxio.AuxioService` in Topway variants | Correct product code | Kept for explicit internal starts only; browse/search intent filters are removed in the Topway manifest to avoid duplicate external browse-service entrypoints. |
| Topway incoming commands | Correct bridge code | Allowlisted and sanitized via isolated `headunit/topway` bridge. |
| Topway outgoing metadata/progress | Correct bridge code | Published from real widget/playback state through `TopwayMusicBroadcastBridge`; payload keys preserve Topway spellings such as `musicaArtist`. |
| Topway widget update routing | Correct bridge code | Topway-compatible variants serve `cmd=update` even when AppWidgetManager cannot report a normal widget instance, because DoFun may not behave as a normal AppWidget host. |
| Android 10 overlay runtime | Correct product code; requires TS18 validation | API 34 `SPECIAL_USE` foreground-service type is selected only in code on API 34+, manifest avoids hard-coded `specialUse`, and add/update/remove failures stop cleanly. |
| Storage `/storage/usbdisk0` | Requires TS18 runtime validation | Musikr uses Android MediaStore/StorageVolume abstraction; real TS18 validation must confirm whether the USB volume is indexed by MediaStore or needs user-selected access. |
| Private Cardoor/TWUtil/vendor services | Correct docs/reference evidence only | Not implemented in production; remains evidence-gated future work under the tier process. |

## Remaining hardware-only checks

Run the commands in `docs/TS18_RUNTIME_VALIDATION.md` on the actual head unit to validate install conflicts, DoFun launch/widget behavior, duplicate sessions/services/notifications, `/storage/usbdisk0` indexing, overlay permission revocation, boot, and ACC wake.
