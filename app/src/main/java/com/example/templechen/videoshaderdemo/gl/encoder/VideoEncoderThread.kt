package com.example.templechen.videoshaderdemo.gl.encoder

import android.os.Looper
import android.util.Log

class VideoEncoderThread(videoEncoder: VideoEncoder) : Thread() {

    companion object {
        private const val TAG = "VideoEncoderThread"
    }

    private var mVideoEncoder = videoEncoder

    private var mReadyLock = Object()  // guards ready/running
    private var mRunning = false
    private var mReady = false
    private var mHandler: VideoEncoderHandler? = null

    /**
     * Waits until the encoder thread is ready to receive messages.
     * <p>
     * Call from the Render thread.
     */
    fun waitUntilReady() {
        synchronized(mReadyLock) {
            mRunning = true
            while (!mReady) {
                mReadyLock.wait()
            }
        }
    }

    override fun run() {
        Looper.prepare()
        mHandler = VideoEncoderHandler(this)
        synchronized(mReadyLock) {
            mReady = true
            mReadyLock.notify()
        }
        Looper.loop()
        Log.d(TAG, "Encoder thread exiting")
        synchronized(mReadyLock) {
            mReady = false
            mRunning = false
            mHandler = null
        }
    }

    fun stopRecording() {
        mHandler?.stopRecording()
    }

    fun frameAvailableSoon() {
        if (!mReady) {
            return
        }
        mHandler?.frameAvailable()
    }

    fun handleFrameAvailableSoon() {
        mVideoEncoder.drainEncoder(false)
    }

    fun handleStopRecording() {
        mVideoEncoder.drainEncoder(true)
        mVideoEncoder.release()
        Looper.myLooper()?.quit()
    }

}