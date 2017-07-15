package com.brian.common.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.brian.common.Env;

/**
 * 获得设备唯一识别码PeerID
 * <p>
 * 注意：由于NetUtil.getWifiMacAddress(context)函数不太可靠(手机启动从来没有开过wifi的情况下，使用这个函数会返回null)，
 * 所以getPeerID采用(生成后保存到preference，一直沿用)的策略
 */
public class PeerIDUtil {


    /**
     * 获取peerid
     */
    private static String sPeerID;

    public static String getPeerID(Context context) {

        // 先尝试从preference获取
        if (sPeerID == null) {
            SharedPreferences preferences = Env.getContext().getSharedPreferences("PeerIDUtil", 0);
            String peerID = preferences.getString("peerID", "");
            if (!TextUtils.isEmpty(peerID)) {
                sPeerID = peerID;
            }
        }

        // 只获取一次
        if (sPeerID == null) {
            String mac_address = NetUtil.getWifiMacAddress(context);
            if (mac_address == null) {
                mac_address = DeviceUtil.getSerialId(context);
            }

            char[] peerid = new char[16];
            for (int idx = 0; idx < mac_address.length(); idx++) {
                if (idx * 2 >= peerid.length - 1)
                    break;

                char ch = (char) (mac_address.charAt(idx) >> 4);
                peerid[idx * 2] = (char) ((ch) < 10 ? (ch) + '0'
                        : (ch) - 10 + 'A');
                ch = (char) (mac_address.charAt(idx) & 0x0F);
                peerid[idx * 2 + 1] = (char) ((ch) < 10 ? (ch) + '0'
                        : (ch) - 10 + 'A');
            }
            sPeerID = new String(peerid) + "003V";

            // 保存到preference
            SharedPreferences preferences = Env.getContext().getSharedPreferences("PeerIDUtil", 0);
            Editor editor = preferences.edit();
            editor.putString("peerID", sPeerID);
            editor.apply();
        }

        return sPeerID;
    }
}
