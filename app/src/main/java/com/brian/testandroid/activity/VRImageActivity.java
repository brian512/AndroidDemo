package com.brian.testandroid.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.brian.common.BaseActivity;
import com.brian.common.ThreadPoolManager;
import com.brian.common.util.BitmapUtil;
import com.brian.common.util.DeviceUtil;
import com.brian.common.util.LogUtil;
import com.brian.common.util.ToastUtil;
import com.brian.common.util.UIUtil;
import com.brian.testandroid.R;
import com.brian.testandroid.common.BasePreference;

import java.io.File;

/**
 * 解析支付宝VR红包
 * Created by brian on 2016/12/23.
 */

public class VRImageActivity extends BaseActivity {

    private static final int REQUEST_CODE_PICK_IMAGE = 222;


    private ImageView mImageView;
    private Button mOK;
    private Button mSelect;

    private EditText mStartX;
    private EditText mStartY;
    private EditText mWidth;
    private EditText mHeight;
    private EditText mBright;
    private EditText mBlurR;
    private EditText mOffset;
    private TextView mImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vrimage);
        mStartX = (EditText) findViewById(R.id.startx);
        mStartY = (EditText) findViewById(R.id.starty);
        mWidth = (EditText) findViewById(R.id.width);
        mHeight = (EditText) findViewById(R.id.height);
        mBright = (EditText) findViewById(R.id.bright);
        mBlurR = (EditText) findViewById(R.id.blurRadio);
        mOffset = (EditText) findViewById(R.id.offset);
        mImagePath = (TextView) findViewById(R.id.imagePath);

        initData();

        mImageView = (ImageView) findViewById(R.id.image);
        mOK = (Button) findViewById(R.id.ok);
        mSelect = (Button) findViewById(R.id.select);

        mSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");//相片类型
                startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
            }
        });

        mOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doParse();
            }
        });

        getScreenshots();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!mHasPickImage) {
            getScreenshots();
            mHasPickImage = false;
        }
    }

    private void initData() {
        BasePreference preference = BasePreference.initPreference("");
        mStartX.setText(preference.getString("mStartX", "105"));
        mStartY.setText(preference.getString("mStartY", "315"));
        mWidth.setText(preference.getString("mWidth", "150"));
        mHeight.setText(preference.getString("mHeight", "150"));
        mBright.setText(preference.getString("mBright", "50"));
        mBlurR.setText(preference.getString("mBlurR", "1"));
        mOffset.setText(preference.getString("mOffset", "2.5"));
    }

    private void saveData() {
        BasePreference preference = BasePreference.initPreference("");
        preference.putString("mStartX", mStartX.getText().toString());
        preference.putString("mStartY", mStartY.getText().toString());
        preference.putString("mWidth", mWidth.getText().toString());
        preference.putString("mHeight", mHeight.getText().toString());
        preference.putString("mBright", mBright.getText().toString());
        preference.putString("mBlurR", mBlurR.getText().toString());
        preference.putString("mOffset", mOffset.getText().toString());
    }

    private void doParse() {
        if (mImageUri == null) {
            ToastUtil.showMsg("先选择图片");
            return;
        }
        mImagePath.setText(mImageUri.toString().substring(mImageUri.toString().lastIndexOf("/")));
        saveData();
        final int startX = UIUtil.dp2Px(Integer.valueOf(mStartX.getText().toString()));
        final int startY = UIUtil.dp2Px(Integer.valueOf(mStartY.getText().toString()));
        final int width = UIUtil.dp2Px(Integer.valueOf(mWidth.getText().toString()));
        final int height = UIUtil.dp2Px(Integer.valueOf(mHeight.getText().toString()));
        final int offset = UIUtil.dp2Px(Float.valueOf(mOffset.getText().toString()));
        final int brightFront = Integer.valueOf(mBright.getText().toString());
        final float blurR = Float.valueOf(mBlurR.getText().toString());

        ThreadPoolManager.getPoolProxy().execute(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = BitmapUtil.readBitmap(getApplicationContext(), mImageUri);
                if (bitmap == null) {
                    return;
                }
                LogUtil.log("bitmap.getWidth()=" + bitmap.getWidth());
                LogUtil.log("getScreenWidth=" + DeviceUtil.getScreenWidth(getApplicationContext()));
                if (bitmap.getWidth() == DeviceUtil.getScreenWidth(getApplicationContext())) {
                    bitmap = clipImage(bitmap, startX, startY, width, height);
                }
                final Bitmap result = getDealedImage(bitmap, brightFront, 50, blurR, offset);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mImageView.setImageBitmap(result);
                    }
                });
            }
        });
    }

    private Bitmap clipImage(Bitmap sourcBitmap, int startX, int startY, int width, int height) {
        LogUtil.log("startX=" + startX
                + "; startY=" + startY
                + "; width=" + width
                + "; height=" + height
        );
        LogUtil.log("getWidth=" + sourcBitmap.getWidth());
        LogUtil.log("getHeight=" + sourcBitmap.getHeight());

        return BitmapUtil.clipBitmap(sourcBitmap, startX, startY, width, height);
    }

    private Bitmap getDealedImage(Bitmap sourceBitmap, int bright, int transparent, float blurR, int offset) {

//        dealPixels(sourceBitmap);

        Bitmap secondBitmap = getTransparentBitmap(sourceBitmap, transparent);
        Bitmap bitmap = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight() + offset, Bitmap.Config.ARGB_8888);
        Rect baseRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Rect sourceRect = new Rect(0, 0, secondBitmap.getWidth(), secondBitmap.getHeight());
        Rect secondRect = new Rect(0, offset, secondBitmap.getWidth(), secondBitmap.getHeight()+offset);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(sourceBitmap, sourceRect, baseRect, getBrightPaint(bright));
        canvas.drawBitmap(secondBitmap, secondRect, baseRect, getBrightPaint(bright));

//        return sourceBitmap;
        return BitmapUtil.blurBitmapUseSysApi(bitmap, blurR);
    }

    private void dealPixels(Bitmap sourceBitmap) {
        int redCount = 0;
        int greenCount = 0;
        int blueCount = 0;

        int width = sourceBitmap.getWidth();
        int height = sourceBitmap.getHeight();
        int lastRed = 0;
        int lastGreen = 0;
        int lastBlue = 0;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int pixel = sourceBitmap.getPixel(col, row);// ARGB
                int red = Color.red(pixel); // same as (pixel >> 16) &0xff
                int green = Color.green(pixel); // same as (pixel >> 8) &0xff
                int blue = Color.blue(pixel); // same as (pixel & 0xff)

                redCount += red;
                greenCount += green;
                blueCount += blue;
                if (col == width-1) {
                    redCount=redCount/width;
                    greenCount=greenCount/width;
                    blueCount=blueCount/width;

//                    LogUtil.log("row:" + row
//                            + "  redCount=" + redCount
//                            + ",greenCount=" + greenCount
//                            + ",blueCount=" + blueCount
//                    );
                    int delta = 10;
                    if ((lastRed+lastBlue+lastGreen > 0)
                            && redCount < lastRed-delta
                            && greenCount < lastGreen-delta
                            && blueCount < lastBlue-delta
                            &&(redCount+greenCount+blueCount) < (lastRed+lastBlue+lastGreen)-45
                            ) {
                        LogUtil.e("row:" + row);
                        replaceRowPixels(sourceBitmap, row);
                    } else {
                        lastRed = redCount;
                        lastGreen = greenCount;
                        lastBlue = blueCount;
                    }

                    redCount = 0;
                    greenCount = 0;
                    blueCount = 0;
                }
            }
        }
    }

    private void replaceRowPixels(Bitmap sourceBitmap, int row) {
        if (row <= 1) {
            return;
        }

        for (int i = 0; i < sourceBitmap.getWidth(); i++) {
            int prePixel = sourceBitmap.getPixel(i, row-1);// ARGB
            int prered = Color.red(prePixel); // same as (pixel >> 16) &0xff
            int pregreen = Color.green(prePixel); // same as (pixel >> 8) &0xff
            int preblue = Color.blue(prePixel); // same as (pixel & 0xff)

            int pixel = sourceBitmap.getPixel(i, row);// ARGB
            int red = Color.red(pixel); // same as (pixel >> 16) &0xff
            int green = Color.green(pixel); // same as (pixel >> 8) &0xff
            int blue = Color.blue(pixel); // same as (pixel & 0xff)

            try {
                sourceBitmap.setPixel(i, row,
                        Color.rgb(getInt(prered, red),
                                getInt(pregreen, green) ,
                                getInt(preblue, blue))
                );
            } catch (Exception e) {}

        }
    }

    private int getInt(int preV, int v) {
//        LogUtil.log("preV=" + preV + "; v=" + v + "; result=" + (preV+v)/2);
        return Math.min(255, (preV + v + 100)/2);
//        return preV;
//        return (preV+v)/2;
    }

    public static Paint getBrightPaint(int brightness) {
        ColorMatrix cMatrix = new ColorMatrix();
        cMatrix.set(new float[] { 1, 0, 0, 0, brightness, 0, 1,
                0, 0, brightness,// 改变亮度
                0, 0, 1, 0, brightness, 0, 0, 0, 1, 0 });

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));
        return paint;
    }

    public static Bitmap getTransparentBitmap(Bitmap sourceImg, int number){
        int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];
        sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg
                .getWidth(), sourceImg.getHeight());// 获得图片的ARGB值
        number = number * 255 / 100;

        for (int i = 0; i < argb.length; i++) {
            argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);
        }

        sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(), sourceImg
                .getHeight(), Bitmap.Config.ARGB_8888);
        return sourceImg;
    }

    private boolean mHasPickImage = false;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_IMAGE) {
            if (data == null || data.getData() == null) {
                return;
            }
            Uri uri = data.getData();
            //to do find the path of pic
            LogUtil.log("uri=" + uri);
            mImageUri = uri;
            mHasPickImage = true;

            doParse();
        }
    }

    private Uri mImageUri;

    private void getScreenshots() {
        File pix = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File screenshots = new File(pix, "Screenshots");
        LogUtil.log("screenshots=" + screenshots);
        if (screenshots.isDirectory()) {
            File[] children = screenshots.listFiles();
            LogUtil.log("children=" + children);
            if (children != null && children.length > 0 ) {
                for (File c : children) {
                    LogUtil.log("" + c);
                }
                mImageUri = Uri.fromFile(children[children.length-1]);
                doParse();
            }
        }
    }
}
