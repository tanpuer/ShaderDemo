package com.example.templechen.videoshaderdemo.gl.render

import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.RectF
import android.os.Handler
import android.os.Message
import android.view.Surface
import java.lang.IllegalArgumentException
import java.lang.ref.WeakReference

class RenderHandler(renderThread: RenderThread) : Handler() {

    companion object {
        const val MSG_SURFACE_CREATED = 0
        const val MSG_SURFACE_CHANGED = 1
        const val MSG_DO_FRAME = 2
        const val MSG_SHUTDOWN = 3
        const val MSG_START_RECORD = 4
        const val MSG_STOP_RECORD = 5
        const val MSG_CHANGE_FILTER = 6
        const val MSG_RENDER_ANOTHER_SURFACE = 7
        const val MSG_STOP_RENDER_ANOTHER_SURFACE = 8
        const val MSG_VIDEO_EDITOR_RECT = 9
        const val MSG_CUSTOM_WATER_MARK_BITMAP = 10
        const val MSG_CUSTOM_WATER_MARK_RECT = 11

        //gesture
        const val MSG_GESTURE_VIDEO_SIZE_CHANGED = 100
        const val MSG_GESTURE_ON_SCALE = 101
        const val MSG_GESTURE_ON_SCROLL = 102
        const val MSG_GESTURE_ON_ROTATE = 103
        const val MSG_CHANGE_BACKGROUND_COLOR = 104

        //cover image
        const val MSG_RENDER_COVER_IMAGE = 201
        const val MSG_RENDER_FIRST_FRAME = 202

        //overlay
        const val MSG_OVERLAY_VIDEO_SIZE_CHANGED = 301

        //play
        const val MSG_START_PLAY = 401
        const val MSG_START_OVERLAY_PLAY = 402
    }

    private var weakRenderThread: WeakReference<RenderThread> = WeakReference(renderThread)

    /**
     * Sends the "surface created" message.
     * <p>
     * Call from UI thread.
     */
    fun sendSurfaceCreated(type: Int) {
        sendMessage(obtainMessage(MSG_SURFACE_CREATED, type, 0))
    }

    /**
     * Sends the "surface changed" message, forwarding what we got from the SurfaceHolder.
     * <p>
     * Call from UI thread.
     */
    fun sendSurfaceChanged(format: Int, width: Int, height: Int) {
        sendMessage(obtainMessage(MSG_SURFACE_CHANGED, width, height))
    }

    /**
     * Sends the "do frame" message, forwarding the Choreographer event.
     * <p>
     * Call from UI thread.
     */
    fun sendDoFrame(frameTimeNanos: Long) {
        sendMessage(obtainMessage(MSG_DO_FRAME, frameTimeNanos.shr(32).toInt(), frameTimeNanos.toInt()))
    }

    /**
     * Sends the "shutdown" message, which tells the render thread to halt.
     * <p>
     * Call from UI thread.
     */
    fun sendShutDown() {
        sendMessage(obtainMessage(MSG_SHUTDOWN))
    }

    fun sendStartEncoder() {
        sendMessage(obtainMessage(MSG_START_RECORD))
    }

    fun sendStopEncoder() {
        sendMessage(obtainMessage(MSG_STOP_RECORD))
    }

    fun changeFilter(type: Int) {
        sendMessage(obtainMessage(MSG_CHANGE_FILTER, type, 0))
    }

    fun renderAnotherSurface(surface: Surface?) {
        sendMessage(obtainMessage(MSG_RENDER_ANOTHER_SURFACE, surface))
    }

    fun stopRenderAnotherSurface() {
        sendMessage(obtainMessage(MSG_STOP_RENDER_ANOTHER_SURFACE))
    }

    fun setVideoEditorRect(rect: Rect) {
        sendMessage(obtainMessage(MSG_VIDEO_EDITOR_RECT, rect))
    }

    fun setCustomWaterMark(bitmap: Bitmap?) {
        sendMessage(obtainMessage(MSG_CUSTOM_WATER_MARK_BITMAP, bitmap))
    }

    fun setCustomWaterRect(rectF: RectF) {
        sendMessage(obtainMessage(MSG_CUSTOM_WATER_MARK_RECT, rectF))
    }

    //gesture
    fun setVideoSize(width: Int, height: Int) {
        sendMessage(obtainMessage(MSG_GESTURE_VIDEO_SIZE_CHANGED, width, height))
    }

    fun setOnScale(totalScaleX: Float, totalScaleY: Float) {
        sendMessage(obtainMessage(MSG_GESTURE_ON_SCALE, floatArrayOf(totalScaleX, totalScaleY)))
    }

    fun setOnScroll(totalScrollX: Float, totalScrollY: Float) {
        sendMessage(obtainMessage(MSG_GESTURE_ON_SCROLL, floatArrayOf(totalScrollX, totalScrollY)))
    }

    fun setOnRotate(degrees: Int) {
        sendMessage(obtainMessage(MSG_GESTURE_ON_ROTATE, degrees, 0))
    }

    fun setBackgroundColorChanged(colorArray: FloatArray) {
        sendMessage(obtainMessage(MSG_CHANGE_BACKGROUND_COLOR, colorArray))
    }

    //render cover Image
    fun setCoverImage(url: Bitmap?) {
        sendMessage(obtainMessage(MSG_RENDER_COVER_IMAGE, url))
    }

    fun setRenderFirstFrame() {
        sendMessage(obtainMessage(MSG_RENDER_FIRST_FRAME))
    }

    //overlay
    fun setOverlayVideoSize(width: Int, height: Int) {
        sendMessage(obtainMessage(MSG_OVERLAY_VIDEO_SIZE_CHANGED, width, height))
    }

    //play

    fun startPlay() {
        sendMessage(obtainMessage(MSG_START_PLAY))
    }

    fun startOverlayPlay() {
        sendMessage(obtainMessage(MSG_START_OVERLAY_PLAY))
    }

    override fun handleMessage(msg: Message?) {
        val what = msg?.what
        val renderThread = weakRenderThread.get() ?: return
        when (what) {
            MSG_SURFACE_CREATED -> {
                renderThread.surfaceCreated(msg.arg1)
            }
            MSG_SURFACE_CHANGED -> {
                renderThread.surfaceChanged(msg.arg1, msg.arg2)
            }
            MSG_DO_FRAME -> {
                val timestamp: Long = msg.arg1.toLong().shl(32).or(msg.arg2.toLong().and(0xffffffffL))
                renderThread.doFrame(timestamp)
            }
            MSG_SHUTDOWN -> {
                renderThread.shutDown()
            }
            MSG_START_RECORD -> {
                renderThread.startEncoder()
            }
            MSG_STOP_RECORD -> {
                renderThread.stopEncoder()
            }
            MSG_CHANGE_FILTER -> {
                renderThread.resetFilter(msg.arg1)
            }
            MSG_RENDER_ANOTHER_SURFACE -> {
                val surface = msg.obj
                if (surface != null) {
                    renderThread.renderAnotherSurface(surface as Surface)
                }
            }
            MSG_STOP_RENDER_ANOTHER_SURFACE -> {
                renderThread.stopRenderAnotherSurface()
            }
            MSG_VIDEO_EDITOR_RECT -> {
                renderThread.setVideoEditorRect(msg.obj as Rect)
            }
            MSG_CUSTOM_WATER_MARK_BITMAP -> {
                renderThread.setCustomWaterMark(msg.obj as Bitmap)
            }
            MSG_CUSTOM_WATER_MARK_RECT -> {
                renderThread.setCustomWaterRect(msg.obj as RectF)
            }
            MSG_GESTURE_VIDEO_SIZE_CHANGED -> {
                renderThread.setVideoSize(msg.arg1, msg.arg2)
            }
            MSG_GESTURE_ON_SCALE -> {
                val floatArray: FloatArray = msg.obj as FloatArray
                if (floatArray.size != 2) {
                    return
                }
                renderThread.setScale(floatArray[0], floatArray[1])
            }
            MSG_GESTURE_ON_SCROLL -> {
                val floatArray: FloatArray = msg.obj as FloatArray
                if (floatArray.size != 2) {
                    return
                }
                renderThread.setScroll(floatArray[0], floatArray[1])
            }
            MSG_GESTURE_ON_ROTATE -> {
                renderThread.setDegrees(msg.arg1)
            }
            MSG_CHANGE_BACKGROUND_COLOR -> {
                val floatArray: FloatArray = msg.obj as FloatArray
                if (floatArray.size != 4) {
                    return
                }
                renderThread.setBackgroundColor(floatArray[0], floatArray[1], floatArray[2], floatArray[3])
            }
            MSG_RENDER_COVER_IMAGE -> {
                renderThread.renderCoverImage(msg.obj as Bitmap?)
            }
            MSG_RENDER_FIRST_FRAME -> {
                renderThread.renderFirstFrame()
            }
            MSG_OVERLAY_VIDEO_SIZE_CHANGED -> {
                renderThread.setOverlayVideoSize(msg.arg1, msg.arg2)
            }
            MSG_START_PLAY -> {
                renderThread.startPlay()
            }
            MSG_START_OVERLAY_PLAY -> {
                renderThread.startOverlayPlay()
            }
            else -> throw IllegalArgumentException()
        }
    }

}