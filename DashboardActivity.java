package com.success.successEntellus.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.NodeApi;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;
import com.success.successEntellus.R;
import com.success.successEntellus.fragment.AddEditGoalsFragment;
import com.success.successEntellus.fragment.CFTDashboardFragment;
import com.success.successEntellus.fragment.CFTLocatorFragment;
import com.success.successEntellus.fragment.CalenderFragment;
import com.success.successEntellus.fragment.ChangeprofileFragment;
import com.success.successEntellus.fragment.DailyTopTenFragment;
import com.success.successEntellus.fragment.EarnCreditScoreFragment;
import com.success.successEntellus.fragment.EmailCampaignFragment;
import com.success.successEntellus.fragment.HelpFragment;
import com.success.successEntellus.fragment.HelpFragmentWithTab;
import com.success.successEntellus.fragment.MyContactFragment;
import com.success.successEntellus.fragment.MyContactFragmentNew;
import com.success.successEntellus.fragment.MyGroupsFragment;
import com.success.successEntellus.fragment.MyGroupsFragmentNew;
import com.success.successEntellus.fragment.MyRecruitsFragment;
import com.success.successEntellus.fragment.ScratchNoteFragment;
import com.success.successEntellus.fragment.TeamCampaignsfragment;
import com.success.successEntellus.fragment.TextCampaignFragment;
import com.success.successEntellus.fragment.TimeAnalysisFragment;
import com.success.successEntellus.fragment.UploadDocumentFragment;
import com.success.successEntellus.fragment.VisionBoardFragment;
import com.success.successEntellus.fragment.WeeklyGraphFragment;
import com.success.successEntellus.fragment.WeeklyTrackingFragment;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.MyValidator;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.GetCustomizeModuleList;
import com.success.successEntellus.model.GetReferralLink;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.model.SingleModule;
import com.success.successEntellus.model.SubscriptionDetails;
import com.success.successEntellus.model.UserProfile;
import com.success.successEntellus.model.UserProfileDetails;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION =1008;
    private static final int ACTION_SETTING = 900;
    SPLib spLib;
    Bundle bundle;
    Bundle extras;

    String profile_image;
    Bitmap profile_bmp;
    String user_id;
    public static boolean goalsetFlag=false;
    public static CircleImageView iv_nav_profile;
    public static Switch sw_cft_status_dash;
    private int cftActiveStatus;
    LocationManager manager;
    double user_lat, user_long;
    public static NavigationView navigationView;
    Location location;
    public GoogleApiClient mGoogleApiClient;
    public LocationRequest mLocationRequest;
    boolean is_tracking=false;
    List<SingleModule> moduleList=new ArrayList<>();
    List<SingleModule> myToolsIdsList=new ArrayList<>();
    List<String> moduleIds=new ArrayList<>();
    List<String> myToolsIds=new ArrayList<>();
    DrawerLayout drawer;
    private String user_address = "", user_city = "", user_state = "", user_country = "", zipcode = "";
    TextView tv_full_name,tv_emailId,tv_version;
    View action_view;
    ImageView iv_dots,iv_green_dots;
    final List<MenuItem> items=new ArrayList<>();
    Menu menu;
    String version="";
    String refreshedToken;
    List<EditText> emailEditList,phoneEditList;
    final List<String> emailList=new ArrayList<>();
    final List<String> phoneList=new ArrayList<>();
    boolean isGoalSuccessFromAPI=false;
    int my_crm_count=0,my_campaign_count=0,toolsCount=0;
    int add_edit_count=0,dashboard_count=0,calendar_count=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        spLib = new SPLib(DashboardActivity.this);
        bundle = ActivityOptionsCompat.makeCustomAnimation(DashboardActivity.this, android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
        user_id=spLib.getPref(SPLib.Key.USER_ID);



        //getProfileDetailsFoeCheckGoalSuccess();
        //Getting current version
        try {
            PackageInfo pInfo =getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        AppUpdater appUpdater = new AppUpdater(this)
                .setDisplay(Display.DIALOG)
                .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                .setTitleOnUpdateAvailable("Update Available")
                .setContentOnUpdateAvailable("A new version of SuccessEntellus is available. Download the latest update to get the latest enhancements.")
                .setButtonUpdate("Update now")
               // .setButtonUpdateClickListener(...)
	            .setButtonDismiss("Later")
               // .setButtonDismissClickListener(...)
	            .setButtonDoNotShowAgain(null)
                //.setButtonDoNotShowAgainClickListener(...)
                //.setIcon(R.drawable.ic_update) // Notification icon
                .setCancelable(false); // Dialog could not be dismissable

        appUpdater.start();;

        refreshedToken = FirebaseInstanceId.getInstance().getToken();

        if (refreshedToken!=null){
            Log.d(Global.TAG, "Refreshed token: " + refreshedToken);
            if (!refreshedToken.equals("")){
                updateToken(refreshedToken);
            }
        }


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu=navigationView.getMenu();

        for (int i=0;i<menu.size();i++){
            items.add(menu.getItem(i));
        }
        Log.d(Global.TAG, "onCreate: Items List:"+items.size());



        View hView = navigationView.getHeaderView(0);
        tv_full_name = (TextView) hView.findViewById(R.id.tv_full_name);
        tv_emailId = (TextView) hView.findViewById(R.id.tv_email);
        tv_version = (TextView) hView.findViewById(R.id.tv_version);
        iv_nav_profile= (CircleImageView) hView.findViewById(R.id.iv_user_profile);


        /*action_view=(ImageView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.nav_edit_goals));*/

        getModuleDetails();
        getProfileDetails();

       /* if (spLib.checkSharedPrefs(SPLib.Key.MODULELIST)){
            moduleList=spLib.getArrayList(SPLib.Key.MODULELIST);
        }else{
            Log.d(Global.TAG, "Module list not available in spLib:");
        }
*/
      /*  if (moduleList!=null){
            for(int i=0;i<moduleList.size();i++){
                moduleIds.add(moduleList.get(i).getModuleId());
            }
            Log.d(Global.TAG, "ModuleList from SPLIB: "+moduleList.size());
            Log.d(Global.TAG, "ModuleList Ids: "+moduleIds.size());
        }else{
            Log.d(Global.TAG, "Module List is null: ");
        }
*/


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getLocationPermission();
        }


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
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
        gpsalert();
       /* extras=getIntent().getExtras();
        if (extras!=null){
            onBoardFlag=extras.getBoolean("onboardFlag");
            Log.d(Global.TAG, "OnboardFlaag at Dashboard: "+onBoardFlag);
        }else{
            onBoardFlag=false;
        }
*/
       /* if (onBoardFlag){
            replaceFragments(new AddEditGoalsFragment(true));
        }else{
            if (moduleList!=null && moduleList.size()>0){
                if (moduleIds.contains("17")){
                    replaceFragments(new DashboardFragment());
                }else{
                    replaceFragments(new CalenderFragment());
                }
            }else{
                replaceFragments(new DashboardFragment());
                Log.d(Global.TAG, "ModuleList Not Get from API: ");
            }

        }*/

      /*  FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
//        navigationView.getMenu().setGroupVisible(R.id.gp_my_crm,false);
//        navigationView.getMenu().setGroupVisible(R.id.gp_my_campaigns,false);
//        navigationView.getMenu().setGroupVisible(R.id.gp_my_tools,false);


        //initNavigation();

    }
    public void updateToken(final String token)
    {

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
            paramObj.put("deviceId",refreshedToken );

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        final Dialog myLoader = Global.showDialog(DashboardActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        Log.d(Global.TAG,"update_token: "+paramObj.toString());
        try {
            APIService service= APIClient.getRetrofit().create(APIService.class);
            Call<JsonResult> call = service.update_device_token(paramObj.toString());
            call.enqueue(new Callback<JsonResult>() {
                @Override
                public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                    if (response.isSuccessful()) {
                        JsonResult jsonResult = response.body();
                        if (jsonResult.isSuccess()) {
                            //spLib.sharedpreferences.edit().putString(SPLib.Key.TOKEN, token).commit();
                            Log.d(Global.TAG,"Token updated Successfully");
                        }else{
                            Log.d(Global.TAG,"token error = "+jsonResult.getResult());
                        }
                    }
                    myLoader.dismiss();
                }

                @Override
                public void onFailure(Call<JsonResult> call, Throwable t) {
                    t.printStackTrace();
                    myLoader.dismiss();
                    Log.d(Global.TAG,"token onFailure"+t);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(Global.TAG,"token Excecption = "+ex.getMessage());
        }

    }
    private void setDots(String color,int item_id){

        LinearLayout ll_dots=(LinearLayout)navigationView.getMenu().findItem(item_id).getActionView();
        iv_dots = (ImageView) ll_dots.findViewById(R.id.iv_dots);

        if (color.equals("green")){
            iv_dots.setBackground(getResources().getDrawable(R.drawable.green_dot));
        }else if (color.equals("red")){
            iv_dots.setBackground(getResources().getDrawable(R.drawable.circle_solid));
        }

    }
    private void initNavigation() {
       //navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.getMenu().findItem(R.id.nav_dashboard).setVisible(false);
        //navigationView.getMenu().findItem(R.id.logout).setVisible(true);

        if (moduleList!=null){
            if (moduleList.size()>0){
                checkModuleAvailability(navigationView);
            }else{
                Log.d(Global.TAG, "Module List size is 0: ");
            }
        }


       /* View hView = navigationView.getHeaderView(0);
        TextView tv_full_name = (TextView) hView.findViewById(R.id.tv_full_name);
        TextView tv_emailId = (TextView) hView.findViewById(R.id.tv_email);
        iv_nav_profile= (CircleImageView) hView.findViewById(R.id.iv_user_profile);*/
        //sw_cft_status_dash= (Switch) hView.findViewById(R.id.sw_cft_status_dash);
        //navigationView.getMenu().getItem(1).setChecked(true);


        profile_image=spLib.getPref(SPLib.Key.PROFILE_IMAGE);
        if (profile_image!=null ){
            if (!profile_image.equals("")){
                Picasso.with(DashboardActivity.this)
                        .load(profile_image)
                        .placeholder(R.drawable.place)   // optional
                        .error(R.drawable.error)      // optional
                        .resize(400, 400)
                        .skipMemoryCache()
                        .into(iv_nav_profile);
            }
        }

        Menu menu = navigationView.getMenu();
        navigationView.setItemIconTintList(null);

        //getProfileDetails();
        Log.d(Global.TAG, "User Name: " + spLib.getPref(SPLib.Key.USER_NAME));
        tv_full_name.setText(spLib.getPref(SPLib.Key.USER_NAME));
        tv_emailId.setText(spLib.getPref(SPLib.Key.USER_EMAIL));

        tv_version.setText("Version "+version);
        Log.d(Global.TAG, " Version: "+version);

        if (profile_bmp!=null)
            iv_nav_profile.setImageBitmap(profile_bmp);

        navigationView.setNavigationItemSelectedListener(this);


    }


    private void checkModuleAvailability(NavigationView navigationView) {
        Log.d(Global.TAG, "checkModuleAvailability: Module List "+moduleIds);
        if (moduleIds.contains("17")){
            navigationView.getMenu().findItem(R.id.nav_dashboard).setVisible(true);
            setDots("green",R.id.nav_dashboard);
            Log.d(Global.TAG, "Dashboard Available: ");
        }else{
            Log.d(Global.TAG, "Dashboard Not Available: ");
            //navigationView.getMenu().findItem(R.id.nav_dashboard).setEnabled(false);
           // MenuItem menuItem=navigationView.getMenu().findItem(R.id.nav_dashboard);
            setDots("red",R.id.nav_dashboard);
            //setTextColorForMenuItem(menuItem, R.color.colorGrey);
        }

        if (moduleIds.contains("2")){
            navigationView.getMenu().findItem(R.id.nav_edit_goals).setVisible(true);
            setDots("green",R.id.nav_edit_goals);
        }else{
            //MenuItem menuItem=navigationView.getMenu().findItem(R.id.nav_edit_goals);
            //setTextColorForMenuItem(menuItem, R.color.colorGrey);
            setDots("red",R.id.nav_edit_goals);
        }

        if (moduleIds.contains("3")){
            navigationView.getMenu().findItem(R.id.nav_weekly_goal_graph).setVisible(true);
            setDots("green",R.id.nav_weekly_goal_graph);
        }else{
            //MenuItem menuItem=navigationView.getMenu().findItem(R.id.nav_weekly_goal_graph);
            //setTextColorForMenuItem(menuItem, R.color.colorGrey);
            setDots("red",R.id.nav_weekly_goal_graph);
        }

        if (moduleIds.contains("4")){
            navigationView.getMenu().findItem(R.id.nav_weekly_tracking).setVisible(true);
            setDots("green",R.id.nav_weekly_tracking);
        }else{
            //MenuItem menuItem=navigationView.getMenu().findItem(R.id.nav_weekly_tracking);
            //setTextColorForMenuItem(menuItem, R.color.colorGrey);
            setDots("red",R.id.nav_weekly_tracking);
           // navigationView.getMenu().findItem(R.id.nav_weekly_tracking).setVisible(false);
        }
       /* if (moduleIds.contains("5")){
            navigationView.getMenu().findItem(R.id.nav_calendar).setVisible(true);
            setDots("green",R.id.nav_calendar);
        }else{
            //MenuItem menuItem=navigationView.getMenu().findItem(R.id.nav_calendar);
            //setTextColorForMenuItem(menuItem, R.color.colorGrey);
            setDots("red",R.id.nav_calendar);
           // navigationView.getMenu().findItem(R.id.nav_calendar).setVisible(false);
        }*/
        //6 for time Analysis
      /*  if (moduleIds.contains("6")){
            navigationView.getMenu().findItem(R.id.nav_time_analysis).setVisible(true);
            setDots("green",R.id.nav_time_analysis);
        }else{
            //MenuItem menuItem=navigationView.getMenu().findItem(R.id.nav_time_analysis);
            //setTextColorForMenuItem(menuItem, R.color.colorGrey);
            setDots("red",R.id.nav_time_analysis);
            // navigationView.getMenu().findItem(R.id.nav_calendar).setVisible(false);
        }*/

        if (moduleIds.contains("7")){
            navigationView.getMenu().findItem(R.id.nav_my_groups).setVisible(true);
            setDots("green",R.id.nav_my_groups);
        }else{
            //MenuItem menuItem=navigationView.getMenu().findItem(R.id.nav_my_groups);
            //setTextColorForMenuItem(menuItem, R.color.colorGrey);
            setDots("red",R.id.nav_my_groups);
          //  navigationView.getMenu().findItem(R.id.nav_my_groups).setVisible(false);
        }
        if (moduleIds.contains("8")){
            navigationView.getMenu().findItem(R.id.nav_cft_dashboard).setVisible(true);
            setDots("green",R.id.nav_cft_dashboard);
        }else{
            //MenuItem menuItem=navigationView.getMenu().findItem(R.id.nav_cft_dashboard);
           // setTextColorForMenuItem(menuItem, R.color.colorGrey);
            setDots("red",R.id.nav_cft_dashboard);
            //navigationView.getMenu().findItem(R.id.nav_cft_dashboard).setVisible(false);
        }

        if (moduleIds.contains("9")){
            navigationView.getMenu().findItem(R.id.nav_email_campaign).setVisible(true);
            setDots("green",R.id.nav_email_campaign);
        }else{
            //MenuItem menuItem=navigationView.getMenu().findItem(R.id.nav_email_campaign);
            //setTextColorForMenuItem(menuItem, R.color.colorGrey);
            setDots("red",R.id.nav_email_campaign);
            //navigationView.getMenu().findItem(R.id.nav_email_campaign).setVisible(false);
        }
        if (moduleIds.contains("10")){
            navigationView.getMenu().findItem(R.id.nav_text_campaigns).setVisible(true);
            setDots("green",R.id.nav_text_campaigns);
        }else{
            //MenuItem menuItem=navigationView.getMenu().findItem(R.id.nav_text_campaigns);
            //setTextColorForMenuItem(menuItem, R.color.colorGrey);
            setDots("red",R.id.nav_text_campaigns);
           // navigationView.getMenu().findItem(R.id.nav_text_campaigns).setVisible(false);
        }
        if (moduleIds.contains("11")){
            navigationView.getMenu().findItem(R.id.nav_Contact_list).setVisible(true);
            setDots("green",R.id.nav_Contact_list);
        }else{
            //MenuItem menuItem=navigationView.getMenu().findItem(R.id.nav_Contact_list);
            //setTextColorForMenuItem(menuItem, R.color.colorGrey);
            setDots("red",R.id.nav_Contact_list);
            //navigationView.getMenu().findItem(R.id.nav_Contact_list).setVisible(false);
        }
        if (moduleIds.contains("12")){
            navigationView.getMenu().findItem(R.id.nav_customer_list).setVisible(true);
            setDots("green",R.id.nav_customer_list);
        }else{
            //MenuItem menuItem=navigationView.getMenu().findItem(R.id.nav_customer_list);
            //setTextColorForMenuItem(menuItem, R.color.colorGrey);
            setDots("red",R.id.nav_customer_list);
            //navigationView.getMenu().findItem(R.id.nav_customer_list).setVisible(false);
        }
        if (moduleIds.contains("13")){
            navigationView.getMenu().findItem(R.id.nav_prospect_list).setVisible(true);
            setDots("green",R.id.nav_prospect_list);
        }else{
            //MenuItem menuItem=navigationView.getMenu().findItem(R.id.nav_prospect_list);
            //setTextColorForMenuItem(menuItem, R.color.colorGrey);
            setDots("red",R.id.nav_prospect_list);
           // navigationView.getMenu().findItem(R.id.nav_prospect_list).setVisible(false);
        }
        if (moduleIds.contains("15")){
            navigationView.getMenu().findItem(R.id.nav_upload_document).setVisible(true);
            setDots("green",R.id.nav_upload_document);
        }else{
            //MenuItem menuItem=navigationView.getMenu().findItem(R.id.nav_upload_document);
            //setTextColorForMenuItem(menuItem, R.color.colorGrey);
            setDots("red",R.id.nav_upload_document);
           // navigationView.getMenu().findItem(R.id.nav_upload_document).setVisible(false);
        }
        if (moduleIds.contains("16")){
            navigationView.getMenu().findItem(R.id.nav_recruits).setVisible(true);
            setDots("green",R.id.nav_recruits);
        }else{
           // MenuItem menuItem=navigationView.getMenu().findItem(R.id.nav_recruits);
            //setTextColorForMenuItem(menuItem, R.color.colorGrey);
            setDots("red",R.id.nav_recruits);
           // navigationView.getMenu().findItem(R.id.nav_recruits).setVisible(false);
        }

        if (moduleIds.contains("21")){
            navigationView.getMenu().findItem(R.id.nav_cft_locator).setVisible(true);
            setDots("green",R.id.nav_cft_locator);
        }else{
            // MenuItem menuItem=navigationView.getMenu().findItem(R.id.nav_cft_locator);
            // setTextColorForMenuItem(menuItem, R.color.colorGrey);
            setDots("red",R.id.nav_cft_locator);
            //navigationView.getMenu().findItem(R.id.nav_cft_locator).setVisible(false);
        }

        Log.d(Global.TAG, "checkModuleAvailability: myToolsIds "+myToolsIds);

        if (myToolsIds.contains("25")){
            navigationView.getMenu().findItem(R.id.nav_daily_top_ten).setVisible(true);
        }else{
            navigationView.getMenu().findItem(R.id.nav_daily_top_ten).setVisible(false);
        }

        if (myToolsIds.contains("26")){
            navigationView.getMenu().findItem(R.id.nav_vision_board).setVisible(true);
            //setDots("green",R.id.nav_cft_locator);
        }else{
            navigationView.getMenu().findItem(R.id.nav_vision_board).setVisible(false);
        }

        if (myToolsIds.contains("27")){
            navigationView.getMenu().findItem(R.id.nav_scratch_pad).setVisible(true);

        }else{
            navigationView.getMenu().findItem(R.id.nav_scratch_pad).setVisible(false);
        }

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
        final Dialog myLoader = Global.showDialog(DashboardActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        Log.d(Global.TAG, "getModuleDetails: "+paramObj.toString());
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<GetCustomizeModuleList> call = service.getModuleDetails(paramObj.toString());
        call.enqueue(new Callback<GetCustomizeModuleList>() {
            @Override
            public void onResponse(Call<GetCustomizeModuleList> call, Response<GetCustomizeModuleList> response) {
                GetCustomizeModuleList getCustomizeModuleList=response.body();
                if (getCustomizeModuleList!=null){
                    if (getCustomizeModuleList.isSuccess()){
                        moduleList=getCustomizeModuleList.getResult();
                        Log.d(Global.TAG, "onResponse: Module List:"+moduleList.size());
                        moduleIds.clear();
                        for(int i=0;i<moduleList.size();i++){
                            moduleIds.add(moduleList.get(i).getModuleId());
                        }

                        myToolsIdsList=getCustomizeModuleList.getShowMenuList();
                        my_campaign_count=8+myToolsIdsList.size();
                        my_crm_count=14+myToolsIdsList.size();
                        toolsCount=myToolsIdsList.size();
                        Log.d(Global.TAG, "onResponse: myToolsIdsList List:"+myToolsIdsList.size());
                        Log.d(Global.TAG, "onResponse: my_campaign_count:"+my_campaign_count);
                        Log.d(Global.TAG, "onResponse: my_crm_count:"+my_crm_count);

                        for(int i=0;i<myToolsIdsList.size();i++){
                            moduleIds.add(myToolsIdsList.get(i).getModuleId());
                            myToolsIds.add(myToolsIdsList.get(i).getModuleId());
                            if (myToolsIdsList.get(i).getModuleName().equals("Calendar")){
                                calendar_count=i;
                                Log.d(Global.TAG, "calendar_count: "+calendar_count);
                            }
                        }



                        Log.d(Global.TAG, "moduleIds: "+moduleIds);
                        spLib.saveArrayList(moduleList,SPLib.Key.MODULELIST);



                        getFragmentToReplace();
                        initNavigation();

                        //moduleList=spLib.getArrayList(SPLib.Key.MODULELIST);
                        //Log.d(Global.TAG, "ModuleList from spLib: "+moduleList.size());

                        if (!moduleIds.contains("17")){
                            Log.d(Global.TAG, "Dashboard Not Available from API: ");
                        }
                    }else{
                        initNavigation();
                        getFragmentToReplace();
                        Toast.makeText(DashboardActivity.this, ""+getCustomizeModuleList.getResult(), Toast.LENGTH_SHORT).show();
                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetCustomizeModuleList> call, Throwable t) {
                myLoader.dismiss();
                initNavigation();
                getFragmentToReplace();
                Log.d(Global.TAG, "onFailure:getModuleDetails: "+t);
            }
        });
    }

    private void getFragmentToReplace() {

            if (moduleList!=null && moduleList.size()>0){
                getProfileDetailsFoeCheckGoalSuccess(); //Check GoalsetSuccessFlag
            }else{
                replaceFragments(new DashboardFragment());
                Log.d(Global.TAG, "ModuleList Not Get from API: ");
                if (navigationView!=null) {
                    navigationView.setCheckedItem(R.id.nav_dashboard);
                    //navigationView.getMenu().getItem(dashboard_count-1).setChecked(true);
                }
            }
    }

    public void openDialogDisplayAlert() {
        Button btn_upgrade_dissmiss,btn_upgrade_ok;
        final Dialog dialog = new Dialog(DashboardActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.upgrade_package_alert_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Confirmation");

        btn_upgrade_dissmiss=(Button) dialog.findViewById(R.id.btn_upgrade_dissmiss);
       // btn_upgrade_ok=(Button) dialog.findViewById(R.id.btn_upgrade_ok);

       /* btn_upgrade_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().getSupportFragmentManager().popBackStackImmediate();
                dialog.dismiss();
            }
        });

        btn_upgrade_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),SignUpActivity.class));
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                dialog.dismiss();
            }
        });*/

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DashboardActivity.this,
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
                    Toast.makeText(DashboardActivity.this, "Permission was granted!", Toast.LENGTH_LONG).show();
                    gpsalert();
                } else {
                    Toast.makeText(DashboardActivity.this, "Permission denied!", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void updateLocation() throws IOException {
        Log.d(Global.TAG, "updateLocation: ");
        getAddressDetails();

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

                    if(response.isSuccessful()){
                        JsonResult ack = response.body();
                        if (ack.isSuccess()) {
                            Log.d(Global.TAG, "Location Updated..: ");
                            //Toast.makeText(DashboardActivity.this, "Location Updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(Global.TAG, "Location not Updated..: ");
                            //  Toast.makeText(DashboardActivity.this, " Location Error.", Toast.LENGTH_SHORT).show();
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
            mGoogleApiClient.connect();
        }

    }

    private void buildAlertMessageNoGps() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DashboardActivity.this);
        builder.setMessage("Please Turn On Your GPS")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),ACTION_SETTING);
                    }
                });
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
//                        dialog.cancel();
//                    }
//                });
        final android.app.AlertDialog alert = builder.create();
        alert.show();
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
        final Dialog myLoader = Global.showDialog(DashboardActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call = service.getCFTStatus(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Log.d(Global.TAG, "getCFTStatus onResponse: Status: "+jsonResult.getResult());
                    if(jsonResult.getResult().equals("1")){
                        sw_cft_status_dash.setChecked(true);
                    }else  if(jsonResult.getResult().equals("2")){
                        sw_cft_status_dash.setChecked(false);
                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:getCFTStatus "+t);
            }
        });
    }

    private void updateCFTStatus() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
            paramObj.put("cftActiveStatus",cftActiveStatus);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "updateCFTStatus: " + paramObj.toString());
        final Dialog myLoader = Global.showDialog(DashboardActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call = service.updateCftStatus(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Log.d(Global.TAG, "onResponse: "+ Log.d(Global.TAG, "Status Updated..: "));
                    //Toast.makeText(DashboardActivity.this, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(DashboardActivity.this, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:updateCFTStatus "+t);
            }
        });
    }

    private void getProfileDetailsFoeCheckGoalSuccess() {

        Log.d(Global.TAG, "getProfileDetails: "+user_id);
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<UserProfileDetails> call = service.getProfileDetails(user_id,"2");
        call.enqueue(new Callback<UserProfileDetails>() {
            @Override
            public void onResponse(Call<UserProfileDetails> call, Response<UserProfileDetails> response) {
                if (response.isSuccessful()){
                    UserProfileDetails userProfileModel=response.body();
                    if (userProfileModel.isSuccess()) {
                        List<UserProfile> userDetails = userProfileModel.getResult();
                        String isGoalSetSuccess=userProfileModel.getGoalSuccessFlag();

                        if (isGoalSetSuccess.equals("false")){
                            isGoalSuccessFromAPI=false;
                        }else if (isGoalSetSuccess.equals("true")){
                            isGoalSuccessFromAPI=true;
                        }

                        if (isGoalSetSuccess.equals("false")){
                            Log.d(Global.TAG, "goalSuccess false: ");
                            if (moduleIds.contains("17")){
                                replaceFragments(new AddEditGoalsFragment(true));
                                if (navigationView!=null){
                                    navigationView.setCheckedItem(R.id.nav_edit_goals);
                                    //navigationView.getMenu().getItem(add_edit_count-1).setChecked(true);//Add Edit goals at 10th position.
                                }
                            }else{
                                replaceFragments(new CalenderFragment());
                                if (navigationView!=null) {
                                    navigationView.setCheckedItem(R.id.nav_calendar);
                                    //navigationView.getMenu().getItem(calendar_count-1).setChecked(true);//calendar at 5th position.
                                }
                            }

                        }else{
                            Log.d(Global.TAG, "goalSuccess true: ");
                            if (moduleIds.contains("17")){
                                replaceFragments(new DashboardFragment());
                                if (navigationView!=null) {
                                    navigationView.setCheckedItem(R.id.nav_dashboard);
                                    //navigationView.getMenu().getItem(dashboard_count-1).setChecked(true);// Dashboard at 7th
                                }
                            }else{
                                replaceFragments(new CalenderFragment());
                                if (navigationView!=null) {
                                    navigationView.setCheckedItem(R.id.nav_calendar);
                                   // navigationView.getMenu().getItem(calendar_count-1).setChecked(true);
                                }
                            }
                        }


                    }else{
                        Toast.makeText(DashboardActivity.this, "Error in getting Profile Details..!", Toast.LENGTH_SHORT).show();
                    }
                }

                // myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<UserProfileDetails> call, Throwable t) {
                //myLoader.dismiss();
                Log.d("mytag", "onFailure: getUserProfile:"+t);

            }
        });

    }


    private void getProfileDetails() {
       /* final Dialog myLoader = Global.showDialog(DashboardActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);*/
        Log.d(Global.TAG, "getProfileDetails: "+user_id);
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<UserProfileDetails> call = service.getProfileDetails(user_id,"2");
        call.enqueue(new Callback<UserProfileDetails>() {
            @Override
            public void onResponse(Call<UserProfileDetails> call, Response<UserProfileDetails> response) {
                if (response.isSuccessful()){
                    UserProfileDetails userProfileModel=response.body();
                    if (userProfileModel.isSuccess()) {
                        List<UserProfile> userDetails = userProfileModel.getResult();
                        Log.d(Global.TAG, "onResponse:userDetails " + userDetails.get(0).getProfile_pic());
                        spLib.sharedpreferences.edit().putString(SPLib.Key.PROFILE_IMAGE, userDetails.get(0).getProfile_pic()).commit();
                        spLib.sharedpreferences.edit().putString(SPLib.Key.BUSINESS_IMAGE, userDetails.get(0).getDreamImage()).commit();
                        spLib.sharedpreferences.edit().putString(SPLib.Key.PURPOSE, userDetails.get(0).getReason()).commit();
                        spLib.sharedpreferences.edit().putString(SPLib.Key.ADDRESS, userDetails.get(0).getUserAddress()).commit();
                        spLib.sharedpreferences.edit().putString(SPLib.Key.CITY, userDetails.get(0).getUserCity()).commit();
                        spLib.sharedpreferences.edit().putString(SPLib.Key.STATE, userDetails.get(0).getUserState()).commit();
                        spLib.sharedpreferences.edit().putString(SPLib.Key.COUNTRY, userDetails.get(0).getUserCountry()).commit();
                        spLib.sharedpreferences.edit().putString(SPLib.Key.ZIPCODE, userDetails.get(0).getUserZipcode()).commit();

                        if (!userDetails.get(0).getProfile_pic().equals("null")){
                            if (!userDetails.get(0).getProfile_pic().equals("")) {
                                Picasso.with(DashboardActivity.this)
                                        .load(userDetails.get(0).getProfile_pic())
                                        .placeholder(R.drawable.place)   // optional
                                        .error(R.drawable.error)      // optional
                                        .resize(400, 400)
                                        .skipMemoryCache()
                                        .into(iv_nav_profile);
                            }
                        }
                        String cft=userDetails.get(0).getUserCft();
                        if (cft.equals("1")){
                            spLib.sharedpreferences.edit().putBoolean(SPLib.Key.IS_TRACKING,true).commit();
                            Intent intent = new Intent(DashboardActivity.this, UpdateService.class);
                            startService(intent);
                        }else{
                            spLib.sharedpreferences.edit().putBoolean(SPLib.Key.IS_TRACKING,false).commit();
                        }

                    }else{
                        Toast.makeText(DashboardActivity.this, "Error in getting Profile Details..!", Toast.LENGTH_SHORT).show();
                    }
                }

               // myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<UserProfileDetails> call, Throwable t) {
                //myLoader.dismiss();
                Log.d("mytag", "onFailure: getUserProfile:"+t);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==ACTION_SETTING && resultCode==RESULT_OK){
            Log.d(Global.TAG, "onActivityResult: Alert: ");
            try {
                updateLocation();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
       /* DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
       /* new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DashboardActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();*/

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int fragments = getSupportFragmentManager().getBackStackEntryCount();
            if (fragments == 1) {
                // finish();
                new AlertDialog.Builder(this)
                        .setMessage("Are you sure you want to exit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                DashboardActivity.this.finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            } else {
                super.onBackPressed();

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(Global.TAG, "onResume: Dashboard: ");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(Global.TAG, "onCreateOptionsMenu: ");
        navigationView.getMenu().setGroupVisible(R.id.gp_my_crm,false);
        navigationView.getMenu().setGroupVisible(R.id.gp_my_campaigns,false);
       // navigationView.getMenu().setGroupVisible(R.id.gp_my_tools,false);
/*
        LinearLayout ll_dots=(LinearLayout)navigationView.getMenu().findItem(R.id.nav_dashboard).getActionView();
        iv_dots = (ImageView) ll_dots.findViewById(R.id.iv_dots);
        iv_dots.setBackground(getResources().getDrawable(R.drawable.green_dot));*/


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }
*/
        return super.onOptionsItemSelected(item);
    }

    private void setTextColorForMenuItem(MenuItem menuItem, @ColorRes int color) {
        SpannableString spanString = new SpannableString(menuItem.getTitle().toString());
        spanString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, color)), 0, spanString.length(), 0);
        menuItem.setTitle(spanString);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

      /*  if (!item.isCheckable()){ // if item is disabled
            Toast.makeText(this, "This is not include in your package..!", Toast.LENGTH_SHORT).show();
            Log.d(Global.TAG, "onNavigationItemSelected: ");
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }

        }else{*/ // if item is enabled
           /* if (moduleList!=null && moduleList.size()>0){
                if (id == R.id.nav_dashboard) {
                    if (moduleIds.contains("17")){
                        replaceFragments(new DashboardFragment());
                    }else{
                        startActivity(new Intent(DashboardActivity.this,ModuleNotAvailableActivity.class));
                    }
                } else if (id == R.id.nav_edit_goals) {
                    if (moduleIds.contains("2")) {
                        replaceFragments(new AddEditGoalsFragment(false));
                        setTitle("Add Edit Goals");
                    }else{
                        startActivity(new Intent(DashboardActivity.this,ModuleNotAvailableActivity.class));
                    }
                }else if (id == R.id.nav_weekly_goal_graph) {
                    if (moduleIds.contains("3")) {
                        replaceFragments(new WeeklyGraphFragment());
                    }else{
                        startActivity(new Intent(DashboardActivity.this,ModuleNotAvailableActivity.class));
                    }
                } else if (id == R.id.nav_weekly_tracking) {
                    if (moduleIds.contains("4")) {
                        replaceFragments(new WeeklyTrackingFragment());
                    }else{
                        startActivity(new Intent(DashboardActivity.this,ModuleNotAvailableActivity.class));
                    }
                }else if (id == R.id.nav_calendar) {
                    if (moduleIds.contains("5")) {
                        setTitle("Calendar");
                        replaceFragments(new CalenderFragment());
                    }else{
                        startActivity(new Intent(DashboardActivity.this,ModuleNotAvailableActivity.class));
                    }
                }else if (id == R.id.nav_my_groups) {
                    if (moduleIds.contains("7")) {
                        replaceFragments(new MyGroupsFragment());
                        setTitle("My Groups");
                    }else{
                        startActivity(new Intent(DashboardActivity.this,ModuleNotAvailableActivity.class));
                    }
                }else if (id == R.id.nav_cft_dashboard) {
                    if (moduleIds.contains("8")) {
                        replaceFragments(new CFTDashboardFragment());
                        setTitle("CFT Dashboard");
                    }else{
                        startActivity(new Intent(DashboardActivity.this,ModuleNotAvailableActivity.class));
                    }
                } else if (id == R.id.nav_email_campaign) {
                    if (moduleIds.contains("9")) {
                        replaceFragments(new EmailCampaignFragment());
                        setTitle("Email Campaigns");
                    }else{
                        startActivity(new Intent(DashboardActivity.this,ModuleNotAvailableActivity.class));
                    }
                }else if (id == R.id.nav_text_campaigns) {
                    if (moduleIds.contains("10")) {
                        replaceFragments(new TextCampaignFragment());
                        setTitle("Text Campaigns");
                    }else{
                        startActivity(new Intent(DashboardActivity.this,ModuleNotAvailableActivity.class));
                    }
                }else if (id == R.id.nav_Contact_list) {
                    if (moduleIds.contains("11")) {
                        replaceFragments(new MyContactFragment("1"));
                        setTitle("My Contacts");
                    }else{
                        startActivity(new Intent(DashboardActivity.this,ModuleNotAvailableActivity.class));
                    }

                } else if (id == R.id.nav_customer_list) {
                    if (moduleIds.contains("12")) {
                        replaceFragments(new MyContactFragment("2"));
                        setTitle("My Customers");
                    }else{
                        startActivity(new Intent(DashboardActivity.this,ModuleNotAvailableActivity.class));
                    }
                } else if (id == R.id.nav_prospect_list) {
                    if (moduleIds.contains("13")) {
                        replaceFragments(new MyContactFragment("3"));
                        setTitle("My Prospects");
                    }else{
                        startActivity(new Intent(DashboardActivity.this,ModuleNotAvailableActivity.class));
                    }

                } else if (id == R.id.nav_upload_document) {
                    if (moduleIds.contains("15")) {
                        replaceFragments(new UploadDocumentFragment());
                        setTitle("Upload Documents");
                    }else{
                        startActivity(new Intent(DashboardActivity.this,ModuleNotAvailableActivity.class));
                    }
                }else if (id == R.id.nav_recruits) {
                    if (moduleIds.contains("16")) {
                        replaceFragments(new MyRecruitsFragment());
                        setTitle("My Recruits");
                    }else{
                        startActivity(new Intent(DashboardActivity.this,ModuleNotAvailableActivity.class));
                    }
                }else if (id == R.id.nav_cft_locator) {
                    if (moduleIds.contains("21")) {
                        Intent intent=new Intent(DashboardActivity.this,CFTLocatorActivity.class);
                        startActivity(intent);
                    }else{
                        startActivity(new Intent(DashboardActivity.this,ModuleNotAvailableActivity.class));
                    }
                }else if (id == R.id.nav_daily_top_ten) {
                    replaceFragments(new DailyTopTenFragment());
                    setTitle("Daily Top 10");
                } else if (id == R.id.nav_help) {
                    replaceFragments(new HelpFragment());
                    setTitle("Help");
                }else if (id == R.id.nav_change_profile) {
                    replaceFragments(new ChangeprofileFragment());
                    setTitle("Change Profile");
                }else if (id == R.id.nav_logout) {
                    spLib.clearSharedPrefs();
                    Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                    startActivity(intent, bundle);
                    finish();
                }

            }else{*/ //if module list not available;

                if (id == R.id.nav_dashboard) {
                    replaceFragments(new DashboardFragment());
                } else if (id == R.id.nav_edit_goals) {
                    replaceFragments(new AddEditGoalsFragment(false));
                    setTitle("Add Edit Goals");
                }else if (id == R.id.nav_weekly_goal_graph) {
                    replaceFragments(new WeeklyGraphFragment());
                } else if (id == R.id.nav_weekly_tracking) {
                    replaceFragments(new WeeklyTrackingFragment());
                }else if (id == R.id.nav_calendar) {
                    setTitle("Calendar");
                    replaceFragments(new CalenderFragment());
                   // closeGroup(R.id.nav_my_tools,"close");
                    closeGroup(R.id.nav_my_campaign,"close");
                    closeGroup(R.id.nav_my_crm,"close");
                }else if (id == R.id.nav_time_analysis) {
                    setTitle("Time Analysis");
                    replaceFragments(new TimeAnalysisFragment());
                    closeGroup(R.id.nav_my_crm,"close");
                    //closeGroup(R.id.nav_my_tools,"close");
                    closeGroup(R.id.nav_my_campaign,"close");

                }else if (id == R.id.nav_my_groups) {
                    replaceFragments(new MyGroupsFragmentNew());
                    setTitle("My Groups");
                    closeGroup(R.id.nav_my_crm,"close");
                   // closeGroup(R.id.nav_my_tools,"close");
                    closeGroup(R.id.nav_my_campaign,"close");
                }else if (id == R.id.nav_cft_dashboard) {
                    replaceFragments(new CFTDashboardFragment());
                    setTitle("CFT Dashboard");
                } else if (id == R.id.nav_email_campaign) {
                    replaceFragments(new EmailCampaignFragment());
                    setTitle("Email Campaigns");
                    closeGroup(R.id.nav_my_campaign,"close");
                    closeGroup(R.id.nav_my_crm,"close");
                    //closeGroup(R.id.nav_my_tools,"close");
                }else if (id == R.id.nav_text_campaigns) {
                    replaceFragments(new TextCampaignFragment());
                    setTitle("Text Campaigns");
                    closeGroup(R.id.nav_my_campaign,"close");
                    closeGroup(R.id.nav_my_crm,"close");
                    //closeGroup(R.id.nav_my_tools,"close");
                }/*else if (id == R.id.nav_team_campaigns) {
                    replaceFragments(new TeamCampaignsfragment());
                    setTitle("Team Campaigns");
                }*/else if (id == R.id.nav_Contact_list) {
                    replaceFragments(new MyContactFragmentNew("1"));
                    setTitle("My Contacts");
                    closeGroup(R.id.nav_my_crm,"close");
                    //closeGroup(R.id.nav_my_tools,"close");
                    closeGroup(R.id.nav_my_campaign,"close");
                } else if (id == R.id.nav_customer_list) {
                    replaceFragments(new MyContactFragmentNew("2"));
                    setTitle("My Customers");
                    closeGroup(R.id.nav_my_crm,"close");
                   // closeGroup(R.id.nav_my_tools,"close");
                    closeGroup(R.id.nav_my_campaign,"close");
                } else if (id == R.id.nav_prospect_list) {
                    replaceFragments(new MyContactFragmentNew("3"));
                    setTitle("My Prospects");
                    closeGroup(R.id.nav_my_crm,"close");
                    //closeGroup(R.id.nav_my_tools,"close");
                    closeGroup(R.id.nav_my_campaign,"close");
                } else if (id == R.id.nav_upload_document) {
                    replaceFragments(new UploadDocumentFragment());
                    setTitle("Upload Documents");
                    closeGroup(R.id.nav_my_campaign,"close");
                    closeGroup(R.id.nav_my_crm,"close");
                    //closeGroup(R.id.nav_my_tools,"close");
                }else if (id == R.id.nav_recruits) {
                    replaceFragments(new MyContactFragmentNew("4"));
                    setTitle("My Recruits");
                    closeGroup(R.id.nav_my_crm,"close");
                    //closeGroup(R.id.nav_my_tools,"close");
                    closeGroup(R.id.nav_my_campaign,"close");
                }else if (id == R.id.nav_cft_locator) {
                    Intent intent=new Intent(DashboardActivity.this,CFTLocatorActivity.class);
                    startActivity(intent);
                }else if (id == R.id.nav_daily_top_ten) {
                    replaceFragments(new DailyTopTenFragment());
                    setTitle("Daily Top 10");
                   // closeGroup(R.id.nav_my_tools,"close");
                    closeGroup(R.id.nav_my_campaign,"close");
                    closeGroup(R.id.nav_my_crm,"close");
                }else if (id == R.id.nav_scratch_pad) {
                    replaceFragments(new ScratchNoteFragment());
                    setTitle("Scratch Pad");
                   // closeGroup(R.id.nav_my_tools,"close");
                    closeGroup(R.id.nav_my_campaign,"close");
                    closeGroup(R.id.nav_my_crm,"close");
                } else if (id == R.id.nav_help) {
                    //replaceFragments(new MyContactFragment("1"));
                    replaceFragments(new HelpFragmentWithTab());
                    /*Intent intent=new Intent(DashboardActivity.this,MainOnboardingActivity.class);
                    startActivity(intent);*/
                    setTitle("Help");
                }else if (id == R.id.nav_change_profile) {
                    replaceFragments(new ChangeprofileFragment());
                    setTitle("Change Profile");
                }else if (id == R.id.nav_vision_board) {
                    replaceFragments(new VisionBoardFragment());
                }else if (id == R.id.nav_my_subscription) {
                   openDialogMySubscription();
                }else if (id == R.id.nav_logout) {

                    DeleteTokenTask deleteToken=new DeleteTokenTask();
                    deleteToken.execute();
                   /* try {
                        FirebaseInstanceId.getInstance().deleteInstanceId();
                        Log.d(Global.TAG, "Token Deleted: ");
                        //FirebaseInstanceId.getInstance().getToken();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(Global.TAG, "Token Delete Exception: "+e);
                    }*/
                    spLib.clearSharedPrefs();
                    Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                    startActivity(intent, bundle);
                    finish();
                }else if(id==R.id.nav_my_crm){
                    Log.d("mytag", "nav_my_crm: "+my_crm_count);
                    Log.d("mytag", "nav_my_crm Visibility: "+navigationView.getMenu().getItem(my_crm_count).getTitle());
                    if (navigationView.getMenu().getItem(my_crm_count-1).isVisible()){ //20 for my contact
                        navigationView.getMenu().setGroupVisible(R.id.gp_my_crm,false);
                        Log.d(Global.TAG, "onNavigationItemSelected: Contcts Visible Hide group");
                        closeGroup(R.id.nav_my_crm,"close");
                    }else{
                        navigationView.getMenu().setGroupVisible(R.id.gp_my_crm,true);
                        Log.d(Global.TAG, "onNavigationItemSelected: Contcts Hide Show group");
                        closeGroup(R.id.nav_my_crm,"open");
                    }

                }else if(id==R.id.nav_my_campaign){
                    Log.d("mytag", "my_campaign_count: "+my_campaign_count);
                    Log.d("mytag", "my_campaign_count Visibility: "+navigationView.getMenu().getItem(my_campaign_count).getTitle());
                    if (navigationView.getMenu().getItem(my_campaign_count-1).isVisible()){ //13 for email campaign
                        navigationView.getMenu().setGroupVisible(R.id.gp_my_campaigns,false);
                        closeGroup(R.id.nav_my_campaign,"close");
                    }else{
                        navigationView.getMenu().setGroupVisible(R.id.gp_my_campaigns,true);
                        closeGroup(R.id.nav_my_campaign,"open");
                    }
                }/*else if(id==R.id.nav_my_tools){

                    if (navigationView.getMenu().getItem(2).isVisible()){ //1 for daily top ten
                       // navigationView.getMenu().setGroupVisible(R.id.gp_my_tools,false);
                        closeGroup(R.id.nav_my_tools,"close");
                    }else{
                        //navigationView.getMenu().setGroupVisible(R.id.gp_my_tools,true);
                        closeGroup(R.id.nav_my_tools,"open");
                    }
                }*/else if (id==R.id.nav_earn_referral){
                    openDialogEarnRefferalMoney();
                }

        if (id!=R.id.nav_my_campaign && id!=R.id.nav_my_crm){
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

            drawer.addDrawerListener(new DrawerLayout.DrawerListener() {

                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                    //Called when a drawer's position changes.

                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    //Called when a drawer has settled in a completely open state.
                    //The drawer is interactive at this point.
                    // If you have 2 drawers (left and right) you can distinguish
                    // them by using id of the drawerView. int id = drawerView.getId();
                    // id will be your layout's id: for example R.id.left_drawer
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    // Called when a drawer has settled in a completely closed state.
                }

                @Override
                public void onDrawerStateChanged(int newState) {
                    // Called when the drawer motion state changes. The new state will be one of STATE_IDLE, STATE_DRAGGING or STATE_SETTLING.
                    InputMethodManager inputMethodManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                }
            });
        }


        return true;
    }
    private class DeleteTokenTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                FirebaseInstanceId.getInstance().deleteInstanceId();
                Log.d(Global.TAG, "Token deleted: ");
            } catch (IOException e) {
                Log.d(Global.TAG, "Exception deleting token", e);
            }
            return null;
        }
    }
    private void closeGroup(int item_id, String action) {
        Log.d(Global.TAG, "closeGroup: ");
        if (item_id==R.id.nav_my_campaign || item_id==R.id.nav_my_crm ){
            LinearLayout ll_group=(LinearLayout)navigationView.getMenu().findItem(item_id).getActionView();
            ImageView iv_open = (ImageView) ll_group.findViewById(R.id.iv_group);

            if (action.equals("close")){
                iv_open.setBackground(getResources().getDrawable(R.mipmap.arrow_down1));
            }else if (action.equals("open")){
                iv_open.setBackground(getResources().getDrawable(R.mipmap.arrow_up));
            }
        }else{
            Log.d(Global.TAG, "closeGroup: Item id wrong");
        }
    }


    public void replaceFragment(Fragment fragment) {
        Log.d(Global.TAG, "replaceFragment: ");
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_main, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.addToBackStack("demo");
        transaction.commit();
        return;

    }

    public void replaceFragments(android.support.v4.app.Fragment fragment) {
        Log.d(Global.TAG, "replaceFragment: ");
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_main, fragment);
        transaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.addToBackStack("demo");
        transaction.commit();
        return;
    }

    public void replaceCampaignFragments(android.support.v4.app.Fragment fragment) {
        Log.d(Global.TAG, "replaceFragment: ");
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.ll_view_pager, fragment);
        transaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.addToBackStack("campaign");
        transaction.commit();
        return;
    }

    private Bitmap ImageEncode(String bmp) {
        byte[] decodedString = Base64.decode(bmp, Base64.DEFAULT);
        Bitmap newbmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return newbmp;
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
            if (ActivityCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

            // updateLocation();
            Log.d(Global.TAG, "onConnected: " + user_lat);
            Log.d(Global.TAG, "onConnected: " + user_long);
            try {
                updateLocation();
            } catch (IOException e) {
                Log.d(Global.TAG, "getAddressDetails: Exc: "+e);
                e.printStackTrace();
            }

            // findLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(Global.TAG, "onConnectionSuspended: ");
    }
    private void getAddressDetails() throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        addresses = geocoder.getFromLocation(user_lat, user_long, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        user_address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        user_city= addresses.get(0).getLocality();
        user_state = addresses.get(0).getAdminArea();
        user_country = addresses.get(0).getCountryName();
        zipcode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

        Log.d(Global.TAG, "getAddressDetails: address:"+user_address);
        Log.d(Global.TAG, "getAddressDetails: city:"+user_city);
        Log.d(Global.TAG, "getAddressDetails: state:"+user_state);
        Log.d(Global.TAG, "getAddressDetails: country:"+user_country);
        Log.d(Global.TAG, "getAddressDetails: Zipcode:"+zipcode);
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
            if (ActivityCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
       /* try {
            updateLocation();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
    private void openDialogMySubscription() {
        final Dialog dialog = new Dialog(DashboardActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.my_subscription_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Confirmation");


        Button btn_subscription_dialog_dissmiss = dialog.findViewById(R.id.btn_subscription_dialog_dissmiss);
        final TextView tv_subscription_plan = dialog.findViewById(R.id.tv_subscription_plan);
        final TextView tv_sub_start_date = dialog.findViewById(R.id.tv_sub_start_date);
        final TextView tv_sub_renewal_date = dialog.findViewById(R.id.tv_sub_renewal_date);
        final TextView tv_custom_packages = dialog.findViewById(R.id.tv_custom_packages);
        final TextView tv_upgrade_msg = dialog.findViewById(R.id.tv_upgrade_msg);
        final TextView tv_sub_next_cycle_date = dialog.findViewById(R.id.tv_sub_next_cycle_date);

        getMySubscriptionDetails(tv_subscription_plan,tv_sub_start_date,tv_sub_renewal_date,tv_custom_packages,tv_upgrade_msg,tv_sub_next_cycle_date);
        btn_subscription_dialog_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void getMySubscriptionDetails(final TextView tv_subscription_plan, final TextView tv_sub_start_date, final TextView tv_sub_renewal_date, final TextView tv_custom_packages, final TextView tv_upgrade_msg, final TextView tv_sub_next_cycle_date) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", user_id);
            paramObj.put("platform", "2");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "Send Link Param: "+paramObj.toString());
        final Dialog myLoader = Global.showDialog(DashboardActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<SubscriptionDetails> call = service.getMySubscriptionDetails(paramObj.toString());
        call.enqueue(new Callback<SubscriptionDetails>() {
            @Override
            public void onResponse(Call<SubscriptionDetails> call, Response<SubscriptionDetails> response) {
                if (response.isSuccessful()){
                    SubscriptionDetails subscriptionDetails=response.body();

                    String plan=subscriptionDetails.getSubscribePlanName().toUpperCase();
                    String duration=subscriptionDetails.getPlanDuation().toUpperCase();
                    String amount=subscriptionDetails.getSubscribePlanAmt().toUpperCase();

                    if (!subscriptionDetails.getCustomPackageNames().equals("")){
                        tv_custom_packages.setVisibility(View.VISIBLE);
                        tv_custom_packages.setText(subscriptionDetails.getCustomPackageNames());
                    }else{
                        tv_custom_packages.setVisibility(View.GONE);
                        //tv_custom_packages.setText(subscriptionDetails.getCustomPackageNames());
                    }

                    tv_subscription_plan.setText(plan+"-$"+amount+"/"+duration);

                    if(!subscriptionDetails.getSubscriptionStartDate().equals("") && !subscriptionDetails.getSubscriptionEndDate().equals("") ){
                        tv_sub_start_date.setText("Subscription Start Date: "+subscriptionDetails.getSubscriptionStartDate()+" End Date: "+subscriptionDetails.getSubscriptionEndDate());
                    }else if (!subscriptionDetails.getSubscriptionStartDate().equals("")){
                        tv_sub_start_date.setText("Subscription Start Date: "+subscriptionDetails.getSubscriptionStartDate());
                    }



                    Log.d(Global.TAG, "getMonthlySubRenewalDate: "+subscriptionDetails.getMonthlySubRenewalDate());
                    if (!subscriptionDetails.getMonthlySubRenewalDate().equals("")){
                        tv_sub_renewal_date.setVisibility(View.VISIBLE);
                        tv_sub_renewal_date.setText("Next monthly subscription renewal date: "+subscriptionDetails.getMonthlySubRenewalDate());
                    }else{
                        tv_sub_renewal_date.setVisibility(View.GONE);
                    }

                    if (!subscriptionDetails.getNextMonthlyCycleDate().equals("")){
                        tv_sub_next_cycle_date.setVisibility(View.VISIBLE);
                        tv_sub_next_cycle_date.setText("Next Monthly Cycle: "+subscriptionDetails.getNextMonthlyCycleDate());
                    }else{
                        tv_sub_next_cycle_date.setVisibility(View.GONE);
                    }

                    if (!subscriptionDetails.getUpgradeMessage().equals("")){
                        tv_upgrade_msg.setVisibility(View.VISIBLE);
                        tv_upgrade_msg.setText(subscriptionDetails.getUpgradeMessage());
                    }else{
                        tv_upgrade_msg.setVisibility(View.GONE);
                    }


                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<SubscriptionDetails> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:SubscriptionDetails "+t);
            }
        });
    }

    private void openDialogEarnRefferalMoney() {
        final Dialog dialog = new Dialog(DashboardActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.earn_refferal_money_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Confirmation");

        Button btn_earn_dialog_dissmiss=dialog.findViewById(R.id.btn_earn_dialog_dissmiss);
        final TextView tv_refferal_link=dialog.findViewById(R.id.tv_refferal_link);
        final TextView btn_view_credits=dialog.findViewById(R.id.btn_view_credits);
        final EditText edt_phone_referral1=dialog.findViewById(R.id.edt_phone_referral1);
        final EditText edt_email_referral1=dialog.findViewById(R.id.edt_email_referral1);
        ImageButton ib_add_email_phone=dialog.findViewById(R.id.ib_add_email_phone);
        Button btn_share_referral_link=dialog.findViewById(R.id.btn_share_referral_link);
        Button btn_send_referral_link=dialog.findViewById(R.id.btn_send_referral_link);
        final LinearLayout ll_add_email_phone=dialog.findViewById(R.id.ll_add_email_phone);

        emailEditList=new ArrayList<>();
        phoneEditList=new ArrayList<>();

        getReferralLink(tv_refferal_link);

        ib_add_email_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edt_email_referral1.getText().toString().equals("") && !edt_phone_referral1.getText().toString().equals("")){
                    if (MyValidator.isValidEmailAdd(edt_email_referral1) && MyValidator.isValidMobileNo(edt_phone_referral1)){
                        edt_email_referral1.setError(null);
                        edt_phone_referral1.setError(null);
                        if (emailEditList.size()>0){
                            int lastposition=emailEditList.size()-1;

                            if (!emailEditList.get(lastposition).getText().toString().equals("") && !phoneEditList.get(lastposition).getText().toString().equals("")){
                                if (MyValidator.isValidEmailAdd(emailEditList.get(lastposition)) && MyValidator.isValidMobileNo(phoneEditList.get(lastposition))){
                                    emailEditList.get(lastposition).setError(null);
                                    phoneEditList.get(lastposition).setError(null);
                                    addEmailPhoneView(ll_add_email_phone);
                                }
                            }else if (!emailEditList.get(lastposition).getText().toString().equals("")){
                                if (MyValidator.isValidEmailAdd(emailEditList.get(lastposition))){
                                    emailEditList.get(lastposition).setError(null);
                                    phoneEditList.get(lastposition).setError(null);
                                    addEmailPhoneView(ll_add_email_phone);
                                }
                            }else if (!phoneEditList.get(lastposition).getText().toString().equals("")){
                                if (MyValidator.isValidEmailAdd(phoneEditList.get(lastposition))){
                                    emailEditList.get(lastposition).setError(null);
                                    phoneEditList.get(lastposition).setError(null);
                                    addEmailPhoneView(ll_add_email_phone);
                                }
                            }

                        }else{
                            edt_email_referral1.setError(null);
                            edt_phone_referral1.setError(null);
                            addEmailPhoneView(ll_add_email_phone);
                        }

                    }
                }else if (!edt_email_referral1.getText().toString().equals("")){
                    if (MyValidator.isValidEmailAdd(edt_email_referral1)){
                        edt_email_referral1.setError(null);
                        edt_phone_referral1.setError(null);
                        if (emailEditList.size()>0){
                            int lastposition=emailEditList.size()-1;

                            if (!emailEditList.get(lastposition).getText().toString().equals("") && !phoneEditList.get(lastposition).getText().toString().equals("")){
                                if (MyValidator.isValidEmailAdd(emailEditList.get(lastposition)) && MyValidator.isValidMobileNo(phoneEditList.get(lastposition))){
                                    emailEditList.get(lastposition).setError(null);
                                    phoneEditList.get(lastposition).setError(null);
                                    addEmailPhoneView(ll_add_email_phone);
                                }
                            }else if (!emailEditList.get(lastposition).getText().toString().equals("")){
                                if (MyValidator.isValidEmailAdd(emailEditList.get(lastposition))){
                                    emailEditList.get(lastposition).setError(null);
                                    phoneEditList.get(lastposition).setError(null);
                                    addEmailPhoneView(ll_add_email_phone);
                                }
                            }else if (!phoneEditList.get(lastposition).getText().toString().equals("")){
                                if (MyValidator.isValidMobileNo(phoneEditList.get(lastposition))){
                                    emailEditList.get(lastposition).setError(null);
                                    phoneEditList.get(lastposition).setError(null);
                                    addEmailPhoneView(ll_add_email_phone);
                                }
                            }

                        }else{
                            edt_email_referral1.setError(null);
                            edt_phone_referral1.setError(null);
                            addEmailPhoneView(ll_add_email_phone);
                        }
                    }
                }else if (!edt_phone_referral1.getText().toString().equals("")){
                    if (MyValidator.isValidMobileNo(edt_phone_referral1)){
                        edt_email_referral1.setError(null);
                        edt_phone_referral1.setError(null);
                        if (phoneEditList.size()>0){
                            int lastposition=emailEditList.size()-1;

                            if (!emailEditList.get(lastposition).getText().toString().equals("") && !phoneEditList.get(lastposition).getText().toString().equals("")){
                                if (MyValidator.isValidEmailAdd(emailEditList.get(lastposition)) && MyValidator.isValidMobileNo(phoneEditList.get(lastposition))){
                                    emailEditList.get(lastposition).setError(null);
                                    phoneEditList.get(lastposition).setError(null);
                                    addEmailPhoneView(ll_add_email_phone);
                                }
                            }else if (!emailEditList.get(lastposition).getText().toString().equals("")){
                                if (MyValidator.isValidEmailAdd(emailEditList.get(lastposition))){
                                    emailEditList.get(lastposition).setError(null);
                                    phoneEditList.get(lastposition).setError(null);
                                    addEmailPhoneView(ll_add_email_phone);
                                }
                            }else if (!phoneEditList.get(lastposition).getText().toString().equals("")){
                                if (MyValidator.isValidMobileNo(phoneEditList.get(lastposition))){
                                    emailEditList.get(lastposition).setError(null);
                                    phoneEditList.get(lastposition).setError(null);
                                    addEmailPhoneView(ll_add_email_phone);
                                }
                            }

                        }else{
                            edt_email_referral1.setError(null);
                            edt_phone_referral1.setError(null);
                            addEmailPhoneView(ll_add_email_phone);
                        }
                    }
                }else{
                    Toast.makeText(DashboardActivity.this, "Please Enter Email or Phone..!", Toast.LENGTH_SHORT).show();
                }



               /* if (MyValidator.isValidEmailAdd(edt_email_referral1) || MyValidator.isValidMobileNo(edt_phone_referral1)){
                    edt_email_referral1.setError(null);
                    edt_phone_referral1.setError(null);
                    if (emailEditList.size()>0){
                        int lastposition=emailEditList.size()-1;
                        if (MyValidator.isValidEmailAdd(emailEditList.get(lastposition)) || MyValidator.isValidMobileNo(phoneEditList.get(lastposition))){
                            addEmailPhoneView(ll_add_email_phone);
                        }
                    }else{
                        addEmailPhoneView(ll_add_email_phone);
                    }

                }else{
                    Toast.makeText(DashboardActivity.this, "Please Enter Email or Phone..!", Toast.LENGTH_SHORT).show();
                }*/

            }
        });


        btn_earn_dialog_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_view_credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragments(new EarnCreditScoreFragment());
                dialog.dismiss();
            }
        });

        btn_share_referral_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = tv_refferal_link.getText().toString();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String ShareSub = "Sign Up Link By "+spLib.getPref(SPLib.Key.USER_NAME);
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ShareSub);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,"SIGN UP NOW\n" +
                        "Your friend sent you special link to help you to save money and grow your business \n"+ url);
                startActivity(Intent.createChooser(sharingIntent, "Share Referral Link Via"));
            }
        });

        btn_send_referral_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Global.TAG, "emailEditList: "+emailEditList.size());
                Log.d(Global.TAG, "phoneEditList: "+phoneEditList.size());
                emailList.clear();
                phoneList.clear();

                if (!edt_email_referral1.getText().toString().equals("") && !edt_phone_referral1.getText().toString().equals("")){
                    if (MyValidator.isValidEmailAdd(edt_email_referral1) && MyValidator.isValidMobileNo(edt_phone_referral1)){
                        edt_email_referral1.setError(null);
                        edt_phone_referral1.setError(null);
                        if (emailEditList.size()>0){
                            int lastposition=emailEditList.size()-1;

                            if (!emailEditList.get(lastposition).getText().toString().equals("") && !phoneEditList.get(lastposition).getText().toString().equals("")){
                                if (MyValidator.isValidEmailAdd(emailEditList.get(lastposition)) && MyValidator.isValidMobileNo(phoneEditList.get(lastposition))){
                                    emailEditList.get(lastposition).setError(null);
                                    phoneEditList.get(lastposition).setError(null);
                                    sendReferralLink(edt_email_referral1,edt_phone_referral1,dialog);
                                }
                            }else if (!emailEditList.get(lastposition).getText().toString().equals("")){
                                if (MyValidator.isValidEmailAdd(emailEditList.get(lastposition))){
                                    emailEditList.get(lastposition).setError(null);
                                    phoneEditList.get(lastposition).setError(null);
                                    sendReferralLink(edt_email_referral1,edt_phone_referral1,dialog);
                                }
                            }else if (!phoneEditList.get(lastposition).getText().toString().equals("")){
                                if (MyValidator.isValidEmailAdd(phoneEditList.get(lastposition))){
                                    emailEditList.get(lastposition).setError(null);
                                    phoneEditList.get(lastposition).setError(null);
                                    sendReferralLink(edt_email_referral1,edt_phone_referral1,dialog);
                                }
                            }else{
                                Toast.makeText(DashboardActivity.this, "Please Enter Email or Phone..!", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            edt_email_referral1.setError(null);
                            edt_phone_referral1.setError(null);
                            sendReferralLink(edt_email_referral1,edt_phone_referral1,dialog);
                        }

                    }
                }else if (!edt_email_referral1.getText().toString().equals("")){
                    if (MyValidator.isValidEmailAdd(edt_email_referral1)){
                        edt_email_referral1.setError(null);
                        edt_phone_referral1.setError(null);
                        if (emailEditList.size()>0){
                            int lastposition=emailEditList.size()-1;

                            if (!emailEditList.get(lastposition).getText().toString().equals("") && !phoneEditList.get(lastposition).getText().toString().equals("")){
                                if (MyValidator.isValidEmailAdd(emailEditList.get(lastposition)) && MyValidator.isValidMobileNo(phoneEditList.get(lastposition))){
                                    emailEditList.get(lastposition).setError(null);
                                    phoneEditList.get(lastposition).setError(null);
                                    sendReferralLink(edt_email_referral1,edt_phone_referral1,dialog);
                                }
                            }else if (!emailEditList.get(lastposition).getText().toString().equals("")){
                                if (MyValidator.isValidEmailAdd(emailEditList.get(lastposition))){
                                    emailEditList.get(lastposition).setError(null);
                                    phoneEditList.get(lastposition).setError(null);
                                    sendReferralLink(edt_email_referral1,edt_phone_referral1,dialog);
                                }
                            }else if (!phoneEditList.get(lastposition).getText().toString().equals("")){
                                if (MyValidator.isValidMobileNo(phoneEditList.get(lastposition))){
                                    emailEditList.get(lastposition).setError(null);
                                    phoneEditList.get(lastposition).setError(null);
                                    sendReferralLink(edt_email_referral1,edt_phone_referral1,dialog);
                                }
                            }else{
                                Toast.makeText(DashboardActivity.this, "Please Enter Email or Phone..!", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            edt_email_referral1.setError(null);
                            edt_phone_referral1.setError(null);
                            sendReferralLink(edt_email_referral1,edt_phone_referral1,dialog);
                        }
                    }
                }else if (!edt_phone_referral1.getText().toString().equals("")){
                    if (MyValidator.isValidMobileNo(edt_phone_referral1)){
                        edt_email_referral1.setError(null);
                        edt_phone_referral1.setError(null);
                        if (phoneEditList.size()>0){
                            int lastposition=emailEditList.size()-1;

                            if (!emailEditList.get(lastposition).getText().toString().equals("") && !phoneEditList.get(lastposition).getText().toString().equals("")){
                                if (MyValidator.isValidEmailAdd(emailEditList.get(lastposition)) && MyValidator.isValidMobileNo(phoneEditList.get(lastposition))){
                                    emailEditList.get(lastposition).setError(null);
                                    phoneEditList.get(lastposition).setError(null);
                                    sendReferralLink(edt_email_referral1,edt_phone_referral1,dialog);
                                }
                            }else if (!emailEditList.get(lastposition).getText().toString().equals("")){
                                if (MyValidator.isValidEmailAdd(emailEditList.get(lastposition))){
                                    emailEditList.get(lastposition).setError(null);
                                    phoneEditList.get(lastposition).setError(null);
                                    sendReferralLink(edt_email_referral1,edt_phone_referral1,dialog);
                                }
                            }else if (!phoneEditList.get(lastposition).getText().toString().equals("")){
                                if (MyValidator.isValidMobileNo(phoneEditList.get(lastposition))){
                                    emailEditList.get(lastposition).setError(null);
                                    phoneEditList.get(lastposition).setError(null);
                                    sendReferralLink(edt_email_referral1,edt_phone_referral1,dialog);
                                }
                            }else{
                                Toast.makeText(DashboardActivity.this, "Please Enter Email or Phone..!", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            edt_email_referral1.setError(null);
                            edt_phone_referral1.setError(null);
                            sendReferralLink(edt_email_referral1,edt_phone_referral1,dialog);
                        }
                    }
                }else{
                    Toast.makeText(DashboardActivity.this, "Please Enter Email or Phone..!", Toast.LENGTH_SHORT).show();
                }



            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void sendReferralLink(EditText edt_email_referral1, EditText edt_phone_referral1, final Dialog dialog) {

        emailList.add(edt_email_referral1.getText().toString());
        phoneList.add(edt_phone_referral1.getText().toString());

        if (emailEditList.size()>0 && phoneEditList.size()>0){

            if (emailEditList.size()==phoneEditList.size()){
                for (int i=0;i<emailEditList.size();i++){
                    emailList.add(emailEditList.get(i).getText().toString());
                    phoneList.add(phoneEditList.get(i).getText().toString());
                }
            }else{
                Log.d(Global.TAG, "Both size are mismatched..!: ");
            }

        }
        Log.d(Global.TAG, "emailList: "+emailList);
        Log.d(Global.TAG, "phoneList: "+phoneList);

        String emailReferral = "";
        for (String s : emailList) {
            emailReferral += s + ",";
        }
        if (emailReferral.endsWith(",")) {
            emailReferral = emailReferral.substring(0, emailReferral.length() - 1);
        }

        String phoneReferral = "";
        for (String s : phoneList) {
            phoneReferral += s + ",";
        }
        if (phoneReferral.endsWith(",")) {
            phoneReferral = phoneReferral.substring(0, phoneReferral.length() - 1);
        }
        Log.d(Global.TAG, "emailReferral: "+emailReferral);
        Log.d(Global.TAG, "phoneReferral: "+phoneReferral);

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", user_id);
            paramObj.put("platform", "2");
            paramObj.put("emailReferral", emailReferral);
            paramObj.put("phoneReferral", phoneReferral);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "Send Link Param: "+paramObj.toString());
        final Dialog myLoader = Global.showDialog(DashboardActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call = service.send_referral_link(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                if (response.isSuccessful()){
                    JsonResult jsonResult=response.body();
                    if (jsonResult.isSuccess()){
                        Toast.makeText(DashboardActivity.this, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }else{
                        Toast.makeText(DashboardActivity.this, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    }

                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {

                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: sendLink "+t);
            }
        });
    }

    private void getReferralLink(final TextView tv_refferal_link) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", user_id);
            paramObj.put("platform", "2");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "Send Link Param: "+paramObj.toString());
        final Dialog myLoader = Global.showDialog(DashboardActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<GetReferralLink> call = service.getReferralLink(paramObj.toString());
        call.enqueue(new Callback<GetReferralLink>() {
            @Override
            public void onResponse(Call<GetReferralLink> call, Response<GetReferralLink> response) {
                if (response.isSuccessful()){
                    GetReferralLink getReferralLink=response.body();
                    if (getReferralLink.isSuccess()){

                        GetReferralLink.RLink listLink=getReferralLink.getResult();
                        tv_refferal_link.setText(listLink.getLink());
                        Log.d(Global.TAG, "onResponse: Link:"+listLink.getLink());

                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetReferralLink> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:GetReferralLink "+t);
            }
        });


    }

    private void addEmailPhoneView(LinearLayout ll_add_email_phone) {
        LayoutInflater inflater= (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View attachmentView = inflater.inflate(R.layout.earn_refferal_add_email_row, null);

        final EditText edt_email_referral = (EditText) attachmentView.findViewById(R.id.edt_email_referral);
        final EditText edt_phone_referral = (EditText) attachmentView.findViewById(R.id.edt_phone_referral);
        ImageButton ib_remove_view = (ImageButton) attachmentView.findViewById(R.id.ib_remove_view);
        ll_add_email_phone.addView(attachmentView);
        emailEditList.add(edt_email_referral);
        phoneEditList.add(edt_phone_referral);

        ib_remove_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attachmentView.setVisibility(View.GONE);
                emailEditList.remove(edt_email_referral);
                phoneEditList.remove(edt_phone_referral);
            }
        });
    }
}
