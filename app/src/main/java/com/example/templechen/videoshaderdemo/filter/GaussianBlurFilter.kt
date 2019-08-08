package com.example.templechen.videoshaderdemo.filter

import android.content.Context
import android.opengl.GLES30
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.R

class GaussianBlurFilter(context: Context, oesTextureId: Int) : BaseFilter(context, oesTextureId) {

    companion object {
        private const val uTexelOffset = "uTexelOffset"

        private val texelOffset = floatArrayOf(0.008f, 0.008f)
    }

    private var uTexelOffsetLocation = -1

    override fun initProgram() {
        vertexShader = GLUtils.loadShader(
            GLES30.GL_VERTEX_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.gaussian_blur_vertex_shader)
        )
        fragmentShader = GLUtils.loadShader(
            GLES30.GL_FRAGMENT_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.gaussian_blur_fragment_shader)
        )
        program = GLUtils.createProgram(vertexShader, fragmentShader)
    }

    override fun drawFrame() {
        GLES30.glUseProgram(program)
        uTexelOffsetLocation = GLES30.glGetUniformLocation(program, uTexelOffset)
        GLES30.glUniform2fv(uTexelOffsetLocation, 1, texelOffset, 0)
        super.drawFrame()
    }
}