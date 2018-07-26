package com.brian.testandroid.activity;

import android.os.Bundle;
import android.widget.ImageView;

import com.brian.common.BaseActivity;
import com.brian.common.util.LogUtil;
import com.brian.common.view.frameanimation.FrameAnimation;
import com.brian.testandroid.R;

import java.util.ArrayList;
import java.util.List;

public class FrameAnimActivity extends BaseActivity {

    private FrameAnimation animation1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frameanim);

        ImageView imageView = (ImageView) findViewById(R.id.image);


        List<String> images = new ArrayList<>();
        images.add("/sdcard/xllive/anim/000@2x.png");
        images.add("/sdcard/xllive/anim/001@2x.png");
        images.add("/sdcard/xllive/anim/002@2x.png");
        images.add("/sdcard/xllive/anim/003@2x.png");
        images.add("/sdcard/xllive/anim/004@2x.png");
        images.add("/sdcard/xllive/anim/005@2x.png");
        images.add("/sdcard/xllive/anim/006@2x.png");
        images.add("/sdcard/xllive/anim/007@2x.png");
        images.add("/sdcard/xllive/anim/008@2x.png");
        images.add("/sdcard/xllive/anim/009@2x.png");
        images.add("/sdcard/xllive/anim/010@2x.png");
        images.add("/sdcard/xllive/anim/011@2x.png");
        images.add("/sdcard/xllive/anim/012@2x.png");
        images.add("/sdcard/xllive/anim/013@2x.png");
        images.add("/sdcard/xllive/anim/014@2x.png");
        images.add("/sdcard/xllive/anim/015@2x.png");
        images.add("/sdcard/xllive/anim/016@2x.png");
        images.add("/sdcard/xllive/anim/017@2x.png");
        images.add("/sdcard/xllive/anim/018@2x.png");
        images.add("/sdcard/xllive/anim/019@2x.png");
        images.add("/sdcard/xllive/anim/020@2x.png");
        images.add("/sdcard/xllive/anim/021@2x.png");
        images.add("/sdcard/xllive/anim/022@2x.png");
        images.add("/sdcard/xllive/anim/023@2x.png");
        images.add("/sdcard/xllive/anim/024@2x.png");

        animation1 = new FrameAnimation();
        animation1.init(imageView, images, 100);

        animation1.setAnimationListener(new FrameAnimation.AnimationStateListener() {
            @Override
            public void onStart() {
                LogUtil.d("onStart");
            }

            @Override
            public void onStop() {
                LogUtil.d("onStop");
            }

            @Override
            public void onRepeat() {
                LogUtil.d("onRepeat");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        animation1.start();
    }

    @Override
    protected void onStop() {
        super.onStop();

        animation1.stop();
    }
}
