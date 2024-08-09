#include <string>
#include "jni.h"
#include <iostream>
#include <thread>
#include <vector>
#include <dlfcn.h>




void loadDynamicLibrary(const std::string& filePath) {
    void* handle = dlopen(filePath.c_str(), RTLD_LAZY);
    if (!handle) {
        throw std::runtime_error(dlerror());
    }

    dlclose(handle);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_libonline_system_DownloadSystem_Loadthelibfile(JNIEnv *env, jobject thiz,
                                                        jstring filepath) {
    const char *nativeString = env->GetStringUTFChars(filepath, 0);
    printf("jni", "Loading the native library from: %s", nativeString);
    loadDynamicLibrary(nativeString);
}