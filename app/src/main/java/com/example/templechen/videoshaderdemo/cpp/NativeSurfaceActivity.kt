package com.example.templechen.videoshaderdemo.cpp

import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import com.example.templechen.videoshaderdemo.R
import com.example.templechen.videoshaderdemo.player.ExoPlayerTool
import java.io.File

class NativeSurfaceActivity : AppCompatActivity(), ExoPlayerTool.IVideoListener {

    private lateinit var mPlayer: ExoPlayerTool

    private lateinit var nativeSurfaceView: NativeSurfaceView
    private lateinit var changeBgColorBtn: Button
    private lateinit var rotateBtn: Button
    private var rotateIndex = 0
    private var resume = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native_surface)
        mPlayer = ExoPlayerTool.getInstance(applicationContext)
        mPlayer.setLoop(true)
        mPlayer.addVideoListener(this)
        //mp4竖屏 //mov横屏
        mPlayer.quickSetting(
            this,
//            "file://${File(this.cacheDir, "gltest.mp4")}"
            "file://${File(Environment.getExternalStorageDirectory().absolutePath, "trailer.mp4")}"
//            "file://${File(Environment.getExternalStorageDirectory().absolutePath, "trailer.mov")}"
        )

        nativeSurfaceView = findViewById(R.id.surface_view)
        nativeSurfaceView.player = mPlayer

        changeBgColorBtn = findViewById(R.id.changeBgColorBtn)
        changeBgColorBtn.setOnClickListener {
            nativeSurfaceView.setBackgroundColor(
                floatArrayOf(
                    Math.random().toFloat() * 1000000,
                    Math.random().toFloat() * 1000000, Math.random().toFloat() * 1000000, 1.0f
                )
            )
        }
        rotateBtn = findViewById(R.id.rotateBtn)
        rotateBtn.setOnClickListener {
            rotateIndex++
            if (rotateIndex > 3) {
                rotateIndex = 0
            }
            nativeSurfaceView.setRotate(rotateIndex * 90)
        }
    }

    override fun onDestroy() {
        nativeSurfaceView.destroy()
        super.onDestroy()
        mPlayer.release()
    }

    override fun onResume() {
        super.onResume()
        if (resume) {
            mPlayer.playWhenReady = true
        }
        resume = true
    }

    override fun onPause() {
        super.onPause()
        mPlayer.playWhenReady = false
    }

    override fun onVideoSizeChanged(
        width: Int,
        height: Int,
        unappliedRotationDegrees: Int,
        pixelWidthHeightRatio: Float
    ) {
        nativeSurfaceView.setVideoSize(width, height)
    }
}