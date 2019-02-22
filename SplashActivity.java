package com.success.successEntellus.activity;

import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.success.successEntellus.R;

public class SplashActivity extends AppCompatActivity {
    ImageView iv_splash;
    Bundle bundleEffect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        bundleEffect = ActivityOptionsCompat.makeCustomAnimation(SplashActivity.this, android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
        Animation animation= AnimationUtils.loadAnimation(SplashActivity.this,R.anim.fade_in);
        iv_splash=(ImageView)findViewById(R.id.iv_splash);
        iv_splash.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent=new Intent(SplashActivity.this,CFTLocatorBeforeLogin.class);
                startActivity(intent,bundleEffect);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }
}
