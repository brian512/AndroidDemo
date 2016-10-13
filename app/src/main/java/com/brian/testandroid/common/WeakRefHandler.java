package com.brian.testandroid.common;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * 实现回调弱引用的Handle
 * 防止由于内部持有导致的内存泄露
 * 
 * PS：
 * 1、传入的Callback不能使用匿名实现的变量，必须与使用这个Handle的对象的生命周期一致，否则会被立即释放掉了
 * 
 * @author huamm
 */
public class WeakRefHandler extends Handler {
    private WeakReference<Callback> mWeakReference;
    
    public WeakRefHandler(Callback callback) {
        mWeakReference = new WeakReference<Callback>(callback);
    }
    
    public WeakRefHandler(Callback callback, Looper looper) {
        super(looper);
        mWeakReference = new WeakReference<Callback>(callback);
    }
    
    @Override
    public void handleMessage(Message msg) {
        if (mWeakReference != null && mWeakReference.get() != null) {
            Callback callback = mWeakReference.get();
            callback.handleMessage(msg);
        }
    }
}
