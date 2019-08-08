package com.example.templechen.videoshaderdemo.test

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.*
import com.example.templechen.videoshaderdemo.R
import kotlinx.android.synthetic.main.activity_recycler_touch.*
import kotlinx.android.synthetic.main.item_recycler_touch_test.view.*

class RecyclerViewTouchTestActivity : AppCompatActivity() {

    private var list = mutableListOf<String>()

    init {
        for (i in 0..10) {
            list.add("$i")
        }
    }

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: MyAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_touch)
        layoutManager = GridLayoutManager(this, 2)
        recycler_view.layoutManager = layoutManager
        recycler_view.setHasFixedSize(true)
        adapter = MyAdapter()
        recycler_view.adapter = adapter
        val itemTouchCallback = ItemTouchCallback()
        itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recycler_view)
        recycler_view.addItemDecoration(MyDecoration(this))
    }

    inner class MyAdapter : RecyclerView.Adapter<MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(
                LayoutInflater.from(this@RecyclerViewTouchTestActivity).inflate(
                    R.layout.item_recycler_touch_test,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.textView.text = list[position]
            holder.image.setOnTouchListener { v, event ->
                itemTouchHelper.startDrag(holder)
                false
            }
            if (position == list.size - 1) {
                for (i in 0..10) {
                    list.add("$i")
                }
                recycler_view.post {
                    notifyDataSetChanged()
                }
            }
        }

    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.text
        val image: ImageView = itemView.image
    }

    inner class ItemTouchCallback : ItemTouchHelper.Callback() {

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            if (layoutManager is GridLayoutManager) {
                return makeMovementFlags(
                    ItemTouchHelper.UP.or(ItemTouchHelper.DOWN).or(ItemTouchHelper.LEFT).or(
                        ItemTouchHelper.RIGHT
                    ), 0
                )
            } else {
                return makeMovementFlags(
                    ItemTouchHelper.UP.or(ItemTouchHelper.DOWN),
                    ItemTouchHelper.LEFT
                )
            }
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPos = viewHolder.adapterPosition
            val toPos = target.adapterPosition
            adapter.notifyItemMoved(fromPos, toPos)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val deletePos = viewHolder.adapterPosition
            adapter.notifyItemRangeRemoved(deletePos, 1)
        }
    }

    inner class MyDecoration(context: Context) : RecyclerView.ItemDecoration() {

        init {

        }

        override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            super.onDraw(c, parent, state)
        }

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
        }
    }
}