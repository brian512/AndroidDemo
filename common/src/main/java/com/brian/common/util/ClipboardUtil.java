package com.brian.common.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;

/**
 * 剪贴板相关操作
 * Created by brian on 17-6-21.
 */

public class ClipboardUtil {

    private static final String LABEL_ROKK = "lable_rokk";

    public static void coypText(Context context, CharSequence text) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(LABEL_ROKK, text);
        clipboardManager.setPrimaryClip(clipData);
    }

    public static void coypText(Context context, Uri uri) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newUri(context.getContentResolver(), LABEL_ROKK, uri);
        clipboardManager.setPrimaryClip(clipData);
    }

    public static CharSequence getText(Context context) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager.hasPrimaryClip()) {
            ClipData clipData = clipboardManager.getPrimaryClip();
            return clipData.toString();
        } else {
            return "";
        }
    }
}
