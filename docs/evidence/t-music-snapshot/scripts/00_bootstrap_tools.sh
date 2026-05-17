#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
APKTOOL_VER="${APKTOOL_VER:-3.0.2}"
JADX_VER="${JADX_VER:-1.5.1}"
mkdir -p "$ROOT/.tools"
if [ ! -f "$ROOT/.tools/apktool.jar" ]; then
  curl -L --fail --connect-timeout 30     -o "$ROOT/.tools/apktool.jar"     "https://github.com/iBotPeaches/Apktool/releases/download/v${APKTOOL_VER}/apktool_${APKTOOL_VER}.jar"
fi
if [ ! -x "$ROOT/.tools/jadx/bin/jadx" ]; then
  curl -L --fail --connect-timeout 30     -o "$ROOT/.tools/jadx.zip"     "https://github.com/skylot/jadx/releases/download/v${JADX_VER}/jadx-${JADX_VER}.zip"
  rm -rf "$ROOT/.tools/jadx"
  unzip -q -o "$ROOT/.tools/jadx.zip" -d "$ROOT/.tools/jadx"
  chmod +x "$ROOT/.tools/jadx/bin/jadx"
  rm -f "$ROOT/.tools/jadx.zip"
fi
echo "Tools ready."
