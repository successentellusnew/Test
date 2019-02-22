package com.success.successEntellus.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.success.successEntellus.R;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.InfoWindow;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.GetCFTLocations;
import com.success.successEntellus.model.UserLocation;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CFTLocatorBeforeLogin extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9009;
    Button btn_subscibe_now, btn_cont_to_login;
    //TextView tv_located;
    SearchView svw_search_location;
    public static GoogleMap mMap;
    double user_lat, user_long;
    public GoogleApiClient mGoogleApiClient;
    public LocationRequest mLocationRequest;
    LocationManager manager;
    Location location;
    LinearLayout ll_locator;
    List<UserLocation> locationsList;
    SPLib spLib;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cftlocator_before_login);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);

        getSupportActionBar().hide();
        init();

        if (spLib.checkSharedPrefs(SPLib.Key.USER_ID)) {
            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(intent);
            finish();
        }
      /*  if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getLocationPermission();
        }
        gpsalert();*/

        btn_subscibe_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CFTLocatorBeforeLogin.this, SignUpActivity.class));
            }
        });

        btn_cont_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CFTLocatorBeforeLogin.this, LoginActivity.class));
                finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void init() {
        btn_subscibe_now = findViewById(R.id.btn_subscibe_now);
        btn_cont_to_login = findViewById(R.id.btn_cont_to_login);
       // tv_located = findViewById(R.id.tv_located);
        ll_locator = findViewById(R.id.ll_locator);
        spLib = new SPLib(CFTLocatorBeforeLogin.this);
        // svw_search_location = findViewById(R.id.svw_search_location);
        //svw_search_location.setIconified(false);
        //svw_search_location.setFocusable(false);
        // svw_search_location.clearFocus();
        // svw_search_location.setQueryHint("Search Location");

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(9 * 1000) // 1 second, in milliseconds
                .setSmallestDisplacement(10);

        /*Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        //animation.setDuration(5000);
        // animation.setRepeatMode(10);
        tv_located.setAnimation(animation);*/

      /*  Animation animationScaleUp = AnimationUtils.loadAnimation(this, R.anim.pop_in);
        Animation animationScaleDown = AnimationUtils.loadAnimation(this, R.anim.pop_out);

        AnimationSet growShrink = new AnimationSet(true);
        growShrink.addAnimation(animationScaleUp);
        growShrink.addAnimation(animationScaleDown);
        tv_located.startAnimation(growShrink);*/


    }


    private void gpsalert() {
        Log.d(Global.TAG, "gpsalert: ");
        manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            Log.d(Global.TAG, "gpsalert:Location enabled ");
        }

    }

    private void getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CFTLocatorBeforeLogin.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            return;
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(CFTLocatorBeforeLogin.this);
        builder.setMessage("Please Turn On Your GPS")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
//                        dialog.cancel();
//                    }
//                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

   /* @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(CFTLocatorBeforeLogin.this, "Permission  granted!", Toast.LENGTH_LONG).show();
                    mGoogleApiClient.connect();
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    Log.d(Global.TAG, "onConnected: location:" + location);
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                    if (location!=null){
                        user_lat = location.getLatitude();
                        user_long = location.getLongitude();
                        Log.d(Global.TAG, "onConnected: " + user_lat);
                        Log.d(Global.TAG, "onConnected: " + user_long);
                        LatLng current_location = new LatLng(user_lat, user_long);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(current_location));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                        getAllCFTLocations();
                    }
                } else {
                    Toast.makeText(CFTLocatorBeforeLogin.this, "Permission denied!", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }*/

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.d(Global.TAG, "onConnected: location:" + location);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
      if (location!=null){
          user_lat = location.getLatitude();
          user_long = location.getLongitude();
          Log.d(Global.TAG, "onConnected: " + user_lat);
          Log.d(Global.TAG, "onConnected: " + user_long);
          LatLng current_location = new LatLng(user_lat, user_long);
          mMap.moveCamera(CameraUpdateFactory.newLatLng(current_location));
          mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
          getAllCFTLocations();
      }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(Global.TAG, "onConnectionSuspended: ");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(Global.TAG, "onConnectionFailed:");
        mGoogleApiClient.connect();
        if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.d(Global.TAG, "onConnectionFailed: ");
        if (location == null) {
            if (ActivityCompat.checkSelfPermission(CFTLocatorBeforeLogin.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CFTLocatorBeforeLogin.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            //If everything went fine lets get latitude and longitude
            user_lat = location.getLatitude();
            user_long = location.getLongitude();
            Log.d(Global.TAG, "onConnectionFailed: " + user_lat);
            Log.d(Global.TAG, "onConnectionFailed: " + user_long);
        }
    }

    private void getAllCFTLocations() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("platform", "2");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getAllCFTLocations: " + paramObj.toString());
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<GetCFTLocations> call = service.getAllUsersLocationBeforeLogin(paramObj.toString());
        call.enqueue(new Callback<GetCFTLocations>() {
            @Override
            public void onResponse(Call<GetCFTLocations> call, Response<GetCFTLocations> response) {
                GetCFTLocations cftLocations = response.body();
                if (cftLocations!=null){
                    if (cftLocations.isSuccess()) {
                        locationsList = cftLocations.getResult();
                        Log.d(Global.TAG, "locationsList: " + locationsList.size());
                        if (locationsList.size()>0){
                            showCFTUser(locationsList);
                        }else{
                            Toast.makeText(CFTLocatorBeforeLogin.this, "No Users Available...", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Log.d(Global.TAG, "Error in getting locations.: ");
                    }
                }

            }

            @Override
            public void onFailure(Call<GetCFTLocations> call, Throwable t) {
                Log.d(Global.TAG, "Error GetCFTLocations :onFailure " + t);
            }
        });

    }
    private void showCFTUser(final List<UserLocation> userLocationList) {
        mMap.clear();
        for (int i = 0; i < userLocationList.size(); i++) {
            // Log.d(Global.TAG, "Loop: "+i);
            String latitide = userLocationList.get(i).getUserLatitude();
            String longitude = userLocationList.get(i).getUserLongitude();

            BitmapDescriptor icon;
            if (userLocationList.get(i).getUserCftActiveStatus().equals("1")) {
                icon = BitmapDescriptorFactory.fromResource(R.drawable.green_pin);
            } else {
                icon = BitmapDescriptorFactory.fromResource(R.drawable.red_pin);
            }

            LatLng user_location = new LatLng(Double.parseDouble(latitide), Double.parseDouble(longitude));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(user_location)
                    .title("CFT")
                    .icon(icon);
//               mMap.addMarker(new MarkerOptions().position(user_location).title(userLocationList.get(i).getFirst_name()+" "+userLocationList.get(i).getLast_name()).icon(icon));
//               mMap.moveCamera(CameraUpdateFactory.newLatLng(user_location));

           /* final UserLocation userInfo = userLocationList.get(i);
            // double distance=calculateDistance(userInfo.getUserLatitude(),userInfo.getUserLongitude());
            //userInfo.setDistance(String.valueOf(distance) + " Km.");
            InfoWindow customInfoWindow = new InfoWindow(CFTLocatorBeforeLogin.this);
            mMap.setInfoWindowAdapter(customInfoWindow);
*/
            mMap.addMarker(markerOptions);

            //m.setTag(userInfo);
            // m.showInfoWindow();
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(user_location));


            //mMap.setOnInfoWindowClickListener(CFTLocatorBeforeLogin.this);


        }
       /* LatLng current_location=new LatLng(user_lat,user_long);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current_location));*/
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(Global.TAG, "onLocationChanged: ");
        user_lat = location.getLatitude();
        user_long = location.getLongitude();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Log.d(Global.TAG, "onMapReady: ");
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        getAllCFTLocations();
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(Global.TAG, "onPause: ");
        Log.d(Global.TAG, "lat: " + user_lat);
        Log.d(Global.TAG, "long: " + user_long);
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }
}
