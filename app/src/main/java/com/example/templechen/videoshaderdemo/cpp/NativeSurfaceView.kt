package com.example.templechen.videoshaderdemo.cpp

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES30
import android.util.AttributeSet
import android.view.*
import com.almeros.android.multitouch.MoveGestureDetector
import com.almeros.android.multitouch.RotateGestureDetector
import com.almeros.android.multitouch.ShoveGestureDetector
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.player.ExoPlayerTool

class NativeSurfaceView : SurfaceView, SurfaceHolder.Callback, Choreographer.FrameCallback {

    companion object {
        private const val TAG = "NativeSurfaceView"

        init {
            System.loadLibrary("native-lib")
        }
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var textureId: Int = -1
    private var firstCreated = true

    private var mScaleDetector: ScaleGestureDetector? = null
    private var mRotateDetector: RotateGestureDetector? = null
    private var mMoveDetector: MoveGestureDetector? = null
    private var mShoveDetector: ShoveGestureDetector? = null

    init {
        holder.addCallback(this)
        // Setup Gesture Detectors
        mScaleDetector = ScaleGestureDetector(context.applicationContext, ScaleListener(this))
        mRotateDetector = RotateGestureDetector(context.applicationContext, RotateListener(this))
        mMoveDetector = MoveGestureDetector(context.applicationContext, MoveListener(this))
        mShoveDetector = ShoveGestureDetector(context.applicationContext, ShoveListener(this))
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        mScaleDetector?.onTouchEvent(event)
        mRotateDetector?.onTouchEvent(event)
        mMoveDetector?.onTouchEvent(event)
        mShoveDetector?.onTouchEvent(event)
        return true
    }

    lateinit var player: ExoPlayerTool
    private lateinit var nativeSurfaceTexture: SurfaceTexture

    override fun surfaceCreated(holder: SurfaceHolder?) {
        nativeInit()
        textureId = GLUtils.createOESTextureObject()
        nativeSurfaceCreated(holder?.surface!!, textureId)
        nativeSurfaceTexture = SurfaceTexture(textureId)
        nativeSetSurfaceTexture(nativeSurfaceTexture)
        player.setVideoSurface(Surface(nativeSurfaceTexture))
        player.playWhenReady = true
        Choreographer.getInstance().postFrameCallback(this)

        if (firstCreated) {
            firstCreated = false
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        nativeSurfaceChanged(holder?.surface!!, format, width, height)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        nativeSurfaceDestroyed(holder?.surface!!)
        Choreographer.getInstance().removeFrameCallback(this)
        nativeSurfaceTexture.release()
        GLES30.glDeleteTextures(1, intArrayOf(textureId), 0)
    }

    override fun doFrame(frameTimeNanos: Long) {
        nativeDoFrame(frameTimeNanos)
        Choreographer.getInstance().postFrameCallback(this)
    }

    fun destroy() {
        nativeDestroyed()
    }

    fun setVideoSize(width: Int, height: Int) {
        nativeSetVideoSize(width, height)

        //restore status
        if (firstCreated) {
            return
        }
        nativeSetScale(totalScale, totalScale)
        nativeSetScroll(floatArrayOf(totalScrollX * 2 / getWidth(), totalScrollY * 2 / getHeight()))
        nativeSetBackgroundColor(colors)
        nativeSetRotate(degrees)
    }

    private var colors = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
    fun setBackgroundColor(colors: FloatArray) {
        this.colors = colors
        nativeSetBackgroundColor(colors)
    }

    private var totalScrollX = 0f
    private var totalScrollY = 0f
    fun setScroll(scrollX: Float, scrollY: Float) {
        totalScrollX += scrollX
        totalScrollY += scrollY
        nativeSetScroll(floatArrayOf(totalScrollX * 2 / width, totalScrollY * 2 / height))
    }

    private var totalScale = 1.0f
    fun setScale(scaleX: Float, scaleY: Float) {
        nativeSetScale(totalScale * scaleX, totalScale * scaleY)
    }

    private var degrees = 0
    fun setRotate(degrees: Int) {
        this.degrees = degrees
        nativeSetRotate(degrees)
    }

    private inner class ScaleListener(val nativeSurfaceView: NativeSurfaceView) :
        ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            nativeSurfaceView.setScale(detector.scaleFactor, detector.scaleFactor)
            nativeSurfaceView.totalScale *= detector.scaleFactor
            return true
        }
    }

    private inner class RotateListener(val nativeSurfaceView: NativeSurfaceView) :
        RotateGestureDetector.SimpleOnRotateGestureListener() {
        override fun onRotate(detector: RotateGestureDetector): Boolean {
            degrees -= detector.rotationDegreesDelta.toInt()
            nativeSurfaceView.setRotate(degrees)
            return true
        }
    }

    private inner class MoveListener(val nativeSurfaceView: NativeSurfaceView) :
        MoveGestureDetector.SimpleOnMoveGestureListener() {
        override fun onMove(detector: MoveGestureDetector): Boolean {
            nativeSurfaceView.setScroll(detector.focusDelta.x, -detector.focusDelta.y)
            return true
        }
    }

    private inner class ShoveListener(val nativeSurfaceView: NativeSurfaceView) :
        ShoveGestureDetector.SimpleOnShoveGestureListener() {
        override fun onShove(detector: ShoveGestureDetector): Boolean {
            return false
        }
    }

    private external fun nativeInit()
    private external fun nativeSurfaceCreated(surface: Surface, textureId: Int)
    private external fun nativeSurfaceChanged(
        surface: Surface,
        format: Int,
        width: Int,
        height: Int
    )

    private external fun nativeSurfaceDestroyed(surface: Surface)
    private external fun nativeDoFrame(frameTimeNanos: Long)
    private external fun nativeDestroyed()
    private external fun nativeSetSurfaceTexture(surfaceTexture: SurfaceTexture)
    private external fun nativeSetVideoSize(videoWidth: Int, videoHeight: Int)
    private external fun nativeSetBackgroundColor(colors: FloatArray)
    private external fun nativeSetScroll(scrolls: FloatArray)
    private external fun nativeSetScale(scrollX: Float, scrollY: Float)
    private external fun nativeSetRotate(degrees: Int)


}