package com.example.templechen.videoshaderdemo.widget

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View

class DouyinLoadingView : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val mPaint = Paint()
    private var currentRatio = START_WIDTH_RATIO
    private var currentAlpha = (START_TRANSPARENCY * 255).toInt()
    private var animatorSet: AnimatorSet
    private var canceled = false

    init {
        mPaint.isAntiAlias = true
        mPaint.color = Color.WHITE
        mPaint.alpha = (START_TRANSPARENCY * 255).toInt()
        mPaint.style = Paint.Style.FILL_AND_STROKE

        val startToMiddleAni = ValueAnimator.ofFloat(START_WIDTH_RATIO, MIDDLE_WIDTH_RATIO)
        startToMiddleAni.addUpdateListener {
            currentRatio = it.animatedValue as Float
            currentAlpha =
                (((currentRatio - START_WIDTH_RATIO) / (MIDDLE_WIDTH_RATIO - START_WIDTH_RATIO) * (MIDDLE_TRANSPARENCY - START_TRANSPARENCY) + START_TRANSPARENCY) * 255).toInt()
            invalidate()
        }
        startToMiddleAni.duration = ANIMATION_DURATION

        val middleToEndAni = ValueAnimator.ofFloat(MIDDLE_WIDTH_RATIO, END_WIDTH_RATIO)
        middleToEndAni.addUpdateListener {
            currentRatio = it.animatedValue as Float
            currentAlpha =
                (((currentRatio - MIDDLE_WIDTH_RATIO) / (END_WIDTH_RATIO - MIDDLE_WIDTH_RATIO) * (END_TRANSPARENCY - MIDDLE_TRANSPARENCY) + MIDDLE_TRANSPARENCY) * 255).toInt()
            invalidate()
        }
        middleToEndAni.duration = ANIMATION_DURATION

        animatorSet = AnimatorSet()
        animatorSet.playSequentially(startToMiddleAni, middleToEndAni)
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                if (!canceled) {
                    animatorSet.start()
                }
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })
        animatorSet.start()
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas != null) {
            val left = (0.5f - currentRatio) * width
            val right = (0.5f + currentRatio) * width
            mPaint.alpha = currentAlpha
            canvas.drawRect(left, 0f, right, height.toFloat(), mPaint)
        }
    }

    private var visibility = false
    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)
        if (visibility != isVisible) {
            visibility = isVisible
            if (isVisible) {
                startAnimation()
            } else {
                cancelAnimation()
            }
        }
    }

    private fun startAnimation() {
        canceled = false
        animatorSet.start()
    }

    private fun cancelAnimation() {
        canceled = true
        animatorSet.cancel()
    }

    companion object {
        private const val TAG = "DouyinLoadingView"
        private const val ANIMATION_DURATION = 230L

        private const val START_WIDTH_RATIO = 0.05f / 2
        private const val MIDDLE_WIDTH_RATIO = 0.6f / 2
        private const val END_WIDTH_RATIO = 1.0f / 2

        private const val START_TRANSPARENCY = 0.1f
        private const val MIDDLE_TRANSPARENCY = 1.0f
        private const val END_TRANSPARENCY = 0f
    }

}