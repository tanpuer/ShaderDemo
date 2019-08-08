package com.example.templechen.videoshaderdemo.widget.coordinator

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.templechen.videoshaderdemo.R
import com.example.templechen.videoshaderdemo.util.Utils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_coordinator.*

class CoordinatorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coordinator)

//        coordinator_layout.headerHeight = Utils.dpToPx(this, 200f)
        image.setOnClickListener {
            Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show()
        }

        val list = mutableListOf<String>()
        for (i in 0..100) {
            list.add("$i")
        }
//        recycler_view.adapter = FilterAdapter(this, list)
//        recycler_view.layoutManager = LinearLayoutManager(this)
//        CoverHeaderScrollBehavior.from(recycler_view).peekHeight = resources.displayMetrics.heightPixels - Utils.dpToPx(this, 250f).toInt()

        recycler_view.adapter = ArrayAdapter<String>(this, R.layout.item_list_text, list)
        CoverHeaderScrollBehavior.from(recycler_view).peekHeight = resources.displayMetrics.heightPixels - Utils.dpToPx(this, 250f).toInt()
    }

    class FilterAdapter(context: Context, list: List<String>) : RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {

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
        }

        class FilterViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

            var textView: Button = itemView.findViewById(R.id.text)

        }
    }

}