package com.example.templechen.videoshaderdemo.filter

import android.content.Context
import android.opengl.GLES30
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.R

class CornerRadiusFilter(context: Context, oesTextureId: Int) : BaseFilter(context, oesTextureId) {

    companion object {
        private const val uCornerRadiusX = "uCornerRadiusX"
        private const val uCornerRadiusY = "uCornerRadiusY"
        private const val uRatio = "uRatio"
    }

    private var uCornerRadiusXLocation = -1
    private var uCornerRadiusYLocation = -1
    private var uRatioLocation = -1

    private var radiusY = 0.3f * 16 / 9
    private var radiusX = 0.3f
    private var ratio = 16f / 9f

    override fun initProgram() {
        vertexShader = GLUtils.loadShader(
            GLES30.GL_VERTEX_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.corner_radius_vertex_shader)
        )
        fragmentShader = GLUtils.loadShader(
            GLES30.GL_FRAGMENT_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.corner_radius_fragment_shader)
        )
        program = GLUtils.createProgram(vertexShader, fragmentShader)
    }

    override fun drawFrame() {
        GLES30.glUseProgram(program)
        uCornerRadiusXLocation = GLES30.glGetUniformLocation(program, uCornerRadiusX)
        GLES30.glUniform1f(uCornerRadiusXLocation, radiusX)
        uCornerRadiusYLocation = GLES30.glGetUniformLocation(program, uCornerRadiusY)
        GLES30.glUniform1f(uCornerRadiusYLocation, radiusY)
        uRatioLocation = GLES30.glGetUniformLocation(program, uRatio)
        GLES30.glUniform1f(uRatioLocation, ratio)
        super.drawFrame()
    }

}