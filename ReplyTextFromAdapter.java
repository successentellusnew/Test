package com.success.successEntellus.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.success.successEntellus.R;
import com.success.successEntellus.activity.TextMessageReplyActivity;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.model.ReplyFrom;
import com.success.successEntellus.model.ReplyMessage;
import com.success.successEntellus.viewholder.ReplyFromHolder;

import java.util.List;
import java.util.Random;

/**
 * Created by user on 2/14/2019.
 */

public class ReplyTextFromAdapter  extends RecyclerView.Adapter<ReplyFromHolder>{
    TextMessageReplyActivity textMessageReplyActivity;
    List<ReplyFrom> replyFromList;
    View layout;
    LayoutInflater inflater;
    Random rnd;
    public ReplyTextFromAdapter(TextMessageReplyActivity textMessageReplyActivity, List<ReplyFrom> replyFromList) {

        this.textMessageReplyActivity=textMessageReplyActivity;
        this.replyFromList=replyFromList;
        rnd=new Random();
    }

    @NonNull
    @Override
    public ReplyFromHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       inflater= (LayoutInflater) textMessageReplyActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       layout=inflater.inflate(R.layout.reply_from_row_layout,parent,false);
        ReplyFromHolder holder=new ReplyFromHolder(layout);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ReplyFromHolder holder, int position) {

        holder.rv_text_reply_list.setLayoutManager(new LinearLayoutManager(textMessageReplyActivity));
        holder.tv_from_name.setText(replyFromList.get(position).getReplyfromName());
        holder.iv_user_reply_text.setMonoColor(Color.BLUE,Color.WHITE);

        if (!replyFromList.get(position).getReplyfromName().equals("")){
            holder.iv_user_reply_text.loadThumbForName( "",replyFromList.get(position).getReplyfromName());
        }


        final List<ReplyMessage> replyMessageList=replyFromList.get(position).getReply();
        Log.d(Global.TAG, "onBindViewHolder:replyMessageList "+replyMessageList.size());

        final int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        //int color_index = rnd.nextInt(7 - 0) + 0;
        Log.d(Global.TAG, "onBindViewHolder:color_index "+color);

        if (replyMessageList.size()>1){
            ReplyMessageAdapter replyMessageAdapter=new ReplyMessageAdapter(textMessageReplyActivity,replyMessageList,color,1);
            holder.rv_text_reply_list.setAdapter(replyMessageAdapter);
        }else  if (replyMessageList.size()==1){
            holder.tv_view_more.setVisibility(View.GONE);

            ReplyMessageAdapter replyMessageAdapter=new ReplyMessageAdapter(textMessageReplyActivity,replyMessageList,color,1);
            holder.rv_text_reply_list.setAdapter(replyMessageAdapter);
        }


        holder.tv_view_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (holder.tv_view_more.getText().toString().equals("View More..")){
                    ReplyMessageAdapter replyMessageAdapter=new ReplyMessageAdapter(textMessageReplyActivity,replyMessageList,color,replyMessageList.size());
                    holder.rv_text_reply_list.setAdapter(replyMessageAdapter);
                    holder.tv_view_more.setText("View Less..");
                }else{
                    ReplyMessageAdapter replyMessageAdapter=new ReplyMessageAdapter(textMessageReplyActivity,replyMessageList,color,1);
                    holder.rv_text_reply_list.setAdapter(replyMessageAdapter);
                    holder.tv_view_more.setText("View More..");
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return replyFromList.size();
    }
}
