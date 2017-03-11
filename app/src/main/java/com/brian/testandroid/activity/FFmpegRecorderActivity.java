package com.brian.testandroid.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Video;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brian.common.Env;
import com.brian.common.util.LogUtil;
import com.brian.testandroid.R;
import com.brian.testandroid.record.AudioRecordRunnable;
import com.brian.testandroid.record.FrameData;
import com.brian.testandroid.record.RecorderParameters;
import com.brian.testandroid.record.Util;
import com.getkeepsafe.relinker.ReLinker;

import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameRecorder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

import static com.brian.testandroid.record.Util.rotateYUV420Degree270;
import static com.brian.testandroid.record.Util.rotateYUV420Degree90;
import static org.bytedeco.javacpp.avutil.AV_PIX_FMT_NV21;

public class FFmpegRecorderActivity extends Activity implements OnClickListener, OnTouchListener {

    //视频文件的存放地址
    private String strVideoPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "rec_video.mp4";
    //视频文件在系统中存放的url
    private Uri uriVideoPath = null;
    //判断是否需要录制，点击下一步时暂停录制
    private boolean rec = false;
    //判断是否需要录制，手指按下继续，抬起时暂停
    boolean recording = false;
    //判断是否开始了录制，第一次按下屏幕时设置为true
    boolean isRecordingStarted = false;
    //是否开启闪光灯
    boolean isFlashOn = false;

    //分别为闪光灯按钮、取消按钮、下一步按钮、转置摄像头按钮
    Button flashIcon, cancelBtn, nextBtn, switchCameraIcon;

    boolean nextEnabled = false;

    //录制视频和保存音频的类
    private volatile FFmpegFrameRecorder videoRecorder;

    //判断是否是前置摄像头
    private boolean isPreviewOn = false;
    private Camera mCamera;

    //预览的宽高和屏幕宽高
    private int previewWidth = 480, screenWidth = 720;
    private int previewHeight = 640, screenHeight = 1280;

    //录制音频的线程
    private AudioRecordRunnable audioRecordRunnable;

    //摄像头以及它的参数
    private Camera cameraDevice;
    private CameraView cameraView;
    Parameters cameraParameters = null;
    //分别为 默认摄像头（后置）、默认调用摄像头的分辨率、被选择的摄像头（前置或者后置）
    int defaultCameraId = -1, defaultScreenResolution = -1, cameraSelection = 0;

    //包含显示摄像头数据的surfaceView
    RelativeLayout topLayout = null;

    //第一次按下屏幕时记录的时间
    long firstTime = 0;
    //手指抬起是的时间
    long startPauseTime = 0;
    //每次按下手指和抬起之间的暂停时间
    long totalPauseTime = 0;
    //手指抬起是的时间
    long pausedTime = 0;
    //总的暂停时间
    long stopPauseTime = 0;
    //录制的有效总时间
    long totalTime = 0;
    //视频帧率
    private int frameRate = RecorderParameters.videoFrameRate;
    //录制的最长时间
    public static int recordingTime = 30000;
    //录制的最短时间
    private int recordingMinimumTime = 6000;
    //提示换个场景
    private int recordingChangeTime = 3000;

    boolean recordFinish = false;
    private Dialog creatingProgress;

    //以下两个只做同步标志，没有实际意义
    private final int[] mVideoRecordLock = new int[0];
    private long mLastAudioTimestamp = 0L;
    private long frameTime = 0L;
    //每一幀的数据结构
    private final FrameData mLastFrameData = new FrameData();
    //视频时间戳
    private long mVideoTimestamp = 0L;
    //时候保存过视频文件
    private boolean isRecordingSaved = false;
    private boolean isFinalizing = false;

    //进度条
    private ProgressView progressView;
    //捕获的第一幀的图片
    private String imagePath = null;
    private RecorderState currentRecorderState = RecorderState.PRESS;
    private ImageView stateImageView;

    private byte[] firstData = null;

    private Frame mFrameData = new Frame(previewWidth, previewHeight, Frame.DEPTH_UBYTE, 2);

    static {
        Context context = Env.getContext();
        ReLinker.recursively().loadLibrary(context, "avcodec");
        ReLinker.recursively().loadLibrary(context, "avformat");
        ReLinker.recursively().loadLibrary(context, "jniswresample");
        ReLinker.recursively().loadLibrary(context, "avutil");
        ReLinker.recursively().loadLibrary(context, "jniavutil");
    }


    private Handler mHandler;

    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void dispatchMessage(Message msg) {
                switch (msg.what) {
                    case 2:
                        int resId = 0;
                        if (currentRecorderState == RecorderState.PRESS) {
                            resId = R.drawable.video_text01;
                        } else if (currentRecorderState == RecorderState.LOOSEN) {
                            resId = R.drawable.video_text02;
                        } else if (currentRecorderState == RecorderState.CHANGE) {
                            resId = R.drawable.video_text03;
                        } else if (currentRecorderState == RecorderState.SUCCESS) {
                            resId = R.drawable.video_text04;
                        }
                        stateImageView.setImageResource(resId);
                        break;
                    case 3:
                        if (!recording)
                            initiateRecording(true);
                        else {
                            //更新暂停的时间
                            stopPauseTime = System.currentTimeMillis();
                            totalPauseTime = stopPauseTime - startPauseTime - ((long) (1.0 / (double) frameRate) * 1000);
                            pausedTime += totalPauseTime;
                        }
                        rec = true;
                        //开始进度条增长
                        progressView.setCurrentState(ProgressView.State.START);
                        //setTotalVideoTime();
                        break;
                    case 4:
                        //设置进度条暂停状态
                        progressView.setCurrentState(ProgressView.State.PAUSE);
                        //将暂停的时间戳添加到进度条的队列中
                        progressView.putProgressList((int) totalTime);
                        rec = false;
                        startPauseTime = System.currentTimeMillis();
                        if (totalTime >= recordingMinimumTime) {
                            currentRecorderState = RecorderState.SUCCESS;
                            mHandler.sendEmptyMessage(2);
                        } else if (totalTime >= recordingChangeTime) {
                            currentRecorderState = RecorderState.CHANGE;
                            mHandler.sendEmptyMessage(2);
                        }
                        break;
                    case 5:
                        currentRecorderState = RecorderState.SUCCESS;
                        mHandler.sendEmptyMessage(2);
                        break;
                    default:
                        break;
                }
            }
        };
    }

    //neon库对opencv做了优化
    static {
        ReLinker.recursively().loadLibrary(Env.getContext(), "checkneon");
    }

    public native static int checkNeonFromJNI();

    private boolean initSuccess = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recorder);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        //Find screen dimensions
        screenWidth = displaymetrics.widthPixels;
        screenHeight = displaymetrics.heightPixels;

        initHandler();

        initLayout();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!initSuccess)
            return false;
        return super.dispatchTouchEvent(ev);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mHandler.sendEmptyMessage(2);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isFinalizing)
            finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Log.i("video", this.getLocalClassName()+"—destory");
        recording = false;
        audioRecordRunnable.setRecordState(false);

        releaseResources();

        if (cameraView != null) {
            cameraView.stopPreview();
            if (cameraDevice != null) {
                cameraDevice.setPreviewCallback(null);
                cameraDevice.release();
            }
            cameraDevice = null;
        }
        firstData = null;
        mCamera = null;
        cameraView = null;
    }

    private void initLayout() {
        stateImageView = (ImageView) findViewById(R.id.recorder_surface_state);

        progressView = (ProgressView) findViewById(R.id.recorder_progress);
        cancelBtn = (Button) findViewById(R.id.recorder_cancel);
        cancelBtn.setOnClickListener(this);
        nextBtn = (Button) findViewById(R.id.recorder_next);
        nextBtn.setOnClickListener(this);
        flashIcon = (Button) findViewById(R.id.recorder_flashlight);
        switchCameraIcon = (Button) findViewById(R.id.recorder_frontcamera);
        flashIcon.setOnClickListener(this);

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            switchCameraIcon.setVisibility(View.VISIBLE);
        }
        initCameraLayout();
    }

    private void initCameraLayout() {
        new AsyncTask<String, Integer, Boolean>() {

            @Override
            protected Boolean doInBackground(String... params) {
                boolean result = setCamera();

                if (!initSuccess) {
                    initVideoRecorder();
                    startRecording();
                    initSuccess = true;
                }

                return result;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (!result || cameraDevice == null) {
                    finish();
                    return;
                }

                topLayout = (RelativeLayout) findViewById(R.id.recorder_surface_parent);
                if (topLayout != null && topLayout.getChildCount() > 0)
                    topLayout.removeAllViews();

                cameraView = new CameraView(FFmpegRecorderActivity.this, cameraDevice);

                handleSurfaceChanged();
                //设置surface的宽高
                RelativeLayout.LayoutParams layoutParam1 = new RelativeLayout.LayoutParams(screenWidth, (int) (screenWidth * (previewWidth / (previewHeight * 1f))));
                layoutParam1.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

                RelativeLayout.LayoutParams layoutParam2 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                layoutParam2.topMargin = screenWidth;

                View view = new View(FFmpegRecorderActivity.this);
                view.setFocusable(false);
                view.setBackgroundColor(Color.BLACK);
                view.setFocusableInTouchMode(false);

                topLayout.addView(cameraView, layoutParam1);
                topLayout.addView(view, layoutParam2);

                topLayout.setOnTouchListener(FFmpegRecorderActivity.this);

                switchCameraIcon.setOnClickListener(FFmpegRecorderActivity.this);
                if (cameraSelection == CameraInfo.CAMERA_FACING_FRONT)
                    flashIcon.setVisibility(View.GONE);
                else
                    flashIcon.setVisibility(View.VISIBLE);
            }

        }.execute("start");
    }

    private boolean setCamera() {
        try {

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
                int numberOfCameras = Camera.getNumberOfCameras();

                CameraInfo cameraInfo = new CameraInfo();
                for (int i = 0; i < numberOfCameras; i++) {
                    Camera.getCameraInfo(i, cameraInfo);
                    if (cameraInfo.facing == cameraSelection) {
                        defaultCameraId = i;
                    }
                }
            }
            stopPreview();
            if (mCamera != null)
                mCamera.release();

            if (defaultCameraId >= 0)
                cameraDevice = Camera.open(defaultCameraId);
            else
                cameraDevice = Camera.open();

        } catch (Exception e) {
            return false;
        }
        return true;
    }


    private void initVideoRecorder() {
        strVideoPath = Util.createFinalPath(this);
        LogUtil.e("strVideoPath=" + strVideoPath);

        RecorderParameters recorderParameters = Util.getRecorderParameter(RecorderParameters.RESOLUTION_MEDIUM_VALUE);
        int sampleRate = recorderParameters.getAudioSamplingRate();
        frameRate = recorderParameters.getVideoFrameRate();
        frameTime = (1000000L / frameRate);

        videoRecorder = new FFmpegFrameRecorder(strVideoPath, recorderParameters.getVidioWidth(), recorderParameters.getVidioHeight(), 1);
        videoRecorder.setFormat(recorderParameters.getVideoOutputFormat());
        videoRecorder.setSampleRate(recorderParameters.getAudioSamplingRate());
        videoRecorder.setFrameRate(recorderParameters.getVideoFrameRate());
        videoRecorder.setVideoCodec(recorderParameters.getVideoCodec());
        videoRecorder.setVideoQuality(recorderParameters.getVideoQuality());
        videoRecorder.setAudioQuality(recorderParameters.getVideoQuality());
        videoRecorder.setAudioCodec(recorderParameters.getAudioCodec());
        videoRecorder.setVideoBitrate(recorderParameters.getVideoBitrate());
        videoRecorder.setAudioBitrate(recorderParameters.getAudioBitrate());

        audioRecordRunnable = new AudioRecordRunnable(videoRecorder, recorderParameters.getAudioSamplingRate());
    }

    public void startRecording() {
        try {
            videoRecorder.start();
            audioRecordRunnable.start();
        } catch (FFmpegFrameRecorder.Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止录制
     *
     * @author QD
     */
    public class AsyncStopRecording extends AsyncTask<Void, Integer, Void> {

        private ProgressBar bar;
        private TextView progress;

        @Override
        protected void onPreExecute() {
            isFinalizing = true;
            recordFinish = true;
            audioRecordRunnable.setRecordState(false);

            //创建处理进度条
            creatingProgress = new Dialog(FFmpegRecorderActivity.this, R.style.Dialog_loading_noDim);
            Window dialogWindow = creatingProgress.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = (int) (getResources().getDisplayMetrics().density * 240);
            lp.height = (int) (getResources().getDisplayMetrics().density * 80);
            lp.gravity = Gravity.CENTER;
            dialogWindow.setAttributes(lp);
            creatingProgress.setCanceledOnTouchOutside(false);
            creatingProgress.setContentView(R.layout.activity_recorder_progress);

            progress = (TextView) creatingProgress.findViewById(R.id.recorder_progress_progresstext);
            bar = (ProgressBar) creatingProgress.findViewById(R.id.recorder_progress_progressbar);
            creatingProgress.show();

            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progress.setText(values[0] + "%");
            bar.setProgress(values[0]);
        }

        /**
         * 依据byte[]里的数据合成一张bitmap，
         * 截成480*480，并且旋转90度后，保存到文件
         *
         * @param data
         */
        private void getFirstCapture(byte[] data) {

            publishProgress(10);

            String captureBitmapPath = Util.createImagePath(FFmpegRecorderActivity.this);
            YuvImage localYuvImage = new YuvImage(data, 17, previewWidth, previewHeight, null);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            FileOutputStream outStream = null;

            publishProgress(50);

            try {
                File file = new File(captureBitmapPath);
                if (!file.exists())
                    file.createNewFile();
                localYuvImage.compressToJpeg(new Rect(0, 0, previewWidth, previewHeight), 100, bos);
                Bitmap localBitmap1 = BitmapFactory.decodeByteArray(bos.toByteArray(),
                        0, bos.toByteArray().length);

                bos.close();

                Matrix localMatrix = new Matrix();
                if (cameraSelection == 0)
                    localMatrix.setRotate(90.0F);
                else
                    localMatrix.setRotate(270.0F);

                Bitmap localBitmap2 = Bitmap.createBitmap(localBitmap1, 0, 0,
                        localBitmap1.getHeight(),
                        localBitmap1.getHeight(),
                        localMatrix, true);

                publishProgress(70);

                ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
                localBitmap2.compress(Bitmap.CompressFormat.JPEG, 100, bos2);

                outStream = new FileOutputStream(captureBitmapPath);
                outStream.write(bos2.toByteArray());
                outStream.close();

                localBitmap1.recycle();
                localBitmap2.recycle();

                publishProgress(90);

                isFirstFrame = false;
                imagePath = captureBitmapPath;
            } catch (FileNotFoundException e) {
                isFirstFrame = true;
                e.printStackTrace();
            } catch (IOException e) {
                isFirstFrame = true;
                e.printStackTrace();
            }
        }


        @Override
        protected Void doInBackground(Void... params) {
            if (firstData != null)
                getFirstCapture(firstData);
            isFinalizing = false;
            if (videoRecorder != null && recording) {
                recording = false;
                releaseResources();
            }
            publishProgress(100);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            creatingProgress.dismiss();
            registerVideo();
            returnToCaller(true);
            videoRecorder = null;
        }

    }

    @Override
    public void onBackPressed() {
        if (!recording)
            videoTheEnd(false);
    }

    //获取第一幀的图片
    private boolean isFirstFrame = true;


    /**
     * 显示摄像头的内容，以及返回摄像头的每一帧数据
     *
     * @author QD
     */
    class CameraView extends SurfaceView implements SurfaceHolder.Callback, PreviewCallback {

        private SurfaceHolder mHolder;


        public CameraView(Context context, Camera camera) {
            super(context);
            mCamera = camera;
            cameraParameters = mCamera.getParameters();
            mHolder = getHolder();
            mHolder.addCallback(CameraView.this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            mCamera.setPreviewCallback(CameraView.this);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                stopPreview();
                mCamera.setPreviewDisplay(holder);
            } catch (IOException exception) {
                mCamera.release();
                mCamera = null;
            }
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (isPreviewOn)
                mCamera.stopPreview();
            handleSurfaceChanged();
            startPreview();
            mCamera.autoFocus(null);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            try {
                mHolder.addCallback(null);
                mCamera.setPreviewCallback(null);

            } catch (RuntimeException e) {
            }
        }

        public void startPreview() {
            if (!isPreviewOn && mCamera != null) {
                isPreviewOn = true;
                mCamera.startPreview();
            }
        }

        public void stopPreview() {
            if (isPreviewOn && mCamera != null) {
                isPreviewOn = false;
                mCamera.stopPreview();
            }
        }

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            //计算时间戳
            long frameTimeStamp = 0L;
            long mAudioTimestamp = audioRecordRunnable.mAudioTimestamp;
            long mAudioTimeRecorded = audioRecordRunnable.mAudioTimeRecorded;
            if (mAudioTimestamp == 0L && firstTime > 0L) {
                frameTimeStamp = 1000L * (System.currentTimeMillis() - firstTime);
            }
            else if (mLastAudioTimestamp == mAudioTimestamp) {
                frameTimeStamp = mAudioTimestamp + frameTime;
            }
            else {
                long l2 = (System.nanoTime() - mAudioTimeRecorded) / 1000L;
                frameTimeStamp = l2 + mAudioTimestamp;
                mLastAudioTimestamp = mAudioTimestamp;
            }

            //录制视频
            synchronized (mVideoRecordLock) {
                if (recording && rec && mLastFrameData != null && mLastFrameData.frameBytesData != null) {
                    //保存某一幀的图片
                    if (isFirstFrame) {
                        isFirstFrame = false;
                        firstData = data;
                    }
                    //超过最低时间时，下一步按钮可点击
                    totalTime = System.currentTimeMillis() - firstTime - pausedTime - ((long) (1.0 / (double) frameRate) * 1000);
                    if (!nextEnabled && totalTime >= recordingChangeTime) {
                        nextEnabled = true;
                        nextBtn.setEnabled(true);
                    }

                    if (nextEnabled && totalTime >= recordingMinimumTime) {
                        mHandler.sendEmptyMessage(5);
                    }

                    if (currentRecorderState == RecorderState.PRESS && totalTime >= recordingChangeTime) {
                        currentRecorderState = RecorderState.LOOSEN;
                        mHandler.sendEmptyMessage(2);
                    }

                    mVideoTimestamp += frameTime;
                    if (mLastFrameData.timeStamp > mVideoTimestamp) {
                        mVideoTimestamp = mLastFrameData.timeStamp;
                    }
                    try {
                        ((ByteBuffer)mFrameData.image[0].position(0)).put(mLastFrameData.frameBytesData);
                        videoRecorder.setTimestamp(mLastFrameData.timeStamp);
                        videoRecorder.record(mFrameData);
                    } catch (FrameRecorder.Exception e) {
                        Log.i("recorder", "录制错误: " + e.getMessage());
                    }
                }
                byte[] tempData = rotateYUV420Degree90(data, previewWidth, previewHeight);
                if (cameraSelection == 1)
                    tempData = rotateYUV420Degree270(data, previewWidth, previewHeight);
                mLastFrameData.timeStamp = frameTimeStamp;
                mLastFrameData.frameBytesData = tempData;
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (!recordFinish) {
            if (totalTime < recordingTime) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //如果MediaRecorder没有被初始化
                        //执行初始化
                        mHandler.removeMessages(3);
                        mHandler.removeMessages(4);
                        mHandler.sendEmptyMessageDelayed(3, 300);
                        break;
                    case MotionEvent.ACTION_UP:
                        mHandler.removeMessages(3);
                        mHandler.removeMessages(4);
                        if (rec)
                            mHandler.sendEmptyMessage(4);

                        break;
                }
            } else {
                //如果录制时间超过最大时间，保存视频
                rec = false;
                saveRecording();
            }
        }
        return true;
    }

    /**
     * 关闭摄像头的预览
     */
    public void stopPreview() {
        if (isPreviewOn && mCamera != null) {
            isPreviewOn = false;
            mCamera.stopPreview();

        }
    }

    private void handleSurfaceChanged() {
        if (mCamera == null) {
            finish();
            return;
        }
        //获取摄像头的所有支持的分辨率
        List<Camera.Size> resolutionList = Util.getResolutionList(mCamera);
        if (resolutionList != null && resolutionList.size() > 0) {
            Collections.sort(resolutionList, new Util.ResolutionComparator());
            Camera.Size previewSize = null;
            if (defaultScreenResolution == -1) {
                boolean hasSize = false;
                //如果摄像头支持640*480，那么强制设为640*480
                for (int i = 0; i < resolutionList.size(); i++) {
                    Size size = resolutionList.get(i);
                    if (size != null && size.width == 640 && size.height == 480) {
                        previewSize = size;
                        hasSize = true;
                        break;
                    }
                }
                //如果不支持设为中间的那个
                if (!hasSize) {
                    int mediumResolution = resolutionList.size() / 2;
                    if (mediumResolution >= resolutionList.size())
                        mediumResolution = resolutionList.size() - 1;
                    previewSize = resolutionList.get(mediumResolution);
                }
            } else {
                if (defaultScreenResolution >= resolutionList.size())
                    defaultScreenResolution = resolutionList.size() - 1;
                previewSize = resolutionList.get(defaultScreenResolution);
            }
            //获取计算过的摄像头分辨率
            if (previewSize != null) {
                previewWidth = previewSize.width;
                previewHeight = previewSize.height;
                cameraParameters.setPreviewSize(previewWidth, previewHeight);
                if (videoRecorder != null) {
                    videoRecorder.setImageWidth(previewWidth);
                    videoRecorder.setImageHeight(previewHeight);
                }

            }
        }
        Log.e("preview", "previewWidth=" + previewWidth + "; previewHeight=" + previewHeight);
        //设置预览帧率
        cameraParameters.setPreviewFrameRate(frameRate);

        //系统版本为8一下的不支持这种对焦
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
            mCamera.setDisplayOrientation(Util.determineDisplayOrientation(FFmpegRecorderActivity.this, defaultCameraId));
            List<String> focusModes = cameraParameters.getSupportedFocusModes();
            if (focusModes != null) {
                Log.i("video", Build.MODEL);
                if (((Build.MODEL.startsWith("GT-I950"))
                        || (Build.MODEL.endsWith("SCH-I959"))
                        || (Build.MODEL.endsWith("MEIZU MX3"))) && focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {

                    cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                    cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                } else
                    cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
            }
        } else
            mCamera.setDisplayOrientation(90);
        mCamera.setParameters(cameraParameters);

    }

    @Override
    public void onClick(View v) {
        //下一步
        if (v.getId() == R.id.recorder_next) {
            if (isRecordingStarted) {
                rec = false;
                saveRecording();
            } else
                initiateRecording(false);
        } else if (v.getId() == R.id.recorder_flashlight) {
            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                //showToast(this, "不能开启闪光灯");
                return;
            }
            //闪光灯
            if (isFlashOn) {
                isFlashOn = false;
                flashIcon.setSelected(false);
                cameraParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
            } else {
                isFlashOn = true;
                flashIcon.setSelected(true);
                cameraParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
            }
            mCamera.setParameters(cameraParameters);
        } else if (v.getId() == R.id.recorder_frontcamera) {
            //转换摄像头
            cameraSelection = ((cameraSelection == CameraInfo.CAMERA_FACING_BACK) ? CameraInfo.CAMERA_FACING_FRONT : CameraInfo.CAMERA_FACING_BACK);
            initCameraLayout();

            if (cameraSelection == CameraInfo.CAMERA_FACING_FRONT)
                flashIcon.setVisibility(View.GONE);
            else {
                flashIcon.setVisibility(View.VISIBLE);
                if (isFlashOn) {
                    cameraParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
                    mCamera.setParameters(cameraParameters);
                }
            }
        } else if (v.getId() == R.id.recorder_cancel) {
            if (!recording)
                videoTheEnd(false);
        }
    }


    /**
     * 结束录制
     */
    public void videoTheEnd(boolean isSuccess) {
        releaseResources();

        returnToCaller(isSuccess);
    }

    /**
     * 设置返回结果
     */
    private void returnToCaller(boolean valid) {
        try {
            setActivityResult(valid);
            if (valid) {
                Intent intent = new Intent(this, FFmpegPreviewActivity.class);
                intent.putExtra("path", strVideoPath);
                intent.putExtra("imagePath", imagePath);
                startActivity(intent);
            }
        } catch (Throwable e) {
        } finally {
            finish();
        }
    }

    private void setActivityResult(boolean valid) {
        Intent resultIntent = new Intent();
        int resultCode;
        if (valid) {
            resultCode = RESULT_OK;
            resultIntent.setData(uriVideoPath);
        } else
            resultCode = RESULT_CANCELED;

        setResult(resultCode, resultIntent);
    }

    /**
     * 向系统注册我们录制的视频文件，这样文件才会在sd卡中显示
     */
    private void registerVideo() {
        Uri videoTable = Uri.parse("content://media/external/video/media");

        Util.videoContentValues.put(Video.Media.SIZE, new File(strVideoPath).length());
        try {
            uriVideoPath = getContentResolver().insert(videoTable, Util.videoContentValues);
        } catch (Throwable e) {
            uriVideoPath = null;
            strVideoPath = null;
            e.printStackTrace();
        } finally {
        }
        Util.videoContentValues = null;
    }


    /**
     * 保存录制的视频文件
     */
    private void saveRecording() {
        if (isRecordingStarted) {
            audioRecordRunnable.setRecordState(false);
            if (!isRecordingSaved) {
                isRecordingSaved = true;
                new AsyncStopRecording().execute();
            }
        } else {
            videoTheEnd(false);
        }
    }

    /**
     * 释放资源，停止录制视频和音频
     */
    private void releaseResources() {
        isRecordingSaved = true;
        try {
            if (videoRecorder != null) {
                videoRecorder.stop();
                videoRecorder.release();
            }
        } catch (FrameRecorder.Exception e) {
            e.printStackTrace();
        }

        videoRecorder = null;

        //progressView.putProgressList((int) totalTime);
        //停止刷新进度
        progressView.setCurrentState(ProgressView.State.PAUSE);
    }

    /**
     * 第一次按下时，初始化录制数据
     *
     * @param isActionDown
     */
    private void initiateRecording(boolean isActionDown) {
        isRecordingStarted = true;
        firstTime = System.currentTimeMillis();

        recording = true;
        totalPauseTime = 0;
        pausedTime = 0;

        //txtTimer.setVisibility(View.VISIBLE);
        //handler.removeCallbacks(mUpdateTimeTask);
        //handler.postDelayed(mUpdateTimeTask, 100);
    }

    public static enum RecorderState {
        PRESS(1), LOOSEN(2), CHANGE(3), SUCCESS(4);

        static RecorderState mapIntToValue(final int stateInt) {
            for (RecorderState value : RecorderState.values()) {
                if (stateInt == value.getIntValue()) {
                    return value;
                }
            }
            return PRESS;
        }

        private int mIntValue;

        RecorderState(int intValue) {
            mIntValue = intValue;
        }

        int getIntValue() {
            return mIntValue;
        }
    }
}