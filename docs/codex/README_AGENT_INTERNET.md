# Agent internet guidance
If setup warmed caches and SDK fully, agent internet is often unnecessary.
Limited internet can help with missing dependencies or branch/toolchain drift.
Use restrictive allowlists and read methods (GET/HEAD/OPTIONS) where practical.
Likely setup domains: github.com, raw.githubusercontent.com, dl.google.com, services.gradle.org, plugins.gradle.org, repo.maven.apache.org, maven.google.com, repo1.maven.org, git.ffmpeg.org.
Secrets are setup-only and unavailable in agent phase.
