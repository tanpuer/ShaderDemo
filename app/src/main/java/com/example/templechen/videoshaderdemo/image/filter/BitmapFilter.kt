package com.example.templechen.videoshaderdemo.image.filter

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES30
import android.opengl.Matrix
import com.example.templechen.videoshaderdemo.GLUtils
import java.nio.FloatBuffer


open class BitmapFilter {

    companion object {
        private const val TAG = "ImageFilter"
        private const val aPosition = "aPosition"
        private const val uTextureMatrix = "uTextureMatrix"
        private const val aTextureCoordinate = "aTextureCoordinate"
        private const val uTextureSampler = "uTextureSampler"
        private const val uCoordMatrix = "uCoordMatrix"
    }

    private var aPositionLocation = -1
    private var uTextureMatrixLocation = -1
    private var aTextureCoordinateLocation = -1
    private var uTextureSamplerLocation = -1
    private var uCoordMatrixLocation = -1

    private var context: Context
    private var bitmap: Bitmap
    private var vertexShader = -1
    private var fragmentShader = -1
    private var program = -1
    private var floatBuffer: FloatBuffer
    protected var textureId = -1
    private var transformMatrix = floatArrayOf(
        1.0f, 0.0f, 0.0f, 0.0f,
        0.0f, -1.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 1.0f, 0.0f,
        0.0f, 1.0f, 0.0f, 1.0f
    )

    private var coordMatrix = FloatArray(16) { 0f }

    private val vertexShaderString = """
        attribute vec4 aPosition;
        uniform mat4 uTextureMatrix;
        uniform mat4 uCoordMatrix;
        attribute vec4 aTextureCoordinate;
        varying vec2 vTextureCoord;
        void main()
        {
            vTextureCoord = (uTextureMatrix * aTextureCoordinate).xy;
            gl_Position = uCoordMatrix * aPosition;
        }
    """

    private val fragmentShaderString = """
        precision mediump float;
        uniform sampler2D uTextureSampler;
        varying vec2 vTextureCoord;
        void main()
        {
            gl_FragColor = texture2D(uTextureSampler, vTextureCoord);
        }
    """

    constructor(context: Context, bitmap: Bitmap) {
        this.context = context
        this.bitmap = bitmap
        floatBuffer = GLUtils.createBuffer(GLUtils.vertexData)
    }

    fun initProgram() {
        vertexShader = GLUtils.loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderString)
        fragmentShader = GLUtils.loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderString)
        program = GLUtils.createProgram(vertexShader, fragmentShader)
        textureId = GLUtils.loadTexture(context, bitmap)
    }

    fun setSize(imageWidth: Int, imageHeight: Int, viewWidth: Int, viewHeight: Int) {
        val imageRatio = imageWidth * 1.0f / imageHeight
        val viewRatio = viewWidth * 1.0f / viewHeight
        if (imageRatio > viewRatio) {
            Matrix.setIdentityM(coordMatrix, 0)
            Matrix.scaleM(coordMatrix, 0, 1f, viewRatio / imageRatio, 1f)
        } else {
            Matrix.scaleM(coordMatrix, 0, imageRatio / viewRatio, 1f, 1f)
        }
    }

    fun drawFrame() {
        drawFrame(null)
    }

    fun drawFrame(bitmap: Bitmap?) {
        if (bitmap != null) {
            GLES30.glDeleteTextures(1, intArrayOf(textureId), 0)
            textureId = GLUtils.loadTexture(context, bitmap)
        }

        GLES30.glUseProgram(program)

        aPositionLocation = GLES30.glGetAttribLocation(program,
            aPosition
        )
        aTextureCoordinateLocation = GLES30.glGetAttribLocation(program,
            aTextureCoordinate
        )
        uTextureMatrixLocation = GLES30.glGetUniformLocation(program,
            uTextureMatrix
        )
        uTextureSamplerLocation = GLES30.glGetUniformLocation(program,
            uTextureSampler
        )
        uCoordMatrixLocation = GLES30.glGetUniformLocation(program,
            uCoordMatrix
        )

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId)

        GLES30.glUniform1i(uTextureSamplerLocation, 0)
        GLES30.glUniformMatrix4fv(uTextureMatrixLocation, 1, false, transformMatrix, 0)
        GLES30.glUniformMatrix4fv(uCoordMatrixLocation, 1, false, coordMatrix, 0)

        floatBuffer.position(0)
        GLES30.glEnableVertexAttribArray(aPositionLocation)
        GLES30.glVertexAttribPointer(aPositionLocation, 2, GLES30.GL_FLOAT, false, 16, floatBuffer)
        floatBuffer.position(2)
        GLES30.glEnableVertexAttribArray(aTextureCoordinateLocation)
        GLES30.glVertexAttribPointer(aTextureCoordinateLocation, 2, GLES30.GL_FLOAT, false, 16, floatBuffer)

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 6)

        GLES30.glDisableVertexAttribArray(aPositionLocation)
        GLES30.glDisableVertexAttribArray(aTextureCoordinateLocation)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
    }

    fun release() {
        GLES30.glDeleteProgram(program)
        program = 0
        GLES30.glDeleteShader(vertexShader)
        vertexShader = 0
        GLES30.glDeleteShader(fragmentShader)
        fragmentShader = 0
        floatBuffer.clear()
        GLES30.glDeleteTextures(1, intArrayOf(textureId), 0)
    }
}