//
// Created by oxycblt on 12/12/24.
//

#ifndef AUXIO_JVMTAGMAP_H
#define AUXIO_JVMTAGMAP_H

#include <jni.h>
#include <string_view>
#include <taglib/tstring.h>
#include <taglib/tstringlist.h>

class JVMTagMap {
public:
    JVMTagMap(JNIEnv *env);
    ~JVMTagMap();

    JVMTagMap(const JVMTagMap &) = delete;
    JVMTagMap &operator=(const JVMTagMap &) = delete;

    void add(TagLib::String &key, TagLib::String &value);
    void add(TagLib::String &key, TagLib::StringList &value);

    jobject getObject();

private:
    JNIEnv *env;
    jobject hashMap;
    jmethodID hashMapGetMethod;
    jmethodID hashMapPutMethod;
    jmethodID arrayListInitMethod;
    jmethodID arrayListAddMethod;
};


#endif //AUXIO_JVMTAGMAP_H
