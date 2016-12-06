package com.brian.common.util;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class UriUtil {

    /**
     * 根据Uri获取绝对路径
     * PS: 根据友盟反馈以及网上资料得知acquireUnstableProvider方法存在bug，当传入的参数uri为null的时候，可能会崩溃
     * 所以对传入参数做判空处理，并且捕获空指针异常
     * @param activity
     * @param uri
     * @return
     */
    public static String getAbsoluteImagePath(Activity activity ,Uri uri) {
        if(uri == null){
            return "";
        }
        
        try{            
            String[] proj = {MediaStore.Images.Media.DATA};            
            Cursor cursor = activity.managedQuery(uri, proj, null, null, null);
            if (cursor == null) {
                return "";
            } else {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            }            
        }catch(NullPointerException e){
            
        }
        
        return "";
    }
}
