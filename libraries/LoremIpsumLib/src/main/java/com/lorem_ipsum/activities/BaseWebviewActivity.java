package com.lorem_ipsum.activities;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lorem_ipsum.R;

/**
 * Created by Torin on 23/10/14.
 */
public class BaseWebviewActivity extends BaseActivity {

    private WebView mWebView;
    private String mUrl;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        ActionBar actionBar = getActionBar();
        if(actionBar != null)
            actionBar.hide();

        if (getIntent().getExtras().isEmpty()) {
            Log.e(LOG_TAG, "No intent extras");
            return;
        }

        this.mUrl = getIntent().getExtras().getString("url", null);
        if (mUrl == null || mUrl.length() <= 0) {
            Log.e(LOG_TAG, "Invalid URL");
            return;
        }

        this.mWebView = (WebView) findViewById(R.id.webview);
        this.mWebView.setWebViewClient(new WebViewClient());
        this.mWebView.setWebChromeClient(new WebChromeClient());
        this.mWebView.getSettings().setJavaScriptEnabled(true);
        this.mWebView.loadUrl(this.mUrl);
        this.mWebView.requestFocus();
    }

    @Override
    protected void onPause() {
        dismissLoadingDialog();
        mWebView.stopLoading();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mUrl = null;
        if (mWebView != null)
            mWebView.stopLoading();
        mWebView = null;
        super.onDestroy();
    }
}
