# Where to start

1. `app/apktool/AndroidManifest.xml` — all components declared
2. `docs/maps/activities.txt` / `services.txt` / `receivers.txt`
3. `docs/reports/vendor-hooks.txt` — vendor SDK boundary points
4. `docs/reports/jadx-problems.txt` — JADX decompiler failures to work around
5. `reference/jadx-aliased/` — most readable Java view
6. `reference/jadx-raw/` — raw JADX output if aliased is misleading
7. `app/apktool/res/layout/` — UI layouts

## Common layout files to check first
- `res/layout/music_activity.xml`
- `res/layout/music_act_player.xml`
- `res/layout/music_act_list.xml`
- `res/layout/layout_music_split_screen.xml`
- `res/layout/layout_presentation.xml`
- `res/layout/layout_settings.xml`
