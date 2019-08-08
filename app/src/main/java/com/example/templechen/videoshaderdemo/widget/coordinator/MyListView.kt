package com.example.templechen.videoshaderdemo.widget.coordinator

import android.content.Context
import android.util.AttributeSet
import android.widget.ListView
import androidx.core.view.NestedScrollingChild2
import androidx.core.view.NestedScrollingChildHelper

class MyListView : ListView, NestedScrollingChild2{

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        isNestedScrollingEnabled = true
    }

    private val mScrollingChildHelper = NestedScrollingChildHelper(this)

    override fun startNestedScroll(axes: Int, type: Int): Boolean {
        return mScrollingChildHelper.startNestedScroll(axes)
    }

    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?,
        type: Int
    ): Boolean {
        return mScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)
    }

    override fun stopNestedScroll(type: Int) {
        mScrollingChildHelper.stopNestedScroll()
    }

    override fun hasNestedScrollingParent(type: Int): Boolean {
        return mScrollingChildHelper.hasNestedScrollingParent()
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?,
        type: Int
    ): Boolean {
        return mScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow)
    }

}