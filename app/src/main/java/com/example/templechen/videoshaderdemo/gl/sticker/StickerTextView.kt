package com.example.templechen.videoshaderdemo.gl.sticker

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import com.example.templechen.videoshaderdemo.R

class StickerTextView : FrameLayout, View.OnTouchListener {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var parentView: FrameLayout =
        LayoutInflater.from(context).inflate(R.layout.sticker_text_view, this, false) as FrameLayout
    private var topLeftView: LineView = parentView.findViewById(R.id.top_left_view)
    private var topRightView: LineView = parentView.findViewById(R.id.top_right_view)
    private var bottomLeftView: LineView = parentView.findViewById(R.id.bottom_left_view)
    private var bottomRightView: LineView = parentView.findViewById(R.id.bottom_right_view)
    private var textView: TextView = parentView.findViewById(R.id.text)
    private var paint: Paint = Paint()

    init {
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f
        paint.color = Color.RED
        topLeftView.lineType = LineView.LINE_TOP_LEFT_TYPE
        topRightView.lineType = LineView.LINE_TOP_RIGHT_TYPE
        bottomLeftView.lineType = LineView.LINE_BOTTOM_LEFT_TYPE
        bottomRightView.lineType = LineView.LINE_BOTTOM_RIGHT_TYPE
        addView(parentView)
        textView.setOnTouchListener(this)
    }

    private var mGestureDetector: GestureDetector = GestureDetector(context, SingleTapConfirm(this))

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return mGestureDetector.onTouchEvent(event)
    }

    private class SingleTapConfirm(stickerView: StickerTextView) : GestureDetector.SimpleOnGestureListener() {

        private var mStickerView = stickerView
        private var deltaX = 0f
        private var deltaY = 0f

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            if (e1 != null && e2 != null) {
                deltaX = e2.x - e1.x
                deltaY = e2.y - e1.y
                mStickerView.layout(
                    (mStickerView.left + deltaX).toInt(),
                    (mStickerView.top + deltaY).toInt(),
                    (mStickerView.right + deltaX).toInt(),
                    (mStickerView.bottom + deltaY).toInt()
                )
            }
            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            return true
        }

    }

}