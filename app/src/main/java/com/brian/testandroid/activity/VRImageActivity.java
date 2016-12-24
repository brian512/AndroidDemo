package com.brian.testandroid.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 解析支付宝VR红包
 * Created by brian on 2016/12/23.
 */

public class VRImageActivity extends BaseActivity {

    private static final int REQUEST_CODE_PICK_IMAGE = 222;


    private ImageView mImageView;
    private Button mOK;
    private Button mSelect;

    @BindView(R.id.startx)
    EditText mStartX;
    @BindView(R.id.starty)
    EditText mStartY;
    @BindView(R.id.width)
    EditText mWidth;
    @BindView(R.id.height)
    EditText mHeight;
    @BindView(R.id.bright)
    EditText mBright;
    @BindView(R.id.blurRadio)
    EditText mBlurR;
    @BindView(R.id.offset)
    EditText mOffset;
    @BindView(R.id.imagePath)
    TextView mImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vrimage);
        ButterKnife.bind(this);

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
        getScreenshots();
    }

    private void initData() {
        BasePreference preference = BasePreference.initPreference("");
        mStartX.setText(preference.getString("mStartX", "105"));
        mStartY.setText(preference.getString("mStartY", "290"));
        mWidth.setText(preference.getString("mWidth", "150"));
        mHeight.setText(preference.getString("mHeight", "200"));
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
        Bitmap secondBitmap = getTransparentBitmap(sourceBitmap, transparent);
        Bitmap bitmap = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight() + offset, Bitmap.Config.ARGB_8888);
        Rect baseRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Rect sourceRect = new Rect(0, 0, secondBitmap.getWidth(), secondBitmap.getHeight());
        Rect secondRect = new Rect(0, offset, secondBitmap.getWidth(), secondBitmap.getHeight()+offset);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(sourceBitmap, sourceRect, baseRect, getBrightPaint(bright));
        canvas.drawBitmap(secondBitmap, secondRect, baseRect, getBrightPaint(bright));

        return BitmapUtil.blurBitmapUseSysApi(bitmap, blurR);
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
