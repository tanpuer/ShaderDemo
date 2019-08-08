package com.example.templechen.videoshaderdemo.image.offscreen

import android.os.Handler
import android.os.Message

class OffScreenImageHandler(private val offScreenImageRenderThread: OffScreenImageRenderThread) : Handler() {

    companion object {
        const val MSG_START_CROP = 1
        const val MSG_PREPARE = 2
    }

    fun startCrop() {
        sendMessage(obtainMessage(MSG_START_CROP))
    }

    fun prepare() {
        sendMessage(obtainMessage(MSG_PREPARE))
    }

    override fun handleMessage(msg: Message?) {
        if (msg != null) {
            when (msg.what) {
                MSG_START_CROP -> {
                    offScreenImageRenderThread.startCrop()
                }
                MSG_PREPARE -> {
                    offScreenImageRenderThread.prepareGL()
                }
            }
        }
    }
}