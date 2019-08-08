package com.example.templechen.videoshaderdemo.album.filter

import android.content.Context
import android.opengl.GLES30
import android.util.Log
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.R
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class AlbumCoverFilter : AlbumFilter {

    companion object {
        private const val uWidth = "uWidth"
        private const val uHeight = "uHeight"
        private const val uVideoRatio = "uVideoRatio"
        private const val uWidthSection = "uWidthSection"
        private const val uHeightSection = "uHeightSection"
    }

    private var uWidthLocation = -1
    private var uHeigthLocation = -1
    private var uVideoRatioLocation = -1
    private var uWidthSectionLocation = -1
    private var uHeightSectionLocation = -1

    private var width = 0.004f
    private var height = 0.004f
    private var widthSection = 4.0f
    private var heightSection = 7.0f

    private val minWidth = 0.004f

    constructor(context: Context) : super(context)
    constructor(context: Context, resId: Int) : super(context, resId)
    constructor(context: Context, resId: Int, isGif: Boolean) : super(context, resId, isGif)

    override fun initProgram() {
        vertexShader = GLUtils.loadShader(
            GLES30.GL_VERTEX_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.album_cover_vertex_shader)
        )
        fragmentShader = GLUtils.loadShader(
            GLES30.GL_FRAGMENT_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.album_cover_fragment_shader)
        )
        program = GLUtils.createProgram(vertexShader, fragmentShader)
    }

    override fun drawFrame() {
        GLES30.glUseProgram(program)
        uWidthLocation = GLES30.glGetUniformLocation(program, uWidth)
        uHeigthLocation = GLES30.glGetUniformLocation(program, uHeight)
        uVideoRatioLocation = GLES30.glGetUniformLocation(program, uVideoRatio)
        uWidthSectionLocation = GLES30.glGetUniformLocation(program, uWidthSection)
        uHeightSectionLocation = GLES30.glGetUniformLocation(program, uHeightSection)

        val index = if (currentIndex < times / 2) currentIndex else times - currentIndex
        width = index * 2f / times / widthSection
        height = index * 2f / times / heightSection
//        Log.d(TAG, "$width : $height : $currentIndex")
        GLES30.glUniform1f(uWidthLocation, max(width, minWidth))
        GLES30.glUniform1f(uHeigthLocation, max(height, minWidth / bitmapHeight * bitmapWidth))
        GLES30.glUniform1f(uVideoRatioLocation, bitmapWidth * 1.0f / bitmapHeight)
        GLES30.glUniform1f(uWidthSectionLocation, widthSection)
        GLES30.glUniform1f(uHeightSectionLocation, heightSection)

        super.drawFrame()
    }
}