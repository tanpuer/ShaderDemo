package com.example.templechen.videoshaderdemo.fake


import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES30
import android.opengl.Matrix
import android.os.Looper
import android.util.Log
import android.view.Surface
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.filter.*
import com.example.templechen.videoshaderdemo.gl.egl.EglCore
import com.example.templechen.videoshaderdemo.gl.egl.OffscreenSurface
import com.example.templechen.videoshaderdemo.gl.egl.WindowSurface
import com.example.templechen.videoshaderdemo.player.IExoPlayer

class SimpleGLFakeThread(
    context: Context,
    refreshPeriodsNs: Long,
    player: IExoPlayer,
    type: String,
    eglCore: EglCore,
    windowSurface: OffscreenSurface
) :
    Thread() {

    companion object {
        private const val TAG = "RenderThread"
    }

    lateinit var mHandler: SimpleGLFakeHandler
    private var mEglCore = eglCore
    //use to wait for the thread to start
    private val mStartLock = Object()
    private var mReady = false

    private var mPlayer = player
    private var mContext = context
    private var mRefreshPeriod = refreshPeriodsNs
    private var mWindowSurface = windowSurface
    private var mDisplayProjectionMatrix = FloatArray(16) { 0f }
    private var mType = type

    // FPS / drop counter.
    private var mDroppedFrames: Int = 0
    private var mPreviousWasDropped: Boolean = false

    //my custom program
    private lateinit var filter: BaseFilter
    private var mOESTextureId: Int = -1
    private lateinit var mSurfaceTexture: SurfaceTexture

    //another surface
    private var renderAnotherSurfaceEnable = false
    private var anotherSurface: Surface? = null
    private lateinit var anotherWindowSurface: WindowSurface

    override fun run() {
        Looper.prepare()
        mHandler = SimpleGLFakeHandler(this)
        synchronized(mStartLock) {
            mReady = true
            mStartLock.notify()  // signal waitUntilReady()
        }
        Looper.loop()
        Log.d(TAG, "looper quit")
        releaseGL()
        mEglCore.release()
        synchronized(mStartLock) {
            mReady = false
        }
    }

    fun waitUtilReady() {
        synchronized(mStartLock) {
            while (!mReady) {
                mStartLock.wait()
            }
        }
    }

    fun surfaceCreated(type: Int) {
        prepareGL(type)
    }

    private fun prepareGL(type: Int) {
        Log.d(TAG, "prepareGl")
//        mWindowSurface = OffscreenSurface(mEglCore, 1280, 720)
        mWindowSurface.makeCurrent()
        //custom program
        mOESTextureId = GLUtils.createOESTextureObject()
        mSurfaceTexture = SurfaceTexture(mOESTextureId)

        mType = FilterListUtil.LIST[type]
        setFilter(mType, mOESTextureId)

        //todo main thread
        mPlayer.setVideoSurface(Surface(mSurfaceTexture))
        mPlayer.setPlayWhenReady(true)

        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES30.glDisable(GLES30.GL_DEPTH_TEST)
        GLES30.glDisable(GLES30.GL_CULL_FACE)
        GLES30.glEnable(GLES30.GL_BLEND)
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA)
    }

    fun surfaceChanged(width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        Matrix.orthoM(
            mDisplayProjectionMatrix,
            0,
            0f,
            width.toFloat(),
            0f,
            height.toFloat(),
            -1f,
            1f
        )
    }

    fun doFrame(timestampsNanos: Long) {
        val diff = System.nanoTime() - timestampsNanos
        val max = mRefreshPeriod - 2000000  // if we're within 2ms, don't bother
        if (diff > max) {
            // too much, drop a frame
            Log.d(
                TAG, "diff is " + (diff / 1000000.0) + " ms, max " + (max / 1000000.0) +
                        ", skipping render"
            )
            mPreviousWasDropped = true
            mDroppedFrames++
            return
        }

        //reset
        if (filterNeedReset && filterType != -1) {
            filter.release()
            setFilter(FilterListUtil.LIST[filterType], mOESTextureId)
            filterNeedReset = false
            filterType = -1
        }

        draw()

        // Render another surface
        if (renderAnotherSurfaceEnable) {
            anotherWindowSurface.makeCurrentReadFrom(mWindowSurface)
            GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
            filter.drawFrame()

//            GLES30.glBlitFramebuffer(
//                0,
//                0,
//                mWindowSurface.width,
//                mWindowSurface.height,
//                0,
//                0,
//                anotherWindowSurface.width,
//                anotherWindowSurface.height,
//                GLES30.GL_COLOR_BUFFER_BIT,
//                GLES30.GL_NEAREST
//            )
            anotherWindowSurface.setPresentationTime(timestampsNanos)
            anotherWindowSurface.swapBuffers()

            mWindowSurface.makeCurrent()
        }

        val swapResult: Boolean = mWindowSurface.swapBuffers()
        if (!swapResult) {
            Log.w(TAG, "swapBuffers failed, killing renderer thread")
            shutDown()
            return
        }
    }

    fun shutDown() {
        Log.d(TAG, "shutdown")
        Looper.myLooper()?.quit()
    }

    private fun releaseGL() {
        GLUtils.checkGlError("releaseGl start")
        mWindowSurface.release()
        mEglCore.makeNothingCurrent()
        if (anotherSurface != null) {
            anotherWindowSurface.release()
            anotherSurface = null
        }
    }

    private fun draw() {
        GLUtils.checkGlError("draw start")
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)

        mSurfaceTexture.getTransformMatrix(filter.transformMatrix)
        mSurfaceTexture.updateTexImage()
//        filter.drawFrame()
    }

    private fun setFilter(type: String, mOESTextureId: Int) {
        filter = FilterListUtil.setFilter(type, mOESTextureId, mContext)
        filter.initProgram()
    }

    fun renderAnotherSurface(surface: Surface) {
        if (anotherSurface != null) {
            stopRenderAnotherSurface()
        }
        renderAnotherSurfaceEnable = true
        anotherSurface = surface
        anotherWindowSurface = WindowSurface(mEglCore, anotherSurface, false)
        GLES30.glViewport(0, 0, anotherWindowSurface.width, anotherWindowSurface.height)
        var scaleX = 1f
        var scaleY = 1f
        var scrollX = 0f
        var scrollY = 0f
        val fitCenter = false
        if (fitCenter) {
            if (9f * 1.0f / 16f > anotherWindowSurface.width * 1.0f / anotherWindowSurface.height) {
                //横屏视频
                scaleY = anotherWindowSurface.width * 1.0f / 9 * 16 / anotherWindowSurface.height
            } else {
                //竖屏视频
                scaleX = anotherWindowSurface.height * 1.0f / 16 * 9 / anotherWindowSurface.width
            }
        } else {
            if (9f * 1.0f / 16f > anotherWindowSurface.width * 1.0f / anotherWindowSurface.height) {
                //横屏视频
                scaleX = anotherWindowSurface.height * 1.0f / 16 * 9 / anotherWindowSurface.width
            } else {
                //竖屏视频
                scaleY = anotherWindowSurface.width * 1.0f / 9 * 16 / anotherWindowSurface.height
            }
        }
        filter.setMVPMatrixScaleAndTransform(scaleX, scaleY, scrollX, scrollY)
    }

    fun stopRenderAnotherSurface() {
        renderAnotherSurfaceEnable = false
        anotherSurface = null
        anotherWindowSurface.release()
    }

    private var filterNeedReset = false
    private var filterType = -1
    fun resetFilter(type: Int) {
        filterNeedReset = true
        filterType = type
    }

}