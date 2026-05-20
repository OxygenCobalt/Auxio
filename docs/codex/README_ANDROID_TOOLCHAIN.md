# Android toolchain derivation
Derived from repo files:
- Gradle wrapper: `gradle/wrapper/gradle-wrapper.properties`
- AGP/Kotlin/NDK and SDK targets: `build.gradle`, `app/build.gradle`
- JDK preference: app toolchain + workflows (`.github/workflows/*.yml`)

Repo currently prefers JDK 21, AGP 9.2.1, Kotlin 2.3.10, Gradle 9.4.1, target/compile SDK 36, min SDK 24, NDK 28.2.13676358.

Compatibility references:
- https://developer.android.com/studio/releases/gradle-plugin
- https://docs.gradle.org/current/userguide/compatibility.html
