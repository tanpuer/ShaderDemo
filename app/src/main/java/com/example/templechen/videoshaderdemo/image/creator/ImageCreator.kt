package com.example.templechen.videoshaderdemo.image.creator

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File

open class ImageCreator : IBitmapCreator {

    private lateinit var coverBitmap: Bitmap

    constructor()

    constructor(context: Context, resId: Int) {
        val options = BitmapFactory.Options()
        options.inScaled = false
        coverBitmap = BitmapFactory.decodeResource(context.resources, resId, options)
    }

    constructor(context: Context, assetsName: String) {
        coverBitmap = BitmapFactory.decodeStream(context.assets.open(assetsName))
    }

    constructor(context: Context, file: File) {
        val options = BitmapFactory.Options()
        options.inScaled = false
        coverBitmap = BitmapFactory.decodeFile(file.absolutePath)
    }

    override fun generateBitmap(): Bitmap? {
        return null
    }

    override fun coverImage(): Bitmap {
        return coverBitmap
    }

    override fun framesCount(): Int {
        return 1
    }

    override fun getIntrinsicWidth(): Int {
        return coverBitmap.height
    }

    override fun getIntrinsicHeight(): Int {
        return coverBitmap.width
    }
}