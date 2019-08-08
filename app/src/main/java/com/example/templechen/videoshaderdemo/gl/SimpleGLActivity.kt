package com.example.templechen.videoshaderdemo.gl

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.*
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.templechen.videoshaderdemo.R
import com.example.templechen.videoshaderdemo.editor.SimpleEditorView
import com.example.templechen.videoshaderdemo.filter.FilterListUtil
import com.example.templechen.videoshaderdemo.player.ExoPlayerTool
import java.io.File

class SimpleGLActivity : AppCompatActivity(), ExoPlayerTool.IVideoListener, SurfaceHolder.Callback, IGLInfoCallback{

    companion object {
        private const val TAG = "SimpleGLActivity"
        private val LIST = FilterListUtil.LIST
    }

    private lateinit var simpleGLSurfaceView: SimpleGLSurfaceView
    private lateinit var parentView: RelativeLayout
    private lateinit var mPlayer: ExoPlayerTool
    private lateinit var mActivityHandler: ActivityHandler
    private lateinit var fpsView: TextView
    private lateinit var glVersionView: TextView
    private lateinit var startBtn: Button
    private lateinit var stopBtn: Button
    private var isRecording = false
    private lateinit var filterRecyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var anotherSurfaceView: SurfaceView
    private lateinit var anotherSurface: Surface
    private var renderAnotherSurfaceEnable = false
    private lateinit var videoEditorView: SimpleEditorView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_gl)
        parentView = findViewById(R.id.parent_view)

        mActivityHandler = ActivityHandler(this)
        mPlayer = ExoPlayerTool.getInstance(applicationContext)
        mPlayer.quickSetting(
            this,
            "file://${File(Environment.getExternalStorageDirectory().absolutePath, "trailer.mp4")}"
        )
        mPlayer.setLoop(true)
        simpleGLSurfaceView = findViewById(R.id.simple_gl_surface_view)
        simpleGLSurfaceView.initViews(mActivityHandler, mPlayer, 0)
        mPlayer.addVideoListener(this)

        //fps view
        fpsView = TextView(this)
        val fpsViewParams =
            RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        fpsView.textSize = 16f
        fpsView.setTextColor(Color.BLACK)
        fpsViewParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        parentView.addView(fpsView, fpsViewParams)

        //glVersion view
        glVersionView = TextView(this)
        val glVersionViewParams =
            RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        glVersionView.textSize = 16f
        glVersionView.setTextColor(Color.BLACK)
        //貌似不生效啊
//        glVersionViewParams.addRule(RelativeLayout.BELOW, fpsView.id)
        glVersionViewParams.topMargin = 100
        parentView.addView(glVersionView, glVersionViewParams)

        //start stop btn
        startBtn = findViewById(R.id.start_btn)
        startBtn.setOnClickListener {
            isRecording = true
            simpleGLSurfaceView.startRecording()
        }
        stopBtn = findViewById(R.id.stop_btn)
        stopBtn.setOnClickListener {
            isRecording = false
            simpleGLSurfaceView.stopRecording()
        }

        //filter RecyclerView
        filterRecyclerView = findViewById(R.id.filter_recycler_view)
        filterRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            this,
            androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
            false
        )
        filterRecyclerView.adapter = FilterAdapter(this, LIST)
        filterRecyclerView.setBackgroundColor(Color.GRAY)

        //another surface
        anotherSurfaceView = findViewById(R.id.another_surface)
        anotherSurfaceView.holder.addCallback(this)
        anotherSurfaceView.setOnClickListener {
            if (!renderAnotherSurfaceEnable) {
                simpleGLSurfaceView.renderAnotherSurface(anotherSurface)
            } else {
                simpleGLSurfaceView.stopRenderAnotherSurface()
            }
            renderAnotherSurfaceEnable = !renderAnotherSurfaceEnable
        }

        //video editor
        videoEditorView = findViewById(R.id.video_editor_view)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        anotherSurface = holder!!.surface
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
    }

    override fun onVideoSizeChanged(
        width: Int,
        height: Int,
        unappliedRotationDegrees: Int,
        pixelWidthHeightRatio: Float
    ) {
        val params = simpleGLSurfaceView.getView().layoutParams
        val viewWidth = simpleGLSurfaceView.getView().width
        val viewHeight = simpleGLSurfaceView.getView().height
        val ratio = viewWidth * 1.0f / viewHeight
        val videoRatio = width * 1.0f / height
        if (ratio > videoRatio) {
            params.width = (viewHeight * videoRatio).toInt()
            params.height = viewHeight
        } else {
            params.height = (viewWidth / videoRatio).toInt()
            params.width = viewWidth
        }
        simpleGLSurfaceView.getView().layoutParams = params

        videoEditorView.setSize(params.height.toFloat(), params.width.toFloat())
        videoEditorView.simpleGlView = simpleGLSurfaceView
    }

    override fun onPause() {
        super.onPause()
        mPlayer.setPlayWhenReady(false)
        if (isRecording) {
            isRecording = false
            simpleGLSurfaceView.stopRecording()
        }
    }

    override fun onResume() {
        super.onResume()
        mPlayer.setPlayWhenReady(true)
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

    class FilterAdapter(context: Context, list: List<String>) : androidx.recyclerview.widget.RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {

        private var mList = list
        private var mContext = context

        override fun onCreateViewHolder(parent: ViewGroup, pos: Int): FilterViewHolder {
            val view = LayoutInflater.from(mContext).inflate(R.layout.item_filter, parent, false)
            return FilterViewHolder(view)
        }

        override fun getItemCount(): Int {
            return mList.size
        }

        override fun onBindViewHolder(filterViewHolder: FilterViewHolder, pos: Int) {
            filterViewHolder.textView.text = mList[pos]
            filterViewHolder.textView.setOnClickListener {
                (mContext as SimpleGLActivity).simpleGLSurfaceView.changeFilter(pos)
            }
        }

        class FilterViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

            var textView: Button = itemView.findViewById(R.id.text)

        }
    }

}