package com.success.successEntellus.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.success.successEntellus.R;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.MyValidator;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.GetVisionBoardDetail;
import com.success.successEntellus.model.GetVisionCategory;
import com.success.successEntellus.model.ImageResponse;
import com.success.successEntellus.model.ImageUpload;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.model.VBAttachment;
import com.success.successEntellus.model.VBInfo;
import com.success.successEntellus.model.VCategory;
import com.success.successEntellus.model.Visions;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateVisionBoardActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE =8990 ;
    EditText edt_vision_title, edt_vision_description, edt_vision_emotion;
    Spinner sp_select_vision_category;
    Button btn_upload_vision_image, btn_add_vision, btn_vision_cancel;
    TextView tv_from_saved_doc_vision,tv_vision_screen_name;
    SPLib spLib;
    String user_id;
    List<String> vision_category;
    List<String> vision_category_id;
    private String category = "";
    String edit_category, vBoardId = "0";
    boolean editFlag = false;
    Uri picuri;
    LinearLayout ll_vboard_images;
    ImageButton ib_create_vb_back;
    List<String> attachmentURLList=new ArrayList<>();
    public static List<String> attachmentURLfromDocList=new ArrayList<>();
    public static List<String> deleteattachmentIds=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_vision_board);
        getSupportActionBar().hide();
        init();
        requestRead();
        fillCategoryDropDown();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            editFlag = true;
            edt_vision_title.setText(bundle.getString("vision_title"));
            edt_vision_description.setText(bundle.getString("vision_desc"));
            edt_vision_emotion.setText(bundle.getString("vision_emotion"));
            edit_category = bundle.getString("vision_category");
            vBoardId = bundle.getString("vision_id");
            Log.d(Global.TAG, "onCreate: edit_category:" + edit_category);
            tv_vision_screen_name.setText("Edit Vision Board");
            getVisionDetailsOnId();
        }else{
            tv_vision_screen_name.setText("Create Vision Board");
        }

        sp_select_vision_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (sp_select_vision_category.getSelectedItem().toString().contains("Select")) {
                    category = "";
                } else {
                    category = vision_category_id.get(position - 1).toString();
                    Log.d(Global.TAG, "onItemSelected:category " + category);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btn_add_vision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (MyValidator.isValidFieldE(edt_vision_title,"Enter Vision Title")){
                    if (sp_select_vision_category.getSelectedItem().toString().contains("Select")){
                        MyValidator.setSpinnerError(sp_select_vision_category,"Select category");
                    }else{
                        if (MyValidator.isValidFieldE(edt_vision_description,"Enter description")){
                            if (!editFlag) {
                                addVisionBoard();
                            } else {
                                editVisionBoard();
                            }
                        }
                    }
                }


            }
        });

        btn_vision_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_upload_vision_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        ib_create_vb_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tv_from_saved_doc_vision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CreateVisionBoardActivity.this,UploadFromSavedDocVB.class);
                Bundle bundle=new Bundle();
                bundle.putString("vision_id",vBoardId);
                intent.putExtras(bundle);
                startActivityForResult(intent,1001);
            }
        });
    }

    public void requestRead() {
        if (ContextCompat.checkSelfPermission(CreateVisionBoardActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(CreateVisionBoardActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(CreateVisionBoardActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            // openDocuments();
            Log.d(Global.TAG, "requestRead: Permission Already Granted.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d(Global.TAG, "onRequestPermissionsResult: ");
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(Global.TAG, "onRequestPermissionsResult: Granted ");
                }else{
                    Toast.makeText(CreateVisionBoardActivity.this, "Write External Storage Permission Denied", Toast.LENGTH_SHORT).show();
                }
                //openDocuments();
            } else {
                // Permission Denied
                Toast.makeText(CreateVisionBoardActivity.this, "Read External Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getVisionDetailsOnId() {

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", user_id);
            paramObj.put("platform", "2");
            paramObj.put("vboardId", vBoardId);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getVisionDetailsOnId: " + e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getVisionDetailsOnId: " + paramObj.toString());
        final Dialog myLoader = Global.showDialog(CreateVisionBoardActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<GetVisionBoardDetail> call = service.getVisionBoardById(paramObj.toString());
        call.enqueue(new Callback<GetVisionBoardDetail>() {
            @Override
            public void onResponse(Call<GetVisionBoardDetail> call, Response<GetVisionBoardDetail> response) {
                if (response.isSuccessful()){
                    GetVisionBoardDetail getVisionBoardDetail=response.body();
                    if (getVisionBoardDetail.isSuccess()){
                        Visions visions=getVisionBoardDetail.getResult();
                        String filePath=visions.getFilePath();
                        List<VBAttachment> attachmentList=visions.getVboardAttachment();
                        Log.d(Global.TAG, "onResponse: attachmentList:"+attachmentList.size());

                        attachmentURLList.clear();
                        ll_vboard_images.removeAllViews();
                        if (attachmentList.size()>0 && filePath!=null){
                            for (int i=0;i<attachmentList.size();i++){
                                String image_path=filePath+user_id+"/"+attachmentList.get(i).getVbAttachmentFile();
                                Log.d(Global.TAG, "onResponse:Image Path: ");
                                attachmentURLList.add(image_path);

                                addImageView(image_path,attachmentList.get(i).getVbAttachmentId());
                            }
                        }
                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetVisionBoardDetail> call, Throwable t) {

                myLoader.dismiss();
                Log.d(Global.TAG, "GetVisionBoardDetail: "+t);
            }
        });
    }

    private void selectImage() {
        //this.iv_category_image = iv_category_image;
        final CharSequence[] options = {"Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateVisionBoardActivity.this);
        builder.setTitle("Please Select ");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    try {
                        Log.d("mytag", "Take Photo");
                        Intent CameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/picture.jpg";
                        File file = new File(imageFilePath);
                        picuri = Uri.fromFile(file);
                        CameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, picuri);
                        startActivityForResult(CameraIntent, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (options[item].equals("Choose from Gallery")) {
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            Uri uri = picuri;
            File image_file = new File(picuri.getPath());
            Log.d("picUri", uri.toString());
            //insertImage(image_file);
            //performCrop();

        } else if (resultCode == RESULT_OK && requestCode == 2) {
            picuri = data.getData();
            String Path = getRealPathFromURI(CreateVisionBoardActivity.this, picuri);
//            String filename=Path.substring(Path.lastIndexOf("/")+1);
//            Log.d(Global.TAG, "Path filename : "+filename);

            File image_file = new File(Path);
            Log.d(Global.TAG, "Image File: " + image_file);
            Log.d("mytag", picuri.toString());
            insertImage(image_file);

        }else if (resultCode == RESULT_OK && requestCode == 1001){
            Log.d(Global.TAG, "onActivityResult: Add from Doc");


            if (attachmentURLfromDocList.size()>0){
                for (int i=0;i<attachmentURLfromDocList.size();i++){
                    //String image_path=filePath+user_id+"/"+attachmentList.get(i).getVbAttachmentFile();
                    //Log.d(Global.TAG, "onResponse:Image Path: ");
                    //attachmentURLList.add(image_path);

                    addImageView(attachmentURLfromDocList.get(i),deleteattachmentIds.get(i));
                }
                attachmentURLfromDocList.clear();
            }
        }
    }

    private void insertImage(File image_file) {

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), image_file);
        MultipartBody.Part upload = MultipartBody.Part.createFormData("upload", image_file.getName(), reqFile);

        //Log.d(Global.TAG, "insertImage: "+image_file);
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Uploading...");
        dialog.setMessage("Uploading... Please Wait..");
        dialog.show();

        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), user_id);
        RequestBody platform = RequestBody.create(MediaType.parse("text/plain"), "2");
        RequestBody VisionBoardId = RequestBody.create(MediaType.parse("text/plain"), vBoardId);

        Log.d(Global.TAG, "insertImage: " + user_id + " platform:" + platform + " vboardId:" + vBoardId);
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<ImageUpload> call = service.uploadVBoardImage(userId, platform, VisionBoardId, upload);
        call.enqueue(new Callback<ImageUpload>() {
            @Override
            public void onResponse(Call<ImageUpload> call, Response<ImageUpload> response) {
                ImageUpload imageResponse = response.body();
                if (imageResponse.isSuccess()) {
                    VBInfo vbInfo = imageResponse.getResult();
                    Toast.makeText(CreateVisionBoardActivity.this, "Image Uploaded SuccessFully..!", Toast.LENGTH_LONG).show();
                    Log.d(Global.TAG, "Image Uploaded..! " + imageResponse.getResult());
                    Log.d(Global.TAG, "Image URL: " + vbInfo.getUrl());
                    addImageView(vbInfo.getUrl(), vbInfo.getVbAttachmentId());

                } else {
                    Log.d(Global.TAG, " Error in Image Upload..! ");
                    Toast.makeText(CreateVisionBoardActivity.this, "Error..!" + imageResponse.getResult(), Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<ImageUpload> call, Throwable t) {
                dialog.dismiss();
                Log.d(Global.TAG, "onFailure: Upload " + t);
                Toast.makeText(CreateVisionBoardActivity.this, "Error in Uploading Image!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addImageView(String url, final String attachment_id) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.imageview_with_close_button, null);
        final ImageView imageView = rowView.findViewById(R.id.iv_image_vboard);
        Button btn_close = rowView.findViewById(R.id.btn_delete_image);

        Picasso.with(CreateVisionBoardActivity.this)
                .load(url)
                .placeholder(R.drawable.place)   // optional
                .error(R.drawable.error)      // optional
                .resize(100, 100)
                .into(imageView);

        ll_vboard_images.addView(rowView);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new android.support.v7.app.AlertDialog.Builder(CreateVisionBoardActivity.this)
                        .setMessage("Are you sure to permanently delete without save?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteAttachment(attachment_id,rowView);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();


            }
        });

    }

    private void deleteAttachment(String attachment_id, final View rowView) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", user_id);
            paramObj.put("platform", "2");
            paramObj.put("attachmentId", attachment_id);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "deleteAttachment: " + e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "deleteAttachment: " + paramObj.toString());
        final Dialog myLoader = Global.showDialog(CreateVisionBoardActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call = service.deteleVisionAttachment(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                if (response.isSuccessful()) {
                    JsonResult jsonResult = response.body();
                    if (jsonResult.isSuccess()) {
                        Toast.makeText(CreateVisionBoardActivity.this, "" + jsonResult.getResult(), Toast.LENGTH_LONG).show();
                        Log.d(Global.TAG, "onResponse: " + jsonResult.getResult());
                        ll_vboard_images.removeView(rowView);
                    } else {
                        Toast.makeText(CreateVisionBoardActivity.this, "" + jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    }
                }
                myLoader.dismiss();

            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: CreateVisionBoardActivity "+t);
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

    private void editVisionBoard() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", user_id);
            paramObj.put("platform", "2");
            paramObj.put("vboardId", vBoardId);
            paramObj.put("vboardTitle", edt_vision_title.getText().toString());
            paramObj.put("vboardDescription", edt_vision_description.getText().toString());
            paramObj.put("vboardEmotion", edt_vision_emotion.getText().toString());
            paramObj.put("vboardCategoryId", category);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "editVisionBoard: " + e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "editVisionBoard: " + paramObj.toString());

        final Dialog myLoader = Global.showDialog(CreateVisionBoardActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call = service.edit_vision_board(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                if (response.isSuccessful()) {
                    JsonResult jsonResult = response.body();
                    if (jsonResult.isSuccess()) {
                        Toast.makeText(CreateVisionBoardActivity.this, "" + jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(CreateVisionBoardActivity.this, "" + jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:addVisionBoard " + t);
            }
        });
    }

    private void fillCategoryDropDown() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", user_id);
            paramObj.put("platform", "2");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "fillCategoryDropDown: " + e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "fillCategoryDropDown: " + paramObj.toString());

        final Dialog myLoader = Global.showDialog(CreateVisionBoardActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<GetVisionCategory> call = service.getVisionBoardCategory(paramObj.toString());
        call.enqueue(new Callback<GetVisionCategory>() {
            @Override
            public void onResponse(Call<GetVisionCategory> call, Response<GetVisionCategory> response) {
                if (response.isSuccessful()) {
                    GetVisionCategory getVisionCategory = response.body();
                    if (getVisionCategory.isSuccess()) {
                        List<VCategory> categoryList = getVisionCategory.getResult();
                        Log.d(Global.TAG, "onResponse: categoryList :" + categoryList.size());
                        if (categoryList.size() > 0) {

                            for (int i = 0; i < categoryList.size(); i++) {
                                vision_category.add(categoryList.get(i).getVbCategory());
                                vision_category_id.add(categoryList.get(i).getVbCategoryId());
                            }
                            applySpinner(vision_category, sp_select_vision_category, "--Select Categoty--");

                            if (editFlag) {
                                Log.d(Global.TAG, "onCreate: vision_category:" + vision_category.size());
                                int position = 0;
                                for (int i = 0; i < vision_category.size(); i++) {
                                    if (vision_category.get(i).toString().equalsIgnoreCase(edit_category)) {
                                        position = i;
                                    }
                                }
                                Log.d(Global.TAG, "edit_category Position : " + position);
                                sp_select_vision_category.setSelection(position + 1);

                            }
                        } else {
                            sp_select_vision_category.setEnabled(false);
                        }
                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetVisionCategory> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:categoryList " + t);
            }
        });
    }

    private void addVisionBoard() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", user_id);
            paramObj.put("platform", "2");
            paramObj.put("vboardTitle", edt_vision_title.getText().toString());
            paramObj.put("vboardDescription", edt_vision_description.getText().toString());
            paramObj.put("vboardEmotion", edt_vision_emotion.getText().toString());
            paramObj.put("vboardCategoryId", category);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "addVisionBoard: " + e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "addVisionBoard: " + paramObj.toString());

        final Dialog myLoader = Global.showDialog(CreateVisionBoardActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call = service.create_vision_board(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                if (response.isSuccessful()) {
                    JsonResult jsonResult = response.body();
                    if (jsonResult.isSuccess()) {
                        Toast.makeText(CreateVisionBoardActivity.this, "" + jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(CreateVisionBoardActivity.this, "" + jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:addVisionBoard " + t);
            }
        });
    }

    private void init() {
        edt_vision_title = findViewById(R.id.edt_vision_title);
        edt_vision_description = findViewById(R.id.edt_vision_description);
        edt_vision_emotion = findViewById(R.id.edt_vision_emotion);

        sp_select_vision_category = findViewById(R.id.sp_select_vision_category);
        btn_upload_vision_image = findViewById(R.id.btn_upload_vision_image);
        btn_add_vision = findViewById(R.id.btn_add_vision);
        btn_vision_cancel = findViewById(R.id.btn_vision_cancel);

        tv_from_saved_doc_vision = findViewById(R.id.tv_from_saved_doc_vision);
        tv_vision_screen_name = findViewById(R.id.tv_vision_screen_name);
        spLib = new SPLib(CreateVisionBoardActivity.this);
        user_id = spLib.getPref(SPLib.Key.USER_ID);
        vision_category = new ArrayList<>();
        vision_category_id = new ArrayList<>();
        ll_vboard_images = findViewById(R.id.ll_vboard_images);
        ib_create_vb_back = findViewById(R.id.ib_create_vb_back);
    }

    private void applySpinner(final List<String> taglist, Spinner sp_name, String tag_string) {
        //Adding spinner on Repeate By:: When Monthly Selected Add 12
        AddContactActivity.spinnerAdapter adapterRepeateDaily = new AddContactActivity.spinnerAdapter(CreateVisionBoardActivity.this, android.R.layout.simple_list_item_1);
        adapterRepeateDaily.add(tag_string);
        adapterRepeateDaily.addAll(taglist);
        adapterRepeateDaily.add(tag_string);
        sp_name.setAdapter(adapterRepeateDaily);
        sp_name.setSelection(adapterRepeateDaily.getCount());
        sp_name.setEnabled(true);
    }
}
