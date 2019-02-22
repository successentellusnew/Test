package com.success.successEntellus.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.success.successEntellus.R;
import com.success.successEntellus.model.AddedMembers;
import com.success.successEntellus.viewholder.StartTextCampHolder;

import java.util.List;

/**
 * Created by user on 11/27/2018.
 */

public class StartTextCampAdapter extends RecyclerView.Adapter<StartTextCampHolder> {
    View layout;
    LayoutInflater inflater;
    FragmentActivity context;
    List<AddedMembers> addeddMemberList;
    RefreshStartCampList refreshStartCampList;

    public interface RefreshStartCampList{
        public void addSMember(String member_id);
        public void removeSMember(String member_id);
    }
    public StartTextCampAdapter(FragmentActivity context, List<AddedMembers> addeddMemberList, RefreshStartCampList refreshStartCampList) {
            this.context=context;
            this.addeddMemberList=addeddMemberList;
            this.refreshStartCampList=refreshStartCampList;
    }

    @NonNull
    @Override
    public StartTextCampHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout=inflater.inflate(R.layout.start_text_camp_row,parent,false);
        StartTextCampHolder startTextCampHolder=new StartTextCampHolder(layout);
        return startTextCampHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StartTextCampHolder holder, final int position) {
        holder.tv_member_contact_name.setText(addeddMemberList.get(position).getContact_name());
        //holder.tv_start_member_date.setText(addeddMemberList.get(position).getEmail_addDate());
        holder.tv_member_contact_no.setText(addeddMemberList.get(position).getContact_phone());

        holder.ch_start_txt_camp_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    refreshStartCampList.addSMember(addeddMemberList.get(position).getTxtcontactCampaignId());
                }else{
                    refreshStartCampList.removeSMember(addeddMemberList.get(position).getTxtcontactCampaignId());
                }
            }
        });
        holder.ch_start_txt_camp_check.setChecked(true);
    }

    @Override
    public int getItemCount() {
        return addeddMemberList.size();
    }
}
