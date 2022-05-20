---
name: Bug/Crash Report
about: Report an issue with Auxio
title: ''
labels: bug
assignees: ''

---

#### Describe the bug/crash:
<!-- A clear and concise description of what the bug or crash is. -->

#### Expected behavior
<!-- Include behavior of other android music players, if applicable. -->

#### Steps To Reproduce the bug/crash:
<!--
1. Go to X
2. Click on Y
3. Scroll down to Z
4. See error
-->

#### Logs/Stack Traces:
<!-- 
If possible, provide a stack trace or a Logcat. This can help identify the issue. 
To take a logcat, you must do the following:
1. Use a desktop/laptop to download the android platform tools from https://developer.android.com/studio/releases/platform-tools.
2. Extract the downloaded file to a folder.
3. Enable USB debugging on your phone [See https://developer.android.com/studio/command-line/adb#Enabling], and then connect your
phone to a laptop. You will get a prompt to "Allow USB debugging" when you run the logcat command. Accept this.
4. Open up a terminal/command prompt in that folder and run:
	- `./adb -d logcat | grep -i "[DWE] Auxio"` in the case of a bug [may require some changes on windows]
	- `./adb -d logcat AndroidRuntime:E *:S` in the case of a crash
5. Copy and paste the output to this area of the issue.
-->

#### Screenshots:
<!-- If applicable, add screenshots to help explain your problem. -->

#### Phone Information:
<!-- Please provide information about your phone's manufacturer, model, android version, and skin. -->

#### Due Diligence:
- [ ] I have checked this issue for any duplicates.
- [ ] I have checked for this issue in the [FAQ](https://github.com/OxygenCobalt/Auxio/blob/dev/info/FAQ.md).
- [ ] I have read the [Contribution Guidelines](https://github.com/OxygenCobalt/Auxio/blob/dev/.github/CONTRIBUTING.md).