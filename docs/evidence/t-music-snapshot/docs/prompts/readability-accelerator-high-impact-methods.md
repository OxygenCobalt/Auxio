# Codex prompt — high-impact method/interface review

Continue accelerated readability closure in repo `com.tw.music`.

Use `docs/reports/readability-high-impact-methods.md` as a review queue. Promote only high-confidence method/interface/callback labels with strong evidence. Do not run a broad method rename sweep.

Allowed targets:
- obvious getters/setters;
- Android/framework override methods;
- presenter/model/view interface boundary methods;
- callback methods with strongly typed arguments and clear call paths;
- `run`, `handleMessage`, `doInBackground`, `onPostExecute`, `compare`, `accept`, `onClick` when behaviour is explicit.

Do not edit smali descriptors. Prefer mapping/docs only unless separately justified.

Run:
```bash
python3 tools/readability/04_high_impact_method_candidates.py
bash scripts/08_verify_vendor_tokens.sh
bash tools/readability/06_diff_size_guard.sh
git status --short
git diff --stat
git diff --numstat
```

Final response must be a triple-tilde unbroken markdown codeblock.
