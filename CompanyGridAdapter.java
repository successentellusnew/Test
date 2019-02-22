package com.success.successEntellus.fragment;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.success.successEntellus.R;
import com.success.successEntellus.activity.DashboardActivity;
import com.success.successEntellus.viewholder.CompanyCampaignHolder;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.model.Campaign;

import java.util.List;

/**
 * Created by user on 5/7/2018.
 */

class CompanyGridAdapter extends RecyclerView.Adapter<CompanyCampaignHolder> {
    DashboardActivity context;
    List<Campaign> campaignsList;
    LayoutInflater inflater;

    View layout;
    String user_id;

    public CompanyGridAdapter(@NonNull DashboardActivity context, List<Campaign> campaignsList, String user_id) {
        this.context=context;
        this.campaignsList=campaignsList;
        this.user_id=user_id;

    }

    @Override
    public CompanyCampaignHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout=inflater.inflate(R.layout.company_campaign_row,parent,false);
        CompanyCampaignHolder companyCampaignHolder=new CompanyCampaignHolder(context,layout);

        return companyCampaignHolder;
    }

    @Override
    public void onBindViewHolder(final CompanyCampaignHolder holder, int position) {
        holder.tv_company_campaign_name.setText(campaignsList.get(position).getCampaignTitle());
        holder.campaign_id=campaignsList.get(position).getCampaignId();
        holder.user_id=user_id;
        Log.d(Global.TAG, "ImageUrl:: "+campaignsList.get(position).getCampaignImage());
        Picasso.with(context)
                .load(campaignsList.get(position).getCampaignImage())
                .resize(400, 400)
                .skipMemoryCache()
                .into( holder.iv_company_campaign);
        Log.d(Global.TAG, "CampaignColor: "+campaignsList.get(position).getCampaignColor());
        List<Integer> colorList=campaignsList.get(position).getCampaignColor();
        if (colorList.size()>0){
            holder.ll_campaign.setBackgroundColor(Color.rgb(colorList.get(0),colorList.get(1),colorList.get(2)));
        }

    }

    @Override
    public int getItemCount() {
        return campaignsList.size();
    }
}
