package com.example.templechen.videoshaderdemo.album.filter

import android.content.Context
import android.opengl.GLES30
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.R

class AlbumCircleFilter : AlbumFilter {

    constructor(context: Context) : super(context)
    constructor(context: Context, resId: Int) : super(context, resId)
    constructor(context: Context, resId: Int, isGif: Boolean) : super(context, resId, isGif)

    companion object {
        private const val uRatio = "uRatio"
        private const val uRadius = "uRadius"
    }

    private var uRatioLocation = -1
    private var uRadiusLocation = -1

    private var radius = 0f

    override fun initProgram() {
        vertexShader = GLUtils.loadShader(
            GLES30.GL_VERTEX_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.album_circle_vertex_shader)
        )
        fragmentShader = GLUtils.loadShader(
            GLES30.GL_FRAGMENT_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.album_circle_fragment_shader)
        )
        program = GLUtils.createProgram(vertexShader, fragmentShader)
    }

    override fun drawFrame() {
        radius += 1f / times
        if (radius > 2f) {
            radius = 0f
        }
        GLES30.glUseProgram(program)
        uRatioLocation = GLES30.glGetUniformLocation(program,
            uRatio
        )
        uRadiusLocation = GLES30.glGetUniformLocation(program,
            uRadius
        )
        GLES30.glUniform1f(uRatioLocation, bitmapWidth * 1.0f / bitmapHeight)
        GLES30.glUniform1f(uRadiusLocation, radius)
        super.drawFrame()
    }

    override fun reset() {
        super.reset()
        radius = 0f
    }
}