package com.example.templechen.videoshaderdemo.album.offscreen


import android.content.Context
import android.opengl.GLES30
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.example.templechen.videoshaderdemo.R
import com.example.templechen.videoshaderdemo.album.filter.*
import com.example.templechen.videoshaderdemo.gl.egl.EglCore
import com.example.templechen.videoshaderdemo.gl.egl.OffscreenSurface
import com.example.templechen.videoshaderdemo.gl.egl.WindowSurface
import com.example.templechen.videoshaderdemo.offscreen.VideoEncoder
import java.io.File

class AlbumOffScreenRenderThread(val context: Context) : Thread() {

    companion object {
        private const val RATIO = 1
        private const val TAG = "AlbumRenderThread"
    }

    var mHandler: AlbumOffScreenRenderHandler? = null
    private lateinit var mEglCore: EglCore
    private lateinit var mWindowSurface: OffscreenSurface
    private val mStartLock = Object()
    private var mReady = false
    private var filterList = mutableListOf<AlbumFilter>()
    private var filterTotalTimes = 0
    private lateinit var mVideoEncoder: VideoEncoder
    private lateinit var mInputWindowSurface: WindowSurface
    private var recordingEnable = false
    private var startTime = System.currentTimeMillis()
    private var refreshRate = 60f

    override fun run() {
        Looper.prepare()
        mHandler = AlbumOffScreenRenderHandler(this)
        mEglCore = EglCore(null, EglCore.FLAG_RECORDABLE.or(EglCore.FLAG_TRY_GLES3))
        synchronized(mStartLock) {
            mReady = true
            mStartLock.notify()  // signal waitUntilReady()
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

    private fun releaseGL() {
        filterList.forEach {
            it.release()
        }
        mWindowSurface.release()
        mEglCore.makeNothingCurrent()
    }

    fun surfaceCreated() {
        mWindowSurface = OffscreenSurface(mEglCore, 720, 1280)
        mWindowSurface.makeCurrent()
        GLES30.glClearColor(0f, 0f, 0f, 1.0f)
        GLES30.glDisable(GLES30.GL_DEPTH_TEST)
        GLES30.glDisable(GLES30.GL_CULL_FACE)
        GLES30.glEnable(GLES30.GL_BLEND)
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA)

        filterList.add(AlbumFilter(context, R.drawable.test, true))
        filterList.add(AlbumTranslateFilter(context, R.drawable.testjpg3))
        filterList.add(AlbumCircleFilter(context, R.drawable.testjpg))
        filterList.add(AlbumScaleFilter(context, R.drawable.testjpg2))
        filterList.add(AlbumCoverFilter(context, R.drawable.testjpg))
//        filterList.add(AlbumCover2Filter(context, R.drawable.testjpg))

        filterList.forEach {
            filterTotalTimes += it.times
            it.times /= RATIO
        }
        surfaceChanged(720, 1280)
        initEncoder()
        val display =
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        refreshRate = display.refreshRate
        doFrame()
    }

    fun surfaceChanged(width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        filterList.forEach {
            it.setViewSize(width, height)
        }
    }

    fun doFrame() {
        var start = System.currentTimeMillis()
        for (i in 0..filterTotalTimes / RATIO) {
            Log.d(TAG, "${System.currentTimeMillis() - start}ms")
            start = System.currentTimeMillis()

            GLES30.glClearColor(0f, 0f, 0f, 1.0f)
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
            //recording
            if (recordingEnable) {
                mInputWindowSurface.makeCurrentReadFrom(mWindowSurface)
                GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
                GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)

                draw(i)

                mInputWindowSurface.setPresentationTime(i * RATIO * 16667L * 1000)
                mInputWindowSurface.swapBuffers()
                mVideoEncoder.drainEncoderWithNoTimeOut(false)
                mWindowSurface.makeCurrent()
            }

            val swapResult: Boolean = mWindowSurface.swapBuffers()
            if (!swapResult) {
                Log.w(TAG, "swapBuffers failed, killing image renderer thread")
                shutDown()
                return
            }
        }
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(
                context,
                "off screen image finish ${System.currentTimeMillis() - startTime}ms! $filterTotalTimes",
                Toast.LENGTH_LONG
            ).show()
        }
        Log.d(TAG, "doFrame over")
        mVideoEncoder.drainEncoderWithNoTimeOut(true)
        mInputWindowSurface.release()
        mVideoEncoder.release()
        filterList.forEach {
            it.release()
        }
        shutDown()
    }

    fun shutDown() {
        Looper.myLooper()?.quit()
    }

    private fun getCurrentFilterIndex(totalTimes: Int): Int {
        var times = 0
        filterList.forEachIndexed { index, albumFilter ->
            times += albumFilter.times
            if (totalTimes <= times) {
                return index
            }
        }
        return filterList.size - 1
    }

    private fun draw(totalTimes: Int) {
        val currentIndex = getCurrentFilterIndex(totalTimes)
        val albumFilter = filterList[currentIndex]
        if (!albumFilter.initedProgram) {
            if (currentIndex - 1 in 0 until filterList.size) {
                filterList[currentIndex - 1].reset()
            }
            albumFilter.initFilter()
        }
        albumFilter.drawFrame()
    }

    fun initEncoder() {
        val BIT_RATE = 2500000
        val WIDTH = 720
        val HEIGHT = 1280
        var outputFile =
            File(Environment.getExternalStorageDirectory().absolutePath, "trailer_test.mp4")
        if (outputFile.exists()) {
            outputFile.delete()
            outputFile =
                File(Environment.getExternalStorageDirectory().absolutePath, "trailer_test.mp4")
        }
        mVideoEncoder = VideoEncoder(WIDTH, HEIGHT, BIT_RATE, outputFile)
        mInputWindowSurface = WindowSurface(mEglCore, mVideoEncoder.mInputSurface, true)
        recordingEnable = true
    }

}