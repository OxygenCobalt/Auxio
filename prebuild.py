#!/usr/bin/env python3

# This script automatically assembles any required ExoPlayer extensions or components as
# an AAR blob. This method is not only faster than depending on ExoPlayer outright as we 
# only need to build our components once, it's also easier to use with Android Studio, which
# tends to get bogged down when we include a massive source repository as part of the gradle
# project. This script may change from time to time depending on the components or extensions
# that I leverage. It's recommended to re-run it after every release to ensure consistent
# behavior.

# As for why I wrote this in Python and not Bash, it's because Bash really does not have
# the capabilities for a nice, seamless pre-build process.

import os
import platform
import sys
import subprocess
import re

# WARNING: THE EXOPLAYER VERSION MUST BE KEPT IN LOCK-STEP WITH THE FLAC EXTENSION AND 
# THE GRADLE DEPENDENCY. IF NOT, VERY UNFRIENDLY BUILD FAILURES AND CRASHES MAY ENSUE.
# EXO_VERSION = "2.18.2"
FLAC_VERSION = "1.3.2"

OK="\033[1;32m" # Bold green
FATAL="\033[1;31m" # Bold red
WARN="\033[1;33m" # Bold yellow
RUN="\033[1;34m" # Bold blue
INFO="\033[1m" # Bold white
NC="\033[0m"

# We do some shell scripting later on, so we can't support windows.
# TODO: Support windows
system = platform.system()
if system not in ["Linux", "Darwin"]:
    print("fatal: unsupported platform " + system)
    sys.exit(1)

def sh(cmd):
    print(RUN + "execute: " + NC + cmd)
    code = subprocess.call(["sh", "-c", "set -e; " + cmd])
    if code != 0:
        print(FATAL + "fatal:" + NC + " command failed with exit code " + str(code))
        sys.exit(1)

start_path = os.path.join(os.path.abspath(os.curdir))
libs_path = os.path.join(start_path, "app", "libs")
if os.path.exists(libs_path):
    reinstall = input(INFO + "info:" + NC + " exoplayer is already installed. " + 
        "would you like to reinstall it? [y/n] ")
    if not re.match("[yY][eE][sS]|[yY]", reinstall):
        sys.exit(0)

exoplayer_path = os.path.join(start_path, "app", "build", "srclibs", "exoplayer")

# Ensure that there is always an SDK environment variable.
# Technically there is also an sdk.dir field in local.properties, but that does
# not work when you clone a project without a local.properties.
if os.getenv("ANDROID_HOME") is None and os.getenv("ANDROID_SDK_ROOT") is None:
    print(FATAL + "fatal:" + NC + " sdk location not found. please define " +
        "ANDROID_HOME/ANDROID_SDK_ROOT before continuing.")
    sys.exit(1)

ndk_path = os.getenv("ANDROID_NDK_HOME")
if ndk_path is None or not os.path.isfile(os.path.join(ndk_path, "ndk-build")):
    # We don't have a proper path. Do some digging on the Android SDK directory
    # to see if we can find it.
    if system == "Linux":
        ndk_root = os.path.join(os.getenv("HOME"), "Android", "Sdk", "ndk")
    elif system == "Darwin":
        ndk_root = os.path.join(os.getenv("HOME"), "Library", "Android", "sdk", "ndk")

    candidates = []
    for entry in os.scandir(ndk_root):
        if entry.is_dir():
            candidates.append(entry.path)

    if len(candidates) > 0:
        print(WARN + "warn:" + NC + " ANDROID_NDK_HOME was not set or invalid. multiple " + 
            "candidates were found however:")
        for i, candidate in enumerate(candidates):
            print("[" + str(i) + "] " + candidate)
        print(INFO + "info:" + NC + " NDK r21e is recommended for this script. Other " +
            "NDKs may result in unexpected behavior.")
        try:
            ndk_path = candidates[int(input("enter the ndk to use [default 0]: "))]
        except ValueError:
            ndk_path = candidates[0]
    else:
        print(FATAL + "fatal:" + NC + " the android ndk was not installed at a " + 
            "recognized location.")
        sys.exit(1)

ndk_build_path = os.path.join(ndk_path, "ndk-build")

# Now try to install ExoPlayer.
sh("rm -rf " + exoplayer_path)
sh("rm -rf " + libs_path)

print(INFO + "info:" + NC + " cloning exoplayer...")
sh("git clone https://github.com/OxygenCobalt/ExoPlayer.git " + exoplayer_path)
os.chdir(exoplayer_path)
sh("git checkout auxio")

print(INFO + "info:" + NC + " assembling flac extension...")
flac_ext_aar_path = os.path.join(exoplayer_path, "extensions", "flac", 
    "buildout", "outputs", "aar", "extension-flac-release.aar")
flac_ext_jni_path = os.path.join("extensions", "flac", "src", "main", "jni")

os.chdir(flac_ext_jni_path)
sh('curl "https://ftp.osuosl.org/pub/xiph/releases/flac/flac-' + FLAC_VERSION + 
    '.tar.xz" | tar xJ && mv "flac-' + FLAC_VERSION + '" flac')
sh(ndk_build_path + " APP_ABI=all -j4")

os.chdir(exoplayer_path)
sh("./gradlew extension-flac:bundleReleaseAar")
 
print(INFO + "info:" + NC + " assembling extractor component...")

extractor_aar_path = os.path.join(exoplayer_path, "library", "extractor", 
    "buildout", "outputs", "aar", "library-extractor-release.aar")

sh("./gradlew library-extractor:bundleReleaseAar")

os.chdir(start_path)
sh("mkdir " + libs_path)
sh("cp " + flac_ext_aar_path + " " + libs_path)
sh("cp " + extractor_aar_path + " " + libs_path)
 
print(OK + "success:" + NC + " completed pre-build")
