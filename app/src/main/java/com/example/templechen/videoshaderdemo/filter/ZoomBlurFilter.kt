package com.example.templechen.videoshaderdemo.filter

import android.content.Context
import android.opengl.GLES30
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.R

class ZoomBlurFilter(context: Context, oesTextureId: Int) : BaseFilter(context, oesTextureId) {

    companion object {
        private const val uBlurCenter = "uBlurCenter"
        private const val uBlurSize = "uBlurSize"

        private val blurCenter = floatArrayOf(0.5f, 0.5f)
        private const val blurSize = 1f
    }

    private var uBlurCenterLocation = -1
    private var uBlurSizeLocation = -1

    override fun initProgram() {
        vertexShader = GLUtils.loadShader(
            GLES30.GL_VERTEX_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.zoom_blur_vertex_shader)
        )
        fragmentShader = GLUtils.loadShader(
            GLES30.GL_FRAGMENT_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.zoom_blur_fragment_shader)
        )
        program = GLUtils.createProgram(vertexShader, fragmentShader)
    }

    override fun drawFrame() {
        uBlurCenterLocation = GLES30.glGetUniformLocation(program, uBlurCenter)
        uBlurSizeLocation = GLES30.glGetUniformLocation(program, uBlurSize)
        GLES30.glUniform2fv(uBlurCenterLocation, 1, blurCenter, 0)
        GLES30.glUniform1f(uBlurSizeLocation, blurSize)
        super.drawFrame()
    }
}