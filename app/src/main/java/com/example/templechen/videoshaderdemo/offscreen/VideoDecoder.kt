package com.example.templechen.videoshaderdemo.offscreen

import android.media.MediaCodec
import android.media.MediaCodecList
import android.media.MediaExtractor
import android.media.MediaFormat
import android.util.Log
import android.view.Surface
import java.io.File
import java.io.FileNotFoundException
import java.lang.RuntimeException


class VideoDecoder(file: File) {

    companion object {
        private const val TAG = "VideoDecoder"
    }

    private var mFile = file
    var mOutputSurface: Surface? = null
    private var mMediaExtractor = MediaExtractor()
    private var mVideoTrack = -1
    private var mMediaFormat: MediaFormat
    var mVideoWidth = -1
    var mVideoHeight = -1
    private lateinit var mMediaCodec: MediaCodec
    var mFrameCallback: FrameCallback? = null

    init {
        mMediaExtractor.setDataSource(file.toString())
        val numTracks = mMediaExtractor.trackCount
        for (i in 0..numTracks) {
            val mediaFormat = mMediaExtractor.getTrackFormat(i)
            val mime = mediaFormat.getString(MediaFormat.KEY_MIME)
            if (mime.startsWith("video/")) {
                mVideoTrack = i
                break
            }
        }
        if (mVideoTrack == -1) {
            throw RuntimeException("file contains no video track, please check")
        }
        mMediaExtractor.selectTrack(mVideoTrack)
        mMediaFormat = mMediaExtractor.getTrackFormat(mVideoTrack)
        mVideoWidth = mMediaFormat.getInteger(MediaFormat.KEY_WIDTH)
        mVideoHeight = mMediaFormat.getInteger(MediaFormat.KEY_HEIGHT)
    }


    fun decode() {
        if (!mFile.canRead()) {
            throw FileNotFoundException("video file not exist")
        }
        val codecName = MediaCodecList(MediaCodecList.ALL_CODECS).findDecoderForFormat(mMediaFormat)
        if (codecName == null) {
            throw RuntimeException("video can not be decoded by GPU")
        }
        mMediaCodec = MediaCodec.createDecoderByType(mMediaFormat.getString(MediaFormat.KEY_MIME))
        mMediaCodec.configure(mMediaFormat, mOutputSurface, null, 0)
        mMediaCodec.start()

        //begin
        val TIMEOUT_USEC = 0L
        var mBufferInfo = MediaCodec.BufferInfo()
        var outputDone = false
        var inputDone = false
        mFrameCallback?.decodeFrameBegin()
        while (!outputDone) {
            //feed more data to the decoder
            if (!inputDone) {
                val inputBufferIndex = mMediaCodec.dequeueInputBuffer(TIMEOUT_USEC)
                if (inputBufferIndex > 0) {
                    val inputBuffer = mMediaCodec.getInputBuffer(inputBufferIndex)
                    // Read the sample data into the ByteBuffer.  This neither respects nor
                    // updates inputBuf's position, limit, etc.
                    val chunkSize = mMediaExtractor.readSampleData(inputBuffer, 0)
                    if (chunkSize < 0) {
                        //End of Stream
                        mMediaCodec.queueInputBuffer(inputBufferIndex, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                        inputDone = true
                        Log.d(TAG, "doExtract: " + "inputDone")
                    } else {
                        if (mMediaExtractor.sampleTrackIndex != mVideoTrack) {
                            Log.d(TAG, "doExtract: " + "get wrong trackIndex!!!")
                        }
                        val presentationTimeUs = mMediaExtractor.sampleTime
                        mMediaCodec.queueInputBuffer(inputBufferIndex, 0, chunkSize, presentationTimeUs, 0)
                        mMediaExtractor.advance()
                    }
                } else {
                    Log.d(TAG, "doExtract: " + " buffer index not available")
                }
            }

            if (!outputDone) {
                val decoderStatus = mMediaCodec.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC)
                if (decoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    // no output available yet
                    Log.d(TAG, "no output from decoder available")
                } else if (decoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    val newFormat = mMediaCodec.outputFormat
                    Log.d(TAG, "decoder output format changed: $newFormat")
                } else if (decoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    Log.d(TAG, "doExtract: output buffers changed")
                } else if (decoderStatus < 0) {
                    throw RuntimeException(
                        "unexpected result from decoder.dequeueOutputBuffer: $decoderStatus"
                    )
                } else { // decoderStatus >= 0
                    if (mBufferInfo.flags.and(MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        Log.d(TAG, "output EOS")
                        outputDone = true
                    }

                    val doRender = mBufferInfo.size != 0
                    mMediaCodec.releaseOutputBuffer(decoderStatus, doRender)
                    if (doRender && mBufferInfo.presentationTimeUs > 0) {
                        mFrameCallback?.decodeOneFrame(mBufferInfo.presentationTimeUs)
                    }
                }
            }
        }
        mMediaCodec.stop()
        mMediaCodec.release()
        mMediaExtractor.release()
        mFrameCallback?.decodeFrameEnd()
    }
}