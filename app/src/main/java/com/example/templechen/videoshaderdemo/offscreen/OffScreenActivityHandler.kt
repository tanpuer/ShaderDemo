package com.example.templechen.videoshaderdemo.offscreen

import android.os.Handler
import android.os.Message

class OffScreenActivityHandler(private val offScreenActivity: OffScreenActivity) : Handler() {

    companion object {
        const val MSG_OFF_SCREEN_END = 0
    }

    fun sendOffscreenEnd() {
        sendMessage(obtainMessage(MSG_OFF_SCREEN_END))
    }

    override fun handleMessage(msg: Message?) {
        if (msg == null) {
            return
        }
        when (msg.what) {
            MSG_OFF_SCREEN_END -> {
                offScreenActivity.setDuration()
            }
        }
    }
}