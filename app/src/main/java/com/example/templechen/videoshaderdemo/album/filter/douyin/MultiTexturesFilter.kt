package com.example.templechen.videoshaderdemo.album.filter.douyin

import android.content.Context
import com.example.templechen.videoshaderdemo.R
import com.example.templechen.videoshaderdemo.album.filter.AlbumFilter
import com.example.templechen.videoshaderdemo.album.filter.douyin.config.TextureConfig
import java.lang.RuntimeException
import kotlin.math.max

class MultiTexturesFilter(context: Context, textureConfigs: Array<TextureConfig>) :
    AlbumFilter(context, textureConfigs[0].resId) {

    companion object {
        private const val TEXTURE_MAX_SIZE = 8
    }

    private val filterList = mutableListOf<AlbumFilter>()

    init {
        if (textureConfigs.size > TEXTURE_MAX_SIZE) {
            throw RuntimeException("texture ")
        }
        times = 0
        textureConfigs.forEachIndexed { index, textureConfig ->
            val albumFilter = DouyinCircleFilter(context, textureConfig.resId)
//            val albumFilter = DouyinCircleFilter(context, R.drawable.test, true)
            albumFilter.times = textureConfig.duration
            albumFilter.startTime = textureConfig.startTime
            albumFilter.trimStart = textureConfig.trimStart
            albumFilter.trimEnd = textureConfig.trimEnd
            albumFilter.textureIndex = index
            filterList.add(albumFilter)
            times = max(times, textureConfig.startTime + textureConfig.duration)
        }
    }

    override fun initFilter() {
        if (initedProgram) {
            return
        }
        filterList.forEach {
            it.initFilter()
        }
        initedProgram = true
    }

    override fun initProgram() {
        filterList.forEach {
            it.initProgram()
        }
    }

    override fun initTexture() {
        filterList.forEach {
            it.initTexture()
        }
    }

    override fun setViewSize(width: Int, height: Int) {
        filterList.forEach {
            it.setViewSize(width, height)
        }
    }

    override fun drawFrame() {
        filterList.forEach {
            it.drawFrame()
        }
        currentIndex++
    }

    override fun release() {
        filterList.forEach {
            it.release()
        }
        initedProgram = false
        currentIndex = 0
    }

    override fun reset() {
        filterList.forEach {
            it.reset()
        }
        initedProgram = false
        currentIndex = 0
    }


}