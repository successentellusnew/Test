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
import com.success.successEntellus.model.Member;
import com.success.successEntellus.viewholder.MemberHolder;

import java.util.List;

/**
 * Created by user on 7/3/2018.
 */

public class MemberListAdapter extends RecyclerView.Adapter<MemberHolder> {
    View layout;
    LayoutInflater inflater;
    FragmentActivity context;
    List<Member> memberList;
    TextCampaignMessageAdapter textCampaignMessageAdapter;

    public MemberListAdapter(FragmentActivity context, List<Member> memberList) {
        this.memberList = memberList;
        this.context = context;
    }

    @Override
    public MemberHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       layout=inflater.inflate(R.layout.member_details_row,parent,false);
       textCampaignMessageAdapter=new TextCampaignMessageAdapter(context);

        MemberHolder holder=new MemberHolder(layout);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MemberHolder holder, final int position) {
        holder.tv_email_name.setText(memberList.get(position).getContactName());
        //holder.tv_eemail.setText(memberList.get(position).getContactEmail());
        holder.tv_ephone.setText(memberList.get(position).getContactPhone());
        holder.tv_schedule_date.setText(memberList.get(position).getScheduleDate());
        holder.tv_email_status.setText(memberList.get(position).getSent());
        holder.ll_read_unread.setVisibility(View.GONE);
        holder.ll_email.setVisibility(View.GONE);

        if (holder.ll_expand_email_details.getVisibility()==View.VISIBLE){
            holder.ib_expand_details.setBackground(context.getResources().getDrawable(R.mipmap.arrow_up1));
        }else{
            holder.ib_expand_details.setBackground(context.getResources().getDrawable(R.mipmap.arrow_down1));
        }

        holder.ib_expand_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.ll_expand_email_details.getVisibility()==View.VISIBLE){
                    holder.ll_expand_email_details.setVisibility(View.GONE);
                    holder.ib_expand_details.setBackground(context.getResources().getDrawable(R.mipmap.arrow_down1));
                }else if (holder.ll_expand_email_details.getVisibility()==View.GONE){
                    holder.ll_expand_email_details.setVisibility(View.VISIBLE);
                    holder.ib_expand_details.setBackground(context.getResources().getDrawable(R.mipmap.arrow_up1));
                }

            }
        });

        holder.ch_checkEmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked){
                    if (!textCampaignMessageAdapter.deleteIds.contains(memberList.get(position).getTxtcontactCampaignId())){
                        textCampaignMessageAdapter.deleteIds.add(memberList.get(position).getTxtcontactCampaignId());
                        Log.d(Global.TAG, "checked: "+textCampaignMessageAdapter.deleteIds);
                    }

                }else{
                    if (textCampaignMessageAdapter.deleteIds.contains(memberList.get(position).getTxtcontactCampaignId())){
                        textCampaignMessageAdapter.deleteIds.remove(memberList.get(position).getTxtcontactCampaignId());
                        Log.d(Global.TAG, "Unchecked: "+textCampaignMessageAdapter.deleteIds);
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }
}
