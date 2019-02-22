package com.success.successEntellus.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;
import com.success.successEntellus.R;
import com.success.successEntellus.activity.AddContactActivity;
import com.success.successEntellus.activity.CreateNewTextMessageActivity;
import com.success.successEntellus.activity.DashboardActivity;
import com.success.successEntellus.fragment.TextCampaignFragment;
import com.success.successEntellus.fragment.TextCampaignHolder;
import com.success.successEntellus.fragment.TextMessagesListFragment;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.MyValidator;
import com.success.successEntellus.model.AddedMember;
import com.success.successEntellus.model.AddedMembers;
import com.success.successEntellus.model.Contact;
import com.success.successEntellus.model.ContactDetails;
import com.success.successEntellus.model.GetAddedMemberToStartCampaign;
import com.success.successEntellus.model.GetAllTextMessages;
import com.success.successEntellus.model.GetRemoveMemberList;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.model.RMember;
import com.success.successEntellus.model.RemoveMember;
import com.success.successEntellus.model.TextCampaign;
import com.success.successEntellus.model.TextMessage;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 6/29/2018.
 */

public class TextCampaignsAdapter extends RecyclerView.Adapter<TextCampaignHolder> implements AddMemberAdapter.NotifyAddRemoveMember,StartTextCampAdapter.RefreshStartCampList {
    FragmentActivity context;
    LayoutInflater inflater;
    View layout;
    String user_id;
    EditText edt_text_message_name,edt_text_message,edt_time_interval_text_message;
    Spinner sp_select_message_time_interval;
    Button btn_attach_file;
    List<TextCampaign> textCampaignList;
    private String text_message_interval_type;
    String[] intervalType={"Hours","Day","Week"};
    DashboardActivity dashboardActivity;
    ToggleButton tv_my_contact_member,tv_my_prospect_member,tv_my_customers_member,tv_my_recruit_member;
    SearchView search_add_member;
    TextView tv_selected_members;
    RecyclerView rv_add_member;
    LinearLayout ll_add_from_system,ll_add_manually;
    EditText edt_member_lname,edt_member_fname,edt_email_add_member,edt_mobile_add_member;
    Button btn_assign_member,btn_assign_member_cancel;
    String remove_camp_name;

    public static List<String> selected_all_contact_list=new ArrayList<>();
    public static List<String> selected_contact_list=new ArrayList<>();
    public static List<String> selected_prospect_list=new ArrayList<>();
    public static List<String> selected_customers_list=new ArrayList<>();
    public static List<String> selected_recruits_list=new ArrayList<>();
    String crm_flag;
    private String addMembersList;
    int lastPosition=-1;
    private List<Contact> search_list,contactList;
    public static List<String> removeMemberIds=new ArrayList<>();
    public static List<String> startCampaignList=new ArrayList<>();
    RecyclerView rv_remove_members,rv_added_member_list;
    TextView tv_text_rtemplate_name,tv_added_text_camp_name;
    int textMsgCount;
    Dialog dialog;
    private String startCampaignIdsListString="";



    public  interface NotifyRefresh{
        void refresh();
    }
    private NotifyRefresh notifyRefresh;
    TextCampaignFragment textFragment;

    public TextCampaignsAdapter(FragmentActivity activity, List<TextCampaign> textCampaignList, String user_id,NotifyRefresh notifyRefresh,TextCampaignFragment textFragment,int textMsgCount) {
        this.context=activity;
        this.textCampaignList=textCampaignList;
        this.user_id =user_id;
        dashboardActivity=(DashboardActivity) activity;
        this.notifyRefresh=notifyRefresh;
        this.textFragment=textFragment;
        this.textMsgCount=textMsgCount;
    }

    public TextCampaignsAdapter(FragmentActivity context) {
        this.context=context;
    }

    @Override
    public TextCampaignHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        search_list=new ArrayList<>();
        layout=inflater.inflate(R.layout.text_campaign_row_layout,parent,false);
        TextCampaignHolder textCampaignHolder=new TextCampaignHolder(layout);
        return textCampaignHolder;
    }

    @Override
    public void onBindViewHolder(final TextCampaignHolder holder, final int position) {
        setAnimation(holder.itemView,position);
        holder.tv_text_campaign_name.setText(textCampaignList.get(position).getTxtCampName());

        if (textMsgCount==0){
            holder.ib_text_campaign_menu.setEnabled(false);
            Log.d(Global.TAG, "onBindViewHolder: textMsgCount is 0 ");
        }else if(textMsgCount>0){
            Log.d(Global.TAG, "onBindViewHolder: textMsgCount is not 0 ");
            holder.ib_text_campaign_menu.setEnabled(true);
            holder.ib_text_campaign_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (textCampaignList.get(position).getTxtCampfeature().equals("1")){
                        showAddMemberMenu(position,holder);
                    }else if (textCampaignList.get(position).getTxtCampfeature().equals("0")){
                        showAllPopUpMenu(position,holder);
                    }

                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getTextMessageList(position);

                }
            });

        }

        Picasso.with(context)
                .load(textCampaignList.get(position).getCampaignImage())
                .resize(400, 400)
                .into( holder.iv_text_campaign);

        List<Integer> colorList=textCampaignList.get(position).getCampaignColor();
        if (colorList.size()>0){
            holder.ll_text_campaign.setCardBackgroundColor(Color.rgb(colorList.get(0),colorList.get(1),colorList.get(2)));
        }

    }

    @Override
    public void onViewDetachedFromWindow(TextCampaignHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        Log.d(Global.TAG, "setAnimation: ");
        Animation animation = AnimationUtils.loadAnimation(context,
                (position > lastPosition) ? R.anim.up_from_bottom
                        : R.anim.down_from_top);
        viewToAnimate.startAnimation(animation);
        lastPosition = position;
    }
    @Override
    public int getItemCount() {
        return textCampaignList.size();
    }

    private void showAllPopUpMenu(final int position, final TextCampaignHolder holder) {
        PopupMenu popup = new PopupMenu(context, holder.ib_text_campaign_menu);
        popup.getMenuInflater().inflate(R.menu.text_campaign_context_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                    switch(menuItem.getItemId()){
                        case R.id.action_create_text_message:
                            //openCreateTextMessageDialog(position);
                            Intent intent=new Intent(context, CreateNewTextMessageActivity.class);
                            Bundle bundle=new Bundle();
                            bundle.putString("txtCampId",textCampaignList.get(position).getTxtCampId());
                            intent.putExtras(bundle);
                            textFragment.startActivityForResult(intent,10);
                            break;
                        case R.id.action_add_member:
                            CheckMessagesForAddMember(position);
                            break;
                        case R.id.action_start_text_camp:
                            CheckMessagesForStartCampaign(position);
                            break;
                        case R.id.action_remove_member:
                            CheckMessagesForRemoveMember(position);
                            break;
                        case R.id.action_edit_text_campaign:
                            openDailogUpdateTextCampaign(holder,position);
                            break;
                        case R.id.action_delete_text_campaign:
                            new AlertDialog.Builder(context)
                                    .setMessage("Are you sure you want to Delete this Text Campaign..?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            deleteTextCampaign(position);
                                        }
                                    })
                                    .setNegativeButton("No", null)
                                    .show();
                            break;
                    }


                return false;
            }
        });

        popup.show();//showing popup menu
    }

    private void showAddMemberMenu(final int position, final TextCampaignHolder holder) {
        PopupMenu popup = new PopupMenu(context, holder.ib_text_campaign_menu);
        popup.getMenuInflater().inflate(R.menu.text_predefine_camp_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.action_add_memberp:
                        CheckMessagesForAddMember(position);
                        break;
                    case R.id.action_remove_memberp:
                        CheckMessagesForRemoveMember(position);
                        break;
                    case R.id.action_start_campaignp:
                        CheckMessagesForStartCampaign(position);
                        break;
                }
                return false;
            }
        });

        popup.show();//showing popup menu
    }

    private void CheckMessagesForRemoveMember(final int position) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("txtCampId",textCampaignList.get(position).getTxtCampId());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getRemoveMemberList: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getRemoveMemberList: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<GetRemoveMemberList> call=service.getRemoveMemberList(paramObj.toString());
        call.enqueue(new Callback<GetRemoveMemberList>() {
            @Override
            public void onResponse(Call<GetRemoveMemberList> call, Response<GetRemoveMemberList> response) {
                GetRemoveMemberList getRemoveMemberList=response.body();
                if (getRemoveMemberList!=null){
                    if (getRemoveMemberList.isSuccess()){
                        RemoveMember removeMember=getRemoveMemberList.getResult();
                        List<RMember> rMemberList=removeMember.getMemberDetails();
                        String camp_name=removeMember.getTxtCampName();
                        Log.d(Global.TAG, "rMemberList: "+rMemberList.size());
                        Log.d(Global.TAG, "rMemberList: camp_name:"+camp_name);

                        if (rMemberList.size()>0){
                           openDialogRemoveMember(position);
                        }else{
                            Toast.makeText(context, "No members available to remove..!", Toast.LENGTH_LONG).show();
                        }

                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetRemoveMemberList> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "GetRemoveMemberList onFailure: "+t);
                Toast.makeText(context, "No members available to remove..!", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void openDialogRemoveMember(final int position) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.remove_member_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Confirmation");

        Button btn_remove_member=dialog.findViewById(R.id.btn_remove_member);
         rv_remove_members=dialog.findViewById(R.id.rv_remove_members);
        Button btn_rmember_close=dialog.findViewById(R.id.btn_rmember_close);
         tv_text_rtemplate_name=dialog.findViewById(R.id.tv_text_rtemplate_name);
        rv_remove_members.setLayoutManager(new LinearLayoutManager(context));


        getRemoveMemberList(position);

        btn_rmember_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_remove_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(removeMemberIds.size()>0){
                    new android.support.v7.app.AlertDialog.Builder(context)
                            .setMessage("Are you sure you want to unassigned this member from current campaign '"+ remove_camp_name+"'..?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    removeMemberFromList(position);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }else{
                    Toast.makeText(context, "Please Select at least one member to unassign.", Toast.LENGTH_LONG).show();
                }



            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void removeMemberFromList(final int position) {
        Log.d(Global.TAG, "removeMemberFromList: "+removeMemberIds.size());
        String removeMemberString="";
        if (removeMemberIds.size()>0){
            for (String member : removeMemberIds)
            {
                removeMemberString += member + ",";
            }
            if (removeMemberString.endsWith(",")) {
                removeMemberString = removeMemberString.substring(0, removeMemberString.length() - 1);
            }
            Log.d(Global.TAG, "removeMemberFromList: "+removeMemberString);
        }
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("txtcontactCampaignIds",removeMemberString);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "removeMemberFromList: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "removeMemberFromList: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.remove_member_from_text_campaign(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult!=null){
                    if (jsonResult.isSuccess()){
                        Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                        removeMemberIds.clear();
                        getRemoveMemberList(position);
                    }else{
                        Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    }
                }
               myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: removeMemberFromList:"+t);
            }
        });


    }

    private void getRemoveMemberList(int position) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("txtCampId",textCampaignList.get(position).getTxtCampId());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getRemoveMemberList: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getRemoveMemberList: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<GetRemoveMemberList> call=service.getRemoveMemberList(paramObj.toString());
        call.enqueue(new Callback<GetRemoveMemberList>() {
            @Override
            public void onResponse(Call<GetRemoveMemberList> call, Response<GetRemoveMemberList> response) {
                GetRemoveMemberList getRemoveMemberList=response.body();
                if (getRemoveMemberList!=null){
                    if (getRemoveMemberList.isSuccess()){
                        RemoveMember removeMember=getRemoveMemberList.getResult();
                        List<RMember> rMemberList=removeMember.getMemberDetails();
                         remove_camp_name=removeMember.getTxtCampName();
                        Log.d(Global.TAG, "rMemberList: "+rMemberList.size());
                        Log.d(Global.TAG, "rMemberList: camp_name:"+remove_camp_name);
                        if (remove_camp_name!=null){
                            tv_text_rtemplate_name.setText(remove_camp_name);
                        }

                        if (rMemberList.size()>0){
                            removeMemberIds.clear();
                            RemoveMemberAdapter adapter=new RemoveMemberAdapter(context,rMemberList);
                            rv_remove_members.setAdapter(adapter);
                        }else{
                            Toast.makeText(context, "No member available to remove..!", Toast.LENGTH_LONG).show();
                        }

                    }else{
                        Toast.makeText(context, "No member available to remove..!", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetRemoveMemberList> call, Throwable t) {
                Toast.makeText(context, "No member available to remove..!", Toast.LENGTH_LONG).show();
                dialog.dismiss();
                myLoader.dismiss();
                Log.d(Global.TAG, "GetRemoveMemberList onFailure: "+t);
            }
        });
    }

    private void deleteTextCampaign(int position) {

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("campIdToDelete",textCampaignList.get(position).getTxtCampId() );
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "deleteTextCampaign: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "deleteTextCampaign: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.delete_text_campaign(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    notifyRefresh.refresh();
                }else{
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: deleteTextCampaign "+t);
            }
        });
    }

    private void openDialogAddMember(final int position) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.add_member_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Confirmation");

        Button btn_member_close = (Button) dialog.findViewById(R.id.btn_member_close);
        final RadioGroup rbg_add_from = (RadioGroup) dialog.findViewById(R.id.rbg_add_from);
        RadioButton rb_add_from_contacts = (RadioButton) dialog.findViewById(R.id.rb_add_from_contacts);
        RadioButton add_manually = (RadioButton) dialog.findViewById(R.id.add_manually);
        ll_add_from_system = (LinearLayout) dialog.findViewById(R.id.ll_add_from_system);
        ll_add_manually = (LinearLayout) dialog.findViewById(R.id.ll_add_manually);
        edt_member_fname=(EditText) dialog.findViewById(R.id.edt_member_fname);
        edt_member_lname=(EditText)dialog.findViewById(R.id.edt_member_lname);
        //edt_email_add_member=(EditText)dialog.findViewById(R.id.edt_email_add_member);
        edt_mobile_add_member=(EditText)dialog.findViewById(R.id.edt_mobile_add_member);
        tv_my_contact_member=(ToggleButton)dialog.findViewById(R.id.tv_my_contact_member);
        tv_my_prospect_member=(ToggleButton)dialog.findViewById(R.id.tv_my_prospect_member);
        tv_my_customers_member=(ToggleButton)dialog.findViewById(R.id.tv_my_customers_member);
        tv_my_recruit_member=(ToggleButton)dialog.findViewById(R.id.tv_my_recruit_member);
        rv_add_member=(RecyclerView)dialog.findViewById(R.id.rv_add_member);
        rv_add_member.setLayoutManager(new LinearLayoutManager(context));
        btn_assign_member=(Button) dialog.findViewById(R.id.btn_assign_member);
        btn_assign_member_cancel=(Button) dialog.findViewById(R.id.btn_assign_member_cancel);
        Button btn_assign_member_and_start_camp=(Button) dialog.findViewById(R.id.btn_assign_member_and_start_camp);
        tv_selected_members=(TextView) dialog.findViewById(R.id.tv_selected_members);
        search_add_member=(SearchView) dialog.findViewById(R.id.search_add_member);
        search_add_member.setIconified(false);
        search_add_member.setFocusable(false);
        search_add_member.setQueryHint("Search Contact");
        search_add_member.clearFocus();

        rb_add_from_contacts.setChecked(true);
        rbg_add_from.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int selected_rb) {
                if (selected_rb==R.id.rb_add_from_contacts){
                    ll_add_from_system.setVisibility(View.VISIBLE);
                    ll_add_manually.setVisibility(View.GONE);
                }else if (selected_rb==R.id.add_manually){
                    ll_add_from_system.setVisibility(View.GONE);
                    ll_add_manually.setVisibility(View.VISIBLE);
                }
            }
        });


        tv_my_contact_member.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    Log.d(Global.TAG, "onCheckedChanged: tv_my_contact checked");
                    tv_my_contact_member.setBackgroundColor(context.getResources().getColor(R.color.colorOragne));
                    tv_my_prospect_member.setChecked(false);
                    tv_my_customers_member.setChecked(false);
                    tv_my_recruit_member.setChecked(false);
                    //tv_selected_members.setText(""+selected_contact.size()+" Contact Selected");
                    getContactsListForTextCampaign("1");
                    crm_flag="1";
                    tv_selected_members.setText(selected_contact_list.size()+" Contacts Selected");
                }else{
                    Log.d(Global.TAG, "onCheckedChanged: tv_my_contact unchecked");
                    tv_my_contact_member.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
                }

            }
        });
        tv_my_prospect_member.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    Log.d(Global.TAG, "onCheckedChanged: tv_my_prospect checked");
                    tv_my_prospect_member.setBackgroundColor(context.getResources().getColor(R.color.colorOragne));
                    tv_my_contact_member.setChecked(false);
                    tv_my_customers_member.setChecked(false);
                    tv_my_recruit_member.setChecked(false);
                    //tv_selected_contacts.setText(""+selected_prospects.size()+" Prospects Selected");
                    getContactsListForTextCampaign("3");
                    crm_flag="3";
                    tv_selected_members.setText(selected_prospect_list.size()+" Prospects Selected");
                }else{
                    Log.d(Global.TAG, "onCheckedChanged: tv_my_prospect unchecked");
                    tv_my_prospect_member.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
                }
            }
        });
        tv_my_customers_member.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                if (checked){
                    Log.d(Global.TAG, "onCheckedChanged: tv_my_customers checked");
                    tv_my_customers_member.setBackgroundColor(context.getResources().getColor(R.color.colorOragne));
                    tv_my_contact_member.setChecked(false);
                    tv_my_prospect_member.setChecked(false);
                    tv_my_recruit_member.setChecked(false);
                    //tv_selected_contacts.setText(""+selected_customers.size()+" Customers Selected");
                    getContactsListForTextCampaign("2");
                    crm_flag="2";
                    tv_selected_members.setText(selected_customers_list.size()+" Customers Selected");
                }else{
                    Log.d(Global.TAG, "onCheckedChanged: tv_my_customers unchecked");
                    tv_my_customers_member.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
                }
            }
        });
        tv_my_recruit_member.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                if (checked){
                    Log.d(Global.TAG, "onCheckedChanged: tv_my_recruit_member checked");
                    tv_my_recruit_member.setBackgroundColor(context.getResources().getColor(R.color.colorOragne));
                    tv_my_contact_member.setChecked(false);
                    tv_my_prospect_member.setChecked(false);
                    tv_my_customers_member.setChecked(false);
                    //tv_selected_contacts.setText(""+selected_customers.size()+" Customers Selected");
                    getContactsListForTextCampaign("4");
                    crm_flag="4";
                    tv_selected_members.setText(selected_recruits_list.size()+" Recruits Selected");
                }else{
                    Log.d(Global.TAG, "onCheckedChanged: tv_my_recruit_member unchecked");
                    tv_my_recruit_member.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
                }
            }
        });

        tv_my_contact_member.setChecked(true);

        btn_assign_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rbg_add_from.getCheckedRadioButtonId()==R.id.add_manually){
                    if (MyValidator.isValidFieldE(edt_member_fname,"Enter First Name")){
                            if (MyValidator.isValidMobileNo(edt_mobile_add_member)){
                                if(validateMobile()){
                                    addMemberManually(position,dialog,"0");
                                }

                            }
                    }

                }else if (rbg_add_from.getCheckedRadioButtonId()==R.id.rb_add_from_contacts){
                    if (selected_all_contact_list.size()>0){
                        assignMemberFromContacts(dialog,position,"0");
                    }else{
                        Toast.makeText(context, "Please Select at least One Member to Add..!", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });

        btn_assign_member_and_start_camp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rbg_add_from.getCheckedRadioButtonId()==R.id.add_manually){
                    if (MyValidator.isValidFieldE(edt_member_fname,"Enter First Name")){
                        if (MyValidator.isValidMobileNo(edt_mobile_add_member)){
                            if(validateMobile()){
                                addMemberManually(position,dialog,"1");
                            }

                        }
                    }

                }else if (rbg_add_from.getCheckedRadioButtonId()==R.id.rb_add_from_contacts){
                    if (selected_all_contact_list.size()>0){
                        assignMemberFromContacts(dialog,position,"1");
                    }else{
                        Toast.makeText(context, "Please Select at least One Member to Add..!", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });

        search_add_member.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search_list.clear();
                for (int i=0;i<contactList.size();i++) {
                    String first_name = contactList.get(i).getContact_fname();
                    String last_name = contactList.get(i).getContact_lname();

                    if (Pattern.compile(Pattern.quote(newText), Pattern.CASE_INSENSITIVE).matcher(first_name).find()
                            || Pattern.compile(Pattern.quote(newText), Pattern.CASE_INSENSITIVE).matcher(last_name).find()) {
                        search_list.add(contactList.get(i));
                    }
                }
                Log.d(Global.TAG, "Search List:: " + search_list.size());
                if (search_list.size() > 0) {
                    rv_add_member.setVisibility(View.VISIBLE);
                    AddMemberAdapter adapter=new AddMemberAdapter(context,search_list,crm_flag,TextCampaignsAdapter.this);
                    rv_add_member.setAdapter(adapter);

                } else {
                    rv_add_member.setVisibility(View.GONE);
                }
                return false;
            }
        });




        btn_member_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_all_contact_list.clear();
                selected_contact_list.clear();
                selected_customers_list.clear();
                selected_prospect_list.clear();
                selected_recruits_list.clear();
                dialog.dismiss();
            }
        });

        btn_assign_member_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_all_contact_list.clear();
                selected_contact_list.clear();
                selected_customers_list.clear();
                selected_prospect_list.clear();
                selected_recruits_list.clear();
                dialog.dismiss();
            }
        });


        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private boolean validateMobile() {
        String mobile=edt_mobile_add_member.getText().toString();
        String numbers = mobile.replaceAll("[^\\d]", "");
        Log.d(Global.TAG, "validateMobile: numbers:"+numbers);
        if (numbers.length()>=10 && numbers.length()<12){
            return true;
        }else{
            edt_mobile_add_member.setError("Enter 10 digit Mobile");
            return false;
        }
    }

    private void assignMemberFromContacts(final Dialog dialog, int position,String addAndAssign) {
        addMembersList="";
        if (selected_all_contact_list.size()>0){
            for (String contact : selected_all_contact_list)
            {
                addMembersList += contact + ",";
            }
            if (addMembersList.endsWith(",")) {
                addMembersList = addMembersList.substring(0, addMembersList.length() - 1);
            }
        }
        Log.d(Global.TAG, "assignMemberFromContacts: "+selected_all_contact_list.size());
        Log.d(Global.TAG, "addMembersList: "+addMembersList);

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("addAndAssinged", addAndAssign);
            paramObj.put("txt_contact_ids",addMembersList);
            paramObj.put("txtcontactMainCampaignId",textCampaignList.get(position).getTxtCampId());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "assignMemberFromContacts: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "assignMemberFromContacts: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.add_member_from_contact(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    selected_all_contact_list.clear();
                    selected_contact_list.clear();
                    selected_customers_list.clear();
                    selected_prospect_list.clear();
                    selected_recruits_list.clear();
                    notifyRefresh.refresh();
                    dialog.dismiss();
                }else{
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure assignMemberFromContacts: "+t);
            }
        });
    }

    private void getContactsListForTextCampaign(final String crm_flag) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("crmFlag",crm_flag);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getContactsListForTextCampaign: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getContactsListForTextCampaign: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<ContactDetails> call=service.getContactListTextCampaign(paramObj.toString());
        call.enqueue(new Callback<ContactDetails>() {
            @Override
            public void onResponse(Call<ContactDetails> call, Response<ContactDetails> response) {
                ContactDetails contactDetails=response.body();
                if (contactDetails.isSuccess()){
                    contactList=contactDetails.getResult();
                    Log.d(Global.TAG, "onResponse:contactList "+contactList.size());
                    if (contactList.size()>0){
                        rv_add_member.setVisibility(View.VISIBLE);
                       /* AddGroupContactsAdapter adapter=new AddGroupContactsAdapter(context,contactList,user_id,crm_flag,false);
                        adapter.notifyDataSetChanged();*/
                       AddMemberAdapter adapter=new AddMemberAdapter(context,contactList,crm_flag,TextCampaignsAdapter.this);
                        rv_add_member.setAdapter(adapter);

                    }else{
                        rv_add_member.setVisibility(View.GONE);
                        Toast.makeText(context, "No Contacts Available", Toast.LENGTH_LONG).show();
                    }
                }else{
                    rv_add_member.setVisibility(View.GONE);
                    Toast.makeText(context, "No Contacts Available", Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<ContactDetails> call, Throwable t) {
                rv_add_member.setVisibility(View.GONE);
                if (crm_flag.equals("1")){
                    Toast.makeText(context, "No Contacts Available", Toast.LENGTH_LONG).show();
                }else if (crm_flag.equals("2")){
                    Toast.makeText(context, "No Customers Available", Toast.LENGTH_LONG).show();
                }else if (crm_flag.equals("3")){
                    Toast.makeText(context, "No Prospect Available", Toast.LENGTH_LONG).show();
                }else if (crm_flag.equals("4")){
                    Toast.makeText(context, "No Recruits Available", Toast.LENGTH_LONG).show();
                }

                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: ");
            }
        });

    }

    private void addMemberManually(int position, final Dialog dialog,String addAndAssign) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("addAndAssinged", addAndAssign);
            paramObj.put("txtcontactMainCampaignId",textCampaignList.get(position).getTxtCampId());
            paramObj.put("fname",edt_member_fname.getText().toString());
            paramObj.put("lname",edt_member_lname.getText().toString());
            paramObj.put("email","");
            paramObj.put("phone",edt_mobile_add_member.getText().toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "addMemberManually: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "addMemberManually: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.add_member_manually(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (response!=null){
                    if (jsonResult.isSuccess()){
                        Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                        notifyRefresh.refresh();
                        dialog.dismiss();
                    }else{
                        Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    }
                }

                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: addMemberManually:"+t);
            }
        });
    }

    private void openCreateTextMessageDialog(final int position) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.new_text_campaign_message_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Confirmation");

        Button btn_text_dissmiss = (Button) dialog.findViewById(R.id.btn_text_dissmiss);
        edt_text_message_name = (EditText) dialog.findViewById(R.id.edt_text_message_name);
        edt_text_message = (EditText) dialog.findViewById(R.id.edt_text_message);
//        TextView tv_attached_files = (TextView) dialog.findViewById(R.id.tv_attached_files);
//        btn_attach_file = (Button) dialog.findViewById(R.id.btn_attach_file);
        edt_time_interval_text_message = (EditText) dialog.findViewById(R.id.edt_time_interval_text_message);
        sp_select_message_time_interval = (Spinner) dialog.findViewById(R.id.sp_select_message_time_interval);
        Button btn_save_text_message = (Button) dialog.findViewById(R.id.btn_save_text_message);
        Button btn_cancel_text_message = (Button) dialog.findViewById(R.id.btn_cancel_text_message);

        applySpinner(intervalType,sp_select_message_time_interval,"--Select--");

        sp_select_message_time_interval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (sp_select_message_time_interval.getSelectedItem().toString().contains("Select")) {
                    text_message_interval_type = "";
                } else {
                    text_message_interval_type = intervalType[position-1];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btn_cancel_text_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_save_text_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyValidator.isValidFieldE(edt_text_message_name,"Enter Message Title")){
                    if (MyValidator.isValidFieldE(edt_text_message,"Enter Text Message")){
                        if (MyValidator.isValidFieldE(edt_time_interval_text_message,"Enter Time Interval")){
                          if (!sp_select_message_time_interval.getSelectedItem().toString().contains("Select")){
                              addTextMessage(position,dialog);
                          }else{
                              MyValidator.setSpinnerError(sp_select_message_time_interval,"Select");
                          }
                        }
                    }
                }

            }
        });
/*
        btn_attach_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectAttachMent();
            }
        });*/
        btn_text_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void selectAttachMent() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        textFragment.startActivityForResult(intent, 100);
    }



    /*private void selectAttachMent() {

        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        Intent newIntent = new Intent(Intent.ACTION_VIEW);
        String mimeType = myMime.getMimeTypeFromExtension(fileExt(getFile()).substring(1));
        newIntent.setDataAndType(Uri.fromFile(getFile()),mimeType);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(newIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_LONG).show();
        }
    }
    private String fileExt(String url) {
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }
    }
*/
    private void addTextMessage(int position, final Dialog dialog) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("txtTemplateCampId",textCampaignList.get(position).getTxtCampId());
            paramObj.put("txtTemplateTitle",edt_text_message_name.getText().toString());
            paramObj.put("txtTemplateMsg",edt_text_message.getText().toString());
            paramObj.put("txtTemplateInterval",edt_time_interval_text_message.getText().toString());
            paramObj.put("txtTemplateIntervalType",text_message_interval_type.toLowerCase());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "addTextMessage: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "addTextMessage: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.add_text_message(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }else{
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:addMessage: "+t);
            }
        });

    }
    private void applySpinner(final String[] taglist, Spinner sp_name, String tag_string) {
        //Adding spinner on Repeate By:: When Monthly Selected Add 12
        AddContactActivity.spinnerAdapter adapterRepeateDaily = new AddContactActivity.spinnerAdapter(context, android.R.layout.simple_list_item_1);
        adapterRepeateDaily.add(tag_string);
        adapterRepeateDaily.addAll(taglist);
        adapterRepeateDaily.add(tag_string);
        sp_name.setAdapter(adapterRepeateDaily);
        sp_name.setSelection(adapterRepeateDaily.getCount());
        sp_name.setEnabled(true);
    }
    private void openDailogUpdateTextCampaign(final TextCampaignHolder holder, final int position) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.create_text_campaign_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Confirmation");

        final EditText edt_text_campaign_name=dialog.findViewById(R.id.edt_text_campaign_name);
        Button btn_save_text_campaign=dialog.findViewById(R.id.btn_save_text_campaign);
        Button btn_cancel_text_campaign=dialog.findViewById(R.id.btn_cancel_text_campaign);
        Button btn_text_dissmiss=dialog.findViewById(R.id.btn_text_create_dissmiss);
        TextView tv_create_tcamp_title=dialog.findViewById(R.id.tv_create_tcamp_title);

        edt_text_campaign_name.setText(textCampaignList.get(position).getTxtCampName());
        tv_create_tcamp_title.setText("Rename Text Campaign ");
        btn_save_text_campaign.setText("Update");

        btn_save_text_campaign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyValidator.isValidFieldE(edt_text_campaign_name,"Enter Campaign Name")){
                    updateTextCampaign(dialog,edt_text_campaign_name,holder,position);
                }
            }
        });
        btn_cancel_text_campaign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btn_text_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void updateTextCampaign(final Dialog dialog, EditText edt_text_campaign_name, TextCampaignHolder holder, int position) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("txtCampId",textCampaignList.get(position).getTxtCampId() );
            paramObj.put("campaignTitle", edt_text_campaign_name.getText().toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "updateTextCampaign: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "updateTextCampaign: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.update_text_campaign(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    notifyRefresh.refresh();
                    dialog.dismiss();
                }else{
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: updateTextCampaign"+t);
            }
        });
    }

    private void getTextMessageList(final int position) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("txtCampId",textCampaignList.get(position).getTxtCampId());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getTextMessageList: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getTextMessageList: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<GetAllTextMessages> call=service.get_text_messages(paramObj.toString());
        call.enqueue(new Callback<GetAllTextMessages>() {
            @Override
            public void onResponse(Call<GetAllTextMessages> call, Response<GetAllTextMessages> response) {
                GetAllTextMessages getAllTextMessages=response.body();
                if (getAllTextMessages!=null){
                    if (getAllTextMessages.IsSuccess){
                        List<TextMessage> textMessageList=getAllTextMessages.getResult();
                        Log.d(Global.TAG, "textMessageList: "+textMessageList.size());
                        if (textMessageList.size()>0){
                            dashboardActivity.replaceFragments(new TextMessagesListFragment(textCampaignList.get(position).getTxtCampId(),textCampaignList.get(position).getTxtCampName(),textCampaignList.get(position).getTxtCampfeature()));
                        }else{
                            Toast.makeText(context, "No Text Messages Added Yet..!", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(context, "No Text Messages Added Yet..!", Toast.LENGTH_LONG).show();
                    }
                }

                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetAllTextMessages> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: GetAllTextMessages:"+t);
            }
        });
    }
    private void CheckMessagesForAddMember(final int position) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("txtCampId",textCampaignList.get(position).getTxtCampId());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getTextMessageList: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getTextMessageList: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<GetAllTextMessages> call=service.get_text_messages(paramObj.toString());
        call.enqueue(new Callback<GetAllTextMessages>() {
            @Override
            public void onResponse(Call<GetAllTextMessages> call, Response<GetAllTextMessages> response) {
                GetAllTextMessages getAllTextMessages=response.body();
                if (getAllTextMessages.IsSuccess){
                    List<TextMessage> textMessageList=getAllTextMessages.getResult();
                    Log.d(Global.TAG, "textMessageList: "+textMessageList.size());
                    if (textMessageList.size()>0){
                        openDialogAddMember(position);
                        //dashboardActivity.replaceFragments(new TextMessagesListFragment(textCampaignList.get(position).getTxtCampId(),textCampaignList.get(position).getTxtCampName()));
                    }else{
                        Toast.makeText(context, "No Text Messages Added Yet..!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(context, "No Text Messages Added Yet..!", Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetAllTextMessages> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: GetAllTextMessages:"+t);
            }
        });
    }

    private void CheckMessagesForStartCampaign(final int position) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("txtCampId",textCampaignList.get(position).getTxtCampId());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getTextMessageList: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getTextMessageList: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<GetAllTextMessages> call=service.get_text_messages(paramObj.toString());
        call.enqueue(new Callback<GetAllTextMessages>() {
            @Override
            public void onResponse(Call<GetAllTextMessages> call, Response<GetAllTextMessages> response) {
                GetAllTextMessages getAllTextMessages=response.body();
                if (getAllTextMessages.IsSuccess){
                    List<TextMessage> textMessageList=getAllTextMessages.getResult();
                    Log.d(Global.TAG, "textMessageList: "+textMessageList.size());
                    if (textMessageList.size()>0){
                        openDialogStartCampaign(position);
                        //dashboardActivity.replaceFragments(new TextMessagesListFragment(textCampaignList.get(position).getTxtCampId(),textCampaignList.get(position).getTxtCampName()));
                    }else{
                        Toast.makeText(context, "No Text Messages Added Yet..!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(context, "No Text Messages Added Yet..!", Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetAllTextMessages> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: GetAllTextMessages:"+t);
            }
        });
    }
    private void openDialogStartCampaign(final int position) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.start_text_campaign);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Confirmation");

        TextView tv_no_member_to_start=dialog.findViewById(R.id.tv_no_member_to_start);
        TextView tv_added_txt_camp_date=dialog.findViewById(R.id.tv_added_txt_camp_date);
        Button btn_start_text_campaign=dialog.findViewById(R.id.btn_start_text_campaign);
        Button btn_start_txtcamp_close=dialog.findViewById(R.id.btn_start_txtcamp_close);
        rv_added_member_list=dialog.findViewById(R.id.rv_added_member_list);
        tv_added_text_camp_name=dialog.findViewById(R.id.tv_added_text_camp_name);
        rv_added_member_list.setLayoutManager(new LinearLayoutManager(context));


        getAddedMemberToStartCampaign(position,tv_no_member_to_start,btn_start_text_campaign,tv_added_txt_camp_date);

        btn_start_txtcamp_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCampaignList.clear();
                dialog.dismiss();
            }
        });

        btn_start_text_campaign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (startCampaignList.size()>0){
                    new AlertDialog.Builder(context)
                            .setMessage("Are you sure you want to start the current campaign for selected member")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    startTextCampaign(position);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }else{
                    Toast.makeText(context, "Please select at least one member, to start campaign.!", Toast.LENGTH_LONG).show();
                }



            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
    private void getAddedMemberToStartCampaign(int position, final TextView tv_no_member_to_start, final Button btn_start_campaign, final TextView tv_added_text_camp_date) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("txtCampId",textCampaignList.get(position).getTxtCampId());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getAddedMemberToStartCampaign: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getAddedMemberToStartCampaign: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<GetAddedMemberToStartCampaign> call=service.getAddedMemberToStartCampaign(paramObj.toString());
        call.enqueue(new Callback<GetAddedMemberToStartCampaign>() {
            @Override
            public void onResponse(Call<GetAddedMemberToStartCampaign> call, Response<GetAddedMemberToStartCampaign> response) {
                if (response.isSuccessful()){
                    GetAddedMemberToStartCampaign getAddedMemberToStartCampaign=response.body();
                    if (getAddedMemberToStartCampaign.isSuccess()){
                        AddedMember addedMember=getAddedMemberToStartCampaign.getResult();
                        List<AddedMembers> addeddMemberList=addedMember.getCampaignEmails();
                        String camp_name=addedMember.getTxtCampName();
                        String camp_date=addedMember.getCampaignDateTime();
                       // scheduleMsg=removeEmail.getScheduleMessage();

                        Log.d(Global.TAG, "addeddMemberList text: "+addeddMemberList.size());
                        Log.d(Global.TAG, "addeddEmailList: camp_name:"+camp_name);
                        if (camp_name!=null){
                            tv_added_text_camp_name.setText(camp_name);
                        }
                        if (camp_date!=null){
                            tv_added_text_camp_date.setText(camp_date);
                        }

                        if (addeddMemberList.size()>0){
                            btn_start_campaign.setVisibility(View.VISIBLE);
                            rv_added_member_list.setVisibility(View.VISIBLE);
                            tv_no_member_to_start.setVisibility(View.GONE);
                            StartTextCampAdapter adapter =new StartTextCampAdapter(context,addeddMemberList,TextCampaignsAdapter.this);
                            rv_added_member_list.setAdapter(adapter);
                        }else{
                            rv_added_member_list.setVisibility(View.GONE);
                            tv_no_member_to_start.setVisibility(View.VISIBLE);
                            btn_start_campaign.setVisibility(View.GONE);
                            Toast.makeText(context, "No Emails available !", Toast.LENGTH_LONG).show();
                        }

                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetAddedMemberToStartCampaign> call, Throwable t) {
                myLoader.dismiss();
                rv_added_member_list.setVisibility(View.GONE);
                tv_no_member_to_start.setVisibility(View.VISIBLE);
                btn_start_campaign.setVisibility(View.GONE);
                Toast.makeText(context, "No Members available !", Toast.LENGTH_LONG).show();
                Log.d(Global.TAG, "getAddedMemberListToStart onFailure: "+t);
            }
        });
    }
    private void startTextCampaign(final int position) {

        startCampaignIdsListString="";
        if (startCampaignList.size()>0){
            for (String contact : startCampaignList)
            {
                startCampaignIdsListString += contact + ",";
            }
            if (startCampaignIdsListString.endsWith(",")) {
                startCampaignIdsListString = startCampaignIdsListString.substring(0, startCampaignIdsListString.length() - 1);
            }
        }

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("txtCampId",textCampaignList.get(position).getTxtCampId());
            paramObj.put("txtcontactCampaignIds",startCampaignIdsListString);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "startTextCampaign: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "startTextCampaign: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.startTextCampaign(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult!=null){
                    if (jsonResult.isSuccess()){
                        Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                        startCampaignList.clear();
                        dialog.dismiss();
                    }else{
                        Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: startTextCampaign:"+t);
            }
        });


    }

    @Override
    public void addMember(String contact_id) {
        selected_all_contact_list.add(contact_id);
            if (crm_flag.equals("1")){
                selected_contact_list.add(contact_id);
                tv_selected_members.setText(selected_contact_list.size()+" Contacts Selected");
            }else if (crm_flag.equals("2")){
                selected_customers_list.add(contact_id);
                tv_selected_members.setText(selected_customers_list.size()+" Customers Selected");
            }else if (crm_flag.equals("3")){
                selected_prospect_list.add(contact_id);
                tv_selected_members.setText(selected_prospect_list.size()+" Prospects Selected");
            }else if (crm_flag.equals("4")){
                selected_recruits_list.add(contact_id);
                tv_selected_members.setText(selected_recruits_list.size()+" Recruits Selected");
            }
    }

    @Override
    public void removeMember(String contact_id) {
        selected_all_contact_list.remove(contact_id);
        if (crm_flag.equals("1")){
            selected_contact_list.remove(contact_id);
            tv_selected_members.setText(selected_contact_list.size()+" Contacts Selected");
        }else if (crm_flag.equals("2")){
            selected_customers_list.remove(contact_id);
            tv_selected_members.setText(selected_customers_list.size()+" Customers Selected");
        }else if (crm_flag.equals("3")){
            selected_prospect_list.remove(contact_id);
            tv_selected_members.setText(selected_prospect_list.size()+" Prospects Selected");
        }else if (crm_flag.equals("4")){
            selected_recruits_list.remove(contact_id);
            tv_selected_members.setText(selected_recruits_list.size()+" Recruits Selected");
        }
    }

    @Override
    public void addSMember(String member_id) {
        if (!startCampaignList.contains(member_id)){
            startCampaignList.add(member_id);
        }
        Log.d(Global.TAG, "startCampaignList: Add: "+startCampaignList);
    }

    @Override
    public void removeSMember(String member_id) {
        if (startCampaignList.contains(member_id)){
            startCampaignList.remove(member_id);
        }
        Log.d(Global.TAG, "startCampaignList: Remove:"+startCampaignList);
    }
}
