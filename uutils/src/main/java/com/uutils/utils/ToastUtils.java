package com.uutils.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * Toast统一管理类
 *
 * @author way
 */
public class ToastUtils {
    // Toast
    private volatile static Toast toast;

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, CharSequence message) {
        showMessage(context, message, Toast.LENGTH_SHORT);
    }

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, int message) {
        showMessage(context, message, Toast.LENGTH_SHORT);
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, CharSequence message) {
        showMessage(context, message, Toast.LENGTH_LONG);
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, int message) {
        showMessage(context, message, Toast.LENGTH_LONG);
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, CharSequence message, int duration) {
        showMessage(context, message, duration);
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, int message, int duration) {
        showMessage(context, message, duration);
    }

    private static Handler sHandler = null;

    private static void showMessage(
            final Context context, final int message, final int duration) {
        showMessage(context, context.getString(message), duration);
    }

    private static void showMessage(
            final Context context, final CharSequence message, final int duration) {
        if (sHandler == null) {
            sHandler = new Handler(context.getApplicationContext().getMainLooper());
        }
        if (Looper.myLooper() != context.getMainLooper()) {
            //不是主线程
            sHandler.post(new Runnable() {
                @Override
                public void run() {
                    make(context, message, duration);
                }
            });
        } else {
            make(context, message, duration);
        }
    }

    private static void make(Context context, CharSequence message, int duration) {
        if (null == toast) {
            toast = Toast.makeText(context, message, duration);
            // toast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            toast.setText(message);
        }
        toast.show();
    }

    /** Hide the toast, if any. */
    public static void hideToast() {
        if (null == toast) return;
        if (Looper.myLooper() != Looper.getMainLooper()) {
            //不是主线程
            sHandler.post(new Runnable() {
                @Override
                public void run() {
                    toast.cancel();
                }
            });
        } else {
            toast.cancel();
        }
    }
}
