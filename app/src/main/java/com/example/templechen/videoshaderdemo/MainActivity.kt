package com.example.templechen.videoshaderdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.example.templechen.videoshaderdemo.player.ExoPlayerTool

class MainActivity : AppCompatActivity(), ExoPlayerTool.IVideoListener{

    private lateinit var glSurfaceView: VideoGLSurfaceView

    private var mPlayer : ExoPlayerTool? = null
    private lateinit var parentView: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glSurfaceView = VideoGLSurfaceView(this)
        glSurfaceView.setEGLContextClientVersion(3)
        setContentView(R.layout.activity_main)
        parentView = findViewById(R.id.parent)
        val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        parentView.addView(glSurfaceView, params)
        initPlayer()
        glSurfaceView.init(this, false)
    }

    private fun initPlayer() {
        mPlayer = ExoPlayerTool.getInstance(applicationContext)
        mPlayer?.quickSetting(
            this,
            "https://oimryzjfe.qnssl.com/content/1F3D7F815F2C6870FB512B8CA2C3D2C1.mp4"
        )
        mPlayer?.addVideoListener(this)
    }

    override fun onPause() {
        super.onPause()
        mPlayer?.setPlayWhenReady(false)
    }

    override fun onResume() {
        super.onResume()
        mPlayer?.setPlayWhenReady(true)
    }

    override fun onVideoSizeChanged(
        width: Int,
        height: Int,
        unappliedRotationDegrees: Int,
        pixelWidthHeightRatio: Float
    ) {
        val params = glSurfaceView.layoutParams
        val viewWidth = glSurfaceView.width
        val viewHeight = glSurfaceView.height
        val ratio = viewWidth * 1.0f / viewHeight
        val videoRatio = width * 1.0f / height
        if (ratio > videoRatio) {
            params.width = (viewHeight * videoRatio).toInt()
        } else {
            params.height = (viewWidth / videoRatio).toInt()
        }
        glSurfaceView.layoutParams = params
    }

    override fun onDestroy() {
        super.onDestroy()
        mPlayer?.release()
        glSurfaceView.destroy()
    }
}
