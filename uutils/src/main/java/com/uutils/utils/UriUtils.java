package com.uutils.utils;

/**
 * Created by Administrator on 2015/12/17.
 */

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class UriUtils {
    private static final String UTF_8 = "UTF-8";

    public static String toString(Map<String, String> list) {
        if (list == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> e : list.entrySet()) {
            sb.append(e.getKey() + "=" + encode(e.getValue()));
            sb.append("&");
        }
        String args = sb.toString();
        if (args.endsWith("&")) {
            args = args.substring(0, args.length() - 1);
        }
        return args;
    }

    public static String getUrlNoData(String url) {
        if (url != null) {
            int index = url.indexOf('?');
            if (index > 0) {
                return url.substring(0, index);
            }
        }
        return url;
    }

    public static HashMap<String, String> getUrlData(String uri) {
        HashMap<String, String> args = new HashMap<String, String>();
        if (uri != null) {
            int index = uri.indexOf('?');
            if (index > 0 && index < uri.length()) {
                uri = uri.substring(index + 1);
            }
            String[] tmps = uri.split("[&]");
            for (String tmp : tmps) {
                String[] m = tmp.split("=");
                if (m.length == 2) {
                    args.put(m[0], decode(m[1]));
                }
            }
        }
        return args;
    }

    public static String getRealUrl(String skipurl) {
        String newurl = null;
        String url = skipurl;
        boolean isSame = false;
        while (!isSame) {
            try {
                newurl = getLocationUrl(url);
            } catch (Exception e) {
                newurl = url;
            }
            isSame = url.equals(newurl);
            url = newurl;
        }
        return newurl;
    }

    public static String getLocationUrl(String httpurl) throws IOException {
        String newurl = httpurl;
        URL url = new URL(httpurl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(60 * 1000);// 5秒的链接超时
        connection.setReadTimeout(60 * 1000);// 设置从主机读取数据超时（单位：毫秒）
        connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
        int code = connection.getResponseCode();
        if (code == HttpURLConnection.HTTP_MOVED_TEMP || code == HttpURLConnection.HTTP_MOVED_PERM) {
            newurl = connection.getHeaderField("Location");
        } else {
            Logs.e("code=" + code);
        }
        connection.disconnect();
        return newurl;
    }

    public static String getHostwithProt(String url) {
        if (url != null) {
            int i = url.indexOf("://");
            if (i > 0) {
                int j = url.indexOf("/", i + 3);
                String host = (j > 0) ? url.substring(0, j) : url;
                return host;
            }
        }
        return url;
    }

    public static String getHostPath(String url) {
        if (url != null) {
            int i = url.indexOf("://");
            if (i > 0) {
                int j = url.indexOf("/", i + 3);
                String host = (j > 0)
                        ? url.substring(0, j) : url;
                return host;
            }
        }
        return url;
    }

    public static String decode(String str) {
        String nstr = "";
        try {
            nstr = URLDecoder.decode(str, UTF_8);
        } catch (Exception e) {

        }
        return nstr;
    }

    public static String encode(String str) {
        String nstr = "";
        try {
            nstr = URLEncoder.encode(str, UTF_8);
        } catch (Exception e) {

        }
        return nstr;
    }
}
