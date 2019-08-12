package com.example.templechen.videoshaderdemo.album.filter.douyin.config

data class TextureConfig(
    val resId: Int,
    val startTime: Int,
    val duration: Int,
    val trimStart: Int = 0,
    val trimEnd: Int = 0
)