package com.example.templechen.videoshaderdemo.filter

import android.content.Context
import android.opengl.GLES30
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.R

class SwirlFilter(context: Context, oesTextureId: Int) : BaseFilter(context, oesTextureId) {

    companion object {
        private const val uCenter = "uCenter"
        private const val uRadius = "uRadius"
        private const val uAngle = "uAngle"

        private val center = floatArrayOf(0.5f, 0.5f)
        private const val radius = 1f
        private const val angle = 1f
    }

    private var uCenterLocation = -1
    private var uRadiusLocation = -1
    private var uAngleLocation = -1

    override fun initProgram() {
        vertexShader = GLUtils.loadShader(
            GLES30.GL_VERTEX_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.swirl_vertex_shader)
        )
        fragmentShader = GLUtils.loadShader(
            GLES30.GL_FRAGMENT_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.swirl_fragment_shader)
        )
        program = GLUtils.createProgram(vertexShader, fragmentShader)
    }

    override fun drawFrame() {
        uCenterLocation = GLES30.glGetUniformLocation(program, uCenter)
        GLES30.glUniform2fv(uCenterLocation, 1, center, 0)
        uRadiusLocation = GLES30.glGetUniformLocation(program, uRadius)
        GLES30.glUniform1f(uRadiusLocation, radius)
        uAngleLocation = GLES30.glGetUniformLocation(program, uAngle)
        GLES30.glUniform1f(uAngleLocation, angle)
        super.drawFrame()
    }
}