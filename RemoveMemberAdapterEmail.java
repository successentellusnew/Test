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
import com.success.successEntellus.model.RemoveEmailDetails;
import com.success.successEntellus.viewholder.RemoveEmailHolder;

import java.util.List;

/**
 * Created by user on 10/11/2018.
 */

public class RemoveMemberAdapterEmail extends RecyclerView.Adapter<RemoveEmailHolder> {
    FragmentActivity context;
    LayoutInflater inflater;
    List<RemoveEmailDetails> rMemberList;
    View layout;
    CustomCampaignAdapter customCampaignAdapter;
    String startCamplistFlag;

    public RemoveMemberAdapterEmail(FragmentActivity context, List<RemoveEmailDetails> rMemberList,String startCamplist) {
        this.context=context;
        this.rMemberList=rMemberList;
        customCampaignAdapter=new CustomCampaignAdapter(context);
        this.startCamplistFlag=startCamplist;
    }

    @Override
    public RemoveEmailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout=inflater.inflate(R.layout.remove_email_row,parent,false);
        RemoveEmailHolder removeMemberHolder=new RemoveEmailHolder(layout);
        return removeMemberHolder;
    }

    @Override
    public void onBindViewHolder(RemoveEmailHolder holder, final int position) {

        if (startCamplistFlag.equals("1")){
            holder.ch_remail_check.setVisibility(View.GONE);
            holder.tv_email_date.setText("Added date: "+rMemberList.get(position).getEmail_addDate());
        }else{
            holder.ch_remail_check.setVisibility(View.VISIBLE);
            holder.tv_email_date.setText("Assigned date: "+rMemberList.get(position).getContactCampaignDate());
        }

        holder.tv_member_contact_email_name.setText(""+rMemberList.get(position).getContact_name());
        holder.tv_member_emaill_contact_no.setText("Email: "+rMemberList.get(position).getContact_email());


        holder.ch_remail_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    if (!customCampaignAdapter.removeMemberIds.contains(rMemberList.get(position).getContactCampaignId())){
                        customCampaignAdapter.removeMemberIds.add(rMemberList.get(position).getContactCampaignId());
                        Log.d(Global.TAG, "Checked: "+customCampaignAdapter.removeMemberIds);
                    }

                }else{
                    if (customCampaignAdapter.removeMemberIds.contains(rMemberList.get(position).getContactCampaignId())){
                        customCampaignAdapter.removeMemberIds.remove(rMemberList.get(position).getContactCampaignId());
                        Log.d(Global.TAG, "UnChecked: "+customCampaignAdapter.removeMemberIds);
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
