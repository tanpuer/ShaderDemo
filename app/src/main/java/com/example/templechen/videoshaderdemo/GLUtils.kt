package com.example.templechen.videoshaderdemo

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.*
import android.opengl.GLUtils
import android.util.Log
import android.view.WindowManager
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.RuntimeException
import java.lang.StringBuilder
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10

class GLUtils {

    companion object {

        fun createOESTextureObject(): Int {
            var tex = IntArray(1)
            GLES30.glGenTextures(1, tex, 0)
            GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex[0])
            GLES30.glTexParameteri(
                GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES30.GL_TEXTURE_MAG_FILTER,
                GLES30.GL_LINEAR
            )
            GLES30.glTexParameteri(
                GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES30.GL_TEXTURE_MIN_FILTER,
                GLES30.GL_NEAREST
            )
            GLES30.glTexParameteri(
                GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES30.GL_TEXTURE_WRAP_S,
                GLES30.GL_CLAMP_TO_EDGE
            )
            GLES30.glTexParameteri(
                GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES30.GL_TEXTURE_WRAP_T,
                GLES30.GL_CLAMP_TO_EDGE
            )
            GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)
            return tex[0]
        }

        fun readShaderFromResource(context: Context, resId: Int): String {
            var builder = StringBuilder()
            var inputStream: InputStream? = null
            var inputStreamReader: InputStreamReader? = null
            var bufferedReader: BufferedReader? = null

            try {
                inputStream = context.resources.openRawResource(resId)
                inputStreamReader = InputStreamReader(inputStream)
                bufferedReader = BufferedReader(inputStreamReader)
                var line: String? = bufferedReader.readLine()
                while (line != null && line.isNotEmpty()) {
                    builder.append(line).append("\n")
                    line = bufferedReader.readLine()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                inputStream?.close()
                inputStreamReader?.close()
                bufferedReader?.close()
            }
            return builder.toString()
        }

        fun createBuffer(vertexData: FloatArray): FloatBuffer {
            val floatBuffer = ByteBuffer.allocateDirect(vertexData.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
            floatBuffer.put(vertexData, 0, vertexData.size).position(0)
            return floatBuffer
        }

        fun loadShader(type: Int, shaderSource: String): Int {
            val shader = GLES30.glCreateShader(type)
            if (shader == 0) {
                throw RuntimeException("create shader failed $type")
            }
            GLES30.glShaderSource(shader, shaderSource)
            GLES30.glCompileShader(shader)
            val compiled = intArrayOf(0)
            GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0)
            if (compiled[0] == GLES30.GL_FALSE) {
                Log.e("Shader Compile Error: ", GLES30.glGetShaderInfoLog(shader))
                GLES30.glDeleteShader(shader)
            }
            return shader
        }

        fun createProgram(vertexShader: Int, fragmentShader: Int): Int {
            val program = GLES30.glCreateProgram()
            if (program == 0) {
                throw RuntimeException("create gl program failed")
            }
            GLES30.glAttachShader(program, vertexShader)
            GLES30.glAttachShader(program, fragmentShader)
            GLES30.glLinkProgram(program)
            val compiled = IntArray(1)
            GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, compiled, 0)
            if (compiled[0] == GLES30.GL_FALSE) {
                Log.e("Program Link Error: ", GLES30.glGetProgramInfoLog(program));
                GLES30.glDeleteProgram(program)
            }
            return program
        }

        val vertexData = floatArrayOf(
            1f, 1f, 1f, 1f,
            -1f, 1f, 0f, 1f,
            -1f, -1f, 0f, 0f,
            1f, 1f, 1f, 1f,
            -1f, -1f, 0f, 0f,
            1f, -1f, 1f, 0f
        )

        fun loadTexture(context: Context, resId: Int): Int {
            val textureObjectIds = IntArray(1)
            GLES30.glGenTextures(1, textureObjectIds, 0)
            if (textureObjectIds[0] == 0) {
                return 0
            }
            val options = BitmapFactory.Options()
            options.inScaled = false
            val bitmap = BitmapFactory.decodeResource(context.resources, resId, options)
            if (bitmap == null) {
                GLES30.glDeleteTextures(1, textureObjectIds, 0)
                return 0
            }
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureObjectIds[0])
            GLES30.glTexParameteri(
                GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MAG_FILTER,
                GLES30.GL_LINEAR
            )
            GLES30.glTexParameteri(
                GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MIN_FILTER,
                GLES30.GL_LINEAR_MIPMAP_LINEAR
            )
            GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0)
            GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D)
            bitmap.recycle()
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
            return textureObjectIds[0]
        }

        fun loadTexture(context: Context, bitmap: Bitmap): Int {
            val textureObjectIds = IntArray(1)
            GLES30.glGenTextures(1, textureObjectIds, 0)
            if (textureObjectIds[0] == 0) {
                return 0
            }
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureObjectIds[0])
            GLES30.glTexParameteri(
                GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MAG_FILTER,
                GLES30.GL_LINEAR
            )
            GLES30.glTexParameteri(
                GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MIN_FILTER,
                GLES30.GL_LINEAR_MIPMAP_LINEAR
            )
            GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0)
            GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D)
            bitmap.recycle()
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
            return textureObjectIds[0]
        }

        val waterMarkVertexData = floatArrayOf(
            1f, 1f, 1f, 1f,
            0.7f, 1f, 0f, 1f,
            0.7f, 0.7f, 0f, 0f,
            1f, 1f, 1f, 1f,
            0.7f, 0.7f, 0f, 0f,
            1.0f, 0.7f, 1f, 0f
        )

        fun getDisplayRefreshNsec(activity: Activity): Long {
            val display =
                (activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
            val displayFps = display.refreshRate.toDouble()
            val refreshNs = Math.round(1000000000L / displayFps)
            Log.d("getDisplayRefreshNsec", "refresh rate is $displayFps fps --> $refreshNs ns")
            return refreshNs
        }

        /**
         * Checks to see if a GLES error has been raised.
         */
        fun checkGlError(op: String) {
            val error = GLES20.glGetError()
            if (error != GLES20.GL_NO_ERROR) {
                val msg = op + ": glError 0x" + Integer.toHexString(error)
                Log.e("checkGlError", msg)
//                throw RuntimeException(msg)
            }
        }

        fun loadBitmap(resId: Int, context: Context): Bitmap? {
            val options = BitmapFactory.Options()
            options.inScaled = false
            return BitmapFactory.decodeResource(context.resources, resId, options)
        }

    }

}