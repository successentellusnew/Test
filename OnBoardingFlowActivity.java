package com.success.successEntellus.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.success.successEntellus.R;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.MyValidator;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.OnBoardingResult;
import com.success.successEntellus.model.UserDetails;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OnBoardingFlowActivity extends AppCompatActivity {
EditText edt_purpose,et_mobile;
Button btn_choose_file,btn_lets_go;
    ImageView iv_profile;
    Uri picuri;
    Bitmap bmp;
    private String image_data;
    String user_id;
    DashboardActivity dashboardActivity;
    SPLib spLib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding_flow);
        init();

        Bundle bundle=getIntent().getExtras();
        if (bundle!=null){
            user_id=bundle.getString("user_id");
        }

        btn_choose_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(OnBoardingFlowActivity.this);
            }
        });

        btn_lets_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyValidator.isValidFieldE(edt_purpose,"Enter purpose..!")){
                    if (MyValidator.isValidMobileNo(et_mobile)){
                        uploadOnboardingDetails();
                    }
                }

            }
        });

    }
    private void selectImage(Context context) {
        final CharSequence[] options = {"Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Business Image!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
             if (options[item].equals("Choose from Gallery")) {
                    Log.d("mytag", "Choose from Gallery");
                    Intent GalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(GalleryIntent, 2);

                } else if (options[item].equals("Cancel")) {
                    Log.d("mytag", "Cancel");
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 2) {
            picuri = data.getData();
            Log.d("uriGallery", picuri.toString());
            try {
                bmp = MediaStore.Images.Media.getBitmap(OnBoardingFlowActivity.this.getContentResolver(), picuri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bmp!=null){
                iv_profile.setVisibility(View.VISIBLE);
                iv_profile.setImageBitmap(bmp);
                image_data = ImageDecode(bmp);
                Log.d(Global.TAG, "profile_bmp: "+image_data);
            }
            //performCrop();
        } else if (requestCode == 3) {
            Bundle extras = data.getExtras();
            bmp = (Bitmap) extras.get("data");
            Log.d("mytag", "performCrop");
            //
            if (bmp != null) {
                iv_profile.setVisibility(View.VISIBLE);
                iv_profile.setImageBitmap(bmp);
                this.image_data = ImageDecode(bmp);
                Log.d(Global.TAG, "ImageData: "+image_data);
            }else{
                image_data="";
            }
        }
    }

    private void uploadOnboardingDetails() {
      //  Log.d("mytag", " uploadUserProfile image_data" + image_data);
        final Dialog myLoader = Global.showDialog(OnBoardingFlowActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(false);
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<OnBoardingResult> call = service.upload_onboard_image(user_id,"2",image_data,edt_purpose.getText().toString(),et_mobile.getText().toString());
        call.enqueue(new Callback<OnBoardingResult>() {
           @Override
           public void onResponse(Call<OnBoardingResult> call, Response<OnBoardingResult> response) {
               OnBoardingResult onBoardingResult=response.body();
               if (onBoardingResult.isSuccess()){
                   List<UserDetails> userDetailsList=onBoardingResult.getLoginResult();
                   String user_name=userDetailsList.get(0).getFirst_name()+" "+userDetailsList.get(0).getLast_name();
                   spLib.sharedpreferences.edit().putString(SPLib.Key.USER_ID, userDetailsList.get(0).getZo_user_id()).commit();
                   spLib.sharedpreferences.edit().putString(SPLib.Key.USER_NAME, user_name).commit();
                   spLib.sharedpreferences.edit().putString(SPLib.Key.USER_FIRST_NAME, userDetailsList.get(0).getFirst_name()).commit();
                   spLib.sharedpreferences.edit().putString(SPLib.Key.USER_LAST_NAME, userDetailsList.get(0).getLast_name()).commit();
                   spLib.sharedpreferences.edit().putString(SPLib.Key.USER_EMAIL, userDetailsList.get(0).getEmail()).commit();
                   spLib.sharedpreferences.edit().putString(SPLib.Key.USER_MOBILE, userDetailsList.get(0).getPhone()).commit();
                   spLib.sharedpreferences.edit().putString(SPLib.Key.PURPOSE, edt_purpose.getText().toString()).commit();
                   Toast.makeText(OnBoardingFlowActivity.this, ""+onBoardingResult.getResult(), Toast.LENGTH_SHORT).show();
                  // Intent intent=new Intent(getApplicationContext(),DashboardActivity.class);
                   Intent intent=new Intent(getApplicationContext(),OnboardingImportContactActivity.class);
                   Bundle bundle=new Bundle();
                   bundle.putBoolean("onboardFlag",true);
                   intent.putExtras(bundle);
                   startActivity(intent);
                   finish();
               }else{
                   Toast.makeText(OnBoardingFlowActivity.this, ""+onBoardingResult.getResult(), Toast.LENGTH_SHORT).show();
               }
               myLoader.dismiss();
           }

           @Override
           public void onFailure(Call<OnBoardingResult> call, Throwable t) {
               Log.d(Global.TAG, "onFailure: uploadDetails "+t);
               myLoader.dismiss();
           }
       });

    }

    private String ImageDecode(Bitmap bmp) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        return Base64.encodeToString(bytes.toByteArray(), Base64.DEFAULT);
    }

    private void performCrop() {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picuri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 300);
            cropIntent.putExtra("outputY", 300);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, 3);
        } catch (ActivityNotFoundException anfe) {
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(OnBoardingFlowActivity.this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void init() {
        edt_purpose=(EditText)findViewById(R.id.edt_purpose);
        et_mobile=(EditText)findViewById(R.id.et_mobile);
        btn_choose_file= (Button) findViewById(R.id.btn_choose_file);
        btn_lets_go= (Button) findViewById(R.id.btn_lets_go);
        iv_profile= (ImageView) findViewById(R.id.iv_profile);
        dashboardActivity=new DashboardActivity();
        spLib=new SPLib(OnBoardingFlowActivity.this);
    }
}
