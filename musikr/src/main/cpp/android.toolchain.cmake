# Define the minimum CMake version and project name
cmake_minimum_required(VERSION 3.22.1)

# Set the Android NDK path
option(ANDROID_NDK_PATH "Path to Android NDK Install. Should be same version specified in gradle." REQUIRED)

# Specify the target Android API level
set(ANDROID_PLATFORM android-24)

# Define the toolchain
set(CMAKE_SYSTEM_NAME Android)
set(CMAKE_ANDROID_ARCH_ABI ${ANDROID_ABI})
set(CMAKE_ANDROID_NDK ${ANDROID_NDK_PATH})
set(CMAKE_ANDROID_STL_TYPE c++_static)
set(CMAKE_ANDROID_NDK_TOOLCHAIN_VERSION clang)