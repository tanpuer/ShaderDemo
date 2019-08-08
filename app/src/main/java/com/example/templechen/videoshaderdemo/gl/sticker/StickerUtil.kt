package com.example.templechen.videoshaderdemo.gl.sticker

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.view.View



class StickerUtil {

    companion object {

        var bitmap: Bitmap? = null

        fun convertViewToBitmap(view: View): Bitmap {
            val time = System.currentTimeMillis()
//            view.isDrawingCacheEnabled = true
//            bitmap = Bitmap.createBitmap(view.getDrawingCache())
//            view.isDrawingCacheEnabled = false
            bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_4444)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
//            Log.d("StickerUtil", (System.currentTimeMillis() - time).toString())
            return bitmap!!
        }
    }
}