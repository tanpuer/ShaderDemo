package com.example.templechen.videoshaderdemo.gl

import android.os.Handler
import android.os.Message
import java.lang.IllegalArgumentException
import java.lang.ref.WeakReference

class ActivityHandler(glInfoCallback: IGLInfoCallback) : Handler() {

    companion object {
        private const val MSG_GLES_VERSION = 0
        private const val MSG_UPDATE_FPS = 1
    }

    private var weakActivity: WeakReference<IGLInfoCallback> = WeakReference(glInfoCallback)

    fun sendGLESVersion(version: Int) {
        sendMessage(obtainMessage(MSG_GLES_VERSION, version, 0))
    }

    fun sendFpsUpdate(tfps: Int, dropped: Int) {
        sendMessage(obtainMessage(MSG_UPDATE_FPS, tfps, dropped))
    }

    override fun handleMessage(msg: Message?) {
        val what = msg?.what
        val glInfoCallback = weakActivity.get()
        if (glInfoCallback != null) {
            when (what) {
                MSG_GLES_VERSION -> {
                    glInfoCallback.updateGLVersion(msg.arg1)
                }
                MSG_UPDATE_FPS -> {
                    glInfoCallback.updateFps(msg.arg1, msg.arg2)
                }
                else -> throw IllegalArgumentException()
            }
        }
    }

}