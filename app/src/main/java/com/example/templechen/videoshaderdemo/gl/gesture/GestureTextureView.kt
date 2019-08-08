package com.example.templechen.videoshaderdemo.gl.gesture

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import com.example.templechen.videoshaderdemo.gl.SimpleGLTextureView

class GestureTextureView : SimpleGLTextureView {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var gestureEnable = false
    private var mScrollGesture: GestureDetector
    private var mScaleGesture: ScaleGestureDetector

    init {
        mScaleGesture = ScaleGestureDetector(context, ScaleGestureConfirm(this))
        mScrollGesture = GestureDetector(context, SingleGestureConfirm(this))
    }

    private var canScroll = true
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!gestureEnable) {
            return false
        }
        when (event?.pointerCount) {
            1 -> {
                if (canScroll) {
                    mScrollGesture.onTouchEvent(event)
                }
            }
            2 -> {
                mScaleGesture.onTouchEvent(event)
            }
        }
        return true
    }

    private var firstSetVideoSize = false
    fun setVideoSize(width: Int, height: Int) {
        renderThread?.mHandler?.setVideoSize(width, height)
        if (firstSetVideoSize) {
            renderThread?.mHandler?.setOnScale(totalScale, totalScale)
            renderThread?.mHandler?.setOnScroll(totalScrollX * 2 / getWidth(), totalScrollY * 2 / getHeight())
            renderThread?.mHandler?.setOnRotate(degrees)
            renderThread?.mHandler?.setBackgroundColorChanged(colors)
        }
        firstSetVideoSize = true
    }

    private var totalScrollX = 0f
    private var totalScrollY = 0f
    fun setScroll(scrollX: Float, scrollY: Float) {
        totalScrollX += scrollX
        totalScrollY += scrollY
        renderThread?.mHandler?.setOnScroll(totalScrollX * 2 / width, totalScrollY * 2 / height)
    }

    private var totalScale = 1.0f
    fun setScale(scaleX: Float, scaleY: Float) {
        renderThread?.mHandler?.setOnScale(totalScale * scaleX, totalScale * scaleY)
    }

    fun setScaleEnd(totalScale: Float) {
        this.totalScale *= totalScale
    }

    private var degrees = 0
    fun setRotate(degrees: Int) {
        this.degrees = degrees
        renderThread?.mHandler?.setOnRotate(degrees)
    }

    private var colors = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
    fun setBackgroundColor(colors: FloatArray) {
        this.colors = colors
        renderThread?.mHandler?.setBackgroundColorChanged(colors)
    }

    private class SingleGestureConfirm(val gestureTextureView: GestureTextureView) :
        GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            gestureTextureView.setScroll(-distanceX, distanceY)
            return true
        }

    }

    private class ScaleGestureConfirm(val gestureTextureView: GestureTextureView) :
        ScaleGestureDetector.OnScaleGestureListener {
        override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
            gestureTextureView.canScroll = false
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector?) {
            if (detector != null) {
                gestureTextureView.setScaleEnd(detector.scaleFactor)
            }
            gestureTextureView.postDelayed({
                gestureTextureView.canScroll = true
            }, 150)
        }

        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            if (detector != null) {
                gestureTextureView.setScale(detector.scaleFactor, detector.scaleFactor)
            }
            return false
        }

    }
}