package com.example.templechen.videoshaderdemo.filter

import android.content.Context
import android.graphics.RectF
import android.opengl.GLES30
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.R

class ScalableFilter(context: Context, oesTextureId: Int) : BaseFilter(context, oesTextureId) {

    companion object {
        private const val uRotate = "uRotate"
    }

    private var uRotateLocation = -1
    private var rotate = false

    override fun initProgram() {
        super.initProgram()
        vertexShader = GLUtils.loadShader(
            GLES30.GL_VERTEX_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.scalable_vertex_shader)
        )
        fragmentShader = GLUtils.loadShader(
            GLES30.GL_FRAGMENT_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.scalable_fragment_shader)
        )
        program = GLUtils.createProgram(vertexShader, fragmentShader)
    }

    private fun setVertexData(glCoordRectF: RectF, textureCoordRecF: RectF) {
        val vertexData = floatArrayOf(
            glCoordRectF.right, glCoordRectF.top, textureCoordRecF.right, textureCoordRecF.top,
            glCoordRectF.left, glCoordRectF.top, textureCoordRecF.left, textureCoordRecF.top,
            glCoordRectF.left, glCoordRectF.bottom, textureCoordRecF.left, textureCoordRecF.bottom,
            glCoordRectF.right, glCoordRectF.top, textureCoordRecF.right, textureCoordRecF.top,
            glCoordRectF.left, glCoordRectF.bottom, textureCoordRecF.left, textureCoordRecF.bottom,
            glCoordRectF.right, glCoordRectF.bottom, textureCoordRecF.right, textureCoordRecF.bottom
        )
        floatBuffer = GLUtils.createBuffer(vertexData)
    }

    fun setVertexData(vertexArray: FloatArray) {
        if (vertexArray.size == 8) {
            val glCoordRectF = RectF(vertexArray[0], vertexArray[1], vertexArray[2], vertexArray[3])
            val textureCoordRecF = RectF(vertexArray[4], vertexArray[5], vertexArray[6], vertexArray[7])
            setVertexData(glCoordRectF, textureCoordRecF)
        }
    }

    override fun drawFrame() {
        GLES30.glUseProgram(program)
        uRotateLocation = GLES30.glGetUniformLocation(program, uRotate)
        GLES30.glUniform1f(uRotateLocation, if (rotate) 1f else -1f)
        super.drawFrame()
    }

    fun rotate(doRotate: Boolean) {
        rotate = doRotate
    }

}