package com.example.templechen.videoshaderdemo.image

import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.SeekBar
import com.example.templechen.videoshaderdemo.R
import com.example.templechen.videoshaderdemo.image.creator.BitmapCreatorFactory
import com.example.templechen.videoshaderdemo.image.offscreen.OffScreenImageRenderThread
import kotlinx.android.synthetic.main.activity_dynamic_cropping.*
import kotlinx.android.synthetic.main.activity_image_surface.*

class ImageSurfaceActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    private lateinit var handler: ImageSurfaceHandler
    private lateinit var seekbar: SeekBar
    private lateinit var offscreenBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_surface)
        seekbar = findViewById(R.id.seekbar)
        seekbar.setOnSeekBarChangeListener(this)
        handler = ImageSurfaceHandler(this)
        surface_view.initViews(handler, "")
        offscreenBtn = findViewById(R.id.offScreenBtn)
        offscreenBtn.setOnClickListener {
            val offScreenImageRenderThread = OffScreenImageRenderThread(
                this,
//                BitmapCreatorFactory.newImageCreator(R.drawable.dynamic_cropping_ready2, BitmapCreatorFactory.TYPE_IMAGE, this),
                BitmapCreatorFactory.newImageCreator(R.drawable.test, BitmapCreatorFactory.TYPE_GIF, this),
                Environment.getExternalStorageDirectory().absolutePath + "/trailer_test.mp4"
            )
            offScreenImageRenderThread.start()
            offScreenImageRenderThread.waitUntilReady()
            offScreenImageRenderThread.offscreenImageHandler.prepare()
            offScreenImageRenderThread.offscreenImageHandler.startCrop()
        }
    }

    fun setTotalFrames(count: Int) {
        seekbar.max = count
    }

    fun setCurrentFrame(count: Int) {
        seekbar.progress = count
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        if (seekBar != null) {
            surface_view.pause()
        }
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        if (seekBar != null) {
            surface_view.seekTo(seekBar.progress)
            surface_view.start()
        }
    }
}