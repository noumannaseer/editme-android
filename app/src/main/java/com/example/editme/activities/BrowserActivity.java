package com.example.editme.activities;

import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.example.editme.R;
import com.example.editme.interfaces.ConnectionChangeCallback;
import com.example.editme.utils.AndroidUtil;
import com.example.editme.utils.NetworkChangeReceiver;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import lombok.val;

//*********************************************************************
public class BrowserActivity
        extends BaseActivity
        implements ConnectionChangeCallback
//*********************************************************************
{
    private WebView mWebView;
    private View mProgressView;
    public static final String INFO_TITLE = "INFO_TITLE";
    public static final String SCREEN_URL = "SCREEN_URL";
    private String mTitle;
    private String mUrl;
    private NetworkChangeReceiver receiver;
    private ImageView mNoInternetImage;
    private IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");

    //*********************************************************************
    private void getData()
    //*********************************************************************
    {
        Bundle args = getIntent().getExtras();
        if (args != null)
        {
            mTitle = args.getString(INFO_TITLE);
            mUrl = args.getString(SCREEN_URL);
        }
    }

    //*********************************************************************
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    //*********************************************************************
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        getData();
        val toolbar = (Toolbar)findViewById(R.id.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mTitle);
        mWebView = findViewById(R.id.web_view);
        mProgressView = findViewById(R.id.progress_view);
        mNoInternetImage = findViewById(R.id.no_internet);
        mProgressView.setVisibility(View.VISIBLE);
        switchVisibility(false);

        mWebView.setWebViewClient(new WebViewClient()
        {


            //**********************************************************************
            @Override
            public void onPageFinished(WebView view, String url)
            //**********************************************************************
            {
                super.onPageFinished(view, url);
                switchVisibility(true);
            }


            //**********************************************************************
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            //**********************************************************************
            {
                super.onPageStarted(view, url, favicon);
                switchVisibility(false);
            }
        });
        mWebView.getSettings()
                .setJavaScriptEnabled(true);
        mWebView.getSettings()
                .setDomStorageEnabled(true);
        mWebView.getSettings()
                .setDatabaseEnabled(true);
        mWebView.getSettings()
                .setMinimumFontSize(1);
        mWebView.getSettings()
                .setMinimumLogicalFontSize(1);
//        mWebView.getSettings()
//                .setSupportMultipleWindows(true);
        mWebView.getSettings()
                .setJavaScriptEnabled(true);
        mWebView.getSettings()
                .setJavaScriptEnabled(true);
////        mWebView.getSettings()
////                .setJavaScriptCanOpenWindowsAutomatically(true);
//        if (mUrl.contains("company/company/"))
//        {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        }
//
//        if (mUrl.contains(".pdf"))
//            mWebView.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + mUrl);
//        else
        mWebView.loadUrl(mUrl);

    }

    //*********************************************************************
    @Override
    protected void onPostResume()
    //*********************************************************************
    {
        super.onPostResume();
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, intentFilter);
        receiver.setConnectionChangeCallback(this);
    }


    //*********************************************************************
    @Override
    protected void onDestroy()
    //*********************************************************************
    {
        if (receiver != null)
            unregisterReceiver(receiver);
        super.onDestroy();
    }

    //**********************************************************************
    private void switchVisibility(boolean pageLoaded)
    //**********************************************************************
    {

        if (AndroidUtil.isNetworkStatusAvialable())
        {
            if (pageLoaded)
            {
                mWebView.setVisibility(View.VISIBLE);
                mProgressView.setVisibility(View.GONE);
                mNoInternetImage.setVisibility(View.GONE);
            }
            else
            {
                mWebView.setVisibility(View.GONE);
                mProgressView.setVisibility(View.VISIBLE);
                mNoInternetImage.setVisibility(View.GONE);
            }

        }
        else
        {
            mWebView.setVisibility(View.GONE);
            mProgressView.setVisibility(View.GONE);
            mNoInternetImage.setVisibility(View.VISIBLE);
        }
    }


    //**********************************************************************
    @Override
    public void onBackPressed()
    //**********************************************************************
    {
        if (mWebView.canGoBack())
            mWebView.goBack();
        else
            super.onBackPressed();
    }

    //**********************************************************************
    @Override
    public void onConnectionChanged(boolean isConnected)
    //**********************************************************************
    {
        if (!isConnected)
        {
            mWebView.setVisibility(View.GONE);
            mProgressView.setVisibility(View.GONE);
            mNoInternetImage.setVisibility(View.VISIBLE);
        }
        else
        {
            mWebView.setVisibility(View.GONE);
            mWebView.reload();
            mProgressView.setVisibility(View.VISIBLE);
            mNoInternetImage.setVisibility(View.GONE);
        }
    }

}
