package com.example.templechen.videoshaderdemo.album.filter

import android.content.Context
import android.opengl.Matrix

class AlbumScaleFilter : AlbumFilter {

    constructor(context: Context) : super(context)
    constructor(context: Context, resId: Int) : super(context, resId)
    constructor(context: Context, resId: Int, isGif: Boolean) : super(context, resId, isGif)


    private var scale = 1f

    override fun drawFrame() {
        scale += 0.5f / times
        if (scale > 1.5f) {
            scale = 1f
        }
        scaleBitmap()
        super.drawFrame()
    }

    private fun scaleBitmap() {
        Matrix.setIdentityM(baseMVPMatrix, 0)
        Matrix.scaleM(baseMVPMatrix, 0, scale * originScaleX, scale * originScaleY, 1f)
    }

    override fun reset() {
        super.reset()
        scale = 1f
    }
}