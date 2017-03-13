package com.uutils;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;

import com.uutils.crypto.DESUtils;
import com.uutils.plugin.Analytics;
import com.uutils.utils.CrashHandler;
import com.uutils.utils.KingUserUtils;
import com.uutils.utils.Logs;
import com.uutils.utils.PackageUtils;
import com.uutils.utils.PreferenceUtils;
import com.uutils.utils.ReflectUtils;

public class u {

    /** 应用包名 */
    /**
     * 应用包名
     */
    public static String PKGNAME = null;
    public static int VersionCode = 0;
    public static String VersionName = null;
    public static Context sContext;

    /***
     * 以默认值初始化
     *
     * @param app
     */
    public static void init(Application app) {
        PKGNAME = app.getPackageName();
        sContext = app.getApplicationContext();
        PackageInfo packageInfo = PackageUtils.getAppPackageInfo(app, PKGNAME);
        if (packageInfo != null) {
            VersionCode = packageInfo.versionCode;
            VersionName = packageInfo.versionName;
        }

        init(app, BuildConfig.DEBUG, app.getClass().getSimpleName());
    }


    /***
     * 初始化
     *
     * @param app
     * @param log 打印log
     * @param tag logcat的tag
     */
    public static void init(Application app, boolean log, String tag) {


        PKGNAME = app.getPackageName();
        sContext = app.getApplicationContext();
        PackageInfo packageInfo = PackageUtils.getAppPackageInfo(app, PKGNAME);
        if (packageInfo != null) {
            VersionCode = packageInfo.versionCode;
            VersionName = packageInfo.versionName;
        }

        Logs.sLog = log;
        Logs.TAG = tag;
        PreferenceUtils.sSharedPreferencesKey = app.getPackageName() + ".uutils.prefs";
        KingUserUtils.init(app);
        Analytics.init(app, log);
        CrashHandler.getInstance().init(app);
    }

    /**
     * 以默认key，des解密
     *
     * @param str
     * @return
     */
    public static String d(String str) {
        return DESUtils.decrypt(str, ReflectUtils.TAG);
    }
}

//
//class Test {
//    public static void main(String[] args) {
//        System.out.println("" + ReflectUtils.TAG);
//        System.out.println(DESUtils.encrypt("AccuWeatherProvider", String.class.getName()));
//    }
//}