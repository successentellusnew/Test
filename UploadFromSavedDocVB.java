package com.success.successEntellus.activity;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.success.successEntellus.R;
import com.success.successEntellus.adapter.UploadFromDocAdapter;
import com.success.successEntellus.adapter.UploadImgFileAdapter;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.AttachmentDetails;
import com.success.successEntellus.model.AttachmentResult;
import com.success.successEntellus.model.GetFromSavedDocument;
import com.success.successEntellus.model.SavedDoc;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadFromSavedDocVB extends AppCompatActivity {
    SPLib spLib;
    String user_id;
    ImageButton ib_upload_imagedoc_back;
    LinearLayout ll_upload_imgfile;
    FrameLayout fl_no_uploadedimg_documents;
    Button btn_upload_imgdoc;
    RecyclerView rv_imagedocument_to_upload;
    SearchView sv_uploded_imgfile;
    List<SavedDoc> uploadedFiles,uploadedImageFiles;
    public static List<String> fileSelectionList=new ArrayList<>();
    CreateVisionBoardActivity createVisionBoardActivity;
    String vBoardId="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_from_saved_doc_vb);
        getSupportActionBar().hide();
        setTitle("Upload From Saved Documents");

        Bundle bundle=getIntent().getExtras();
        if (bundle!=null){
            vBoardId=bundle.getString("vision_id");
            Log.d(Global.TAG, "onCreate: vBoardId:"+vBoardId);
        }

        init();
        getFilesFromSavedDoc();


        btn_upload_imgdoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fileSelectionList.size()>0){
                    for(int i=0;i<fileSelectionList.size();i++){
                            uploadImageFiles(fileSelectionList.get(i).toString());

                    }
                }else{
                    Toast.makeText(UploadFromSavedDocVB.this, "Please Select at least one document..!", Toast.LENGTH_LONG).show();
                }

            }
        });

        ib_upload_imagedoc_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void uploadImageFiles(final String file) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("fileOri", file);
            paramObj.put("vboardId", vBoardId);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "uploadFilesFromDoc: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "uploadFilesFromDoc: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(UploadFromSavedDocVB.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<AttachmentResult> call=service.uploadVisionFromSaveDocument(paramObj.toString());
        call.enqueue(new Callback<AttachmentResult>() {
            @Override
            public void onResponse(Call<AttachmentResult> call, Response<AttachmentResult> response) {
                AttachmentResult attachmentResult=response.body();
                if (attachmentResult.isSuccess()){
                    AttachmentDetails attachmentDetails=attachmentResult.getResult();

                  //  String file_url=attachmentDetails.getUrl();
                    Log.d(Global.TAG, "Uploaded File Name: "+attachmentDetails.getFileName());
//                    String filename = file_url.substring(file_url.lastIndexOf("/")+1);
//                    Log.d(Global.TAG, "onResponse:Uploaded File Name: "+filename);
                    createVisionBoardActivity.attachmentURLfromDocList.add(attachmentDetails.getUrl());
                    createVisionBoardActivity.deleteattachmentIds.add(attachmentDetails.getVbAttachmentId());
                    fileSelectionList.remove(file);

                    if (fileSelectionList.size()==0){
                        setResult(RESULT_OK);
                        finish();
                    }

                    // displayAttachment(filename);
                }else{
                    Toast.makeText(UploadFromSavedDocVB.this, ""+attachmentResult.getResult(), Toast.LENGTH_SHORT).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<AttachmentResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: AttachmentResult "+t);
            }
        });

    }


    private void init() {
        spLib=new SPLib(UploadFromSavedDocVB.this);
        createVisionBoardActivity=new CreateVisionBoardActivity();
        user_id=spLib.getPref(SPLib.Key.USER_ID);
        ib_upload_imagedoc_back=findViewById(R.id.ib_upload_imagedoc_back);
        ll_upload_imgfile=findViewById(R.id.ll_upload_imgfile);
        fl_no_uploadedimg_documents=findViewById(R.id.fl_no_uploadedimg_documents);
        btn_upload_imgdoc=findViewById(R.id.btn_upload_imgdoc);
        rv_imagedocument_to_upload=findViewById(R.id.rv_imagedocument_to_upload);
        rv_imagedocument_to_upload.setLayoutManager(new GridLayoutManager(UploadFromSavedDocVB.this,2));
        sv_uploded_imgfile=findViewById(R.id.sv_uploded_imgfile);
        uploadedImageFiles=new ArrayList<>();

    }
    private void getFilesFromSavedDoc() {
        Log.d(Global.TAG, "getFilesFromSavedDoc: User Id: "+user_id);
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getFilesFromSavedDoc: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getFilesFromSavedDoc: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(UploadFromSavedDocVB.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<GetFromSavedDocument> call=service.getFromSavedDocument(paramObj.toString());
        call.enqueue(new Callback<GetFromSavedDocument>() {
            @Override
            public void onResponse(Call<GetFromSavedDocument> call, Response<GetFromSavedDocument> response) {
                GetFromSavedDocument getUploadedFiles=response.body();
                if (getUploadedFiles.isSuccess()){
                    uploadedFiles=getUploadedFiles.getResult();

                    Log.d(Global.TAG, "onResponse: uploadedFilesDoc:"+uploadedFiles.size());

                    if (uploadedFiles.size()>0){
                        fl_no_uploadedimg_documents.setVisibility(View.GONE);
                        ll_upload_imgfile.setVisibility(View.VISIBLE);

                        for(int i=0;i<uploadedFiles.size();i++){
                            if (uploadedFiles.get(i).getFileOri().contains(".jpg") || uploadedFiles.get(i).getFileOri().contains(".jpeg") || uploadedFiles.get(i).getFileOri().contains(".png")){
                                uploadedImageFiles.add(uploadedFiles.get(i));
                            }
                        }
                        Log.d(Global.TAG, "uploadedImagFilesDoc: "+uploadedImageFiles.size());

                        if (uploadedImageFiles.size()>0){
                            UploadImgFileAdapter adapter=new UploadImgFileAdapter(UploadFromSavedDocVB.this,uploadedImageFiles);
                            rv_imagedocument_to_upload.setAdapter(adapter);
                        }else{
                            ll_upload_imgfile.setVisibility(View.GONE);
                            fl_no_uploadedimg_documents.setVisibility(View.VISIBLE);
                        }


                    }else{
                        ll_upload_imgfile.setVisibility(View.GONE);
                        fl_no_uploadedimg_documents.setVisibility(View.VISIBLE);
                    }


                }else{
                    Toast.makeText(UploadFromSavedDocVB.this, ""+getUploadedFiles.getResult(), Toast.LENGTH_SHORT).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetFromSavedDocument> call, Throwable t) {

                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: getFilesFromSavedDoc..");
                ll_upload_imgfile.setVisibility(View.GONE);
                fl_no_uploadedimg_documents.setVisibility(View.VISIBLE);
            }
        });

    }

}
