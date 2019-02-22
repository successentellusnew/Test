package com.success.successEntellus.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.success.successEntellus.R;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.model.RMember;
import com.success.successEntellus.viewholder.RemoveMemberHolder;

import java.util.List;

/**
 * Created by user on 10/10/2018.
 */

public class RemoveMemberAdapter extends RecyclerView.Adapter<RemoveMemberHolder> {
    FragmentActivity context;
    LayoutInflater inflater;
    List<RMember> rMemberList;
    View layout;
    TextCampaignsAdapter textCampaignsAdapter;

    public RemoveMemberAdapter(FragmentActivity context, List<RMember> rMemberList) {
        this.context=context;
        this.rMemberList=rMemberList;
        textCampaignsAdapter=new TextCampaignsAdapter(context);
    }

    @Override
    public RemoveMemberHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout=inflater.inflate(R.layout.remove_member_row,parent,false);
        RemoveMemberHolder removeMemberHolder=new RemoveMemberHolder(layout);
        return removeMemberHolder;
    }

    @Override
    public void onBindViewHolder(RemoveMemberHolder holder, final int position) {

        holder.tv_member_contact_name.setText(""+rMemberList.get(position).getContact_name());
        holder.tv_member_contact_no.setText(""+rMemberList.get(position).getContact_phone());

        holder.ch_rmember_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    if (!textCampaignsAdapter.removeMemberIds.contains(rMemberList.get(position).getTxtcontactCampaignId())){
                        textCampaignsAdapter.removeMemberIds.add(rMemberList.get(position).getTxtcontactCampaignId());
                        Log.d(Global.TAG, "Checked: "+textCampaignsAdapter.removeMemberIds);
                    }

                }else{
                    if (textCampaignsAdapter.removeMemberIds.contains(rMemberList.get(position).getTxtcontactCampaignId())){
                        textCampaignsAdapter.removeMemberIds.remove(rMemberList.get(position).getTxtcontactCampaignId());
                        Log.d(Global.TAG, "UnChecked: "+textCampaignsAdapter.removeMemberIds);
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return rMemberList.size();
    }
}
