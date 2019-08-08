package com.example.templechen.videoshaderdemo.filter

import android.content.Context
import android.opengl.GLES30
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.R

class StandaloneFilter(context: Context, oesTextureId: Int) : BaseFilter(context, oesTextureId) {

    companion object {
        private const val uLeftBorder = "uLeftBorder"
        private const val uRightBorder = "uRightBorder"
        private const val uAlpha = "uAlpha"
    }

    private var uLeftBorderLocation = -1
    private var uRightBorderLocation = -1
    private var uAlphaLocation = -1
    var leftBorder = 0f
    var rightBorder = 1f
    var alpha = 0.5f

    override fun initProgram() {
        vertexShader = GLUtils.loadShader(
            GLES30.GL_VERTEX_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.stand_alone_vertex_shader)
        )
        fragmentShader = GLUtils.loadShader(
            GLES30.GL_FRAGMENT_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.stand_alone_fragment_shader)
        )
        program = GLUtils.createProgram(vertexShader, fragmentShader)
    }

    override fun drawFrame() {
        GLES30.glUseProgram(program)
        uLeftBorderLocation = GLES30.glGetUniformLocation(program, uLeftBorder)
        GLES30.glUniform1f(uLeftBorderLocation, leftBorder)
        uRightBorderLocation = GLES30.glGetUniformLocation(program, uRightBorder)
        GLES30.glUniform1f(uRightBorderLocation, rightBorder)
        uAlphaLocation = GLES30.glGetUniformLocation(program, uAlpha)
        GLES30.glUniform1f(uAlphaLocation, alpha)
        super.drawFrame()
    }
}