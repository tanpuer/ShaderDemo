package com.example.templechen.videoshaderdemo.album.offscreen


import android.os.Handler
import android.os.Message

class AlbumOffScreenRenderHandler(val albumRenderThread: AlbumOffScreenRenderThread) : Handler() {

    companion object {
        const val MSG_START = 0
    }

    fun startEncode() {
        sendMessage(obtainMessage(MSG_START))
    }

    override fun handleMessage(msg: Message?) {
        when (msg?.what) {
            MSG_START -> {
                albumRenderThread.surfaceCreated()
            }
        }
    }
}