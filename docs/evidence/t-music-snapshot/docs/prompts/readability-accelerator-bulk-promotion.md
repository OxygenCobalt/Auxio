# Codex prompt — accelerated bulk safe class promotion

Continue accelerated readability closure in repo `com.tw.music`.

Run the candidate tooling and promote only strict safe class/interface candidates. Do not edit smali, rename descriptors, rename resources, promote methods/fields, or alter vendor/runtime contract surfaces.

Commands:

```bash
python3 tools/readability/01_inventory_symbols.py
python3 tools/readability/02_generate_mapping_candidates.py
python3 tools/readability/03_promote_safe_class_mappings.py --dry-run --limit 150
python3 tools/readability/03_promote_safe_class_mappings.py --apply-safe-class-mappings --limit 80
python3 tools/readability/01_inventory_symbols.py
python3 tools/readability/02_generate_mapping_candidates.py
python3 tools/readability/05_tail_classification.py
python3 tools/readability/04_high_impact_method_candidates.py
bash scripts/08_verify_vendor_tokens.sh
bash tools/readability/06_diff_size_guard.sh
git status --short
git diff --stat
git diff --numstat
```

Keep reports compact. Do not commit APKs, binaries, raw JADX output, archives, or oversized dumps.

Update:
- `docs/readability-closure-plan-2026-05.md`
- `docs/deobf/enigma-notes.md`
- `mappings/manual-enigma/music-core.mapping`
- `mappings/manual-enigma/mapping-evidence.md`
- compact reports under `docs/reports/`

Commit the slice if checks pass.

Final response must be a triple-tilde unbroken markdown codeblock including:
1. candidate counts by bucket;
2. mappings promoted;
3. tail classification summary;
4. commands run and outcomes;
5. diff-size guard result;
6. confirmation no large artifacts were committed;
7. next recommended accelerated pass.
