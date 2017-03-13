package com.uutils.plugin;

import android.content.Context;

import com.uutils.utils.ReflectUtils;

import java.util.Map;

public class MobclickAgentEx {
    static ReflectUtils Mobclick;

    private static ReflectUtils getUmengReflectUtils() {
        if (Mobclick == null) {
            try {
                Mobclick = ReflectUtils.on("com.umeng.analytics.MobclickAgentNew");
            } catch (Exception e) {
                Mobclick = ReflectUtils.on("com.umeng.analytics.MobclickAgent");
            }
        }
        return Mobclick;
    }

    public static void setDebugMode(boolean debug) {
        try {
            ReflectUtils obj = getUmengReflectUtils();
            if (obj == null) {
                return;
            }
            obj.call("setDebugMode", debug);
        } catch (Exception e) {
        }
    }

    public static void onPause(Context context, String pagename) {
        ReflectUtils obj = null;
        try {
            obj = getUmengReflectUtils();
            obj.call("stopDownload", context, pagename);
        } catch (Exception e) {
            try {
                if (obj != null)
                    obj.call("stopDownload", context);
            } catch (Exception e1) {

            }
        }
    }

    public static void onResume(Context context, String pagename) {
        ReflectUtils obj = null;
        try {
            obj = getUmengReflectUtils();
            obj.call("onResume", context, pagename);
        } catch (Exception e) {
            try {
                if (obj != null)
                    obj.call("onResume", context);
            } catch (Exception e1) {

            }
        }
    }

    public static void onEvent(Context context, String id) {
        ReflectUtils obj = null;
        try {
            obj = getUmengReflectUtils();
            obj.call("onEvent", context, id, id);
        } catch (Exception e) {
            try {
                if (obj != null)
                    obj.call("onEvent", context, id);
            } catch (Exception e1) {
            }
        }
    }

    /***
     * mac,在gp商店，建议关闭
     * @param b
     */
    public static void setCheckDevice(boolean b) {
        try {
            ReflectUtils obj = getUmengReflectUtils();
            obj.call("setCheckDevice", b);
        } catch (Exception e) {
        }
    }

    public static void setAppKey(Context context, String key) {
        ReflectUtils obj = null;
        try {
            obj = ReflectUtils.on("com.umeng.analytics.AnalyticsConfig");
            obj.call("setAppKey", context, context, key);
            obj = ReflectUtils.on("com.umeng.analytics.social.e");
            obj.set("d", key);
        } catch (Exception e) {

        }
    }

    public static void setChannel(String channel) {
        ReflectUtils obj = null;
        try {
            obj = ReflectUtils.on("com.umeng.analytics.AnalyticsConfig");
            obj.call("setChannel", channel);
        } catch (Exception e) {

        }
    }

    public static void onEvents(Context context, String id, Map<String, String> args) {
        ReflectUtils obj = null;
        try {
            obj = getUmengReflectUtils();
            obj.call("onEvent", context, id, id, args);
        } catch (Exception e) {
            try {
                if (obj != null)
                    obj.call("onEvent", context, id, args);
            } catch (Exception e1) {

            }
        }
    }
}
