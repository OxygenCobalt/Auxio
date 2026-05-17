# Resource Compatibility Map (Planning)

## Purpose

This map identifies resource surfaces that future UI work must preserve to avoid regressions in service, widget, theme, or vendor integration paths.

## Manifest-level theme/resource anchors

From `AndroidManifest.xml`:

- Application icon: `@drawable/ic_launcher`
- Application theme: `@style/AppTheme`
- Widget provider metadata: `@xml/appwidget_info`

These are compatibility anchors and should not be changed casually.

## MusicActivity dependency surfaces

`MusicActivity` and adjacent playback UI paths depend on:

- core playback layouts under `res/layout/`
- transport icon drawables used by activity controls and notifications
- string resources projected into activity headers/state text

Any rename/removal requires full call-site audit across smali and reference surfaces.

## MusicWidgetProvider dependency surfaces

`MusicWidgetProvider` depends on:

- widget metadata XML (`res/xml/appwidget_info*`)
- widget layouts (`res/layout/*widget*` patterns where present)
- playback control/action drawables used in widget RemoteViews
- shared strings and IDs consumed by widget update paths

Widget-facing resource IDs/names are treated as protected until verified by static + runtime checks.

## TWTHEME / tw_* compatibility surface

Preserve:

- `@style/AppTheme` inheritance/contract
- `twtheme` usage semantics
- `tw_`-prefixed resources and attributes used by theme switch paths

Do not hardcode theme-bypassing colors/styles without explicit reason and validation.

## Icon/notification/widget asset surfaces

- Launcher icon family (`ic_launcher` and density variants)
- notification transport icons
- widget transport/status icons

Asset refresh is allowed in future PRs, but **resource naming continuity is preferred** unless a full audit is done.

## Drawable density guidance

For TS18 1280×720:

- Ensure at least hdpi/xhdpi quality paths are clean.
- Avoid excessive bitmap sizes that increase decode/memory pressure.
- Validate visual parity across launcher, notification, and widget contexts.

## Protected naming guidance

Do not rename resource names referenced by:

- manifest entries
- widget provider/update logic
- vendor/theme integration paths
- notification/media control construction paths

Any rename must pass exhaustive reference checks first.

## Safe resource rename audit workflow

1. Identify candidate resource and all qualifier variants.
2. Run targeted searches across:
   - `app/apktool/res/`
   - `app/apktool/smali*`
   - `reference/firstparty-jadx/`
   - `reference/vendor-jadx/`
3. Confirm manifest/widget/theme references are updated or intentionally unchanged.
4. Re-run guard scripts and readability validation.
5. Capture runtime/widget evidence before finalizing rename.
