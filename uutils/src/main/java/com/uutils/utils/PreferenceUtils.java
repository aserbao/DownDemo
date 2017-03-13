package com.uutils.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

@SuppressWarnings("CommitPrefEdits")
public class PreferenceUtils {
    public static String sSharedPreferencesKey = "com.uutils.prefs";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(sSharedPreferencesKey, Context.MODE_MULTI_PROCESS);
    }

    public static String getPrefString(Context context, String key, final String defaultValue) {
        final SharedPreferences settings = getSharedPreferences(context);
        String value = defaultValue;
        try {
            value = settings.getString(key, defaultValue);
        } catch (Exception e) {

        }
        return value;
    }

    public static void setPrefString(Context context, final String key, final String value) {
        final SharedPreferences settings = getSharedPreferences(context);
        try {
            settings.edit().putString(key, value).commit();
        } catch (Exception e) {
        }
    }

    public static boolean getPrefBoolean(Context context, final String key, final boolean defaultValue) {
        final SharedPreferences settings = getSharedPreferences(context);
        boolean b = defaultValue;
        try {
            int i = settings.getInt(key, 0);
            if (i == 1) b = true;
            else if (i == -1) b = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    public static boolean hasKey(Context context, final String key) {
        return getSharedPreferences(context).contains(key);
    }

    public static void setPrefBoolean(Context context, final String key, final boolean value) {
        final SharedPreferences settings = getSharedPreferences(context);
        try {
            if (value) {
                settings.edit().putInt(key, 1).commit();
            } else {
                settings.edit().putInt(key, -1).commit();
            }
        } catch (Exception e) {
        }
    }

    public static void setPrefInt(Context context, final String key, final int value) {
        final SharedPreferences settings = getSharedPreferences(context);
        try {
            settings.edit().putInt(key, value).commit();
        } catch (Exception e) {
        }
    }

    public static int getPrefInt(Context context, final String key, final int defaultValue) {
        final SharedPreferences settings = getSharedPreferences(context);
        int value = defaultValue;
        try {
            value = settings.getInt(key, defaultValue);
        } catch (Exception e) {

        }
        return value;
    }

    public static void setPrefFloat(Context context, final String key, final float value) {
        final SharedPreferences settings = getSharedPreferences(context);
        try {
            settings.edit().putFloat(key, value).commit();
        } catch (Exception e) {
        }
    }

    public static float getPrefFloat(Context context, final String key, final float defaultValue) {
        final SharedPreferences settings = getSharedPreferences(context);
        float value = defaultValue;
        try {
            value = settings.getFloat(key, defaultValue);
        } catch (Exception e) {

        }
        return value;
    }

    public static void setPrefLong(Context context, final String key, final long value) {
        final SharedPreferences settings = getSharedPreferences(context);
        try {
            settings.edit().putLong(key, value).commit();
        } catch (Exception e) {
        }
    }

    public static long getPrefLong(Context context, final String key, final long defaultValue) {
        final SharedPreferences settings = getSharedPreferences(context);
        long value = defaultValue;
        try {
            value = settings.getLong(key, defaultValue);
        } catch (Exception e) {

        }
        return value;
    }

    public static void clearPreference(Context context, final SharedPreferences p) {
        final Editor editor = p.edit();
        editor.clear();
        editor.commit();
    }
}
