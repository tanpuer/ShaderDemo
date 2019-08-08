package com.example.templechen.videoshaderdemo.gl

interface IGLInfoCallback {

    fun updateFps(tfps: Int, dropped: Int)

    fun updateGLVersion(version: Int)

}