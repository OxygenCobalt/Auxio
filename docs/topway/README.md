# Topway `com.tw.music` compatibility evidence

This directory contains source-led requirements extracted from the user-provided official Topway music APK decompile.

Use these files before prompting or implementing TS18/TWTHEME music compatibility work:

1. [`TOPWAY_APKTOOL_AND_JADX_COMPATIBILITY_ANALYSIS.md`](TOPWAY_APKTOOL_AND_JADX_COMPATIBILITY_ANALYSIS.md) — concrete behaviours observed from apktool resources, smali, and JADX outputs.
2. [`JADX_ALIAS_AND_APKTOOL_USAGE_GUIDE.md`](JADX_ALIAS_AND_APKTOOL_USAGE_GUIDE.md) — how to interpret `com.p060tw`/`com.p073tw` aliases vs runtime `com.tw` package identity.
3. [`TOPWAY_NATIVE_COMPATIBILITY_REQUIREMENTS.md`](TOPWAY_NATIVE_COMPATIBILITY_REQUIREMENTS.md) — implementation requirements and safety boundaries for Auxio-TS.
4. [`TOPWAY_SEARCH_TERMS_AND_SOURCE_EXTRACTS.md`](TOPWAY_SEARCH_TERMS_AND_SOURCE_EXTRACTS.md) — strings, class names, and query terms extracted from the decompile for future research.

## Status

The decompile shows **concrete Topway compatibility contracts**. Generic Android MediaSession/AppWidget compatibility is still necessary, but it is no longer sufficient for Auxio-TS compatibility goals.

The safe implementation target is an isolated Auxio-TS Topway bridge that mirrors the observable contract without copying smali, requiring `android.uid.system`, or binding to `com.tw.service.xt` as a production dependency. Dedicated Topway/DoFun release variants may intentionally use stock-compatible public package identities (`com.tw.music` or `com.tw.media`) only through the documented thin wrapper boundary.


## Related DoFun/TS18 APK reference

For the current DoFun Variety launcher/theme target and the stock `twmusic` replacement identity, also read:

- [`../DOFUN_VARIETY_COMPATIBILITY.md`](../DOFUN_VARIETY_COMPATIBILITY.md)
- [`../TS18_APK_REFERENCE.md`](../TS18_APK_REFERENCE.md)
- [`../reference/ts18-apk/`](../reference/ts18-apk/)

The DoFun APK fixes music entries to `com.tw.music/com.tw.music.MusicActivity` and `com.tw.media/com.tw.music.MusicActivity`; these are the package/component compatibility requirements for the dedicated Topway/DoFun release variants.
