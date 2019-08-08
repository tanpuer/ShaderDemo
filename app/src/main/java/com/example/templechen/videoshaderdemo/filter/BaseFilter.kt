package com.example.templechen.videoshaderdemo.filter

import android.content.Context
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLES30
import android.opengl.Matrix
import android.util.Log
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.R
import java.nio.FloatBuffer

open class BaseFilter {

    companion object {
        const val aPosition = "aPosition"
        const val uTextureMatrix = "uTextureMatrix"
        const val aTextureCoordinate = "aTextureCoordinate"
        const val uTextureSampler = "uTextureSampler"
        const val uMVPMatrix = "uMVPMatrix"
    }

    protected var context: Context
    protected var mOESTextureId: Int
    protected var floatBuffer: FloatBuffer
    protected var vertexShader: Int = -1
    protected var fragmentShader: Int = -1
    protected var program: Int = -1
    var transformMatrix = floatArrayOf(
        1.0f, 0.0f, 0.0f, 0.0f,
        0.0f, -1.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 1.0f, 0.0f,
        0.0f, 1.0f, 0.0f, 1.0f
    )
    var baseMVPMatrix = FloatArray(16) {0f}

    protected var aPositionLocation = -1
    protected var uTextureMatrixLocation = -1
    protected var aTextureCoordinateLocation = -1
    protected var uTextureSamplerLocation = -1
    protected var uMVPMatrixLocation = -1

    constructor(context: Context, oesTextureId: Int) {
        this.context = context
        mOESTextureId = oesTextureId
        floatBuffer = GLUtils.createBuffer(GLUtils.vertexData)
        Matrix.setIdentityM(baseMVPMatrix, 0)
    }

    open fun initProgram() {
        vertexShader = GLUtils.loadShader(
            GLES30.GL_VERTEX_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.base_vertex_shader)
        )
        fragmentShader = GLUtils.loadShader(
            GLES30.GL_FRAGMENT_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.base_fragment_shader)
        )
        program = GLUtils.createProgram(vertexShader, fragmentShader)
    }

    open fun drawFrame() {
        GLES30.glUseProgram(program)

        aPositionLocation = GLES30.glGetAttribLocation(program, aPosition)
        aTextureCoordinateLocation = GLES30.glGetAttribLocation(program, aTextureCoordinate)
        uTextureMatrixLocation = GLES30.glGetUniformLocation(program, uTextureMatrix)
        uTextureSamplerLocation = GLES30.glGetUniformLocation(program, uTextureSampler)
        uMVPMatrixLocation = GLES30.glGetUniformLocation(program, uMVPMatrix)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mOESTextureId)

        GLES30.glUniform1i(uTextureSamplerLocation, 0)
        GLES30.glUniformMatrix4fv(uTextureMatrixLocation, 1, false, transformMatrix, 0)
        GLES30.glUniformMatrix4fv(uMVPMatrixLocation, 1, false, baseMVPMatrix, 0)

        floatBuffer.position(0)
        GLES30.glEnableVertexAttribArray(aPositionLocation)
        GLES30.glVertexAttribPointer(aPositionLocation, 2, GLES30.GL_FLOAT, false, 16, floatBuffer)
        floatBuffer.position(2)
        GLES30.glEnableVertexAttribArray(aTextureCoordinateLocation)
        GLES30.glVertexAttribPointer(aTextureCoordinateLocation, 2, GLES30.GL_FLOAT, false, 16, floatBuffer)

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 6)

        GLES30.glDisableVertexAttribArray(aPositionLocation)
        GLES30.glDisableVertexAttribArray(aTextureCoordinateLocation)
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)
    }

    open fun drawOverlayFrame() {
        GLES30.glUseProgram(program)

        aPositionLocation = GLES30.glGetAttribLocation(program, aPosition)
        aTextureCoordinateLocation = GLES30.glGetAttribLocation(program, aTextureCoordinate)
        uTextureMatrixLocation = GLES30.glGetUniformLocation(program, uTextureMatrix)
        uTextureSamplerLocation = GLES30.glGetUniformLocation(program, uTextureSampler)
        uMVPMatrixLocation = GLES30.glGetUniformLocation(program, uMVPMatrix)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE1)
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mOESTextureId)

        GLES30.glUniform1i(uTextureSamplerLocation, 0)
        GLES30.glUniformMatrix4fv(uTextureMatrixLocation, 1, false, transformMatrix, 0)
        GLES30.glUniformMatrix4fv(uMVPMatrixLocation, 1, false, baseMVPMatrix, 0)

        floatBuffer.position(0)
        GLES30.glEnableVertexAttribArray(aPositionLocation)
        GLES30.glVertexAttribPointer(aPositionLocation, 2, GLES30.GL_FLOAT, false, 16, floatBuffer)
        floatBuffer.position(2)
        GLES30.glEnableVertexAttribArray(aTextureCoordinateLocation)
        GLES30.glVertexAttribPointer(aTextureCoordinateLocation, 2, GLES30.GL_FLOAT, false, 16, floatBuffer)

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 6)

        GLES30.glDisableVertexAttribArray(aPositionLocation)
        GLES30.glDisableVertexAttribArray(aTextureCoordinateLocation)
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)
    }

    fun setMVPMatrixScaleAndTransform(scaleX: Float, scaleY: Float, scrollX: Float, scrollY: Float) {
        Matrix.setIdentityM(baseMVPMatrix, 0)
        Matrix.scaleM(baseMVPMatrix, 0, scaleX, scaleY, 1.0f)
        Matrix.translateM(baseMVPMatrix, 0, scrollX, scrollY, 1.0f)
    }

    open fun release() {
        GLES30.glDeleteProgram(program)
        program = 0
        GLES30.glDeleteShader(vertexShader)
        vertexShader = 0
        GLES30.glDeleteShader(fragmentShader)
        fragmentShader = 0
        floatBuffer.clear()
    }

}