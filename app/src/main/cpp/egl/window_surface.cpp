//
// Created by cw on 2018/7/24.
//

#include "window_surface.h"
#include "../common/native_log.h"

window_surface::window_surface(ANativeWindow *nativeWindow, egl_core *eglCore, bool releaseSurface):base_surface(eglCore) {
    mSurface = nativeWindow;
    createWindowSurface(mSurface);
    mReleaseSurface = releaseSurface;
}

window_surface::window_surface(ANativeWindow *nativeWindow, egl_core *eglCore) :base_surface(eglCore){
    mSurface = nativeWindow;
    createWindowSurface(mSurface);
}

void window_surface::release() {
    releaseEglSurface();
    if (mSurface != NULL){
        ANativeWindow_release(mSurface);
        mSurface = NULL;
    }
}

void window_surface::recreate(egl_core *eglCore) {
    if (mSurface == NULL){
        ALOGD("not yet implements ANativeWindow");
        return;
    }
    mEglCore = eglCore;
    createWindowSurface(mSurface);
}
