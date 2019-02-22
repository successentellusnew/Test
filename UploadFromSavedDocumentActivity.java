package com.success.successEntellus.activity;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.success.successEntellus.R;
import com.success.successEntellus.adapter.UploadFileAdapter;
import com.success.successEntellus.adapter.UploadFromDocAdapter;
import com.success.successEntellus.fragment.TextMessagesListFragment;
import com.success.successEntellus.fragment.UploadDocumentFragment;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.AttachmentDetails;
import com.success.successEntellus.model.AttachmentResult;
import com.success.successEntellus.model.GetFromSavedDocument;
import com.success.successEntellus.model.GetUploadedFiles;
import com.success.successEntellus.model.SavedDoc;
import com.success.successEntellus.model.UploadRes;
import com.success.successEntellus.model.UploadResult;
import com.success.successEntellus.model.UploadedFile;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadFromSavedDocumentActivity extends AppCompatActivity {

    SPLib spLib;
    String user_id;
    RecyclerView rv_document_to_upload;
    SearchView sv_uploded_file;
    List<SavedDoc> uploadedFiles,search_list;
    LinearLayout ll_upload_file;
    FrameLayout fl_no_uploaded_documents;
    Button btn_upload_doc;
    String campaign_id,campaign_step_id,template_id;
    AddTemplateActivity addTemplateActivity;
    CreateNewTextMessageActivity createNewTextMessageActivity;
    boolean emailFlag,textFlag;
    public static List<String> fileSelectionList=new ArrayList<>();
    private boolean textEditFlag;
    TextMessagesListFragment textMessagesListFragment;
    ImageButton ib_upload_doc_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_from_saved_document);
        getSupportActionBar().hide();
        setTitle("Upload From Saved Documents");
        init();
        Bundle bundle=getIntent().getExtras();

        if (bundle!=null){
            if (bundle.containsKey("emailFlag")){
                emailFlag=bundle.getBoolean("emailFlag",false);
                Log.d(Global.TAG, "emailFlag: "+emailFlag);
            }
            if (bundle.containsKey("campaign_id")){
                Log.d(Global.TAG, "campaign_id");
                campaign_id=bundle.getString("campaign_id","0");
            }
            if (bundle.containsKey("campaign_step_id")){
                Log.d(Global.TAG, "campaign_step_id");
                campaign_step_id=bundle.getString("campaign_step_id","0");
            }
            if (bundle.containsKey("template_id")){

                template_id=bundle.getString("template_id","0");
                Log.d(Global.TAG, "template_id: "+template_id);
            }
            if (bundle.containsKey("textFlag")){

                textFlag=bundle.getBoolean("textFlag",false);
                Log.d(Global.TAG, "textFlag: "+textFlag);
            }
            if (bundle.containsKey("textEditFlag")){
                textEditFlag=bundle.getBoolean("textEditFlag",false);
                Log.d(Global.TAG, "textEditFlag: "+textEditFlag);
            }

        }
        getFilesFromSavedDoc();

        ib_upload_doc_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sv_uploded_file.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search_list.clear();
                if (uploadedFiles.size()>0){
                    for (int i=0;i<uploadedFiles.size();i++){
                        String upload_file_name=uploadedFiles.get(i).getFileNameToShow();
                        if ( Pattern.compile(Pattern.quote(query), Pattern.CASE_INSENSITIVE).matcher(upload_file_name).find()){
                            search_list.add(uploadedFiles.get(i));
                        }
                    }

                    if (search_list.size()>0){
                        fl_no_uploaded_documents.setVisibility(View.GONE);
                        ll_upload_file.setVisibility(View.VISIBLE);
                        UploadFromDocAdapter adpter=new UploadFromDocAdapter(UploadFromSavedDocumentActivity.this,search_list,user_id);
                        rv_document_to_upload.setAdapter(adpter);
                    }

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search_list.clear();
                if (uploadedFiles.size()>0){
                    for (int i=0;i<uploadedFiles.size();i++){
                        String upload_file_name=uploadedFiles.get(i).getFileNameToShow();
                        if ( Pattern.compile(Pattern.quote(newText), Pattern.CASE_INSENSITIVE).matcher(upload_file_name).find()){
                            search_list.add(uploadedFiles.get(i));
                        }
                    }

                    Log.d(Global.TAG, "onQueryTextChange: Searchlist: "+search_list.size());
                    if (search_list.size()>0){
                        fl_no_uploaded_documents.setVisibility(View.GONE);
                        ll_upload_file.setVisibility(View.VISIBLE);
                        UploadFromDocAdapter adpter=new UploadFromDocAdapter(UploadFromSavedDocumentActivity.this,search_list,user_id);
                        rv_document_to_upload.setAdapter(adpter);
                    }

                }
                return false;
            }
        });
        btn_upload_doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Global.TAG, "onClick:emailFlag: "+emailFlag);
                Log.d(Global.TAG, "onClick:TextFlag: "+textFlag);
                if (fileSelectionList.size()>0){
                    for(int i=0;i<fileSelectionList.size();i++){
                        if (emailFlag){
                            uploadEmailFilesFromDoc(fileSelectionList.get(i).toString());
                        }else if (textFlag || textEditFlag){
                            uploadTextFilesFromDoc(fileSelectionList.get(i).toString());
                        }

                    }
                }else{
                    Toast.makeText(UploadFromSavedDocumentActivity.this, "Please Select at least one document..!", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void uploadEmailFilesFromDoc(final String file) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("campaignId", campaign_id);
            paramObj.put("campaignStepId", campaign_step_id);
            paramObj.put("fileOri", file);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "uploadFilesFromDoc: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "uploadFilesFromDoc: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(UploadFromSavedDocumentActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<AttachmentResult> call=service.uplod_from_saved_doc(paramObj.toString());
        call.enqueue(new Callback<AttachmentResult>() {
            @Override
            public void onResponse(Call<AttachmentResult> call, Response<AttachmentResult> response) {
                AttachmentResult attachmentResult=response.body();
                if (attachmentResult.isSuccess()){
                    AttachmentDetails attachmentDetails=attachmentResult.getResult();

                    String file_url=attachmentDetails.getUrl();
                    Log.d(Global.TAG, "Uploaded File Name: "+attachmentDetails.getFileName());
//                    String filename = file_url.substring(file_url.lastIndexOf("/")+1);
//                    Log.d(Global.TAG, "onResponse:Uploaded File Name: "+filename);
                    addTemplateActivity.attached_files_names.add(attachmentDetails.getFileName());
                    addTemplateActivity.deleteAttachmentIds.add(attachmentDetails.getAttachmentId());
                    fileSelectionList.remove(file);
                    if (fileSelectionList.size()==0){
                        setResult(RESULT_OK);
                        finish();
                    }

                    // displayAttachment(filename);
                }else{
                    Toast.makeText(UploadFromSavedDocumentActivity.this, ""+attachmentResult.getResult(), Toast.LENGTH_SHORT).show();
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

    private void uploadTextFilesFromDoc(final String file) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("txtCampAttachTempId", template_id);
            paramObj.put("fileOri", file);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "uploadTextFilesFromDoc: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "uploadTextFilesFromDoc: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(UploadFromSavedDocumentActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<UploadResult> call=service.upload_text_from_saved_doc(paramObj.toString());
        call.enqueue(new Callback<UploadResult>() {
            @Override
            public void onResponse(Call<UploadResult> call, Response<UploadResult> response) {
                UploadResult attachmentResult=response.body();
                if (attachmentResult.isSuccess()){
                    UploadRes attachmentDetails=attachmentResult.getResult();

                    String file_url=attachmentDetails.getUrl();
                    Log.d(Global.TAG, "Uploaded File Name: "+attachmentDetails.getUrl());
                    String filename = file_url.substring(file_url.lastIndexOf("/")+1);
                    Log.d(Global.TAG, "onResponse:Uploaded File Name: "+filename);
                    Log.d(Global.TAG, "AttachmentId(): "+attachmentDetails.getTxtCampAttachTempId());
                    if (textFlag){
                        createNewTextMessageActivity.attached_file_names.add(filename);
                        createNewTextMessageActivity.deleteIds.add(attachmentDetails.getTxtCampAttachTempId());
                    }else if (textEditFlag){
                        textMessagesListFragment.attached_files.add(filename);
                        textMessagesListFragment.deleteAttachmentId.add(attachmentDetails.getTxtCampAttachTempId());
                    }

                    fileSelectionList.remove(file);
                    if (fileSelectionList.size()==0){
                        setResult(RESULT_OK);
                        finish();
                    }

                    // displayAttachment(filename);
                }else{
                    Toast.makeText(UploadFromSavedDocumentActivity.this, ""+attachmentResult.getResult(), Toast.LENGTH_SHORT).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<UploadResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: AttachmentResult "+t);
            }
        });

    }


    private void init() {

        spLib=new SPLib(UploadFromSavedDocumentActivity.this);
        user_id=spLib.getPref(SPLib.Key.USER_ID);
        btn_upload_doc=findViewById(R.id.btn_upload_doc);
        rv_document_to_upload=findViewById(R.id.rv_document_to_upload);
        rv_document_to_upload.setLayoutManager(new GridLayoutManager(UploadFromSavedDocumentActivity.this,2));
        sv_uploded_file=findViewById(R.id.sv_uploded_file);
        ll_upload_file=findViewById(R.id.ll_upload_file);
        ib_upload_doc_back=findViewById(R.id.ib_upload_doc_back);
        fl_no_uploaded_documents=findViewById(R.id.fl_no_uploaded_documents);
        sv_uploded_file.setIconified(false);
        sv_uploded_file.setFocusable(false);
        sv_uploded_file.clearFocus();
        search_list=new ArrayList<>();
        sv_uploded_file.setQueryHint("Search Document");
        uploadedFiles=new ArrayList<>();
        fileSelectionList=new ArrayList<>();
        addTemplateActivity=new AddTemplateActivity();
        createNewTextMessageActivity=new CreateNewTextMessageActivity();
        textMessagesListFragment=new TextMessagesListFragment();

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

        final Dialog myLoader = Global.showDialog(UploadFromSavedDocumentActivity.this);
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
                        fl_no_uploaded_documents.setVisibility(View.GONE);
                        ll_upload_file.setVisibility(View.VISIBLE);
                        UploadFromDocAdapter adpter=new UploadFromDocAdapter(UploadFromSavedDocumentActivity.this,uploadedFiles,user_id);
                        rv_document_to_upload.setAdapter(adpter);

                    }else{
                        ll_upload_file.setVisibility(View.GONE);
                        fl_no_uploaded_documents.setVisibility(View.VISIBLE);
                    }


                }else{
                    Toast.makeText(UploadFromSavedDocumentActivity.this, ""+getUploadedFiles.getResult(), Toast.LENGTH_SHORT).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetFromSavedDocument> call, Throwable t) {

                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: getFilesFromSavedDoc..");
                ll_upload_file.setVisibility(View.GONE);
                fl_no_uploaded_documents.setVisibility(View.VISIBLE);
            }
        });

    }

}
