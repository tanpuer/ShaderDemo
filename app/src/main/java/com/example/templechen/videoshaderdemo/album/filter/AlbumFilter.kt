package com.example.templechen.videoshaderdemo.album.filter

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES30
import android.opengl.Matrix
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.R
import com.example.templechen.videoshaderdemo.image.creator.BitmapCreatorFactory
import com.example.templechen.videoshaderdemo.image.creator.IBitmapCreator
import java.nio.FloatBuffer

open class AlbumFilter {

    companion object {
        const val TAG = "AlbumFilter"
        const val aPosition = "aPosition"
        const val uTextureMatrix = "uTextureMatrix"
        const val aTextureCoordinate = "aTextureCoordinate"
        const val uTextureSampler = "uTextureSampler"
        const val uMVPMatrix = "uMVPMatrix"
    }

    //default times, about 4s
    var times = 240
    var initedProgram = false
    private var resId = -1

    var baseMVPMatrix = FloatArray(16) { 0f }
    var transformMatrix = floatArrayOf(
        1.0f, 0.0f, 0.0f, 0.0f,
        0.0f, -1.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 1.0f, 0.0f,
        0.0f, 1.0f, 0.0f, 1.0f
    )
    protected var floatBuffer: FloatBuffer
    protected var vertexShader: Int = -1
    protected var fragmentShader: Int = -1
    protected var program: Int = -1
    protected var context: Context
    protected var textureId: Int = -1
    protected var bitmap: Bitmap? = null
    protected var viewWidth = -1
    protected var viewHeight = -1
    protected var bitmapWidth = -1
    protected var bitmapHeight = -1

    protected var aPositionLocation = -1
    protected var uTextureMatrixLocation = -1
    protected var aTextureCoordinateLocation = -1
    protected var uTextureSamplerLocation = -1
    protected var uMVPMatrixLocation = -1

    protected var bitmapCreator: IBitmapCreator? = null
    protected var isGif = false
    protected val refreshRate = 60
    protected var currentIndex = 0

    constructor(context: Context) {
        this.context = context
        Matrix.setIdentityM(baseMVPMatrix, 0)
        floatBuffer = GLUtils.createBuffer(GLUtils.vertexData)
    }

    constructor(context: Context, resId: Int) {
        this.context = context
        Matrix.setIdentityM(baseMVPMatrix, 0)
        floatBuffer = GLUtils.createBuffer(GLUtils.vertexData)
        this.resId = resId
    }

    constructor(context: Context, resId: Int, isGif: Boolean) {
        this.context = context
        Matrix.setIdentityM(baseMVPMatrix, 0)
        floatBuffer = GLUtils.createBuffer(GLUtils.vertexData)
        this.resId = resId
        this.isGif = isGif
    }

    open fun initFilter() {
        if (initedProgram) {
            return
        }
        initProgram()
        initTexture()
        initedProgram = true
    }

    protected open fun initProgram() {
        vertexShader = GLUtils.loadShader(
            GLES30.GL_VERTEX_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.album_vertex_shader)
        )
        fragmentShader = GLUtils.loadShader(
            GLES30.GL_FRAGMENT_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.album_fragment_shader)
        )
        program = GLUtils.createProgram(vertexShader, fragmentShader)
    }

    protected open fun initTexture() {
        bitmapCreator = BitmapCreatorFactory.newImageCreator(this.resId, if (isGif) BitmapCreatorFactory.TYPE_GIF else BitmapCreatorFactory.TYPE_IMAGE, context)
        bitmap = bitmapCreator!!.generateBitmap()
        textureId = if (bitmap != null) {
            bitmapWidth = bitmapCreator!!.getIntrinsicWidth()
            bitmapHeight = bitmapCreator!!.getIntrinsicHeight()
            GLUtils.loadTexture(context, bitmap!!)
        } else {
            GLUtils.loadTexture(context, R.drawable.testjpg)
        }
        updateMVPMatrix()
    }

    open fun setViewSize(width: Int, height: Int) {
        viewWidth = width
        viewHeight = height
        updateMVPMatrix()
    }

    open fun drawFrame() {
        if (isGif) {
            if (textureId >= 0) {
                GLES30.glDeleteTextures(1, intArrayOf(textureId), 0)
            }
            bitmap = bitmapCreator!!.seekToFrameAndGet((currentIndex * 16.6667f * bitmapCreator!!.framesCount() / bitmapCreator!!.getDuration()).toInt())
            bitmap?.let {
                textureId = GLUtils.loadTexture(context, bitmap!!)
            }
        }

        GLES30.glUseProgram(program)

        aPositionLocation = GLES30.glGetAttribLocation(program, aPosition)
        aTextureCoordinateLocation = GLES30.glGetAttribLocation(program, aTextureCoordinate)
        uTextureMatrixLocation = GLES30.glGetUniformLocation(program, uTextureMatrix)
        uTextureSamplerLocation = GLES30.glGetUniformLocation(program, uTextureSampler)
        uMVPMatrixLocation = GLES30.glGetUniformLocation(program, uMVPMatrix)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId)

        GLES30.glUniform1i(uTextureSamplerLocation, 0)
        GLES30.glUniformMatrix4fv(uTextureMatrixLocation, 1, false, transformMatrix, 0)
        GLES30.glUniformMatrix4fv(uMVPMatrixLocation, 1, false, baseMVPMatrix, 0)

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

        GLES30.glDisableVertexAttribArray(aPositionLocation)
        GLES30.glDisableVertexAttribArray(aTextureCoordinateLocation)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)

        currentIndex++
    }

    open fun release() {
        reset()
        bitmapCreator?.recycle()
    }

    open fun reset() {
        if (textureId >= 0) {
            GLES30.glDeleteTextures(1, intArrayOf(textureId), 0)
            textureId = -1
        }
        GLES30.glDeleteProgram(program)
        program = 0
        GLES30.glDeleteShader(vertexShader)
        vertexShader = 0
        GLES30.glDeleteShader(fragmentShader)
        floatBuffer.clear()
        initedProgram = false
        currentIndex = 0
    }

    protected var originScaleX = 1f
    protected var originScaleY = 1f
    private fun updateMVPMatrix() {
        if (viewHeight > 0 && viewHeight > 0 && bitmapWidth > 0 && bitmapHeight > 0) {
            Matrix.setIdentityM(baseMVPMatrix, 0)
            val viewRatio = viewWidth * 1.0f / viewHeight
            val bitmapRatio = bitmapWidth * 1.0f / bitmapHeight
            if (false) {
                //crop
                if (viewRatio > bitmapRatio) {
                    //scale y
                    originScaleY = viewRatio / bitmapRatio
                    Matrix.scaleM(baseMVPMatrix, 0, 1f, viewRatio / bitmapRatio, 1f)
                } else {
                    //scale x
                    originScaleX = bitmapRatio / viewRatio
                    Matrix.scaleM(baseMVPMatrix, 0, bitmapRatio / viewRatio, 1f, 1f)
                }
            } else {
                //center
                if (viewRatio > bitmapRatio) {
                    //scale x
                    originScaleX = bitmapRatio / viewRatio
                    Matrix.scaleM(baseMVPMatrix, 0, bitmapRatio / viewRatio, 1f, 1f)
                } else {
                    //scale y
                    originScaleY = viewRatio / bitmapRatio
                    Matrix.scaleM(baseMVPMatrix, 0, 1f, viewRatio / bitmapRatio, 1f)
                }
            }
        }
    }

}