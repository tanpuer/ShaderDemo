package com.example.templechen.videoshaderdemo.player

import android.content.Context
import com.danikula.videocache.HttpProxyCacheServer
import com.danikula.videocache.file.FileNameGenerator
import java.io.File
import java.net.URI
import java.net.URISyntaxException

class PlayerCacheServer(application: Context) {

    private val proxy = HttpProxyCacheServer.Builder(application)
        .maxCacheSize(100 * 1024 * 1024)
        .cacheDirectory(File(application.cacheDir, "cacheServer"))
        .fileNameGenerator(VideoFileNameGenerator())
        .build()

    inner class VideoFileNameGenerator : FileNameGenerator {

        override fun generate(url: String?): String {
            return if (url == null) "" else getUrlWithoutQuery(url)
        }

    }

    fun getProxyUrl(url: String): String {
        return proxy.getProxyUrl(url)
    }

    fun isCached(url: String): Boolean {
        return proxy.isCached(url)
    }

    fun preload(url: String) {

    }

    fun getUrlWithoutQuery(url: String): String {
        return try {
            val uri = URI(url)
            URI(
                uri.scheme,
                uri.authority,
                uri.path,
                null, // Ignore the query part of the input url
                uri.fragment
            ).toString()
        } catch (e: URISyntaxException) {
            url
        }
    }

    companion object {

        private var cacheServer: PlayerCacheServer? = null

        fun getInstance(context: Context): PlayerCacheServer {
            if (cacheServer == null) {
                cacheServer = PlayerCacheServer(context)
            }
            return cacheServer!!
        }

    }
}