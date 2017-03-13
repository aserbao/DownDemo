package com.uutils.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import static com.uutils.u.d;

public class KingUserUtils {
    private static final String PKGNAME = d("n3YbI4EoxWDXUia7XyyZ7sBopOnhlWNV");
    private static final String SUPERUSER = d("LWt8AJEdvS0=");
    private static final String SYSTEM_BIN = d("zD8n7CKwk6fcpzr/5wMumQ==");// "/system/bin/";

    static Context context;

    /*** kinguser的最低版本 */
    static final int MIN_CODE = 7890;

    public static void init(Context mContext) {
        context = mContext.getApplicationContext();
    }

    public static String getSuperUserName() {
        return SYSTEM_BIN + SUPERUSER;
    }

    /***
     * 判断{@link #SUPERUSER}是否存在
     *
     * @return
     */
    public static boolean hasKingUser() {
        //拥有mysu，版本号
        return RootUtil.canExecute(SUPERUSER);
    }

    /**
     * 通过{@link #PKGNAME}，检查kinguser的版本
     *
     * @return 是我们的返回true
     */
    public static boolean checkVersion() {
        if (context == null) {
            return false;
        }
        PackageManager pManager = context.getPackageManager();
        try {
            PackageInfo pInfo = pManager.getPackageInfo(PKGNAME, 0);
            int code = pInfo.versionCode;
            Logs.e("ku code=" + code);
            return code >= MIN_CODE;
        } catch (Exception e) {

        }
        return false;
    }
}
