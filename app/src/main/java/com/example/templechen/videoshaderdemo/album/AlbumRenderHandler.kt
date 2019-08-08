package com.example.templechen.videoshaderdemo.album

import android.os.Handler
import android.os.Message

class AlbumRenderHandler(val albumRenderThread: AlbumRenderThread) : Handler() {

    companion object {
        const val MSG_SURFACE_CREATED = 0
        const val MSG_SURFACE_CHANGED = 1
        const val MSG_DO_FRAME = 2
        const val MSG_SHUTDOWN = 4
    }

    fun sendSurfaceCreated() {
        sendMessage(obtainMessage(MSG_SURFACE_CREATED, 0))
    }

    fun sendSurfaceSizeChanged(width: Int, height: Int) {
        sendMessage(obtainMessage(MSG_SURFACE_CHANGED, width, height))
    }

    fun doFrame() {
        sendMessage(obtainMessage(MSG_DO_FRAME, 0))
    }

    fun sendShutDown() {
        sendMessage(obtainMessage(MSG_SHUTDOWN, 0))
    }

    override fun handleMessage(msg: Message?) {
        when(msg?.what) {
            MSG_SURFACE_CREATED -> {
                albumRenderThread.surfaceCreated()
            }
            MSG_SURFACE_CHANGED -> {
                albumRenderThread.surfaceChanged(msg.arg1, msg.arg2)
            }
            MSG_DO_FRAME -> {
                albumRenderThread.doFrame()
            }
            MSG_SHUTDOWN -> {
                albumRenderThread.shutDown()
            }
        }
    }
}