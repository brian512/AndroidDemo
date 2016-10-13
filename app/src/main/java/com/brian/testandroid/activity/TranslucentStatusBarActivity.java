package com.brian.testandroid.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.brian.testandroid.R;
import com.brian.testandroid.common.BaseActivity;

public class TranslucentStatusBarActivity extends BaseActivity {

    private boolean mVisible = true;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
        setContentView(R.layout.activity_statusbar);
        ImageView view = (ImageView) findViewById(R.id.image);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVisible = !mVisible;
                setSystemUIVisible(mVisible);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
//        setTranslucentSystemUI();
        setSystemUIVisible(false);

//        getUIHandler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                setTranslucentSystemUI();
//            }
//        }, 500);
    }

    private void setTranslucentSystemUI() {
        Window window = getWindow();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            window.setStatusBarColor(Color.TRANSPARENT);
//            window.setNavigationBarColor(Color.TRANSPARENT);
//        } else
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Translucent status bar
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Translucent navigation bar
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }

    private void setSystemUIVisible(boolean visible) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return;
        }
        mVisible = visible;
        int visibility = getWindow().getDecorView().getSystemUiVisibility();
        if (visible) {
            visibility &= (~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
//                            & (~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
//                            & (~View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
                            & (~View.SYSTEM_UI_FLAG_FULLSCREEN)
//                            & (~View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            ;
        } else {
            visibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // 隐藏状态栏
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // 全屏布局，状态栏（非透明背景）会盖在布局上
                                                                    // 与SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN一起使用后，可全屏显示布局
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // 隐藏状态栏
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION // 全屏布局，导航栏（非透明背景）会盖在布局上
            ;
        }
        getWindow().getDecorView().setSystemUiVisibility(visibility);
    }

    private void setFullScreenEnable(boolean enable) {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        if (enable) {
            params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN; // 布局占用状态栏，并隐藏状态栏，不影响导航栏
        } else {
            params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS); // 全屏布局，状态栏和导航栏覆盖在布局上
        window.setAttributes(params);
    }
}
