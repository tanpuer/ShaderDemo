package com.example.templechen.videoshaderdemo.image.render

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES30
import android.os.Looper
import android.util.Log
import android.view.Surface
import com.example.templechen.videoshaderdemo.R
import com.example.templechen.videoshaderdemo.gl.egl.EglCore
import com.example.templechen.videoshaderdemo.gl.egl.WindowSurface
import com.example.templechen.videoshaderdemo.image.ImageSurfaceHandler
import com.example.templechen.videoshaderdemo.image.creator.BitmapCreatorFactory
import com.example.templechen.videoshaderdemo.image.creator.IBitmapCreator
import com.example.templechen.videoshaderdemo.image.filter.BitmapFilter

class ImageRenderThread(
    val context: Context,
    surface: Surface,
    url: String,
    val imageSurfaceHandler: ImageSurfaceHandler?
) : Thread() {

    companion object {
        const val TAG = "ImageRenderThread"
    }

    lateinit var imageRendHandler: ImageRenderHandler
    private val mStartLock = Object()
    private var mReady = false

    private lateinit var mEglCore: EglCore
    private lateinit var mWindowSurface: WindowSurface
    private var mSurface = surface
    private lateinit var mFilter: BitmapFilter
    private lateinit var bitmapCreator: IBitmapCreator
    private var mUrl = url

    override fun run() {
        Looper.prepare()
        imageRendHandler = ImageRenderHandler(this)
        mEglCore = EglCore(null, EglCore.FLAG_RECORDABLE.or(EglCore.FLAG_TRY_GLES3))
        synchronized(mStartLock) {
            mReady = true
            mStartLock.notify()
        }
        Looper.loop()
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

    fun surfaceCreated() {
        mWindowSurface = WindowSurface(mEglCore, mSurface, false)
        mWindowSurface.makeCurrent()
        GLES30.glClearColor(0f, 0f, 0f, 1.0f)
        GLES30.glDisable(GLES30.GL_DEPTH_TEST)
        GLES30.glDisable(GLES30.GL_CULL_FACE)
        GLES30.glEnable(GLES30.GL_BLEND)
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA)

        val options = BitmapFactory.Options()
        options.inScaled = false
//        bitmapCreator = BitmapCreatorFactory.newImageCreator(R.drawable.dynamic_cropping_ready2, BitmapCreatorFactory.TYPE_IMAGE, context)
        bitmapCreator = BitmapCreatorFactory.newImageCreator(R.drawable.test, BitmapCreatorFactory.TYPE_GIF, context)
        mFilter = BitmapFilter(context, bitmapCreator.coverImage())
        imageSurfaceHandler?.setTotalFrames(bitmapCreator.framesCount())
        mFilter.setSize(
            bitmapCreator.getIntrinsicWidth(),
            bitmapCreator.getIntrinsicHeight(),
            mWindowSurface.width,
            mWindowSurface.height
        )
        mFilter.initProgram()
    }

    fun surfaceChanged(width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
    }

    var lastFrameIndex = 0
    fun doFrame() {
        val currentPos = bitmapCreator.getCurrentFrameIndex()
        if (currentPos != lastFrameIndex) {
            imageSurfaceHandler?.setCurrentFrame(bitmapCreator.getCurrentFrameIndex())
            lastFrameIndex = currentPos
        }

        GLES30.glClearColor(0f, 0f, 0f, 1.0f)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        mFilter.drawFrame(bitmapCreator.generateBitmap())
        val swapResult: Boolean = mWindowSurface.swapBuffers()
        if (!swapResult) {
            Log.w(TAG, "swapBuffers failed, killing image renderer thread")
            shutDown()
            return
        }
    }

    fun shutDown() {
        Looper.myLooper()?.quit()
    }

    private fun releaseGL() {
        mFilter.release()
        mWindowSurface.release()
    }

    fun startImage() {
        bitmapCreator.start()
    }

    fun pauseImage() {
        bitmapCreator.pause()
    }

    fun seekTo(count: Int) {
        bitmapCreator.seekToFrame(count)
    }
}