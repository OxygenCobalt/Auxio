# Submodules
Auxio-TS depends on recursive submodules (`media`, nested ffmpeg, and `musikr` taglib).
ZIP snapshots are insufficient because Gradle requires `media/core_settings.gradle` and native trees.

Canonical step before Gradle: `bash ./scripts/prepare-ci-environment.sh`.

`--remote` is not default to avoid silently changing pinned revisions; opt-in only with `CODEX_UPDATE_SUBMODULE_REMOTES=1`.
