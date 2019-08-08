package com.example.templechen.videoshaderdemo.gl

import android.graphics.Rect
import android.view.Surface
import android.view.View
import com.example.templechen.videoshaderdemo.player.ExoPlayerTool
import com.example.templechen.videoshaderdemo.player.IExoPlayer

interface SimpleGLView {

    fun initViews(activityHandler: ActivityHandler?, playerTool: IExoPlayer, filterType: Int)

    fun startRecording()

    fun stopRecording()

    fun changeFilter(type: Int)

    fun renderAnotherSurface(surface: Surface?)

    fun stopRenderAnotherSurface()

    fun getView(): View

    fun setVideoEditorRect(rect: Rect)

    fun setCustomStickerView(view: View?)

}