#!/usr/bin/env python3
import os, re, glob, datetime

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..'))
smali_roots = sorted(glob.glob(os.path.join(ROOT, 'app/apktool/smali*')))

class_rows = []
method_count = 0
field_count = 0

# Existing manual class mappings (to avoid re-queuing already-resolved symbols)
mapped_classes = set()
map_file = os.path.join(ROOT, 'mappings/manual-enigma/music-core.mapping')
if os.path.exists(map_file):
    with open(map_file, 'r', encoding='utf-8', errors='ignore') as mf:
        for line in mf:
            line = line.strip()
            if line.startswith('CLASS '):
                parts = line.split()
                if len(parts) >= 3:
                    mapped_classes.add('L' + parts[1] + ';')


obf_re = [
    re.compile(r'^[a-zA-Z]$'),
    re.compile(r'^[a-z]{2}$'),
    re.compile(r'^[A-Z][a-z]?$'),
    re.compile(r'^[A-Za-z]{1,2}\$[A-Za-z0-9_]+$'),
    re.compile(r'^C\d+[A-Za-z]?$'),
    re.compile(r'^(Runnable|Abstract|Interface)C\d+[A-Za-z]?$'),
]

support_prefixes = (
    'Landroid/support/', 'Landroidx/', 'Ljava/', 'Ljavax/', 'Lkotlin/',
    'Lorg/', 'Ltv/danmaku/', 'Lcpdetector/', 'Lantlr/'
)

for root in smali_roots:
    for dp, _, files in os.walk(root):
        for fn in files:
            if not fn.endswith('.smali'):
                continue
            path = os.path.join(dp, fn)
            rel = os.path.relpath(path, ROOT)
            with open(path, 'r', encoding='utf-8', errors='ignore') as f:
                lines = f.readlines()

            cls = None
            impl = methods = fields = 0
            for ln in lines:
                s = ln.strip()
                if s.startswith('.class '):
                    m = re.search(r'(L[^;]+;)', s)
                    if m:
                        cls = m.group(1)
                elif s.startswith('.implements '):
                    impl += 1
                elif s.startswith('.method '):
                    methods += 1
                elif s.startswith('.field '):
                    fields += 1

            if not cls:
                continue

            base = cls[1:-1].split('/')[-1]
            # Ignore generated resource holder classes from unreadable ranking/classification
            if base == 'R' or base.startswith('R$'):
                continue
            obf = any(r.match(base) for r in obf_re)
            support = cls.startswith(support_prefixes)
            if cls.startswith('Lcom/tw/music/') or cls.startswith('Lcom/eckom/xtlibrary/'):
                ownership = 'app-owned/high-value'
            elif support:
                ownership = 'support-or-third-party'
            else:
                ownership = 'other'

            class_rows.append((cls, rel, base, obf, ownership, methods, fields, impl))
            method_count += methods
            field_count += fields

class_count = len(class_rows)
obf_count = sum(1 for r in class_rows if r[3])
app_rows = [r for r in class_rows if r[4] == 'app-owned/high-value']
obf_app = [r for r in app_rows if r[3]]
obf_unmapped_app = [r for r in obf_app if r[0] not in mapped_classes]

def score(r):
    return r[5] * 2 + r[6] + r[7] * 3

top = sorted(obf_unmapped_app, key=score, reverse=True)[:50]

report = os.path.join(ROOT, 'docs/reports/readability-inventory.md')
with open(report, 'w', encoding='utf-8') as o:
    o.write('# Readability Inventory Baseline (R0)\n\n')
    o.write(f'Generated: {datetime.datetime.utcnow().strftime("%Y-%m-%d %H:%M:%S")} UTC\n\n')
    o.write('## Scope\n')
    o.write(f'- Smali roots scanned: {len(smali_roots)}\n')
    o.write(f'- Class files scanned: {class_count}\n')
    o.write(f'- Method definitions scanned: {method_count}\n')
    o.write(f'- Field definitions scanned: {field_count}\n\n')

    o.write('## Baseline metrics\n')
    o.write(f'- Heuristic unreadable classes (global): {obf_count}\n')
    o.write(f'- Heuristic unreadable classes (app-owned/high-value): {len(obf_app)}\n')
    o.write(f'- Heuristic unreadable classes not yet mapped (app-owned/high-value): {len(obf_unmapped_app)}\n')
    o.write('- Coverage note: generated/resource holder classes (`R`, `R$*`) are excluded from unreadable ranking metrics.\n\n')

    o.write('## Classification model (seeded for R0)\n')
    o.write('- `confirmed rename candidate`: high-confidence behavior known, non-vendor contract.\n')
    o.write('- `mapping-only candidate`: readable alias useful but descriptor rename not yet safe.\n')
    o.write('- `vendor token / external contract`: do not rename token-coupled surfaces.\n')
    o.write('- `unsafe due to reflection/JNI/serialization/resource/runtime coupling`: defer rename.\n')
    o.write('- `generated/synthetic/support-library`: low priority/ignore for closure math.\n')
    o.write('- `unknown`: needs evidence.\n')
    o.write('- `low-value tail`: documented and intentionally left.\n\n')

    o.write('## Top 50 high-impact unreadable app-owned classes (heuristic)\n')
    o.write('| Rank | Class descriptor | File | Methods | Fields | Interfaces | Suggested initial class | Evidence status | Runtime evidence required | Initial classification |\n')
    o.write('|---:|---|---|---:|---:|---:|---|---|---|---|\n')
    for i, r in enumerate(top, 1):
        o.write(f'| {i} | `{r[0]}` | `{r[1]}` | {r[5]} | {r[6]} | {r[7]} | TBD | Static-only | Maybe | unknown |\n')

    o.write('\n## Notes\n')
    o.write('- Heuristic unreadable detection is filename/classname-pattern based and will include some false positives/false negatives.\n')
    o.write('- R1 should carve out vendor/external-contract and support-library exclusions before any rename proposals.\n')

print(report)
