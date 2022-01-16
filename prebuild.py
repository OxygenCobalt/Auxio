#!/usr/bin/env python3
# This script automatically installs exoplayer with the necessary components.
# This is written in version-agnostic python3, because I'd rather
# not have to deal with the insanity of bash.
import os
import platform
import sys
import subprocess
import re

FLAC_VERSION = "1.3.2"

FATAL="\033[1;31m"
WARN="\033[1;91m"
INFO="\033[1;94m"
OK="\033[1;92m"
NC="\033[0m"

system = platform.system()

# We do some shell scripting later on, so we can't support windows.
if system not in ["Linux", "Darwin"]:
    print("fatal: unsupported platform " + system)
    sys.exit(1)

def sh(cmd):
    code = subprocess.call(["sh", "-c", "set -e; " + cmd])

    if code != 0:
        print(FATAL + "fatal:" + NC + " command failed with exit code " + str(code))
        sys.exit(1)
        
exoplayer_path = os.path.join(os.path.abspath(os.curdir), "deps", "exoplayer")

if os.path.exists(exoplayer_path):
    reinstall = input(INFO + "info:" + NC + " exoplayer is already installed. would you like to reinstall it? [y/n] ")

    if not re.match("[yY][eE][sS]|[yY]", reinstall):
        sys.exit(0)

ndk_path = os.getenv("NDK_PATH")

if ndk_path is None or not os.path.isfile(os.path.join(ndk_path, "ndk_build")):
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
        print(WARN + "warn:" + NC + " NDK_PATH was not set or invalid. multiple candidates were found however:")

        for i, candidate in enumerate(candidates):
            print("[" + str(i) + "] " + candidate)

        try:
            ndk_path = candidates[int(input("Enter the ndk to use [Default 0]: "))]
        except:
            ndk_path = candidates[0]
    else:
        print(FATAL + "fatal:" + NC + " NDK_PATH is either invalid, or the android ndk was not installed at a recognized location.")
        system.exit(1)

# Now try to install ExoPlayer.
sh("rm -rf deps")

print(INFO + "info:" + NC + " cloning ExoPlayer...")
sh("git clone https://github.com/oxygencobalt/ExoPlayer.git " + exoplayer_path)
os.chdir(exoplayer_path)
sh("git checkout auxio")

flac_ext_jni_path = os.path.join("extensions", "flac", "src", "main", "jni")
print(INFO + "info:" + NC + " installing FLAC extension...")
os.chdir(flac_ext_jni_path)
sh('curl "https://ftp.osuosl.org/pub/xiph/releases/flac/flac-' + FLAC_VERSION + '.tar.xz" | tar xJ && mv "flac-' + FLAC_VERSION + '" flac')
sh(ndk_path + "/ndk-build APP_ABI=all -j4")

print(OK + "success:" + NC + " completed pre-build.")
