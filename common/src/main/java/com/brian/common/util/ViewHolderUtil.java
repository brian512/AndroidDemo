package com.brian.common.util;

import android.util.SparseArray;
import android.view.View;

/**
 * 复用布局的ViewHolder相关的工具类
 * 
 * @author ls
 *
 */
public class ViewHolderUtil {
    
    /**
     * 获取容器中指定的元素
     * 
     * @param view
     * @param id
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends View> T get(View view, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag(view.getId());
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            view.setTag(view.getId(), viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }
}
