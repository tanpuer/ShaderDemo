package com.example.templechen.videoshaderdemo.gl.sticker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

class LineView : View, View.OnTouchListener{

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f
        paint.color = Color.RED
        setBackgroundColor(Color.RED)
        setOnTouchListener(this)
    }

    var lineType: Int = NO_TYPE
        set(value) {
            field = value
            invalidate()
        }
    private var paint: Paint = Paint()


    companion object {
        const val NO_TYPE = 0
        const val LINE_TOP_LEFT_TYPE = 1
        const val LINE_TOP_RIGHT_TYPE = 2
        const val LINE_BOTTOM_LEFT_TYPE = 3
        const val LINE_BOTTOM_RIGHT_TYPE = 4
        const val LINE_CENTER_TYPE = 5
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
//        when (lineType) {
//            LINE_TOP_LEFT_TYPE -> {
//                canvas?.drawLines(
//                    floatArrayOf(
//                        left.toFloat(), bottom.toFloat(),
//                        left.toFloat(), top.toFloat(),
//                        right.toFloat(), top.toFloat()
//                    ), paint
//                )
//            }
//            LINE_TOP_RIGHT_TYPE -> {
//                canvas?.drawLines(
//                    floatArrayOf(
//                        left.toFloat(), top.toFloat(),
//                        right.toFloat(), top.toFloat(),
//                        right.toFloat(), bottom.toFloat()
//                    ), paint
//                )
//            }
//            LINE_BOTTOM_LEFT_TYPE -> {
//                canvas?.drawLines(
//                    floatArrayOf(
//                        left.toFloat(), top.toFloat(),
//                        left.toFloat(), bottom.toFloat(),
//                        right.toFloat(), bottom.toFloat()
//                    ), paint
//                )
//            }
//            LINE_BOTTOM_RIGHT_TYPE -> {
//                canvas?.drawLines(
//                    floatArrayOf(
//                        left.toFloat(), bottom.toFloat(),
//                        right.toFloat(), bottom.toFloat(),
//                        right.toFloat(), top.toFloat()
//                    ), paint
//                )
//            }
//            LINE_CENTER_TYPE -> {
//                canvas?.drawLine(
//                    left.toFloat(),
//                    (top + bottom) / 2f,
//                    right.toFloat(),
//                    (top + bottom) / 2f,
//                    paint
//                )
//                canvas?.drawLine(
//                    (left + right) / 2f,
//                    top.toFloat(),
//                    (left + right) / 2f,
//                    bottom.toFloat(),
//                    paint
//                )
//                canvas?.drawRect(
//                    left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint
//                )
//            }
//        }
    }

    private var mGestureDetector: GestureDetector = GestureDetector(context, SingleTapConfirm(this))

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return mGestureDetector.onTouchEvent(event)
    }

    private class SingleTapConfirm(lineView: LineView) : GestureDetector.SimpleOnGestureListener() {

        private var mLineView = lineView
        private var deltaX = 0f
        private var deltaY = 0f

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            if (e1 != null && e2 != null && mLineView.parent != null) {
                val parent = mLineView.parent as FrameLayout
                deltaX = e2.x - e1.x
                deltaY = e2.y - e1.y
                val params = parent.layoutParams
                params.width = (params.width + deltaX).toInt()
                params.height = (params.height + deltaY).toInt()
                parent.layoutParams = params
                parent.requestLayout()
            }
            return true
        }

    }
}