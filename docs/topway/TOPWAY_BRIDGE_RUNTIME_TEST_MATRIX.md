# Topway Bridge Runtime Test Matrix for PR #34

This matrix turns the apktool/JADX findings into implementation tests for Auxio-TS.

## Contract tests

- `ACTION_MUSIC_INFO == "com.tw.music.info"`
- `EXTRA_MUSIC_ARTIST == "musicaArtist"` exactly
- `ACTION_PROGRESS_DURATION == "com.tw.launcher.music_progress_duration"`
- `ACTION_LAUNCHER_WIDGET_SEEK == "com.android.launcher.widget_music_progress"`
- command actions and command values match observed Topway strings
- allowed Topway strings are centralised in `TopwayMusicContract`

## Command mapper tests

| Case | Expected |
| --- | --- |
| `ACTION_PREV` | previous |
| `ACTION_NEXT` | next |
| `ACTION_PLAY_PAUSE` | play/pause |
| `ACTION_CMD + cmd=prev` | previous |
| `ACTION_CMD + cmd=next` | next |
| `ACTION_CMD + cmd=pp` | play/pause |
| `ACTION_CMD + cmd=update` | widget update |
| `ACTION_CMD + missing cmd` | unknown/no-op |
| `ACTION_CMD + unknown cmd` | unknown/no-op |
| unrelated action | unknown/no-op |

## Receiver/helper tests

If Android framework receiver tests are hard, extract helper logic and test it.

| Case | Expected |
| --- | --- |
| cold external `ACTION_PREV` | delegates to existing safe previous path |
| cold external `ACTION_NEXT` | delegates to existing safe next path |
| cold external `ACTION_PLAY_PAUSE` with no current song | safe no-op; no forced autoplay |
| `cmd=update` with widgets present | triggers widget refresh path |
| `cmd=update` with no widgets | safe no-op |
| malformed extras | safe no-op |
| receiver onReceive | no long work, no direct heavy operations |

## Seek tests

| Case | Expected |
| --- | --- |
| valid int progress | seek target accepted/clamped |
| valid long progress | seek target accepted/clamped |
| negative progress | clamped to zero or rejected safely |
| beyond duration | clamped to duration or rejected safely |
| zero/unknown duration | no-op |
| missing extra | no-op |
| non-number extra | no-op |
| no current song/session | no-op |

## Intent factory tests

| Case | Expected |
| --- | --- |
| metadata snapshot | intent action `com.tw.music.info`, expected extras |
| null/missing values | no crash, safe empty or omitted values according to policy |
| progress valid | intent action `com.tw.launcher.music_progress_duration`, ms extras |
| progress negative | non-negative extras |
| duration unknown | safe zero/clear behaviour |
| duplicate metadata | bridge suppresses duplicate publish where implemented |

## Widget render-state tests

| Case | Expected |
| --- | --- |
| active song | title/artist/time/duration/progress populated |
| no session | placeholder title/subtitle/time/duration/progress/artwork |
| null artwork after previous artwork | fallback resource/path, no stale art |
| artist == album artist | no duplicate subtitle |
| duration zero | progress max safe, time text safe |
| position beyond duration | progress clamped |
| position negative | progress clamped |
| hour+ track | `h:mm:ss` formatting |
| under hour track | `m:ss` formatting |

## Guardrail tests

- Topway strings are allowed only in:
  - `app/src/main/java/org/oxycblt/auxio/headunit/topway/`
  - `app/src/test/java/org/oxycblt/auxio/headunit/topway/`
  - `docs/`
  - `docs/topway/`
  - manifest only for the approved receiver action filters, if the implementation requires a static receiver.
- `android.uid.system` is forbidden.
- `sharedUserId` is forbidden.
- `com.tw.service.xt` runtime binding is forbidden.
- `ITWCommandAidl` runtime binding is forbidden.
- `Class.forName("android.tw...")` is forbidden.
- vendor package scanners/probe frameworks are forbidden.
