package com.example.templechen.videoshaderdemo.image

import android.os.Handler
import android.os.Message


class ImageSurfaceHandler(val imageSurfaceActivity: ImageSurfaceActivity) : Handler() {

    companion object {
        const val MSG_TOTAL_FRAMES = 1
        const val MSG_CURRENT_FRAME = 2
    }

    fun setTotalFrames(frames: Int) {
        sendMessage(obtainMessage(MSG_TOTAL_FRAMES, frames, 0))
    }

    fun setCurrentFrame(frame: Int) {
        sendMessage(obtainMessage(MSG_CURRENT_FRAME, frame, 0))
    }

    override fun handleMessage(msg: Message?) {
        if (msg != null) {
            when (msg.what) {
                MSG_TOTAL_FRAMES -> {
                    imageSurfaceActivity.setTotalFrames(msg.arg1)
                }
                MSG_CURRENT_FRAME -> {
                    imageSurfaceActivity.setCurrentFrame(msg.arg1)
                }
            }
        }
    }
}