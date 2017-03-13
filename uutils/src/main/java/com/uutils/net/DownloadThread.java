package com.uutils.net;

import com.uutils.utils.FileUtils;
import com.uutils.utils.Logs;

import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadThread extends Thread {

    private volatile DownloadListener listener;
    private volatile boolean isDownloading = false;
    private long contentLength = 0;

    private String mUrl;
    private String mFile;
    private File tmpFile;

    public DownloadThread(String url, String file, DownloadListener listener) {
        super(file);
        this.mUrl = url;
        this.mFile = file;
        this.listener = listener;
        tmpFile = new File(mFile + ".tmp");
    }

    public long getContentLength() {
        return contentLength;
    }

    @Override
    public void interrupt() {
        this.isDownloading = false;
        super.interrupt();
    }

    public long getProgress() {
        return tmpFile.length();
    }

    @Override
    public void run() {
        super.run();
        this.isDownloading = true;
        File dir = tmpFile.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 下载前，之前的长度
        long pos = tmpFile.exists() ? tmpFile.length() : 0;
        //需要下载的长度
        long alllength = getContentLength(mUrl);
        Logs.v("http", "tmpfile pos:" + pos + ",file=" + alllength);
        if (alllength > 0) {
            long sdcard = FileUtils.getSDFreeSize();
            if (sdcard < (alllength + 1024 * 1024 * 10)) {
                // 小于10M
                if (listener != null) {
                    listener.onFinish(mUrl, mFile, DownloadError.ERR_FILE);
                }
                return;
            }
        }
        if (pos > 0 && pos == alllength) {
            // tmp的长度和服务器返回的一致
            Logs.v("http", "pos=lenght");
            onfinish(alllength);
        } else {
            download(mUrl, pos, alllength);
        }
    }

    private void download(String uri, long pos, long alllength) {
        HttpURLConnection httpURLConnection = null;
        RandomAccessFile output = null;
        InputStream input = null;
        if (listener != null) {
            listener.onStart(mUrl, mFile);
        }
        // FileLock lock = null;
        // 准备下载
        try {
            // 断点续传测试
            URL url = new URL(uri);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("User-agent", System.getProperty("http.agent"));
            httpURLConnection.setConnectTimeout(60 * 1000);
            httpURLConnection.setReadTimeout(60 * 1000);
            if (pos > 0 && alllength > 0) {
                httpURLConnection.setRequestProperty("Range", "bytes=" + pos + "-" + alllength);
            } else if (pos > 0) {
                httpURLConnection.setRequestProperty("Range", "bytes=" + pos + "-");
            }
            httpURLConnection.setAllowUserInteraction(true);
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_PARTIAL) {
                // 支持断点续传
                input = httpURLConnection.getInputStream();
                Logs.v("http", "can resume");
            } else if (responseCode == HttpURLConnection.HTTP_OK) {
                // 不支持断点续传
                tmpFile.delete();
                if (pos > 0) {
                    Logs.e("http", "can not resume");
                }
                pos = 0;
                input = httpURLConnection.getInputStream();
            } else {
                // 不能下载
            }
            if (input == null) {
                if (listener != null) {
                    listener.onFinish(mUrl, mFile, DownloadError.ERR_404);
                }
            } else {
                if (!tmpFile.exists()) {
                    tmpFile.createNewFile();
                }
                output = new RandomAccessFile(tmpFile, "rws");
                output.seek(pos);
                Logs.v("http", "write pos:" + pos);
                byte[] buffer = new byte[4096];
                int length;
                long compeleteSize = pos;
                while (isDownloading && (length = input.read(buffer)) != -1) {
                    output.write(buffer, 0, length);
                    compeleteSize += length;
                    if (listener != null && alllength > 0) {
                        listener.onProgress(mUrl, mFile, (float) compeleteSize / (float) alllength);
                    }
                }
                if (isDownloading) {
                    onfinish(alllength);
                } else {
                    if (listener != null) {
                        listener.onFinish(mUrl, mFile, DownloadError.ERR_PAUSE);
                    }
                    Logs.d("download stop");
                }
            }
            // }
        } catch (EOFException e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onFinish(mUrl, mFile, DownloadError.ERR_NETWORK);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onFinish(mUrl, mFile, DownloadError.ERR_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onFinish(mUrl, mFile, DownloadError.ERR_OTHER);
            }
        } finally {
            isDownloading = false;
            if (httpURLConnection != null) {
                try {
                    httpURLConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            close(output);
            close(input);
        }
    }

    private static void close(Closeable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (Throwable e) {

        }
    }

    private void onfinish(long alllength) {
        if (mFile == null) {
            if (listener != null) {
                listener.onFinish(mUrl, mFile, DownloadError.ERR_FILE);
            }
        } else {
            if (tmpFile.length() == alllength) {
                try {
                    File rfile = new File(mFile);
                    rfile.delete();
                    tmpFile.renameTo(rfile);
                    if (listener != null) {
                        listener.onFinish(mUrl, mFile, DownloadError.ERR_NONE);
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFinish(mUrl, mFile, DownloadError.ERR_OTHER);
                    }
                }
            } else {
                if (listener != null) {
                    listener.onFinish(mUrl, mFile, DownloadError.ERR_FILE);
                }
            }
        }
    }

    private long getContentLength(String uri) {
        contentLength = 0;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(uri);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(60 * 1000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-agent", System.getProperty("http.agent"));
            contentLength = connection.getContentLength();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return contentLength;
    }
}
