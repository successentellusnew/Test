package com.success.successEntellus.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.success.successEntellus.R;
import com.success.successEntellus.fragment.TopScoreHolder;
import com.success.successEntellus.model.TopScoreRecruit;
import com.success.successEntellus.viewholder.DailyTopTenHolder;

import java.util.List;

/**
 * Created by user on 8/1/2018.
 */

public class DailyTopTenAdapter extends RecyclerView.Adapter<DailyTopTenHolder> {
    FragmentActivity context;
    List<TopScoreRecruit> topTenScoreList;
    View layout;
    LayoutInflater inflater;


    public DailyTopTenAdapter(FragmentActivity activity, List<TopScoreRecruit> topScoreRecruits) {
        this.context=activity;
        this.topTenScoreList=topScoreRecruits;
    }

    @Override
    public DailyTopTenHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout=inflater.inflate(R.layout.daily_top_ten_row,parent,false);
        DailyTopTenHolder topScoreHolder=new DailyTopTenHolder(layout);
        return topScoreHolder;
    }

    @Override
    public void onBindViewHolder(DailyTopTenHolder holder, final int position) {
        holder.tv_recruit_name_top10.setText(topTenScoreList.get(position).getRecruitUserName());
        holder.tv_top_ten_score.setText(""+topTenScoreList.get(position).getRecruitUserScoreValue());
/*
        if (!topTenScoreList.get(position).getProfile_pic().equals("")) {
            Picasso.with(context)
                    .load(topTenScoreList.get(position).getProfile_pic())
                    .placeholder(R.drawable.place)   // optional
                    .error(R.mipmap.profile)      // optional
                    .resize(400, 400)
                    .into(holder.iv_top_ten_profile);
        }*/
       /* holder.btn_view_score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCallback.selectRecruit(topScoreRecruits.get(position).getRecruitUserId());
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return topTenScoreList.size();
    }
}
