package io.rezetopia.krito.rezetopiakrito.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class WebActivity extends AppCompatActivity {

    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_web);

        webView = new WebView(this);
        webView.loadUrl("https://rezetopia.dev-krito.com/terms");

        setContentView(webView);
    }
}
