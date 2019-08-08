package com.example.templechen.videoshaderdemo.filter

import android.content.Context
import android.opengl.GLES30
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.R

class BrightnessFilter(context: Context, oesTextureId: Int) : BaseFilter(context, oesTextureId) {

    companion object {
        const val uBrightness = "uBrightness"
    }

    private var uBrightnessLocation = -1
    private var brightness = 0.5f

    override fun initProgram() {
        vertexShader = GLUtils.loadShader(
            GLES30.GL_VERTEX_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.base_vertex_shader)
        )
        fragmentShader = GLUtils.loadShader(
            GLES30.GL_FRAGMENT_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.brightness_fragment_shader)
        )
        program = GLUtils.createProgram(vertexShader, fragmentShader)
    }

    override fun drawFrame() {
        uBrightnessLocation = GLES30.glGetUniformLocation(program, uBrightness)
        GLES30.glUniform1f(uBrightnessLocation, brightness)
        super.drawFrame()
    }
}