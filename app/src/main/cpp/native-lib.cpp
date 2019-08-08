#include <jni.h>
#include <string>
#include "render/render_loop.h"
#include "android/native_window_jni.h"
#include <android/asset_manager_jni.h>
#include <pthread.h>

render_loop *renderLoop = nullptr;
ANativeWindow *nativeWindow = nullptr;
jobject android_java_asset_manager = nullptr;
jobject android_java_surface_texture = nullptr;

static JavaVM *g_vm;

extern "C" JNIEXPORT void JNICALL
Java_com_example_templechen_videoshaderdemo_cpp_NativeSurfaceView_nativeInit(
        JNIEnv *env,
        jobject instance
) {
    renderLoop = new render_loop();
    renderLoop->vm = g_vm;
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_templechen_videoshaderdemo_cpp_NativeSurfaceView_nativeSurfaceCreated(
        JNIEnv *env,
        jobject instance,
        jobject surface,
        jint textureId
) {
    if (renderLoop != nullptr) {
        nativeWindow = ANativeWindow_fromSurface(env, surface);
        renderLoop->postMessage(renderLoop->kMsgSurfaceCreated, textureId, 0, nativeWindow);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_templechen_videoshaderdemo_cpp_NativeSurfaceView_nativeSurfaceChanged(
        JNIEnv *env,
        jobject instance,
        jobject surface,
        jint format,
        jint width,
        jint height
) {
    if (renderLoop != nullptr) {
        renderLoop->postMessage(renderLoop->kMsgSurfaceChanged, width, height);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_templechen_videoshaderdemo_cpp_NativeSurfaceView_nativeSurfaceDestroyed(
        JNIEnv *env,
        jobject instance,
        jobject surface
) {
    if (renderLoop != nullptr) {
        renderLoop->postMessage(renderLoop->kMsgSurfaceDestroyed);
    }
    if (renderLoop != nullptr) {
        renderLoop->quit();
        delete renderLoop;
        renderLoop = nullptr;
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_templechen_videoshaderdemo_cpp_NativeSurfaceView_nativeDoFrame(
        JNIEnv *env,
        jobject instance,
        jlong frameTimeNanos
) {
    if (renderLoop != nullptr) {
        renderLoop->postMessage(renderLoop->kMsgSurfaceDoFrame, reinterpret_cast<void *>(frameTimeNanos));
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_templechen_videoshaderdemo_cpp_NativeSurfaceView_nativeDestroyed(
        JNIEnv *env,
        jobject instance
) {
    nativeWindow = nullptr;
    if (android_java_asset_manager != nullptr) {
        env->DeleteGlobalRef(android_java_asset_manager);
        android_java_asset_manager = nullptr;
    }
    if (android_java_surface_texture != nullptr) {
        env->DeleteGlobalRef(android_java_surface_texture);
        android_java_surface_texture = nullptr;
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_templechen_videoshaderdemo_cpp_NativeSurfaceView_nativeSetSurfaceTexture(
        JNIEnv *env,
        jobject instance,
        jobject surfaceTexture
) {
    if (renderLoop != nullptr) {
        android_java_surface_texture = env->NewGlobalRef(surfaceTexture);
        renderLoop->postMessage(renderLoop->kMsgJavaSurfaceTexture, android_java_surface_texture);
//        renderLoop->postMessage(renderLoop->kMsgSurfaceTexture,
//                                ASurfaceTexture_fromSurfaceTexture(env, surfaceTexture));
    }
}

extern "C" JNIEXPORT jint JNI_OnLoad(
        JavaVM *vm, void *reserved
) {
    g_vm = vm;
    return JNI_VERSION_1_6;
}

void JNI_OnUnload(JavaVM *vm, void *reserved) {
    g_vm = nullptr;
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_templechen_videoshaderdemo_cpp_NativeSurfaceView_nativeSetVideoSize(
        JNIEnv *env,
        jobject instance,
        jint videoWidth,
        jint videoHeight
) {
    if (renderLoop != nullptr) {
        renderLoop->postMessage(renderLoop->kMsgVideoSizeChanged, videoWidth, videoHeight);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_templechen_videoshaderdemo_cpp_NativeSurfaceView_nativeSetBackgroundColor(
        JNIEnv *env,
        jobject instance,
        jfloatArray color
) {
    if (renderLoop != nullptr) {
        float *values = env->GetFloatArrayElements(color, nullptr);
        renderLoop->postMessage(renderLoop->kMsgBackgroundColorChanged, values);
        env->ReleaseFloatArrayElements(color, values, 0);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_templechen_videoshaderdemo_cpp_NativeSurfaceView_nativeSetScroll(
        JNIEnv *env,
        jobject instance,
        jfloatArray scrolls
) {
    if (renderLoop != nullptr) {
        float *values = env->GetFloatArrayElements(scrolls, nullptr);
        //貌似jfloatArray->float * 精度有问题， 很多时候是0...
        renderLoop->postMessage(renderLoop->kMsgScrolled, static_cast<int>(values[0] * 1000000),
                                static_cast<int>(values[1] * 1000000));
        env->ReleaseFloatArrayElements(scrolls, values, 0);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_templechen_videoshaderdemo_cpp_NativeSurfaceView_nativeSetScale(
        JNIEnv *env,
        jobject instance,
        jfloat scaleX,
        jfloat scaleY
) {
    if (renderLoop != nullptr) {
        renderLoop->postMessage(renderLoop->kMsgScaled, static_cast<int>(scaleX * 1000000),
                                static_cast<int>(scaleY * 1000000));
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_templechen_videoshaderdemo_cpp_NativeSurfaceView_nativeSetRotate(
        JNIEnv *env,
        jobject instance,
        jint degrees
) {
    if (renderLoop != nullptr) {
        renderLoop->postMessage(renderLoop->kMsgRotate, reinterpret_cast<int >(degrees), 0);
    }
}
