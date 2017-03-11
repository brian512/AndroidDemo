package com.brian.testandroid.record;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameRecorder;

import java.nio.ShortBuffer;

/**
 * 录制音频的线程
 */
public class AudioRecordRunnable implements Runnable {

    private final int[] mAudioRecordLock = new int[0];

    private short[] mAudioData;
    private AudioRecord mAudioRecord;

    private boolean runAudioThread = true;
    private boolean recording = false;

    private int mCount = 0;
    //音频时间戳
    public volatile long mAudioTimestamp = 0L;
    public volatile long mAudioTimeRecorded;

    private FFmpegFrameRecorder mRecorder;

    public AudioRecordRunnable(FFmpegFrameRecorder recorder, int sampleRate) {
        mRecorder = recorder;

        int mBufferSize = AudioRecord.getMinBufferSize(sampleRate,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, mBufferSize);
        mAudioData = new short[mBufferSize];
    }

    public void setRecordState(boolean isOn) {
        recording = isOn;
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
        runAudioThread = true;
    }

    public void stop() {
        runAudioThread = false;
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        //判断音频录制是否被初始化
        while (mAudioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException localInterruptedException) {
            }
        }
        mAudioRecord.startRecording();
        while (runAudioThread) {
            int bufferReadResult = mAudioRecord.read(mAudioData, 0, mAudioData.length);
            if ((bufferReadResult > 0) && recording) {
                int i = getTimeStampInNsFromSampleCounted(this.mCount);
                if (mAudioTimestamp != i) {
                    mAudioTimestamp = i;
                    mAudioTimeRecorded = System.nanoTime();
                }

                ShortBuffer shortBuffer = ShortBuffer.wrap(mAudioData, 0, bufferReadResult);
                try {
                    synchronized (mAudioRecordLock) {
                        mCount += shortBuffer.limit();
                        mRecorder.recordSamples(shortBuffer);
                    }
                } catch (FrameRecorder.Exception e) {
                    e.printStackTrace();
                }
            }
        }
        mAudioRecord.stop();
        mAudioRecord.release();
    }

    public static int getTimeStampInNsFromSampleCounted(int paramInt) {
        return (int) (paramInt / 0.0441D);
    }
}