# JADX alias and apktool usage guide for Topway music decompile

## Why this matters

The JADX output is easier to read, but it rewrites some package/class names. The apktool output is harder to read, but it preserves runtime package/class/action names more directly.

For implementation decisions, use this rule:

```text
apktool manifest/resources/smali strings define runtime identity and wire contracts.
JADX aliased Java is used for method/control-flow readability.
```

## Alias caveats

JADX outputs may show packages such as:

```text
com.p060tw.music.*
com.p073tw.music.*
p060c.p063b.p064a.*
com.eckom.xtlibrary.p066b.*
```

These are decompiler alias artefacts. They do **not** mean the runtime package is `com.p060tw.music` or `com.p073tw.music`.

Runtime identity from apktool `AndroidManifest.xml` is:

```text
package="com.tw.music"
sharedUserId="android.uid.system"
```

Runtime first-party smali paths include:

```text
smali_classes3/com/tw/music/MusicActivity.smali
smali_classes3/com/tw/music/MusicService.smali
smali_classes3/com/tw/music/view/MusicWidgetProvider.smali
smali_classes3/com/tw/music/media/MediaControlBridge.smali
smali_classes3/com/tw/music/media/MediaNotificationController.smali
```

Readable JADX equivalents include:

```text
reference/firstparty-jadx/sources/com/p060tw/music/MusicActivity.java
reference/firstparty-jadx/sources/com/p060tw/music/MusicService.java
reference/firstparty-jadx/sources/com/p060tw/music/view/MusicWidgetProvider.java
reference/firstparty-jadx/sources/com/p060tw/music/C0780j.java
reference/firstparty-jadx/sources/com/p060tw/music/C0781k.java
```

## Practical workflow for future agents

1. Start with `AndroidManifest.xml`, `res/layout/music_widget.xml`, `res/xml/appwidget_info.xml`, and first-party `com/tw/music` smali.
2. Use `reference/firstparty-jadx` to understand control flow.
3. Use `reference/vendor-jadx` / `reference/jadx-aliased` for XT library bridge behaviour.
4. Treat `com.tw.service.xt` / `ITWCommandAidl` as real evidence but blocked for production unless a future explicit design PR approves a safe contract.
5. Do not copy decompiled code or smali. Port behaviours and string contracts only.

## Helpful grep commands

```sh
rg -n "com\.tw\.music\.info|com\.tw\.launcher\.music_progress_duration|com\.android\.launcher\.widget_music_progress|com\.tw\.music\.action" reference app/apktool
rg -n "musicTitle|musicaArtist|musicAlbum|musicPath|msg_music_progress|msg_music_duration|music_progress" reference app/apktool
rg -n "MusicWidgetProvider|RemoteViews|setOnClickPendingIntent|updateAppWidget|sendStickyBroadcast|registerReceiver" reference app/apktool
rg -n "com\.tw\.service\.xt|ITWCommandAidl|IMusicCallBack|CommandService" reference app/apktool
```
