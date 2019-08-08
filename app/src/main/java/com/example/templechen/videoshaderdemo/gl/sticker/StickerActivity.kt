package com.example.templechen.videoshaderdemo.gl.sticker

import android.graphics.RectF
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.templechen.videoshaderdemo.R
import com.example.templechen.videoshaderdemo.gl.ActivityHandler
import com.example.templechen.videoshaderdemo.gl.IGLInfoCallback
import com.example.templechen.videoshaderdemo.gl.SimpleGLSurfaceView
import com.example.templechen.videoshaderdemo.player.ExoPlayerTool

class StickerActivity : AppCompatActivity(), ExoPlayerTool.IVideoListener, IGLInfoCallback {

    companion object {
        private const val TAG = "StickerActivity"
    }

    private lateinit var simpleGLSurfaceView: SimpleGLSurfaceView
    private lateinit var fpsView: TextView
    private lateinit var glVersionView: TextView
    private lateinit var mPlayer: ExoPlayerTool
    private lateinit var mStickerView: StickerImageView
    private var mStickerViewRectF = RectF()
    private lateinit var mActivityHandler: ActivityHandler
    private lateinit var mStartRecordBtn: Button
    private lateinit var mStopRecordBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sticker)
        mPlayer = ExoPlayerTool.getInstance(applicationContext)
        mPlayer.quickSetting(
            this,
            "https://oimryzjfe.qnssl.com/content/1F3D7F815F2C6870FB512B8CA2C3D2C1.mp4"
        )
        simpleGLSurfaceView = findViewById(R.id.simple_gl_surface_view)
        mActivityHandler = ActivityHandler(this)
        simpleGLSurfaceView.initViews(mActivityHandler, mPlayer, 4)
        mPlayer.addVideoListener(this)

        //sticker
        mStickerView = findViewById(R.id.image)
        Glide.with(this).load(R.drawable.ic_screenroom_playlist_playing_gif).into(mStickerView)
        initSticker()

        //fps and version
        fpsView = findViewById(R.id.fps)
        glVersionView = findViewById(R.id.gl_version)

        //start and stop record btn
        mStartRecordBtn = findViewById(R.id.start_btn)
        mStartRecordBtn.setOnClickListener {
            simpleGLSurfaceView.startRecording()
        }
        mStopRecordBtn = findViewById(R.id.stop_btn)
        mStopRecordBtn.setOnClickListener {
            simpleGLSurfaceView.stopRecording()
        }
    }

    private fun initSticker() {
        mStickerView.setOnStickerViewClickListener(object : IStickerView.OnStickerViewClickListener {
            override fun onStickerViewClicked(stickerView: IStickerView) {
                simpleGLSurfaceView.setCustomStickerView(stickerView.getView())
            }
        })
        mStickerView.setOnStickerViewScrollListener(object : IStickerView.OnStickerViewScroll {
            override fun stickerViewScroll(stickerView: IStickerView) {
                //calculate StickerImageView's rect relative to SimpleGLSurfaceView
                if (stickerView.getView().left < simpleGLSurfaceView.right
                    && stickerView.getView().top < simpleGLSurfaceView.bottom
                    && stickerView.getView().right > simpleGLSurfaceView.left
                    && stickerView.getView().bottom > simpleGLSurfaceView.top
                ) {
                    //gl coordinate is (-1, -1)-> (1,1); texture coordinate is (0,0)->(1,1) , be careful
                    val centerX = (simpleGLSurfaceView.left + simpleGLSurfaceView.right) / 2
                    val centerY = ((simpleGLSurfaceView.top) + simpleGLSurfaceView.bottom) / 2
                    mStickerViewRectF.set(
                        (stickerView.getView().left - centerX) * 2.0f / simpleGLSurfaceView.width,
                        (centerY - stickerView.getView().top) * 2.0f / simpleGLSurfaceView.height,
                        (stickerView.getView().right - centerX) * 2.0f / simpleGLSurfaceView.width,
                        (centerY - stickerView.getView().bottom) * 2.0f / simpleGLSurfaceView.height
                    )
                    simpleGLSurfaceView.setCustomWaterMarkRectF(mStickerViewRectF)
                }
            }
        })
    }

    override fun onVideoSizeChanged(
        width: Int,
        height: Int,
        unappliedRotationDegrees: Int,
        pixelWidthHeightRatio: Float
    ) {
        val params = simpleGLSurfaceView.layoutParams
        val viewWidth = simpleGLSurfaceView.width
        val viewHeight = simpleGLSurfaceView.height
        val ratio = viewWidth * 1.0f / viewHeight
        val videoRatio = width * 1.0f / height
        if (ratio > videoRatio) {
            params.width = (viewHeight * videoRatio).toInt()
        } else {
            params.height = (viewWidth / videoRatio).toInt()
        }
        simpleGLSurfaceView.layoutParams = params
    }

    override fun onDestroy() {
        super.onDestroy()
        mPlayer.release()
    }

    override fun updateFps(tfps: Int, dropped: Int) {
        fpsView.text = "Frame rate: ${tfps / 1000.0f}fps (${dropped} dropped)"
    }

    override fun updateGLVersion(version: Int) {
        glVersionView.text = "GLES Version: ${version}"
    }
}