# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html.
# For more examples on how to use CMake, see https://github.com/android/ndk-samples.

# Sets the minimum CMake version required for this project.
cmake_minimum_required(VERSION 3.22.1)

# Declares the project name. The project name can be accessed via ${ PROJECT_NAME},
# Since this is the top level CMakeLists.txt, the project name is also accessible
# with ${CMAKE_PROJECT_NAME} (both CMake variables are in-sync within the top level
# build script scope).
project("tagJNI") # becomes "libtagJNI.so"

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.
#
# In this top level CMakeLists.txt, ${CMAKE_PROJECT_NAME} is used to define
# the target library name; in the sub-module's CMakeLists.txt, ${PROJECT_NAME}
# is preferred for the same purpose.
#
# In order to load a library into your app from Java/Kotlin, you must call
# System.loadLibrary() and pass the name of the library defined here;
# for GameActivity/NativeActivity derived applications, the same library name must be
# used in the AndroidManifest.xml file.
set(taglib_location "${CMAKE_CURRENT_SOURCE_DIR}/taglib")
set(taglib_pkg "${taglib_location}/pkg/${ANDROID_ABI}")
set(taglib_lib "${taglib_pkg}/lib")
set(taglib_include "${taglib_pkg}/include")

set(taglib_file_name libtag.a)
set(taglib_file_path ${taglib_lib}/${taglib_file_name})
set(taglib_lib_name, "taglib")
add_library(
        "taglib"
        STATIC
        IMPORTED)
set_target_properties(
        "taglib" PROPERTIES
        IMPORTED_LOCATION
        ${taglib_file_path}
        INTERFACE_INCLUDE_DIRECTORIES
        ${taglib_include})

add_library(${CMAKE_PROJECT_NAME} SHARED
        # List C/C++ source files with relative paths to this CMakeLists.txt.
        taglib_jni.cpp
        JVMInputStream.cpp
        JVMTagMap.cpp
        JVMMetadataBuilder.cpp
)
set_target_properties(${CMAKE_PROJECT_NAME} PROPERTIES
        # @Tolriq found that these flags can reduce the size of the linked
        # taglib + jni shim shared library. Kudos to them.
        # https://github.com/taglib/taglib/issues/1212#issuecomment-2326456903
        LINK_FLAGS
        "-Wl,--exclude-libs,ALL")

# Specifies libraries CMake should link to your target library. You
# can link libraries from various origins, such as libraries defined in this
# build script, prebuilt third-party libraries, or Android system libraries.
target_link_libraries(${CMAKE_PROJECT_NAME}
        # List libraries link to the target library
        PRIVATE android
        PRIVATE log
        PRIVATE taglib)