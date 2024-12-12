#include <jni.h>
#include <string>

#include "taglib/tag.h"

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_ktaglib_NativeLib_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}