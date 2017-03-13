package com.uutils.plugin;

import android.content.Context;

import com.uutils.utils.PackageUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/24.
 */
public class Analytics {
    public static void init(Context context, boolean log) {
        String channel = PackageUtils.getMetaData(context, "UMENG_CHANNEL");
        String tdid = PackageUtils.getMetaData(context, "TD_APP_ID");
        TCAgentEx.init(context, tdid, channel);
        TCAgentEx.setLog(log);
        MobclickAgentEx.setDebugMode(log);
        if ("googleplay".equals(channel)) {
            MobclickAgentEx.setCheckDevice(false);
        }
    }

    public static void onPause(Context context) {
        onPause(context, context.getClass().getName());
    }

    public static void onResume(Context context) {
        onResume(context, context.getClass().getName());
    }

    public static void onPause(Context context, String name) {
        TCAgentEx.onPageStart(context, name);
        MobclickAgentEx.onPause(context, name);
    }

    public static void onResume(Context context, String name) {
        TCAgentEx.onPageEnd(context, name);
        MobclickAgentEx.onResume(context, name);
    }

    public static void onEvent(Context context, String id) {
        TCAgentEx.onEvent(context, id);
        MobclickAgentEx.onEvent(context, id);
    }

    public static void onEvent(Context context, String id, String... args) {
        if (args == null || args.length == 0) {
            onEvent(context, id);
        } else {
            Map<String, String> maps = new HashMap<String, String>();
            for (int i = 0; i < (args.length / 2); i++) {
                String k = args[i * 2];
                String v = args[i * 2 + 1];
                maps.put(k, v);
            }
            onEvent(context, id, maps);
        }
    }

    public static void onEvent(Context context, String id, Map<String, String> maps) {
        MobclickAgentEx.onEvents(context, id, maps);
        TCAgentEx.onEvents(context, id, maps);
    }

    public static void onEvent(Context context, String id, HashMap<String, String> m, int value) {
        m.put("__ct__", String.valueOf(value));
        onEvent(context, id, m);
    }
}
