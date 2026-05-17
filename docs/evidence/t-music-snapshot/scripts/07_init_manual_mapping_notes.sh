#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
mkdir -p "$ROOT/mappings/manual-enigma" "$ROOT/docs/deobf"
cat > "$ROOT/docs/deobf/enigma-notes.md" <<'NOTES'
# Enigma mapping notes

Place reviewed semantic renames in `mappings/manual-enigma/` using Enigma format.
Get exact raw names and signatures from `app/apktool/smali*/*.smali`.

## Minimal example

```
CLASS com/tw/music/MusicActivity -
    METHOD a onPlaybackButtonClick (Landroid/view/View;)V
    FIELD p btnPlay Landroid/widget/ImageView;
```

Only add entries for classes/methods/fields you actually understand.
NOTES
echo "[OK] Enigma notes initialised."
