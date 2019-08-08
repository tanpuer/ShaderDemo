package com.example.templechen.videoshaderdemo.snaphelper

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.templechen.videoshaderdemo.R
import com.example.templechen.videoshaderdemo.fake.SimpleGLFakeView
import com.example.templechen.videoshaderdemo.filter.FilterListUtil
import com.example.templechen.videoshaderdemo.player.ExoPlayerTool
import kotlinx.android.synthetic.main.activity_snap_helper.*
import kotlinx.android.synthetic.main.activity_snap_helper_item.view.*
import kotlinx.android.synthetic.main.fragment_video.*
import java.lang.ref.WeakReference

class SnapHelperActivity : AppCompatActivity(), ExoPlayerTool.IVideoListener {

    private lateinit var snapHelper: PagerSnapHelper
    private lateinit var layoutManager: LinearLayoutManager
    private var currPos = -1
    private var lastImageView: WeakReference<ImageView>? = null
    private lateinit var player: ExoPlayerTool
    private lateinit var fakeView: SimpleGLFakeView
    private lateinit var videoFragment: VideoFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snap_helper)
        val adapter = Adapter(this)
        recycler_view.adapter = adapter
        layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = RecyclerView.HORIZONTAL
        recycler_view.layoutManager = layoutManager
        snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recycler_view)
        recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                snapScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                snapScrolled(recyclerView, dx, dy)
            }
        })
        player = ExoPlayerTool.getInstance(applicationContext)
        player.setLoop(true)
        player.addVideoListener(this)
        fakeView = SimpleGLFakeView(this, player)

        videoFragment = VideoFragment(fakeView)
        supportFragmentManager.beginTransaction().replace(R.id.container, videoFragment)
            .commit()
    }

    private var lastState = -2
    fun snapScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        Log.d(TAG, "$newState")
        if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
            Log.d(TAG, "snapScrollStateChanged setting")
            val view = snapHelper.findSnapView(layoutManager)
            if (view != null) {
                val newPos = layoutManager.getPosition(view)
                if (newPos != currPos) {
                    player.playWhenReady = false
                    lastImageView?.get()?.visibility = View.VISIBLE
                    lastImageView = WeakReference(view.findViewById(R.id.cover))
                }
            }
        } else if (newState == RecyclerView.SCROLL_STATE_IDLE && lastState != RecyclerView.SCROLL_STATE_DRAGGING) {
            Log.d(TAG, "snapScrollStateChanged idle")
            val view = snapHelper.findSnapView(layoutManager)
            if (view != null) {
                val newPos = layoutManager.getPosition(view)
                if (newPos != currPos) {
                    currPos = newPos
                    onPlayerViewChanged(currPos, view)
                    lastImageView?.get()?.visibility = View.VISIBLE
                    lastImageView = WeakReference(view.findViewById(R.id.cover))
                }
            }
        }
        lastState = newState
    }

    private fun onPlayerViewChanged(pos: Int, view: View) {
        fakeView.cancelDoFrame()
        player.playWhenReady = false
        Log.d(TAG, "quickSetting")
        player.quickSetting(this, data[pos].video)
        player.playWhenReady = true

        fakeView.changeFilter((Math.random() * FilterListUtil.LIST.size - 1).toInt())

        fakeView.startDoFrame()

        scrollX = 0
        videoFragment.parent?.layout(
            scrollX, 0, videoFragment.parent.width + scrollX,
            videoFragment.parent.height
        )
    }

    override fun onRenderedFirstFrame() {
        val view = snapHelper.findSnapView(layoutManager)
        view?.postDelayed(Runnable {
            view.findViewById<ImageView>(R.id.cover)?.visibility = View.INVISIBLE
        }, 32)
    }

    private var scrollX = 0
    fun snapScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        scrollX -= dx
        videoFragment.parent?.layout(
            scrollX, 0, videoFragment.parent.width + scrollX,
            videoFragment.parent.height
        )
    }

    private var paused = false
    override fun onPause() {
        paused = true
        player.playWhenReady = false
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (paused) {
            player.playWhenReady = true
            paused = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
        fakeView.sendShutDown()
    }

    inner class Adapter(val context: Context) : RecyclerView.Adapter<MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.activity_snap_helper_item,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.itemView.cover.visibility = View.VISIBLE
            Glide.with(context).load(data[position].cover).into(holder.itemView.cover)
            if (currPos == -1 && position == 0) {
                recycler_view.postDelayed(Runnable {
                    onPlayerViewChanged(0, holder.itemView)
                }, 100)
            }
        }

    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val data = listOf(
        VideoModel(
            "https://oimryzjfe.qnssl.com/content/0fcbbe738abf1bf524dc2e7818200cc8.mp4",
            "https://oimryzjfe.qnssl.com/content/f38ce694e89a462ea79eb6f16b94ead7.png"
        ),
        VideoModel(
            "https://oimryzjfe.qnssl.com/content/2c61c7c5e95b3f4dec31aa42e4315bb1.mp4",
            "https://oimryzjfe.qnssl.com/content/a92ba82c9c9c44152f3523d000cfbb9c.png"
        ),
        VideoModel(
            "https://oimryzjfe.qnssl.com/content/afc192cfae2df1366d7268bc7a181555.mp4",
            "https://oimryzjfe.qnssl.com/content/e72446a57dbf64e234bad0582ecdb44e.png"
        ),
        VideoModel(
            "https://oimryzjfe.qnssl.com/content/0fcbbe738abf1bf524dc2e7818200cc8.mp4",
            "https://oimryzjfe.qnssl.com/content/f38ce694e89a462ea79eb6f16b94ead7.png"
        ),
        VideoModel(
            "https://oimryzjfe.qnssl.com/content/2c61c7c5e95b3f4dec31aa42e4315bb1.mp4",
            "https://oimryzjfe.qnssl.com/content/a92ba82c9c9c44152f3523d000cfbb9c.png"
        ),
        VideoModel(
            "https://oimryzjfe.qnssl.com/content/afc192cfae2df1366d7268bc7a181555.mp4",
            "https://oimryzjfe.qnssl.com/content/e72446a57dbf64e234bad0582ecdb44e.png"
        ),
        VideoModel(
            "https://oimryzjfe.qnssl.com/content/0fcbbe738abf1bf524dc2e7818200cc8.mp4",
            "https://oimryzjfe.qnssl.com/content/f38ce694e89a462ea79eb6f16b94ead7.png"
        ),
        VideoModel(
            "https://oimryzjfe.qnssl.com/content/2c61c7c5e95b3f4dec31aa42e4315bb1.mp4",
            "https://oimryzjfe.qnssl.com/content/a92ba82c9c9c44152f3523d000cfbb9c.png"
        ),
        VideoModel(
            "https://oimryzjfe.qnssl.com/content/afc192cfae2df1366d7268bc7a181555.mp4",
            "https://oimryzjfe.qnssl.com/content/e72446a57dbf64e234bad0582ecdb44e.png"
        ),
        VideoModel(
            "https://oimryzjfe.qnssl.com/content/0fcbbe738abf1bf524dc2e7818200cc8.mp4",
            "https://oimryzjfe.qnssl.com/content/f38ce694e89a462ea79eb6f16b94ead7.png"
        ),
        VideoModel(
            "https://oimryzjfe.qnssl.com/content/2c61c7c5e95b3f4dec31aa42e4315bb1.mp4",
            "https://oimryzjfe.qnssl.com/content/a92ba82c9c9c44152f3523d000cfbb9c.png"
        ),
        VideoModel(
            "https://oimryzjfe.qnssl.com/content/afc192cfae2df1366d7268bc7a181555.mp4",
            "https://oimryzjfe.qnssl.com/content/e72446a57dbf64e234bad0582ecdb44e.png"
        )
    )

    companion object {
        const val TAG = "SnapHelperActivity123"
    }

}