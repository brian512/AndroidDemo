package com.brian.testandroid.record;

import android.os.Build;

import org.bytedeco.javacpp.avcodec;

public class RecorderParameters {

    public final static int RESOLUTION_HIGH_VALUE = 2;
    public final static int RESOLUTION_MEDIUM_VALUE = 1;
    public final static int RESOLUTION_LOW_VALUE = 0;

    private static boolean AAC_SUPPORTED = Build.VERSION.SDK_INT >= 10;
    //private int videoCodec = avcodec.AV_CODEC_ID_H264;
    private int videoCodec = avcodec.AV_CODEC_ID_MPEG4;
    public static int videoFrameRate = 24;
    //private int videoBitrate = 500 *1000;
    private int videoQuality = 12;
    private int audioCodec = AAC_SUPPORTED ? avcodec.AV_CODEC_ID_AAC : avcodec.AV_CODEC_ID_AMR_NB;
    private int audioChannel = 1;
    private int audioBitrate = 96000;//192000;//AAC_SUPPORTED ? 96000 : 12200;
    private int videoBitrate = 1000000;
    private int audioSamplingRate = AAC_SUPPORTED ? 44100 : 8000;
    private String videoOutputFormat = AAC_SUPPORTED ? "mp4" : "3gp";
    private int vidioWidth = 480;
    private int vidioHeight = 480;

    public static boolean isAAC_SUPPORTED() {
        return AAC_SUPPORTED;
    }

    public static void setAAC_SUPPORTED(boolean aAC_SUPPORTED) {
        AAC_SUPPORTED = aAC_SUPPORTED;
    }

    public String getVideoOutputFormat() {
        return videoOutputFormat;
    }

    public void setVideoOutputFormat(String videoOutputFormat) {
        this.videoOutputFormat = videoOutputFormat;
    }

    public int getAudioSamplingRate() {
        return audioSamplingRate;
    }

    public void setAudioSamplingRate(int audioSamplingRate) {
        this.audioSamplingRate = audioSamplingRate;
    }

    public int getVideoCodec() {
        return videoCodec;
    }

    public void setVideoCodec(int videoCodec) {
        this.videoCodec = videoCodec;
    }

    public int getVideoFrameRate() {
        return videoFrameRate;
    }

    /**
     * 每一帧的视频间隔，ns
     */
    public long getVideoFrameTime() {
        return 1000000L / videoFrameRate;
    }

    public void setVideoFrameRate(int videoFrameRate) {
        this.videoFrameRate = videoFrameRate;
    }

    public int getVideoQuality() {
        return videoQuality;
    }

    public void setVideoQuality(int videoQuality) {
        this.videoQuality = videoQuality;
    }

    public int getAudioCodec() {
        return audioCodec;
    }

    public void setAudioCodec(int audioCodec) {
        this.audioCodec = audioCodec;
    }

    public int getAudioChannel() {
        return audioChannel;
    }

    public void setAudioChannel(int audioChannel) {
        this.audioChannel = audioChannel;
    }

    public int getAudioBitrate() {
        return audioBitrate;
    }

    public void setAudioBitrate(int audioBitrate) {
        this.audioBitrate = audioBitrate;
    }

    public int getVideoBitrate() {
        return videoBitrate;
    }

    public void setVideoBitrate(int videoBitrate) {
        this.videoBitrate = videoBitrate;
    }

    public int getVidioWidth() {
        return vidioWidth;
    }

    public void setVidioWidth(int vidioWidth) {
        this.vidioWidth = vidioWidth;
    }

    public int getVidioHeight() {
        return vidioHeight;
    }

    public void setVidioHeight(int vidioHeight) {
        this.vidioHeight = vidioHeight;
    }


    private static RecorderParameters getParameter(int currentResolution) {
        RecorderParameters parameters = new RecorderParameters();
        if (currentResolution == RESOLUTION_HIGH_VALUE) {
            parameters.setAudioBitrate(128000);
            parameters.setVideoQuality(0);
        } else if (currentResolution == RESOLUTION_MEDIUM_VALUE) {
            parameters.setAudioBitrate(128000);
            parameters.setVideoQuality(5);
        } else if (currentResolution == RESOLUTION_LOW_VALUE) {
            parameters.setAudioBitrate(96000);
            parameters.setVideoQuality(20);
        }
        return parameters;
    }

    public static RecorderParameters getDefaultParameter() {
        return getParameter(RESOLUTION_MEDIUM_VALUE);
    }

}
