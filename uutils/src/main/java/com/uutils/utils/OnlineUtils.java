/**
 *
 */
package com.uutils.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.uutils.crypto.DESUtils;
import com.uutils.plugin.Analytics;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class OnlineUtils {
    //region 以下是POST BODY的键值对定义
    /**
     * 程序包名
     */
    public static final String KEY_PACKAGENAME = "package_name";
    /**
     * 手机语言
     */
    public static final String KEY_LANGUAGE = "language";
    /**
     * 手机imei号
     */
    public static final String KEY_IMEI = "imei";
    public static final String KEY_IMSI = "imsi";
    /**
     * APP版本码 version code
     */
    public static final String KEY_VERSION = "version";
    public static final String KEY_VERSIONNAME = "versionName";
    /**
     * 手机屏幕密度
     */
    public static final String KEY_DENSITY = "density";
    /**
     * 手机名称(huawei, xiaomi, saga， etc)
     */
    public static final String KEY_PHONENAME = "phonename";
    /**
     * APP渠道号
     */
    public static final String KEY_CHANNEL = "channelId";
    /**
     * 手机屏幕高度
     */
    public static final String KEY_SHEIGHT = "screenheight";
    /**
     * 手机屏幕宽度
     */
    public static final String KEY_SWIDTH = "screenwidth";
    /**
     * 手机当前联网IP
     */
    public static final String KEY_IP = "ip";
    /**
     * 手机当前联网wifi IP
     */
    public static final String KEY_WIFI_IP = "ip";
    /**
     * 手机SIM卡的国家编号
     */
    public static final String KEY_COUNTYISO = "countyiso";

    public static final String KEY_MAC = "macaddress";
    /**
     * 手机唯一标识，由imei+mac 生成
     */
    public static final String KEY_ClientID = "clientid";
    /**
     * 个推cid
     */
    public static final String KEY_CID = "getuicid";
    /**
     * yeahmobi
     */
    public static final String KEY_UDID = "udid";
    public static final String KEY_ANDROIDID = "androidid";
    /**
     * SIM更换时间
     */
    public static final String KEY_SIMTIME = "simtime";
    /**
     * 新的解析
     */
    public static final String KEY_NEWPARSE = "newparse";
    /**
     * 是否有googleplay
     */
    public static final String KEY_GPSTORE = "gpstore";
    public static final String KEY_FACEBOOK = "facebook";
    /**
     * 是否root
     */
    public static final String KEY_ROOT = "root";
    /**
     * 发布版本
     */
    public static final String KEY_OSLEVEL = "oslevel";
    /**
     * appid
     */
    public static final String KEY_APPID = "appverid";
    /**
     * 使用json 0 1
     */
    public static final String KEY_JSON = "json";
    //endregion

    public static final String KEY_SIMName = "simname";

    public static final String KEY_ApnName = "apnname";


    //region 参数组合

    /**
     * 可选参数，必须要一对对
     */
    public static Map<String, String> makePostData(Context context, Object... params) {
        Map<String, String> nameValuePairs = getDefaultData(context);
        if (params != null) {
            int i = 0;
            for (i = 0; i < (params.length / 2); i++) {
                String k = String.valueOf(params[i * 2]);
                String v = String.valueOf(params[i * 2 + 1]);
                nameValuePairs.put(k, v);
            }
            if (i < params.length / 2) {
                Logs.w("params:" + i + "," + params.length);
            }
        }
        return nameValuePairs;
    }

    private static Map<String, String> getDefaultData(Context context) {
        Map<String, String> nameValuePairs = new LinkedHashMap<String, String>();
        SystemUtils.init(context);
        if (SystemUtils.mScreenHeight > 0) {
            nameValuePairs.put(KEY_SHEIGHT, String.valueOf(SystemUtils.mScreenHeight));
        }

        if (SystemUtils.mScreenWidth > 0) {
            nameValuePairs.put(KEY_SWIDTH, String.valueOf(SystemUtils.mScreenWidth));
        }
        if (SystemUtils.mDensity > 0) {
            nameValuePairs.put(KEY_DENSITY, String.valueOf(SystemUtils.mDensity));
        }

        if (SystemUtils.mImei != null) {
            nameValuePairs.put(KEY_IMEI, SystemUtils.mImei);
        }
        if (SystemUtils.mImsi != null) {
            nameValuePairs.put(KEY_IMSI, SystemUtils.mImsi);
        }
        if (SystemUtils.mVersionCode > 0) {
            nameValuePairs.put(KEY_VERSION, "" + SystemUtils.mVersionCode);
        }
        if (SystemUtils.mVersionName != null) {
            nameValuePairs.put(KEY_VERSIONNAME, SystemUtils.mVersionName);
        }
        if (SystemUtils.mPackageName != null) {
            nameValuePairs.put(KEY_PACKAGENAME, SystemUtils.mPackageName);
        }
        if (SystemUtils.mIP != null) {
            nameValuePairs.put(KEY_IP, SystemUtils.mIP);
        }
        if (SystemUtils.osLevel != null) {
            nameValuePairs.put(KEY_OSLEVEL, "" + SystemUtils.osLevel);
        }
        if (SystemUtils.phoneName != null) {
            nameValuePairs.put(KEY_PHONENAME, SystemUtils.phoneName);
        }
        if (SystemUtils.wifiIP != null) {
            nameValuePairs.put(KEY_WIFI_IP, SystemUtils.wifiIP);
        }
        Log.i("TDlog", "channel = " + SystemUtils.channelId);
        if (SystemUtils.channelId != null) {
            nameValuePairs.put(KEY_CHANNEL, SystemUtils.channelId);
        }
        if (SystemUtils.networkCountryIso != null) {
            nameValuePairs.put(KEY_COUNTYISO, SystemUtils.networkCountryIso);
        } else {
            nameValuePairs.put(KEY_COUNTYISO, "86");
        }
        if (SystemUtils.language != null) {
            nameValuePairs.put(KEY_LANGUAGE, SystemUtils.language);
        }

        if (SystemUtils.macAddress != null) {
            nameValuePairs.put(KEY_MAC, SystemUtils.macAddress);
        }

        if (SystemUtils.ClientID != null) {
            nameValuePairs.put(KEY_ClientID, SystemUtils.ClientID);
        }
        if (SystemUtils.AndroidId != null) {
            nameValuePairs.put(KEY_ANDROIDID, SystemUtils.AndroidId);
        }
        nameValuePairs.put(KEY_UDID, SystemUtils.Uuid);
        if (SystemUtils.GetuiID != null) {
            nameValuePairs.put(KEY_CID, SystemUtils.GetuiID);
        }
        if (SystemUtils.SIMName != null) {
            nameValuePairs.put(KEY_SIMName, SystemUtils.SIMName);
        }
        if (SystemUtils.apnName != null) {
            nameValuePairs.put(KEY_ApnName, SystemUtils.apnName);
        }
        /** 第一次插入sim的时间 */
        if (SystemUtils.FristSIM > 0) {
            nameValuePairs.put(KEY_SIMTIME, "" + SystemUtils.FristSIM);
        }
        nameValuePairs.put(KEY_GPSTORE, "" + SystemUtils.hasGPSotre);
        nameValuePairs.put(KEY_FACEBOOK, "" + SystemUtils.hasFaceBook);
        nameValuePairs.put(KEY_ROOT, SystemUtils.isRoot ? "1" : "0");
        nameValuePairs.put(KEY_NEWPARSE, "1");
        return nameValuePairs;
    }
    //endregion

    //region network
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = cm.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static boolean isWifiConnected(Context context) {
        if (context == null) return false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();// wifi
        return info != null && info.isAvailable() && info.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static boolean isMobileConnected(Context context) {
        if (context == null) return false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isAvailable() && info.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    public static String getIp(Context context) {
        if (isWifiConnected(context)) {
            return getWifiIpAddress(context);
        }
        return getGPRSIpAddress();
    }

    public static String getGPRSIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMacAddress(Context context) {
        String address;
        address = loadAddress("wlan0");
        if (TextUtils.isEmpty(address)) {
            address = loadAddress("eth0");
        }
        if (TextUtils.isEmpty(address)) {
            try {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                address = wifiManager.getConnectionInfo().getMacAddress();
            } catch (Exception e) {
            /* no-op */
            }
        }
        if (TextUtils.isEmpty(address)) {
            return address;
        }
        return address.toUpperCase(Locale.US).replaceAll("\\s", "");
    }

    private static String loadAddress(final String interfaceName) {
        try {
            final String filePath = "/sys/class/net/" + interfaceName + "/address";
            final StringBuilder fileData = new StringBuilder(1000);
            final BufferedReader reader = new BufferedReader(new FileReader(filePath), 1024);
            final char[] buf = new char[1024];
            int numRead;

            String readData;
            while ((numRead = reader.read(buf)) != -1) {
                readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
            }

            reader.close();
            return fileData.toString();
        } catch (IOException e) {
            return null;
        }
    }

    public static String getWifiIpAddress(Context context) {
        String ip = null;
        try {
            final WifiManager wifimanage = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiinfo = wifimanage.getConnectionInfo();
            int i = wifiinfo.getIpAddress();
            ip = (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return ip;
    }
    //endregion

    //region http

    /***
     * @param url
     * @param ispost
     * @param timeout
     * @param propertys 头
     * @param datas     body
     * @return
     * @throws IOException
     */
    public static HttpURLConnection connectHttp(String url, boolean ispost, int timeout, Map<String, String> datas, Map<String, String> propertys, boolean isEncrypt) throws IOException {

        URL _url = new URL(url);
        HttpURLConnection url_con = (HttpURLConnection) _url.openConnection();

        if (timeout > 0) {
            url_con.setConnectTimeout(timeout);
            url_con.setReadTimeout(timeout);
        }

        url_con.setRequestProperty("User-agent", System.getProperty("http.agent"));
        if (propertys != null) {
            for (Map.Entry<String, String> entry : propertys.entrySet()) {
                url_con.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        // 设置session
        if (ispost) {
            // 输入参数
            url_con.setRequestMethod("POST");
            String body = UriUtils.toString(datas);
            if (isEncrypt) {
                body = DESUtils.encryptServerData(body, DESUtils.SIGN_POST_KEY);
            }
            if (body != null && body.length() > 0) {
                url_con.setDoOutput(true);
                url_con.getOutputStream().write(body.getBytes());
            }
        } else {
            url_con.setRequestMethod("GET");
        }

        return url_con;
    }

    public static HttpURLConnection connect(String url, String url_2, boolean ispost, int timeout, Map<String, String> datas, Map<String, String> propertys, boolean isEncrypt) throws IOException {
        URL _url = new URL(url);
        HttpsURLConnection url_con = null;
        if (url.startsWith("https")) {
            try {
                trustAllHosts();
                url_con = (HttpsURLConnection) _url.openConnection();
                url_con.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
                if (timeout > 0) {
                    url_con.setConnectTimeout(timeout);
                    url_con.setReadTimeout(timeout);

                }

                url_con.setRequestProperty("User-agent", System.getProperty("http.agent"));
                if (propertys != null) {
                    for (Map.Entry<String, String> entry : propertys.entrySet()) {
                        url_con.setRequestProperty(entry.getKey(), entry.getValue());
                    }
                }
                // 设置session
                if (ispost) {
                    // 输入参数
                    url_con.setRequestMethod("POST");
                    String body = UriUtils.toString(datas);
                    if (isEncrypt) {
                        body = DESUtils.encryptServerData(body, DESUtils.SIGN_POST_KEY);
                    }
                    if (body != null && body.length() > 0) {
                        url_con.setDoOutput(true);
                        url_con.getOutputStream().write(body.getBytes());
                    }
                } else {
                    url_con.setRequestMethod("GET");
                }
                Logs.d("https connect ResponseCode =" + url_con.getResponseCode());


            } catch (Exception e) {
                Logs.e("https connect e =" + e);
                if (SystemUtils.sContext != null && ispost) {
                    Logs.e("post https_post_method_sigin_exception msg to UM");
                    Analytics.onEvent(SystemUtils.sContext, "https_post_method_sigin_exception", "msg", e.toString());
                }
                if (url_2 != null) {
                    Logs.e("connect url_2=" + url_2);
                    return connectHttp(url_2, ispost, timeout, datas, propertys, isEncrypt);
                }
            }
        } else {
            return connectHttp(url, ispost, timeout, datas, propertys, isEncrypt);
        }
        return url_con;
    }

    /**
     * Member cache 文件解压处理
     *
     * @param buf
     * @return
     * @throws IOException
     */
    private static byte[] unGzip(byte[] buf) throws IOException {
        GZIPInputStream gzi = null;
        ByteArrayOutputStream bos = null;
        try {
            gzi = new GZIPInputStream(new ByteArrayInputStream(buf));
            bos = new ByteArrayOutputStream(buf.length);
            int count = 0;
            byte[] tmp = new byte[2048];
            while ((count = gzi.read(tmp)) != -1) {
                bos.write(tmp, 0, count);
            }
            buf = bos.toByteArray();
        } finally {
            if (bos != null) {
                bos.flush();
                bos.close();
            }
            if (gzi != null) gzi.close();
        }
        return buf;
    }

    /**
     * Member cache 文件压缩处理
     *
     * @param val
     * @return
     * @throws IOException
     */
    private static byte[] gzip(byte[] val) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(val.length);
        GZIPOutputStream gos = null;
        try {
            gos = new GZIPOutputStream(bos);
            gos.write(val, 0, val.length);
            gos.finish();
            gos.flush();
            bos.flush();
            val = bos.toByteArray();
        } finally {
            if (gos != null) gos.close();
            if (bos != null) bos.close();
        }
        return val;
    }


    private static byte[] getContent(String url, String url_2, boolean ispost, int timeout, Map<String, String> datas, boolean isEncrypt) {
        ByteArrayOutputStream outputStream = null;
        InputStream inputStream = null;
        byte[] result = null;
        HttpURLConnection conn = null;
        try {
            conn = connect(url, url_2, ispost, 60 * 1000, datas, null, isEncrypt);
            outputStream = new ByteArrayOutputStream();
            int code = conn.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_PARTIAL) {
                inputStream = conn.getInputStream();
                // 4096
                byte[] data = new byte[4096];
                int len = 0;
                while ((len = inputStream.read(data)) != -1) {
                    outputStream.write(data, 0, len);
                }
                result = outputStream.toByteArray();
                Logs.w("result.length=" + result.length);
            } else if (code < HttpURLConnection.HTTP_OK) {
                Logs.w("other:" + code);
            } else {
                Logs.w("err:" + code);
            }
        } catch (Exception e) {
            Logs.e("e:" + e);
            if (SystemUtils.sContext != null && ispost) {
                Logs.e("post http_post_method_sigin_exception msg to UM");
                Analytics.onEvent(SystemUtils.sContext, "http_post_method_sigin_exception", "msg", e.toString());
            }
            e.printStackTrace();
        } finally {
            FileUtils.close(inputStream);
            FileUtils.close(outputStream);
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result;
    }

    public static byte[] get(String url) {
        return get(url, null, false);
    }

    public static byte[] get(String url, String url_2) {
        return getContent(url, url_2, false, 60 * 1000, null, false);
    }

    public static byte[] get(String url, String url_2, boolean isEncrypt) {
        return getContent(url, url_2, false, 60 * 1000, null, isEncrypt);
    }

    // 使用POST方法提交到后台
    public static byte[] post(String url, Map<String, String> data) {
        return getContent(url, null, true, 60 * 1000, data, false);
    }

    public static byte[] post(String url, Map<String, String> data, boolean isEncrypt) {
        return getContent(url, null, true, 60 * 1000, data, isEncrypt);
    }

    /**
     *
     * @param url  连接服务器的主url可以是https链接或者http链接
     * @param url_2  当主url连接不到时访问的链接，只可以是http链接或null
     * @param data
     * @param isEncrypt
     * @return
     */
    public static byte[] post(String url, String url_2, Map<String, String> data, boolean isEncrypt) {
        return getContent(url, url_2, true, 60 * 1000, data, isEncrypt);
    }
    //endregion

    //endregion
    // 移动数据开启和关闭
    public static boolean setMobileDataStatus(Context context, boolean enabled) {
        boolean state = false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //ConnectivityManager类

            Class<?> conMgrClass = null;

            //ConnectivityManager类中的字段
            Field iConMgrField = null;
            //IConnectivityManager类的引用
            Object iConMgr = null;
            //IConnectivityManager类
            Class<?> iConMgrClass = null;
            //setMobileDataEnabled方法
            Method setMobileDataEnabledMethod = null;
            try {

                //取得ConnectivityManager类
                conMgrClass = Class.forName(conMgr.getClass().getName());
                //取得ConnectivityManager类中的对象Mservice
                iConMgrField = conMgrClass.getDeclaredField("mService");
                //设置mService可访问
                iConMgrField.setAccessible(true);
                //取得mService的实例化类IConnectivityManager
                iConMgr = iConMgrField.get(conMgr);
                //取得IConnectivityManager类
                iConMgrClass = Class.forName(iConMgr.getClass().getName());

                //取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法
                setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);

                //设置setMobileDataEnabled方法是否可访问
                setMobileDataEnabledMethod.setAccessible(true);
                //调用setMobileDataEnabled方法
                setMobileDataEnabledMethod.invoke(iConMgr, enabled);
                state = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                TelephonyManager telephonyService = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

                Method setMobileDataEnabledMethod = telephonyService.getClass().getDeclaredMethod("setDataEnabled", boolean.class);

                if (null != setMobileDataEnabledMethod) {
                    setMobileDataEnabledMethod.invoke(telephonyService, enabled);
                    state = true;
                }
            } catch (Exception ex) {
                ex.printStackTrace();

            }
        }
        return state;
    }


    //获取移动数据开关状态
    public static boolean getMobileDataStatus(Context context) {
        Boolean isOpen = false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ConnectivityManager cm;
            cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Class cmClass = cm.getClass();
            Class[] argClasses = null;
            Object[] argObject = null;

            try {
                Method method = cmClass.getMethod("getMobileDataEnabled", argClasses);
                isOpen = (Boolean) method.invoke(cm, argObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                TelephonyManager telephonyService = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

                Method getMobileDataEnabledMethod = telephonyService.getClass().getDeclaredMethod("getDataEnabled");

                if (null != getMobileDataEnabledMethod) {
                    isOpen = (Boolean) getMobileDataEnabledMethod.invoke(telephonyService);
                }
            } catch (Exception ex) {
            }
        }
        return isOpen;
    }

    /**
     * WIFI网络开关
     */
    public static void setWifiStatus(Context context, boolean enabled) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wm.setWifiEnabled(enabled);
    }

    /**
     * @param context
     * @return 当前网络类型（wifi /2/3/4G ）
     */
    public static String GetNetworkType(Context context) {
        String strNetworkType = "unknown";

        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                strNetworkType = "WIFI";
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String _strSubTypeName = networkInfo.getSubtypeName();

                // TD-SCDMA   networkType is 17
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        strNetworkType = "2G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        strNetworkType = "3G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        strNetworkType = "4G";
                        break;
                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                            strNetworkType = "3G";
                        } else {
                            if (!TextUtils.isEmpty(strNetworkType)) {
                                strNetworkType = _strSubTypeName;
                            }
                        }
                        break;
                }
            }
        }
        return strNetworkType;
    }

    public static boolean gotoBrowser(Context context, String uriString) {
        boolean ret = false;

        if (TextUtils.isEmpty(uriString)) {
            return ret;
        }

        Uri browserUri = Uri.parse(uriString);
        if (browserUri == null) {
            return ret;
        }

        Intent browserIntent = new Intent("android.intent.action.VIEW", browserUri);
        // Intent.FLAG_ACTIVITY_FORWARD_RESULT
        browserIntent.setFlags(335544320);
        try {
            context.startActivity(browserIntent);
            ret = true;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

   /* private static Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");*/

    /**
     * 需要添加系统权限
     * <uses-permission android:name="android.permission.WRITE_APN_SETTINGS"/>
     *
     * @param context
     * @return
     */
    /*public static String getApnType(Context context) {
        String apntype = "nomatch";
        Cursor c = context.getContentResolver().query(PREFERRED_APN_URI, null, null, null, null);
        c.moveToFirst();
        String user = c.getString(c.getColumnIndex("user"));
        Logs.d("user= "+user);
        apntype = c.getString(c.getColumnIndex("apn"));
        Logs.d("apntype= "+apntype);
        return user;
    }*/
    public static void trustAllHosts() throws Exception {
        // Create a trust manager that does not validate certificate chains
        // Android use X509 cert
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
        }};

        // Install the all-trusting trust manager
           /* SSLContext sc = SSLContext.getInstance("TLSv1");
            SSLSocketFactory NoSSLv3Factory = new NoSSLv3SocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultSSLSocketFactory(NoSSLv3Factory);*/

        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

    }
}
