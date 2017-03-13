package com.uutils.utils;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

import com.uutils.u;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * log
 */
public class Logs {
    public static String TAG = "test";
    public static boolean sLog = false;
    private static final File logFile = new File(FileUtils.getSDCardPath("logs/" + u.PKGNAME + ".log"));
    // private static final File logFile = new File(FileUtils.getSDCardPath("logs/" + u.PKGNAME + ".txt"));


    public static void v(Object msg) {
        v(TAG, msg);
    }

    public static void d(Object msg) {
        d(TAG, msg);
    }

    public static void i(Object msg) {
        i(TAG, msg);
    }

    public static void w(Object msg) {
        w(TAG, msg);
    }

    public static void e(Object msg) {
        e(TAG, msg);
    }

    public static void v(String tag, Object msg) {
        if (sLog) {
            Log.v(tag, toString(msg));
        }
    }

    public static void d(String tag, Object msg) {
        if (sLog) {
            Log.d(tag, toString(msg));
        }
    }

    public static void i(String tag, Object msg) {
        if (sLog) {
            Log.i(tag, toString(msg));
        }
    }

    public static void w(String tag, Object msg) {
        if (sLog) {
            String err = toString(msg);
            Log.w(tag, err);
            writeFile(u.VersionCode + " " + tag + ":" + err);
        }
    }

    public static void e(String tag, Object msg) {
        if (sLog) {
            String err = toString(msg);
            Log.e(tag, err);
            writeFile(u.VersionCode + " " + tag + ":" + err);
        }
    }

    private static String toString(Object e) {
        if (e instanceof Throwable) {
            StackTraceElement caller = ((Throwable) e).getStackTrace()[2];
            String tag = "%s.%s(L:%d)";
            String callerClazzName = caller.getClassName();
            callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
            return String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber()) + "\n" + getThrowableString((Throwable) e);
        }
        return String.valueOf(e);
    }

    private static String getThrowableString(Throwable ex) {
        String result = null;
        Writer writer = null;
        try {
            writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            Throwable cause = ex.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            printWriter.close();
            result = writer.toString();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            FileUtils.close(writer);
        }
        return result;
    }

    @SuppressLint("SimpleDateFormat")
    private static synchronized void writeFile(String logText) {
        if (!sLog) {
            deleteLogFile();
            return;
        }
        if (Environment.getExternalStorageState().equals("mounted")) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String strDateTime = dateFormat.format(curDate);
            String strLog = strDateTime + " " + logText;
            FileOutputStream outputStream = null;
            try {
                File logFilePath = logFile.getParentFile();
                if (!logFilePath.exists()) {
                    logFilePath.mkdirs();
                }

                if (!logFile.exists()) {
                    logFile.createNewFile();
                }
                outputStream = new FileOutputStream(logFile, true);
                outputStream.write(strLog.getBytes());
                outputStream.write("\n".getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                FileUtils.close(outputStream);
            }
        }
    }

    public static boolean deleteLogFile() {
        if (logFile != null && logFile.exists()) {
            return logFile.delete();
        }
        return true;
    }
}
