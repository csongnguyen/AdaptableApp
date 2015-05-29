package com.adaptableandroid;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Connie on 5/29/2015.
 */
public class WebViewActivity extends ActionBarActivity {

    private WebView webView;

    private class HelloWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            return false;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(true);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF5BA4F3));//0xFF4697b5

        Intent intent = getIntent();
        String url = intent.getStringExtra(StringUtils.WEB_VIEW);

        webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
//        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
//        webView.getSettings().setPluginsEnabled(true);
//        webView.setWebViewClient(new HelloWebViewClient());
        webView.loadUrl(url);

    }

}
