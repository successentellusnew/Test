package com.success.successEntellus.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;

import com.success.successEntellus.R;

public class SignUpActivity extends AppCompatActivity {
    WebView wv_sign_up;
    ImageButton ib_sign_up_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();

        ib_sign_up_back=findViewById(R.id.ib_sign_up_back);
        wv_sign_up=findViewById(R.id.wv_sign_up);
        WebSettings webSettings = wv_sign_up.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wv_sign_up.loadUrl("https://successentellus.com/signup/register");

        ib_sign_up_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
