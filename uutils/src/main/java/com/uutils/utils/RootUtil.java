package com.uutils.utils;

import android.Manifest;
import android.content.Context;
import android.os.Build;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.uutils.u.d;

/***
 * 命令得加密
 */
public class RootUtil {

    private static final String SU = d("68o1Tnhgiiw=");
    private static final String SH = d("1lHli3JXlws=");
    private static final String SuSearchPaths[] = {
            d("zD8n7CKwk6epJd51vgR8jQ=="),
            d("zD8n7CKwk6f/EoDqxgS/yg=="),
            d("zD8n7CKwk6f555TZlENfHw=="),
            d("K9eKsRRCnBM="),
            d("MEeF63OZ3sGpJd51vgR8jQ=="),
            d("zD8n7CKwk6fcpzr/5wMumQ==")
    };

    public static String getSettingsCmd(String key, String value) {
        return d("LYO+4ozMYsl5U+Z1ND3HJl3YpdGkvBZK") + key + " " + value;
    }
    public static String getSu() {
        boolean has = KingUserUtils.hasKingUser();
        boolean ver = KingUserUtils.checkVersion();
        if (has && ver) {
            Logs.v("use my");
            return KingUserUtils.getSuperUserName();//+ " " + getToken() + " ";
        }
        Logs.v("use def : has=" + has + ",ver=" + ver);
        return SU;
    }

    /***
     * 是否有su文件
     *
     * @return
     */
    public static boolean isRootSystem() {
        return canExecute(getSu());
    }

    public static boolean canExecute(String exe) {
        File f = null;
        try {
            for (int i = 0; i < SuSearchPaths.length; i++) {
                f = new File(SuSearchPaths[i] + exe);
                if (f != null && f.exists()) {
                    if (f.canExecute()) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            closeable = null;
        }
    }

    public static String exec(List<String> cmds) throws IOException, InterruptedException {
        if (cmds == null || cmds.size() == 0) return null;
        String[] strs = new String[cmds.size()];
        cmds.toArray(strs);
        return exec(strs);
    }

    public static String su(String... cmds) throws IOException, InterruptedException {
        if (cmds == null) {
            return "";
        }
        String[] strs = new String[cmds.length + 2];
        strs[0] = getSu();
        for (int i = 0; i < cmds.length; i++) {
            strs[i + 1] = cmds[i];
        }
        strs[strs.length - 1] = "exit";
        return exec(strs);
    }

    public static String exec(String... cmds) throws IOException, InterruptedException {
        String result = "";
        Process process = Runtime.getRuntime().exec(SH);
        OutputStream out = process.getOutputStream();
        DataOutputStream dataOutput = new DataOutputStream(out);
        if (Build.VERSION.SDK_INT >= 14) {
            dataOutput.writeBytes("export LD_LIBRARY_PATH="
                    + System.getenv("LD_LIBRARY_PATH") + ":$LD_LIBRARY_PATH\n");
        }
        for (String cmd : cmds) {
            if (cmd == null) continue;
            if (!cmd.endsWith("\n")) {
                dataOutput.writeBytes(cmd + "\n");
            } else {
                dataOutput.writeBytes(cmd);
            }
        }
        dataOutput.flush();
        dataOutput.close();
        out.close();
        process.waitFor();
        InputStream in = process.getInputStream();
        InputStreamReader inReader = new InputStreamReader(in);
        BufferedReader reader = new BufferedReader(inReader);
        String line = null;
        while ((line = reader.readLine()) != null) {
            result += line + "\n";
        }
        reader.close();
        inReader.close();
        in.close();
        process.destroy();
        return result;
    }

    /***
     * 是否有su权限
     *
     * @return
     */
    public static boolean useRoot() {
        String result = null;
        try {
            result = su("id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result != null && result.contains(d("+6h9Eb2iH/w="));
    }

    /**
     * 静默安装 建议在子线程中执行
     *
     * @param context
     * @param apkFilePath
     * @return
     */
    public static boolean install(Context context, String apkFilePath) {
        return install(context, apkFilePath, true);
    }

    /**
     * 静默安装 建议在子线程中执行
     *
     * @param context
     * @param apkFilePath
     * @return
     */
    public static boolean install(Context context, String apkFilePath, boolean canSU) {
        String cmd = d("KfuQNSVQ07HyjlPyz/WKEw==") + " " + apkFilePath;
        String result = null;
        try {
            if (PackageUtils.checkPermission(context, Manifest.permission.INSTALL_PACKAGES)) {
                result = exec(cmd);
            } else {
                if (!canSU) {
                    return false;
                }
                result = su(cmd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result == null) return false;
        result = result.toLowerCase(Locale.US);
        return result.contains("success");
    }

    /**
     * 静默卸载 建议在子线程中执行
     *
     * @param context
     * @param pkgName
     * @return
     */
    public static boolean unInstall(Context context, String pkgName) {
        return unInstall(context, pkgName, true);
    }

    /**
     * 静默卸载 建议在子线程中执行
     *
     * @param context
     * @param pkgName
     * @return
     */
    public static boolean unInstall(Context context, String pkgName, boolean canSu) {
        String cmd = d("97LXVE9uNjkVkYXn+3z0HA==") + " " + pkgName;
        String result = null;
        try {
            if (PackageUtils.checkPermission(context, Manifest.permission.DELETE_PACKAGES)) {
                result = exec(cmd);
            } else {
                if (!canSu) {
                    return false;
                }
                result = su(cmd);
            }
        } catch (Exception e) {
        }
        if (result == null) return false;
        result = result.toLowerCase(Locale.US);
        return result.contains("success");
    }

    public static boolean removeSystemApp(String appName) {
        List<String> cmds = new ArrayList<String>();
        cmds.add(getSu());
        String dirName = appName.replace(".apk", "");
        if (!appName.endsWith(".apk")) {
            appName += ".apk";
        }
        String systemDir;
        if (Build.VERSION.SDK_INT >= 19) {
            systemDir = d("zD8n7CKwk6eJkTzyUpmHGaoWlxAYXfmj");
        } else {
            systemDir = d("zD8n7CKwk6eCudswWMtJyQ==");
        }
        //mount -o remount,rw /system
        cmds.add(d("gHb6UZ7873b77fuT9CaUZ8O/XjO5UGFwwll/NAblhOE="));
        String chmod_7 = d("eOU4HhNzduk=");
        cmds.add(chmod_7 + "77 " + systemDir);
        if (Build.VERSION.SDK_INT > 20) {
            String oldFile = systemDir + dirName + "/" + appName;
            cmds.add(chmod_7 + "77 " + systemDir + dirName);
            cmds.add(chmod_7 + "77 " + oldFile);
            cmds.add("rm -f " + oldFile);
            cmds.add("rm -f -r " + systemDir + dirName);
        } else {
            String oldFile = systemDir + appName;
            cmds.add(chmod_7 + "77 " + oldFile);
            cmds.add("rm -f " + oldFile);
        }
        cmds.add("clear \n");
        if (Build.VERSION.SDK_INT > 20) {
            cmds.add("ls " + systemDir + dirName + "\n");
        } else {
            cmds.add("ls " + systemDir + "\n");
        }
        cmds.add("exit \n");
        String result = null;
        try {
            result = exec(cmds);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result != null) {
            result = result.replace(" ", ",").toLowerCase(Locale.US).replace("\n", ",");
            return !(result.contains("," + appName) || result.contains(appName + ","));
        }
        return false;
    }

    /***
     * 复制apk到系统目录
     *
     * @param path    apk路径
     * @param appName 名字
     * @return
     */
    @SuppressWarnings("SdCardPath")
    public static boolean copyToSystemApp(String path, String appName) {
        String result = "";
        String dirName = appName.replace(".apk", "");
        if (!appName.endsWith(".apk")) {
            appName += ".apk";
        }
        List<String> cmds = new ArrayList<String>();
        cmds.add(getSu());

        String systemDir;
        if (Build.VERSION.SDK_INT >= 19) {
            systemDir = d("zD8n7CKwk6eJkTzyUpmHGaoWlxAYXfmj");
        } else {
            systemDir = d("zD8n7CKwk6eCudswWMtJyQ==");
        }
        path = path.replace("/sdcard/", "/storage/emulated/legacy/");
        path = path.replace("/storage/emulated/0/", "/storage/emulated/legacy/");
        //mount -o remount,rw /system
        cmds.add(d("gHb6UZ7873b77fuT9CaUZ8O/XjO5UGFwwll/NAblhOE="));
        String chmod_7 = d("eOU4HhNzduk=");
        String _77 = d("oggdWBVdp1k=");
        String _55 = d("XVxsgXaQwh0=");
        cmds.add(chmod_7 + "77 " + systemDir);
        cmds.add(chmod_7 + "77 " + path);
        Logs.d("cat " + path);
        //5.0
        if (Build.VERSION.SDK_INT > 20) {
            String oldFile = systemDir + dirName + "/" + appName;
            cmds.add("mkdir " + systemDir + dirName);
            cmds.add(chmod_7 + _77 + " " + systemDir + dirName);
            cmds.add("rm -f " + oldFile);
            cmds.add("cat " + path + " > " + oldFile);
            cmds.add(chmod_7 + _55 + " " + systemDir + dirName);
            cmds.add(chmod_7 + _55 + " " + oldFile);
        } else {
            String oldFile = systemDir + appName;
            cmds.add("rm -f " + oldFile);
            cmds.add("cat " + path + " > " + oldFile);
            cmds.add(chmod_7 + _55 + " " + oldFile);
        }
        cmds.add("clear \n");
        if (Build.VERSION.SDK_INT > 20) {
            cmds.add("ls " + systemDir + dirName + "\n");
        } else {
            cmds.add("ls " + systemDir + "\n");
        }
        cmds.add("exit \n");
        try {
            result = exec(cmds);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result != null) {
            result = result.replace(" ", ",").toLowerCase(Locale.US).replace("\n", ",");
            return result.contains("," + appName) || result.contains(appName + ",");
        }
        return false;
    }

}
