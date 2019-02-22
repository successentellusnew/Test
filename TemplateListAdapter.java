package com.success.successEntellus.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.success.successEntellus.R;
import com.success.successEntellus.activity.CreateCampaignActivity;
import com.success.successEntellus.model.CampaignTemplate;
import com.success.successEntellus.viewholder.TemplateHolder;

import java.util.List;

/**
 * Created by user on 5/14/2018.
 */

public class TemplateListAdapter extends RecyclerView.Adapter<TemplateHolder> {
    Context context;
    List<CampaignTemplate> campaignTemplates;
    LayoutInflater inflater;
    View layout;
    CreateCampaignActivity createCampaignActivity=new CreateCampaignActivity();
    boolean importflag;
    boolean selectAllTemplates;
    public TemplateListAdapter(@NonNull Context context, List<CampaignTemplate> campaignTemplates, boolean importflag,boolean selectAllTemplates) {
        this.context=context;
        this.importflag=importflag;
        this.campaignTemplates=campaignTemplates;
        this.selectAllTemplates=selectAllTemplates;
    }

    @Override
    public TemplateHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout=inflater.inflate(R.layout.create_campaign_row,parent,false);
        TemplateHolder templateHolder=new TemplateHolder(layout);
        return templateHolder;
    }

    @Override
    public void onBindViewHolder(TemplateHolder holder, final int position) {
        if (!campaignTemplates.get(position).getCampaignStepTitle().equals("")){
            holder.tv_campaign_tem_name.setText(campaignTemplates.get(position).getCampaignStepTitle());
        }else{
            holder.tv_campaign_tem_name.setText("No Heading");
        }

        if (selectAllTemplates){
           holder.ch_create_campaign.setChecked(true);
        }

        holder.ch_create_campaign.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    if ( !createCampaignActivity.createCampaignIds.contains(campaignTemplates.get(position).getCampaignStepId())){
                        if (importflag){
                            createCampaignActivity.importCampaignIds.add(campaignTemplates.get(position).getCampaignStepId());
                        }else{
                            createCampaignActivity.createCampaignIds.add(campaignTemplates.get(position).getCampaignStepId());
                        }
                    }

                }else{
                    if ( createCampaignActivity.createCampaignIds.contains(campaignTemplates.get(position).getCampaignStepId())){
                        if (importflag){
                            createCampaignActivity.importCampaignIds.remove(campaignTemplates.get(position).getCampaignStepId());
                        }else{
                            createCampaignActivity.createCampaignIds.remove(campaignTemplates.get(position).getCampaignStepId());                      }
                    }
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return campaignTemplates.size();
    }

    /*@NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView=inflater.inflate(R.layout.create_campaign_row,parent,false);


        tv_campaign_tem_name.setText(campaignTemplates.get(position).getCampaignStepTitle());

        ch_create_campaign.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
              if (checked){
                  if ( !createCampaignActivity.createCampaignIds.contains(campaignTemplates.get(position).getCampaignStepId())){
                      if (importflag){
                          createCampaignActivity.importCampaignIds.add(campaignTemplates.get(position).getCampaignStepId());
                      }else{
                          createCampaignActivity.createCampaignIds.add(campaignTemplates.get(position).getCampaignStepId());
                      }
                  }

              }else{
                  if ( createCampaignActivity.createCampaignIds.contains(campaignTemplates.get(position).getCampaignStepId())){
                      if (importflag){
                          createCampaignActivity.importCampaignIds.remove(campaignTemplates.get(position).getCampaignStepId());
                      }else{
                          createCampaignActivity.createCampaignIds.remove(campaignTemplates.get(position).getCampaignStepId());                      }
                    }
              }

          }
      });
        return convertView;
    }

    @Override
    public int getCount() {
        return campaignTemplates.size();
    }*/
}
