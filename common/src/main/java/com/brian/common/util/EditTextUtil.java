package com.brian.common.util;

import android.text.Selection;
import android.text.Spannable;
import android.widget.EditText;

/**
 * EditText的工具类
 * 目前收集的功能有：1.光标定位到尾端
 * 
 * @author ls
 *
 */
public class EditTextUtil {
    
    /**
     * 设置EditText的光标定位到文字后面
     */
    public static void setCursorToEnd(EditText editText){
        CharSequence text = editText.getText();
        if (text != null) {
            Spannable spanText = (Spannable)text;
            Selection.setSelection(spanText, text.length());
        }
    }
}
