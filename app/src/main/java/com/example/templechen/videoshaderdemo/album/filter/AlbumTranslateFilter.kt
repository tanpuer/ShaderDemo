package com.example.templechen.videoshaderdemo.album.filter

import android.content.Context
import android.opengl.Matrix

class AlbumTranslateFilter : AlbumFilter {

    private var translate = -2f
    private var scale = 0.5f
    private var index = 0

    constructor(context: Context) : super(context)
    constructor(context: Context, resId: Int) : super(context, resId)
    constructor(context: Context, resId: Int, isGif: Boolean) : super(context, resId, isGif)


    override fun drawFrame() {
        calculateTranslate()
        super.drawFrame()
    }

    private fun calculateTranslate() {
        translate += 1f / refreshRate
        index++
        if (index > times / 2) {
            scale -= 1f / times
        } else {
            scale += 1f / times
        }
        Matrix.setIdentityM(baseMVPMatrix, 0)
        Matrix.scaleM(baseMVPMatrix, 0, originScaleX * scale, originScaleY * scale, 1f)
        Matrix.translateM(baseMVPMatrix, 0, translate, 0f, 0f)
    }

    override fun reset() {
        super.reset()
        translate = -2f
        scale = 0.5f
        index = 0
    }
}