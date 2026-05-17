# Vendor Hook Ownership Map (static)

Last updated: 2026-05-02 (UTC)

This file maps critical vendor-boundary tokens/surfaces to current smali owner files for safe migration work.

| Surface | Owner smali file(s) | Status | Notes |
|---|---|---|---|
| `com.tw.music.action.cmd/.prev/.next/.pp` command flow | `app/apktool/smali_classes3/com/tw/music/MusicService.smali`, `app/apktool/smali_classes3/com/tw/music/k.smali`, `app/apktool/smali_classes3/com/tw/music/view/MusicWidgetProvider.smali` | Mapped | Service + receiver + widget command paths confirmed. |
| TW AIDL descriptor strings (`ITWCommandAidl`, `ITWCommandCallbackAidl`, `IMusicCallBack`, `IRadioCallBack`, `IVideoCallBack`, `IBTCallBack`) | `app/apktool/smali_classes4/c/b/a/a/a/*.smali` (notably `a$a`, `b$a`, `c$a`, `d$a`, `d$a$a`, `e$a`, `f$a`) | Mapped | Descriptor strings are vendor-critical and must remain exact. |
| TW playback property keys `persist.tw.ijk*` | `app/apktool/smali_classes4/tv/danmaku/ijk/media/player/tw/TWMediaPlayer.smali`, `.../TWMediaPlayerView.smali` | Mapped | Header notes added; keys must not change. |
| EQ launch `com.tw.eq.EQActivity` | `app/apktool/smali_classes3/com/tw/music/MusicActivity.smali` | Mapped | External component dependency. |
| Radio handoff surfaces `com.tw.radio.*` | `app/apktool/smali_classes3/com/eckom/xtlibrary/twproject/activity/BaseRadioActivity.smali`, `app/apktool/smali_classes3/com/eckom/xtlibrary/b/h/b/e.smali` | Mapped | Includes `com.tw.radio.theme`, `com.tw.radio.state`, `com.tw.radio.av`. |
| TW theme/system properties (`persist.tw.theme`, `persist.sys.tw.*`) | `app/apktool/smali_classes3/com/eckom/xtlibrary/twproject/activity/XTActivity.smali` | Mapped | Theme switcher integration boundary. |
| BT module/vendor properties (`persist.tw.bt.module`) | `app/apktool/smali_classes3/com/eckom/xtlibrary/twproject/activity/BaseBTActivity.smali`, `app/apktool/smali_classes3/com/eckom/xtlibrary/b/a/a/b.smali`, `.../b/a/d/f.smali`, `.../b/a/e/a.smali` | Mapped | Vendor variant behavior gating. |
| Media mode/property toggles (`persist.media.type`, `persist.media.forward`) | `app/apktool/smali_classes3/com/eckom/xtlibrary/b/f/e/a.smali`, `.../b/k/b/a.smali`, `.../b/f/d/L.smali`, `.../b/f/d/t.smali`, `.../b/f/f/s.smali` | Mapped | Impacts playback path and model selection. |

## Open follow-ups

- Method-level ownership mapping for MediaSession/PlaybackState/MediaMetadata publication is still open.
- Runtime/firmware behavioral confirmation on TS18 remains blocked in this environment.
