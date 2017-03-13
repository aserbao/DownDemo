package com.uutils.net;

public interface DownloadListener {
    void onStart(String url, String file);

    /**
     * @param p 0-1
     */
    void onProgress(String url, String file, float p);

    void onFinish(String url, String file, DownloadError err);
}