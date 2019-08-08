package com.example.templechen.videoshaderdemo.player;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.Surface;
import com.google.android.exoplayer2.*;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ClippingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.upstream.*;
import com.google.android.exoplayer2.upstream.cache.*;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class ExoPlayerTool2 implements IExoPlayer{

    private static final String TAG = "ExoPlayerTool";

    public static int STATE_IDLE = 1;
    public static int STATE_BUFFERING = 2;
    public static int STATE_READY = 3;
    public static int STATE_ENDED = 4;

    private SimpleExoPlayer mExoPlayer;
    private Uri mUri;
    private Map<String, Long> mPositionMap = new HashMap<>();
    private boolean loop;

    public ExoPlayerTool2(Context context) {
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(context);
    }

    public void quickSetting(Context context, String url) {
        quickSetting(context, url, null);
    }

    public void quickSetting(Context context, String url, Surface surface) {
        mUri = Uri.parse(url);
        if (mVideoListener != null) {
            mVideoListener.onPlayerStateChanged(false, STATE_IDLE);
        }
        MediaSource mediaSource = setUrl(url, context);
        prepare(mediaSource);
        if (surface != null) {
            setVideoSurface(surface);
        }
    }

    public void clipQuickSetting(Context context, String url, long startPositionUs, long endPositionUs) {
        Uri uri = Uri.parse(url);
        MediaSource mediaSource = buildMediaSource(uri, createDataSourceFactory(uri, context), context);
        ClippingMediaSource clippingMediaSource = new ClippingMediaSource(mediaSource, startPositionUs, endPositionUs);
        prepare(new LoopingMediaSource(clippingMediaSource));
    }

    public MediaSource setUrl(String url, Context context) {
        return setUri(Uri.parse(url), context);
    }

    public MediaSource setUri(Uri uri, Context context) {
        if (uri == null) {
            return null;
        }
        MediaSource mediaSource = buildMediaSource(uri, createDataSourceFactory(uri, context), context);
        return loop ? new LoopingMediaSource(mediaSource) : mediaSource;
    }

    public DataSource.Factory createDataSourceFactory(Uri uri, Context context) {
        DataSource.Factory dataSourceFactory;
        if (uri.getScheme().equals("asset") || uri.getScheme().equals("file")) {
            dataSourceFactory = new DefaultDataSourceFactory(context, "ExoPlayer");
        } else {
            dataSourceFactory =
                    new DefaultHttpDataSourceFactory(
                            "ExoPlayer",
                            null,
                            DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                            DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                            true);
        }
        return dataSourceFactory;
    }

    public void prepare(MediaSource mediaSource) {
        mExoPlayer.prepare(mediaSource);
    }

    public void setVideoSurface(Surface surface) {
        mExoPlayer.setVideoSurface(surface);
    }

    public void setPlayWhenReady(boolean playWhenReady) {
        setPlayWhenReady(playWhenReady, false);
    }

    public void setPlayWhenReady(boolean playWhenReady, boolean seekToLastPos) {
        mExoPlayer.setPlayWhenReady(playWhenReady);
    }

    public void setVolume(float audioVolume) {
        mExoPlayer.setVolume(audioVolume);
    }

    public void reset() {
        mExoPlayer.stop(true);
    }

    public void release() {
        mExoPlayer.release();
    }

    public void clearVideoSurface() {
        mExoPlayer.clearVideoSurface();
    }

    public int getPlayerState() {
        return mExoPlayer.getPlaybackState();
    }

    public boolean isPlaying() {
        return mExoPlayer.getPlayWhenReady() && getPlayerState() == STATE_READY;
    }

    private MediaSource buildMediaSource(Uri uri, DataSource.Factory mediaDataSourceFactory, Context context) {
        int type = Util.inferContentType(uri.getLastPathSegment());
        switch (type) {
            case C.TYPE_SS:
                return new SsMediaSource.Factory(
                        new DefaultSsChunkSource.Factory(mediaDataSourceFactory),
                        new DefaultDataSourceFactory(context, null, mediaDataSourceFactory))
                        .createMediaSource(uri);
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(
                        new DefaultDashChunkSource.Factory(mediaDataSourceFactory),
                        new DefaultDataSourceFactory(context, null, mediaDataSourceFactory))
                        .createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource.Factory(mediaDataSourceFactory)
                        .setExtractorsFactory(new DefaultExtractorsFactory())
                        .createMediaSource(uri);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private IVideoListener mVideoListener;

    public interface IVideoListener {
        default void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        }

        default void onSurfaceSizeChanged(int width, int height) {
        }

        default void onRenderedFirstFrame() {
        }

        default void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        }

        default void onPlayerError(ExoPlaybackException error) {
        }
    }

    public void addVideoListener(IVideoListener iVideoListener) {
        mVideoListener = iVideoListener;
        mExoPlayer.addVideoListener(new VideoListener() {
            @Override
            public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                if (mVideoListener != null) {
                    mVideoListener.onVideoSizeChanged(width, height, unappliedRotationDegrees, pixelWidthHeightRatio);
                }
            }

            @Override
            public void onSurfaceSizeChanged(int width, int height) {
                if (mVideoListener != null) {
                    mVideoListener.onSurfaceSizeChanged(width, height);
                }
            }

            @Override
            public void onRenderedFirstFrame() {
                if (mVideoListener != null) {
                    mVideoListener.onRenderedFirstFrame();
                }
            }
        });

        mExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (mVideoListener != null) {
                    mVideoListener.onPlayerStateChanged(playWhenReady, playbackState);
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                if (mVideoListener != null) {
                    mVideoListener.onPlayerError(error);
                }
            }
        });

    }

    public void removeVideoListener() {
        mVideoListener = null;
    }

    public Uri getUri() {
        return mUri;
    }

    public long getCurrentPosition() {
        return mExoPlayer.getCurrentPosition();
    }

    public long getDuration() {
        return mExoPlayer.getDuration();
    }

    public void seekTo(long positionMs) {
        mExoPlayer.seekTo(positionMs);
    }

    public long getBufferedPosition() {
        return mExoPlayer.getBufferedPosition();
    }

    public long getTotalBufferedDuration() {
        return mExoPlayer.getTotalBufferedDuration();
    }

    public int getBufferedPercentage() {
        return mExoPlayer.getBufferedPercentage();
    }

    public void setPlaySpeed(float speed) {
        mExoPlayer.setPlaybackParameters(new PlaybackParameters(speed, 1, false));
    }

    public boolean getPlayWhenReady() {
        return mExoPlayer.getPlayWhenReady();
    }

    public int getVideoFrameRate() {
        return (int) mExoPlayer.getVideoFormat().frameRate;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }
}
