# Add to AGENTS.md / .github/copilot-instructions.md / docs/CODEX_TASK_PROMPTS.md

## PR #34 Topway bridge closure rule

When working on PR #34, agents must treat the isolated Topway bridge as an incomplete runtime feature until all closure areas are addressed:

- receiver lifecycle/cold-state strategy;
- widget RemoteViews parity with title/artist/current time/duration/progress/artwork/controls;
- widget stale/no-session clearing;
- event-driven widget update parity;
- metadata/progress broadcast lifecycle;
- MediaSession/notification/widget/Topway metadata unification;
- runtime edge-case tests;
- guardrail and manifest safety checks;
- concise docs status sync after implementation.

Do not count constants, mappers, docs, or factory tests as completion by themselves.

Topway widget progress has two relevant units:

- stock widget RemoteViews progress bar uses seconds;
- `com.tw.launcher.music_progress_duration` broadcast extras use milliseconds.

Keep those separate.

Topway strings remain allowed only inside the isolated bridge/test/docs paths, plus AndroidManifest receiver filters if an explicit bridge receiver is intentionally implemented. They remain forbidden elsewhere.
