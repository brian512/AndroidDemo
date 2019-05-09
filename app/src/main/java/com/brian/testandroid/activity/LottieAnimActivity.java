package com.brian.testandroid.activity;

import android.os.Bundle;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.brian.common.BaseActivity;
import com.brian.testandroid.R;

public class LottieAnimActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottie_anim);


        LottieAnimationView lottieAnimationView1 = (LottieAnimationView) findViewById(R.id.animation_view1);
        lottieAnimationView1.loop(true);
        lottieAnimationView1.playAnimation();

        LottieAnimationView lottieAnimationView2 = (LottieAnimationView) findViewById(R.id.animation_view2);
        lottieAnimationView2.loop(true);
        lottieAnimationView2.playAnimation();

        LottieAnimationView lottieAnimationView3 = (LottieAnimationView) findViewById(R.id.animation_view3);
        lottieAnimationView3.loop(true);
        lottieAnimationView3.playAnimation();
    }
}
