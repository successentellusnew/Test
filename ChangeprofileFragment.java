package com.success.successEntellus.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.success.successEntellus.R;
import com.success.successEntellus.activity.DashboardActivity;
import com.success.successEntellus.activity.LoginActivity;
import com.success.successEntellus.activity.UpdateService;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.MyValidator;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.model.UserProfile;
import com.success.successEntellus.model.UserProfileDetails;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 5/30/2018.
 */

public class ChangeprofileFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {
    private static final int REQUEST_PERMISSIONS = 100;
    View layout;
    EditText edt_pfirstName, edt_plastname, edt_pemail, edt_pmobile, edt_ppurpose, edt_current_pass, edt_new_pass, edt_confirm_pass;
    EditText edt_paddress, edt_pcity, edt_pstate, edt_pcountry, edt_pzipcode;
    Button btn_choose_profile_image, btn_choose_why_image, btn_save_profile, btn_cancel_profile, btn_save_pass, btn_cancel_pass;
    TextView tv_profile_image_name, tv_why_image_name;
    ImageView iv_profile_image, iv_why_image;
    ImageButton ib_image_cancel, ib_image_procancel;
    Switch sw_is_cft;
    LinearLayout ll_profile_image, ll_why_image;
    Uri picuri;
    String profile_image_data, why_image_data;
    Bitmap profile_bmp, whybmp;
    SPLib spLib;
    String user_id;
    DashboardActivity dashboardActivity;
    boolean profile_flag = false, business_flag = false;
    private boolean boolean_permission;
    int is_cft;
    CheckBox ch_terms_for_cft;
    //private boolean terms_accepted;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.change_profile, container, false);
        setHasOptionsMenu(false);
        init();
        getProfileDetails();
        if (!boolean_permission){
            fn_permission();
        }
        btn_choose_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImageProfile(getActivity());
            }
        });

        btn_choose_why_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImageWhy(getActivity());
            }
        });

       /* ch_terms_for_cft.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                   // terms_accepted=true;
                    ch_terms_for_cft.setError(null);
                }else{
                    //terms_accepted=false;
                }
            }
        });*/

        btn_save_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyValidator.isValidFieldE(edt_pfirstName, "Enter First Name")) {
                    if (MyValidator.isValidFieldE(edt_plastname, "Enter Last Name")) {
                        if (MyValidator.isValidEmail(edt_pemail)) {
                            if (MyValidator.isValidMobile(edt_pmobile)) {
                                if (MyValidator.isValidFieldE(edt_paddress, "Enter Address..!")) {
                                    if (MyValidator.isValidFieldE(edt_pcity, "Enter City..!")) {
                                        if (MyValidator.isValidFieldE(edt_pstate, "Enter_state..!")) {
                                            if (MyValidator.isValidFieldE(edt_pcountry, "Enter Country..!")) {
                                                if (MyValidator.isValidFieldE(edt_pzipcode, "Enter Zipcode..!")) {
                                                    if (profile_image_data != null) {
                                                        if (why_image_data != null) {
                                                            if (validateMobile()){
                                                                updateProfileDetails();
                                                            }
                                                        } else {
                                                            Toast.makeText(getActivity(), "Please Select Business Image..!", Toast.LENGTH_SHORT).show();
                                                        }

                                                    } else {
                                                        Toast.makeText(getActivity(), "Please Select Profile Image..!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        });

      /*  btn_cancel_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });*/

     /* sw_is_cft.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
              if (checked){
                  is_cft=1;
                  ch_terms_for_cft.setVisibility(View.VISIBLE);
                  ch_terms_for_cft.setChecked(true);
                  ch_terms_for_cft.setClickable(false);
                  showTrackingAlert();
                  //ch_terms_for_cft.setEnabled(false);
              }else{
                  is_cft=0;
                  ch_terms_for_cft.setVisibility(View.GONE);
              }
          }
      });*/

        ib_image_procancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profile_image_data = "";
                ll_profile_image.setVisibility(View.GONE);
            }
        });

        ib_image_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                why_image_data = "";
                ll_why_image.setVisibility(View.GONE);
            }
        });

        btn_save_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (MyValidator.isValidFieldE(edt_current_pass, "Enter Current Password")) {
                    if (MyValidator.isValidPassword(edt_new_pass, "Enter New Password")) {
                        if (MyValidator.isValidPassword(edt_confirm_pass, "Enter Confirm Password")) {
                            String new_pass = edt_new_pass.getText().toString();
                            String confirm_pass = edt_confirm_pass.getText().toString();
                            if (new_pass.equals(confirm_pass)) {
                                new android.support.v7.app.AlertDialog.Builder(getActivity())
                                        .setMessage("Your current session will be get logged out. Make sure your new Log In credentials are correct. Do you want to continue ?")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                change_password();
                                            }
                                        })
                                        .setNegativeButton("No", null)
                                        .show();

                            } else {
                                edt_confirm_pass.setText("");
                                edt_confirm_pass.setFocusable(true);
                                edt_confirm_pass.requestFocus();
                                edt_confirm_pass.setError("Enter Confirm Password..!");
                            }

                        }
                    }

                }

            }
        });
/*

        btn_cancel_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
*/
       /* InputMethodManager inputMethodManager = (InputMethodManager)  getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
*/

        return layout;
    }

    private void showTrackingAlert() {

        new android.support.v7.app.AlertDialog.Builder(getActivity())
                .setMessage("This will track your current location.Are you sure..?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sw_is_cft.setChecked(false);
                    }
                })
                .show();
    }


    private void change_password() {
        final Dialog myLoader = Global.showDialog(getActivity());
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call = service.change_password(user_id, edt_current_pass.getText().toString(), edt_new_pass.getText().toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult = response.body();
                if (jsonResult.isSuccess()) {
                    Toast.makeText(getActivity(), "" + jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                    spLib.clearSharedPrefs();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(getActivity(), "" + jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: Change Pass:" + t);

            }
        });
    }
    private boolean validateMobile() {
        String mobile=edt_pmobile.getText().toString();
        String numbers = mobile.replaceAll("[^\\d]", "");
        Log.d(Global.TAG, "validateMobile: numbers:"+numbers);
        if (numbers.length()>=10 && numbers.length()<12){
            return true;
        }else{
            edt_pmobile.setError("Enter 10 digit Mobile");
            return false;
        }
    }
    private void init() {
        edt_pfirstName = (EditText) layout.findViewById(R.id.edt_pfirstName);
        edt_plastname = (EditText) layout.findViewById(R.id.edt_plastname);
        edt_pemail = (EditText) layout.findViewById(R.id.edt_pemail);
        edt_pmobile = (EditText) layout.findViewById(R.id.edt_pmobile);
        edt_ppurpose = (EditText) layout.findViewById(R.id.edt_ppurpose);
        edt_current_pass = (EditText) layout.findViewById(R.id.edt_current_pass);
        edt_new_pass = (EditText) layout.findViewById(R.id.edt_new_pass);
        edt_confirm_pass = (EditText) layout.findViewById(R.id.edt_confirm_pass);

        edt_paddress = (EditText) layout.findViewById(R.id.edt_paddress);
        edt_pcity = (EditText) layout.findViewById(R.id.edt_pcity);
        edt_pstate = (EditText) layout.findViewById(R.id.edt_pstate);
        edt_pcountry = (EditText) layout.findViewById(R.id.edt_pcountry);
        edt_pzipcode = (EditText) layout.findViewById(R.id.edt_pzipcode);

        btn_choose_profile_image = (Button) layout.findViewById(R.id.btn_choose_profile_image);
        btn_choose_why_image = (Button) layout.findViewById(R.id.btn_choose_why_image);
        btn_save_profile = (Button) layout.findViewById(R.id.btn_save_profile);
        //btn_cancel_profile=(Button) layout.findViewById(R.id.btn_cancel_profile);
        btn_save_pass = (Button) layout.findViewById(R.id.btn_save_pass);
        ch_terms_for_cft = (CheckBox) layout.findViewById(R.id.ch_terms_for_cft);
        //btn_cancel_pass=(Button) layout.findViewById(R.id.btn_cancel_pass);

        tv_profile_image_name = (TextView) layout.findViewById(R.id.tv_profile_image_name);
        tv_why_image_name = (TextView) layout.findViewById(R.id.tv_why_image_name);

        ib_image_cancel = (ImageButton) layout.findViewById(R.id.ib_image_cancel);
        ib_image_procancel = (ImageButton) layout.findViewById(R.id.ib_image_procancel);
        sw_is_cft = (Switch) layout.findViewById(R.id.sw_is_cft);

        ll_profile_image = (LinearLayout) layout.findViewById(R.id.ll_profile_image);
        ll_why_image = (LinearLayout) layout.findViewById(R.id.ll_why_image);

        iv_profile_image = (ImageView) layout.findViewById(R.id.iv_profile_image);
        iv_why_image = (ImageView) layout.findViewById(R.id.iv_why_image);
        spLib = new SPLib(getActivity());
        user_id = spLib.getPref(SPLib.Key.USER_ID);
        sw_is_cft.setOnCheckedChangeListener(ChangeprofileFragment.this);

        String first_name = spLib.getPref(SPLib.Key.USER_FIRST_NAME);
        String last_name = spLib.getPref(SPLib.Key.USER_LAST_NAME);
        String email = spLib.getPref(SPLib.Key.USER_EMAIL);
        String mobile = spLib.getPref(SPLib.Key.USER_MOBILE);
        String purpose = spLib.getPref(SPLib.Key.PURPOSE);
        String address = spLib.getPref(SPLib.Key.ADDRESS);
        String city = spLib.getPref(SPLib.Key.CITY);
        String state = spLib.getPref(SPLib.Key.STATE);
        String country = spLib.getPref(SPLib.Key.COUNTRY);
        String zipcode = spLib.getPref(SPLib.Key.ZIPCODE);

        profile_image_data = spLib.getPref(SPLib.Key.PROFILE_IMAGE);
        why_image_data = spLib.getPref(SPLib.Key.BUSINESS_IMAGE);
        dashboardActivity = new DashboardActivity();

        edt_pfirstName.setText(first_name);
        edt_plastname.setText(last_name);
        edt_pemail.setText(email);
        edt_pmobile.setText(mobile);
        edt_ppurpose.setText(purpose);
        edt_paddress.setText(address);
        edt_pcity.setText(city);
        edt_pstate.setText(state);
        edt_pcountry.setText(country);
        edt_pzipcode.setText(zipcode);

        if (profile_image_data != null) {

            if (!profile_image_data.equals("")) {

                ll_profile_image.setVisibility(View.VISIBLE);
                Picasso.with(getActivity())
                        .load(profile_image_data)
                        .placeholder(R.drawable.place)   // optional
                        .error(R.drawable.error)      // optional
                        .resize(400, 400)
                        .into(iv_profile_image);
            }
        }


        if (why_image_data != null) {
            if (!why_image_data.equals("")) {
                ll_why_image.setVisibility(View.VISIBLE);
                Picasso.with(getActivity())
                        .load(why_image_data)
                        .placeholder(R.drawable.place)   // optional
                        .error(R.drawable.error)      // optional
                        .resize(400, 400)
                        .into(iv_why_image);
            }
        }
    }


    private void selectImageProfile(Context context) {
        final CharSequence[] options = {"Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Profile!");
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

    private void selectImageWhy(Context context) {
        final CharSequence[] options = {"Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Business Image!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Choose from Gallery")) {
                    Log.d("mytag", "Choose from Gallery");
                    Intent GalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(GalleryIntent, 4);

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
        if (requestCode == 2) {
            picuri = data.getData();
            Log.d("uriGallery", picuri.toString());
            String path = getRealPathFromURI(getActivity(), picuri);
            String filename = path.substring(path.lastIndexOf("/") + 1);
            tv_profile_image_name.setText(filename);

            try {
                profile_bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), picuri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (profile_bmp!=null){
                profile_flag = true;
                ll_profile_image.setVisibility(View.VISIBLE);
                iv_profile_image.setImageBitmap(profile_bmp);
                profile_image_data = ImageDecode(profile_bmp);
                Log.d(Global.TAG, "profile_bmp: "+profile_image_data);
            }


           // performCropProfile();

        }else if (requestCode == 3) {

                if (data!=null){
                Bundle extras = data.getExtras();
                //Bitmap profile_bmp = extras.getParcelable("data");

                profile_bmp = (Bitmap) extras.get("data");
                if (profile_bmp != null) {
                    profile_flag = true;
                    ll_profile_image.setVisibility(View.VISIBLE);
                    iv_profile_image.setImageBitmap(profile_bmp);
                    profile_image_data = ImageDecode(profile_bmp);
                    Log.d(Global.TAG, "profile_image_data: " + profile_image_data);
                }
            }

        } else if (requestCode == 4) {
            picuri = data.getData();
            Log.d("uriGallery", picuri.toString());
            String path = getRealPathFromURI(getActivity(), picuri);
            String filename = path.substring(path.lastIndexOf("/") + 1);
            tv_why_image_name.setText(filename);

            try {
                whybmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), picuri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (whybmp!=null){
                business_flag = true;
                ll_why_image.setVisibility(View.VISIBLE);
                iv_why_image.setImageBitmap(whybmp);
                why_image_data = ImageDecode(whybmp);
                Log.d(Global.TAG, "whybmp: "+why_image_data);
            }
            //performCropWhy();
        } else if (requestCode == 5) {
            Bundle extras = data.getExtras();
            whybmp = (Bitmap) extras.get("data");
            if (whybmp != null) {
                business_flag = true;
                ll_why_image.setVisibility(View.VISIBLE);
                iv_why_image.setImageBitmap(whybmp);
                why_image_data = ImageDecode(whybmp);
                Log.d(Global.TAG, "why_image_data: " + why_image_data);
            }
        }
    }
    private boolean fn_permission() {
        if ((ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)||
                (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
            if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
            boolean_permission = false;
        } else {
            boolean_permission = true;
        }
        return boolean_permission;
    }

    private String ImageDecode(Bitmap bmp) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        return Base64.encodeToString(bytes.toByteArray(), Base64.DEFAULT);
    }

    private Bitmap ImageEncode(String bmp) {
        byte[] decodedString = Base64.decode(bmp, Base64.DEFAULT);
        Bitmap newbmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return newbmp;
    }

    private void performCropProfile() {
        Log.d(Global.TAG, "performCropProfile: ");
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
            Log.d(Global.TAG, "performCropProfile: "+anfe);
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void performCropWhy() {
        Log.d(Global.TAG, "performCropWhy: ");
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picuri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 300);
            cropIntent.putExtra("outputY", 300);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, 5);
        } catch (ActivityNotFoundException anfe) {
            Log.d(Global.TAG, "performCropProfile: "+anfe);
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void updateProfileDetails() {
        Log.d(Global.TAG, "updateProfileDetails: ");

        if (!profile_flag) {
            profile_image_data = "";
        }
        if (!business_flag) {
            why_image_data = "";
        }
        Log.d("mytag", " first_name:" + edt_pfirstName.getText().toString());
        Log.d("mytag", " last_name:" + edt_plastname.getText().toString());
        Log.d("mytag", " email:" +  edt_pemail.getText().toString());
        Log.d("mytag", " phone:" + edt_pmobile.getText().toString());
        Log.d("mytag", " zo_user_id:" + user_id);
        Log.d("mytag", " userAddress:" +  edt_paddress.getText().toString());
        Log.d("mytag", " userCity:" +  edt_pcity.getText().toString());
        Log.d("mytag", " userState:" + edt_pstate.getText().toString());
        Log.d("mytag", " userCountry:" +edt_pcountry.getText().toString());
        Log.d("mytag", " uuserZipcode:" + edt_pzipcode.getText().toString());
        Log.d("mytag", " image_profile:" + profile_image_data);
        Log.d("mytag", " image_why_busuiness:" + why_image_data);
        Log.d("mytag", " reason:" + edt_ppurpose.getText().toString());
        Log.d("mytag", " userCft:" + is_cft);
        final Dialog myLoader = Global.showDialog(getActivity());
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call = service.change_profile(edt_pfirstName.getText().toString(),
                edt_plastname.getText().toString(),
                edt_pemail.getText().toString(),
                edt_pmobile.getText().toString(), user_id, "2",
                edt_paddress.getText().toString(),
                edt_pcity.getText().toString(),
                edt_pstate.getText().toString(),
                edt_pcountry.getText().toString(),
                edt_pzipcode.getText().toString(),
                profile_image_data, why_image_data, edt_ppurpose.getText().toString(),is_cft);

        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult = response.body();
                if (jsonResult.isSuccess()) {
                    Toast.makeText(getActivity(), "" + jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                    spLib.sharedpreferences.edit().putString(SPLib.Key.USER_FIRST_NAME, edt_pfirstName.getText().toString()).commit();
                    spLib.sharedpreferences.edit().putString(SPLib.Key.USER_LAST_NAME, edt_plastname.getText().toString()).commit();
                    spLib.sharedpreferences.edit().putString(SPLib.Key.USER_EMAIL, edt_pemail.getText().toString()).commit();
                    spLib.sharedpreferences.edit().putString(SPLib.Key.USER_MOBILE, edt_pmobile.getText().toString()).commit();
                    spLib.sharedpreferences.edit().putString(SPLib.Key.PURPOSE, edt_ppurpose.getText().toString()).commit();
                    //spLib.sharedpreferences.edit().putString(SPLib.Key.PROFILE_IMAGE,profile_image_data).commit();
                    // spLib.sharedpreferences.edit().putString(SPLib.Key.BUSINESS_IMAGE,why_image_data).commit();
                    spLib.sharedpreferences.edit().putString(SPLib.Key.ADDRESS, edt_paddress.getText().toString()).commit();
                    spLib.sharedpreferences.edit().putString(SPLib.Key.CITY, edt_pcity.getText().toString()).commit();
                    spLib.sharedpreferences.edit().putString(SPLib.Key.STATE, edt_pstate.getText().toString()).commit();
                    spLib.sharedpreferences.edit().putString(SPLib.Key.COUNTRY, edt_pcountry.getText().toString()).commit();
                    spLib.sharedpreferences.edit().putString(SPLib.Key.ZIPCODE, edt_pzipcode.getText().toString()).commit();

                    if (is_cft==1) {
                        spLib.sharedpreferences.edit().putBoolean(SPLib.Key.IS_TRACKING,true).commit();
                        Intent myService = new Intent(getActivity(), UpdateService.class);
                        getActivity().startService(myService);
                    }else{
                        spLib.sharedpreferences.edit().putBoolean(SPLib.Key.IS_TRACKING,false).commit();
                        Intent myService = new Intent(getActivity(), UpdateService.class);
                        getActivity().stopService(myService);
                    }

                    //dashboardActivity.iv_nav_profile.setImageBitmap(ImageEncode(profile_image_data));
                    getProfileDetails();
                    //getActivity().getSupportFragmentManager().popBackStackImmediate();
                } else {
                    Toast.makeText(getActivity(), "" + jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: Profile" + t);
            }
        });

    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void getProfileDetails() {
        final Dialog myLoader = Global.showDialog(getActivity());
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        Log.d(Global.TAG, "getProfileDetails: " + user_id);
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<UserProfileDetails> call = service.getProfileDetails(user_id, "2");
        call.enqueue(new Callback<UserProfileDetails>() {
            @Override
            public void onResponse(Call<UserProfileDetails> call, Response<UserProfileDetails> response) {
                UserProfileDetails userProfileModel = response.body();
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

                    Log.d(Global.TAG, "getProfile_pic: " + userDetails.get(0).getProfile_pic());
                    Log.d(Global.TAG, "getDreamImage: " + userDetails.get(0).getDreamImage());

                    is_cft= Integer.parseInt(userDetails.get(0).getUserCft());
                    Log.d(Global.TAG, "onResponse:is_cft "+is_cft);
                    if (is_cft==1){
                        sw_is_cft.setOnCheckedChangeListener(null);
                        sw_is_cft.setChecked(true);
                        ch_terms_for_cft.setChecked(true);
                        ch_terms_for_cft.setClickable(false);
                        sw_is_cft.setOnCheckedChangeListener(ChangeprofileFragment.this);
                       // ch_terms_for_cft.setEnabled(false);
                    }else{
                        sw_is_cft.setChecked(false);
                        ch_terms_for_cft.setVisibility(View.GONE);

                    }

                    if (!userDetails.get(0).getProfile_pic().equals("")) {
                        Picasso.with(getActivity())
                                .load(userDetails.get(0).getProfile_pic())
                                .placeholder(R.drawable.place)   // optional
                                .error(R.drawable.error)      // optional
                                .resize(400, 400)
                                .skipMemoryCache()
                                .into(iv_profile_image);
                    }
                    if (!userDetails.get(0).getProfile_pic().equals("")) {
                        Picasso.with(getActivity())
                                .load(userDetails.get(0).getProfile_pic())
                                .placeholder(R.drawable.place)   // optional
                                .error(R.drawable.error)      // optional
                                .resize(400, 400)
                                .skipMemoryCache()
                                .into(dashboardActivity.iv_nav_profile);
                    }
                    if (!userDetails.get(0).getDreamImage().equals("")) {
                        Picasso.with(getActivity())
                                .load(userDetails.get(0).getDreamImage())
                                .placeholder(R.drawable.place)   // optional
                                .error(R.drawable.error)      // optional
                                .resize(400, 400)
                                .skipMemoryCache()
                                .into(iv_why_image);
                    }

                } else {
                    Toast.makeText(getActivity(), "Error in getting Profile Details..!", Toast.LENGTH_SHORT).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<UserProfileDetails> call, Throwable t) {
                myLoader.dismiss();
                Log.d("mytag", "onFailure: getUserProfile:" + t);

            }
        });

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        if (checked){
            is_cft=1;
            ch_terms_for_cft.setVisibility(View.VISIBLE);
            ch_terms_for_cft.setChecked(true);
            ch_terms_for_cft.setClickable(false);
            //showTrackingAlert();
            //ch_terms_for_cft.setEnabled(false);
        }else{
            is_cft=0;
            ch_terms_for_cft.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("My Profile");
    }
}


