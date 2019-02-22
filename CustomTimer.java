package com.success.successEntellus.activity;

import android.content.Context;
import android.os.Handler;

import java.util.TimerTask;

/**
 * Created by user on 8/9/2018.
 */

import android.util.Log;
import android.widget.Toast;

import com.success.successEntellus.lib.Global;

public class CustomTimer extends TimerTask {


    private Context context;
    private Handler mHandler = new Handler();
    public static boolean isStarted=false;

    public CustomTimer(Context con) {
        this.context = con;
    }



    @Override
    public void run() {
        new Thread(new Runnable() {

            public void run() {

                mHandler.post(new Runnable() {
                    public void run() {
                        isStarted=true;
                       // Toast.makeText(context, "Service is running.....", Toast.LENGTH_SHORT).show();
                        Log.d(Global.TAG, "Service is running..: ");

                    }
                });
            }
        }).start();

    }
    public static boolean ifStarted(){
        return isStarted;
    }

}