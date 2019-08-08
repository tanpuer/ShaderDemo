package com.example.templechen.videoshaderdemo.editor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.templechen.videoshaderdemo.gl.SimpleGLSurfaceView

class SimpleEditorView : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    companion object {
        private const val PAINT_WIDTH = 20
    }

    private var mPaint: Paint = Paint()
    private var mRect = Rect()
    private var mVideoViewWidth = 0f
    var simpleGlView: SimpleGLSurfaceView? = null

    init {
        mPaint.color = Color.RED
        mPaint.isAntiAlias = true
        mPaint.strokeWidth = PAINT_WIDTH.toFloat()
        mPaint.style = Paint.Style.STROKE
        mRect.set(0, 0, width, height)
    }

    fun setSize(videoViewWHeight: Float, videoViewWidth: Float) {
        mVideoViewWidth = videoViewWidth
        val width = (videoViewWHeight / 16f * 9f).toInt()
        val marginLeft = (videoViewWidth - width) / 2
//        mRect.set((width * 0.05).toInt(), (height * 0.05f).toInt(), (width * 0.95).toInt(), (height * 0.95).toInt())
        mRect.set(marginLeft.toInt(), 0, width + marginLeft.toInt(), videoViewWHeight.toInt())
        invalidate()
        simpleGlView?.setVideoEditorRect(mRect)
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        canvas?.drawRect(mRect, mPaint)
    }

    private var startX = 0f

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
            }
            MotionEvent.ACTION_MOVE -> {
                val endX = event.x
                var left = mRect.left
                var right = mRect.right
                left += (endX - startX).toInt()
                right += (endX - startX).toInt()
                if (left < 0) {
                    left = 0
                    right = mRect.width()
                }
                if (right > mVideoViewWidth) {
                    right = mVideoViewWidth.toInt()
                    left = (mVideoViewWidth - mRect.width()).toInt()
                }
                mRect.set(left, mRect.top, right, mRect.bottom)
                startX = endX
                invalidate()
                simpleGlView?.setVideoEditorRect(mRect)
            }
            MotionEvent.ACTION_UP -> {

            }
        }
        return true
    }

}