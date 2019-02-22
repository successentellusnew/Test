package com.success.successEntellus.activity;

/**
 * Created by user on 8/4/2018.
 */
import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.location.Location;
        import android.os.Bundle;
        import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.content.LocalBroadcastManager;
        import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.api.GoogleApiClient;
        import com.google.android.gms.location.LocationListener;
        import com.google.android.gms.location.LocationRequest;
        import com.google.android.gms.location.LocationServices;
import com.success.successEntellus.lib.Global;

import java.util.Timer;
import java.util.TimerTask;

public class UpdateService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "mytag";
    Timer timer = new Timer();
    GoogleApiClient mLocationClient;
    LocationRequest mLocationRequest = new LocationRequest();
    TimerTask getTimeUpdate = new CustomTimer(UpdateService.this);
    private Thread.UncaughtExceptionHandler defaultUEH;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;


    public static final String ACTION_LOCATION_BROADCAST = UpdateService.class.getName() + "LocationBroadcast";
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        mLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();




        int priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY; //by default
        //PRIORITY_BALANCED_POWER_ACCURACY, PRIORITY_LOW_POWER, PRIORITY_NO_POWER are the other priority modes


        mLocationRequest.setPriority(priority);
        mLocationRequest.setInterval(900000);//900000 ms=15min 1800sec
        mLocationRequest.setFastestInterval(90000); //90000 1.5 min
        mLocationRequest.setSmallestDisplacement(10); //50 meters
        mLocationClient.connect();

      /*  if (!CustomTimer.ifStarted()){
            timer.scheduleAtFixedRate(getTimeUpdate, 0, 10000);
        }*/

        //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.
        return START_STICKY;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
     * LOCATION CALLBACKS
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        Log.d(TAG, "onConnected: UpdateService:");
        if (mLocationClient.isConnected()){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                Log.d("mytag", "== Error On onConnected() Permission not granted");
                //Permission not granted by user so cancel the further execution.

                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);
            LocationServices.FusedLocationApi.getLastLocation(
                    mLocationClient);
            Log.d(TAG, "Connected to Google API");
        }else{
            Log.d(TAG, "GoogleAPI Client Not connected Yet.. ");
        }

    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended");
    }

    //to get the location change
    @Override
    public void onLocationChanged(Location location) {
        Log.d("mytag", "Location changed: Service:"+location.getLatitude()+" "+location.getLongitude());
       // Toast.makeText(this, "Location Changed: New Location :"+location.getLatitude()+" "+location.getLongitude(), Toast.LENGTH_LONG).show();


        if (location != null) {
            Log.d(TAG, "== location != null");

            //Send result to activities
            sendMessageToUI(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
        }
    }

    private void sendMessageToUI(String lat, String lng) {

        Log.d(TAG, "Sending info...");
        //Toast.makeText(this, "Sending Location...", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra(EXTRA_LATITUDE, lat);
        intent.putExtra(EXTRA_LONGITUDE, lng);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Failed to connect to Google API");

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "onTaskRemoved: ");

        PendingIntent service = PendingIntent.getService(
                getApplicationContext(),
                1001,
                new Intent(getApplicationContext(), UpdateService.class),
                PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        //Toast.makeText(this, "onDestroy again started", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onTaskRemoved: ");
        intent.putExtra("msg","start_service");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

      /*  Intent intent = new Intent(getApplicationContext(), UpdateService.class);
        intent.putExtra("msg","start_service");
        PendingIntent pintent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, pintent);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
*/
       /* Intent restartServiceTask = new Intent(getApplicationContext(),this.getClass());
        restartServiceTask.setPackage(getPackageName());
        PendingIntent restartPendingIntent =PendingIntent.getService(getApplicationContext(), 1,restartServiceTask, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager myAlarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        myAlarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartPendingIntent);*/

        super.onTaskRemoved(rootIntent);

    }

    @Override
    public void onDestroy() {

        //timer.cancel();
       // Toast.makeText(this, "Service is destroyed", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onDestroy: Service is destroyed");

        PendingIntent service = PendingIntent.getService(
                getApplicationContext(),
                1001,
                new Intent(getApplicationContext(), UpdateService.class),
                PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);

        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra("msg","start_service");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

       /* Intent intent = new Intent(getApplicationContext(), UpdateService.class);
        intent.putExtra("msg","start_service");
        PendingIntent pintent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, pintent);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
*/
      /*  Toast.makeText(this, "Service is destroyed", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onDestroy: Service is destroyed");
        Intent restartServiceTask = new Intent(getApplicationContext(),this.getClass());
        restartServiceTask.setPackage(getPackageName());
        PendingIntent restartPendingIntent =PendingIntent.getService(getApplicationContext(), 1,restartServiceTask, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager myAlarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        myAlarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartPendingIntent);*/
        super.onDestroy();
    }


}