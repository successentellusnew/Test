package com.success.successEntellus.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.success.successEntellus.R;
import com.success.successEntellus.model.DocumentInfo;
import com.success.successEntellus.viewholder.DocumentHolder;

import java.util.List;

/**
 * Created by user on 12/1/2018.
 */

public class DocumentsAdapter extends RecyclerView.Adapter<DocumentHolder> {
    List<DocumentInfo> documentLists;
    FragmentActivity context;
    View layout;
    LayoutInflater inflater;
    public DocumentsAdapter(FragmentActivity activity, List<DocumentInfo> documentLists) {
        this.context=activity;
        this.documentLists=documentLists;
    }

    @NonNull
    @Override
    public DocumentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout=inflater.inflate(R.layout.documnt_row_layout,parent,false);
        DocumentHolder holder=new DocumentHolder(layout);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentHolder holder, final int position) {
        holder.tv_doc_name.setText(documentLists.get(position).getStepStaticTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(documentLists.get(position).getStepStaticUrl()));
                context.startActivity(browserIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return documentLists.size();
    }
}
