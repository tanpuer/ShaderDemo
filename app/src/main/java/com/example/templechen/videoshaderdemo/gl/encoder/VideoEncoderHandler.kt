package com.example.templechen.videoshaderdemo.gl.encoder

import android.os.Handler
import android.os.Looper
import android.os.Message
import java.lang.RuntimeException
import java.lang.ref.WeakReference

class VideoEncoderHandler(videoEncoderRunnable: VideoEncoderThread) : Handler() {

    companion object {
        const val MSG_STOP_RECORDING = 1
        const val MSG_FRAME_AVAILABLE = 2
    }

    private var weakVideoEncoderThread = WeakReference<VideoEncoderThread>(videoEncoderRunnable)

    fun stopRecording() {
        sendMessage(obtainMessage(MSG_STOP_RECORDING))
    }

    fun frameAvailable() {
        sendMessage(obtainMessage(MSG_FRAME_AVAILABLE))
    }

    override fun handleMessage(msg: Message?) {
        val what = msg?.what
        val videoEncoderThread = weakVideoEncoderThread.get()
        if (videoEncoderThread == null) {
            return
        }
        when (what) {
            MSG_STOP_RECORDING -> {
                videoEncoderThread.handleStopRecording()
            }
            MSG_FRAME_AVAILABLE -> {
                videoEncoderThread.handleFrameAvailableSoon()
            }
            else -> {
                throw RuntimeException("unhandled msg what $what")
            }
        }
    }

}