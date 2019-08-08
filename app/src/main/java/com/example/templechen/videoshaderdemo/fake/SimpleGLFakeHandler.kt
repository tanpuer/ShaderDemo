package com.example.templechen.videoshaderdemo.fake

import android.os.Handler
import android.os.Message
import android.view.Surface
import java.lang.IllegalArgumentException
import java.lang.ref.WeakReference

class SimpleGLFakeHandler(renderThread: SimpleGLFakeThread) : Handler() {

    companion object {
        const val MSG_SURFACE_CREATED = 0
        const val MSG_SURFACE_CHANGED = 1
        const val MSG_DO_FRAME = 2
        const val MSG_SHUTDOWN = 3

        const val MSG_RENDER_ANOTHER_SURFACE = 7
        const val MSG_STOP_RENDER_ANOTHER_SURFACE = 8

        const val MSG_CHANGE_FILTER = 21
    }

    private var weakRenderThread: WeakReference<SimpleGLFakeThread> = WeakReference(renderThread)

    fun sendSurfaceCreated(type: Int) {
        sendMessage(obtainMessage(MSG_SURFACE_CREATED, type, 0))
    }

    fun sendSurfaceChanged(format: Int, width: Int, height: Int) {
        sendMessage(obtainMessage(MSG_SURFACE_CHANGED, width, height))
    }

    fun sendDoFrame(frameTimeNanos: Long) {
        sendMessage(
            obtainMessage(
                MSG_DO_FRAME,
                frameTimeNanos.shr(32).toInt(),
                frameTimeNanos.toInt()
            )
        )
    }

    fun sendShutDown() {
        sendMessage(obtainMessage(MSG_SHUTDOWN))
    }

    fun renderAnotherSurface(surface: Surface?) {
        sendMessage(obtainMessage(MSG_RENDER_ANOTHER_SURFACE, surface))
    }

    fun stopRenderAnotherSurface() {
        sendMessage(obtainMessage(MSG_STOP_RENDER_ANOTHER_SURFACE))
    }

    fun changeFilter(type: Int) {
        sendMessage(obtainMessage(MSG_CHANGE_FILTER, type, 0))
    }

    override fun handleMessage(msg: Message?) {
        val what = msg?.what
        val renderThread = weakRenderThread.get() ?: return
        when (what) {
            MSG_SURFACE_CREATED -> {
                renderThread.surfaceCreated(msg.arg1)
            }
            MSG_SURFACE_CHANGED -> {
                renderThread.surfaceChanged(msg.arg1, msg.arg2)
            }
            MSG_DO_FRAME -> {
                val timestamp: Long =
                    msg.arg1.toLong().shl(32).or(msg.arg2.toLong().and(0xffffffffL))
                renderThread.doFrame(timestamp)
            }
            MSG_SHUTDOWN -> {
                renderThread.shutDown()
            }
            MSG_RENDER_ANOTHER_SURFACE -> {
                val surface = msg.obj
                if (surface != null) {
                    renderThread.renderAnotherSurface(surface as Surface)
                }
            }
            MSG_STOP_RENDER_ANOTHER_SURFACE -> {
                renderThread.stopRenderAnotherSurface()
            }
            MSG_CHANGE_FILTER -> {
                renderThread.resetFilter(msg.arg1)
            }
            else -> throw IllegalArgumentException()
        }
    }

}