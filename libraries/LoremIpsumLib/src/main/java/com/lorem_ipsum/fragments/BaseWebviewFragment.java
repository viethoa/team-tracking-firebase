package com.lorem_ipsum.fragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lorem_ipsum.R;

/**
 * Created by Torin on 1/11/14.
 */
public class BaseWebviewFragment extends BaseFragment {

    public String mUrl;
    private WebView mWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.activity_webview, null);
        super.onCreateView(inflater, container, savedInstanceState);

        getActivity().setTitle(mTitle);

        //Hide logo in action bar
        mActionBar.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        initialiseUI();
        configureWithData();

        return mRootView;
    }

    @Override
    public void onResume() {
        if (mUrl != null && mWebView != null)
            configureWithData();

        super.onResume();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initialiseUI() {

        mWebView = (WebView) mRootView.findViewById(R.id.webview);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        mWebView.getSettings().setSupportMultipleWindows(false);
        mWebView.getSettings().setSupportZoom(false);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
    }

    private void configureWithData() {
        this.mWebView.loadUrl(this.mUrl);
        this.mWebView.requestFocus();
    }
}
