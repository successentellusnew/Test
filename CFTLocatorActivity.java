package com.success.successEntellus.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.vision.barcode.Barcode;
import com.squareup.picasso.Picasso;
import com.success.successEntellus.R;
import com.success.successEntellus.adapter.UserInfoAdapter;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.InfoWindow;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.CFTOffice;
import com.success.successEntellus.model.GetCFTLocations;
import com.success.successEntellus.model.GetCustomizeModuleList;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.model.SingleModule;
import com.success.successEntellus.model.UserLocation;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CFTLocatorActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnInfoWindowClickListener, CompoundButton.OnCheckedChangeListener, UserInfoAdapter.RefreshUserStatus, GoogleMap.OnMarkerClickListener {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1111;
    public static GoogleMap mMap;
    AutoCompleteTextView sv_search_location;
    Toolbar toolbar_location;
    double user_lat, user_long;
    LatLng starting_point;
    public GoogleApiClient mGoogleApiClient;
    public LocationRequest mLocationRequest;
    LocationManager manager;
    ImageButton ib_location_back;
    SPLib spLib;
    Location location;
    public static ArrayList<LatLng> markerPoints = new ArrayList<>();
    List<UserLocation> locationsList;
    PolylineOptions lineOptions = null;
    Polyline polyLine = null;
    boolean isSecond = false;
    Switch sw_cft_status;
    private String user_address = "", user_city = "", user_state = "", user_country = "", zipcode = "";
    private int cftActiveStatus;
    Button btn_search_location;
    ImageButton btn_find_route,btn_share_location;
    LatLng lastClicked;
   // boolean directionFlag = false;
    RecyclerView rv_user_listshow;
    Button btn_user_dissmiss;
    boolean is_tracking;
    Switch sw_enable_all;
    List<SingleModule> moduleList = new ArrayList<>();
    List<String> moduleIds = new ArrayList<>();
    String disableFlag;
    List<String> CFTuserMarkers = new ArrayList<>();
    List<CFTOffice> cftOfficeList;
    RadioButton rb_search_location, rb_search_user;
    CardView ll_search_location;
    RadioGroup rbg_search_by;
    Button btn_search_cft;
    ImageButton ib_close_search,ib_cft_search;
    String[] user_names_list, user_ids_list;
    ArrayAdapter<String> autoCompleteAdapter;
    String search_user_id="";
    DashboardActivity dashboardActivity;
    float zoom=20;

    //private ArrayList<LatLng> moving_points; //added
    //Polyline moving_line;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cftlocator);
        toolbar_location = findViewById(R.id.toolbar_location);
        setActionBar(toolbar_location);

        setTitle("CFT Locator");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        init();
        getModuleDetails();
        /*if (!Global.isModulePresent(CFTLocatorActivity.this,"21")){
            Log.d(Global.TAG, "CFT Locator Module not present: ");
            openDialogDisplayAlert();
        }else{
            Log.d(Global.TAG, "CFT Locator Module present: ");
        }*/

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getLocationPermission();
        }
        // Initializing
        gpsalert();
        getCFTStatus();

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Log.d(Global.TAG, "onReceive: ");
                        String msg = "", latitude = "", longitude = "";
                        if (intent.hasExtra("msg")) {
                            msg = intent.getStringExtra("msg");
                        }
                        Log.d(Global.TAG, "onReceive:msg: " + msg);

                        if (intent.hasExtra(UpdateService.EXTRA_LATITUDE)) {
                            latitude = intent.getStringExtra(UpdateService.EXTRA_LATITUDE);
                        }

                        if (intent.hasExtra(UpdateService.EXTRA_LATITUDE)) {
                            longitude = intent.getStringExtra(UpdateService.EXTRA_LONGITUDE);
                        }

                        if (!msg.equals("")) {
                            Intent intent1 = new Intent(CFTLocatorActivity.this, UpdateService.class);
                            startService(intent1);
                            Log.d(Global.TAG, "Service Newly Started...........! ");


                            // Toast.makeText(CFTLocatorActivity.this, "Service Newly Started..", Toast.LENGTH_SHORT).show();

                           /* PendingIntent service = PendingIntent.getService(*/
                           /*         getApplicationContext(),*/
                           /*         1001,*/
                           /*         new Intent(getApplicationContext(), UpdateService.class),*/
                           /*         PendingIntent.FLAG_ONE_SHOT);*/
/*
*/

                           /* AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);*/
                           /* alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);*/
                           /* System.exit(2);*/
                        }

                        if ((latitude != null && longitude != null) && (!latitude.equals("") && !longitude.equals(""))) {
                            //Toast.makeText(context, "Location Received..", Toast.LENGTH_SHORT).show();
                            //mMsgView.setText("\n Latitude : " + latitude + "\n Longitude: " + longitude);
                            Log.d(Global.TAG, "onReceive: Previous User latlong:" + user_lat + " " + " " + user_long);
                            user_lat = Double.parseDouble(latitude);
                            user_long = Double.parseDouble(longitude);
                            Log.d(Global.TAG, "onReceive: LatLong Received:" + latitude + " " + longitude);
                            Log.d(Global.TAG, "onReceive: New User latlong:" + user_lat + " " + " " + user_long);

                            updateLocation();
                            //LatLng latLng = new LatLng(user_lat, user_long); //you already have this
                            //moving_points.add(latLng); //added

                            //getAllCFTLocations();
                        }
                    }
                }, new IntentFilter(UpdateService.ACTION_LOCATION_BROADCAST)
        );


        ib_cft_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ll_search_location.getVisibility() == View.VISIBLE) {
                    ll_search_location.setVisibility(View.GONE);
                } else if (ll_search_location.getVisibility() == View.GONE) {
                    ll_search_location.setVisibility(View.VISIBLE);
                }
            }
        });
        btn_find_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Global.TAG, "onDirectionClick: ");
                // LatLng position = marker.getPosition(); //
                // Log.d(Global.TAG, "onInfoWindowClick: marker latlong:"+position.latitude+" "+position.longitude);
                //LatLng latLng=new LatLng(position.latitude,position.longitude);
                //moveMyLocation();
                openDialogFindRoute();
               /* if (lastClicked != null) {
                    findRouteMap(lastClicked);
                }
*/
            }
        });

      /*  sw_cft_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

            }
        });
*/
       /* btn_search_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               findLocation(sv_search_location.getText);
            }
        });*/
      /*  sv_search_location.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(Global.TAG, "onQueryTextSubmit: ");
                findLocation(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
*/


        if (isMyServiceRunning(UpdateService.class)) {
            Log.d(Global.TAG, "Service is Running.........: ");
        }
        //updateLocation();

        rb_search_user.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    sv_search_location.setHint("Search User Name");
                    /*if (user_names_list.length>0){
                        autoCompleteAdapter = new ArrayAdapter<String>
                                (CFTLocatorActivity.this, R.layout.dropdown_textview_layout, user_names_list);
                        sv_search_location.setAdapter(autoCompleteAdapter);
                    }*/
                }

            }
        });

        rb_search_location.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    sv_search_location.setHint(" Search Location");
                    sv_search_location.dismissDropDown();
                }

            }
        });

        ib_close_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_search_location.setVisibility(View.GONE);
                getAllCFTLocations();
            }
        });

        sv_search_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getAllCFTLocations();
                int selected_rb = rbg_search_by.getCheckedRadioButtonId();
                if (selected_rb==R.id.rb_search_user){
                    sv_search_location.showDropDown();
                }

            }
        });
        sv_search_location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
               /* Log.d(Global.TAG, "onItemClick:ACV: adapterView position "+adapterView.getSelectedItemPosition());
                Log.d(Global.TAG, "onItemClick:ACV: position "+position);
                search_user_id=user_ids_list[position];
                Log.d(Global.TAG, "onItemSelected: user_id: "+search_user_id);
                Log.d(Global.TAG, "onItemSelected: user_name: "+user_names_list[position]);*/

                String selection = (String) adapterView.getItemAtPosition(position);
                int pos = -1;
                if (user_names_list.length > 0) {
                    for (int i = 0; i < user_names_list.length; i++) {
                        if (user_names_list[i].equalsIgnoreCase(selection)) {
                            pos = i;
                            break;
                        }else{
                            search_user_id="";
                        }
                    }
                    if (pos==-1){
                        search_user_id = "";
                    }else {
                        search_user_id = user_ids_list[pos];
                    }

                    Log.d(Global.TAG, "onItemClick:Position: " + pos);
                    Log.d(Global.TAG, "onItemClick:search_user_id: " + search_user_id);
                    Log.d(Global.TAG, "onItemClick:search_user_name: " + user_names_list[pos]);
                }else{
                    search_user_id="";
                }


            }

        });

        btn_search_cft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Global.TAG, "Search: ");
                int selected_rb = rbg_search_by.getCheckedRadioButtonId();

                    if (selected_rb == R.id.rb_search_location) {
                        Log.d(Global.TAG, "Search: Location ");
                        if (!sv_search_location.getText().toString().equals("")) {
                            findLocation(sv_search_location.getText().toString());
                        }else{
                            Toast.makeText(CFTLocatorActivity.this, "Please enter location to search..!", Toast.LENGTH_LONG).show();
                        }
                    } else if (selected_rb == R.id.rb_search_user) {
                        Log.d(Global.TAG, "Search:User ");
                        if (!search_user_id.equals("")){
                            searchByUser(search_user_id);
                        }else{
                            Toast.makeText(CFTLocatorActivity.this, "Please select CFT user from list..!", Toast.LENGTH_LONG).show();
                        }

                    }

            }
        });



    }

    private void moveMyLocation() {
        Log.d(Global.TAG, "moveMyLocation: ");

        final LatLng startPosition = new LatLng(user_lat,user_long);
        final LatLng finalPosition = new LatLng(20.008182, 73.785242);

        final Marker myMarker = mMap.addMarker(new MarkerOptions()
                .position(startPosition)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_pin))
                .title("Hello world"));

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 6000;
        final boolean hideMarker = false;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                LatLng currentPosition = new LatLng(
                        startPosition.latitude*(1-t)+finalPosition.latitude*t,
                        startPosition.longitude*(1-t)+finalPosition.longitude*t);

                myMarker.setPosition(currentPosition);

                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        myMarker.setVisible(false);
                    } else {
                        myMarker.setVisible(true);
                    }
                }
            }
        });

    }

    private void openDialogFindRoute() {

        final Dialog dialog = new Dialog(CFTLocatorActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.destibation_point_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Conformation");

        final EditText edt_source=dialog.findViewById(R.id.edt_source);
        final EditText edt_destination=dialog.findViewById(R.id.edt_destination);
        Button btn_find_destination=dialog.findViewById(R.id.btn_find_destination);

        if (!user_address.equals("")){
            edt_source.setText(user_address);
        }

        btn_find_destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!edt_destination.getText().toString().equals("")){
                    LatLng destination=getLocationFromAddress(edt_destination.getText().toString());

                   if (destination!=null){
                       final Dialog myLoader = Global.showDialog(CFTLocatorActivity.this);
                       myLoader.show();
                       myLoader.setCanceledOnTouchOutside(true);
                       findRouteMap(destination);
                       myLoader.dismiss();
                       dialog.dismiss();
                   }else{
                       Toast.makeText(CFTLocatorActivity.this, "Please Enter valid location", Toast.LENGTH_SHORT).show();
                   }

                }
            }
        });


        dialog.show();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public LatLng getLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng dest = null;

        try {
            address = coder.getFromLocationName(strAddress,5);
            if (address==null) {
                return null;
            }
            Address location=address.get(0);

            if (location!=null){
                location.getLatitude();
                location.getLongitude();
                dest=new LatLng(location.getLatitude(),location.getLongitude());
                Log.d(Global.TAG, "getLocationFromAddress: Dest: "+dest);
                return dest;
            }else{
                Toast.makeText(CFTLocatorActivity.this, "InValid Location..!", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(Global.TAG, "getLocationFromAddress: Exc:"+e);
            Toast.makeText(CFTLocatorActivity.this, "InValid Location..!", Toast.LENGTH_SHORT).show();
        }
        return dest;
    }


    private void searchByUser(String user_id) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
            paramObj.put("searchUserId", user_id);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "searchByUser: " + paramObj.toString());
        final Dialog myLoader = Global.showDialog(CFTLocatorActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<GetCFTLocations> call = service.search_by_user(paramObj.toString());
        call.enqueue(new Callback<GetCFTLocations>() {
            @Override
            public void onResponse(Call<GetCFTLocations> call, Response<GetCFTLocations> response) {
                GetCFTLocations getCFTLocations = response.body();
                if (getCFTLocations != null) {
                    if (getCFTLocations.isSuccess()) {
                        List<UserLocation> userList = getCFTLocations.getResult();
                        Log.d(Global.TAG, "userList: " + userList.size());
                        if (userList.size() == 1) {
                            mMap.clear();
                            showCFTUser(userList,false);
                            /*UserLocation userLocation = userList.get(0);
                            String search_user_lat = userLocation.getUserLatitude();
                            String search_user_long = userLocation.getUserLongitude();
                            LatLng search_user_latlng = new LatLng(Double.parseDouble(search_user_lat), Double.parseDouble(search_user_long));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(search_user_latlng));*/

                            sv_search_location.setText("");
                            search_user_id="";
                            //ll_search_location.setVisibility(View.GONE);

                        }
                    } else {
                        Toast.makeText(CFTLocatorActivity.this, "No User Found..!", Toast.LENGTH_SHORT).show();
                    }
                }

                myLoader.dismiss();

            }

            @Override
            public void onFailure(Call<GetCFTLocations> call, Throwable t) {
                myLoader.dismiss();
                Toast.makeText(CFTLocatorActivity.this, "No User Found..!", Toast.LENGTH_SHORT).show();
                Log.d(Global.TAG, "onFailure: searchUser: " + t);
            }
        });
    }

    public void openDialogDisplayAlert() {
        Button btn_upgrade_dissmiss, btn_upgrade_ok;
        final Dialog dialog = new Dialog(CFTLocatorActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.upgrade_package_alert_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Confirmation");

        btn_upgrade_dissmiss = (Button) dialog.findViewById(R.id.btn_upgrade_dissmiss);
        // btn_upgrade_ok=(Button) dialog.findViewById(R.id.btn_upgrade_ok);

        btn_upgrade_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                dialog.dismiss();
            }
        });

       /* btn_upgrade_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CFTLocatorActivity.this,SignUpActivity.class));
                finish();
                dialog.dismiss();
            }
        });*/

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }

    private void getCFTStatus() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getCFTStatus: " + paramObj.toString());
        final Dialog myLoader = Global.showDialog(CFTLocatorActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call = service.getCFTStatus(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                if (response.isSuccessful()){
                    JsonResult jsonResult = response.body();
                    if (jsonResult.isSuccess()) {
                        Log.d(Global.TAG, "onResponse: Status: " + jsonResult.getResult());

                        disableFlag = jsonResult.getCftDisplayFlagAll();
                        Log.d(Global.TAG, "getCFTStatus disableFlag: " + disableFlag);

                        if (jsonResult.getResult().equals("1")) {
                            sw_cft_status.setOnCheckedChangeListener(null);
                            sw_cft_status.setChecked(true);
                            sw_cft_status.setText("Available ");
                            sw_cft_status.setOnCheckedChangeListener(CFTLocatorActivity.this);
                        } else if (jsonResult.getResult().equals("2")) {
                            sw_cft_status.setOnCheckedChangeListener(null);
                            sw_cft_status.setChecked(false);
                            sw_cft_status.setText("Not Available ");
                            sw_cft_status.setOnCheckedChangeListener(CFTLocatorActivity.this);
                        }
                    }
                }

                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:getCFTStatus " + t);
            }
        });
    }

    private void updateCFTStatus() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
            paramObj.put("cftActiveStatus", cftActiveStatus);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "updateCFTStatus: " + paramObj.toString());
        final Dialog myLoader = Global.showDialog(CFTLocatorActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call = service.updateCftStatus(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult = response.body();
                if (jsonResult.isSuccess()) {
                    Toast.makeText(CFTLocatorActivity.this, "" + jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    /*if (cftActiveStatus==1){
                        dashboardActivity.sw_cft_status_dash.setChecked(true);
                    }else  if (cftActiveStatus==2){
                        dashboardActivity.sw_cft_status_dash.setChecked(false);
                    }*/

                } else {
                    Toast.makeText(CFTLocatorActivity.this, "" + jsonResult.getResult(), Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:updateCFTStatus " + t);
            }
        });
    }

    private void findLocation(final String query) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
            paramObj.put("searchlocation", query);
            paramObj.put("cftActive", "yes");
            paramObj.put("cftLicense", "");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "findLocation: " + paramObj.toString());
        final Dialog myLoader = Global.showDialog(CFTLocatorActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<GetCFTLocations> call = service.findLocation(paramObj.toString());
        call.enqueue(new Callback<GetCFTLocations>() {
            @Override
            public void onResponse(Call<GetCFTLocations> call, Response<GetCFTLocations> response) {
                GetCFTLocations getCFTLocations = response.body();
                if (getCFTLocations.isSuccess()) {
                    List<UserLocation> userLocationList = getCFTLocations.getResult();
                    List<CFTOffice> cftOfficeList = getCFTLocations.getCftOffices();
                    Log.d(Global.TAG, "userLocationList: " + userLocationList.size());
                    Log.d(Global.TAG, "cftOfficeList: " + cftOfficeList.size());
                    if (cftOfficeList.size() > 0) {
                        showCFTOffice(cftOfficeList);
                    }

                    if (userLocationList.size() > 0) {
                        showCFTUser(userLocationList, false);
                    }


                } else {
                    Toast.makeText(CFTLocatorActivity.this, "No Users Available..!", Toast.LENGTH_SHORT).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetCFTLocations> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:GetCFTLocations :" + t);
                // Toast.makeText(CFTLocatorActivity.this, "No Details Found..!", Toast.LENGTH_SHORT).show();
                searchLocation(query);
            }
        });
    }

    private void getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CFTLocatorActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(CFTLocatorActivity.this, "Permission was granted!", Toast.LENGTH_LONG).show();
                    mGoogleApiClient.connect();
                } else {
                    Toast.makeText(CFTLocatorActivity.this, "Permission denied!", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void getAllCFTLocations() {
        Log.d(Global.TAG, "zoom: "+zoom);
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
            paramObj.put("userLatitude", user_lat);
            paramObj.put("userLongitude", user_long);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getAllCFTLocations: " + paramObj.toString());
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<GetCFTLocations> call = service.getAllUsersLocation(paramObj.toString());
        call.enqueue(new Callback<GetCFTLocations>() {
            @Override
            public void onResponse(Call<GetCFTLocations> call, Response<GetCFTLocations> response) {
                if (response.isSuccessful()){

                    GetCFTLocations cftLocations = response.body();
                    if (cftLocations.isSuccess()) {
                        locationsList = cftLocations.getResult();
                        cftOfficeList = cftLocations.getCftOffices();


                        Log.d(Global.TAG, "locationsList: " + locationsList.size());
                        Log.d(Global.TAG, "Offices List: " + cftOfficeList.size());

                        if (cftOfficeList.size() > 0) {
                            showCFTOffice(cftOfficeList);
                        } else {
                            Toast.makeText(CFTLocatorActivity.this, "No Offices Available...", Toast.LENGTH_SHORT).show();
                        }

                        if (locationsList.size() > 0) {
                            showCFTUser(locationsList, true);
                        } else {
                            Toast.makeText(CFTLocatorActivity.this, "No Users Available...", Toast.LENGTH_SHORT).show();
                        }

                        //redrawLine();

                        user_names_list = new String[locationsList.size()];
                        user_ids_list = new String[locationsList.size()];
                        for (int i = 0; i < locationsList.size(); i++) {
                            user_names_list[i] = locationsList.get(i).getFirst_name() + " " + locationsList.get(i).getLast_name();
                            user_ids_list[i] = locationsList.get(i).getUserId();
                        }
                        Log.d(Global.TAG, "user_names_list: " + user_names_list.length);
                        Log.d(Global.TAG, "user_ids_list: " + user_ids_list.length);

                        autoCompleteAdapter = new ArrayAdapter<String>
                                (CFTLocatorActivity.this, R.layout.dropdown_textview_layout, user_names_list);
                        sv_search_location.setAdapter(autoCompleteAdapter);

                    } else {
                        Log.d(Global.TAG, "Error in getting locations.");
                    }
                }

            }

            @Override
            public void onFailure(Call<GetCFTLocations> call, Throwable t) {
                Log.d(Global.TAG, "Error GetCFTLocations :onFailure " + t);
            }
        });

    }

    private void updateLocation() {
        Log.d(Global.TAG, "updateLocation: ");
        try {
            getAddressDetails();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            JSONObject paramObj = new JSONObject();
            try {
                paramObj.put("userId", spLib.getPref(SPLib.Key.USER_ID));
                paramObj.put("platform", "2");
                paramObj.put("userLatitude", user_lat);
                paramObj.put("userLongitude", user_long);
                paramObj.put("userAddress", user_address);
                paramObj.put("userCity", user_city);
                paramObj.put("userState", user_state);
                paramObj.put("userCountry", user_country);
                paramObj.put("userZipcode", zipcode);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Log.d(Global.TAG, "updateLocation: " + paramObj.toString());
            Log.d(Global.TAG, "updateLocation: ");
            APIService service = APIClient.getRetrofit().create(APIService.class);
            Call<JsonResult> call = service.update_location(paramObj.toString());
            call.enqueue(new Callback<JsonResult>() {
                @Override
                public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                    if (response.isSuccessful()){
                        JsonResult ack = response.body();
                        if (ack.isSuccess()) {
                            Log.d(Global.TAG, "Location Updated..: ");
                            zoom = mMap.getCameraPosition().zoom;
                            Log.d(Global.TAG, "zoom: "+zoom);
                            getAllCFTLocations();
                            // Toast.makeText(CFTLocatorActivity.this, "Location Updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(Global.TAG, "Location not Updated..: ");
                            //Toast.makeText(CFTLocatorActivity.this, " Location Error.", Toast.LENGTH_SHORT).show();
                        }
                    }

                }

                @Override
                public void onFailure(Call<JsonResult> call, Throwable t) {
                    Log.d(Global.TAG, "updateLocation: Exception " + t);
                }
            });


        }

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

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(CFTLocatorActivity.this);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


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
        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerClickListener(CFTLocatorActivity.this);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
       // directionFlag = false;
        //btn_find_route.setVisibility(View.GONE);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        /*if (directionFlag) {
            btn_find_route.setVisibility(View.VISIBLE);
        } else {
            btn_find_route.setVisibility(View.GONE);
        }*/

        //  mMap.setMinZoomPreference(11);

       /* mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Log.d(Global.TAG, "onMyLocationButtonClick: ");
               *//* String uri = "geo:" + user_lat + ","
                        +user_long + "?q=" + user_lat
                        + "," + user_long;
                startActivity(new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse(uri)));*//*


                String uri = "http://maps.google.com/maps?addr=" +user_lat+","+user_long;
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String ShareSub = "Here is my location";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ShareSub);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));


                return false;
            }
        });
*/


        btn_share_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = "http://maps.google.com/maps?addr=" +user_lat+","+user_long;
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String ShareSub = "Here is my location";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ShareSub);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

       /* mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {

               *//* // Already two locations
                if (markerPoints.size() > 1) {
                    markerPoints.clear();
                    mMap.clear();
                }
*//*
                // Adding new item to the ArrayList
                markerPoints.add(point);

                Log.d(Global.TAG, "markerPoints size: "+markerPoints.size());
                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(point);

                *//**
         * For the start location, the color of marker is GREEN and
         * for the end location, the color of marker is RED.
         *//*
                if (markerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (markerPoints.size() == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }


                // Add new marker to the Google Map Android API V2
                mMap.addMarker(options);

                // Checks, whether start and end locations are captured
                if (markerPoints.size() >= 2) {
                    Log.d(Global.TAG, "markerPoints size>2: "+markerPoints.size());
                    LatLng origin = new LatLng(user_lat,user_long);
                    LatLng dest = markerPoints.get(0);

                    // Getting URL to the Google Directions API
                    String url = getUrl(origin, dest);
                    Log.d("onMapClick", url.toString());
                    FetchUrl FetchUrl = new FetchUrl();

                    // Start downloading json data from Google Directions API
                    FetchUrl.execute(url);
                    //move map camera
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                }

            }
        });*/

    }

    private void findRoute() {
        // Checks, whether start and end locations are captured
        // if (MarkerPoints.size() >= 2) {
        LatLng origin = new LatLng(user_lat, user_long);
        LatLng dest = new LatLng(user_lat, user_long);

        // Getting URL to the Google Directions API
        String url = getUrl(origin, dest);
        Log.d("mytag", url.toString());
        FetchUrl FetchUrl = new FetchUrl();

        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        //  }
    }

    private String getUrl(LatLng origin, LatLng dest) {
        Log.d(Global.TAG, "getUrl: Begin ");

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";
        String key = "key=AIzaSyChOSkTylH3iJnhicjJbiZBqZObVVU3GrE";

        // Building the parameters to the web service
        //String parameters = str_origin + "&" + str_dest + "&" + sensor;
        String parameters = str_origin + "&" + str_dest + "&";

        // Output format
        String output = "json";

        // Building the url to the web service
       // String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
       // https://maps.googleapis.com/maps/api/directions/json?origin=43.65077%2C-79.378425&destination=43.63881%2C-79.42745&key=VALID_API_KEY

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?"+parameters+key;
        Log.d(Global.TAG, "getUrl: end");
        return url;
    }

    private void showCFTUser(final List<UserLocation> userLocationList, boolean currentLocation) {

        CFTuserMarkers.clear();
        /*final Dialog myLoader = Global.showDialog(CFTLocatorActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);*/
        for (int i = 0; i < userLocationList.size(); i++) {
            // Log.d(Global.TAG, "Loop: "+i);
            String latitide = userLocationList.get(i).getUserLatitude();
            String longitude = userLocationList.get(i).getUserLongitude();

            Log.d(Global.TAG, "showCFTUser: Active Status:"+userLocationList.get(i).getUserCftActiveStatus());
            BitmapDescriptor icon;
            if (userLocationList.get(i).getUserCftActiveStatus().equals("1")) {
                icon = BitmapDescriptorFactory.fromResource(R.drawable.green_pin);
            } else {
                icon = BitmapDescriptorFactory.fromResource(R.drawable.red_pin);
            }

            LatLng user_location = new LatLng(Double.parseDouble(latitide), Double.parseDouble(longitude));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(user_location)
                    .title(userLocationList.get(i).getFirst_name() + " " + userLocationList.get(i).getLast_name())
                    .icon(icon);
//               mMap.addMarker(new MarkerOptions().position(user_location).title(userLocationList.get(i).getFirst_name()+" "+userLocationList.get(i).getLast_name()).icon(icon));
//               mMap.moveCamera(CameraUpdateFactory.newLatLng(user_location));

            final UserLocation userInfo = userLocationList.get(i);
            double distance = calculateDistance(userInfo.getUserLatitude(), userInfo.getUserLongitude());
            userInfo.setDistance(String.valueOf(distance));
            InfoWindow customInfoWindow = new InfoWindow(CFTLocatorActivity.this, CFTuserMarkers);
            mMap.setInfoWindowAdapter(customInfoWindow);

            Marker m = mMap.addMarker(markerOptions);
            m.setTag(userInfo);
            CFTuserMarkers.add(m.getId());
            // m.showInfoWindow();

            mMap.animateCamera(CameraUpdateFactory.zoomTo(8));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(user_location));

            // mMap.setOnInfoWindowClickListener(CFTLocatorActivity.this);

            final int finalI = i;
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    markerPoints.clear();
                   // directionFlag = true;
                    LatLng position = marker.getPosition();

                    lastClicked = new LatLng(position.latitude, position.longitude);
                    //mMap.clear();
                    // getAllCFTLocations();
                    //openDialogShowUserDetails(userLocationList.get(finalI));
                    // showDirection(userInfo);
                    //btn_find_route.setVisibility(View.VISIBLE);
                    mMap.setInfoWindowAdapter(new InfoWindow(CFTLocatorActivity.this, CFTuserMarkers));

                   /* if (marker.getTag().equals("Title")){
                        marker.setTitle("Your Destination");
                        Log.d(Global.TAG, "This is Destination Point: ");
                    }else{
                        mMap.setInfoWindowAdapter(new InfoWindow(CFTLocatorActivity.this, CFTuserMarkers));
                    }*/
                    return false;
                }
            });
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    //findRouteMap(latLng);
                   // btn_find_route.setVisibility(View.GONE);
                  //  directionFlag = false;
                }
            });
        }
        //myLoader.dismiss();
        if (currentLocation) {
            LatLng current_location = new LatLng(user_lat, user_long);
            mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(current_location));
        }


    }

    private void showCFTOffice(final List<CFTOffice> officeList) {
        mMap.clear();
        /*final Dialog myLoader = Global.showDialog(CFTLocatorActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);*/
        for (int i = 0; i < officeList.size(); i++) {
            // Log.d(Global.TAG, "Loop: "+i);
            String latitide = officeList.get(i).getCftOfficeLatitude();
            String longitude = officeList.get(i).getCftOfficeLongitude();

            BitmapDescriptor icon;
            if (officeList.get(i).getCftOfficeActive().equals("1")) {
                icon = BitmapDescriptorFactory.fromResource(R.drawable.cft_office);
            } else {
                icon = BitmapDescriptorFactory.fromResource(R.drawable.cft_office);
            }

            LatLng office_location = new LatLng(Double.parseDouble(latitide), Double.parseDouble(longitude));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(office_location)
                    .title(officeList.get(i).getCftOfficeName())
                    .icon(icon);
//               mMap.addMarker(new MarkerOptions().position(user_location).title(userLocationList.get(i).getFirst_name()+" "+userLocationList.get(i).getLast_name()).icon(icon));
//               mMap.moveCamera(CameraUpdateFactory.newLatLng(user_location));

            final CFTOffice cftOffice = officeList.get(i);
            double distance = calculateDistance(cftOffice.getCftOfficeLatitude(), cftOffice.getCftOfficeLongitude());
            cftOffice.setCftOfficeDistance(String.valueOf(distance));

            Marker m = mMap.addMarker(markerOptions);
            m.setTag(cftOffice);
            // m.showInfoWindow();


            InfoWindow customInfoWindow = new InfoWindow(CFTLocatorActivity.this, CFTuserMarkers);
            mMap.setInfoWindowAdapter(customInfoWindow);

       /*     if (m.getTag().equals(cftOffice)){
                CFTOfficeInfo cftOfficeInfo = new CFTOfficeInfo(CFTLocatorActivity.this);
                mMap.setInfoWindowAdapter(cftOfficeInfo);
                Log.d(Global.TAG, "CFT OFFICE: ");
            }else{
                InfoWindow customInfoWindow = new InfoWindow(CFTLocatorActivity.this);
                mMap.setInfoWindowAdapter(customInfoWindow);
                Log.d(Global.TAG, "CFT USER: ");
            }*/

            mMap.animateCamera(CameraUpdateFactory.zoomTo(8));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(office_location));

            //mMap.setOnInfoWindowClickListener(CFTLocatorActivity.this);

            final int finalI = i;
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    markerPoints.clear();
                    //directionFlag = true;
                    LatLng position = marker.getPosition();
                    lastClicked = new LatLng(position.latitude, position.longitude);
                    //mMap.clear();
                    // getAllCFTLocations();
                    //openDialogShowUserDetails(userLocationList.get(finalI));
                    // showDirection(userInfo);
                  //  btn_find_route.setVisibility(View.VISIBLE);
                    mMap.setInfoWindowAdapter(new InfoWindow(CFTLocatorActivity.this, CFTuserMarkers));
                    return false;
                }
            });
          /*  mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    //findRouteMap(latLng);
                    btn_find_route.setVisibility(View.GONE);
                    directionFlag=false;
                }
            });*/
        }
       // myLoader.dismiss();
       /* if (currentLocation){
            LatLng current_location=new LatLng(user_lat,user_long);
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(current_location));
        }
*/

    }

    /*private void showDirection(UserLocation userInfo) {

        LatLng latLng = new LatLng(Double.parseDouble(userInfo.getUserLatitude()), Double.parseDouble(userInfo.getUserLongitude()));
        LatLng user_latlog=new LatLng(user_lat,user_long);

        distance_task.downloadUrl(user_latlog, latLng);
        distance_task.setLoadListener(new CalculateDistanceTime.taskCompleteListener() {
            @Override
            public void taskCompleted(String[] time_distance) {
                text1.setText(time_distance[0]); //Distance
                text2.setText(time_distance[1]); //Time
            }
        });
        String url = getDirectionsUrl(latLng, marker.getPosition());
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
    }
*/
    private double calculateDistance(String userLatitude, String userLongitude) {
        Location startPoint = new Location("A");
        startPoint.setLatitude(user_lat);
        startPoint.setLongitude(user_long);

        Location endPoint = new Location("B");
        endPoint.setLatitude(Double.parseDouble(userLatitude));
        endPoint.setLongitude(Double.parseDouble(userLongitude));

        double distance = startPoint.distanceTo(endPoint); // in meters
        // Log.d(Global.TAG, "calculateDistance:  "+distance);
        distance = distance / 1000; // in km
        double miles = 0.621371 * distance; //in miles
        return miles;

    }

    public void findRouteMap(LatLng point) {

        markerPoints.clear();
        markerPoints.add(point);

        Log.d(Global.TAG, "markerPoints size: " + markerPoints.size());
        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();
//        BitmapDescriptor icon;
//        icon = BitmapDescriptorFactory.fromResource(R.drawable.red_pin);
//        options.icon(icon);
//        options.title("Your Destination");
        // Setting the position of the marker
        options.position(point);
//        Marker dest_point=mMap.addMarker(options);
//        dest_point.setTag("Title");

        /**
         * For the start location, the color of marker is GREEN and
         * for the end location, the color of marker is RED.
         */
       /* if (markerPoints.size() == 1) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        } else if (markerPoints.size() == 2) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }*/


        // Add new marker to the Google Map Android API V2
        //mMap.addMarker(options);

        if (markerPoints.size() == 1) {
            Log.d(Global.TAG, "findRouteMap: size 1: ");
            LatLng origin = new LatLng(user_lat, user_long);
            LatLng dest = markerPoints.get(0);
            Log.d(Global.TAG, "findRouteMap: origin: " + origin);
            Log.d(Global.TAG, "findRouteMap: dest: " + dest);

            String url = getUrl(origin, dest);
            Log.d("mytag", url.toString());
            FetchUrl FetchUrl = new FetchUrl();

            // Start downloading json data from Google Directions API
            FetchUrl.execute(url);
            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(dest));
            //mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        } else if (markerPoints.size() > 1) {
            LatLng origin = new LatLng(user_lat, user_long);
            LatLng dest = markerPoints.get(markerPoints.size() - 1);

            String url = getUrl(origin, dest);
            Log.d("onMapClick", url.toString());
            FetchUrl FetchUrl = new FetchUrl();

            // Start downloading json data from Google Directions API
            FetchUrl.execute(url);
            //move map camera

            mMap.moveCamera(CameraUpdateFactory.newLatLng(dest));
            Log.d(Global.TAG, "findRouteMap: markerpoints " + markerPoints.size());
        }
       /* // Checks, whether start and end locations are captured
        if (markerPoints.size() >= 2) {
            Log.d(Global.TAG, "markerPoints size>2: "+markerPoints.size());
            LatLng origin = new LatLng(user_lat,user_long);
            LatLng dest = markerPoints.get(0);

            // Getting URL to the Google Directions API

        }
*/
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("mytag", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("mytag Exc:", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cft_locator_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_visible_to) {
            openDialogShowHideUsers();
        } else if (item.getItemId() == R.id.action_cft_locator_help) {
            Intent intent = new Intent(CFTLocatorActivity.this, OnboardingTextCampaign.class);
            Bundle bundle = new Bundle();
            bundle.putString("keyString", "cft_locator");
            intent.putExtras(bundle);
            startActivity(intent);
        } /*else if (item.getItemId() == R.id.action_search_cft) {
            if (ll_search_location.getVisibility() == View.VISIBLE) {
                ll_search_location.setVisibility(View.GONE);
            } else if (ll_search_location.getVisibility() == View.GONE) {
                ll_search_location.setVisibility(View.VISIBLE);
            }
        }*/

        return super.onOptionsItemSelected(item);
    }

    private void openDialogShowHideUsers() {
        final Dialog dialog = new Dialog(CFTLocatorActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.show_hide_cft_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Conformation");

        btn_user_dissmiss = (Button) dialog.findViewById(R.id.btn_user_dissmiss);
        sw_enable_all = (Switch) dialog.findViewById(R.id.sw_enable_all);
        rv_user_listshow = (RecyclerView) dialog.findViewById(R.id.rv_user_listshow);
        rv_user_listshow.setLayoutManager(new LinearLayoutManager(CFTLocatorActivity.this));

        sw_enable_all.setOnCheckedChangeListener(CFTLocatorActivity.this);
        getCFTStatusToChangeSelectAll(true);
        getUserListWithShowStatus();


        if (disableFlag.equals("1")) {
            sw_enable_all.setChecked(false);
            sw_enable_all.setText("Enable All");
        } else if (disableFlag.equals("2")) {
            sw_enable_all.setChecked(false);
            sw_enable_all.setText("Enable All");
        } else {
            sw_enable_all.setChecked(true);
            sw_enable_all.setText("Disable All");
        }

        btn_user_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

       /* sw_enable_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

            }
        });
*/
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void cftDisableToAll() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
            paramObj.put("disableFlag", disableFlag);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "cftDisableToAll: " + paramObj.toString());
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call = service.cftDisableToAll(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult = response.body();
                if (jsonResult != null) {
                    if (jsonResult.isSuccess()) {
                        Toast.makeText(CFTLocatorActivity.this, "" + jsonResult.getResult(), Toast.LENGTH_LONG).show();
                        getUserListWithShowStatus();
                    } else {
                        Toast.makeText(CFTLocatorActivity.this, "" + jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                Log.d(Global.TAG, "onFailure: cftDisableToAll:" + t);

            }
        });


    }

    private void getUserListWithShowStatus() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
            paramObj.put("userLatitude", user_lat);
            paramObj.put("userLongitude", user_long);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getUserListWithShowStatus: " + paramObj.toString());
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<GetCFTLocations> call = service.getCftInfoWithShowStatus(paramObj.toString());
        call.enqueue(new Callback<GetCFTLocations>() {
            @Override
            public void onResponse(Call<GetCFTLocations> call, Response<GetCFTLocations> response) {
                GetCFTLocations getCFTLocations = response.body();
                if (getCFTLocations.isSuccess()) {
                    List<UserLocation> userList = getCFTLocations.getResult();
                    Log.d(Global.TAG, "userList: " + userList.size());
                    if (userList.size() > 0) {
                        UserInfoAdapter adapter = new UserInfoAdapter(CFTLocatorActivity.this, userList, CFTLocatorActivity.this);
                        rv_user_listshow.setAdapter(adapter);
                    } else {
                        Log.d(Global.TAG, "No User Available..: ");
                    }
                } else {
                    Toast.makeText(CFTLocatorActivity.this, "" + getCFTLocations.getResult(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetCFTLocations> call, Throwable t) {
                Log.d(Global.TAG, "onFailure:getUserListWithShowStatus: " + t);
            }
        });

    }

    private void getModuleDetails() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        final Dialog myLoader = Global.showDialog(CFTLocatorActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        Log.d(Global.TAG, "getModuleDetails: " + paramObj.toString());
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<GetCustomizeModuleList> call = service.getModuleDetails(paramObj.toString());
        call.enqueue(new Callback<GetCustomizeModuleList>() {
            @Override
            public void onResponse(Call<GetCustomizeModuleList> call, Response<GetCustomizeModuleList> response) {
                GetCustomizeModuleList getCustomizeModuleList = response.body();
                if (getCustomizeModuleList != null) {
                    if (getCustomizeModuleList.isSuccess()) {
                        moduleList = getCustomizeModuleList.getResult();
                        Log.d(Global.TAG, "onResponse: Module List:" + moduleList.size());
                        moduleIds.clear();
                        for (int i = 0; i < moduleList.size(); i++) {
                            moduleIds.add(moduleList.get(i).getModuleId());
                        }
                        Log.d(Global.TAG, "moduleIds: " + moduleIds.size());
                        spLib.saveArrayList(moduleList, SPLib.Key.MODULELIST);
                        //moduleList=spLib.getArrayList(SPLib.Key.MODULELIST);
                        // Log.d(Global.TAG, "ModuleList from spLib: "+moduleList.size());
                        if (!moduleIds.contains("21")) {
                            openDialogDisplayAlert();
                        }
                    } else {
                        Toast.makeText(CFTLocatorActivity.this, "" + getCustomizeModuleList.getResult(), Toast.LENGTH_SHORT).show();
                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetCustomizeModuleList> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:getModuleDetails: " + t);
            }
        });

    }


    private void openDialogShowUserDetails(UserLocation userLocation) {
        Log.d(Global.TAG, "openDialogShowUserDetails: ");
        final Dialog dialog = new Dialog(CFTLocatorActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //d.setTitle("Select");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setContentView(R.layout.show_user_details_on_location);

        ImageView iv_user_pic = dialog.findViewById(R.id.iv_user_pic);
        TextView tv_user_name = dialog.findViewById(R.id.tv_user_name);
        TextView tv_user_email = dialog.findViewById(R.id.tv_user_email);
        TextView tv_user_phone = dialog.findViewById(R.id.tv_user_phone);
        // TextView tv_user_address=dialog.findViewById(R.id.tv_user_address);

        Picasso.with(CFTLocatorActivity.this)
                .load(userLocation.getProfile_pic())
                .resize(400, 400)
                .into(iv_user_pic);

        tv_user_name.setText(userLocation.getFirst_name() + " " + userLocation.getLast_name());
        tv_user_email.setText(userLocation.getEmail());
        tv_user_phone.setText(userLocation.getPhone());
        //tv_user_address.setText(userLocation.getUserAddress());


        dialog.show();
    }

    private void init() {
        dashboardActivity=new DashboardActivity();
        sv_search_location = findViewById(R.id.sv_search_location);
        ll_search_location = findViewById(R.id.ll_search_location);
        btn_search_cft = findViewById(R.id.btn_search_cft);
        ib_close_search = findViewById(R.id.ib_close_search);
        ib_cft_search = findViewById(R.id.ib_cft_search);
       // moving_points = new ArrayList<LatLng>();

        //btn_search_location = findViewById(R.id.btn_search_location);
        // sv_search_location.setIconified(false);
        //sv_search_location.setFocusable(false);
        //sv_search_location.clearFocus();
        //sv_search_location.setQueryHint("Search Location");
        ib_location_back = findViewById(R.id.ib_location_back);
        rb_search_location = findViewById(R.id.rb_search_location);
        rbg_search_by = findViewById(R.id.rbg_search_by);
        rb_search_user = findViewById(R.id.rb_search_user);
        spLib = new SPLib(CFTLocatorActivity.this);
        locationsList = new ArrayList<>();
        markerPoints = new ArrayList<>();
        sw_cft_status = findViewById(R.id.sw_cft_status);
        sw_cft_status.setOnCheckedChangeListener(this);
        btn_find_route = (ImageButton) findViewById(R.id.btn_find_route);
        btn_share_location = (ImageButton) findViewById(R.id.btn_share_location);
        is_tracking = spLib.getPrefBoolean(SPLib.Key.IS_TRACKING);
        Log.d(Global.TAG, "init: Is Tracking:" + is_tracking);


        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(900000)        // 10 seconds, in milliseconds
                .setFastestInterval(90000) // 1 second, in milliseconds
                .setSmallestDisplacement(10);

        ib_location_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        Log.d(Global.TAG, "Location Service Started..");
        //Start location sharing service to app server.........
        if (is_tracking) {
            Intent intent = new Intent(this, UpdateService.class);
            startService(intent);
        }


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(Global.TAG, "onConnected: ");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(Global.TAG, "onConnected: Permission not granted");
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
        Log.d(Global.TAG, "onConnected: loc" + location);

        if (location == null) {
            if (ActivityCompat.checkSelfPermission(CFTLocatorActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CFTLocatorActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

            starting_point=new LatLng(user_lat,user_long);


            // updateLocation();
            Log.d(Global.TAG, "onConnected: " + user_lat);
            Log.d(Global.TAG, "onConnected: " + user_long);
            /*try {
                getAddressDetails();
            } catch (IOException e) {
                Log.d(Global.TAG, "getAddressDetails: Exc: "+e);
                e.printStackTrace();
            }*/
            updateLocation();
            getAllCFTLocations();
            // findLocation();
        }
    }

    private void getAddressDetails() throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        addresses = geocoder.getFromLocation(user_lat, user_long, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        user_address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        user_city = addresses.get(0).getLocality();
        user_state = addresses.get(0).getAdminArea();
        user_country = addresses.get(0).getCountryName();
        zipcode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

        Log.d(Global.TAG, "getAddressDetails: address:" + user_address);
        Log.d(Global.TAG, "getAddressDetails: city:" + user_city);
        Log.d(Global.TAG, "getAddressDetails: state:" + user_state);
        Log.d(Global.TAG, "getAddressDetails: country:" + user_country);
        Log.d(Global.TAG, "getAddressDetails: Zipcode:" + zipcode);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(Global.TAG, "onConnectionSuspended: ");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(Global.TAG, "onConnectionFailed: ");
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
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.d(Global.TAG, "onConnectionFailed: ");
        if (location == null) {
            if (ActivityCompat.checkSelfPermission(CFTLocatorActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CFTLocatorActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    @Override
    public void onLocationChanged(Location location) {
        Log.d(Global.TAG, "onLocationChanged: ");
        user_lat = location.getLatitude();
        user_long = location.getLongitude();
      /*  LatLng latLng = new LatLng(user_lat, user_long); //you already have this
        moving_points.add(latLng); //added

        redrawLine();*/
    }
   /* private void redrawLine(){

       // mMap.clear();  //clears all Markers and Polylines
        Log.d(Global.TAG, "redrawLine: "+moving_points.size());
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for (int i = 0; i < moving_points.size(); i++) {
            LatLng point = moving_points.get(i);
            options.add(point);
            moving_line = mMap.addPolyline(options);
        }
        //addMarker(); //add Marker in current position
        moving_line = mMap.addPolyline(options); //add Polyline
    }*/
    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
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
    public void onInfoWindowClick(Marker marker) {
        Log.d(Global.TAG, "onInfoWindowClick: ");
        LatLng position = marker.getPosition(); //
        Log.d(Global.TAG, "onInfoWindowClick: marker latlong:" + position.latitude + " " + position.longitude);
        LatLng latLng = new LatLng(position.latitude, position.longitude);
        findRouteMap(latLng);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        if (compoundButton == sw_cft_status) {
            if (checked) {
                cftActiveStatus = 1;
                sw_cft_status.setText("Available");
            } else {
                cftActiveStatus = 2;
                sw_cft_status.setText("Not Available");
            }

            updateCFTStatus();
        } else if (compoundButton == sw_enable_all) {
            if (checked) {
                disableFlag = "0";
                sw_enable_all.setText("Disable All ");
            } else {
                disableFlag = "1";
                sw_enable_all.setText("Enable All");
            }
            cftDisableToAll();
        }

    }

    @Override
    public void refreshusercftList() {
        getUserListWithShowStatus();
    }

    @Override
    public void changeSelectAll() {
        getCFTStatusToChangeSelectAll(false);

    }

    private void getCFTStatusToChangeSelectAll(final boolean change_All) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getCFTStatus: " + paramObj.toString());
        final Dialog myLoader = Global.showDialog(CFTLocatorActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call = service.getCFTStatus(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult = response.body();
                if (jsonResult.isSuccess()) {
                    Log.d(Global.TAG, "onResponse: Status: " + jsonResult.getResult());

                    disableFlag = jsonResult.getCftDisplayFlagAll();
                    Log.d(Global.TAG, "getCFTStatus disableFlag: " + disableFlag);

                    if (change_All) {
                        if (disableFlag.equals("1")) {
                            // sw_enable_all.setOnCheckedChangeListener(null);
                            sw_enable_all.setChecked(false);
                            sw_enable_all.setText("Enable All");
                            // sw_enable_all.setOnCheckedChangeListener(CFTLocatorActivity.this);
                        } else if (disableFlag.equals("2")) {
                            // sw_enable_all.setOnCheckedChangeListener(null);
                            sw_enable_all.setChecked(false);
                            sw_enable_all.setText("Enable All");
                            //sw_enable_all.setOnCheckedChangeListener(CFTLocatorActivity.this);
                        } else {
                            // sw_enable_all.setOnCheckedChangeListener(null);
                            sw_enable_all.setChecked(true);
                            sw_enable_all.setText("Disable All");
                            //  sw_enable_all.setOnCheckedChangeListener(CFTLocatorActivity.this);
                        }

                    } else {
                        if (disableFlag.equals("1")) {
                            sw_enable_all.setOnCheckedChangeListener(null);
                            sw_enable_all.setChecked(false);
                            sw_enable_all.setText("Enable All");
                            sw_enable_all.setOnCheckedChangeListener(CFTLocatorActivity.this);
                        } else if (disableFlag.equals("2")) {
                            sw_enable_all.setOnCheckedChangeListener(null);
                            sw_enable_all.setChecked(false);
                            sw_enable_all.setText("Enable All");
                            sw_enable_all.setOnCheckedChangeListener(CFTLocatorActivity.this);
                        } else {
                            sw_enable_all.setOnCheckedChangeListener(null);
                            sw_enable_all.setChecked(true);
                            sw_enable_all.setText("Disable All");
                            sw_enable_all.setOnCheckedChangeListener(CFTLocatorActivity.this);
                        }
                    }


                  /*  if(jsonResult.getResult().equals("1")){
                        sw_cft_status.setOnCheckedChangeListener(null);
                        sw_cft_status.setChecked(true);
                        sw_cft_status.setText("Available ");
                        sw_cft_status.setOnCheckedChangeListener(CFTLocatorActivity.this);
                    }else  if(jsonResult.getResult().equals("2")){
                        sw_cft_status.setOnCheckedChangeListener(null);
                        sw_cft_status.setChecked(false);
                        sw_cft_status.setText("Not Available ");
                        sw_cft_status.setOnCheckedChangeListener(CFTLocatorActivity.this);
                    }*/
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:getCFTStatus " + t);
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(Global.TAG, "onMarkerClick: ");
        CFTOffice cftOffice = new CFTOffice();
        UserLocation userLocation = new UserLocation();

        if (marker.getTag().equals(cftOffice)) {
            Log.d(Global.TAG, "This is CFT Office : ");
        } else if (marker.getTag().equals(userLocation)) {
            Log.d(Global.TAG, "This is CFT User : ");
        } else {
            Log.d(Global.TAG, "Obj not matched: ");
        }

        return false;
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("mytag", "Background Task data" + data.toString());
            } catch (Exception e) {
                Log.d("mytag", "Background Task" + e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("mytag", "ParserTask" + jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("mytag", "ParserTask" + "Executing routes");
                Log.d("mytag", "ParserTask" + routes.toString());

            } catch (Exception e) {
                Log.d("mytag Exc:", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;


            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();
                //polyLine=new Polyline(lineOptions);


                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                Log.d("onPostExecute", "onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                //mMap.addPolyline(lineOptions);
                if (isSecond) {
                    Log.d(Global.TAG, "Polyline already added true: ");
                    polyLine.remove();
                    polyLine = mMap.addPolyline(lineOptions);
                } else {
                    Log.d(Global.TAG, "Polyline not already added false: ");
                    polyLine = mMap.addPolyline(lineOptions);
                    isSecond = true;
                }
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }

    public void searchLocation(String location) {
        Log.d(Global.TAG, "searchLocation: ");
        List<Address> addressList = new ArrayList<>();

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(CFTLocatorActivity.this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
                if (addressList.size() > 0) {
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    //mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

                } else {
                    Toast.makeText(this, "No Result Found..!", Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
                Log.d(Global.TAG, "searchLocation: Exception: " + e);
            }

            // Toast.makeText(getApplicationContext(),address.getLatitude()+" "+address.getLongitude(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(Global.TAG, "onDestroy: ");
       //dashboardActivity.navigationView.getMenu().getItem(9).setChecked(false);
    }
}
