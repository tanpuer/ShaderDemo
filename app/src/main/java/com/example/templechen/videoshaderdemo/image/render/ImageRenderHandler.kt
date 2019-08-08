package com.example.templechen.videoshaderdemo.image.render

import android.os.Handler
import android.os.Message


class ImageRenderHandler(private val imageRenderThread: ImageRenderThread) : Handler() {

    companion object {
        const val MSG_SURFACE_CREATED = 0
        const val MSG_SURFACE_CHANGED = 1
        const val MSG_DO_FRAME = 2
        const val MSG_SHUTDOWN = 4

        const val MSG_START = 11
        const val MSG_PAUSE = 12
        const val MSG_SEEK = 13
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

    fun start() {
        sendMessage(obtainMessage(MSG_START))
    }

    fun pause() {
        sendMessage(obtainMessage(MSG_PAUSE))
    }

    fun seekTo(count: Int) {
        sendMessage(obtainMessage(MSG_SEEK, count, 0))
    }

    override fun handleMessage(msg: Message?) {
        if (msg == null) {
            return
        }
        when (msg.what) {
            MSG_SURFACE_CREATED -> {
                imageRenderThread.surfaceCreated()
            }
            MSG_SURFACE_CHANGED -> {
                imageRenderThread.surfaceChanged(msg.arg1, msg.arg2)
            }
            MSG_DO_FRAME -> {
                imageRenderThread.doFrame()
            }
            MSG_SHUTDOWN -> {
                imageRenderThread.shutDown()
            }
            MSG_START -> {
                imageRenderThread.startImage()
            }
            MSG_PAUSE -> {
                imageRenderThread.pauseImage()
            }
            MSG_SEEK -> {
                imageRenderThread.seekTo(msg.arg1)
            }
        }
    }

}