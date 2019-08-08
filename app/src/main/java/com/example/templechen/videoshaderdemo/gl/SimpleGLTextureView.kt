package com.example.templechen.videoshaderdemo.gl

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.view.Choreographer
import android.view.Surface
import android.view.TextureView
import android.view.View
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.gl.render.RenderThread
import com.example.templechen.videoshaderdemo.gl.sticker.StickerUtil
import com.example.templechen.videoshaderdemo.player.ExoPlayerTool
import com.example.templechen.videoshaderdemo.player.IExoPlayer

open class SimpleGLTextureView :
    TextureView, TextureView.SurfaceTextureListener, Choreographer.FrameCallback, SimpleGLView {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var mActivityHandler: ActivityHandler? = null
    private lateinit var mPlayer: IExoPlayer
    protected var renderThread: RenderThread? = null
    private var mSurface: Surface? = null
    private var mFilterType = 0
    private var mStickerView: View? = null

    override fun initViews(activityHandler: ActivityHandler?, playerTool: IExoPlayer, filterType: Int) {
        mActivityHandler = activityHandler
        mPlayer = playerTool
        mFilterType = filterType
        surfaceTextureListener = this
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        mSurface = Surface(surface)
        renderThread =
            RenderThread(
                context,
                mSurface!!,
                mActivityHandler,
                GLUtils.getDisplayRefreshNsec(context as Activity),
                mPlayer,
                "BaseFilter"
            )
        renderThread?.start()
        renderThread?.waitUtilReady()
        val renderHandler = renderThread?.mHandler
        renderHandler?.sendSurfaceCreated(mFilterType)
        Choreographer.getInstance().postFrameCallback(this)
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
        val renderHandler = renderThread?.mHandler
        renderHandler?.sendSurfaceChanged(0, width, height)
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        mSurface = null
        val renderHandler = renderThread?.mHandler
        renderHandler?.sendShutDown()
        renderThread?.join()
        renderThread = null
        Choreographer.getInstance().removeFrameCallback(this)
        return true
    }

    override fun doFrame(frameTimeNanos: Long) {
        val renderHandler = renderThread?.mHandler
        Choreographer.getInstance().postFrameCallback(this)
        if (mStickerView != null && mFilterType == 3) {
            renderHandler?.setCustomWaterMark(StickerUtil.convertViewToBitmap(mStickerView!!))
        }
        renderHandler?.sendDoFrame(frameTimeNanos)
    }

    override fun startRecording() {
        val renderHandler = renderThread?.mHandler
        renderHandler?.sendStartEncoder()
    }

    override fun stopRecording() {
        val renderHandler = renderThread?.mHandler
        renderHandler?.sendStopEncoder()
    }

    override fun changeFilter(type: Int) {
        val renderHandler = renderThread?.mHandler
        mFilterType = type
        renderHandler?.changeFilter(type)
    }

    override fun renderAnotherSurface(surface: Surface?) {
        val renderHandler = renderThread?.mHandler
        renderHandler?.renderAnotherSurface(surface)
    }

    override fun stopRenderAnotherSurface() {
        val renderHandler = renderThread?.mHandler
        renderHandler?.stopRenderAnotherSurface()
    }

    override fun getView(): View {
        return this
    }

    override fun setVideoEditorRect(rect: Rect) {
        val renderHandler = renderThread?.mHandler
        renderHandler?.setVideoEditorRect(rect)
    }

    override fun setCustomStickerView(view: View?) {
        mStickerView = view
    }

    fun setCustomWaterMarkRectF(rectF: RectF) {
        val renderHandler = renderThread?.mHandler
        renderHandler?.setCustomWaterRect(rectF)
    }

}