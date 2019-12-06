package com.example.intent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class FullNews extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_news);

        WebView webview =(WebView)findViewById(R.id.webView);



        Intent intent = getIntent();

        String brief = intent.getStringExtra("url");
        // String brief1 = intent.getStringExtra("result1");
        String Url = "https://www.reddit.com"+brief;

        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient());
        webview.loadUrl(Url);

    }
}
