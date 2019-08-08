package com.example.templechen.videoshaderdemo.gl.gesture

interface IGestureListener {

    fun onScale(ratio: Float) {

    }

    fun onTransform(transformX: Float, transformY: Float) {

    }

    fun onRotate(degrees: Int) {

    }

}