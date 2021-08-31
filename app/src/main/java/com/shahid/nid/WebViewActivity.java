package com.shahid.nid;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;



public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        ImageView backButton = findViewById(R.id.back_button);

        WebView myWebView = findViewById(R.id.webview);

        String url_string = getIntent().getStringExtra("url");

        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.loadUrl(url_string);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
