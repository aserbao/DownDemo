package com.uutils.utils;

import android.app.Service;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.uutils.crypto.MD5Utils;
import com.uutils.plugin.PushManagerEx;

import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2015/12/17.
 */
public class SystemUtils {
    private static final String KEY_SIM_TIME = "sdklite.sim";
    public static Context sContext;
    public static int mScreenWidth;
    public static int mScreenHeight;
    public static int mDensity;
    public static String mImei;
    public static String mImsi;
    public static int mVersionCode;
    public static String mVersionName;
    public static String mPackageName;
    public static String mIP;
    public static String osLevel;
    public static String phoneName;
    public static String wifiIP;
    public static String channelId;
    public static String networkCountryIso;
    public static String language;
    public static String macAddress;
    public static String ClientID;
    public static String AndroidId;
    public static String Uuid;
    public static String GetuiID;
    /**
     * 第一次插入sim的时间
     */
    public static long FristSIM = 0;
    public static boolean isInit = false;
    public static boolean hasGPSotre = false;
    public static boolean hasFaceBook = false;
    /**
     * 运营商名字
     */
    public static String SIMName;
    public static String apnName;
    public static boolean isRoot = false;

    private static void base(Context context, boolean isCheckRoot) {
        if (context == null || isInit) {
            return;
        }
        isInit = true;
        sContext = context.getApplicationContext();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        mDensity = dm.densityDpi;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        mImei = tm.getDeviceId();
        mImsi = tm.getSubscriberId();
        PackageInfo info = PackageUtils.getAppPackageInfo(context, context.getPackageName());
        if (info != null) {
            mVersionCode = info.versionCode;
            mVersionName = info.versionName;
            mPackageName = info.packageName;
        }
        mIP = OnlineUtils.getIp(context);
        // 系统版本
        osLevel = Build.VERSION.RELEASE;
        // 手机型号
        phoneName = Build.MODEL;
        // 语言
        wifiIP = OnlineUtils.getWifiIpAddress(context);
        channelId = PackageUtils.getMetaData(context, "UMENG_CHANNEL").trim();
        networkCountryIso = getSimCountryIso(context);
        language = getLanguage();
        macAddress = OnlineUtils.getMacAddress(context);
        ClientID = MD5Utils.getStringMD5(mImei + macAddress);
        AndroidId = getAndroidId(context);
        if (!TextUtils.isEmpty(AndroidId)) Uuid = MD5Utils.getStringSHA(AndroidId);
        GetuiID = PushManagerEx.getInstance().getClientid(context);
        FristSIM = getFirstSimTime(context);
        /** 第一次插入sim的时间 */
        hasGPSotre = PackageUtils.isInstall(context, PackageUtils.GOOGLE_PLAY_PKG);
        hasFaceBook = PackageUtils.isInstall(context, PackageUtils.FACEBOOK_PKG1) || PackageUtils.isInstall(context, PackageUtils.FACEBOOK_PKG2) || PackageUtils.isInstall(context, PackageUtils.FACEBOOK_PKG3);
        /** 运营商名字 */
        SIMName = getSIMName(context);
        apnName = getApnName(context);
        if (isCheckRoot) {
            isRoot = RootUtil.isRootSystem();
        }
    }

    public static void init(Context context, boolean isCheckRoot) {
        if (!isInit) {
            base(context, isCheckRoot);
        } else {
            mIP = OnlineUtils.getIp(context);
            wifiIP = OnlineUtils.getWifiIpAddress(context);
            language = getLanguage();
            networkCountryIso = getSimCountryIso(context);
            if (TextUtils.isEmpty(Uuid)) {
                AndroidId = getAndroidId(context);
                Uuid = MD5Utils.getStringSHA(AndroidId);
            }
            if (TextUtils.isEmpty(GetuiID))
                GetuiID = PushManagerEx.getInstance().getClientid(context);
            hasGPSotre = PackageUtils.isInstall(context, PackageUtils.GOOGLE_PLAY_PKG);
            hasFaceBook = PackageUtils.isInstall(context, PackageUtils.FACEBOOK_PKG1) || PackageUtils.isInstall(context, PackageUtils.FACEBOOK_PKG2) || PackageUtils.isInstall(context, PackageUtils.FACEBOOK_PKG3);
            FristSIM = getFirstSimTime(context);
            SIMName = getSIMName(context);
            apnName = getApnName(context);
        }
    }

    public static void init(Context context) {
        init(context, false);
    }

    public static long getFirstSimTime(Context context) {
        long t = PreferenceUtils.getPrefLong(context, KEY_SIM_TIME, 0);
        if (t == 0) {
            return FristSIM;
        }
        return t;
    }

    public static boolean setFirstSim(Context context, long time) {
        long f = PreferenceUtils.getPrefLong(context, KEY_SIM_TIME, 0);
        if (f > 0) {
            Logs.i("已经保存SIM插入时间:" + new Date(f));
            FristSIM = f;
            return false;
        }
        Logs.i("设置SIM插入时间:" + new Date(time));
        FristSIM = time;
        PreferenceUtils.setPrefLong(context, KEY_SIM_TIME, time);
        return true;
    }

    // 判断手机是否有手机卡
    public static String getSimCountryIso(Context context) {
        if (context == null) {
            return "86";
        }
        String iso = "";
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (hasSIM(context)) {
            iso = tm.getNetworkCountryIso();
        }
        if (!TextUtils.isEmpty(iso)) {
            networkCountryIso = iso;
        }
        return networkCountryIso;
    }

    public static String getLanguage() {
        Locale l = Locale.getDefault();
        // language = l.getLanguage();
        language = l.toString();
        return language == null ? "en-US" : language;
    }

    public static boolean hasSIM(Context context) {
        if (context == null) {
            return false;
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        int state = tm.getSimState();
        switch (state) {
            case TelephonyManager.SIM_STATE_READY:// 准备
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:// PIN解锁
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:// PUK解锁
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED://
                return true;
            case TelephonyManager.SIM_STATE_UNKNOWN:// 未知
            case TelephonyManager.SIM_STATE_ABSENT:// 未插卡
                return false;
            default:
                // 无效
                break;
        }
        return false;
    }

    public static String getAndroidId(Context context) {
        String sAndroidId = null;
        try {
            sAndroidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {

        }
        return sAndroidId;
    }

    public static String getSIMName(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            // 运营商
            String simname = tm.getSimOperatorName();
            if (!TextUtils.isEmpty(simname)) {
                SIMName = simname;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SIMName;
    }

    public static String getApnName(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = cm.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                apnName = mNetworkInfo.getExtraInfo();
            }
        }
        return apnName;
    }
}
