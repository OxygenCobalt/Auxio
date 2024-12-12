#include <jni.h>
#include <string>

#include "JVMInputStream.h"
#include <taglib/fileref.h>
#include <taglib/tag.h>

extern "C" JNIEXPORT jobject JNICALL
Java_org_oxycblt_ktaglib_KTagLib_openNative(
        JNIEnv* env,
        jobject /* this */,
        jobject inputStream) {
    JVMInputStream stream { env, inputStream };
    TagLib::FileRef file { &stream };
    if (file.isNull()) {
        return nullptr;
    }
    TagLib::Tag* tag = file.tag();
    if (tag == nullptr) {
        return nullptr;
    }
    jclass mapClass = env->FindClass("java/util/HashMap");
    jmethodID init = env->GetMethodID(mapClass, "<init>", "()V");
    jobject id3v2 = env->NewObject(mapClass, init);
    jobject vorbis = env->NewObject(mapClass, init);
    jbyteArray coverData = env->NewByteArray(0);

    jclass tagClass = env->FindClass("org/oxycblt/ktaglib/Tag");
    jmethodID tagInit = env->GetMethodID(tagClass, "<init>", "(Ljava/util/Map;Ljava/util/Map;[B)V");

    jobject tagObj = env->NewObject(tagClass, tagInit, id3v2, vorbis, coverData);

    return tagObj;
}