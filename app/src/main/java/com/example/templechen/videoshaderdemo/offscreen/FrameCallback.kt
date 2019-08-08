package com.example.templechen.videoshaderdemo.offscreen

import android.util.Log

interface FrameCallback {

    companion object {
        const val TAG = "FrameCallback"
    }

    fun decodeFrameBegin() {
        Log.d(TAG, "decodeFrameBegin")
    }

    fun decodeOneFrame(pts: Long) {
        
    }

    fun decodeFrameEnd() {
        Log.d(TAG, "decodeFrameEnd")
    }
}