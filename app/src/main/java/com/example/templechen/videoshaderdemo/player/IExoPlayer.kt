package com.example.templechen.videoshaderdemo.player

import android.content.Context
import android.view.Surface

interface IExoPlayer {

    fun setVideoSurface(surface: Surface)

    fun setPlayWhenReady(play: Boolean)

    fun setLoop(loop: Boolean)

    fun quickSetting(context: Context, url: String)

}