package com.example.templechen.videoshaderdemo.image.creator

import android.graphics.Bitmap

interface IBitmapCreator {

    fun generateBitmap(): Bitmap?

    fun coverImage(): Bitmap

    fun framesCount(): Int

    fun getIntrinsicHeight(): Int

    fun getIntrinsicWidth(): Int

    fun getDuration(): Int {
        return 1
    }

    fun getCurrentFrameIndex(): Int {
        return 1
    }

    fun start() {

    }

    fun pause() {

    }

    fun stop() {

    }

    fun seekTo(duration: Int) {

    }

    fun seekToFrame(index: Int) {

    }

    fun seekToFrameAndGet(index: Int): Bitmap? {
        return null
    }

    fun recycle() {

    }

}