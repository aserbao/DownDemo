package com.uutils.utils;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import java.io.File;
import java.util.List;
import java.util.Locale;

import static com.uutils.u.d;

/**
 * Created by Administrator on 2015/12/17.
 */
public class PackageUtils {
    /**
     * com.android.vending
     */
    public static final String GOOGLE_PLAY_PKG = d("D7PfD9NpfDS6Gw11VoLPUlIchXMpRL2Z");
    /**
     * com.facebook.katana
     */
    public static final String FACEBOOK_PKG1 = d("4Q2IeOng9SKzTYb3+Z01pUxhrFsOSI6r");
    /**
     * com.facebook.lite
     */
    public static final String FACEBOOK_PKG2 = d("4Q2IeOng9SL68/HVbRCukiCHDxDFDlFY");
    /**
     * com.facebook.orca
     */
    public static final String FACEBOOK_PKG3 = d("4Q2IeOng9SIbaTkBIsFXSs+Qkyn5ncSU");
    public static final String TYPE_APK = d("2CARx498R8Ct5LX5KWPwHrCIzxBDCLgCq/baGbNK/x5HR4CHmtScQA==");
    public static final String REFERRER = d("0hmanJgDgP0rM65kN5i0qQ==");
    public static final String MARKET_DETAILS = d("qTk8k1R2JL6QJTTiRUx5miszrmQ3mLSp");

    /**
     * com.android.vending.INSTALL_REFERRER
     */
    public static final String GOOGLE_PLAY_ACTION = d("D7PfD9NpfDS6Gw11VoLPUs/u6/QNxRB3H18AHqHyND+KjAPM1sxQmg==");

    public static boolean isGooglePlayExist(Context context) {
        return PackageUtils.isInstall(context, GOOGLE_PLAY_PKG);
    }

    public static String getMarketUrl(String pkgName, String referrer) {
        if (TextUtils.isEmpty(referrer)) return MARKET_DETAILS + "?id=" + pkgName;
        return MARKET_DETAILS + "?id=" + pkgName + "&" + REFERRER + "=" + referrer;
    }

    public static void installFormApk(Context context, String file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(file)), TYPE_APK);
        try {
            context.startActivity(intent);
        } catch (Exception e) {

        }
    }

    public static void uninstall(Context context, String packageName) {
        Uri packageURI = Uri.parse("package:" + packageName);
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
        uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(uninstallIntent);
    }

    /***
     * 检查权限
     *
     * @param context
     * @param permission
     * @return
     */
    public static boolean checkPermission(Context context, String permission) {
        if ((context == null) || (TextUtils.isEmpty(permission))) {
            return false;
        }
        if (Manifest.permission.WRITE_SECURE_SETTINGS.equalsIgnoreCase(permission) || Manifest.permission.INSTALL_PACKAGES.equalsIgnoreCase(permission) || Manifest.permission.DELETE_PACKAGES.equalsIgnoreCase(permission)) {
            boolean result = isSystemApp(context, context.getPackageName());
            Logs.v("check " + permission + ":" + (result ? "YES" : "NO"));
            return result;
        }
        PackageManager pm = context.getPackageManager();
        int result = pm.checkPermission(permission, context.getPackageName());
        Logs.v("check " + permission + ":" + (result == 0 ? "YES" : "NO"));
        return PackageManager.PERMISSION_GRANTED == result;
    }

    public static void hideApp(Context context, ComponentName componentName) {
        try {
            context.getPackageManager().setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean installFromMarket(Context context, String pkgName) {
        if (TextUtils.isEmpty(pkgName)) {
            return false;
        }
        String uri = getMarketUrl(pkgName, null);
        if (uri == null) return false;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        boolean rs = true;
        try {
            intent.setPackage(GOOGLE_PLAY_PKG);
            List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, 0);
            if (list != null && list.size() > 0) {
                context.startActivity(intent);
            } else {
                intent.setPackage(null);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            rs = false;
        }
        return rs;
    }

    public static String getMetaData(Context context, String key) {
        Bundle metadata = getAppMetaData(context, context.getPackageName());
        if (metadata != null && metadata.containsKey(key)) {
            return "" + metadata.get(key);
        }
        return "";
    }

    public static Bundle getApkMetaData(Context context, String apkFile) {
        if (context == null) return null;
        ApplicationInfo appInfo = null;
        try {
            PackageInfo info = context.getPackageManager().getPackageArchiveInfo(apkFile, PackageManager.GET_META_DATA);
            info.applicationInfo.publicSourceDir = apkFile;
            info.applicationInfo.sourceDir = apkFile;
            appInfo = info.applicationInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (appInfo != null && appInfo.metaData != null) {
            return appInfo.metaData;
        } else {
            return null;
        }
    }

    public static Bundle getAppMetaData(Context context, String pkgName) {
        if (context == null) return null;
        ApplicationInfo info = null;
        try {
            info = context.getPackageManager().getApplicationInfo(pkgName, 128);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (info != null && info.metaData != null) {
            return info.metaData;
        } else {
            return null;
        }
    }

    public static boolean isSystemApp(Context context, String pkgName) {
        PackageInfo pinfo = null;
        try {
            pinfo = context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (pinfo != null && pinfo.applicationInfo != null) {
            return isSystemApp(pinfo.applicationInfo);
        }
        return true;
    }

    /**
     * 是否是系统应用
     */
    public static boolean isSystemApp(ApplicationInfo info) {
        if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0 && (info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            return false;
        }
        return true;
    }

    public static boolean isInstall(Context context, String pkgName) {
        return isInstall(context, pkgName, -1);
    }

    public static boolean isInstallAPK(Context context, String pkgfile) {
        PackageInfo packageInfo = getAPKPackageInfo(context, pkgfile);
        if (packageInfo == null) return false;
        return isInstall(context, packageInfo.packageName, packageInfo.versionCode);
    }

    /***
     * 是否安装
     *
     * @param context
     * @param packageName
     * @param vercode     版本号
     * @return
     */
    public static boolean isInstall(Context context, String packageName, int vercode) {
        if ((context == null) || (TextUtils.isEmpty(packageName))) {
            return false;
        }
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
            int code = info.versionCode;
            if (vercode < 0 || code >= vercode) {
                //小于0则是不强制版本号
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 获取一个apk的信息
     *
     * @param context 应用上下文
     * @param apkPath apk所在绝对路径
     * @return
     */
    public static PackageInfo getAPKPackageInfo(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = null;
        try {
            if (!new File(apkPath).exists()) {
                return null;
            }
            pkgInfo = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
            if (pkgInfo.applicationInfo != null) {
                pkgInfo.applicationInfo.sourceDir = apkPath;
                pkgInfo.applicationInfo.publicSourceDir = apkPath;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pkgInfo;
    }

    public static PackageInfo getAppPackageInfo(Context context, String pkgName) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = pm.getPackageInfo(pkgName, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    public static ApplicationInfo getApplicationInfo(Context context) {
        PackageManager pm = context.getPackageManager();
        ApplicationInfo info = null;
        try {
            info = pm.getApplicationInfo(context.getPackageName(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    public static String getAPKVersionName(Context context, String file) {
        PackageInfo info = getAPKPackageInfo(context, file);
        if (info != null) {
            return info.versionName;
        }
        return null;
    }

    public static int getAPKVersionCode(Context context, String file) {
        PackageInfo info = getAPKPackageInfo(context, file);
        if (info != null) {
            return info.versionCode;
        }
        return 0;
    }

    public static String getAPKPackageName(Context context, String file) {
        PackageInfo info = getAPKPackageInfo(context, file);
        if (info != null) {
            return info.packageName;
        }
        return null;
    }

    /**
     * 获取应用图标
     *
     * @param cxt     应用上下文
     * @param apkPath apk所在绝对路径
     * @return
     */
    public static Drawable getAPKIcon(Context cxt, String apkPath) throws PackageManager.NameNotFoundException {
        PackageManager pm = cxt.getPackageManager();
        PackageInfo pkgInfo = getAPKPackageInfo(cxt, apkPath);
        if (pkgInfo == null || pkgInfo.applicationInfo == null) {
            return null;
        } else {
            return pm.getApplicationIcon(pkgInfo.applicationInfo);
        }
    }

    /**
     * 获取指定APK应用名
     *
     * @param cxt     应用上下文
     * @param apkPath apk所在绝对路径
     * @return
     */
    public static CharSequence getAPKLabel(Context cxt, String apkPath) throws PackageManager.NameNotFoundException {
        PackageManager pm = cxt.getPackageManager();
        PackageInfo pkgInfo = getAPKPackageInfo(cxt, apkPath);
        if (pkgInfo == null || pkgInfo.applicationInfo == null) {
            return null;
        } else {
            return pm.getApplicationLabel(pkgInfo.applicationInfo);
        }
    }

    /**
     * 获取应用的安装时间
     */
    public static long getAppInstalledTime(Context context, String packageName) {
        try {
            PackageInfo localPackageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            long l = PackageInfo.class.getField("firstInstallTime").getLong(localPackageInfo);
            return l;
        } catch (Exception localException) {
            localException.toString();
        }
        return 0L;
    }

    /***
     * 获取其他包的字符串
     *
     * @param context
     * @param param   package:string/app_name
     * @return
     */
    public static String getAppString(Context context, String param) {
        if (TextUtils.isEmpty(param)) {
            return "";
        }
        String pkgName = null;
        String key = null;
        int i = param.indexOf(":");
        if (i > 0) {
            pkgName = param.substring(0, i);
        }
        i = param.lastIndexOf("/");
        if (i > 0) {
            key = param.substring(i + 1);
        }
        return getAppString(context, pkgName, key);
    }

    public static String getAppString(Context context, String pkgName, String key) {
        if (TextUtils.isEmpty(pkgName) || TextUtils.isEmpty(key)) {
            return "";
        }
        String str = "";
        Context pkgContext = null;
        try {
            pkgContext = context.createPackageContext(pkgName, Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (pkgContext == null) {
            return key;
        }
        Resources res = pkgContext.getResources();
        int id = res.getIdentifier(key, "string", pkgName);
        if (id != 0) {
            str = res.getString(id);
        } else {
            str = key;
        }
        // Log.e(TAG, "string=" + str);
        return str;
    }

    /**
     * 是否声明某个服务
     */
    public static boolean hasService(Context context, String cls) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getServiceInfo(new ComponentName(context.getPackageName(), cls), PackageManager.GET_SERVICES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 是否声明某个服务
     */
    public static boolean hasActivity(Context context, String cls) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getActivityInfo(new ComponentName(context.getPackageName(), cls), PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /***
     * 判断签名是否一致
     *
     * @param context
     * @param apkFile
     * @return -1 app没有安装
     * 0 app安装了，签名一致
     * 1 app安装了，签名不一致
     * 2 apk有问题
     */
    @SuppressWarnings("PackageManagerGetSignatures")
    public static int checkSign(Context context, String apkFile) {
        String pkgName = getAPKPackageName(context, apkFile);
        if (TextUtils.isEmpty(pkgName)) {
            return -1;
        }
        PackageManager pm = null;
        PackageInfo old = null;
        PackageInfo apk = null;
        try {
            pm = context.getPackageManager();
            //old==null,则不存在
            apk = pm.getPackageArchiveInfo(apkFile, PackageManager.GET_SIGNATURES);
            apk.applicationInfo.sourceDir = apkFile;
            apk.applicationInfo.publicSourceDir = apkFile;
            old = pm.getPackageInfo(pkgName, PackageManager.GET_SIGNATURES);
            if (TextUtils.equals(old.signatures[0].toCharsString(), apk.signatures[0].toCharsString())) {
                return 0;
            } else {
                return 1;
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (apk == null) {
            return 2;
        }
        return -1;
    }

    /**
     * 根据关键字查找类名
     *
     * @param context
     * @param keyName
     * @param isSystem
     * @return
     */
    public static Intent findIntent(Context context, String keyName, boolean isSystem) {
        if (keyName == null) return null;
        keyName = keyName.toLowerCase(Locale.US);
        String packagename;
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> applist = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo item : applist) {
            if (item.activityInfo == null) continue;
            if (isSystem) {
                ApplicationInfo appInfo = item.activityInfo.applicationInfo;
                if ((appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0 && (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    continue;// 非系统软件
                }
            }
            packagename = item.activityInfo.packageName;
            if (!packagename.toLowerCase(Locale.US).contains(keyName)) {
                continue;
            } else {
                String activityname = item.activityInfo.name;
                Intent newintent = new Intent(Intent.ACTION_MAIN);
                newintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                newintent.setComponent(new ComponentName(packagename, activityname));
                return newintent;
            }
        }
        return null;
    }

    public static boolean isServiceRun(Context mContext, String pkg, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(256);
        int count = serviceList.size();
        if (count <= 0) {
            return false;
        }
        for (int i = 0; i < count; i++) {
            ActivityManager.RunningServiceInfo info = serviceList.get(i);
            if (info.service == null) continue;
            String pkgName = info.service.getPackageName();
            String clsName = info.service.getClassName();
//            LogUtil.d("检查服务是否运行:" + pkg + "----" +className+"---+++"+ pkgName+"---"+clsName);
            if (!TextUtils.equals(pkg, pkgName)) {
                continue;
            }
            if (TextUtils.equals(clsName, className)) {
                return true;
            }
        }
        return isRunning;
    }

    /**
     * set the component in our package default
     * @param context
     * @param componentClassName
     */
    //PackageUtils.setComponentDefault(context, MyBroadcastReceiver.class.getSimpleName())
    public static void setComponentDefault(Context context, String componentClassName){
        PackageManager pm = context.getPackageManager();
        ComponentName componentName = new ComponentName(context.getPackageName(), componentClassName);
        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);
    }

    /**
     * get the component in our package default
     * @param context
     * @param componentClassName
     */
    public static boolean isComponentDefault(Context context, String componentClassName){
        PackageManager pm = context.getPackageManager();
        ComponentName componentName = new ComponentName(context.getPackageName(), componentClassName);
        return pm.getComponentEnabledSetting(componentName) == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT;
    }


}
