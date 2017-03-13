package com.uutils.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import java.util.List;

/**
 * Created by Administrator on 2015/12/17.
 */
public class ProcessUtils {
    /** 获取当前进程包名 */
    public static String getFirstRunningPackageName(Context context) {
        ActivityManager amg = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        String pkgName = null;
        try {
            if (Build.VERSION.SDK_INT > 20) {
                List<ActivityManager.RunningAppProcessInfo> processInfos = amg
                        .getRunningAppProcesses();
                if (processInfos == null || processInfos.size() == 0)
                    return null;
                ActivityManager.RunningAppProcessInfo info = processInfos.get(0);
                // int lenght = Arrays.asList(info.pkgList)
                //        .toString().length();
                pkgName = info.pkgList[0];
            } else {
                List<ActivityManager.RunningTaskInfo> runningTaskInfos = amg.getRunningTasks(1);
                if (runningTaskInfos == null || runningTaskInfos.size() == 0)
                    return null;
                pkgName = runningTaskInfos.get(0).baseActivity.getPackageName();
            }
        } catch (Exception e) {

        }
        return pkgName;
    }

    /**
     * 根据pid获取进程名
     */
    public static String getProcessName(Context context, int pid) {
        if (context == null) {
            return null;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(256);
        int count = serviceList.size();
        if (count <= 0) {
            return false;
        }
        for (int i = 0; i < count; i++) {
            ActivityManager.RunningServiceInfo info = serviceList.get(i);
            if (info.service == null) continue;
            String pkgName = info.service.getPackageName();
            String clsName = info.service.getClassName();
            if (!TextUtils.equals(mContext.getPackageName(), pkgName)) {
                continue;
            }
            if (TextUtils.equals(clsName, className)) {
                return true;
            }
        }
        return isRunning;
    }
}
