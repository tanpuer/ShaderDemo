//
// Created by templechen on 2019/3/15.
//

#include "render_loop.h"

render_loop::render_loop() {
    surface_renderer = new renderer();
}

render_loop::~render_loop() {
    ALOGD("pthread render_loop exit")
    //todo
//    delete surface_renderer;
}

void render_loop::handleMessage(looper::LooperMessage *msg) {
    switch (msg->what) {
        case kMsgSurfaceCreated: {
            if (vm != nullptr) {
                ALOGD("AttachCurrentThread begin!")
                vm->AttachCurrentThread(&surface_renderer->env, nullptr);
                ALOGD("AttachCurrentThread end!")
            }
            surface_renderer->surfaceCreated(msg->arg1, static_cast<ANativeWindow *>(msg->obj));
            break;
        }
        case kMsgSurfaceChanged: {
            surface_renderer->surfaceChanged(msg->arg1, msg->arg2);
            break;
        }
        case kMsgSurfaceDestroyed: {
            surface_renderer->surfaceDestroyed();
            if (vm != nullptr) {
                vm->DetachCurrentThread();
            }
            break;
        }
        case kMsgSurfaceDoFrame: {
            surface_renderer->surfaceDoFrame(reinterpret_cast<long>(msg->obj));
            break;
        }
        case kMsgSurfaceTexture: {
            surface_renderer->surfaceTextureCreated(static_cast<ASurfaceTexture *>(msg->obj));
            break;
        }
        case kMsgJavaSurfaceTexture: {
            surface_renderer->javaSurfaceTexuture = static_cast<jobject>(msg->obj);
            break;
        }
        case kMsgVideoSizeChanged: {
            surface_renderer->videoSizeChanged(msg->arg1, msg->arg2);
            break;
        }
        case kMsgBackgroundColorChanged: {
            surface_renderer->backgroundColorChanged(static_cast<float *>(msg->obj));
            break;
        }
        case kMsgScrolled: {
            surface_renderer->onScroll(msg->arg1 / 1000000.0f, msg->arg2 / 1000000.0f);
            break;
        }
        case kMsgScaled: {
            surface_renderer->onScale(msg->arg1 / 1000000.0f, msg->arg2 / 1000000.0f);
            break;
        }
        case kMsgRotate: {
            surface_renderer->onRotate(msg->arg1);
            break;
        }
        default:
            break;
    }
}

void render_loop::pthreadExit() {

}
