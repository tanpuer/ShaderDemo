package com.example.templechen.videoshaderdemo

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.SeekBar
import com.example.templechen.videoshaderdemo.player.ExoPlayerTool
import java.io.File
import java.util.*

class PurePlayActivity : AppCompatActivity(), SurfaceHolder.Callback, ExoPlayerTool.IVideoListener,
    SeekBar.OnSeekBarChangeListener {

    private lateinit var mParentView: RelativeLayout
    private lateinit var mSurfaceView: SurfaceView
    private lateinit var mFrameLayout: FrameLayout
    private lateinit var mPlayer: ExoPlayerTool
    private lateinit var mSeekBar: SeekBar
    private val timer = Timer()
    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mParentView = RelativeLayout(this)
        mParentView.layoutParams =
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setContentView(mParentView)

        mFrameLayout = FrameLayout(this, null)
        val roundFrameLayout =
            RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        roundFrameLayout.addRule(RelativeLayout.CENTER_IN_PARENT)
        mParentView.addView(mFrameLayout, roundFrameLayout)

        mSurfaceView = SurfaceView(this)
        val layoutParams =
            FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        mFrameLayout.addView(mSurfaceView, layoutParams)

        mSurfaceView.holder.addCallback(this)

        mPlayer = ExoPlayerTool.getInstance(applicationContext)
        mPlayer.quickSetting(
            this,
            "file://${File(Environment.getExternalStorageDirectory().absolutePath, "trailer_test.mp4")}"
        )
//        mPlayer.quickSetting(
//            this,
//            "https://oimryzjfe.qnssl.com/content/1F3D7F815F2C6870FB512B8CA2C3D2C1.mp4"
//        )

        mPlayer.addVideoListener(this)

        mSeekBar = SeekBar(this)
        val seekBarLayoutParams =
            RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        seekBarLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        seekBarLayoutParams.bottomMargin = 50
        mParentView.addView(mSeekBar, seekBarLayoutParams)
        mSeekBar.setOnSeekBarChangeListener(this)

        handler = Handler(mainLooper)
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                handler.post {
                    if (seekBarSettable) {
                        if (mPlayer.duration > 0) {
                            mSeekBar.progress = (mPlayer.currentPosition * 100 / mPlayer.duration).toInt()
                        }
                    }
                }
            }
        }, 0, 500)
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        mPlayer.setVideoSurface(holder?.surface!!)
        mPlayer.setPlayWhenReady(true)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        mPlayer.setPlayWhenReady(false)
    }

    override fun onVideoSizeChanged(
        width: Int,
        height: Int,
        unappliedRotationDegrees: Int,
        pixelWidthHeightRatio: Float
    ) {
        val params = mFrameLayout.layoutParams
        val viewWidth = mFrameLayout.width
        val viewHeight = mFrameLayout.height
        val ratio = viewWidth * 1.0f / viewHeight
        val videoRatio = width * 1.0f / height
        if (ratio > videoRatio) {
            params.width = (viewHeight * videoRatio).toInt()
            params.height = getScreenHeight(this)
        } else {
            params.height = (viewWidth / videoRatio).toInt()
            params.width = getScreenWidth(this)
        }
        mFrameLayout.layoutParams = params
    }

    override fun onDestroy() {
        super.onDestroy()
        mPlayer.release()
    }

    private var seekBarSettable = true
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        seekBarSettable = false
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        if (seekBar != null) {
            mPlayer.seekTo((seekBar.progress / 100f * mPlayer.duration).toLong())
        }
        seekBarSettable = true
    }

    fun getScreenWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    fun getScreenHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }
}