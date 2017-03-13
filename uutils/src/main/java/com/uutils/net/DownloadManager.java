package com.uutils.net;

import android.content.Context;
import android.text.TextUtils;

import com.uutils.crypto.MD5Utils;
import com.uutils.utils.FileUtils;
import com.uutils.utils.Logs;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadManager {
    ExecutorService mPool;
    private static int MAX_POOL = 8;
    static DownloadManager sDownloadManager = new DownloadManager(MAX_POOL);
    final HashMap<String, DownloadThread> sStatus = new HashMap<>();

    private DownloadManager(int num) {
        mPool = Executors.newFixedThreadPool(Math.min(num, MAX_POOL));
    }

    public static DownloadManager getInstance(Context context) {
        return sDownloadManager;
    }

    public DownloadThread getDownloadThread(String filepath) {
        if (filepath == null) return null;
        return sStatus.get(filepath);
    }

    public void stopDownload(String filepath) {
        if (filepath == null) return;
        DownloadThread thread = sStatus.get(filepath);
        if (thread != null && !thread.isInterrupted()) {
            thread.interrupt();
        }
        sStatus.remove(filepath);
    }

    public boolean download(String url, String file, String md5, DownloadListener listener) {
        if (url == null || file == null) return false;
        // 文件存在，md5一致
        if (!TextUtils.isEmpty(md5) && FileUtils.exists(file)) {
            String _md5 = MD5Utils.getFileMD5(file);
            if (md5.equalsIgnoreCase(_md5)) {
                Logs.d("same md5");
                if (listener != null) {
                    listener.onFinish(url, file, DownloadError.ERR_NONE_SAME_MD5);
                }
                return true;
            } else {
                Logs.e("err:" + md5 + "==" + md5 + ",file=" + file);
            }
        }
        if (sStatus.get(file) != null) {
            Logs.d("ding " + file);
            return false;
        }
        DownloadThread thread = new DownloadThread(url, file, new MultiDownloadListener(listener));
        sStatus.put(file, thread);
        mPool.execute(thread);
        return true;
    }

    public void close() {
        mPool.shutdown();
    }


    class MultiDownloadListener implements DownloadListener {
        DownloadListener parent;

        public MultiDownloadListener(DownloadListener parent) {
            this.parent = parent;
        }

        @Override
        public void onFinish(String url, String file, DownloadError err) {
            if (parent != null) {
                parent.onFinish(url, file, err);
            }
            sStatus.remove(file);
        }

        @Override
        public void onStart(String url, String file) {
            if (parent != null) {
                parent.onStart(url, file);
            }
        }

        @Override
        public void onProgress(String url, String file, float p) {
            if (parent != null) {
                parent.onProgress(url, file, p);
            }
        }
    }
}
