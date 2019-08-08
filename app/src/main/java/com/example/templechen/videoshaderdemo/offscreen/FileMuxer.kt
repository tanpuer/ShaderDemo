package com.example.templechen.videoshaderdemo.offscreen

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.util.Log
import java.nio.ByteBuffer

object FileMuxer {

    const val TAG = "FileMuxer"

    fun muxeVideoAndAudio(audioPath: String, videoPath: String, destPath: String) {
        val mediaMuxer = MediaMuxer(destPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        // if byteBuffer is 100 * 1024, long video may crash in readSampleData, do not know why
        val byteBuffer = ByteBuffer.allocate(1024 * 1024)

        //extract audio
        var audioTrackIndex = -1
        var audioFormat: MediaFormat
        val audioExtractor = MediaExtractor()
        audioExtractor.setDataSource(audioPath)
        for (i in 0..audioExtractor.trackCount) {
            val format = audioExtractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime.startsWith("audio")) {
                audioTrackIndex = i
                break
            }
        }
        if (audioTrackIndex == -1) {
            Log.d(TAG, "no audio track : $audioPath")
            return
        }
        audioFormat = audioExtractor.getTrackFormat(audioTrackIndex)
        audioExtractor.selectTrack(audioTrackIndex)
        audioTrackIndex = mediaMuxer.addTrack(audioFormat)

        //extract video
        var videoTrackIndex = -1
        var videoFormat: MediaFormat
        val videoExtractor = MediaExtractor()
        videoExtractor.setDataSource(videoPath)
        for (i in 0..videoExtractor.trackCount) {
            val format = videoExtractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime.startsWith("video/")) {
                videoTrackIndex = i
                break
            }
        }
        if (videoTrackIndex == -1) {
            Log.d(TAG, "no video track: $videoPath")
            return
        }
        videoExtractor.selectTrack(videoTrackIndex)
        videoFormat = videoExtractor.getTrackFormat(videoTrackIndex)
        videoTrackIndex = mediaMuxer.addTrack(videoFormat)

        mediaMuxer.start()

        //write video
        byteBuffer.clear()
        val videoBufferInfo = MediaCodec.BufferInfo()
        videoBufferInfo.presentationTimeUs = 0
        var videoSampleSize = videoExtractor.readSampleData(byteBuffer, 0)
        while (videoSampleSize > 0) {
            videoBufferInfo.size = videoSampleSize
            videoBufferInfo.flags = videoExtractor.sampleFlags
            videoBufferInfo.offset = 0
            videoBufferInfo.presentationTimeUs = videoExtractor.sampleTime
            mediaMuxer.writeSampleData(videoTrackIndex, byteBuffer, videoBufferInfo)
            videoExtractor.advance()
            videoSampleSize = videoExtractor.readSampleData(byteBuffer, 0)
        }

//        write audio
        byteBuffer.clear()
        val audioBufferInfo = MediaCodec.BufferInfo()
        audioBufferInfo.presentationTimeUs = 0
        var audioSampleSize = audioExtractor.readSampleData(byteBuffer, 0)
        while (audioSampleSize > 0) {
            audioBufferInfo.size = audioSampleSize
            audioBufferInfo.flags = audioExtractor.sampleFlags
            audioBufferInfo.offset = 0
            audioBufferInfo.presentationTimeUs = audioExtractor.sampleTime
            mediaMuxer.writeSampleData(audioTrackIndex, byteBuffer, audioBufferInfo)
            audioExtractor.advance()
            audioSampleSize = audioExtractor.readSampleData(byteBuffer, 0)
        }

        audioExtractor.release()
        videoExtractor.release()
        mediaMuxer.stop()
        mediaMuxer.release()
    }
}