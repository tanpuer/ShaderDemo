//
// Created by templechen on 2019/3/15.
//

#ifndef VIDEOSHADERDEMO_RENDERER_H
#define VIDEOSHADERDEMO_RENDERER_H

#include <android/native_activity.h>
#include <android/surface_texture.h>
#include "../egl/egl_core.h"
#include "../egl/window_surface.h"
#include "../filter/base_filter.h"

class renderer {

public:
    renderer();

    ~renderer();

    void surfaceCreated(int textureId, ANativeWindow *nativeWindow);

    void surfaceChanged(int width, int height);

    void surfaceDestroyed();

    void surfaceDoFrame(long frameTimeNanos);

    void surfaceTextureCreated(ASurfaceTexture *surfaceTexture);

    void videoSizeChanged(int width, int height);

    void backgroundColorChanged(float *colors);

    void onScroll(float scrollX, float scrollY);

    void onScale(float scaleX, float scaleY);

    void onRotate(int degrees);

    JNIEnv *env = nullptr;

    jobject javaSurfaceTexuture;

private:

    egl_core *eglCore;

    window_surface *windowSurface;

    base_filter *filter;

    int oesTextureId;

    ASurfaceTexture *surfaceTexture;

    float matrix[16] = {
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, -1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 1.0f
    };

    int videoWidth;
    int videoHeight;

    int viewWidth;
    int viewHeight;

    float red = 0.0f;
    float green = 0.0f;
    float blue = 0.0f;
    float alpha = 1.0f;

    float originScaleX = 1.0f;
    float originScaleY = 1.0f;
    float scaleX = 1.0f;
    float scaleY = 1.0f;
    float transformX = 0.0f;
    float transformY = 0.0f;
    int rotateDegrees = 0;
};


#endif //VIDEOSHADERDEMO_RENDERER_H
