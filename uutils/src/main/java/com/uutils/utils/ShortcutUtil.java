package com.uutils.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.res.Resources;
import android.graphics.Bitmap;

public class ShortcutUtil {

    public static final String ACTION_ADD_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
    public static final String ACTION_REMOVE_SHORTCUT = "com.android.launcher.action.UNINSTALL_SHORTCUT";

    private static void addShortcut(Context context, Intent shortcutbroast) {
        if (PackageUtils.checkPermission(context, Manifest.permission.INSTALL_SHORTCUT)) {
            context.sendBroadcast(shortcutbroast);
        } else {
            Logs.w("dont't define " + Manifest.permission.INSTALL_SHORTCUT);
        }
    }

    public static void addShortcut(Context context, Intent shortcutIntent,
                                   String shortcutName, Bitmap icon, int iconId) {
        addShortcut(context, getInstallShortInfoIntent(context, shortcutIntent,
                shortcutName, icon, iconId));
    }

    private static void removeShortcut(Context context, Intent shortcutbroast) {
        if (PackageUtils.checkPermission(context, Manifest.permission.UNINSTALL_SHORTCUT)) {
            context.sendBroadcast(shortcutbroast);
        } else {
            Logs.w("dont't define " + Manifest.permission.UNINSTALL_SHORTCUT);
        }
    }

    public static void removeShortcut(Context context, Intent shortcutIntent,
                                      String shortcutName, Bitmap icon, int iconId) {

        removeShortcut(context, getUnInstallShortInfoIntent(context, shortcutIntent,
                shortcutName, icon, iconId));
    }

    private static ShortcutIconResource getIconResource(Context context, int iconId)
            throws Resources.NotFoundException {
        if (iconId == 0) return null;
        return ShortcutIconResource.fromContext(context, iconId);
    }

    public static Intent getUnInstallShortInfoIntent(
            Context context, Intent shortcutIntent,
            String shortcutName, Bitmap icon, int iconId) {
        return getShortInfoIntent(false, context, shortcutIntent, shortcutName, icon,
                getIconResource(context, iconId));
    }

    public static Intent getInstallShortInfoIntent(
            Context context, Intent shortcutIntent,
            String shortcutName, Bitmap icon, int iconId) {
        return getShortInfoIntent(true, context, shortcutIntent, shortcutName, icon,
                getIconResource(context, iconId));
    }

    private static Intent getShortInfoIntent(boolean isAdd, Context context, Intent shortcutIntent,
                                             String shortcutName, Bitmap icon, ShortcutIconResource iconRes) {
        Intent shortcut = new Intent();
        if (isAdd) {
            shortcut.setAction(ACTION_ADD_SHORTCUT);
        } else {
            shortcut.setAction(ACTION_REMOVE_SHORTCUT);
        }
        // 快捷方式的名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutName);
        shortcut.putExtra("duplicate", false); // 不允许重复创建
        //点击intent
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        // 图标1
        if (iconRes != null) {
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
            return shortcut;
        }
        // 快捷方式的图标2
        if (icon != null) {
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);
            return shortcut;
        }
        return shortcut;
    }
}
