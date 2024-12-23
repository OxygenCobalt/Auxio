//
// Created by oxycblt on 12/23/24.
//

#ifndef AUXIO_LOG_H
#define AUXIO_LOG_H

#include <android/log.h>

#define LOG_TAG "taglib_jni"
#define LOGE(...) \
  ((void)__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__))
#define LOGD(...) \
  ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))

#endif //AUXIO_LOG_H
