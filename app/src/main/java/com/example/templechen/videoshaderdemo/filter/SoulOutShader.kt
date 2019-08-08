package com.example.templechen.videoshaderdemo.filter

import android.content.Context
import android.opengl.GLES11Ext
import android.opengl.GLES30
import android.opengl.Matrix
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.R

class SoulOutShader(context: Context, oesTextureId: Int) : BaseFilter(context, oesTextureId) {

    companion object {
        private const val MAX_FRAMES = 40
        private const val FRAMES_INTERVAL = 25
        private const val uMvpMatrix = "uMvpMatrix"
        private const val uAlpha = "uAlpha"
    }

    private var mFrames = 0
    private var uMvpMatrixLocation = -1
    private var mvpMatrix = FloatArray(16) { 0f }
    private var uAlphaLocation = -1
    private var alpha = 1f

    override fun initProgram() {
        vertexShader = GLUtils.loadShader(
            GLES30.GL_VERTEX_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.soul_out_vertex_fragment)
        )
        fragmentShader = GLUtils.loadShader(
            GLES30.GL_FRAGMENT_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.soul_out_fragment_shader)
        )
        program = GLUtils.createProgram(vertexShader, fragmentShader)
    }

    override fun drawFrame() {
        GLES30.glEnable(GLES30.GL_BLEND)
        GLES30.glUseProgram(program)
        Matrix.setIdentityM(mvpMatrix, 0)
        alpha = 1f
        uMvpMatrixLocation = GLES30.glGetUniformLocation(program, uMvpMatrix)
        GLES30.glUniformMatrix4fv(uMvpMatrixLocation, 1, false, mvpMatrix, 0)
        uAlphaLocation = GLES30.glGetUniformLocation(program, uAlpha)
        GLES30.glUniform1f(uAlphaLocation, alpha)

        aPositionLocation = GLES30.glGetAttribLocation(program, aPosition)
        aTextureCoordinateLocation = GLES30.glGetAttribLocation(program, aTextureCoordinate)
        uTextureMatrixLocation = GLES30.glGetUniformLocation(program, uTextureMatrix)
        uTextureSamplerLocation = GLES30.glGetUniformLocation(program, uTextureSampler)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mOESTextureId)

        GLES30.glUniform1i(uTextureSamplerLocation, 0)
        GLES30.glUniformMatrix4fv(uTextureMatrixLocation, 1, false, transformMatrix, 0)

        floatBuffer.position(0)
        GLES30.glEnableVertexAttribArray(aPositionLocation)
        GLES30.glVertexAttribPointer(aPositionLocation, 2, GLES30.GL_FLOAT, false, 16, floatBuffer)
        floatBuffer.position(2)
        GLES30.glEnableVertexAttribArray(aTextureCoordinateLocation)
        GLES30.glVertexAttribPointer(
            aTextureCoordinateLocation,
            2,
            GLES30.GL_FLOAT,
            false,
            16,
            floatBuffer
        )

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 6)

        mFrames++
        if (mFrames > MAX_FRAMES) {
            mFrames = 0
        }
        if (mFrames <= FRAMES_INTERVAL) {
            val scale = mFrames * 1.0f / MAX_FRAMES + 1f
            Matrix.scaleM(mvpMatrix, 0, scale, scale, scale)
            GLES30.glUniformMatrix4fv(uMvpMatrixLocation, 1, false, mvpMatrix, 0)
            alpha = mFrames * 0.6f / MAX_FRAMES
            GLES30.glUniform1f(uAlphaLocation, alpha)
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 6)
        }

        GLES30.glDisable(GLES30.GL_BLEND)
        GLES30.glDisableVertexAttribArray(aPositionLocation)
        GLES30.glDisableVertexAttribArray(aTextureCoordinateLocation)
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)
    }
}