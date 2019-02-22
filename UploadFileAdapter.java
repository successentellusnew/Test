package com.success.successEntellus.adapter;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.success.successEntellus.R;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.model.GetUploadedFiles;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.model.UploadedFile;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;
import com.success.successEntellus.viewholder.UploadedHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 7/11/2018.
 */

public class UploadFileAdapter extends RecyclerView.Adapter<UploadedHolder> {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE =1002 ;
    FragmentActivity context;
    List<UploadedFile> uploadedFiles;
    LayoutInflater inflater;
    View layout;
    String user_id;

    public interface RefreshDocuments{
        void refreshDoc();
    }

    RefreshDocuments refreshDocuments;
    public UploadFileAdapter(FragmentActivity activity, List<UploadedFile> uploadedFiles, String user_id,  RefreshDocuments refreshDocuments) {
        this.context = activity;
        this.uploadedFiles = uploadedFiles;
        this.user_id=user_id;
        this.refreshDocuments=refreshDocuments;
    }

    @Override
    public UploadedHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.upload_file_row, parent, false);
        UploadedHolder uploadedHolder = new UploadedHolder(layout);

        return uploadedHolder;
    }

    @Override
    public void onBindViewHolder(UploadedHolder holder, final int position) {
        Picasso.with(context)
                .load(uploadedFiles.get(position).getFileTypeImage())
                .resize(400, 400)
                .into(holder.iv_doc_type);

        holder.tv_doc_name.setText("" + uploadedFiles.get(position).getFileName());

        holder.ib_delete_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setMessage("Are you sure you want to Delete this file..? ")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deteleUploadedDocument(position);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

            }
        });

        holder.ib_download_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(context,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    Toast.makeText(context, "Write External Storage Permission Denied", Toast.LENGTH_SHORT).show();
                } else {
                    downloadFile(position);
                    Log.d(Global.TAG, "requestRead: Permission Already Granted.");
                }


            }
        });

    }



    private void downloadFile(int position) {
        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        String url = uploadedFiles.get(position).getFileUrl();
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Some descrition");
        request.setTitle(""+uploadedFiles.get(position).getFileName());
        // in order for this if to run, you must use the android 3.2 to compile your app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, uploadedFiles.get(position).getFileName());

        // get download service and enqueue file
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
        myLoader.dismiss();
        Toast.makeText(context, "File Downloaded Successfully..!", Toast.LENGTH_SHORT).show();
    }

    private void deteleUploadedDocument(int position) {

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", user_id);
            paramObj.put("platform", "2");
            paramObj.put("fileName", uploadedFiles.get(position).getFileName());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "deteleUploadedDocument: " + e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "deteleUploadedDocument: " + paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call = service.delete_document(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult = response.body();
                if (jsonResult.isSuccess()) {
                    Toast.makeText(context, "" + jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                    refreshDocuments.refreshDoc();
                } else {
                    Toast.makeText(context, "" + jsonResult.getResult(), Toast.LENGTH_SHORT).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: deleteDoc:"+t);

            }
        });

    }

    @Override
    public int getItemCount() {
        return uploadedFiles.size();
    }
}
