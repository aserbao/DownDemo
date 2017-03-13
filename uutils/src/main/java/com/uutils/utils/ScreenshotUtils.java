package com.uutils.utils;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;


public class ScreenshotUtils {
    public static Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        return view.getDrawingCache();
    }

    public static boolean saveBitmap(Bitmap bm, String file, int quality) {
        if (file == null || bm == null) return false;
        FileOutputStream out = null;
        file = file.toLowerCase(Locale.US);
        try {
            File f = new File(file);
            if (f.exists()) {
                f.delete();
            } else {
                File dir = f.getParentFile();
                if (!dir.exists()) {
                    dir.mkdirs();
                }
            }
            out = new FileOutputStream(f);
            if (file.endsWith(".jpg") || file.endsWith(".jpeg"))
                bm.compress(Bitmap.CompressFormat.JPEG, quality, out);
            else bm.compress(Bitmap.CompressFormat.PNG, quality, out);
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public static Bitmap screenshot(Activity activity, boolean hasstatubar) {
        if (activity == null) {
            return null;
        }
        Window window = activity.getWindow();
        if (window == null) {
            return null;
        }
        return screenshot(window.getDecorView(), hasstatubar);
    }

    public static Bitmap screenshot(View view, boolean hasstatubar) {
        if (view == null) {
            return null;
        }
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap cachebmp = view.getDrawingCache();
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeights = rect.top;
        DisplayMetrics m = view.getResources().getDisplayMetrics();
        // 获取屏幕宽和高
        int widths = m.widthPixels;
        int heights = m.heightPixels;
        Bitmap bmp = null;
        if (cachebmp != null) {
            if (hasstatubar) {
                bmp = Bitmap.createBitmap(cachebmp, 0, 0, widths, heights);
            } else {
                bmp = Bitmap.createBitmap(cachebmp, 0, statusBarHeights, widths, heights - statusBarHeights);
            }
        }
        view.destroyDrawingCache();
        return bmp;
    }
}