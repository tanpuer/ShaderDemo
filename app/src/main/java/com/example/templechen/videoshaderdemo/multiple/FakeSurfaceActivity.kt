package com.example.templechen.videoshaderdemo.multiple

import android.os.Bundle
import android.os.Environment
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.templechen.videoshaderdemo.R
import com.example.templechen.videoshaderdemo.fake.SimpleGLFakeView
import com.example.templechen.videoshaderdemo.player.ExoPlayerTool
import kotlinx.android.synthetic.main.activity_multiple_player.*
import java.io.File

class FakeSurfaceActivity : AppCompatActivity() {

    private lateinit var player: ExoPlayerTool
    private lateinit var fakeView: SimpleGLFakeView
    private var surfaceView: SurfaceView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiple_player)

        player = ExoPlayerTool.getInstance(this.applicationContext)
        player.quickSetting(
            this,
            "file://${File(Environment.getExternalStorageDirectory().absolutePath, "trailer.mp4")}"
        )
        fakeView = SimpleGLFakeView(this, player)
        fakeView.startDoFrame()

        resetSurfaceBtn.setOnClickListener {
            if (surfaceView == null) {
                addSurfaceView()
            } else {
                removeSurfaceView()
            }
        }

    }

    override fun onPause() {
        super.onPause()
        fakeView.cancelDoFrame()
    }

    override fun onResume() {
        super.onResume()
        fakeView.startDoFrame()
    }

    override fun onDestroy() {
        super.onDestroy()
        fakeView.sendShutDown()
    }

    private fun addSurfaceView() {
        surfaceView = SurfaceView(this)
        val layoutParams = RelativeLayout.LayoutParams(1280, 720)
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
        parentView.addView(surfaceView, layoutParams)
        surfaceView?.holder?.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder?) {
                fakeView.renderAnotherSurface(holder?.surface)
            }

            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                fakeView.stopRenderAnotherSurface()
            }
        })
    }

    private fun removeSurfaceView() {
        if (surfaceView?.parent == parentView) {
            parentView.removeView(surfaceView)
            surfaceView = null
        }
    }

}