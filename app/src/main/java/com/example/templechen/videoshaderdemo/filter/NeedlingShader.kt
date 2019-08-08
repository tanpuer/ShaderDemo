package com.example.templechen.videoshaderdemo.filter

import android.content.Context
import android.opengl.GLES30
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.R

class NeedlingShader(context: Context, oesTextureId: Int) : BaseFilter(context, oesTextureId) {

    companion object {
        private const val uScanLineJitter = "uScanLineJitter"
        private const val uColorDrift = "uColorDrift"
    }

    private val mMaxFrames = 8
    private val mDriftSequence =
        floatArrayOf(0f, 0.03f, 0.032f, 0.035f, 0.03f, 0.032f, 0.031f, 0.029f, 0.025f)
    private val mJitterSequence =
        floatArrayOf(0f, 0.03f, 0.01f, 0.02f, 0.05f, 0.055f, 0.03f, 0.02f, 0.025f)
    private val mThreshHoldSequence =
        floatArrayOf(1.0f, 0.965f, 0.9f, 0.9f, 0.9f, 0.6f, 0.8f, 0.5f, 0.5f)

    private var uScanLineJitterLocation = -1
    private var uColorDriftLocation = -1
    private var mFrame = 0

    override fun initProgram() {
        vertexShader = GLUtils.loadShader(
            GLES30.GL_VERTEX_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.needling_vertex_shader)
        )
        fragmentShader = GLUtils.loadShader(
            GLES30.GL_FRAGMENT_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.needing_fragment_shader)
        )
        program = GLUtils.createProgram(vertexShader, fragmentShader)
    }

    override fun drawFrame() {
        GLES30.glUseProgram(program)
        mFrame++
        if (mFrame > mMaxFrames) {
            mFrame = 0
        }
        uScanLineJitterLocation = GLES30.glGetUniformLocation(program, uScanLineJitter)
        GLES30.glUniform2fv(
            uScanLineJitterLocation,
            1,
            floatArrayOf(mJitterSequence[mFrame], mThreshHoldSequence[mFrame]),
            0
        )
        uColorDriftLocation = GLES30.glGetUniformLocation(program, uColorDrift)
        GLES30.glUniform1f(uColorDriftLocation, mDriftSequence[mFrame])
        super.drawFrame()
    }
}