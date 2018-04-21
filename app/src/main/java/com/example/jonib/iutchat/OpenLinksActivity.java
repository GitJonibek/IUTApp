package com.example.jonib.iutchat;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Objects;

public class OpenLinksActivity extends AppCompatActivity {

    private WebView mWebview;
    private Toolbar mToolbar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_links);

        mWebview = findViewById(R.id.open_link_webview);
        mToolbar = findViewById(R.id.open_link_appbar);

        String title = Objects.requireNonNull(getIntent().getExtras()).getString("title");

        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);

        WebSettings webSettings = mWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        mWebview.loadUrl(Constants.eclass);
        mWebview.setWebViewClient(new WebViewClient());

    }
}
