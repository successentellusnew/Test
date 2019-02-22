package com.success.successEntellus.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.success.successEntellus.R;
import com.success.successEntellus.activity.TextMessageReplyActivity;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.model.ReplyMessage;
import com.success.successEntellus.viewholder.ReplyFromHolder;
import com.success.successEntellus.viewholder.ReplyMessageHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2/14/2019.
 */

public class ReplyMessageAdapter extends RecyclerView.Adapter<ReplyMessageHolder> {
    TextMessageReplyActivity textMessageReplyActivity;
    List<ReplyMessage> replyMessageList;
    View layout;
    LayoutInflater inflater;
   int color_index;
    Integer[] colorList=new Integer[7];
    int count=0;

    public ReplyMessageAdapter(TextMessageReplyActivity textMessageReplyActivity, List<ReplyMessage> replyMessageList, int color,int count) {
        this.textMessageReplyActivity=textMessageReplyActivity;
        this.replyMessageList=replyMessageList;
        this.color_index=color;
        this.count=count;

        colorList[0]=R.color.color1;
        colorList[1]=R.color.color2;
        colorList[2]=R.color.color3;
        colorList[3]=R.color.color4;
        colorList[4]=R.color.colorWhite;
        colorList[5]=R.color.color3;
        colorList[6]=R.color.colorWhite;

        Log.d(Global.TAG, "ReplyMessageAdapter:Color: "+color_index);
    }


    @NonNull
    @Override
    public ReplyMessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater= (LayoutInflater) textMessageReplyActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout=inflater.inflate(R.layout.text_message_reply_row,parent,false);
        ReplyMessageHolder holder=new ReplyMessageHolder(layout);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyMessageHolder holder, int position) {
       // holder.cardView_reply_msg.setBackgroundColor(color_index);

        /*for(int i=0;i<=6;i++){
            if (color_index==i){
                holder.cardView_reply_msg.setCardBackgroundColor(colorList.get(i));
                Log.d(Global.TAG, "Color match at position: "+i);
            }

        }*/


        holder.tv_reply_date.setText(replyMessageList.get(position).getReplyDate());
        holder.tv_reply_contents.setText(replyMessageList.get(position).getReplyMessge());

    }

    @Override
    public int getItemCount() {
        return count;
    }
}
