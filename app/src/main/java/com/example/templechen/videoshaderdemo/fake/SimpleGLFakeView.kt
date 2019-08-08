package com.example.templechen.videoshaderdemo.fake

import android.app.Activity
import android.content.Context
import android.view.Choreographer
import android.view.Surface
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.gl.egl.EglCore
import com.example.templechen.videoshaderdemo.gl.egl.OffscreenSurface
import com.example.templechen.videoshaderdemo.player.IExoPlayer

class SimpleGLFakeView(context: Context, playerTool: IExoPlayer) : Choreographer.FrameCallback {

    var renderThread: SimpleGLFakeThread? = null
    var alive = false
    var eglCore: EglCore = EglCore(null, EglCore.FLAG_RECORDABLE.or(EglCore.FLAG_TRY_GLES3))
    var offscreenSurface = OffscreenSurface(eglCore, 720, 1280)

    init {
        renderThread =
            SimpleGLFakeThread(
                context,
                GLUtils.getDisplayRefreshNsec(context as Activity),
                playerTool,
                "BaseFilter",
                eglCore,
                offscreenSurface
            )
        renderThread?.start()
        renderThread?.waitUtilReady()
        val renderHandler = renderThread?.mHandler
        renderHandler?.sendSurfaceCreated(0)
//        renderHandler?.sendSurfaceChanged(0, 1280, 720)
    }

    override fun doFrame(frameTimeNanos: Long) {
        val renderHandler = renderThread?.mHandler
        renderHandler?.sendDoFrame(frameTimeNanos)
        if (alive) {
            Choreographer.getInstance().postFrameCallback(this)
        }
    }

    fun surfaceSizeChanged(width: Int, height: Int) {
        val renderHandler = renderThread?.mHandler
        renderHandler?.sendSurfaceChanged(0, width, height)
    }

    fun cancelDoFrame() {
        alive = false
        Choreographer.getInstance().removeFrameCallback(this)
    }

    fun startDoFrame() {
        alive = true
        Choreographer.getInstance().postFrameCallback(this)
    }

    fun renderAnotherSurface(surface: Surface?) {
        val renderHandler = renderThread?.mHandler
        renderHandler?.renderAnotherSurface(surface)
    }

    fun stopRenderAnotherSurface() {
        val renderHandler = renderThread?.mHandler
        renderHandler?.stopRenderAnotherSurface()
    }

    fun sendShutDown() {
        val renderHandler = renderThread?.mHandler
        renderHandler?.sendShutDown()
        renderThread?.join()
        renderThread = null
    }

    fun changeFilter(type: Int) {
        val renderHandler = renderThread?.mHandler
        renderHandler?.changeFilter(type)
    }
}

