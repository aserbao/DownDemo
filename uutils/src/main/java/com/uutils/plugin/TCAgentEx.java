package com.uutils.plugin;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.uutils.utils.ReflectUtils;

import java.util.Map;

public class TCAgentEx {
    static ReflectUtils TCAgent;

    static ReflectUtils getTCAgent() {
        if (TCAgent == null) {
            TCAgent = ReflectUtils.on("com.tendcloud.tenddata.TCAgent");
        }
        return TCAgent;
    }

    public static void init(Context context, String key, String channel) {
        try {
            ReflectUtils tcagent = getTCAgent();
            if (TextUtils.isEmpty(channel)) {
                tcagent.call("init", context);
            } else {
                tcagent.call("init", context.getApplicationContext(), key, channel);
                if (key != null && key.length() > 5) {
                    Log.d("TDLog",
                            "tdid=" + key.substring(key.length() - 5) + ",channel=" + channel);
                }
            }

        } catch (Exception e) {
        }
    }

    public static void setLog(boolean log) {
        try {
            ReflectUtils tcagent = getTCAgent();
            tcagent.set("LOG_ON", log);
        } catch (Exception e) {

        }
    }

    public static String getDeviceId(Context context) {
        String id = null;
        try {
            id = getTCAgent().call("getDeviceId", context).get();
        } catch (Exception e) {
        }
        return id;
    }

    public static void onResume(Activity context) {
        try {
            getTCAgent().call("onResume", context);
        } catch (Exception e) {
        }
    }

    public static void onPause(Activity context) {
        try {
            getTCAgent().call("onResume", context);
        } catch (Exception e) {
        }
    }

    public static void onPageStart(Context context, String paramString) {
        try {
            getTCAgent().call("onPageStart", context, paramString);
        } catch (Exception e) {
        }
    }

    public static void onPageEnd(Context context, String paramString) {
        try {
            getTCAgent().call("onPageEnd", context, paramString);
        } catch (Exception e) {
        }
    }

    public static void onError(Context context, Throwable paramThrowable) {
        try {
            getTCAgent().call("onError", context, paramThrowable);
        } catch (Exception e) {
        }
    }

    public static void onEvent(Context paramContext, String paramString) {
        onEvent(paramContext, paramString, paramString);
    }

    public static void onEvent(Context paramContext, String paramString1, String paramString2) {
        try {
            getTCAgent().call("onEvent", paramContext, paramString1, paramString2);
        } catch (Exception e) {
        }
    }

    public static void onEvents(Context paramContext, String id, Map<String, String> maps) {
        try {
            getTCAgent().call("onEvent", paramContext, id, id, maps);
        } catch (Exception e) {
        }
    }
}
