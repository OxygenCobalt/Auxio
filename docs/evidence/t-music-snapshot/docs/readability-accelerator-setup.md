# Readability accelerator setup

This repo uses an accelerated readability closure workflow:

1. Generate compact candidates.
2. Promote strict safe class/interface mappings in bulk.
3. Review high-impact method/interface boundaries.
4. Classify the remaining tail.
5. Run vendor-token and diff-size guards.

## Codex Cloud setup

Set setup command:

```bash
INSTALL_PINNED_JADX=1 bash scripts/codex/setup_readability_env.sh
```

Set maintenance command:

```bash
bash scripts/codex/maintain_readability_env.sh
```

Keep agent internet off for normal tasks. The setup script installs pinned JADX 1.5.5 only if `INSTALL_PINNED_JADX=1`.

## Local / Termux setup

Optional baseline packages:

```bash
INSTALL_OPTIONAL_PACKAGES=1 ./local/bootstrap_readability_accelerator.sh
```

Optional pinned JADX install:

```bash
INSTALL_PINNED_JADX=1 ./local/bootstrap_readability_accelerator.sh
```

## Bulk safe class promotion

```bash
python3 tools/readability/01_inventory_symbols.py
python3 tools/readability/02_generate_mapping_candidates.py
python3 tools/readability/03_promote_safe_class_mappings.py --dry-run --limit 150
python3 tools/readability/03_promote_safe_class_mappings.py --apply-safe-class-mappings --limit 80
python3 tools/readability/01_inventory_symbols.py
python3 tools/readability/02_generate_mapping_candidates.py
python3 tools/readability/05_tail_classification.py
bash scripts/08_verify_vendor_tokens.sh
bash tools/readability/06_diff_size_guard.sh
```

## Guardrails

- No smali descriptor rename by default.
- No broad method/field rename sweep.
- No vendor/runtime surface mutation.
- No raw JADX exports, APKs, DEX files, archives, or oversized reports committed.
