package com.brian.common.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;

import com.brian.common.Env;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huamm on 2016/9/18 0018.
 */
public class MarketUtils {
    /**
     * 获取已安装应用商店的包名列表
     */
    public static ArrayList<String> queryInstalledMarketPkgs(Context context) {
        ArrayList<String> pkgs = new ArrayList<>();
        if (context == null) {
            return pkgs;
        }
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.APP_MARKET");
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
        if (infos == null || infos.size() == 0) {
            return pkgs;
        }
        int size = infos.size();
        for (int i = 0; i < size; i++) {
            String pkgName = "";
            try {
                ActivityInfo activityInfo = infos.get(i).activityInfo;
                pkgName = activityInfo.packageName;
            } catch (Exception e) {
                JDLog.printError(e);
            }
            if (!TextUtils.isEmpty(pkgName)) {
                pkgs.add(pkgName);
            }
        }
        return pkgs;
    }

    /**
     * 过滤出已经安装的包名集合
     *
     * @param pkgs 待过滤包名集合
     * @return 已安装的包名集合
     */
    public static ArrayList<String> filterInstalledPkgs(Context context, ArrayList<String> pkgs) {
        ArrayList<String> empty = new ArrayList<>();
        if (context == null || pkgs == null || pkgs.size() == 0) {
            return empty;
        }
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> installedPkgs = pm.getInstalledPackages(0);
        int li = installedPkgs.size();
        int lj = pkgs.size();
        for (int j = 0; j < lj; j++) {
            for (int i = 0; i < li; i++) {
                String installPkg = "";
                String checkPkg = pkgs.get(j);
                try {
                    installPkg = installedPkgs.get(i).applicationInfo.packageName;
                } catch (Exception e) {
                    JDLog.printError(e);
                }
                if (TextUtils.isEmpty(installPkg))
                    continue;
                if (installPkg.equals(checkPkg)) {
                    empty.add(installPkg);
                    break;
                }
            }
        }
        return empty;
    }

    /**
     * 启动到app详情界面
     *
     * @param appPkg    App的包名
     * @param marketPkg 应用商店包名 ,如果为""，则由系统弹出应用商店列表供用户选择,否则调转到目标市场的应用详情界面，某些应用商店可能会失败
     */
    public static void launchAppDetail(String appPkg, String marketPkg) {
        try {

            if (TextUtils.isEmpty(appPkg)) {
                return;
            }

            Uri uri = Uri.parse("market://details?id=" + appPkg);

            launchAppDetail(uri, marketPkg);

        } catch (Exception e) {
            JDLog.printError(e);
        }
    }

    public static void launchAppDetail(Uri appUri, String marketPkg) {

        if (appUri == null){
            return;
        }
        try {

            Intent intent = new Intent(Intent.ACTION_VIEW, appUri);
            if (!TextUtils.isEmpty(marketPkg)) {
                intent.setPackage(marketPkg);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Env.getContext().startActivity(intent);
        } catch (Exception e) {
            JDLog.printError(e);
        }
    }
}
