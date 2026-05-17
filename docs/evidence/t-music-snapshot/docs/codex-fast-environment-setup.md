# Codex fast environment setup

Use this default setup for PR18 readability work:

```bash
bash scripts/codex/setup_readability_env.sh
```

Default mode intentionally does **not** run apt, install Java, install Android SDK, or install JADX.

For full APK tooling only:

```bash
INSTALL_SYSTEM_PACKAGES=1 INSTALL_JAVA=1 INSTALL_ANDROID_TOOLS=1 INSTALL_PINNED_JADX=1 \
  bash scripts/codex/setup_readability_env.sh
```

For verification only:

```bash
VERIFY_ONLY=1 bash scripts/codex/setup_readability_env.sh
bash scripts/codex/maintain_readability_env.sh
```

Valid GitHub CLI forms:

```bash
gh pr create --repo cbkii/t-music --base BASE --head HEAD --title "..." --body "..."
gh pr view PR_NUMBER_OR_BRANCH --repo cbkii/t-music --json number,url
gh pr merge PR_NUMBER_OR_BRANCH --repo cbkii/t-music --squash --delete-branch
gh pr list --repo cbkii/t-music --head HEAD --base BASE --json number,url
```

Invalid forms to avoid:

```bash
gh pr view --head ...
gh pr merge --head ...
gh pr create --json ...
```
