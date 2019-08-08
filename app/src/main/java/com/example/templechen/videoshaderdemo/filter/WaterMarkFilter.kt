package com.example.templechen.videoshaderdemo.filter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.opengl.GLES30
import android.opengl.Matrix
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.R
import java.nio.FloatBuffer

class WaterMarkFilter(context: Context, oesTextureId: Int) : BaseFilter(context, oesTextureId) {

    companion object {
        const val aWaterMarkPosition = "aWaterMarkPosition"
        const val uWaterMarkMatrix = "uWaterMarkMatrix"
        const val aWaterMarkTextureCoord = "aWaterMarkTextureCoord"
        const val uWaterMarkTextureSampler = "uWaterMarkTextureSampler"
    }

    private var waterMarkTextureId: Int = -1
    private lateinit var waterMarkFloatBuffer: FloatBuffer
    private var waterMarkVertexShader = -1
    private var waterMarkFragmentShader = -1
    private var waterMarkProgram = -1

    private var aWaterMarkPositionLocation = -1
    private var uWaterMarkMatrixLocation = -1
    private var aWaterMarkTextureCoordLocation = -1
    private var uWaterMarkTextureSamplerLocation = -1

    private var times = 0

    //custom watermark bitmap
    var customWaterMarkBitmap: Bitmap? = null

    fun resetWaterMarkRect(rectF: RectF?) {
        if (rectF != null) {
            waterMarkFloatBuffer.clear()
            val vertexData = floatArrayOf(
                rectF.right, rectF.top, 1f, 1f,
                rectF.left, rectF.top, 0f, 1f,
                rectF.left, rectF.bottom, 0f, 0f,
                rectF.right, rectF.top, 1f, 1f,
                rectF.left, rectF.bottom, 0f, 0f,
                rectF.right, rectF.bottom, 1f, 0f
            )
            waterMarkFloatBuffer = GLUtils.createBuffer(vertexData)
        }
    }

    override fun initProgram() {
        super.initProgram()
        waterMarkTextureId = GLUtils.loadTexture(context, R.drawable.drawer_amino_logo)
        waterMarkFloatBuffer = GLUtils.createBuffer(GLUtils.waterMarkVertexData)
        waterMarkVertexShader = GLUtils.loadShader(
            GLES30.GL_VERTEX_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.water_mark_vertex_shader)
        )
        waterMarkFragmentShader = GLUtils.loadShader(
            GLES30.GL_FRAGMENT_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.water_mark_fragment_shader)
        )
        waterMarkProgram = GLUtils.createProgram(waterMarkVertexShader, waterMarkFragmentShader)
    }

    override fun drawFrame() {
        super.drawFrame()

        if (customWaterMarkBitmap == null) {
            if (times <= 10) {
                GLES30.glDeleteTextures(1, intArrayOf(waterMarkTextureId), 0)
                waterMarkTextureId = GLUtils.loadTexture(context, R.drawable.ic_amino_plus_text_icon)
                times++
            } else {
                GLES30.glDeleteTextures(1, intArrayOf(waterMarkTextureId), 0)
                waterMarkTextureId = GLUtils.loadTexture(context, R.drawable.drawer_amino_logo)
                times++
            }
            if (times == 20) {
                times = 0
            }
        } else {
            GLES30.glDeleteTextures(1, intArrayOf(waterMarkTextureId), 0)
            waterMarkTextureId = GLUtils.loadTexture(context, customWaterMarkBitmap!!)
        }

        GLES30.glUseProgram(waterMarkProgram)
        //water mark
        aWaterMarkPositionLocation = GLES30.glGetAttribLocation(waterMarkProgram, aWaterMarkPosition)
        uWaterMarkMatrixLocation = GLES30.glGetUniformLocation(waterMarkProgram, uWaterMarkMatrix)
        aWaterMarkTextureCoordLocation = GLES30.glGetAttribLocation(waterMarkProgram, aWaterMarkTextureCoord)
        uWaterMarkTextureSamplerLocation = GLES30.glGetUniformLocation(waterMarkProgram, uWaterMarkTextureSampler)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, waterMarkTextureId)

        GLES30.glUniform1i(uWaterMarkTextureSamplerLocation, 0)
        GLES30.glUniformMatrix4fv(
            uWaterMarkMatrixLocation,
            1,
            false,
            transformMatrix,
            0
        )

        waterMarkFloatBuffer.position(0)
        GLES30.glEnableVertexAttribArray(aWaterMarkPositionLocation)
        GLES30.glVertexAttribPointer(aWaterMarkPositionLocation, 2, GLES30.GL_FLOAT, false, 16, waterMarkFloatBuffer)
        waterMarkFloatBuffer.position(2)
        GLES30.glEnableVertexAttribArray(aWaterMarkTextureCoordLocation)
        GLES30.glVertexAttribPointer(
            aWaterMarkTextureCoordLocation,
            2,
            GLES30.GL_FLOAT,
            false,
            16,
            waterMarkFloatBuffer
        )

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 6)

        GLES30.glDisableVertexAttribArray(aWaterMarkPositionLocation)
        GLES30.glDisableVertexAttribArray(aWaterMarkTextureCoordLocation)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
    }

    override fun release() {
        super.release()
        GLES30.glDeleteProgram(waterMarkProgram)
        waterMarkProgram = 0
        GLES30.glDeleteShader(waterMarkVertexShader)
        waterMarkVertexShader = 0
        GLES30.glDeleteShader(waterMarkFragmentShader)
        waterMarkFragmentShader = 0
        waterMarkFloatBuffer.clear()
        GLES30.glDeleteTextures(1, intArrayOf(waterMarkTextureId), 0)
    }
}