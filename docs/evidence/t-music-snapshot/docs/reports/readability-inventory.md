# Readability Inventory Baseline (R0)

Generated: 2026-05-07 02:58:39 UTC

## Scope
- Smali roots scanned: 4
- Class files scanned: 2515
- Method definitions scanned: 21184
- Field definitions scanned: 10405

## Baseline metrics
- Heuristic unreadable classes (global): 385
- Heuristic unreadable classes (app-owned/high-value): 288
- Heuristic unreadable classes not yet mapped (app-owned/high-value): 201
- Coverage note: generated/resource holder classes (`R`, `R$*`) are excluded from unreadable ranking metrics.

## Classification model (seeded for R0)
- `confirmed rename candidate`: high-confidence behavior known, non-vendor contract.
- `mapping-only candidate`: readable alias useful but descriptor rename not yet safe.
- `vendor token / external contract`: do not rename token-coupled surfaces.
- `unsafe due to reflection/JNI/serialization/resource/runtime coupling`: defer rename.
- `generated/synthetic/support-library`: low priority/ignore for closure math.
- `unknown`: needs evidence.
- `low-value tail`: documented and intentionally left.

## Top 50 high-impact unreadable app-owned classes (heuristic)
| Rank | Class descriptor | File | Methods | Fields | Interfaces | Suggested initial class | Evidence status | Runtime evidence required | Initial classification |
|---:|---|---|---:|---:|---:|---|---|---|---|
| 1 | `Lcom/eckom/xtlibrary/twproject/video/model/z;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/twproject/video/model/z.smali` | 69 | 51 | 0 | TBD | Static-only | Maybe | unknown |
| 2 | `Lcom/eckom/xtlibrary/twproject/video/model/m;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/twproject/video/model/m.smali` | 69 | 51 | 0 | TBD | Static-only | Maybe | unknown |
| 3 | `Lcom/eckom/xtlibrary/b/k/b/a;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/b/k/b/a.smali` | 29 | 2 | 1 | TBD | Static-only | Maybe | unknown |
| 4 | `Lcom/eckom/xtlibrary/twproject/video/utils/l;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/twproject/video/utils/l.smali` | 14 | 31 | 0 | TBD | Static-only | Maybe | unknown |
| 5 | `Lcom/eckom/xtlibrary/b/k/c/c;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/b/k/c/c.smali` | 21 | 0 | 1 | TBD | Static-only | Maybe | unknown |
| 6 | `Lcom/eckom/xtlibrary/b/k/c/b;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/b/k/c/b.smali` | 21 | 0 | 0 | TBD | Static-only | Maybe | unknown |
| 7 | `Lcom/eckom/xtlibrary/b/j/b;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/b/j/b.smali` | 18 | 0 | 0 | TBD | Static-only | Maybe | unknown |
| 8 | `Lcom/tw/music/a/c;` | `app/apktool/smali_classes3/com/tw/music/a/c.smali` | 14 | 7 | 0 | TBD | Static-only | Maybe | unknown |
| 9 | `Lcom/eckom/xtlibrary/b/b;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/b/b.smali` | 10 | 12 | 0 | TBD | Static-only | Maybe | unknown |
| 10 | `Lcom/tw/music/lrc/a;` | `app/apktool/smali_classes3/com/tw/music/lrc/a.smali` | 11 | 4 | 1 | TBD | Static-only | Maybe | unknown |
| 11 | `Lcom/eckom/xtlibrary/twproject/radio/utils/b;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/twproject/radio/utils/b.smali` | 10 | 5 | 0 | TBD | Static-only | Maybe | unknown |
| 12 | `Lcom/eckom/xtlibrary/b/b/a;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/b/b/a.smali` | 11 | 2 | 0 | TBD | Static-only | Maybe | unknown |
| 13 | `Lcom/eckom/xtlibrary/b/j/r;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/b/j/r.smali` | 8 | 7 | 0 | TBD | Static-only | Maybe | unknown |
| 14 | `Lcom/eckom/xtlibrary/b/j/m;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/b/j/m.smali` | 7 | 9 | 0 | TBD | Static-only | Maybe | unknown |
| 15 | `Lcom/eckom/xtlibrary/b/c/a;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/b/c/a.smali` | 10 | 0 | 0 | TBD | Static-only | Maybe | unknown |
| 16 | `Lcom/eckom/xtlibrary/b/d/b/a;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/b/d/b/a.smali` | 8 | 1 | 1 | TBD | Static-only | Maybe | unknown |
| 17 | `Lcom/eckom/xtlibrary/b/g/a;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/b/g/a.smali` | 8 | 4 | 0 | TBD | Static-only | Maybe | unknown |
| 18 | `Lcom/eckom/xtlibrary/b/b/d;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/b/b/d.smali` | 9 | 2 | 0 | TBD | Static-only | Maybe | unknown |
| 19 | `Lcom/eckom/xtlibrary/b/k/a/b;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/b/k/a/b.smali` | 5 | 9 | 0 | TBD | Static-only | Maybe | unknown |
| 20 | `Lcom/eckom/xtlibrary/b/b/b;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/b/b/b.smali` | 9 | 1 | 0 | TBD | Static-only | Maybe | unknown |
| 21 | `Lcom/eckom/xtlibrary/a/b;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/a/b.smali` | 7 | 4 | 0 | TBD | Static-only | Maybe | unknown |
| 22 | `Lcom/eckom/xtlibrary/b/j/s;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/b/j/s.smali` | 9 | 0 | 0 | TBD | Static-only | Maybe | unknown |
| 23 | `Lcom/eckom/xtlibrary/b/j/o;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/b/j/o.smali` | 8 | 1 | 0 | TBD | Static-only | Maybe | unknown |
| 24 | `Lcom/tw/music/a/c$c;` | `app/apktool/smali_classes3/com/tw/music/a/c$c.smali` | 2 | 12 | 0 | TBD | Static-only | Maybe | unknown |
| 25 | `Lcom/eckom/xtlibrary/b/a/d/f$a;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/b/a/d/f$a.smali` | 7 | 2 | 0 | TBD | Static-only | Maybe | unknown |
| 26 | `Lcom/eckom/xtlibrary/b/c/c;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/b/c/c.smali` | 8 | 0 | 0 | TBD | Static-only | Maybe | unknown |
| 27 | `Lcom/eckom/xtlibrary/twproject/video/utils/a;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/twproject/video/utils/a.smali` | 6 | 3 | 0 | TBD | Static-only | Maybe | unknown |
| 28 | `Lcom/eckom/xtlibrary/b/f/d/ba$a;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/b/f/d/ba$a.smali` | 7 | 1 | 0 | TBD | Static-only | Maybe | unknown |
| 29 | `Lcom/eckom/xtlibrary/b/d/a/a;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/b/d/a/a.smali` | 6 | 3 | 0 | TBD | Static-only | Maybe | unknown |
| 30 | `Lcom/eckom/xtlibrary/b/b/c;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/b/b/c.smali` | 7 | 1 | 0 | TBD | Static-only | Maybe | unknown |
| 31 | `Lcom/eckom/xtlibrary/twproject/activity/c;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/twproject/activity/c.smali` | 5 | 0 | 1 | TBD | Static-only | Maybe | unknown |
| 32 | `Lcom/eckom/xtlibrary/twproject/bt/bean/a;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/twproject/bt/bean/a.smali` | 5 | 0 | 1 | TBD | Static-only | Maybe | unknown |
| 33 | `Lcom/eckom/xtlibrary/b/b/e;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/b/b/e.smali` | 6 | 1 | 0 | TBD | Static-only | Maybe | unknown |
| 34 | `Lcom/tw/music/c;` | `app/apktool/smali_classes3/com/tw/music/c.smali` | 4 | 1 | 1 | TBD | Static-only | Maybe | unknown |
| 35 | `Lcom/tw/music/g;` | `app/apktool/smali_classes3/com/tw/music/g.smali` | 4 | 1 | 1 | TBD | Static-only | Maybe | unknown |
| 36 | `Lcom/tw/music/a/a;` | `app/apktool/smali_classes3/com/tw/music/a/a.smali` | 2 | 5 | 1 | TBD | Static-only | Maybe | unknown |
| 37 | `Lcom/eckom/xtlibrary/twproject/radio/utils/a;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/twproject/radio/utils/a.smali` | 2 | 5 | 1 | TBD | Static-only | Maybe | unknown |
| 38 | `Lcom/eckom/xtlibrary/twproject/video/model/e;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/twproject/video/model/e.smali` | 2 | 5 | 1 | TBD | Static-only | Maybe | unknown |
| 39 | `Lcom/eckom/xtlibrary/twproject/video/model/q;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/twproject/video/model/q.smali` | 2 | 5 | 1 | TBD | Static-only | Maybe | unknown |
| 40 | `Lcom/eckom/xtlibrary/twproject/video/utils/i;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/twproject/video/utils/i.smali` | 4 | 1 | 1 | TBD | Static-only | Maybe | unknown |
| 41 | `Lcom/eckom/xtlibrary/b/j/j;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/b/j/j.smali` | 3 | 3 | 1 | TBD | Static-only | Maybe | unknown |
| 42 | `Lcom/eckom/xtlibrary/b/j/h;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/b/j/h.smali` | 3 | 3 | 1 | TBD | Static-only | Maybe | unknown |
| 43 | `Lcom/eckom/xtlibrary/b/j/i;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/b/j/i.smali` | 3 | 3 | 1 | TBD | Static-only | Maybe | unknown |
| 44 | `Lcom/eckom/xtlibrary/b/j/l;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/b/j/l.smali` | 3 | 3 | 1 | TBD | Static-only | Maybe | unknown |
| 45 | `Lcom/eckom/xtlibrary/b/j/g;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/b/j/g.smali` | 3 | 3 | 1 | TBD | Static-only | Maybe | unknown |
| 46 | `Lcom/eckom/xtlibrary/b/j/k;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/b/j/k.smali` | 3 | 3 | 1 | TBD | Static-only | Maybe | unknown |
| 47 | `Lcom/eckom/xtlibrary/b/c/b;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/b/c/b.smali` | 6 | 0 | 0 | TBD | Static-only | Maybe | unknown |
| 48 | `Lcom/tw/music/lrc/d;` | `app/apktool/smali_classes3/com/tw/music/lrc/d.smali` | 5 | 1 | 0 | TBD | Static-only | Maybe | unknown |
| 49 | `Lcom/tw/music/lrc/g;` | `app/apktool/smali_classes3/com/tw/music/lrc/g.smali` | 5 | 1 | 0 | TBD | Static-only | Maybe | unknown |
| 50 | `Lcom/eckom/xtlibrary/b/j/e;` | `app/apktool/smali_classes3/com/eckom/xtlibrary/b/j/e.smali` | 3 | 2 | 1 | TBD | Static-only | Maybe | unknown |

## Notes
- Heuristic unreadable detection is filename/classname-pattern based and will include some false positives/false negatives.
- R1 should carve out vendor/external-contract and support-library exclusions before any rename proposals.
