package com.example.templechen.videoshaderdemo.snaphelper

import android.graphics.SurfaceTexture
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.templechen.videoshaderdemo.R
import com.example.templechen.videoshaderdemo.fake.SimpleGLFakeView
import com.example.templechen.videoshaderdemo.player.ExoPlayerTool
import kotlinx.android.synthetic.main.fragment_video.*

class VideoFragment(private val fakeView: SimpleGLFakeView?) : Fragment(), TextureView.SurfaceTextureListener, SurfaceHolder.Callback{

    private lateinit var player: ExoPlayerTool
    private var surface: Surface? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutInflater.inflate(R.layout.fragment_video, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        player = ExoPlayerTool.getInstance(context!!.applicationContext)
//        texture_view.surfaceTextureListener = this
        surface_view.holder.addCallback(this)
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {

    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        fakeView?.stopRenderAnotherSurface()
        return true
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        this.surface = Surface(surface)
        fakeView?.renderAnotherSurface(this.surface)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        fakeView?.stopRenderAnotherSurface()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        this.surface = holder?.surface
        fakeView?.renderAnotherSurface(this.surface)
    }
}