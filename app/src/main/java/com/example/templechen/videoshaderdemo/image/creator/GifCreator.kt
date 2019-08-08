package com.example.templechen.videoshaderdemo.image.creator

import android.content.Context
import android.graphics.Bitmap
import pl.droidsonroids.gif.GifDrawable
import java.io.File

class GifCreator : IBitmapCreator {

    private var coverBitmap: Bitmap
    private var gifDrawable: GifDrawable

    constructor(context: Context, resId: Int) {
        gifDrawable = GifDrawable(context.resources, resId)
        gifDrawable.loopCount = 1
        coverBitmap = gifDrawable.currentFrame
    }

    constructor(context: Context, assetsName: String) {
        gifDrawable = GifDrawable(context.assets, assetsName)
        gifDrawable.loopCount = 1
        coverBitmap = gifDrawable.currentFrame
    }

    constructor(context: Context, file: File) {
        gifDrawable = GifDrawable(file)
        gifDrawable.loopCount = 1
        coverBitmap = gifDrawable.currentFrame
    }

    override fun generateBitmap(): Bitmap? {
        return gifDrawable.currentFrame
    }

    override fun coverImage(): Bitmap {
        return coverBitmap
    }

    override fun framesCount(): Int {
        return gifDrawable.numberOfFrames
    }

    override fun getIntrinsicWidth(): Int {
        return gifDrawable.intrinsicWidth
    }

    override fun getIntrinsicHeight(): Int {
        return gifDrawable.intrinsicHeight
    }

    override fun getDuration(): Int {
        return gifDrawable.duration
    }

    override fun getCurrentFrameIndex(): Int {
        return gifDrawable.currentFrameIndex + 1
    }

    override fun start() {
        gifDrawable.start()
    }

    override fun pause() {
        gifDrawable.pause()
    }

    override fun stop() {
        gifDrawable.stop()
    }

    override fun seekTo(duration: Int) {
        gifDrawable.seekTo(duration)
    }

    override fun seekToFrame(index: Int) {
        gifDrawable.seekToFrame(index)
    }

    override fun seekToFrameAndGet(index: Int): Bitmap? {
        return gifDrawable.seekToFrameAndGet(index)
    }

    override fun recycle() {
        if (!gifDrawable.isRecycled) {
            gifDrawable.recycle()
        }
    }

}