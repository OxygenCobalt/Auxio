# Auxio contribution guidelines

## Crashes & Bugs
Log them in the [Issues](https://github.com/OxygenCobalt/Auxio/issues) tab.

Please keep in mind when reporting an issue:
- **Has it been reported?** Make sure an issue for the issue is not already there.
- **Has it been already fixed?** Make sure a fix wasn't already added.
- **Is it still relevant in the latest version?** Make sure to test it in the latest version.

If you do make an issue, Make sure to provide:
- A description of the bug/crash
- A summary of the steps to create the bug/crash
- A stack trace/logcat if possible, the longer the better.

If you have knowledge of Android/Kotlin in general, you could also go about fixing the bug yourself and opening a [Pull Request](https://github.com/OxygenCobalt/Auxio/pulls).

## Feature Requests
These should also be logged in the [Issues](https://github.com/OxygenCobalt/Auxio/issues) tab.

Please keep in mind when requesting a feature:
- **Has it already been requested?** Make sure request for this feature is not already here.
- **Has it been already added?** Make sure this feature has not already been added in the most recent release.
- **Will it be accepted?** Read the [Why Are These Features Missing?](https://github.com/OxygenCobalt/Auxio/wiki/Why-Are-These-Features-Missing%3F) in order to see the likelihood that your request will be implemented.

If you do make a request, provide the following:
- What is it that you want?
- Is it related to some problem? If so, describe why.
- Why do you think it will benefit everyone's usage of the app?

If you have the knowledge, you can also implement the feature yourself and create a [Pull Request](https://github.com/OxygenCobalt/Auxio/pulls), but its recommended that **you create an issue beforehand to give me a heads up.**
Its also recommended that you read about [Auxio's Architecture](https://github.com/OxygenCobalt/Auxio/wiki/Architecture) as well to make changes better and more efficient.

## Translations
Go to Auxio's weblate project [here](https://hosted.weblate.org/engage/auxio/).

## Code Contributions
If you have knowledge of Android/Kotlin, feel free to to contribute to the project.
- If you want to help out with an existing bug report, comment on the issue that you want to fix saying that you are going to try your hand at it.
- If you want to add something, its recommended to open up an issue for what you want to change before you start working on it. That way I can determine if the addition will be merged in the first place, and generally gives a heads-up overall.
- Do not bring non-free software into the project, such as Binary Blobs.
- Stick to [F-Droid Including Guidelines](https://f-droid.org/wiki/page/Inclusion_Policy)
- Make sure you stick to Auxio's styling with [ktlint](https://github.com/pinterest/ktlint). `ktlintformat` should run on every build.
- Please ***FULLY TEST*** your changes before creating a PR. Untested code will not be merged.
- Java code will **NOT** be accepted. Kotlin only.
- Keep your code up the date with the upstream and continue to maintain it after you create the PR. This makes it less of a hassle to merge.
