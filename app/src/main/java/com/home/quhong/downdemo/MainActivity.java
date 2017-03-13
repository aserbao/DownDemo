package com.home.quhong.downdemo;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.uutils.crypto.MD5Utils;
import com.uutils.net.DownloadError;
import com.uutils.net.DownloadListener;
import com.uutils.net.DownloadManager;
import com.uutils.plugin.Analytics;
import com.uutils.utils.Logs;
import com.uutils.utils.ToastUtils;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @BindView(R.id.btn_down1)
    Button mBtnDown1;
    @BindView(R.id.pb_down1)
    ProgressBar mPbDown1;
    @BindView(R.id.btn_down2)
    Button mBtnDown2;
    @BindView(R.id.pb_down2)
    ProgressBar mPbDown2;
    private String URL_TEST = "https://redirector.googlevideo.com/videoplayback?id=34a855bd1a3d2840&itag=18&source=webdrive&requiressl=yes&ttl=transient&mm=30&mn=sn-4g5ednek&ms=nxu&mv=m&pl=20&ei=sTHGWP30FYWHqgXroLGgBQ&mime=video/mp4&lmt=1477990429524475&mt=1489383752&ip=37.120.186.184&ipbits=0&expire=1489398257&sparams=ip,ipbits,expire,id,itag,source,requiressl,ttl,mm,mn,ms,mv,pl,ei,mime,lmt&signature=278208C5D9CB5AA06C448E4F8C008ACB1C528CDD.939EEF15BAC8F3D05CF67BD571EDAC7629C37B53&key=ck2&type=video/mp4&title=E2-1";
    private String URL_TEST2 = "http://music.wufazhuce.com/lmyuVg_Y-L37x6g93quntpGF2tyH";
    private String file1 = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + File.separator+"QuHong"+File.separator+"test1.mp4";
    private String file2 = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + File.separator+"QuHong"+File.separator+"test2.mp4";
    private final String md51 = MD5Utils.getFileMD5(file1);
    private final String md52 = MD5Utils.getFileMD5(file2);
    private Context mContext;
    private DownloadManager mDownloadManager;
    private DecimalFormat mFnum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = this;
    }

    @OnClick({R.id.btn_down1, R.id.btn_down2, R.id.pb_down2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_down1:
                downLoad(URL_TEST,file1,md51);
                break;
            case R.id.btn_down2:
                downLoad(URL_TEST2,file2,md52);
                break;
            case R.id.pb_down2:
                break;
        }
    }

    public void downLoad(String url, String file, final String md5) {
        mDownloadManager = DownloadManager.getInstance(mContext);
        if (mDownloadManager.download(url, file, md5, new DownloadListener() {
            @Override
            public void onStart(String url, String file) {

            }

            @Override
            public void onProgress(String url, String file, float p) {
                double v = Math.ceil(p*100);
                if (file == file1){
                    mPbDown1.setProgress((int)v);
                }else{
                    mPbDown2.setProgress((int)v);
                }
                Log.d(TAG, "onProgress: "+file+"---->"+p*100);
            }

            @Override
            public void onFinish(String url, String file, DownloadError err) {
               if (err == DownloadError.ERR_NONE) {
                    Analytics.onEvent(mContext, "wbsdk_download_data_success", "fileName", file);
                    Logs.d("wbsdk_download_js_success fileName = " + file);

                } else if (err == DownloadError.ERR_NONE_SAME_MD5) {
                    Logs.d("wbsdk_download_js_success same md5 fileName =" + file);
                } else {
                    Analytics.onEvent(mContext, " ");
                    Logs.d("wbsdk_download_js_fail");
                }
            }
        })) {
            //开始下载
            Analytics.onEvent(mContext, "wbsdk_download_js_start");
            Logs.d("wbsdk_download_js_start");
        } else {
            //正在下载
            Analytics.onEvent(mContext, "wbsdk_download_js_running");
            Logs.d("wbsdk_download_js_running");
        }
    }

}
