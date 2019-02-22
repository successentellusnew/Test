package com.success.successEntellus.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.squareup.picasso.Picasso;
import com.success.successEntellus.R;
import com.success.successEntellus.activity.UploadFromSavedDocVB;
import com.success.successEntellus.activity.UploadFromSavedDocumentActivity;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.model.SavedDoc;
import com.success.successEntellus.viewholder.UploadFromDocHolder;

import java.util.List;

/**
 * Created by user on 11/28/2018.
 */

public class UploadImgFileAdapter extends RecyclerView.Adapter<UploadFromDocHolder> {
    UploadFromSavedDocVB context;
    List<SavedDoc> uploadedFiles;
    View layout;
    LayoutInflater inflater;
    UploadFromSavedDocVB uploadFromSavedDocVB;

    public UploadImgFileAdapter(UploadFromSavedDocVB uploadFromSavedDocVB, List<SavedDoc> uploadedFiles) {
        this.context=uploadFromSavedDocVB;
        this.uploadedFiles=uploadedFiles;

    }

    @NonNull
    @Override
    public UploadFromDocHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        uploadFromSavedDocVB=new UploadFromSavedDocVB();
        layout=inflater.inflate(R.layout.file_upload_row,parent,false);
        UploadFromDocHolder uploadFromDocHolder=new UploadFromDocHolder(layout);
        return uploadFromDocHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UploadFromDocHolder holder, final int position) {
        Picasso.with(context)
                .load(uploadedFiles.get(position).getFileImage())
                .resize(400, 400)
                .into(holder.iv_doc_type);

        holder.tv_doc_name.setText("" + uploadedFiles.get(position).getFileNameToShow());

        holder.ch_select_doc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    uploadFromSavedDocVB.fileSelectionList.add(uploadedFiles.get(position).getFileOri());
                    Log.d(Global.TAG, "fileSelectionList: "+ uploadFromSavedDocVB.fileSelectionList);
                }else{
                    uploadFromSavedDocVB.fileSelectionList.remove(uploadedFiles.get(position).getFileOri());
                    Log.d(Global.TAG, "fileSelectionList: "+ uploadFromSavedDocVB.fileSelectionList);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return uploadedFiles.size();
    }
}
