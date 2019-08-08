package com.example.templechen.videoshaderdemo.image.creator

import android.content.Context
import java.io.File

class BitmapCreatorFactory {

    companion object {

        const val TYPE_IMAGE = 1
        const val TYPE_GIF = 2

        fun newImageCreator(res: Int, type: Int, context: Context): IBitmapCreator {
            when (type) {
                TYPE_IMAGE -> {
                    return ImageCreator(context, res)
                }
                TYPE_GIF -> {
                    return GifCreator(context, res)
                }
            }
            return ImageCreator(context, res)
        }

        fun newImageCreator(file: File, type: Int, context: Context): IBitmapCreator {
            when (type) {
                TYPE_IMAGE -> {
                    return ImageCreator(context, file)
                }
                TYPE_GIF -> {
                    return GifCreator(context, file)
                }
            }
            return ImageCreator(context, file)
        }

        fun newImageCreator(assetsName: String, type: Int, context: Context): IBitmapCreator {
            when (type) {
                TYPE_IMAGE -> {
                    return ImageCreator(context, assetsName)
                }
                TYPE_GIF -> {
                    return GifCreator(context, assetsName)
                }
            }
            return ImageCreator(context, assetsName)
        }

    }
}