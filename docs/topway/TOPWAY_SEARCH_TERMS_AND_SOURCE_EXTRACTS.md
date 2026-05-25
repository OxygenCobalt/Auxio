# Topway search terms and source extracts

These terms were extracted from the official Topway music decompile and are better targets for future web/source searches than generic `TWTHEME widget` queries.

## Exact action / extra strings

```text
"com.tw.music.info"
"musicTitle"
"musicaArtist"
"musicAlbum"
"musicPath"

"com.tw.launcher.music_progress_duration"
"msg_music_progress"
"msg_music_duration"

"com.android.launcher.widget_music_progress"
"music_progress"

"com.tw.music.action.cmd"
"com.tw.music.action.prev"
"com.tw.music.action.next"
"com.tw.music.action.pp"
"cmd"
"prev"
"next"
"pp"
"update"
"appWidgetIds"

"com.tw.service.xt"
"com.tw.service.xt.CommandService"
"com.tw.service.xt.CommandService.Bind"
"com.tw.service.xt.aidl.ITWCommandAidl"
"com.tw.service.xt.aidl.IMusicCallBack"
```

## Suggested web/source queries

```text
"com.tw.music.info" "musicaArtist"
"com.tw.launcher.music_progress_duration" "msg_music_duration"
"com.android.launcher.widget_music_progress" "music_progress"
"com.tw.music.action.pp" "cmd" "update"
"com.tw.service.xt.aidl.ITWCommandAidl" "IMusicCallBack"
"com.tw.service.xt.CommandService.Bind" "Topway"
"MusicWidgetProvider" "com.tw.music.action.cmd"
"msg_music_progress" "Topway" launcher
"musicaArtist" "com.tw.music.info"
```

## Current web-search result status

Exact public searches for the core strings produced little/no useful public documentation. Therefore the local decompile is currently the strongest compatibility source for these behaviours.

## Source confidence

| Behaviour | Source confidence | Porting decision |
|---|---|---|
| Widget layout fields/title/artist/time/duration/progress/art/buttons | Observed | Directly reusable requirement |
| Widget `cmd=update` broadcast shape | Observed | Reusable validation idea / selectively reusable |
| Transport command action strings | Observed | Directly reusable requirement if isolated |
| `com.tw.music.info` metadata broadcast | Observed | Directly reusable requirement if isolated |
| Launcher progress/duration broadcast | Observed | Directly reusable requirement if isolated/throttled |
| Launcher seek action | Observed | Directly reusable requirement if safely gated |
| XT AIDL binding | Observed | Unsafe to port by default |
| `sharedUserId=android.uid.system` | Observed | Should be explicitly avoided |
