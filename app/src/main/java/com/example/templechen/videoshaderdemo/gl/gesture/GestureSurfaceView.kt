package com.example.templechen.videoshaderdemo.gl.gesture

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import com.example.templechen.videoshaderdemo.gl.SimpleGLSurfaceView
import com.almeros.android.multitouch.ShoveGestureDetector
import com.almeros.android.multitouch.MoveGestureDetector
import com.almeros.android.multitouch.RotateGestureDetector


class GestureSurfaceView : SimpleGLSurfaceView {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    var gestureEnable = false
    var mGestureListener: IGestureListener? = null

    private var mScaleDetector: ScaleGestureDetector? = null
    private var mRotateDetector: RotateGestureDetector? = null
    private var mMoveDetector: MoveGestureDetector? = null
    private var mShoveDetector: ShoveGestureDetector? = null

    init {
        // Setup Gesture Detectors
        mScaleDetector = ScaleGestureDetector(context.applicationContext, ScaleListener(this))
        mRotateDetector = RotateGestureDetector(context.applicationContext, RotateListener(this))
        mMoveDetector = MoveGestureDetector(context.applicationContext, MoveListener(this))
        mShoveDetector = ShoveGestureDetector(context.applicationContext, ShoveListener(this))
    }

    private var canScroll = true
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        mScaleDetector?.onTouchEvent(event)
        mRotateDetector?.onTouchEvent(event)
        mMoveDetector?.onTouchEvent(event)
        mShoveDetector?.onTouchEvent(event)
        return true
    }

    private var firstSetVideoSize = false
    fun setVideoSize(width: Int, height: Int) {
        renderThread?.mHandler?.setVideoSize(width, height)
        if (firstSetVideoSize) {
            renderThread?.mHandler?.setOnScale(totalScale, totalScale)
            renderThread?.mHandler?.setOnScroll(
                totalScrollX * 2 / getWidth(),
                totalScrollY * 2 / getHeight()
            )
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
        mGestureListener?.onTransform(totalScrollX, -totalScrollY)
    }

    private var totalScale = 1.0f
    fun setScale(scaleX: Float, scaleY: Float) {
        renderThread?.mHandler?.setOnScale(totalScale * scaleX, totalScale * scaleY)
        mGestureListener?.onScale(totalScale * scaleX)
    }

    private var degrees = 0
    fun setRotate(degrees: Int) {
        this.degrees = degrees
        renderThread?.mHandler?.setOnRotate(degrees)
        mGestureListener?.onRotate(degrees)
    }

    private var colors = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
    fun setBackgroundColor(colors: FloatArray) {
        this.colors = colors
        renderThread?.mHandler?.setBackgroundColorChanged(colors)
    }

    fun setOverlayVideoSize(width: Int, height: Int) {
        renderThread?.mHandler?.setOverlayVideoSize(width, height)
    }

    private inner class ScaleListener(val gestureSurfaceView: GestureSurfaceView) :
        ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            gestureSurfaceView.setScale(detector.scaleFactor, detector.scaleFactor)
            gestureSurfaceView.totalScale *= detector.scaleFactor
            return true
        }
    }

    private inner class RotateListener(val gestureSurfaceView: GestureSurfaceView) :
        RotateGestureDetector.SimpleOnRotateGestureListener() {
        override fun onRotate(detector: RotateGestureDetector): Boolean {
            degrees -= detector.rotationDegreesDelta.toInt()
            gestureSurfaceView.setRotate(degrees)
            return true
        }
    }

    private inner class MoveListener(val gestureSurfaceView: GestureSurfaceView) :
        MoveGestureDetector.SimpleOnMoveGestureListener() {
        override fun onMove(detector: MoveGestureDetector): Boolean {
            gestureSurfaceView.setScroll(detector.focusDelta.x, -detector.focusDelta.y)
            return true
        }
    }

    private inner class ShoveListener(val gestureSurfaceView: GestureSurfaceView) :
        ShoveGestureDetector.SimpleOnShoveGestureListener() {
        override fun onShove(detector: ShoveGestureDetector): Boolean {
            return false
        }
    }
}