
package com.brian.common.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import com.brian.common.Env;

import java.lang.reflect.Field;

/**
 * 输入法先关工具类
 */
public class InputMethodUtil {

    public static final String TAG = InputMethodUtil.class.getSimpleName();
    
    private static boolean sLastVisiable = false;
    private static boolean mSoftWareIsShowing = false;

    /**
     * 隐藏软键盘
     * 
     * @param context
     * @param view
     */
    public static void hiddenInput(Context context, View view) {
        if (context != null && view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 显示软键盘 （PS：这里使用切换/显示的方式）
     * 
     * @param context
     */
    public static void showInput(Context context, View view) {
        if (context != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }
    }
    
    /**
     * 切换 显示/隐藏 软键盘
     * 
     * @param context
     */
    public static void toggleInput(Context context) {
        if (context != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(0,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 查询输入法面板是否已弹出
     * 
     * @param context
     * @return
     */
    public static boolean isInputActive(Context context) {
        if (context != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            return inputMethodManager.isActive();
        }
        return false;
    }

    /**监听软键盘状态
     * 注意：
     * 1、需要监听键盘收起事件的Activity，不能声明 android:windowSoftInputMode="adjustNothing"， 否则会onGlobalLayout的时候
     *    displayHight永远不会变化，导致无法触发回调
     * 2、因为只有当前在显示的Activity的decorView才会触发onGlobalLayout，所以在多个Activity同时绑定监听，也只会有当前的Activity
     *    会触发回调
     * @param activity
     * @param listener
     */
    public static void addOnSoftKeyBoardVisibleListener(Activity activity, final OnSoftKeyBoardVisibleListener listener) {
        
    	JDLog.log(TAG, String.format("addOnSoftKeyBoardVisibleListener, activity : %s, listener : %s", activity.toString(), listener.toString()));
    	
    	final View decorView = activity.getWindow().getDecorView();
        sLastVisiable = false;
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                decorView.getWindowVisibleDisplayFrame(rect);
                int displayHight = rect.bottom - rect.top;
                int hight = decorView.getHeight();
                boolean visible = (double) displayHight / hight < 0.8;

                if(visible != sLastVisiable){
                    listener.onSoftKeyBoardVisible(visible);
                    JDLog.log(TAG, String.format("addOnSoftKeyBoardVisibleListener, onSoftKeyBoardVisible visible : %s", visible));
                }
                sLastVisiable = visible; 
                
                JDLog.log("tag", "height input: "+ (DeviceUtil.getScreenHeight(Env.getContext()) - hight) +" "+hight);
            }
        });
    }
    
    public static  void showOnSoftKeyBoardHeight(Context context){
    	InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    	try {
			Field rect1		= InputMethodManager.class.getField("mTmpCursorRect");
			Field rect2Field		= InputMethodManager.class.getField("mCursorRect");
			
			rect1.setAccessible(true);
			Rect curRootView			= (Rect) rect1.get(inputMethodManager);
			if(curRootView != null){
				JDLog.log(TAG, "show : curRootView height "+ curRootView.top	 +"  "+ curRootView.bottom);
			}
			
			rect2Field.setAccessible(true);
			Rect rect2				= (Rect) rect2Field.get(inputMethodManager);
			if(rect2 != null){
				JDLog.log(TAG, "show : servedView height "+ rect2.top +"  "+ rect2.bottom);
			}
			
			JDLog.log(TAG, "show soft height");
		} catch (NoSuchFieldException e) {
			JDLog.printError(e);
		} catch (IllegalAccessException e) {
			JDLog.printError(e);
		} catch (IllegalArgumentException e) {
			JDLog.printError(e);
		}
    }

    public interface OnSoftKeyBoardVisibleListener {
        void onSoftKeyBoardVisible(boolean visible);
    }

    public static boolean isISoftWareShowing(){
        return mSoftWareIsShowing;
    }


    /**
     * 指定特定的View与键盘一起上移和下移
     * @param rootView
     * @param toggleView
     */
    public static void toggleInputWithView(final View rootView, final View toggleView){

        final int minHeight = DipPixelUtil.dip2px(Env.getContext(),100);//大于该高度则认为键盘弹起键盘

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            int bottomVirtualViewHeight = 0;//底部虚拟按键高度，默认为0
            int mSoftWareHeight;
            @Override
            public void onGlobalLayout() {
                // 需要先在Activity中设置键盘模式为adjustPan,然后在键盘弹出时把输入框移动上去，这样系统就不会改变屏幕大小
                Rect rect = new Rect();
                rootView.getWindowVisibleDisplayFrame(rect);

                int invisibleHeight = rootView.getRootView().getHeight() - rect.bottom;

                if (invisibleHeight < minHeight){//兼容一下包含底部虚拟按键的手机,弹出键盘时需要减去底部虚拟按键高度
                    bottomVirtualViewHeight = invisibleHeight;
                }

                if (invisibleHeight > minHeight) {
                    mSoftWareHeight = invisibleHeight;
                    toggleView.setTranslationY(-mSoftWareHeight+bottomVirtualViewHeight);
                    mSoftWareIsShowing = true;
                } else if (mSoftWareIsShowing) {
                    toggleView.setTranslationY(0);
                    mSoftWareIsShowing = false;
                }
            }
        });

    }

}
