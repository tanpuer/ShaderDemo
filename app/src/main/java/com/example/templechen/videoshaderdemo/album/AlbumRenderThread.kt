package com.example.templechen.videoshaderdemo.album

import android.content.Context
import android.opengl.GLES30
import android.os.Looper
import android.util.Log
import android.view.Surface
import com.example.templechen.videoshaderdemo.R
import com.example.templechen.videoshaderdemo.album.filter.*
import com.example.templechen.videoshaderdemo.gl.egl.EglCore
import com.example.templechen.videoshaderdemo.gl.egl.WindowSurface

class AlbumRenderThread(val context: Context, val surface: Surface) : Thread() {

    companion object {
        private const val TAG = "AlbumRenderThread"
    }

    var mHandler: AlbumRenderHandler? = null
    private lateinit var mEglCore: EglCore
    private lateinit var mWindowSurface: WindowSurface
    private val mStartLock = Object()
    private var mReady = false
    private var filterList = mutableListOf<AlbumFilter>()
    private var filterTotalTimes = 0

    override fun run() {
        Looper.prepare()
        mHandler = AlbumRenderHandler(this)
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
        mWindowSurface = WindowSurface(mEglCore, surface, false)
        mWindowSurface.makeCurrent()
        GLES30.glClearColor(0f, 0f, 0f, 1.0f)
        GLES30.glDisable(GLES30.GL_DEPTH_TEST)
        GLES30.glDisable(GLES30.GL_CULL_FACE)
        GLES30.glEnable(GLES30.GL_BLEND)
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA)

//        filterList.add(AlbumFilter(context, R.drawable.test, true))
//        filterList.add(AlbumTranslateFilter(context, R.drawable.testjpg3))
        filterList.add(AlbumCircleFilter(context, R.drawable.testjpg))
//        filterList.add(AlbumScaleFilter(context, R.drawable.testjpg2))
//        filterList.add(AlbumCoverFilter(context, R.drawable.testjpg))
//        filterList.add(AlbumCover2Filter(context, R.drawable.testjpg))

        filterList.forEach {
            filterTotalTimes += it.times
        }
    }

    fun surfaceChanged(width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        filterList.forEach {
            it.setViewSize(width, height)
        }
    }

    private var totalTimes = 0
    fun doFrame() {
        totalTimes++
        if (totalTimes > filterTotalTimes) {
            totalTimes = 0
            filterList.forEach {
                it.release()
            }
        }
        GLES30.glClearColor(0f, 0f, 0f, 1.0f)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)

        //draw
        draw(totalTimes)

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

}