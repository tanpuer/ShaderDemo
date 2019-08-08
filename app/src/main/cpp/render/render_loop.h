//
// Created by templechen on 2019/3/15.
//

#ifndef VIDEOSHADERDEMO_RENDER_LOOP_H
#define VIDEOSHADERDEMO_RENDER_LOOP_H


#include "looper.h"
#include "renderer.h"

class render_loop : public looper {

public:

    enum {
        kMsgSurfaceCreated,
        kMsgSurfaceChanged,
        kMsgSurfaceDestroyed,
        kMsgSurfaceDoFrame,
        kMsgSurfaceTexture,
        kMsgJavaSurfaceTexture,
        kMsgVideoSizeChanged,
        kMsgBackgroundColorChanged,
        kMsgScrolled,
        kMsgScaled,
        kMsgRotate
    };

    render_loop();

    ~render_loop();

    void handleMessage(LooperMessage *msg) override;

    void pthreadExit() override;

    JavaVM *vm;

private:

    renderer *surface_renderer;

};


#endif //VIDEOSHADERDEMO_RENDER_LOOP_H
