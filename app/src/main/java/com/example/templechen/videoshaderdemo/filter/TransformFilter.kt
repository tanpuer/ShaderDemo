package com.example.templechen.videoshaderdemo.filter

import android.content.Context
import android.opengl.GLES30
import android.opengl.Matrix
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.R

class TransformFilter(context: Context, oesTextureId: Int) : BaseFilter(context, oesTextureId) {

    companion object {
        const val uCoordMatrix = "uCoordMatrix"
    }

    private var uCoordMatrixLocation = -1

    private var coordMatrix = FloatArray(16) { 0.0f }

    private var matrix = FloatArray(16) { 0f }
    private var degree = 0f

    override fun initProgram() {
        vertexShader = GLUtils.loadShader(
            GLES30.GL_VERTEX_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.transform_vertex_shader)
        )
        fragmentShader = GLUtils.loadShader(
            GLES30.GL_FRAGMENT_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.transform_fragment_shader)
        )
        program = GLUtils.createProgram(vertexShader, fragmentShader)
        Matrix.setIdentityM(coordMatrix, 0)
    }

    override fun drawFrame() {
        initMatrix()
        Matrix.multiplyMM(coordMatrix, 0, matrix, 0, coordMatrix, 0)
        uCoordMatrixLocation = GLES30.glGetUniformLocation(program, GestureFilter.uCoordMatrix)
        GLES30.glUniformMatrix4fv(uCoordMatrixLocation, 1, false, coordMatrix, 0)
        super.drawFrame()
    }

    private fun initMatrix() {
        if (degree >= 360f) {
            degree = 0f
        }
        Matrix.setIdentityM(matrix, 0)
        Matrix.rotateM(matrix, 0, degree.toFloat(), 0f, 0f, 1f)
        Matrix.setIdentityM(coordMatrix, 0)
        degree += 1f
    }

}