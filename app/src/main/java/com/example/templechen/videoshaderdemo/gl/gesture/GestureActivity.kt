package com.example.templechen.videoshaderdemo.gl.gesture

import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import com.example.templechen.videoshaderdemo.R
import com.example.templechen.videoshaderdemo.player.ExoPlayerTool
import com.example.templechen.videoshaderdemo.player.ExoPlayerTool2
import kotlinx.android.synthetic.main.activity_gesture_surfaceview.*
import java.io.File

class GestureActivity : AppCompatActivity(), ExoPlayerTool.IVideoListener, View.OnClickListener, IGestureListener {

    private lateinit var gestureSurfaceView: GestureSurfaceView
    private lateinit var player: ExoPlayerTool
    private lateinit var changeBgColorBtn: Button
    private lateinit var rotateBtn: Button
    private var degrees = 0

    private lateinit var overlayPlayer: ExoPlayerTool2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gesture_surfaceview)
        gestureSurfaceView = findViewById(R.id.surface_view)

        player = ExoPlayerTool.getInstance(applicationContext)
        player.setLoop(true)
        player.quickSetting(
            this,
            "file://${File(Environment.getExternalStorageDirectory().absolutePath, "trailer.mp4")}"
        )
        overlayPlayer = ExoPlayerTool2(applicationContext)
        overlayPlayer.setLoop(true)
        overlayPlayer.quickSetting(
            this,
            "file://${File(Environment.getExternalStorageDirectory().absolutePath, "trailer111.mp4")}"
        )
        gestureSurfaceView.overlayPlayer = overlayPlayer
        gestureSurfaceView.initViews(null, player, 17)
        gestureSurfaceView.gestureEnable = true

        player.addVideoListener(this)

        changeBgColorBtn = findViewById(R.id.changeBgColorBtn)
        changeBgColorBtn.setOnClickListener(this)
        rotateBtn = findViewById(R.id.rotateBtn)
        rotateBtn.setOnClickListener(this)

        gestureSurfaceView.mGestureListener = this

        overlayPlayer.addVideoListener(object : ExoPlayerTool2.IVideoListener {
            override fun onVideoSizeChanged(
                width: Int,
                height: Int,
                unappliedRotationDegrees: Int,
                pixelWidthHeightRatio: Float
            ) {
                gestureSurfaceView.setOverlayVideoSize(width, height)
            }
        })
    }

    override fun onVideoSizeChanged(
        width: Int,
        height: Int,
        unappliedRotationDegrees: Int,
        pixelWidthHeightRatio: Float
    ) {
        rect_view.setVideoSizeAndViewSize(width, height, surface_view.width, surface_view.height)
        gestureSurfaceView.setVideoSize(width, height)
    }

    override fun onPause() {
        super.onPause()
        player.playWhenReady = false
        overlayPlayer.playWhenReady = false
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
        overlayPlayer.release()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.changeBgColorBtn -> {
                gestureSurfaceView.setBackgroundColor(
                    floatArrayOf(
                        Math.random().toFloat(),
                        Math.random().toFloat(), Math.random().toFloat(), 1.0f
                    )
                )
            }
            R.id.rotateBtn -> {
                degrees++
                if (degrees > 3) {
                    degrees = 0
                }
                gestureSurfaceView.setRotate(degrees * 90)
            }
        }
    }

    override fun onScale(ratio: Float) {
        rect_view.sendScale(ratio)
    }

    override fun onTransform(transformX: Float, transformY: Float) {
        rect_view.sendTransform(transformX, transformY)
    }

    override fun onRotate(degrees: Int) {
        rect_view.setRotate(degrees)
    }

}