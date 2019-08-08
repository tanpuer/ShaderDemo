package com.example.templechen.videoshaderdemo.album.filter

import android.content.Context
import android.opengl.GLES30
import android.util.Log
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.R

class AlbumCover2Filter : AlbumFilter {

    companion object {
        private const val uWidth = "uWidth"
        private const val uHeight = "uHeight"
        private const val uVideoRatio = "uVideoRatio"
        private const val uWidthSection = "uWidthSection"
        private const val uHeightSection = "uHeightSection"
        private const val uAlphaPos = "uAlphaPos"
    }

    private var uWidthLocation = -1
    private var uHeightLocation = -1
    private var uVideoRatioLocation = -1
    private var uWidthSectionLocation = -1
    private var uHeightSectionLocation = -1
    private var uAlphaPosLocation = -1

    private var width = 0.004f
    private var height = 0.004f
    private var widthSection = 4.0f
    private var heightSection = 6.0f
    private var alphaPos = -1.0f

    private val minWidth = 0.004f

    constructor(context: Context) : super(context)
    constructor(context: Context, resId: Int) : super(context, resId)
    constructor(context: Context, resId: Int, isGif: Boolean) : super(context, resId, isGif)

    override fun initProgram() {
        vertexShader = GLUtils.loadShader(
            GLES30.GL_VERTEX_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.album_cover2_vertex_shader)
        )
        fragmentShader = GLUtils.loadShader(
            GLES30.GL_FRAGMENT_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.album_cover2_fragment_shader)
        )
        program = GLUtils.createProgram(vertexShader, fragmentShader)
    }

    override fun drawFrame() {
        GLES30.glUseProgram(program)
        uWidthLocation = GLES30.glGetUniformLocation(program, uWidth)
        uHeightLocation = GLES30.glGetUniformLocation(program, uHeight)
        uVideoRatioLocation = GLES30.glGetUniformLocation(program, uVideoRatio)
        uWidthSectionLocation = GLES30.glGetUniformLocation(program, uWidthSection)
        uHeightSectionLocation = GLES30.glGetUniformLocation(program, uHeightSection)
        uAlphaPosLocation = GLES30.glGetUniformLocation(program, uAlphaPos)

        GLES30.glUniform1f(uWidthLocation, width)
        GLES30.glUniform1f(uHeightLocation, width / bitmapHeight * bitmapWidth)
        GLES30.glUniform1f(uVideoRatioLocation, bitmapWidth * 1.0f / bitmapHeight)
        GLES30.glUniform1f(uWidthSectionLocation, widthSection)
        GLES30.glUniform1f(uHeightSectionLocation, heightSection)
//        Log.d(TAG, "$alphaPos")
        alphaPos = 1f - currentIndex * 2.0f / times
        GLES30.glUniform1f(uAlphaPosLocation, alphaPos)

        super.drawFrame()
    }
}