package com.success.successEntellus.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.success.successEntellus.R;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.TypefaceUtil;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity implements Validator.ValidationListener {
    @Email
    EditText edt_emailForgetPassword;

    Button btn_getPassword,btn_cancel;
    Context context=this;
    TextView tv_forgettext;
    Bundle bundleEffect;
    Validator validator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        bundleEffect = ActivityOptionsCompat.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
        init();
        btn_getPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Global.isNetworkAvailable(ForgotPasswordActivity.this)){
                    if(edt_emailForgetPassword.getText().toString().length()==0){
                        edt_emailForgetPassword.setError(getResources().getString(R.string.emptyEmail));
                    }else {
                        validator.validate();
                    }
                }
                else{
                    Toast.makeText(context,"Please Check Your Internet Connection",Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent, bundleEffect);
                finish();
            }
        });
    }
    private void init() {
        edt_emailForgetPassword=(EditText)findViewById(R.id.edt_emailForgetPassword);
        btn_getPassword =(Button)findViewById(R.id.btn_send_pass);
        btn_cancel =(Button)findViewById(R.id.btn_cancel);
        tv_forgettext=(TextView) findViewById(R.id.tv_forgettext);
        validator = new Validator(this);
        validator.setValidationListener(this);
        TypefaceUtil.overrideFonts(context, edt_emailForgetPassword);
        TypefaceUtil.overrideFonts(context, tv_forgettext);

    }

    @Override
    public void onValidationSucceeded() {
        sendPasswordtoRegisteredEmail();
    }

    @Override
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

    }

    private void sendPasswordtoRegisteredEmail() {
        Log.d(Global.TAG, "sendPasswordtoRegisteredEmail: ");
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.forgot_password(edt_emailForgetPassword.getText().toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.IsSuccess){

                    final Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.custom_dialog_conform);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    dialog.setTitle("Conformation");
                    Button dialogButton = (Button) dialog.findViewById(R.id.btn_ok);
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Intent intent = new Intent(context , LoginActivity.class);
                            startActivity(intent,bundleEffect);
                            finish();
                        }
                    });
                    dialog.show();
                    Window window = dialog.getWindow();
                    window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                }else{
                    Toast.makeText(ForgotPasswordActivity.this, ""+jsonResult.getResponseMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                Toast.makeText(context,"We could not find an account with this email address.",Toast.LENGTH_LONG).show();
            }
        });

    }


}
