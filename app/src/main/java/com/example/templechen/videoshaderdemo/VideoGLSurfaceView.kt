package com.example.templechen.videoshaderdemo

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class VideoGLSurfaceView : GLSurfaceView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private lateinit var videoGlRenderer: VideoGLRenderer

    fun init(context: Context, isPreviewStarted: Boolean) {
        setEGLContextClientVersion(3)
        videoGlRenderer = VideoGLRenderer()
        videoGlRenderer.init(context, this, isPreviewStarted)
        setRenderer(videoGlRenderer)
    }

    fun destroy() {
        videoGlRenderer.destroy()
    }
}