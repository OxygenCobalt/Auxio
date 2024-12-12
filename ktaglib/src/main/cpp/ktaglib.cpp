#include <jni.h>
#include <string>

#include "taglib/tag.h"

extern "C" JNIEXPORT jstring JNICALL
Java_org_oxycblt_ktaglib_KTagLib_load(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}