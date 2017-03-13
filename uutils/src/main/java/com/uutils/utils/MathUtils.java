package com.uutils.utils;

import android.content.Context;

import static android.text.TextUtils.isEmpty;

public class MathUtils {
    public static double str2double(String str) {
        if (isEmpty(str)) return 0d;
        double i = 0d;
        try {
            i = Double.parseDouble(str);
        } catch (NumberFormatException ex) {

        }
        return i;
    }

    public static float px2dp(Context context, float px) {
        float density = context.getResources().getDisplayMetrics().density;
        float dp = px / 2 * density;
        return dp;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static long str2long(String str) {
        if (isEmpty(str)) return 0l;
        long i = 0l;
        try {
            if (str.startsWith("0x")) {
                i = Long.parseLong(str, 16);
            } else {
                i = Long.parseLong(str);
            }
        } catch (NumberFormatException ex) {

        }
        return i;
    }
}
