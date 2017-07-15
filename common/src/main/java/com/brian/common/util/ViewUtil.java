package com.brian.common.util;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.brian.common.Env;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 视图相关操作的工具类
 *
 * @author ls
 */
public class ViewUtil {
    /**
     * 禁止下拉（适配魅族手机）
     *
     * @param view
     */
    public static void disableScrollMode(View view) {
        try {
            Method method = AbsListView.class.getMethod("setOverScrollMode",
                    int.class);
            @SuppressWarnings("rawtypes")
            Class viewCls = view.getClass();
            int OVER_SCROLL_NEVER = (Integer) viewCls.getField(
                    "OVER_SCROLL_NEVER").get(view);
            method.invoke(view, OVER_SCROLL_NEVER);
        } catch (SecurityException e) {
            JDLog.printError(e);
        } catch (NoSuchMethodException e) {
            JDLog.printError(e);
        } catch (IllegalArgumentException e) {
            JDLog.printError(e);
        } catch (IllegalAccessException e) {
            JDLog.printError(e);
        } catch (NoSuchFieldException e) {
            JDLog.printError(e);
        } catch (InvocationTargetException e) {
            JDLog.printError(e);
        }
    }

    /**
     * @return void
     * @Description: 动态设置ListView/ grideview的高度
     * @date 2015-2-6 上午8:48:28
     * @update (date)
     */
    public static void setListViewHeight(ListView listView) {

        // 获取ListView对应的Adapter    

        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目    
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高    
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度    
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public static void setListViewHeight(ListView listView, int count) {

        // 获取ListView对应的Adapter

        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        int listCount;

        listCount = count;

        for (int i = 0, len = listCount; i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listCount - 1));
        listView.setLayoutParams(params);
    }

    /**
     * 宽度充满屏幕等比例拉伸图片
     *
     * @param view
     * @param rate 长宽比
     */
    public static void rejustViewByRatioFullWidth(View view, float rate, LayoutParams params) {
        if (view == null || params == null) {
            return;
        }
        int screenWidth = DeviceUtil.getScreenWidth(Env.getContext());
        int height = (int) (screenWidth * rate);

        params.width = screenWidth;
        params.height = height;

        view.setLayoutParams(params);
    }

    public static void setMargins(View view, int left, int top, int right, int bottom) {
        LayoutParams lp = view.getLayoutParams();
        if (lp instanceof FrameLayout.LayoutParams) {
            ((FrameLayout.LayoutParams)lp).setMargins(left, top, right, bottom);
        } else if (lp instanceof RelativeLayout.LayoutParams) {
            ((RelativeLayout.LayoutParams)lp).setMargins(left, top, right, bottom);
        } else if (lp instanceof LinearLayout.LayoutParams) {
            ((LinearLayout.LayoutParams)lp).setMargins(left, top, right, bottom);
        }
    }
}
