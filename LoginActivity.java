package com.success.successEntellus.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.Validator;
import com.success.successEntellus.R;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.MyValidator;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.GetCustomizeModuleList;
import com.success.successEntellus.model.LoginDetails;
import com.success.successEntellus.model.SingleModule;
import com.success.successEntellus.model.UserDetails;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    Button btn_login;
    ImageButton ib_login_back;
    TextView tv_forget_password;

    EditText edt_username;

    EditText edt_password;

    String username,password;
    Context context=this;
    protected Validator validator;
    //ProgressDialog dialog;
    SPLib spLib;

    SharedPreferences sp;
    ProgressDialog dialog;
    boolean firstRun;
    Bundle bundleEffect;
    List<SingleModule> moduleList=new ArrayList<>();
    List<String> moduleIds=new ArrayList<>();
    Toolbar toolbar_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.login);
        toolbar_login=findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar_login);
        init();
        if (spLib.checkSharedPrefs(SPLib.Key.USER_ID)){
            Intent intent=new Intent(getApplicationContext(),DashboardActivity.class);
            startActivity(intent,bundleEffect);
            finish();
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Global.isNetworkAvailable(LoginActivity.this)){
                    if (MyValidator.isValidEmailAdd(edt_username)){
                        if (MyValidator.isValidFieldE(edt_password,"Enter Password")){
                            login();
                        }
                    }
                }else{
                    Toast.makeText(LoginActivity.this, "Please Check your Internet Connections..!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        ib_login_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(LoginActivity.this,CFTLocatorBeforeLogin.class));

            }
        });

        tv_forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),ForgotPasswordActivity.class);
                startActivity(intent,bundleEffect);
                finish();
            }
        });
    }

    private void init() {
        btn_login=(Button)findViewById(R.id.btn_login);
        ib_login_back=(ImageButton) findViewById(R.id.ib_login_back);
        tv_forget_password=(TextView) findViewById(R.id.tv_forget_password);
        edt_username=(EditText) findViewById(R.id.edt_username);
        edt_password=(EditText) findViewById(R.id.edt_password);
        dialog=new ProgressDialog(LoginActivity.this);
        bundleEffect = ActivityOptionsCompat.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
        spLib=new SPLib(this);
    }
/*
    @Override
    public void onValidationSucceeded() {
        if (Global.isNetworkAvailable(LoginActivity.this)){
            login();
        }else{
            Intent intent = new Intent(LoginActivity.this, NetworkCheckActivity.class);
            startActivity(intent, bundleEffect);
            Toast.makeText(context, "Please Check your Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }*/

    private void login() {
        Log.d("mytag", "login:");
        username = edt_username.getText().toString();
        password = edt_password.getText().toString();
        dialog.setTitle("Login");
        dialog.setMessage("Login... Please Wait..");
        dialog.show();

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<LoginDetails> call=service.login(username,password);
        call.enqueue(new Callback<LoginDetails>() {
            @Override
            public void onResponse(Call<LoginDetails> call, Response<LoginDetails> response) {
                if (response.isSuccessful()){
                    LoginDetails loginDetails=response.body();

                    if (loginDetails.isSuccess()){
                        Toast.makeText(LoginActivity.this, "You're logged in successfully", Toast.LENGTH_SHORT).show();
                        List<UserDetails> resultModel = loginDetails.getResult();
                        Log.d(Global.TAG, "IsGoalSuccess: "+loginDetails.isGoalSetSuccess());
                       if (loginDetails.isGoalSetSuccess()) {

                            Log.d("mytag", "onResponse: Login List Size.. " + resultModel.size());
                           if (resultModel.size() == 1) {
                                Log.d("mytag", "User Name: " + resultModel.get(0).getFirst_name() + " " + resultModel.get(0).getLast_name());
                                Log.d("mytag", "User Id: " + resultModel.get(0).getZo_user_id());
                                String user_name = resultModel.get(0).getFirst_name() + " " + resultModel.get(0).getLast_name();
                                spLib.sharedpreferences.edit().putString(SPLib.Key.USER_ID, resultModel.get(0).getZo_user_id()).commit();
                                spLib.sharedpreferences.edit().putString(SPLib.Key.USER_NAME, user_name).commit();
                                spLib.sharedpreferences.edit().putString(SPLib.Key.USER_FIRST_NAME, resultModel.get(0).getFirst_name()).commit();
                                spLib.sharedpreferences.edit().putString(SPLib.Key.USER_LAST_NAME, resultModel.get(0).getLast_name()).commit();
                                spLib.sharedpreferences.edit().putString(SPLib.Key.USER_EMAIL, resultModel.get(0).getEmail()).commit();
                                spLib.sharedpreferences.edit().putString(SPLib.Key.USER_MOBILE, resultModel.get(0).getPhone()).commit();
                                getModuleDetails();
                               /* Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                                startActivity(intent, bundleEffect);
                                finish();*/
                            }
                        }else if (!loginDetails.isGoalSetSuccess()){
                                List<UserDetails> loginDetailsResult=loginDetails.getResult();
                                String user_id=resultModel.get(0).getZo_user_id();
                                Log.d(Global.TAG, "OnBoard user_id: "+user_id);
                                openDialogGoalDetails(user_id);
                            }
                        }
                    }
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<LoginDetails> call, Throwable t) {
                Log.d("mytag", "onFailure: "+t);
                Toast.makeText(LoginActivity.this, "Invalid Username Or Password. Please Try Again..!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void openDialogGoalDetails(final String user_id) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_view_goal_details);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Conformation");

        TextView tv_goal_details = (TextView) dialog.findViewById(R.id.tv_goal_details);
        Button btn_go = (Button) dialog.findViewById(R.id.btn_go);

        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,OnBoardingFlowActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("user_id",user_id);
                intent.putExtras(bundle);
                startActivity(intent);
                dialog.dismiss();
                finish();
            }
        });


        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
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
        final Dialog myLoader = Global.showDialog(LoginActivity.this);
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
                        for(int i=0;i<moduleList.size();i++){
                            moduleIds.add(moduleList.get(i).getModuleId());
                        }
                        Log.d(Global.TAG, "moduleIds: "+moduleIds.size());
                        spLib.saveArrayList(moduleList,SPLib.Key.MODULELIST);
                        moduleList=spLib.getArrayList(SPLib.Key.MODULELIST);
                        Log.d(Global.TAG, "ModuleList from spLib: "+moduleList.size());

                        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                        startActivity(intent, bundleEffect);
                        finish();

                    }else{
                        Toast.makeText(LoginActivity.this, ""+getCustomizeModuleList.getResult(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                        startActivity(intent, bundleEffect);
                        finish();
                    }
                }else{
                    Log.d(Global.TAG, "Response is null API is not Available: ");
                    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                    startActivity(intent, bundleEffect);
                    finish();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetCustomizeModuleList> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:getModuleDetails: "+t);
                Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(intent, bundleEffect);
                finish();
            }
        });

    }

    /*@Override
    public void onValidationFailed(List<ValidationError> errors) {


        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages
            if (view instanceof Spinner) {
                Spinner sp = (Spinner) view;
                view = ((LinearLayout) sp.getSelectedView()).getChildAt(0);        // we are actually interested in the text view spinner has
            }

            if (view instanceof TextView) {
                TextView et = (TextView) view;
                et.setError(message);
            }
        }

    }*/
}
