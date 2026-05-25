# Auxio-TS PR #34 Topway Second-Pass Overlay

This overlay updates the Topway support material for PR #34 after reviewing:

- live PR #34 metadata/diff at head `30cbf6f613593093d747c4913c52042a49cd2b93`;
- the attached latest Codex task export;
- `topway-apktool.zip`;
- `topway-apktool0.zip`;
- `topway-music-smali-main.zip` where available.

## What this overlay is for

It is **not** runtime code.

It adds repo support material to help Codex/Copilot finish PR #34 without losing focus:

- PR #34 review and closure plan;
- Topway widget/receiver lifecycle specification;
- runtime bridge test matrix;
- machine-readable contract inventory;
- agent closure rules;
- a ready-to-use Codex prompt for the next pass.

## Apply on PR #34 head branch

```sh
cd /path/to/Auxio-TS
git switch cx/align-auxio-ts-docs-with-topway-compatibility

unzip /path/to/auxio_ts_pr34_topway_second_pass_overlay.zip -d .

git add \
  README_PR34_TOPWAY_SECOND_PASS_OVERLAY.md \
  docs/topway/TOPWAY_PR34_SECOND_PASS_REVIEW.md \
  docs/topway/TOPWAY_WIDGET_AND_RECEIVER_CLOSURE_SPEC.md \
  docs/topway/TOPWAY_BRIDGE_RUNTIME_TEST_MATRIX.md \
  docs/topway/reference/topway_pr34_second_pass_contract_inventory.json \
  docs/agent-instructions/PR34_TOPWAY_BRIDGE_CLOSURE_AGENT_RULES.md \
  docs/prompts/CODEX_PR34_TOPWAY_BRIDGE_CLOSURE_PASS.md \
  patches/ADD_TO_AGENT_INSTRUCTIONS_PR34_TOPWAY_CLOSURE.md

git commit -m "docs(ts18): add PR34 Topway bridge closure requirements"
```

## Then prompt Codex

Use:

```text
docs/prompts/CODEX_PR34_TOPWAY_BRIDGE_CLOSURE_PASS.md
```

## Important branch note

PR #34 currently targets:

```text
base: cx/prepare-auxio-ts-runtime-apk-for-ts18-release
head: cx/align-auxio-ts-docs-with-topway-compatibility
```

Commit this overlay to the **head** branch if you want it inside PR #34.
