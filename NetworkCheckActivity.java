package com.success.successEntellus.activity;

import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.success.successEntellus.R;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.SPLib;

public class NetworkCheckActivity extends AppCompatActivity {
ImageButton btn_retry;
SPLib spLib;
Bundle bundleEffect;
DashboardActivity dashboardActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_check);
        dashboardActivity= new DashboardActivity();
        btn_retry =(ImageButton)findViewById(R.id.btn_retry);
        bundleEffect = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Global.isNetworkAvailable(NetworkCheckActivity.this)){
                    dashboardActivity.replaceFragments(new DashboardFragment());
                    finish();
                }else{
                    Animation rotation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_rotate);
                    rotation.setRepeatCount(Animation.INFINITE);
                    btn_retry.startAnimation(rotation);  //start animation for Button image to rotate the image button

                    Intent intent = new Intent(getApplicationContext(), NetworkCheckActivity.class);
                    startActivity(intent, bundleEffect);
                    finish();
                }
            }
        });

    }
}
