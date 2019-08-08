package com.example.templechen.videoshaderdemo.album

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.templechen.videoshaderdemo.R
import com.example.templechen.videoshaderdemo.album.offscreen.AlbumOffScreenRenderThread
import kotlinx.android.synthetic.main.activity_album.*

class AlbumActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album)
        off_screen_btn.setOnClickListener {
            val thread = AlbumOffScreenRenderThread(this)
            thread.start()
            thread.waitUtilReady()
            thread.mHandler?.startEncode()
        }
    }

}