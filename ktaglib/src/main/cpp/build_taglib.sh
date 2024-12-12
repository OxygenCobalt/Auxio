set -e
TAGLIB_SRC_DIR=$(pwd)/taglib
TAGLIB_DST_DIR=$(pwd)/taglib/build
TAGLIB_PKG_DIR=$(pwd)/taglib/pkg
NDK_TOOLCHAIN=$(pwd)/android.toolchain.cmake
NDK_PATH=$1
echo "Taglib source is at $TAGLIB_SRC_DIR"
echo "Taglib build is at $TAGLIB_DST_DIR"
echo "Taglib package is at $TAGLIB_PKG_DIR"
echo "NDK toolchain is at $NDK_TOOLCHAIN"
echo "NDK path is at $NDK_PATH"

X86_ARCH=x86
X86_64_ARCH=x86_64
ARMV7_ARCH=armeabi-v7a
ARMV8_ARCH=arm64-v8a

build_for_arch() {
  local ARCH=$1
  local DST_DIR=$TAGLIB_DST_DIR/$ARCH
  local PKG_DIR=$TAGLIB_PKG_DIR/$ARCH

  pushd $TAGLIB_SRC_DIR
  cmake -B $DST_DIR -DANDROID_NDK_PATH=${NDK_PATH} -DCMAKE_TOOLCHAIN_FILE=${NDK_TOOLCHAIN}  \
    -DANDROID_ABI=$ARCH -DBUILD_SHARED_LIBS=OFF -DVISIBILITY_HIDDEN=ON -DBUILD_TESTING=OFF \
    -DBUILD_EXAMPLES=OFF -DBUILD_BINDINGS=OFF -DWITH_ZLIB=OFF -DCMAKE_BUILD_TYPE=Release
  cmake --build $DST_DIR --config Release
  popd

  cmake --install $DST_DIR --config Release --prefix $PKG_DIR --strip
}

build_for_arch $X86_ARCH
build_for_arch $X86_64_ARCH
build_for_arch $ARMV7_ARCH
build_for_arch $ARMV8_ARCH