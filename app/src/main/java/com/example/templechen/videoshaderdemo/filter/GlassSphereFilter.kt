package com.example.templechen.videoshaderdemo.filter

import android.content.Context
import android.opengl.GLES30
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.R

class GlassSphereFilter(context: Context, oesTextureId: Int) : BaseFilter(context, oesTextureId) {

    companion object {
        private const val uCenter = "uCenter"
        private const val uRadius = "uRadius"
        private const val uAspectRatio = "uAspectRatio"
        private const val uRefractiveIndex = "uRefractiveIndex"

        private val center = floatArrayOf(0.5f, 0.5f)
        private const val radius = 0.5f
        private const val aspectRatio = 1.0f
        private const val refractiveIndex = 0.7f
    }

    private var uCenterLocation = -1
    private var uRadiusLocation = -1
    private var uAspectRatioLocation = -1
    private var uRefractiveIndexLocation = -1

    override fun initProgram() {
        vertexShader = GLUtils.loadShader(
            GLES30.GL_VERTEX_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.glass_sphere_vertex_shader)
        )
        fragmentShader = GLUtils.loadShader(
            GLES30.GL_FRAGMENT_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.glass_sphere_fragment_shader)
        )
        program = GLUtils.createProgram(vertexShader, fragmentShader)
    }

    override fun drawFrame() {
        uCenterLocation = GLES30.glGetUniformLocation(program, uCenter)
        uRadiusLocation = GLES30.glGetUniformLocation(program, uRadius)
        uAspectRatioLocation = GLES30.glGetUniformLocation(program, uAspectRatio)
        uRefractiveIndexLocation = GLES30.glGetUniformLocation(program, uRefractiveIndex)

        GLES30.glUniform2fv(uCenterLocation, 1, center, 0)
        GLES30.glUniform1f(uRadiusLocation, radius)
        GLES30.glUniform1f(uAspectRatioLocation, aspectRatio)
        GLES30.glUniform1f(uRefractiveIndexLocation, refractiveIndex)

        super.drawFrame()
    }
}