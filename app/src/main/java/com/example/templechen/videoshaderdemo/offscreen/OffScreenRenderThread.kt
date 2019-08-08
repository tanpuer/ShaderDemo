package com.example.templechen.videoshaderdemo.offscreen

import android.content.Context
import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.opengl.GLES30
import android.os.Environment
import android.os.Looper
import android.util.Log
import android.view.Surface
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.filter.BaseFilter
import com.example.templechen.videoshaderdemo.filter.FilterListUtil
import com.example.templechen.videoshaderdemo.filter.GrayFilter
import com.example.templechen.videoshaderdemo.gl.egl.EglCore
import com.example.templechen.videoshaderdemo.gl.egl.OffscreenSurface
import com.example.templechen.videoshaderdemo.gl.egl.WindowSurface
import java.io.File
import java.lang.Exception
import java.nio.ByteBuffer
import java.nio.ByteOrder

class OffScreenRenderThread(context: Context, file: File, offScreenActivityHandler: OffScreenActivityHandler) :
    Thread(), FrameCallback {

    companion object {
        const val TAG = "OffScreenRenderThread"
    }

    //offscreen render
    private var mContext = context
    private var mFile = file
    private var mOffScreenActivityHandler = offScreenActivityHandler
    private lateinit var mEglCore: EglCore
    private lateinit var mOffScreenWindowSurface: OffscreenSurface
    private lateinit var mOutOutSurface: Surface
    private var mOESTextureId = -1
    private lateinit var mSurfaceTexture: SurfaceTexture
    lateinit var mRenderHandler: OffScreenRenderHandler
    private var mStartLock = Object()
    private var mReady = false
    private lateinit var filter: BaseFilter

    //video decode
    private lateinit var mVideoDecoder: VideoDecoder

    //recording
    private var recordingEnable = false
    private lateinit var mInputWindowSurface: WindowSurface
    var editorRect = Rect(0, 0, 0, 0)
    private lateinit var mVideoEncoder: VideoEncoder
    private lateinit var mByteBuffer: ByteBuffer

    private lateinit var mEncoderFilter : BaseFilter

    override fun run() {
        Looper.prepare()
        mRenderHandler = OffScreenRenderHandler(this)
        mEglCore = EglCore(null, EglCore.FLAG_RECORDABLE.or(EglCore.FLAG_TRY_GLES3))
        synchronized(mStartLock) {
            mReady = true
            mStartLock.notify()
        }
        Looper.loop()
        Log.d(TAG, "looper quit")
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

    fun prepareGL() {
        mVideoDecoder = VideoDecoder(mFile)
        mOffScreenWindowSurface = OffscreenSurface(mEglCore, mVideoDecoder.mVideoWidth, mVideoDecoder.mVideoHeight)

        mOffScreenWindowSurface.makeCurrent()

        mOESTextureId = GLUtils.createOESTextureObject()
        mSurfaceTexture = SurfaceTexture(mOESTextureId)
        mOutOutSurface = Surface(mSurfaceTexture)
        mVideoDecoder.mOutputSurface = mOutOutSurface

        GLES30.glClearColor(0f, 0f, 0f, 1f)
        GLES30.glDisable(GLES30.GL_DEPTH_TEST)
        GLES30.glDisable(GLES30.GL_CULL_FACE)
        GLES30.glEnable(GLES30.GL_BLEND)
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA)

        initEncoder()

        filter = GrayFilter(mContext, mOESTextureId)
        filter.initProgram()
    }

    fun renderFrame() {
        mVideoDecoder.mFrameCallback = this
        try {
            mVideoDecoder.decode()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var beginTime = System.nanoTime()
    override fun decodeFrameBegin() {
        super.decodeFrameBegin()
        beginTime = System.currentTimeMillis()
    }

    override fun decodeFrameEnd() {
        super.decodeFrameEnd()
        mVideoEncoder.drainEncoderWithNoTimeOut(true)
        mInputWindowSurface.release()
        mVideoEncoder.release()
        muxerVideoAndAudio()
        mOffScreenActivityHandler.sendOffscreenEnd()
        Looper.myLooper()?.quitSafely()
        join()
    }

    override fun decodeOneFrame(pts: Long) {
        draw()
        //recording
        //first frame is reverse. have set transformMatrix, do not know why, so ignore
        if (recordingEnable) {
            mInputWindowSurface.makeCurrentReadFrom(mOffScreenWindowSurface)
            GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
            mEncoderFilter.drawFrame()

//            if (editorRect.width() > 0 && editorRect.height() > 0) {
//                GLES30.glBlitFramebuffer(
//                    editorRect.left,
//                    editorRect.top,
//                    editorRect.right,
//                    editorRect.bottom,
//                    0,
//                    0,
//                    mInputWindowSurface.width,
//                    mInputWindowSurface.height,
//                    GLES30.GL_COLOR_BUFFER_BIT,
//                    GLES30.GL_NEAREST
//                )
//            } else {
//                GLES30.glBlitFramebuffer(
//                    0,
//                    0,
//                    mInputWindowSurface.width,
//                    mInputWindowSurface.height,
//                    0,
//                    0,
//                    mInputWindowSurface.width,
//                    mInputWindowSurface.height,
//                    GLES30.GL_COLOR_BUFFER_BIT,
//                    GLES30.GL_NEAREST
//                )
//            }
//            val err = GLES30.glGetError()
//            if (err != GLES30.GL_NO_ERROR) {
//                Log.w(TAG, "ERROR: glBlitFramebuffer failed: 0x" + Integer.toHexString(err))
//            }
            mInputWindowSurface.setPresentationTime(pts * 1000)
            mInputWindowSurface.swapBuffers()
            mVideoEncoder.drainEncoderWithNoTimeOut(false)
            mOffScreenWindowSurface.makeCurrent()
        }
//        speed test, for FFmpeg
//        GLES30.glReadPixels(0, 0, mOffScreenWindowSurface.width, mOffScreenWindowSurface.height, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, mByteBuffer)
        val swapResult: Boolean = mOffScreenWindowSurface.swapBuffers()
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

    fun initEncoder() {
        val BIT_RATE = 4000000
        val WIDTH = 720
        val HEIGHT = 1280
        var outputFile = File(mContext.cacheDir, "gltest.mp4")
        if (outputFile.exists()) {
            outputFile.delete()
            outputFile = File(mContext.cacheDir, "gltest.mp4")
        }
        mVideoEncoder = VideoEncoder(
            if (editorRect.width() > 0 && editorRect.height() > 0) WIDTH else mOffScreenWindowSurface.width,
            if (editorRect.width() > 0 && editorRect.height() > 0) HEIGHT else mOffScreenWindowSurface.height,
            BIT_RATE,
            outputFile
        )
        mInputWindowSurface = WindowSurface(mEglCore, mVideoEncoder.mInputSurface, true)
        recordingEnable = true
        mEncoderFilter = BaseFilter(mContext, mOESTextureId)
        mEncoderFilter.initProgram()
    }

    private fun releaseGL() {
        GLUtils.checkGlError("releaseGl start")
        mOffScreenWindowSurface.release()
        mEglCore.makeNothingCurrent()
    }

    private fun draw() {
        GLUtils.checkGlError("draw start")
        GLES30.glClearColor(0f, 0f, 0f, 1.0f)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
//        mSurfaceTexture.getTransformMatrix(filter.transformMatrix)
        mSurfaceTexture.updateTexImage()
        filter.drawFrame()
    }

    private fun muxerVideoAndAudio() {
        val source = File(Environment.getExternalStorageDirectory().absolutePath + "/trailer.mp4")
        var mp4Dest = File(Environment.getExternalStorageDirectory().absolutePath + "/trailer_test.mp4")
        if (mp4Dest.exists()) {
            mp4Dest.delete()
            mp4Dest.createNewFile()
        }
        val start = System.currentTimeMillis()
        FileMuxer.muxeVideoAndAudio(source.path, File(mContext.cacheDir, "gltest.mp4").path, mp4Dest.path)
        Log.d(OffScreenActivity.TAG, "${System.currentTimeMillis() - start}")
    }

}