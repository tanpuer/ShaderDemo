//
// Created by templechen on 2019/3/15.
//

#include <GLES3/gl3.h>
#include "renderer.h"
#include "../common/native_log.h"

renderer::renderer() {
    eglCore = nullptr;
    windowSurface = nullptr;
}

renderer::~renderer() {
    if (windowSurface != nullptr) {
        windowSurface->release();
        delete windowSurface;
        windowSurface = nullptr;
    }
    if (eglCore != nullptr) {
        eglCore->release();
        delete eglCore;
        eglCore = nullptr;
    }
    delete filter;
}

void renderer::surfaceCreated(int textureId, ANativeWindow *nativeWindow) {
    eglCore = new egl_core(nullptr, FLAG_TRY_GLES3);
    windowSurface = new window_surface(nativeWindow, eglCore, false);
    windowSurface->makeCurrent();
    glClearColor(red, green, blue, alpha);
    glDisable(GL_DEPTH_TEST);
    glDisable(GL_CULL_FACE);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    oesTextureId = textureId;
    filter = new base_filter(oesTextureId);
    filter->initProgram();
}

void renderer::surfaceChanged(int width, int height) {
    windowSurface->makeCurrent();
    glViewport(0, 0, width, height);
    this->viewWidth = width;
    this->viewHeight = height;
    windowSurface->swapBuffer();
    filter->setViewSize(width, height);
}

void renderer::surfaceDestroyed() {
    if (windowSurface != nullptr) {
        windowSurface->release();
        delete windowSurface;
        windowSurface = nullptr;
    }
    if (eglCore != nullptr) {
        eglCore->release();
        delete eglCore;
        eglCore = nullptr;
    }
}

void renderer::surfaceDoFrame(long frameTimeNanos) {
    glClearColor(red, green, blue, alpha);
    glClear(GL_COLOR_BUFFER_BIT);

    //api>=28, so call Java SurfaceTexture.updateTexImage() because native
//    ASurfaceTexture_updateTexImage(surfaceTexture);
    //call Java
    if (this->javaSurfaceTexuture != nullptr) {
        jclass clazz = env->GetObjectClass(this->javaSurfaceTexuture);
        jmethodID methodId = env->GetMethodID(clazz, "updateTexImage", "()V");
        env->CallVoidMethod(this->javaSurfaceTexuture, methodId);
    }

    //api >=28
//    ASurfaceTexture_getTransformMatrix(surfaceTexture, matrix);
    //call Java
//    if (this->javaSurfaceTexuture != nullptr) {
//        jclass clazz = env->GetObjectClass(this->javaSurfaceTexuture);
//        jmethodID methodId = env->GetMethodID(clazz, "getTransformMatrix", "([F)V");
//        if (methodId == nullptr) {
//            return;
//        }
//        if (array == nullptr) {
//            array = env->NewFloatArray(16);
//        }
//        env->CallVoidMethod(this->javaSurfaceTexuture, methodId, array);
//        float *nativeValues = env->GetFloatArrayElements(array, nullptr);
//        for (int i = 0; i < 16; i++) {
//            filter->transformMatrix[i] = nativeValues[i];
//        }
//        env->ReleaseFloatArrayElements(array, nativeValues, 0);
//    }
    filter->doFrame();
    windowSurface->swapBuffer();
}

void renderer::surfaceTextureCreated(ASurfaceTexture *surfaceTexture) {
    this->surfaceTexture = surfaceTexture;
}

void renderer::videoSizeChanged(int width, int height) {
    videoWidth = width;
    videoHeight = height;
    filter->setVideoSize(videoWidth, videoHeight);
    if (videoWidth * 1.0f / videoHeight > viewWidth * 1.0f / viewHeight) {
        //横屏视频
        originScaleY = viewWidth * 1.0f / width * height / viewHeight;
        originScaleX = 1.0f;
    } else {
        //竖屏视频
        originScaleY = 1.0f;
        originScaleX = viewHeight * 1.0f / height * width / viewWidth;
    }

    filter->setScaleAndTransform(scaleX * originScaleX, scaleY * originScaleY, transformX, transformY, rotateDegrees);
}

void renderer::backgroundColorChanged(float *colors) {
    red = colors[0] / 1000000.0f;
    green = colors[1] / 1000000.0f;
    blue = colors[2] / 1000000.0f;
    alpha = 1.0f;
}

void renderer::onScroll(float scrollX, float scrollY) {
    transformX = scrollX;
    transformY = scrollY;
    filter->setScaleAndTransform(scaleX * originScaleX, scaleY * originScaleY, transformX, transformY, rotateDegrees);
}

void renderer::onScale(float scaleX, float scaleY) {
    this->scaleX = scaleX;
    this->scaleY = scaleY;
    filter->setScaleAndTransform(scaleX * originScaleX, scaleY * originScaleY, transformX, transformY, rotateDegrees);
}

void renderer::onRotate(int degrees) {
    this->rotateDegrees = degrees;
    filter->setScaleAndTransform(scaleX * originScaleX, scaleY * originScaleY, transformX, transformY, rotateDegrees);
}

