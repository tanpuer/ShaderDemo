package com.example.templechen.videoshaderdemo.album.filter.douyin

import android.content.Context
import android.opengl.GLES30
import android.util.Log
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.R
import com.example.templechen.videoshaderdemo.album.filter.AlbumFilter

class DouyinCircleFilter : AlbumFilter {

    constructor(context: Context) : super(context)
    constructor(context: Context, resId: Int) : super(context, resId)
    constructor(context: Context, resId: Int, isGif: Boolean) : super(context, resId, isGif)

    companion object {
        private const val TAG = "DouyinCircleFilter"
        private const val uRatio = "uRatio"
        private const val uRadius = "uRadius"
        private const val uCenterX = "uCenterX"
        private const val uCenterY = "uCenterY"
    }

    private var uRatioLocation = -1
    private var uRadiusLocation = -1
    private var uCenterXLocation = -1
    private var uCenterYLocation = -1

    private var radius = 0.5f

    override fun initProgram() {
        vertexShader = GLUtils.loadShader(
            GLES30.GL_VERTEX_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.album_douyin_circle_vertex_shader)
        )
        fragmentShader = GLUtils.loadShader(
            GLES30.GL_FRAGMENT_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.album_douyin_circle_fragment_shader)
        )
        program = GLUtils.createProgram(vertexShader, fragmentShader)
        radius = 0.5f
        scrollY = 1.5f
        scrollX = 1.5f
        degrees = 45f
    }

    override fun drawFrame() {
        if (currentIndex < startTime) {
            currentIndex++
            return
        }
        val index = currentIndex - startTime
        radius += 0.002f
        scrollY = (1f - index * 2f / times) * 1.5f
        scrollX = scrollY
        if (scrollY < 0f) {
            scrollY = 0f
            scrollX = 0f
        }

        val scaleIndex = (index * 1f / times - 0.5f)
        if (scaleIndex > 0) {
            scaleX = 1f + scaleIndex
            scaleY = scaleX
        } else {
            scaleX = 1f
            scaleY = 1f
        }

        if (index < times / 2) {
            degrees = (1 - index * 2f / times) * 45f
        } else {
            degrees = 0f
        }

        if (index < times / 2) {
            radius = 0.5f
        } else {
            radius = (index * 2f / times - 1) * 4 + 0.5f
        }

        setScaleAndTransform()
        Log.d(TAG, "glUsePrograme : $program")
        GLES30.glUseProgram(program)
        uRatioLocation = GLES30.glGetUniformLocation(program, uRatio)
        uRadiusLocation = GLES30.glGetUniformLocation(program, uRadius)
        uCenterXLocation = GLES30.glGetUniformLocation(program, uCenterX)
        uCenterYLocation = GLES30.glGetUniformLocation(program, uCenterY)
        GLES30.glUniform1f(uRatioLocation, bitmapWidth * 1.0f / bitmapHeight * originScaleY)
        GLES30.glUniform1f(uRadiusLocation, radius)
        GLES30.glUniform1f(uCenterXLocation, scrollX)
        GLES30.glUniform1f(uCenterYLocation, scrollY)
        super.drawFrame()
    }

    override fun reset() {
        super.reset()
        radius = 0.5f
    }

}