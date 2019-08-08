package com.example.templechen.videoshaderdemo.album

import android.content.Context
import android.util.AttributeSet
import android.view.Choreographer
import android.view.SurfaceHolder
import android.view.SurfaceView

class AlbumSurfaceView : SurfaceView, SurfaceHolder.Callback, Choreographer.FrameCallback {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        holder.addCallback(this)
    }

    var albumRenderThread: AlbumRenderThread? = null

    override fun surfaceCreated(holder: SurfaceHolder?) {
        albumRenderThread = AlbumRenderThread(context, holder!!.surface)
        albumRenderThread?.start()
        albumRenderThread?.waitUtilReady()
        val albumRenderHandler = albumRenderThread?.mHandler
        albumRenderHandler?.sendSurfaceCreated()
        Choreographer.getInstance().postFrameCallback(this)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        val albumRenderHandler = albumRenderThread?.mHandler
        albumRenderHandler?.sendSurfaceSizeChanged(width, height)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        val albumRenderHandler = albumRenderThread?.mHandler
        albumRenderHandler?.sendShutDown()
        albumRenderThread?.join()
        albumRenderThread = null
        Choreographer.getInstance().removeFrameCallback(this)
    }

    override fun doFrame(frameTimeNanos: Long) {
        val albumRenderHandler = albumRenderThread?.mHandler
        albumRenderHandler?.doFrame()
        Choreographer.getInstance().postFrameCallback(this)
    }

}