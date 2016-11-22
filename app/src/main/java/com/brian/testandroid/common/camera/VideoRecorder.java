package com.brian.testandroid.common.camera;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.util.Log;

import com.brian.testandroid.common.camera.record.MediaAudioEncoder;
import com.brian.testandroid.common.camera.record.MediaEncoder;
import com.brian.testandroid.common.camera.record.MediaMuxerWrapper;
import com.brian.testandroid.common.camera.record.MediaVideoEncoder;
import com.brian.testandroid.common.camera.video.WindowSurface;
import com.brian.testandroid.util.LogUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Calendar;
import java.util.Date;

import static com.brian.testandroid.common.camera.CameraUtil.TAG;

/**
 *
 * Created by huamm on 2016/11/14 0014.
 */

public class VideoRecorder implements SurfaceTexture.OnFrameAvailableListener, Camera.PreviewCallback {

    public static final int RECORD_STATE_IDEL = 0;
    public static final int RECORD_STATE_INITED = 1;
    public static final int RECORD_STATE_RECORDING= 2;
    public static final int RECORD_STATE_PAUSED= 3;
    public static final int RECORD_STATE_FINISHED= 4;

    private int mRecordState = RECORD_STATE_IDEL;

    private boolean mShouldTakePhoto = false;

    private SurfaceTexture mSurfaceTexture;

    private WindowSurface mInputWindowSurface;

    private WindowSurface mWindowSurface;

    private OnTakePicListener onTakePicListener;

    private String mPhotoPath;

    private Rect mVideoRect;
    MediaMuxerWrapper mMuxer;
    int mSurfaceWidth, mSurfaceHeight;

    private float[][] mShape;
    private float[][] mShape3D;
    private float[][] mConfidence;
    private float[][] mPose;
    private float[] mPoseQuality;
    private float[][] mPupils;
    private float[][] mGaze;



    public void startRecordVideo(String savePath) {
        int windowWidth = 720;
        int windowHeight = 1280;
//        int windowWidth = mWindowSurface.getWidth();
//        int windowHeight = mWindowSurface.getHeight();
        float windowAspect = (float) windowHeight / (float) windowWidth;
        int outWidth = windowWidth;
        int outHeight = (int) (windowHeight * windowAspect);

        int offX = 0;
        int offY = 0;
        mVideoRect.set(offX, offY, offX + outWidth, offY + outHeight);

        try {
            mMuxer = new MediaMuxerWrapper(savePath);    // if you record audio only, ".m4a" is also OK.
            new MediaVideoEncoder(mMuxer, new MediaEncoder.MediaEncoderListener() {
                @Override
                public void onPrepared(MediaEncoder encoder) {
                    LogUtil.log("" + encoder);
                }

                @Override
                public void onStopped(MediaEncoder encoder) {
                    LogUtil.log("" + encoder);
                }
            }, windowWidth, windowHeight);
            new MediaAudioEncoder(mMuxer, null);
            mMuxer.prepare();
            mMuxer.startRecording();
        } catch (final IOException e) {
            Log.e(TAG, "startCapture:", e);
        }
//        mInputWindowSurface = new WindowSurface(mEglCore, ((MediaVideoEncoder) mMuxer.getVideoEncoder()).getInputSurface(), true);
    }

    public void stopRecord() {
        if (mMuxer != null) {
            mMuxer.stopRecording();
            mMuxer = null;
            // you should not wait here
        }
//        if (mInputWindowSurface != null) {
//            mInputWindowSurface.releaseEglSurface();
//            mInputWindowSurface = null;
//        }
    }

    public void pauseRecord() {
        mRecordState = RECORD_STATE_PAUSED;
        if (mMuxer != null) {
            mMuxer.setIsPause(true);
        }
    }

    public void resumeRecord() {
        mRecordState = RECORD_STATE_RECORDING;
        if (mMuxer != null) {
            mMuxer.setIsPause(false);
        }
    }

    public void takePhoto(String savePath, OnTakePicListener listener) {
        mPhotoPath = savePath;
        mShouldTakePhoto = true;
        onTakePicListener = listener;
    }


    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        try {
            long timeStampNanos = System.nanoTime();
            mSurfaceTexture.getTimestamp();
            mSurfaceTexture.updateTexImage();
            boolean swapResult;
            if (mRecordState != RECORD_STATE_RECORDING) {
                // Render the scene, swap back to front.
//                draw();

                swapResult = mWindowSurface.swapBuffers();
            } else {
//                draw();

                swapResult = mWindowSurface.swapBuffers();

                // Draw for recording, swap.
                ((MediaVideoEncoder) mMuxer.getVideoEncoder()).frameAvailableSoon();
                mInputWindowSurface.makeCurrent();
                // If we don't set the scissor rect, the glClear() we use to draw the
                // light-grey background will draw outside the viewport and muck up our
                // letterboxing.  Might be better if we disabled the test immediately after
                // the glClear().  Of course, if we were clearing the frame background to
                // black it wouldn't matter.
                //
                // We do still need to clear the pixels outside the scissor rect, of course,
                // or we'll get garbage at the edges of the recording.  We can either clear
                // the whole thing and accept that there will be a lot of overdraw, or we
                // can issue multiple scissor/clear calls.  Some GPUs may have a special
                // optimization for zeroing out the color buffer.
                //
                // For now, be lazy and zero the whole thing.  At some point we need to
                // examine the performance here.
                GLES20.glClearColor(0f, 0f, 0f, 1f);
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

                GLES20.glViewport(mVideoRect.left, mVideoRect.top,
                        mVideoRect.width(), mVideoRect.height());
                GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
                GLES20.glScissor(mVideoRect.left, mVideoRect.top,
                        mVideoRect.width(), mVideoRect.height());
//                draw();
                GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
                mInputWindowSurface.setPresentationTime(timeStampNanos);
                mInputWindowSurface.swapBuffers();

                // Restore.
                GLES20.glViewport(0, 0, mWindowSurface.getWidth(), mWindowSurface.getHeight());
                mWindowSurface.makeCurrent();

            }

            //照相
            if (mShouldTakePhoto) {
//                saveScreenShotToFile(mPhotoPath, mSurfaceHolder.getSurfaceFrame().width(), mSurfaceHolder.getSurfaceFrame().height());
                mShouldTakePhoto = false;
                onTakePicListener = null;
            }

//            boolean swapped = mWindowSurface.swapBuffers();
            if (!swapResult) {
                Log.e(TAG, "swapBuffers failed, killing renderer thread");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {

    }

//    public void draw() {
//        GLES20.glViewport(0, 0,/*m_iViewBias,*/ mSurfaceWidth, mSurfaceHeight);
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
//        GLUtil.checkGLError("Clear target");
//        //the image appears upside down in landscape mode.
//        int rotation = mCameraRotation;
//        if (rotation != 90) rotation = 180 - mCameraRotation;
//
//        mRenderer.drawScene(mTextureName, rotation,
//                UlsRenderer.mCameraWidth, UlsRenderer.mCameraHeight,
//                mShape[0], mConfidence[0], mPupils[0], mGaze[0], mPose[0], mPoseQuality[0]);
//
//        for (int i = 0; i < TrackerFragment.mMaxTrackers; i++) {
//            if (mShape[i] != null) {
//                draw(i, rotation);
//            }
//        }
//    }
//
//    public void draw(int index, int rotation) {
//        if (mShape[index] == null) {
//            UlsRenderer.ulsrender.setTrackParam(UlsRenderer.mCameraWidth, UlsRenderer.mCameraHeight,
//                    mShape[index], mConfidence[index], mPupils[index], mGaze[index], mPose[index], mPoseQuality[index], cameraIsFront);
//        } else {
//            if (mPose != null && mPoseQuality[index] > 0.0f && UlsRenderer.mbShowMask) {
//                mMaskRender.drawMask(/*sh, myShapeData, */mShape[index], mConfidence[index], 5.0f, rotation, UlsRenderer.mCameraWidth, UlsRenderer.mCameraHeight);
//                if (cameraIsFront) {
//                    UlsRenderer.ulsrender.setTrackParam(UlsRenderer.mCameraWidth, UlsRenderer.mCameraHeight,
//                            mShape[index], mConfidence[index], mPupils[index], mGaze[index], mPose[index], mPoseQuality[index], cameraIsFront);
//                } else {
//                    float[] flippedFaceShape = new float[66 * 2];
//                    flipFaceShape(flippedFaceShape, mShape[index]);
//
//                    UlsRenderer.ulsrender.setTrackParam(UlsRenderer.mCameraWidth, UlsRenderer.mCameraHeight,
//                            flippedFaceShape, mConfidence[index], mPupils[index], mGaze[index], mPose[index],
//                            mPoseQuality[index], cameraIsFront);
//                }
//
//                UlsRenderer.ulsrender.ulsDrawFrame(null);
//            }
//
//            // Modified.
//            if (UlsRenderer.mbShowGlasses) {
//                if (cameraIsFront) {
//                    UlsRenderer.ulsrender.setTrackParam(UlsRenderer.mCameraWidth, UlsRenderer.mCameraHeight,
//                            mShape[index], mConfidence[index], mPupils[index], mGaze[index], mPose[index], mPoseQuality[index], cameraIsFront);
//                } else {
//                    float[] flippedFaceShape = new float[66 * 2];
//                    flipFaceShape(flippedFaceShape, mShape[index]);
//
//                    UlsRenderer.ulsrender.setTrackParam(UlsRenderer.mCameraWidth, UlsRenderer.mCameraHeight,
//                            flippedFaceShape, mConfidence[index], mPupils[index], mGaze[index], mPose[index],
//                            mPoseQuality[index], cameraIsFront);
//                }
//                UlsRenderer.ulsrender.ulsDrawFrame(null);  // Show glasses.
//            }
//            UlsRenderer.slashx = 90.0f + UlsRenderer.ulsrender.getAngle(mShape[index][39 * 2], mShape[index][39 * 2 + 1], mShape[index][42 * 2], mShape[index][42 * 2 + 1]);
//            if (mbShowFacePointAndGaze) {
//                // Draw face points and lines.
//                if (UlsRenderer.mbShowMesh) {
//                    mRenderer.drawMesh(mShape[index], rotation, UlsRenderer.mCameraWidth, UlsRenderer.mCameraHeight);
//                }
//
//                GLES20.glEnable(GLES20.GL_BLEND);
//                if (UlsRenderer.mbShowShape) {
//                    mRenderer.drawShape(mShape[index], mConfidence[index], 5.0f, rotation, UlsRenderer.mCameraWidth, UlsRenderer.mCameraHeight);
//                }
//                if (mPupils != null && UlsRenderer.mbShowPupils) {
//                    float[] qualityRed = {0.0f, 0.0f};
//                    float dist = (float) Math.sqrt((mPupils[index][0] - mPupils[index][2]) *
//                            (mPupils[index][0] - mPupils[index][2]) + (mPupils[index][1] -
//                            mPupils[index][3]) * (mPupils[index][1] - mPupils[index][3]));
//                    float radius = Math.max(1.0f, dist / 14.0f) - 1.0f;
//                    mRenderer.drawShape(mPupils[index], qualityRed, radius, rotation, UlsRenderer.mCameraWidth, UlsRenderer.mCameraHeight);
//                    GLES20.glDisable(GLES20.GL_BLEND);
//                    if (mGaze != null) {
//                        float[] gazept = new float[8];
//                        final float gazeLen = 50.0f;
//                        gazept[0] = mPupils[index][0];
//                        gazept[1] = mPupils[index][1];
//                        gazept[2] = mPupils[index][0] + gazeLen * mGaze[index][0];
//                        gazept[3] = mPupils[index][1] + gazeLen * mGaze[index][1];
//                        gazept[4] = mPupils[index][2];
//                        gazept[5] = mPupils[index][3];
//                        gazept[6] = mPupils[index][2] + gazeLen * mGaze[0][3];
//                        gazept[7] = mPupils[index][3] + gazeLen * mGaze[index][4];
//
//                        mRenderer.drawMesh(gazept, rotation, UlsRenderer.mCameraWidth, UlsRenderer.mCameraHeight);
//                    }
//                } else {
//                    GLES20.glDisable(GLES20.GL_BLEND);
//                }
//            }
//        }
//        //draw rect
//        if (mPose != null && mPoseQuality[index] > 0.0f && UlsRenderer.mbShowPose) {
//            mRenderer.drawPose(mPose[index], mPoseQuality[index], rotation, UlsRenderer.mCameraWidth, UlsRenderer.mCameraHeight);
//        }
//
//        GLES20.glFlush();
//    }




    private void saveScreenShotToFile(String path, int sceneWidth, int sceneHeight) {
        ByteBuffer byBufPixel = ByteBuffer.allocateDirect(sceneWidth * sceneHeight * 4);
        byBufPixel.order(ByteOrder.LITTLE_ENDIAN);
        byBufPixel.rewind();

        Date beginDate = new Date();
        GLES20.glReadPixels(0, 0, sceneWidth, sceneHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, byBufPixel);
        Date date1 = new Date();
        flipGlesPixelBuf(byBufPixel, sceneWidth, sceneHeight);
        Date date2 = new Date();
        Log.e("MENG", "width:" + sceneWidth + "height:" + sceneHeight + "时间：" + (date1.getTime() - beginDate.getTime()) + "-" + (date2.getTime() - date1.getTime()));

        Calendar calNow = Calendar.getInstance();

        // Prepare storage path and file name.


        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(new File(path)));
            Bitmap bmp = Bitmap.createBitmap(sceneWidth, sceneHeight, Bitmap.Config.ARGB_8888);
            byBufPixel.rewind();
            bmp.copyPixelsFromBuffer(byBufPixel);
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, bos);
            bmp.recycle();
            if (onTakePicListener != null) {
                onTakePicListener.onTakePicSuccess();
            }
        } catch (Exception e) {
            if (onTakePicListener != null) {
                onTakePicListener.onTakePicFail();
            }
            return;
        } finally {
            if (bos != null)
                try {
                    bos.close();
                } catch (Exception e) {
                    return;
                }
        }
    }

    private void flipGlesPixelBuf(ByteBuffer buf, int width, int height) {
        int i = 0;
        byte[] tmp = new byte[width * 4];
        while (i++ < height / 2) {
            buf.get(tmp);
            System.arraycopy(buf.array(), buf.limit() - buf.position(), buf.array(), buf.position() - width * 4, width * 4);
            System.arraycopy(tmp, 0, buf.array(), buf.limit() - buf.position(), width * 4);
        }
        buf.rewind();
    }

    public interface OnTakePicListener {
        void onTakePicSuccess();

        void onTakePicFail();
    }
}
