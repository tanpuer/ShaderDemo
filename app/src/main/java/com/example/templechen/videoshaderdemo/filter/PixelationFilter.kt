package com.example.templechen.videoshaderdemo.filter

import android.content.Context
import android.opengl.GLES30
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.R

class PixelationFilter(context: Context, oesTextureId: Int) : BaseFilter(context, oesTextureId) {

    companion object {
        private const val uImageWidthFactor = "uImageWidthFactor"
        private const val uImageHeightFactor = "uImageHeightFactor"
        private const val uPixel = "uPixel"

        private const val imageWidthFactor = 0.02f
        private const val imageHeightFactor = 0.02f
        private const val pixel = 1f
    }

    private var uImageWidthFactorLocation = -1
    private var uImageHeightFactorLocation = -1
    private var uPixelLocation = -1

    override fun initProgram() {
        vertexShader = GLUtils.loadShader(
            GLES30.GL_VERTEX_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.pixelation_vertex_shader)
        )
        fragmentShader = GLUtils.loadShader(
            GLES30.GL_FRAGMENT_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.pixelation_fragment_shader)
        )
        program = GLUtils.createProgram(vertexShader, fragmentShader)
    }

    override fun drawFrame() {
        GLES30.glUseProgram(program)
        uImageWidthFactorLocation = GLES30.glGetUniformLocation(program, uImageWidthFactor)
        GLES30.glUniform1f(uImageWidthFactorLocation, imageWidthFactor)
        uImageHeightFactorLocation = GLES30.glGetUniformLocation(program, uImageHeightFactor)
        GLES30.glUniform1f(uImageHeightFactorLocation, imageHeightFactor)
        uPixelLocation = GLES30.glGetUniformLocation(program, uPixel)
        GLES30.glUniform1f(uPixelLocation, pixel)
        super.drawFrame()
    }
}