package com.example.templechen.videoshaderdemo.filter

import android.content.Context
import android.graphics.PointF
import android.opengl.GLES30
import android.opengl.Matrix
import android.util.Log
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.R
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class GestureFilter(context: Context, oesTextureId: Int) : BaseFilter(context, oesTextureId) {

    companion object {
        const val uCoordMatrix = "uCoordMatrix"
    }

    private var uCoordMatrixLocation = -1

    private var coordMatrix = FloatArray(16) { 0.0f }

    private var originScaleX = 1.0f
    private var originScaleY = 1.0f
    private var scaleX = 1.0f
    private var scaleY = 1.0f
    private var scrollX = 0.0f
    private var scrollY = 0.0f
    private var degrees = 0

    var overlayEnable = true

    override fun initProgram() {
        vertexShader = GLUtils.loadShader(
            GLES30.GL_VERTEX_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.gesture_vertex_shader)
        )
        fragmentShader = GLUtils.loadShader(
            GLES30.GL_FRAGMENT_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.gesture_fragment_shader)
        )
        program = GLUtils.createProgram(vertexShader, fragmentShader)
        Matrix.setIdentityM(coordMatrix, 0)
    }

    override fun drawFrame() {
        GLES30.glUseProgram(program)
        uCoordMatrixLocation = GLES30.glGetUniformLocation(program, uCoordMatrix)
        GLES30.glUniformMatrix4fv(uCoordMatrixLocation, 1, false, coordMatrix, 0)
        super.drawFrame()
    }

    override fun drawOverlayFrame() {
        GLES30.glUseProgram(program)
        uCoordMatrixLocation = GLES30.glGetUniformLocation(program, uCoordMatrix)
        GLES30.glUniformMatrix4fv(uCoordMatrixLocation, 1, false, coordMatrix, 0)
        super.drawOverlayFrame()
    }

    private fun setScaleAndTransform(
        scaleX: Float,
        scaleY: Float,
        scrollX: Float,
        scrollY: Float,
        degrees: Int
    ) {
        this.scaleX = scaleX
        this.scaleY = scaleY
        this.scrollX = scrollX
        this.scrollY = scrollY
        this.degrees = degrees
        Matrix.setIdentityM(coordMatrix, 0)
        Matrix.scaleM(coordMatrix, 0, scaleX * originScaleX, scaleY * originScaleY, 1.0f)
//        Matrix.rotateM(coordMatrix, 0, degrees.toFloat(), 0.0f, 0.0f, 1.0f)
        //单纯rotate不对，要重新算vbo
        Matrix.translateM(
            coordMatrix,
            0,
            scrollX / (scaleX * originScaleX),
            scrollY / (scaleY * originScaleY),
            1.0f
        )

        updateVertexCoord()
    }

    private fun updateVertexCoord() {
        val ratio = videoWidth * 1.0f / videoHeight
        val radian = -degrees * Math.PI / 180
        val vertexCoord =
            mutableListOf(PointF(1.0f, 1.0f), PointF(-1f, 1f), PointF(-1f, -1f), PointF(1f, -1f))
        vertexCoord.forEachIndexed { index, pointF ->
            vertexCoord[index] = PointF(
                (pointF.x) * cos(radian).toFloat() - (pointF.y / ratio) * sin(radian).toFloat(),
                ((pointF.x) * sin(radian).toFloat() + (pointF.y / ratio) * cos(radian).toFloat()) * ratio
            )
        }
        floatBuffer.clear()
        floatBuffer = GLUtils.createBuffer(
            floatArrayOf(
                vertexCoord[0].x, vertexCoord[0].y, 1f, 1f,
                vertexCoord[1].x, vertexCoord[1].y, 0f, 1f,
                vertexCoord[2].x, vertexCoord[2].y, 0f, 0f,
                vertexCoord[0].x, vertexCoord[0].y, 1f, 1f,
                vertexCoord[2].x, vertexCoord[2].y, 0f, 0f,
                vertexCoord[3].x, vertexCoord[3].y, 1f, 0f
            )
        )
    }

    fun setScale(scaleX: Float, scaleY: Float) {
        setScaleAndTransform(scaleX, scaleY, scrollX, scrollY, degrees)
    }

    fun setScroll(scrollX: Float, scrollY: Float) {
        setScaleAndTransform(scaleX, scaleY, scrollX, scrollY, degrees)
    }

    fun setDegrees(degrees: Int) {
        setScaleAndTransform(scaleX, scaleY, scrollX, scrollY, degrees)
    }

    private fun setOriginScale(originScaleX: Float, originScaleY: Float) {
        this.originScaleX = originScaleX
        this.originScaleY = originScaleY
        setScaleAndTransform(scaleX, scaleY, scrollX, scrollY, degrees)
    }


    private var videoWidth = 0
    private var videoHeight = 0
    private var viewWidth = 0
    private var viewHeight = 0
    fun setVideoAndViewSize(videoWidth: Int, videoHeight: Int, viewWidth: Int, viewHeight: Int) {
        this.videoWidth = videoWidth
        this.videoHeight = videoHeight
        this.viewWidth = viewWidth
        this.viewHeight = viewHeight

        if (videoWidth * 1.0f / videoHeight > viewWidth * 1.0f / viewHeight) {
            //横屏视频
            originScaleY = viewWidth * 1.0f / videoWidth * videoHeight / viewHeight
            originScaleX = 1.0f
        } else {
            //竖屏视频
            originScaleY = 1.0f
            originScaleX = viewHeight * 1.0f / videoHeight * videoWidth / viewWidth
        }
        setOriginScale(originScaleX, originScaleY)
    }

}