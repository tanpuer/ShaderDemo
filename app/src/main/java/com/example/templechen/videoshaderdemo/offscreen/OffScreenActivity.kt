package com.example.templechen.videoshaderdemo.offscreen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.templechen.videoshaderdemo.R
import com.example.templechen.videoshaderdemo.player.ExoPlayerTool
import java.io.File

class OffScreenActivity : AppCompatActivity(), View.OnClickListener, SurfaceHolder.Callback, ExoPlayerTool.IVideoListener{

    companion object {
        const val PERMISSION_CODE = 1001
        const val TAG = "OffScreenActivity"
    }

    private lateinit var mStartBtn: Button
    private lateinit var mOffScreenRenderThread: OffScreenRenderThread
    private lateinit var mDurationText: TextView
    private lateinit var mOffscreenActivityHandler: OffScreenActivityHandler
    private var startTime = -1L
    private lateinit var mSurfaceView: SurfaceView
    private lateinit var mPlayBtn: Button
    private var mPlayer: ExoPlayerTool? = null
    private var mSurface: Surface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offscreen)
        mStartBtn = findViewById(R.id.start_btn)
        mStartBtn.setOnClickListener(this)
        mDurationText = findViewById(R.id.duration_txt)

        if (Build.VERSION.SDK_INT >= 21 && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_CODE
            )
        } else {
//            initRenderThread()
        }

        mSurfaceView = findViewById(R.id.surface)
        mSurfaceView.holder.addCallback(this)
        mPlayBtn = findViewById(R.id.play)
        mPlayBtn.setOnClickListener(this)
        mPlayer = ExoPlayerTool.getInstance(applicationContext)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            initRenderThread()
        } else {
            Toast.makeText(this, "must need write external storage permission!", Toast.LENGTH_LONG).show()
        }
    }

    fun setDuration() {
        mDurationText.text = "Duration : ${(System.currentTimeMillis() - startTime) / 1000f}"
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.start_btn -> {
                initRenderThread()
                mOffScreenRenderThread.mRenderHandler.startOffscreenRender()
                startTime = System.currentTimeMillis()
            }
            R.id.play -> {
                mPlayer?.quickSetting(
                    this,
            "file://${File(Environment.getExternalStorageDirectory().absolutePath + "/trailer_test.mp4")}"
                )
                mPlayer?.addVideoListener(this)
                mPlayer?.setVideoSurface(mSurface!!)
                mPlayer?.setPlayWhenReady(true)
            }
        }
    }

    private fun initRenderThread() {
        val file = File(Environment.getExternalStorageDirectory().absolutePath + "/trailer.mp4")
        if (!file.exists()) {
            Toast.makeText(this, "no mp4 in sdcard, please check", Toast.LENGTH_LONG).show()
            return
        }
        mOffscreenActivityHandler = OffScreenActivityHandler(this)
        mOffScreenRenderThread = OffScreenRenderThread(
            this,
            file,
            mOffscreenActivityHandler
        )
        mOffScreenRenderThread.start()
        mOffScreenRenderThread.waitUntilReady()
        mOffScreenRenderThread.mRenderHandler.prepareOffscreenRender()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        mSurface = holder?.surface
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        mSurface = null
    }

    override fun onVideoSizeChanged(
        width: Int,
        height: Int,
        unappliedRotationDegrees: Int,
        pixelWidthHeightRatio: Float
    ) {
        val params = mSurfaceView.layoutParams
        val viewWidth = mSurfaceView.width
        val viewHeight = mSurfaceView.height
        val ratio = viewWidth * 1.0f / viewHeight
        val videoRatio = width * 1.0f / height
        if (ratio > videoRatio) {
            params.width = (viewHeight * videoRatio).toInt()
        } else {
            params.height = (viewWidth / videoRatio).toInt()
        }
        mSurfaceView.layoutParams = params
    }

    override fun onPause() {
        super.onPause()
        mPlayer?.setPlayWhenReady(false)
    }

    override fun onResume() {
        super.onResume()
        mPlayer?.setPlayWhenReady(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPlayer?.release()
    }
}