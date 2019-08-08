package com.example.templechen.videoshaderdemo.gl.gesture

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class GestureRectView : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val paint = Paint()
    var rectPoints = mutableListOf(PointF(0f, 0f), PointF(0f, 0f), PointF(0f, 0f), PointF(0f, 0f))
    var scale = 1.0f
    var transformX = 0f
    var transformY = 0f
    var degress = 0

    private var videoWidth = 0
    private var videoHeight = 0
    private var viewWidth = 0
    private var viewHeight = 0

    init {
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
        paint.strokeWidth = 8f
        paint.color = Color.RED
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawLine(rectPoints[0].x, rectPoints[0].y, rectPoints[1].x, rectPoints[1].y, paint)
        canvas?.drawLine(rectPoints[1].x, rectPoints[1].y, rectPoints[2].x, rectPoints[2].y, paint)
        canvas?.drawLine(rectPoints[2].x, rectPoints[2].y, rectPoints[3].x, rectPoints[3].y, paint)
        canvas?.drawLine(rectPoints[3].x, rectPoints[3].y, rectPoints[0].x, rectPoints[0].y, paint)
    }

    fun setVideoSizeAndViewSize(
        videoWidth: Int,
        videoHeight: Int,
        viewWidth: Int,
        viewHeight: Int
    ) {
        this.videoWidth = videoWidth
        this.videoHeight = videoHeight
        this.viewWidth = viewWidth
        this.viewHeight = viewHeight
        setScaleRotateAndTransform()
    }

    fun sendScale(scale: Float) {
        this.scale = scale
        setScaleRotateAndTransform()
    }

    fun sendTransform(transformX: Float, transformY: Float) {
        this.transformX = transformX
        this.transformY = transformY
        setScaleRotateAndTransform()
    }

    fun setRotate(degrees: Int) {
        this.degress = degrees
        setScaleRotateAndTransform()
    }

    private fun setScaleRotateAndTransform() {
        val videoRatio = videoWidth * 1.0f / videoHeight
        val viewRatio = viewWidth * 1.0f / viewHeight
        val rotation = degress.toDouble() * Math.PI / 180
        val realWidth: Float
        val realHeight: Float
        if (videoRatio > viewRatio) {
            //横屏视频
            realWidth = viewWidth.toFloat() * scale
            realHeight = realWidth / videoRatio
        } else {
            realHeight = viewHeight.toFloat() * scale
            realWidth = realHeight * videoRatio
        }
        val centerX = viewWidth / 2f
        val centerY = viewHeight / 2f
        //1 scale
        rectPoints[0] = PointF((viewWidth - realWidth) / 2f, (viewHeight - realHeight) / 2f)
        rectPoints[1] = PointF((viewWidth + realWidth) / 2f, (viewHeight - realHeight) / 2f)
        rectPoints[2] = PointF((viewWidth + realWidth) / 2f, (viewHeight + realHeight) / 2f)
        rectPoints[3] = PointF((viewWidth - realWidth) / 2f, (viewHeight + realHeight) / 2f)
        //2 rotate
        rectPoints.forEachIndexed { index, pointF ->
            rectPoints[index] = PointF(
                (pointF.x - centerX) * cos(rotation).toFloat() - (pointF.y - centerY) * sin(rotation).toFloat() + centerX,
                (pointF.x - centerX) * sin(rotation).toFloat() + (pointF.y - centerY) * cos(rotation).toFloat() + centerY
            )
        }
        //3 transform
        rectPoints.forEachIndexed { index, pointF ->
            rectPoints[index] = PointF(pointF.x + transformX, pointF.y + transformY)
        }
        invalidate()
    }

}