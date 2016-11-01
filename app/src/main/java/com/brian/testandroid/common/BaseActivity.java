
package com.brian.testandroid.common;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.brian.testandroid.util.LogUtil;

/**
 * 封装的FragmentActivity，原则上工程内部的所有Activity都要继承这个类
 * 
 * 在此实现的功能包括: 友盟统计上报的功能控制 内存紧张时缓存图片的释放
 * 
 * @author ls
 */
public class BaseActivity extends AppCompatActivity {

    protected static final String TAG = BaseActivity.class.getSimpleName();

    /**
     * 记录当前最顶部的Activity
     */
    private static BaseActivity sTopActivity = null;

    /**
     * 记录界面切这个界面的时候是否是在后台
     */
    private static boolean sIsInBackstage = false;

    /**
     * 标志这个activity是否正处於活跃状态
     */
    protected static boolean sIsActivityActive = false;

    /**
     * 获取顶部的Activity
     */
    public static BaseActivity getTopActivity() {
        return sTopActivity;
    }

    public void startActivity(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    /**
     * onCreate
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.log(TAG, "onCreate：" + getClass().getSimpleName());

        sTopActivity = this; // 确保最新一个Activity创建后，TopActivity立马指向它，此时一些操作可以在onCreate函数执行
    }

    @Override
    protected void onStart() {
        LogUtil.log(TAG, "onStart：" + getClass().getSimpleName());
        super.onStart();
    }

    @Override
    protected void onRestart() {
        LogUtil.log(TAG, "onRestart：" + getClass().getSimpleName());
        super.onRestart();
    }

    /**
     * onResume
     */
    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.log(TAG, "onResume：" + getClass().getSimpleName());
        // 产品逻辑：拉取新的漂流瓶和回复信息
        // 注意：1.4版本为了保证关小黑屋图片及时被去掉，改为从后台切前台都去执行拉取数据操作，去掉了时间间隔限制
        // 2.0改版，去掉了拉取以前的拉取逻辑，改为只使用新协议刷新漂流瓶

        if (sIsInBackstage) {

            sIsInBackstage = false;
        }

        sTopActivity = this; // 确保上一个Activity销毁后，TopActivity指到最上一个Activity
        sIsActivityActive = true;
    }

    /**
     * onPause
     */
    @Override
    protected void onPause() {
        sIsActivityActive = false;

        LogUtil.log(TAG, "onPause：" + getClass().getSimpleName());
        super.onPause();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    /**
     * onDestroy
     */
    @Override
    protected void onDestroy() {
        LogUtil.log(TAG, "onDestroy：" + getClass().getSimpleName());

        super.onDestroy();
    }

    @Override
    protected void onStop() {
        LogUtil.log(TAG, "onStop：" + getClass().getSimpleName());
        super.onStop();
    }

    public static Handler getUIHandler() {
        Handler handler = sTopActivity.getWindow().getDecorView().getHandler();
        if (handler == null) {
            handler = new Handler();
        }
        return handler;
    }

}
