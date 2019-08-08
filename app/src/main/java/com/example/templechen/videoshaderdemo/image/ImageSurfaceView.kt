package com.example.templechen.videoshaderdemo.image

import android.content.Context
import android.util.AttributeSet
import android.view.Choreographer
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.example.templechen.videoshaderdemo.image.render.ImageRenderThread

class ImageSurfaceView : SurfaceView, SurfaceHolder.Callback, Choreographer.FrameCallback {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private lateinit var url: String
    private var imageRenderThread: ImageRenderThread? = null
    private var mSurface: Surface? = null
    private lateinit var imageSurfaceHandler: ImageSurfaceHandler
    private var lastFrameIndex = 0

    fun initViews(imageSurfaceHandler: ImageSurfaceHandler, url: String) {
        this.url = url
        this.imageSurfaceHandler = imageSurfaceHandler
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        mSurface = holder?.surface
        imageRenderThread = ImageRenderThread(
            context,
            holder!!.surface,
            url,
            imageSurfaceHandler
        )
        imageRenderThread?.start()
        imageRenderThread?.waitUtilReady()
        imageRenderThread?.imageRendHandler?.sendSurfaceCreated()
        if (lastFrameIndex > 0) {
            imageRenderThread?.imageRendHandler?.seekTo(lastFrameIndex)
        }
        Choreographer.getInstance().postFrameCallback(this)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        imageRenderThread?.imageRendHandler?.sendSurfaceSizeChanged(width, height)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        if (imageRenderThread != null) {
            lastFrameIndex = imageRenderThread!!.lastFrameIndex
        }
        mSurface = null
        imageRenderThread?.imageRendHandler?.sendShutDown()
        imageRenderThread?.join()
        imageRenderThread = null
        Choreographer.getInstance().removeFrameCallback(this)
    }

    override fun doFrame(frameTimeNanos: Long) {
        imageRenderThread?.imageRendHandler?.doFrame()
        Choreographer.getInstance().postFrameCallback(this)
    }

    fun pause() {
        imageRenderThread?.imageRendHandler?.pause()
    }

    fun start() {
        imageRenderThread?.imageRendHandler?.start()
    }

    fun seekTo(count: Int) {
        imageRenderThread?.imageRendHandler?.seekTo(count)
    }

}