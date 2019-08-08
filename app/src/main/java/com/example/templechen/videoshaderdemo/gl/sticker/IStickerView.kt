package com.example.templechen.videoshaderdemo.gl.sticker

import android.view.View

interface IStickerView {

    fun getView(): View

    fun setOnStickerViewClickListener(onStickerViewClickListener: IStickerView.OnStickerViewClickListener)

    fun setOnStickerViewScrollListener(onStickerViewScroll: IStickerView.OnStickerViewScroll)
    
    interface OnStickerViewClickListener {
        fun onStickerViewClicked(stickerView: IStickerView)
    }

    interface OnStickerViewScroll {
        fun stickerViewScroll(stickerView: IStickerView)
    }
}