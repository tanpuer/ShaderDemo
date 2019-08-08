package com.example.templechen.videoshaderdemo.image.offscreen

import android.content.Context
import android.opengl.GLES30
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.templechen.videoshaderdemo.gl.egl.EglCore
import com.example.templechen.videoshaderdemo.gl.egl.OffscreenSurface
import com.example.templechen.videoshaderdemo.gl.egl.WindowSurface
import com.example.templechen.videoshaderdemo.image.creator.IBitmapCreator
import com.example.templechen.videoshaderdemo.image.filter.BitmapFilter
import com.example.templechen.videoshaderdemo.offscreen.OffScreenRenderThread
import com.example.templechen.videoshaderdemo.offscreen.VideoEncoder
import java.io.File

class OffScreenImageRenderThread(
    val context: Context,
    private val bitmapCreator: IBitmapCreator,
    private val destPath: String
) :
    Thread() {

    companion object {
        const val TAG = "RenderThread"
        const val WIDTH = 720
        const val HEIGHT = 1280
    }

    private lateinit var mEglCore: EglCore
    lateinit var offscreenImageHandler: OffScreenImageHandler
    private lateinit var mOffScreenWindowSurface: OffscreenSurface
    private lateinit var filter: BitmapFilter
    private lateinit var mVideoEncoder: VideoEncoder
    private lateinit var mInputWindowSurface: WindowSurface
    private var recordingEnable = false

    private val mStartLock = Object()
    private var mReady = false
    private val startTime = System.currentTimeMillis()

    override fun run() {
        Looper.prepare()
        offscreenImageHandler = OffScreenImageHandler(this)
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

    fun waitUntilReady() {
        synchronized(mStartLock) {
            while (!mReady) {
                mStartLock.wait()
            }
        }
    }

    private fun releaseGL() {
        mEglCore.makeNothingCurrent()
    }

    fun prepareGL() {
        mOffScreenWindowSurface = OffscreenSurface(mEglCore, 720, 1280)
        mOffScreenWindowSurface.makeCurrent()

        GLES30.glClearColor(0f, 0f, 0f, 1f)
        GLES30.glDisable(GLES30.GL_DEPTH_TEST)
        GLES30.glDisable(GLES30.GL_CULL_FACE)
        GLES30.glEnable(GLES30.GL_BLEND)
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA)

        initEncoder()

        filter = BitmapFilter(context, bitmapCreator.coverImage())
        filter.initProgram()
        filter.setSize(
            bitmapCreator.getIntrinsicWidth(),
            bitmapCreator.getIntrinsicHeight(),
            mOffScreenWindowSurface.width,
            mOffScreenWindowSurface.height
        )
    }

    private fun initEncoder() {
        val BIT_RATE = 4000000
        var outputFile = File(destPath)
        if (outputFile.exists()) {
            outputFile.delete()
            outputFile.createNewFile()
        }
        mVideoEncoder = VideoEncoder(
            WIDTH,
            HEIGHT,
            BIT_RATE,
            outputFile
        )
        mInputWindowSurface = WindowSurface(mEglCore, mVideoEncoder.mInputSurface, true)
        recordingEnable = true
    }

    fun startCrop() {
        val interval =
            bitmapCreator.getDuration().toLong() / (if (bitmapCreator.framesCount() - 1 > 0) bitmapCreator.framesCount() - 1 else 1)

        var start = System.currentTimeMillis()
        for (i in 1..bitmapCreator.framesCount()) {
            Log.d(TAG, "${System.currentTimeMillis() - start}ms")
            start = System.currentTimeMillis()

            mOffScreenWindowSurface.makeCurrent()
            GLES30.glClearColor(0f, 0f, 0f, 1.0f)
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
            filter.drawFrame(bitmapCreator.seekToFrameAndGet(i))

            if (recordingEnable) {

                mInputWindowSurface.makeCurrentReadFrom(mOffScreenWindowSurface)
                GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
                GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)

                GLES30.glBlitFramebuffer(
                    0,
                    0,
                    mInputWindowSurface.width,
                    mInputWindowSurface.height,
                    0,
                    0,
                    mInputWindowSurface.width,
                    mInputWindowSurface.height,
                    GLES30.GL_COLOR_BUFFER_BIT,
                    GLES30.GL_NEAREST
                )
                val err = GLES30.glGetError()
                if (err != GLES30.GL_NO_ERROR) {
                    Log.w(OffScreenRenderThread.TAG, "ERROR: glBlitFramebuffer failed: 0x" + Integer.toHexString(err))
                }
                mInputWindowSurface.setPresentationTime(interval * i * 1000000)
                mInputWindowSurface.swapBuffers()

                Log.d(TAG, "encode one frame $i")
                mVideoEncoder.drainEncoderWithNoTimeOut(false)

            }
            mOffScreenWindowSurface.makeCurrent()
            mOffScreenWindowSurface.swapBuffers()
        }
        mVideoEncoder.drainEncoderWithNoTimeOut(true)
        mInputWindowSurface.release()
        mVideoEncoder.release()
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(
                context,
                "off screen image finish ${System.currentTimeMillis() - startTime}ms! ${bitmapCreator.framesCount()}",
                Toast.LENGTH_LONG
            ).show()
        }
        Looper.myLooper()?.quitSafely()
        join()
    }

}