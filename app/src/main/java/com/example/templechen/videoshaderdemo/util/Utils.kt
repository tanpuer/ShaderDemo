package com.example.templechen.videoshaderdemo.util

import android.content.Context
import android.util.TypedValue

class Utils {

    companion object {
        fun dpToPx(context: Context, dp: Float): Float {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp,
                context.resources.displayMetrics
            )
        }
    }
}