package com.success.successEntellus.adapter;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.success.successEntellus.R;
import com.success.successEntellus.activity.AddContactActivity;
import com.success.successEntellus.activity.CreateNewTextMessageActivity;
import com.success.successEntellus.activity.TextMessageReplyActivity;
import com.success.successEntellus.activity.UploadFromSavedDocumentActivity;
import com.success.successEntellus.fragment.TextMessagesListFragment;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.MyValidator;
import com.success.successEntellus.model.AttachMent;
import com.success.successEntellus.model.CheckTxtCampHasMember;
import com.success.successEntellus.model.EmailDetail;
import com.success.successEntellus.model.EmailDetails;
import com.success.successEntellus.model.GetAllTextMessages;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.model.Member;
import com.success.successEntellus.model.TextMessage;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;
import com.success.successEntellus.tooltip.SimpleTooltip;
import com.success.successEntellus.viewholder.TextMessageHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.wasabeef.richeditor.RichEditor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 7/2/2018.
 */

public class TextCampaignMessageAdapter extends RecyclerView.Adapter<TextMessageHolder>  {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1004;
    View layout;
    LayoutInflater inflater;
    FragmentActivity context;
    List<TextMessage> textMessageList;
    EditText edt_text_message_name,edt_text_message,edt_time_interval_text_message,edt_add_link,edt_ends_after,edt_tcti_ends_after;
    Spinner sp_select_message_time_interval,sp_tc_repeate_every;
    Button btn_attach_file;
    LinearLayout ll_attched_files;
    String[] intervalType={"Hours","Days","Week"};
    private String text_message_interval_type;
    String user_id;
    TextView tv_all_links,tv_text_message_name,tv_text_message,tv_text_msg_interval,tvtitle_attach_file,tv_text_from_saved_document;
    TextView tv_text_template_name,tv_text_template_created_on, tv_attached_files,tv_view_textmsg_created_on,tv_view_textcamp_title;
    TextView tv_tcrepeat_every,tv_tcrepeat_on,tv_link_heading,tv_tcrepeat_ends,tv_check_days_error;
    Button btn_delete_member,btn_delete_all_following_member;
    RecyclerView rv_member_details;
    LinearLayout ll_view_attachment_text,ll_view_links,ll_repeat_view,ll_custom_txttemplate_view,ll_predefine_txttemplateview,ll_txtfooter_view;
    LinearLayout ll_plain_text_message,ll_custom_message,ll_view_custom_text_layout,ll_link_layout,ll_text_header_details;
    WebView wv_txttemplate_content,wv_custom_text_msg;
    ImageButton ib_custom_help;
    private String selected_interval_type;
    public static List<String> deleteIds=new ArrayList<>();
    private String deleteMemberList;
    private String deleteAllFollowingMemberList;
    TextMessagesListFragment textMessagesListFragment;
    List<EditText> allEds = new ArrayList<EditText>();
    List<String> linkFlaglist=new ArrayList<>();
    TextView tv_add_link;
    LinearLayout ll_add_link;
    JSONArray linkArray;
    List<AttachMent> attachMentList;
    List<String> linkList;
    RadioGroup rbg_interval;
    RadioButton rb_immediately,rb_schedule,rb_repeat_text_camp,rb_footer_yes,rb_footer_no,rb_signature_yes,rb_signature_no;
    LinearLayout ll_scheduled_interval,ll_repeat_text_camp,ll_ti_repeat_text_camp;
    String txtCamp_name;
    Dialog memberdialog;
    List<String> repeat_every=new ArrayList<>();
    private String currDayName,textCampFeature;
    int month,days,year;
    private List<String> dayList=new ArrayList<>();
    private String dayString,repeat_every_string,repeat_every_TIstring,dayStringTI;
    CheckBox ch_tc_monday,ch_tc_tuesday,ch_tc_wednesday,ch_tc_thursday,ch_tc_friday,ch_tc_saturday,ch_tc_sunday;
    CheckBox ch_tcti_monday,ch_tcti_tuesday,ch_tcti_wednesday,ch_tcti_thursday,ch_tcti_friday,ch_tcti_saturday,ch_tcti_sunday;
    int lastposition=-1;
    private String txtTemplateFooterFlag;
    private int memberAssignedFlag=0;
    private String txtTemplateAddSignature="0";

    public String text_editor_image="";

    LinearLayout ll_formated_text,ll_plain_text,ll_attchments_layout_text;
    RadioButton rb_plain_text,rb_formatted_text;

    public RichEditor editor_text_msg_contents;
    TextView mPreview;
    String txtTemplateType="1";

    public TextCampaignMessageAdapter(FragmentActivity context) {
        this.context=context;
    }

    public  interface NotifyListRefresh{
        void refreshList();
        void sendParameters(LinearLayout ll_attched_files,String txtTemplate_id);
    }
    private NotifyListRefresh notifyListRefresh;

    public TextCampaignMessageAdapter(FragmentActivity activity, List<TextMessage> textMessageList, String user_id, NotifyListRefresh notifyListRefresh, TextMessagesListFragment textMessagesListFragment,String txtCamp_name,String textCampFeature) {
        context=activity;
        this.textMessageList=textMessageList;
        this.user_id=user_id;
        this.notifyListRefresh=notifyListRefresh;
        this.textMessagesListFragment=textMessagesListFragment;
        this.txtCamp_name=txtCamp_name;
        this.textCampFeature=textCampFeature;
    }

    @Override
    public TextMessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        linkArray=new JSONArray();
        attachMentList=new ArrayList<>();
        linkList=new ArrayList<>();
        layout=inflater.inflate(R.layout.text_message_row_layout,parent,false);
        TextMessageHolder holder=new TextMessageHolder(layout);
        return holder;
    }

    @Override
    public void onBindViewHolder(final TextMessageHolder holder, final int position) {
        holder.tv_text_message_name.setText(textMessageList.get(position).getTxtTemplateTitle());
        holder.tv_text_campaign_days.setText(textMessageList.get(position).getTxtTemplateInterval()+" "+textMessageList.get(position).getTxtTemplateIntervalType());

        if (textCampFeature.equals("1")){
            holder.ib_message_edit.setVisibility(View.GONE);
            holder.ib_text_message_delete.setVisibility(View.GONE);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openViewTextMessageDialog(position);
                }
            });

            holder.tv_text_member_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //openMemberDetailsDialog(position);
                    checkMemberDetails(position);
                }
            });

            if (textMessageList.get(position).getTxtTemplateImage()!=null){
                String templateImage=textMessageList.get(position).getTxtTemplateImage();
                Log.d(Global.TAG, "templateImage: "+templateImage);
                if (!templateImage.equals("")){
                    Picasso.with(context)
                            .load(templateImage)
                            .placeholder(R.drawable.place)   // optional
                            .error(R.drawable.error)      // optional
                            .resize(400, 400)
                            .into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    Log.d(Global.TAG, "onBitmapLoaded: ");
                                    holder.cv_textmessage.setBackground(new BitmapDrawable(bitmap));

                                }

                                @Override
                                public void onBitmapFailed(Drawable errorDrawable) {
                                    Log.d(Global.TAG, "onBitmapFailed: ");
                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {
                                    Log.d(Global.TAG, "onPrepareLoad: ");
                                }
                            });

                }

            }

        }else{
            holder.ib_message_edit.setVisibility(View.VISIBLE);
            holder.ib_text_message_delete.setVisibility(View.VISIBLE);

            holder.ib_message_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openEditTextMessageDialog(position);
                }
            });

            holder.ib_text_message_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(context)
                            .setMessage("Are you sure you want to Delete this Text Message..?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    deleteTextMessage(position);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();

                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openViewTextMessageDialog(position);
                }
            });

            holder.tv_text_campaign_days.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openSetTimeIntervalDialog(position);
                }
            });

            holder.tv_text_member_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //openMemberDetailsDialog(position);
                    checkMemberDetails(position);
                }
            });
        }

        holder.tv_reply_count.setText(textMessageList.get(position).getReplyCount());

        holder.ib_text_reply_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,TextMessageReplyActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("textCampStepId",textMessageList.get(position).getTxtTemplateId());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        int reply_count= Integer.parseInt(textMessageList.get(position).getReplyCount());

        if (reply_count>0){
            holder.fl_reply_count.setVisibility(View.VISIBLE);
        }else{
            holder.fl_reply_count.setVisibility(View.GONE);
        }

        setAnimation(holder.itemView,position);
    }

    @Override
    public void onViewDetachedFromWindow(TextMessageHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    private void setAnimation(View viewToAnimate, int position)
    {

        Animation animation = AnimationUtils.loadAnimation(context,
                (position > lastposition) ? R.anim.up_from_bottom
                        : R.anim.down_from_top);
        viewToAnimate.startAnimation(animation);
        lastposition = position;
    }
    private void openMemberDetailsDialog(final int position) {
        memberdialog = new Dialog(context);
        memberdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        memberdialog.setCancelable(false);
        memberdialog.setContentView(R.layout.member_details_layout);
        memberdialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        memberdialog.setTitle("Confirmation");

        tv_text_template_name=(TextView) memberdialog.findViewById(R.id.tv_text_template_name);
        TextView tv_delete_note=(TextView) memberdialog.findViewById(R.id.tv_delete_note);
        TextView tv_delete_note1=(TextView) memberdialog.findViewById(R.id.tv_delete_note1);
        TextView tv_delete_note2=(TextView) memberdialog.findViewById(R.id.tv_delete_note2);
        tv_text_template_created_on=(TextView) memberdialog.findViewById(R.id.tv_text_template_created_on);
        rv_member_details=(RecyclerView) memberdialog.findViewById(R.id.rv_member_details);
        rv_member_details.setLayoutManager(new LinearLayoutManager(context));
        Button btn_member_dissmiss=(Button) memberdialog.findViewById(R.id.btn_member_dissmiss);
        btn_delete_member=(Button) memberdialog.findViewById(R.id.btn_delete_member);
        btn_delete_all_following_member=(Button) memberdialog.findViewById(R.id.btn_delete_all_following_member);

        getMemberDetails(position);

        Log.d(Global.TAG, "openMemberDetailsDialog: Repeat Flag:"+textMessageList.get(position).getTxtTemplateRepeat());
        if (textMessageList.get(position).getTxtTemplateRepeat()!=null){
            Log.d(Global.TAG, "openMemberDetailsDialog: ");
            if (textMessageList.get(position).getTxtTemplateRepeat().equals("1")){
                Log.d(Global.TAG, "Repeat 1: ");
                btn_delete_all_following_member.setVisibility(View.GONE);
            }else{
                btn_delete_all_following_member.setVisibility(View.VISIBLE);
            }
        }

        tv_delete_note.setText(Html.fromHtml(context.getResources().getString(R.string.text_details_note)));
        tv_delete_note1.setText(Html.fromHtml(context.getResources().getString(R.string.text_details_note1)));
        tv_delete_note2.setText(Html.fromHtml(context.getResources().getString(R.string.text_details_note2)));
        btn_member_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                memberdialog.dismiss();
            }
        });
        btn_delete_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deleteIds.size()>0){
                    new AlertDialog.Builder(context)
                            .setMessage("Are you sure you want to delete this member from current message template only ?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    deleteMember(position);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();

                }else{
                    Toast.makeText(context, "Please select member to delete..!", Toast.LENGTH_LONG).show();
                }
                
               
            }
        });

        btn_delete_all_following_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deleteIds.size()>0){
                    new AlertDialog.Builder(context)
                            .setMessage(" Are you sure you want to delete this member from current and following message templates ?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    deleteAllFollowingMember(position);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();

                }else{
                    Toast.makeText(context, "Please select member to delete..!", Toast.LENGTH_LONG).show();
                }
               
            }
        });

        memberdialog.show();
        Window window = memberdialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    }
    private void deleteMember(final int position) {
        Log.d(Global.TAG, "deleteMember: ");

        deleteMemberList="";
        if (deleteIds.size()>0){
            for (String contact : deleteIds)
            {
                deleteMemberList += contact + ",";
            }
            if (deleteMemberList.endsWith(",")) {
                deleteMemberList = deleteMemberList.substring(0, deleteMemberList.length() - 1);
            }
        }
        Log.d(Global.TAG, "deleteMember: "+deleteIds.size());
        Log.d(Global.TAG, "addMembersList: "+deleteMemberList);

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("txtTemplateId", textMessageList.get(position).getTxtTemplateId());
            paramObj.put("txtTemplateCampId",textMessageList.get(position).getTxtTemplateCampId());
            paramObj.put("emailDetails", deleteMemberList);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "deleteMember: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "deleteMember: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.delete_member(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(context, "Email(s) deleted successfully from this email template only", Toast.LENGTH_LONG).show();
                    getMemberDetails(position);
                }else{
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();

            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Toast.makeText(context, "Error in Deleting Member", Toast.LENGTH_LONG).show();
            }
        });


    }
    private void deleteAllFollowingMember(final int position) {

        deleteAllFollowingMemberList="";
        if (deleteIds.size()>0){
            for (String contact : deleteIds)
            {
                deleteAllFollowingMemberList += contact + ",";
            }
            if (deleteAllFollowingMemberList.endsWith(",")) {
                deleteAllFollowingMemberList = deleteAllFollowingMemberList.substring(0, deleteAllFollowingMemberList.length() - 1);
            }
        }
        Log.d(Global.TAG, "deleteMember: "+deleteIds.size());
        Log.d(Global.TAG, "addMembersList: "+deleteMemberList);

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("txtTemplateId", textMessageList.get(position).getTxtTemplateId());
            paramObj.put("txtTemplateCampId",textMessageList.get(position).getTxtTemplateCampId());
            paramObj.put("emailDetails", deleteAllFollowingMemberList);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "deleteAllFollowing: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "deleteAllFollowing: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.delete_all_following_member(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(context, "Email deleted successfully from this & all future templates.", Toast.LENGTH_LONG).show();
                    getMemberDetails(position);
                }else{
                    Toast.makeText(context, "Error in Deleting Email", Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Toast.makeText(context, "Error in Deleting Email", Toast.LENGTH_LONG).show();
            }
        });
    }
    private int checkMemberAssignedOrNot(int position) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", user_id);
            paramObj.put("platform", "2");
            paramObj.put("txtCampId", textMessageList.get(position).getTxtTemplateCampId());
            paramObj.put("stepId",textMessageList.get(position).getTxtTemplateId());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "checkMemberAssignedOrNot: " + e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "checkMemberAssignedOrNot: " + paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<CheckTxtCampHasMember> call = service.checkMemberAssignedOrNot(paramObj.toString());
        call.enqueue(new Callback<CheckTxtCampHasMember>() {
            @Override
            public void onResponse(Call<CheckTxtCampHasMember> call, Response<CheckTxtCampHasMember> response) {
                if (response.isSuccessful()){
                    CheckTxtCampHasMember checkTxtCampHasMember=response.body();
                    if (checkTxtCampHasMember.getResult()==1){
                        memberAssignedFlag=1;
                    }else if (checkTxtCampHasMember.getResult()==0){
                        memberAssignedFlag=0;
                    }

                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<CheckTxtCampHasMember> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:CheckTxtCampHasMember "+t);
            }
        });
        return memberAssignedFlag;
    }

    private void getMemberDetails(int position) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("txtTemplateId", textMessageList.get(position).getTxtTemplateId());
            paramObj.put("txtTemplateCampId", textMessageList.get(position).getTxtTemplateCampId());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getMemberDetails: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getMemberDetails: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<EmailDetails> call=service.getTextMessageAssignDetails(paramObj.toString());
        call.enqueue(new Callback<EmailDetails>() {
            @Override
            public void onResponse(Call<EmailDetails> call, Response<EmailDetails> response) {
                EmailDetails emailDetails=response.body();
                if (emailDetails.isSuccess()){
                    EmailDetail emailDetail=emailDetails.getResult();
                    //btn_delete_all_following_member.setVisibility(View.VISIBLE);
                    btn_delete_member.setVisibility(View.VISIBLE);
                    rv_member_details.setVisibility(View.VISIBLE);

                    tv_text_template_name.setText(emailDetail.getCampaignStepTitle());
                    tv_text_template_created_on.setText(emailDetail.getTxtTemplateAddDate());

                    List<Member> memberList=emailDetail.getMemberDetails();

                    if (memberList.size()>0){
                        Log.d(Global.TAG, "memberList: "+memberList.size());
                       MemberListAdapter memberListAdapter=new MemberListAdapter(context,memberList);
                       rv_member_details.setAdapter(memberListAdapter);
                       deleteIds.clear();
                    }else{
                        /*btn_delete_all_following_member.setVisibility(View.GONE);
                        btn_delete_member.setVisibility(View.GONE);
                        rv_member_details.setVisibility(View.GONE);*/
                        memberdialog.dismiss();
                        Toast.makeText(context, "No Members Available..!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(context, ""+emailDetails.getResult(), Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<EmailDetails> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: "+t);
            }
        });

    }
    private void checkMemberDetails(final int position) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("txtTemplateId", textMessageList.get(position).getTxtTemplateId());
            paramObj.put("txtTemplateCampId", textMessageList.get(position).getTxtTemplateCampId());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getMemberDetails: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getMemberDetails: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<EmailDetails> call=service.getTextMessageAssignDetails(paramObj.toString());
        call.enqueue(new Callback<EmailDetails>() {
            @Override
            public void onResponse(Call<EmailDetails> call, Response<EmailDetails> response) {
                EmailDetails emailDetails=response.body();
                if (emailDetails.isSuccess()){
                    EmailDetail emailDetail=emailDetails.getResult();
                    List<Member> memberList=emailDetail.getMemberDetails();

                    if (memberList.size()>0){
                        Log.d(Global.TAG, "memberList: "+memberList.size());
                       openMemberDetailsDialog(position);
                    }else{
                        Toast.makeText(context, "No Members Available..!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(context, ""+emailDetails.getResult(), Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<EmailDetails> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: "+t);
            }
        });

    }

    private void openSetTimeIntervalDialog(final int position) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.set_time_interval_text_campaign);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Confirmation");
        final EditText edt_time_intervald=(EditText)dialog.findViewById(R.id.edt_time_intervald);
       edt_tcti_ends_after=(EditText)dialog.findViewById(R.id.edt_tcti_ends_after);
        final Spinner sp_select_intervald=(Spinner)dialog.findViewById(R.id.sp_select_intervald);
        final Spinner sp_tcti_repeate_every=(Spinner)dialog.findViewById(R.id.sp_tcti_repeate_every);
        Button btn_set_interval=(Button) dialog.findViewById(R.id.btn_set_interval);
        Button btn_interval_cancel=(Button) dialog.findViewById(R.id.btn_timeinterval_cancel);
        Button btn_interval_dissmiss=(Button) dialog.findViewById(R.id.btn_interval_dissmiss);
        final LinearLayout ll_scheduled_time=dialog.findViewById(R.id.ll_scheduled_time);
        ll_ti_repeat_text_camp=dialog.findViewById(R.id.ll_ti_repeat_text_camp);
        RadioGroup rbg_interval=dialog.findViewById(R.id.rbg_interval);
        RadioButton rb_immediately=dialog.findViewById(R.id.rb_immediately);
        RadioButton rb_schedule=dialog.findViewById(R.id.rb_schedule);
        RadioButton rb_tcti_repeat=dialog.findViewById(R.id.rb_tcti_repeat);
        final TextView tv_check_tcti_days_error=dialog.findViewById(R.id.tv_check_tcti_days_error);

         ch_tcti_monday=dialog.findViewById(R.id.ch_tcti_monday);
         ch_tcti_tuesday=dialog.findViewById(R.id.ch_tcti_tuesday);
         ch_tcti_wednesday=dialog.findViewById(R.id.ch_tcti_wednesday);
         ch_tcti_thursday=dialog.findViewById(R.id.ch_tcti_thursday);
         ch_tcti_friday=dialog.findViewById(R.id.ch_tcti_friday);
         ch_tcti_saturday=dialog.findViewById(R.id.ch_tcti_saturday);
         ch_tcti_sunday=dialog.findViewById(R.id.ch_tcti_sunday);


        edt_time_intervald.setText(textMessageList.get(position).getTxtTemplateInterval());

        rb_immediately.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    ll_scheduled_time.setVisibility(View.GONE);
                    ll_ti_repeat_text_camp.setVisibility(View.GONE);
                }else{
                   // ll_scheduled_time.setVisibility(View.VISIBLE);

                }
            }
        });

        rb_schedule.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    ll_scheduled_time.setVisibility(View.VISIBLE);
                    ll_ti_repeat_text_camp.setVisibility(View.GONE);
                }else{
                    ll_scheduled_time.setVisibility(View.GONE);
                }
            }
        });

        rb_tcti_repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    ll_scheduled_time.setVisibility(View.GONE);
                    ll_ti_repeat_text_camp.setVisibility(View.VISIBLE);
                    fillSpinnerTIwith12(sp_tcti_repeate_every);
                }else{
                    ll_ti_repeat_text_camp.setVisibility(View.GONE);
                }
            }
        });

        if (textMessageList.get(position).getTxtTemplateInterval().equals("0")){
            rb_immediately.setChecked(true);
            ll_scheduled_time.setVisibility(View.GONE);
        }else if (textMessageList.get(position).getTxtTemplateRepeat().equals("1")) {
            rb_tcti_repeat.setChecked(true);
            ll_ti_repeat_text_camp.setVisibility(View.VISIBLE);
            ll_scheduled_time.setVisibility(View.GONE);
        }else{
            rb_schedule.setChecked(true);
            ll_scheduled_time.setVisibility(View.VISIBLE);
        }

        btn_interval_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_interval_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_set_interval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ll_scheduled_time.getVisibility()==View.VISIBLE){
                    if (MyValidator.isValidFieldE(edt_time_intervald,"Enter Time Interval")){
                        if (!sp_select_intervald.getSelectedItem().toString().contains("Select")){
                            setTimeInterval(position,edt_time_intervald.getText().toString(),dialog,ll_scheduled_time);
                        }else{
                            MyValidator.setSpinnerError(sp_select_intervald,"Select Type");
                        }
                    }
                }else{
                    if (ll_ti_repeat_text_camp.getVisibility()==View.VISIBLE){
                        if (!edt_tcti_ends_after.getText().toString().equals("")){
                            if (!ch_tcti_monday.isChecked() && !ch_tcti_tuesday.isChecked() && !ch_tcti_wednesday.isChecked() && !ch_tcti_thursday.isChecked() && !ch_tcti_friday.isChecked()
                                    && !ch_tcti_saturday.isChecked() && !ch_tcti_sunday.isChecked()){
                                tv_check_tcti_days_error.setVisibility(View.VISIBLE);
                            }else{
                                tv_check_tcti_days_error.setVisibility(View.GONE);
                                setTimeInterval(position,edt_time_intervald.getText().toString(),dialog,ll_scheduled_time);
                            }
                        }else{
                            edt_tcti_ends_after.setError("Enter Occurrences");
                        }
                    }else{
                        setTimeInterval(position,edt_time_intervald.getText().toString(),dialog,ll_scheduled_time);
                    }

                }
            }
        });


        applySpinner(intervalType,sp_select_intervald,"--Select--");
        String intervaltype=textMessageList.get(position).getTxtTemplateIntervalType();
        Log.d(Global.TAG, "Interval Type: "+intervaltype);
        if (intervaltype.equals("hours")){
            sp_select_intervald.setSelection(1);
        }else if (intervaltype.equals("days")){
            sp_select_intervald.setSelection(2);
        }else if (intervaltype.equals("week")){
            sp_select_intervald.setSelection(3);
        }

        if (textMessageList.get(position).getTxtTemplateRepeat().equals("1")){
            if (textMessageList.get(position).getTxtempRepeatWeeks()!=null){
                String repeat_interval_week=textMessageList.get(position).getTxtempRepeatWeeks();
                int interval_week=Integer.parseInt(repeat_interval_week);
                Log.d(Global.TAG, "repeat_interval_week : "+interval_week);
                sp_tcti_repeate_every.setSelection(interval_week-1);
            }

            if (textMessageList.get(position).getTxtempRepeatDays()!=null) {
                String repeat_week_days=textMessageList.get(position).getTxtempRepeatDays();
                if (!repeat_week_days.equals("")){
                    String[] week_days=repeat_week_days.split(",");
                    Log.d(Global.TAG, "week_days: "+week_days.length);
                    for(int i=0;i<week_days.length;i++){
                        Log.d(Global.TAG, "week_days: "+week_days[i]);
                        String day_name=week_days[i];
                        //String name=day_name.substring(0,day_name.length()-1);
                        selectWeekDaysTI(day_name);
                    }

                }
            }

            if (textMessageList.get(position).getTxtempRepeatEndOccurrence()!=null) {
                String repeat_occurances = textMessageList.get(position).getTxtempRepeatEndOccurrence();
                edt_tcti_ends_after.setText(repeat_occurances);
                Log.d(Global.TAG, "repeat_occurances: " + repeat_occurances);
            }

        }

        sp_select_intervald.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                if (sp_select_intervald.getSelectedItem().toString().contains("Select")) {
                    selected_interval_type = "";
                } else {
                    selected_interval_type = intervalType[position-1];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        sp_tcti_repeate_every.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (sp_tcti_repeate_every.getSelectedItem().toString().contains("Select")) {
                    repeat_every_TIstring = "";
                } else {
                    if (repeat_every.size()>0){
                        repeat_every_TIstring = repeat_every.get(position).toString();
                        Log.d(Global.TAG, "onItemSelected: repeat_every_string TI:"+repeat_every_TIstring);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                repeat_every_TIstring = "";
            }
        });


        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void selectWeekDaysTI(String day_name) {
        if (!day_name.equals("")){
            if (day_name.equalsIgnoreCase("mon")){
                ch_tcti_monday.setChecked(true);
            }else if (day_name.equalsIgnoreCase("tue")){
                ch_tcti_tuesday.setChecked(true);
            }else if (day_name.equalsIgnoreCase("wed")){
                ch_tcti_wednesday.setChecked(true);
            }else if (day_name.equalsIgnoreCase("thu")){
                ch_tcti_thursday.setChecked(true);
            }else if (day_name.equalsIgnoreCase("fri")){
                ch_tcti_friday.setChecked(true);
            }else if (day_name.equalsIgnoreCase("sat")){
                ch_tcti_saturday.setChecked(true);
            }else if (day_name.equalsIgnoreCase("sun")){
                ch_tcti_sunday.setChecked(true);
            }
        }
    }

    private void fillSpinnerTIwith12(Spinner sp_tcti_repeate_every) {
        repeat_every.clear();
        for (int i=1;i<=13;i++){
            repeat_every.add(String.valueOf(i));
        }
        applySpinner1(repeat_every,sp_tcti_repeate_every,"-Select-");
        sp_tcti_repeate_every.setSelection(0);
    }

    private void setTimeInterval(int position, String time_interval, final Dialog dialog, LinearLayout ll_scheduled_time) {
        String interval="";
        String interval_type="";
        String select_type="";
        if (ll_scheduled_time.getVisibility()==View.VISIBLE){
            interval=time_interval;
            interval_type=selected_interval_type.toLowerCase();
            select_type="2";
            repeat_every_TIstring="";
        }else if (ll_ti_repeat_text_camp.getVisibility()==View.VISIBLE){
            interval="";
            interval_type="";
            select_type="3";
        }else{
            interval="0";
            interval_type="hours";
            select_type="1";
            repeat_every_TIstring="";
        }
        getDaysSelectionTI();

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("selectType", select_type);
            paramObj.put("txtTemplateId", textMessageList.get(position).getTxtTemplateId());
            paramObj.put("txtTemplateInterval", interval);
            paramObj.put("txtTemplateIntervalType",interval_type);
            paramObj.put("repeat_every_weeks", repeat_every_TIstring);
            paramObj.put("repeat_on", dayStringTI);
            paramObj.put("repeat_ends_after", edt_tcti_ends_after.getText().toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "setTimeInterval: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "setTimeInterval: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.set_time_interval_text_campaign(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    notifyListRefresh.refreshList();
                    dialog.dismiss();
                }else{
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();

            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Toast.makeText(context, "Error in setting Time Interval", Toast.LENGTH_LONG).show();
                Log.d(Global.TAG, "onFailure setTimeInterval: "+t);
            }
        });
    }

    private void getDaysSelectionTI() {
        dayList.clear();

        if (ch_tcti_monday.isChecked()){
            dayList.add("Mon");
        }
        if (ch_tcti_tuesday.isChecked()){
            dayList.add("Tue");
        }
        if (ch_tcti_wednesday.isChecked()){
            dayList.add("Wed");
        }
        if (ch_tcti_thursday.isChecked()){
            dayList.add("Thu");
        }
        if (ch_tcti_friday.isChecked()){
            dayList.add("Fri");
        }
        if (ch_tcti_saturday.isChecked()){
            dayList.add("Sat");
        }
        if (ch_tcti_sunday.isChecked()){
            dayList.add("Sun");
        }

        dayStringTI="";
        for (String s : dayList)
        {
            dayStringTI += s + ",";
        }
        if (dayStringTI.endsWith(",")) {
            dayStringTI = dayStringTI.substring(0, dayStringTI.length() - 1);
        }
        Log.d(Global.TAG, "getDaysSelectionTI: "+dayStringTI);
    }


    private void deleteTextMessage(int position) {

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("txtTemplateId",textMessageList.get(position).getTxtTemplateId() );
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "deleteTextMessage: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "deleteTextMessage: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.delete_text_message(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    notifyListRefresh.refreshList();
                }else{
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: deleteTextMessage "+t);
            }
        });
    }

    @Override
    public int getItemCount() {
        return textMessageList.size();
    }

    private void openViewTextMessageDialog(final int position) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.text_campaign_view_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Confirmation");

        Button btn_text_dissmiss = (Button) dialog.findViewById(R.id.btn_text_dissmiss);
        tv_view_textcamp_title = (TextView) dialog.findViewById(R.id.tv_view_textcamp_title);
        tv_view_textmsg_created_on = (TextView) dialog.findViewById(R.id.tv_view_textmsg_created_on);
       // tv_all_links = (TextView) dialog.findViewById(R.id.tv_all_links);
        tv_text_message_name = (TextView) dialog.findViewById(R.id.tv_text_message_name);
        tv_text_message = (TextView) dialog.findViewById(R.id.tv_text_message);
        tv_attached_files = (TextView) dialog.findViewById(R.id.tv_attached_files);
        tvtitle_attach_file = (TextView) dialog.findViewById(R.id.tvtitle_attach_file);
        tv_text_msg_interval = (TextView) dialog.findViewById(R.id.tv_text_msg_interval);
        ll_view_attachment_text = (LinearLayout) dialog.findViewById(R.id.ll_view_attachment_text);
        ll_view_links = (LinearLayout) dialog.findViewById(R.id.ll_view_links);
        ll_predefine_txttemplateview = (LinearLayout) dialog.findViewById(R.id.ll_predefine_txttemplateview);
        ll_custom_txttemplate_view = (LinearLayout) dialog.findViewById(R.id.ll_custom_txttemplate_view);
        ll_txtfooter_view = (LinearLayout) dialog.findViewById(R.id.ll_txtfooter_view);
        //Button btn_cancel_text_message = (Button) dialog.findViewById(R.id.btn_cancel_text_message);
        ll_repeat_view = (LinearLayout) dialog.findViewById(R.id.ll_repeat_view);
        ll_plain_text_message = (LinearLayout) dialog.findViewById(R.id.ll_plain_text_message);
        ll_custom_message = (LinearLayout) dialog.findViewById(R.id.ll_custom_message);
        ll_text_header_details = (LinearLayout) dialog.findViewById(R.id.ll_text_header_details);

        ll_view_custom_text_layout = (LinearLayout) dialog.findViewById(R.id.ll_view_custom_text_layout);
        wv_txttemplate_content = (WebView) dialog.findViewById(R.id.wv_txttemplate_content);
        wv_custom_text_msg = (WebView) dialog.findViewById(R.id.wv_custom_text_msg);

        tv_tcrepeat_every = (TextView) dialog.findViewById(R.id.tv_tcrepeat_every);
        tv_tcrepeat_on = (TextView) dialog.findViewById(R.id.tv_tcrepeat_on);
        tv_tcrepeat_ends = (TextView) dialog.findViewById(R.id.tv_tcrepeat_ends);
        tv_link_heading = (TextView) dialog.findViewById(R.id.tv_link_heading);

        getMessageDetailsForView(position);

        tv_view_textcamp_title.setText(txtCamp_name);



        if (textMessageList.get(position).getTxtTemplateColor()!=null){
            String txtTemplateColor=textMessageList.get(position).getTxtTemplateColor();
            Log.d(Global.TAG, "openViewTextMessageDialog: txtTemplateColor:"+txtTemplateColor);
            if (!txtTemplateColor.equals("")){
                String[] colors=txtTemplateColor.split(",");
                if (colors.length>0){
                    wv_txttemplate_content.setBackgroundColor(Color.rgb(Integer.parseInt(colors[0].trim()),Integer.parseInt(colors[1].trim()),Integer.parseInt(colors[2].trim())));
                }
            }
        }

        if (textCampFeature.equals("1")){
            ll_predefine_txttemplateview.setVisibility(View.VISIBLE);
            ll_custom_txttemplate_view.setVisibility(View.GONE);
            ll_txtfooter_view.setVisibility(View.VISIBLE);
            ll_text_header_details.setVisibility(View.GONE);
        }else{
            ll_predefine_txttemplateview.setVisibility(View.VISIBLE);
            ll_custom_txttemplate_view.setVisibility(View.GONE);
            ll_txtfooter_view.setVisibility(View.VISIBLE);
            ll_text_header_details.setVisibility(View.VISIBLE);
            wv_txttemplate_content.setBackgroundColor(Color.WHITE);
        }

        if (textMessageList.get(position).getTxtTemplateType().equals("2")){
            ll_custom_message.setVisibility(View.GONE);
            ll_plain_text_message.setVisibility(View.GONE);
            if (textCampFeature.equals("1")){
                ll_view_custom_text_layout.setVisibility(View.GONE);
            }else{
                ll_view_custom_text_layout.setVisibility(View.VISIBLE);
            }

            if (textMessageList.get(position).getTxtTemplateColor()!=null){
                String txtTemplateColor=textMessageList.get(position).getTxtTemplateColor();
                Log.d(Global.TAG, "openViewTextMessageDialog: txtTemplateColor:"+txtTemplateColor);
                if (!txtTemplateColor.equals("")){
                    String[] colors=txtTemplateColor.split(",");
                    if (colors.length>0){
                        //ll_predefine_txttemplateview.setBackgroundColor(Color.rgb(Integer.parseInt(colors[0].trim()),Integer.parseInt(colors[1].trim()),Integer.parseInt(colors[2].trim())));
                        ll_predefine_txttemplateview.setBackgroundColor(Color.WHITE);

                    }
                }
            }

        }else if(textMessageList.get(position).getTxtTemplateType().equals("1")){
            ll_custom_message.setVisibility(View.GONE);
            ll_plain_text_message.setVisibility(View.GONE);
            ll_view_custom_text_layout.setVisibility(View.GONE);
        }

        txtTemplateFooterFlag=textMessageList.get(position).getTxtTemplateFooterFlag();

        Log.d(Global.TAG, ":txtTemplateFooterFlag: "+txtTemplateFooterFlag);
        if (txtTemplateFooterFlag.equals("1")){
            ll_txtfooter_view.setVisibility(View.VISIBLE);
        }else{
            ll_txtfooter_view.setVisibility(View.GONE);
        }


        String str = textMessageList.get(position).getTxtTemplateMsg();
        str = str.replaceAll("(\r\n|\n)", "<br />");
        Log.d(Global.TAG, "Text Message after replacing: "+str);

        wv_txttemplate_content.loadDataWithBaseURL(null, str, "text/html", "UTF-8", "UTF-8");
        wv_custom_text_msg.loadDataWithBaseURL(null, str, "text/html", "UTF-8", "UTF-8");


        String created_date=textMessageList.get(position).getTxtTemplateAddDate();
        String[] date = created_date.split("\\s");
        String newDate=date[0];
        Log.d(Global.TAG, "openViewTextMessageDialog: newDate:"+newDate);

        tv_view_textmsg_created_on.setText(newDate);
        tv_text_message_name.setText(textMessageList.get(position).getTxtTemplateTitle());
        tv_text_message.setText(Html.fromHtml(textMessageList.get(position).getTxtTemplateMsg()));

        String interval=textMessageList.get(position).getTxtTemplateInterval();
        String interval_type=textMessageList.get(position).getTxtTemplateIntervalType();

        Log.d(Global.TAG, "openViewTextMessageDialog: Repeat Flag: "+textMessageList.get(position).getTxtTemplateRepeat());
        if (textMessageList.get(position).getTxtTemplateRepeat().equals("1")){
            tv_text_msg_interval.setVisibility(View.GONE);
            ll_repeat_view.setVisibility(View.VISIBLE);

            if (textMessageList.get(position).getTxtempRepeatWeeks()!=null){
                tv_tcrepeat_every.setText("Repeat Every "+textMessageList.get(position).getTxtempRepeatWeeks()+" weeks");
            }

            if (textMessageList.get(position).getTxtempRepeatDays()!=null){
                tv_tcrepeat_on.setText("Repeat on "+textMessageList.get(position).getTxtempRepeatDays());
            }

            if (textMessageList.get(position).getTxtempRepeatEndOccurrence()!=null){
                tv_tcrepeat_ends.setText("Repeat ends after "+textMessageList.get(position).getTxtempRepeatEndOccurrence()+" weeks");
            }
        }else{
            tv_text_msg_interval.setVisibility(View.VISIBLE);
            ll_repeat_view.setVisibility(View.GONE);

            if (interval.equals("0")){
                tv_text_msg_interval.setText("Immediately");
            }else{
                tv_text_msg_interval.setText(interval+" "+interval_type);
            }
        }

       /* btn_cancel_text_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
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

    private void getMessageDetailsOnId(int position) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("txtTemplateId",textMessageList.get(position).getTxtTemplateId());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getMessageDetailsOnId: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getMessageDetailsOnId: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<GetAllTextMessages> call=service.getMessageDetailsOnId(paramObj.toString());
        call.enqueue(new Callback<GetAllTextMessages>() {
            @Override
            public void onResponse(Call<GetAllTextMessages> call, Response<GetAllTextMessages> response) {
                GetAllTextMessages getDetails=response.body();
                if (getDetails.isSuccess()){

                    List<TextMessage> messageDetails=getDetails.getResult();
                    textMessagesListFragment.deleteAttachmentId.clear();
                    if (messageDetails.size()==1){
                       attachMentList=messageDetails.get(0).getAttachements();
                        Log.d(Global.TAG, "AttachmentList: "+attachMentList.size());
                        String attached_names="";
                        for (int i=0;i<attachMentList.size();i++){
                            String file_path=attachMentList.get(i).getFilePath();
                            String file_name=file_path.substring(file_path.lastIndexOf("/")+1,file_path.length());
                            Log.d(Global.TAG, "onResponse: "+file_name);
                        }
                        linkList=messageDetails.get(0).getLinks();
                        Log.d(Global.TAG, "linkList size: "+linkList.size());
                    }

                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetAllTextMessages> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: getDetails:"+t);
            }
        });

    }

    private void getMessageDetailsForView(int position) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("txtTemplateId",textMessageList.get(position).getTxtTemplateId());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getMessageDetailsOnId: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getMessageDetailsOnId: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<GetAllTextMessages> call=service.getMessageDetailsOnId(paramObj.toString());
        call.enqueue(new Callback<GetAllTextMessages>() {
            @Override
            public void onResponse(Call<GetAllTextMessages> call, Response<GetAllTextMessages> response) {
                GetAllTextMessages getDetails=response.body();
                if (getDetails.isSuccess()){

                    List<TextMessage> messageDetails=getDetails.getResult();
                    textMessagesListFragment.deleteAttachmentId.clear();
                    if (messageDetails.size()==1){
                        attachMentList=messageDetails.get(0).getAttachements();
                        Log.d(Global.TAG, "AttachmentList: "+attachMentList.size());

                        String attached_names="";
                        ll_view_attachment_text.removeAllViews();
                        if (attachMentList.size()>0){
                            for (int i=0;i<attachMentList.size();i++){
                                String file_path=attachMentList.get(i).getFilePath();
                                String file_name=file_path.substring(file_path.lastIndexOf("/")+1,file_path.length());
                                Log.d(Global.TAG, "onResponse: "+file_name);
                                tv_attached_files.setVisibility(View.GONE);
                                viewAttachment(file_name);
                                /*if (attached_names.equals("")){
                                    attached_names=attached_names+file_name;
                                }else{
                                    attached_names=attached_names+"\n"+file_name;
                                }*/

                            }
                           // tv_attached_files.setText("" + attached_names);
                        }else{
                            tv_attached_files.setVisibility(View.VISIBLE);
                        }

                        linkList=messageDetails.get(0).getLinks();
                        Log.d(Global.TAG, "linkList size: "+linkList.size());

                        String link_names="";
                        if (linkList.size()>0){
                            tv_link_heading.setVisibility(View.VISIBLE);
                            for (int i=0;i<linkList.size();i++){

                                viewLink(linkList.get(i).toString());
                               /* if (link_names.equals("")){
                                    link_names=link_names+linkList.get(i).toString();
                                }else{
                                    link_names=link_names+"\n"+linkList.get(i).toString();
                                }
*/
                            }
                            //tv_all_links.setText("" + link_names);
                        }else{
                            tv_link_heading.setVisibility(View.GONE);
                        }
                    }

                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetAllTextMessages> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: getDetails:"+t);
            }
        });

    }

    private void viewAttachment(String file_name) {
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View attachmentView = inflater.inflate(R.layout.attchment_view, null);
        TextView tv_attachment_name = (TextView) attachmentView.findViewById(R.id.tv_view_attachment);
        tv_attachment_name.setText(file_name);
        ll_view_attachment_text.addView(attachmentView);
    }

    private void viewLink(final String link_name) {
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View attachmentView = inflater.inflate(R.layout.view_link_layout, null);
        TextView tv_view_link = (TextView) attachmentView.findViewById(R.id.tv_view_link);
        tv_view_link.setText(link_name);

        tv_view_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link_name));
                    context.startActivity(browserIntent);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Not Found..!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ll_view_links.addView(attachmentView);
    }

    private void getMessageDetailsForEdit(int position) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("txtTemplateId",textMessageList.get(position).getTxtTemplateId());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getMessageDetailsOnId: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getMessageDetailsOnId: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<GetAllTextMessages> call=service.getMessageDetailsOnId(paramObj.toString());
        call.enqueue(new Callback<GetAllTextMessages>() {
            @Override
            public void onResponse(Call<GetAllTextMessages> call, Response<GetAllTextMessages> response) {
                GetAllTextMessages getDetails=response.body();
                if (getDetails.isSuccess()){

                    List<TextMessage> messageDetails=getDetails.getResult();
                    textMessagesListFragment.deleteAttachmentId.clear();
                    if (messageDetails.size()==1){
                        attachMentList=messageDetails.get(0).getAttachements();
                        Log.d(Global.TAG, "AttachmentList: "+attachMentList.size());
                        String attached_names="";
                        if (attachMentList.size()>0) {
                            for (int i = 0; i < attachMentList.size(); i++) {
                                String file_path = attachMentList.get(i).getFilePath();
                                String file_name = file_path.substring(file_path.lastIndexOf("/") + 1, file_path.length());
                                Log.d(Global.TAG, "onResponse: " + file_name);
                                textMessagesListFragment.deleteAttachmentId.add(attachMentList.get(i).getTxtCampAttachId());
                                displayAttachment(file_name);
                            }
                        }
                        linkList=messageDetails.get(0).getLinks();
                        Log.d(Global.TAG, "linkList size: "+linkList.size());

                        if (linkList.size()>0){
                            ll_add_link.removeAllViews();
                            allEds.clear();
                            for (int i=0;i<linkList.size();i++){
                                addLinkView(linkList.get(i).toString());
                            }
                        }

                    }

                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetAllTextMessages> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: getDetails:"+t);
            }
        });

    }
    private void displayAttachment(String attached_file_name) {
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View attachmentView = inflater.inflate(R.layout.uploaded_attachment_layout, null);
        TextView tv_attachment_name = (TextView) attachmentView.findViewById(R.id.tv_attachment_name);
        ImageButton ib_cancel_attachment=(ImageButton) attachmentView.findViewById(R.id.ib_cancel_attachment);
        tv_attachment_name.setText(attached_file_name);

        ib_cancel_attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index=getViewIndex(ll_attched_files,attachmentView);
                Log.d(Global.TAG, " Index in View: "+index);
                deleteAttachment(index,attachmentView);


            }
        });
        ll_attched_files.addView(attachmentView);
    }
    private int getViewIndex (ViewGroup viewGroup, View view)
    {
        return viewGroup.indexOfChild(view);
    }
    private void openEditTextMessageDialog(final int position) {

        final Dialog dialog1 = new Dialog(context);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setCancelable(false);
        dialog1.setContentView(R.layout.new_text_campaign_message_dialog);
        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog1.setTitle("Confirmation");

        ll_scheduled_interval = (LinearLayout)dialog1.findViewById(R.id.ll_scheduled_interval);
        ll_repeat_text_camp = (LinearLayout)dialog1.findViewById(R.id.ll_repeat_text_camp);
        rbg_interval = (RadioGroup) dialog1.findViewById(R.id.rbg_interval);
        rb_immediately = (RadioButton) dialog1.findViewById(R.id.rb_immediately);
        rb_schedule = (RadioButton) dialog1.findViewById(R.id.rb_schedule);
        rb_footer_yes = (RadioButton) dialog1.findViewById(R.id.rb_footer_yes);
        rb_footer_no = (RadioButton) dialog1.findViewById(R.id.rb_footer_no);
        rb_repeat_text_camp = (RadioButton) dialog1.findViewById(R.id.rb_repeat_text_camp);
        rb_signature_yes = (RadioButton) dialog1.findViewById(R.id.rb_signature_yes);
        rb_signature_no = (RadioButton) dialog1.findViewById(R.id.rb_signature_no);
        Button btn_text_dissmiss = (Button) dialog1.findViewById(R.id.btn_text_add_dissmiss);
        edt_text_message_name = (EditText) dialog1.findViewById(R.id.edt_text_message_name);
        tv_text_from_saved_document = (TextView) dialog1.findViewById(R.id.tv_text_from_saved_document);
        edt_text_message = (EditText) dialog1.findViewById(R.id.edt_text_message);
        ll_attched_files = (LinearLayout) dialog1.findViewById(R.id.ll_attached_files);
        TextView tv_dialog_title = (TextView) dialog1.findViewById(R.id.tv_dialog_title);
        btn_attach_file = (Button) dialog1.findViewById(R.id.btn_attach_file);
        tv_add_link = (TextView) dialog1.findViewById(R.id.tv_add_link);
        ll_add_link = (LinearLayout) dialog1.findViewById(R.id.ll_add_link);
        edt_add_link = (EditText) dialog1.findViewById(R.id.edt_add_link);
        edt_ends_after = (EditText) dialog1.findViewById(R.id.edt_tc_ends_after);
        tv_check_days_error = (TextView) dialog1.findViewById(R.id.tv_check_days_error);

        ch_tc_monday = (CheckBox) dialog1.findViewById(R.id.ch_tc_monday);
        ch_tc_tuesday = (CheckBox) dialog1.findViewById(R.id.ch_tc_tuesday);
        ch_tc_wednesday = (CheckBox) dialog1.findViewById(R.id.ch_tc_wednesday);
        ch_tc_thursday = (CheckBox) dialog1.findViewById(R.id.ch_tc_thursday);
        ch_tc_friday = (CheckBox) dialog1.findViewById(R.id.ch_tc_friday);
        ch_tc_saturday = (CheckBox) dialog1.findViewById(R.id.ch_tc_saturday);
        ch_tc_sunday = (CheckBox) dialog1.findViewById(R.id.ch_tc_sunday);

        edt_add_link = (EditText) dialog1.findViewById(R.id.edt_add_link);

        edt_time_interval_text_message = (EditText) dialog1.findViewById(R.id.edt_time_interval_text_message);
        sp_select_message_time_interval = (Spinner) dialog1.findViewById(R.id.sp_select_message_time_interval);
        sp_tc_repeate_every = (Spinner) dialog1.findViewById(R.id.sp_tc_repeate_every);
        Button btn_save_text_message = (Button) dialog1.findViewById(R.id.btn_save_text_message);
        Button btn_cancel_text_message = (Button) dialog1.findViewById(R.id.btn_cancel_text_message);

        rb_plain_text = (RadioButton) dialog1.findViewById(R.id.rb_plain_text);
        rb_formatted_text = (RadioButton) dialog1.findViewById(R.id.rb_formatted_text);
        ll_plain_text = (LinearLayout) dialog1.findViewById(R.id.ll_plain_text);
        ll_formated_text = (LinearLayout) dialog1.findViewById(R.id.ll_formated_text);
        ll_attchments_layout_text = (LinearLayout) dialog1.findViewById(R.id.ll_attchments_layout_text);
        ll_link_layout = (LinearLayout) dialog1.findViewById(R.id.ll_link_layout);
        ib_custom_help = (ImageButton) dialog1.findViewById(R.id.ib_custom_help);


        applySpinner(intervalType,sp_select_message_time_interval,"--Select--");
        btn_save_text_message.setText("Update");

        notifyListRefresh.sendParameters(ll_attched_files,textMessageList.get(position).getTxtTemplateId());
        edt_text_message_name.setText(textMessageList.get(position).getTxtTemplateTitle());
        edt_time_interval_text_message.setText(textMessageList.get(position).getTxtTemplateInterval());
        tv_dialog_title.setText("Edit Text Message");


        editorFunctions(dialog1);

        mPreview.setText(textMessageList.get(position).getTxtTemplateMsg());

        txtTemplateFooterFlag=textMessageList.get(position).getTxtTemplateFooterFlag();

        Log.d(Global.TAG, "openEditTextMessageDialog:txtTemplateFooterFlag: "+txtTemplateFooterFlag);
        if (txtTemplateFooterFlag.equals("1")){
            rb_footer_yes.setChecked(true);
        }else{
            rb_footer_no.setChecked(true);
        }

        txtTemplateAddSignature=textMessageList.get(position).getTxtTemplateAddSignature();
        Log.d(Global.TAG, "openEditTextMessageDialog:txtTemplateAddSignature: "+txtTemplateAddSignature);
        if (txtTemplateAddSignature.equals("1")){
            rb_signature_yes.setChecked(true);
        }else{
            rb_signature_no.setChecked(true);
        }

        checkMemberAssignedOrNot(position);
        rb_immediately.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    ll_scheduled_interval.setVisibility(View.GONE);
                }else{
                  //  ll_scheduled_interval.setVisibility(View.VISIBLE);
                }
            }
        });

        rb_schedule.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    ll_scheduled_interval.setVisibility(View.VISIBLE);
                }else{
                    ll_scheduled_interval.setVisibility(View.GONE);
                }
            }
        });

        rb_repeat_text_camp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    // ll_scheduled_interval.setVisibility(View.GONE);
                    ll_repeat_text_camp.setVisibility(View.VISIBLE);
                    fillSpinnerwith12();
                    //selectCurrentDay();
                }else{
                    ll_repeat_text_camp.setVisibility(View.GONE);
                }
            }
        });

        rb_footer_yes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    txtTemplateFooterFlag="1";
                }else{
                    txtTemplateFooterFlag="0";
                }
            }
        });

        rb_footer_no.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    txtTemplateFooterFlag="0";
                }else{
                    txtTemplateFooterFlag="1";
                }
            }
        });

        rb_signature_yes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    txtTemplateAddSignature="1";
                }else{
                    txtTemplateAddSignature="0";
                }


            }
        });

        rb_signature_no.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    txtTemplateAddSignature="0";
                }else{
                    txtTemplateAddSignature="1";
                }


            }
        });

        if (textMessageList.get(position).getTxtTemplateInterval().equals("0")){
            rb_immediately.setChecked(true);
            ll_scheduled_interval.setVisibility(View.GONE);
            ll_repeat_text_camp.setVisibility(View.GONE);
        }else if (textMessageList.get(position).getTxtTemplateRepeat().equals("1")){
            rb_repeat_text_camp.setChecked(true);
            ll_repeat_text_camp.setVisibility(View.VISIBLE);
        }else  if (!textMessageList.get(position).getTxtTemplateInterval().equals("0")){
            rb_schedule.setChecked(true);
            ll_scheduled_interval.setVisibility(View.VISIBLE);
        }


        //getMessageDetailsOnId(position);
        getMessageDetailsForEdit(position);


        tv_add_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLinkView("");
            }
        });

        tv_text_from_saved_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,UploadFromSavedDocumentActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("template_id",textMessageList.get(position).getTxtTemplateId());
                bundle.putBoolean("textEditFlag",true);
                intent.putExtras(bundle);
                textMessagesListFragment.startActivityForResult(intent,1011);
            }
        });
        btn_attach_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(context,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    Toast.makeText(context, "Read External storage Permission Denied.!", Toast.LENGTH_LONG).show();
                } else {
                    openDocuments();
                    Log.d(Global.TAG, " Permission already granted: ");
                }

            }
        });
/*
        edt_text_message_name.setEnabled(false);
        edt_text_message.setEnabled(false);
        edt_time_interval_text_message.setEnabled(false);
        */
        ib_custom_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SimpleTooltip.Builder(context)
                        .anchorView(v)
                        .text("By using custom message, you can create formatted text with adding images, links and attachments.")
                        .gravity(Gravity.BOTTOM)
                        .textColor(context.getResources().getColor(R.color.colorBlack))
                        .backgroundColor(context.getResources().getColor(R.color.colorOffWhite))
                        .arrowColor(context.getResources().getColor(R.color.colorOffWhite))
                        .animated(true)
                        .build()
                        .show();
            }
        });

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

        sp_tc_repeate_every.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (sp_tc_repeate_every.getSelectedItem().toString().contains("Select")) {
                    repeat_every_string = "";
                } else {
                    if (repeat_every.size()>0){
                        repeat_every_string = repeat_every.get(position).toString();
                        Log.d(Global.TAG, "onItemSelected: repeat_every_string:"+repeat_every_string);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        String intervaltype=textMessageList.get(position).getTxtTemplateIntervalType();
        Log.d(Global.TAG, "Interval Type: "+intervaltype);
        if (intervaltype.equals("hours")){
            sp_select_message_time_interval.setSelection(1);
        }else if (intervaltype.equals("days")){
            sp_select_message_time_interval.setSelection(2);
        }else if (intervaltype.equals("week")){
            sp_select_message_time_interval.setSelection(3);
        }

        if (textMessageList.get(position).getTxtTemplateRepeat().equals("1")){
            if (textMessageList.get(position).getTxtempRepeatWeeks()!=null){
                String repeat_interval_week=textMessageList.get(position).getTxtempRepeatWeeks();
                int interval_week=Integer.parseInt(repeat_interval_week);
                Log.d(Global.TAG, "repeat_interval_week : "+interval_week);
                sp_tc_repeate_every.setSelection(interval_week-1);
            }

            if (textMessageList.get(position).getTxtempRepeatDays()!=null) {
                String repeat_week_days=textMessageList.get(position).getTxtempRepeatDays();
                if (!repeat_week_days.equals("")){
                    String[] week_days=repeat_week_days.split(",");
                    Log.d(Global.TAG, "week_days: "+week_days.length);
                   for(int i=0;i<week_days.length;i++){
                       Log.d(Global.TAG, "week_days: "+week_days[i]);
                       String day_name=week_days[i];
                       //String name=day_name.substring(0,day_name.length()-1);
                      selectWeekDays(day_name);
                   }

                }
            }

            String repeat_occurances=textMessageList.get(position).getTxtempRepeatEndOccurrence();
            edt_ends_after.setText(repeat_occurances);
            Log.d(Global.TAG, "repeat_occurances: "+repeat_occurances);

        }


        btn_cancel_text_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1.dismiss();
            }
        });

        btn_save_text_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String linktoAdd=edt_add_link.getText().toString();
                if (MyValidator.isValidUrl(linktoAdd)){
                    if (!linktoAdd.equals("")){
                        JSONObject link = new JSONObject();
                        try {
                            link.put("link", linktoAdd);
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(Global.TAG, "add Link: "+link);
                        linkArray.put(link);
                        Log.d(Global.TAG, "Link Array: "+linkArray);
                    }
                }else{
                    edt_add_link.setError("Enter Valid URL");
                    edt_add_link.setFocusable(true);
                    edt_add_link.requestFocus();
                }

                boolean linkFlag=false;
                linkFlaglist.clear();
                for( int i=0;i<allEds.size();i++){
                    String linktoAdd1=allEds.get(i).getText().toString();
                    if (MyValidator.isValidUrl(linktoAdd1)){
                        if (!linktoAdd1.equals("")){
                            JSONObject link = new JSONObject();
                            try {
                                link.put("link",linktoAdd1);
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.d(Global.TAG, "add Link: "+link);
                            linkArray.put(link);
                            linkFlaglist.add("1");


                        }
                    }else{
                        linkFlaglist.add("0");
                        allEds.get(i).setError("Enter valid URL..!");
                    }
                }
                if (linkFlaglist.contains("0")){
                    linkFlag=false;
                }else{
                    linkFlag=true;
                }
                Log.d(Global.TAG, " Final Link Array: "+linkArray);



                if (MyValidator.isValidFieldE(edt_text_message_name,"Enter Message Title")){
                   
                    if (txtTemplateType.equals("1")){
                        if (MyValidator.isValidFieldE(edt_text_message,"Enter Text Message")){
                            if (edt_add_link.getError()==null){
                                if (allEds.size()==0 || allEds.size()>0&&linkFlag){
                                    if (ll_scheduled_interval.getVisibility()==View.VISIBLE){
                                        if (MyValidator.isValidFieldE(edt_time_interval_text_message,"Enter Time Interval")){
                                            if (!sp_select_message_time_interval.getSelectedItem().toString().contains("Select")){
                                                updateTextMessage(position,dialog1);
                                            }else{
                                                MyValidator.setSpinnerError(sp_select_message_time_interval,"Select");
                                            }
                                        }
                                    }else{
                                        if(ll_repeat_text_camp.getVisibility()==View.VISIBLE){
                                            if (!edt_ends_after.getText().toString().equals("")){
                                                if (!ch_tc_monday.isChecked() && !ch_tc_tuesday.isChecked() && !ch_tc_wednesday.isChecked() && !ch_tc_thursday.isChecked() && !ch_tc_friday.isChecked()
                                                        && !ch_tc_saturday.isChecked() && !ch_tc_sunday.isChecked()){
                                                    tv_check_days_error.setVisibility(View.VISIBLE);
                                                }else{
                                                    tv_check_days_error.setVisibility(View.GONE);
                                                    updateTextMessage(position,dialog1);
                                                }
                                            }else{
                                                edt_ends_after.setError("Enter Occurrences");
                                            }
                                        }else{
                                            if (memberAssignedFlag==1){
                                                new AlertDialog.Builder(context)
                                                        .setMessage("This text message will send immediately to assigned members. Do you want continue..?")
                                                        .setCancelable(false)
                                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                updateTextMessage(position,dialog1);
                                                            }
                                                        })
                                                        .setNegativeButton("No", null)
                                                        .show();
                                            }else{
                                                updateTextMessage(position,dialog1);
                                            }

                                        }

                                    }
                                }else{
                                    if (allEds.size()>0){
                                        for(int i=0;i<allEds.size();i++){
                                            if (allEds.get(i).getError()==null){

                                            }else{
                                                allEds.get(i).setFocusable(true);
                                                allEds.get(i).requestFocus();
                                            }
                                        }
                                    }
                                }

                            }else{
                                edt_add_link.setFocusable(true);
                                edt_add_link.requestFocus();
                            }
                        }
                    }else if (txtTemplateType.equals("2")){
                        if (!editor_text_msg_contents.getHtml().equals("")){
                            if (edt_add_link.getError()==null){
                                if (allEds.size()==0 || allEds.size()>0&&linkFlag){
                                    if (ll_scheduled_interval.getVisibility()==View.VISIBLE){
                                        if (MyValidator.isValidFieldE(edt_time_interval_text_message,"Enter Time Interval")){
                                            if (!sp_select_message_time_interval.getSelectedItem().toString().contains("Select")){
                                                updateTextMessage(position,dialog1);
                                            }else{
                                                MyValidator.setSpinnerError(sp_select_message_time_interval,"Select");
                                            }
                                        }
                                    }else{
                                        if(ll_repeat_text_camp.getVisibility()==View.VISIBLE){
                                            if (!edt_ends_after.getText().toString().equals("")){
                                                if (!ch_tc_monday.isChecked() && !ch_tc_tuesday.isChecked() && !ch_tc_wednesday.isChecked() && !ch_tc_thursday.isChecked() && !ch_tc_friday.isChecked()
                                                        && !ch_tc_saturday.isChecked() && !ch_tc_sunday.isChecked()){
                                                    tv_check_days_error.setVisibility(View.VISIBLE);
                                                }else{
                                                    tv_check_days_error.setVisibility(View.GONE);
                                                    updateTextMessage(position,dialog1);
                                                }
                                            }else{
                                                edt_ends_after.setError("Enter Occurrences");
                                            }
                                        }else{
                                            if (memberAssignedFlag==1){
                                                new AlertDialog.Builder(context)
                                                        .setMessage("This text message will send immediately to assigned members. Do you want continue..?")
                                                        .setCancelable(false)
                                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                updateTextMessage(position,dialog1);
                                                            }
                                                        })
                                                        .setNegativeButton("No", null)
                                                        .show();
                                            }else{
                                                updateTextMessage(position,dialog1);
                                            }

                                        }

                                    }
                                }else{
                                    if (allEds.size()>0){
                                        for(int i=0;i<allEds.size();i++){
                                            if (allEds.get(i).getError()==null){

                                            }else{
                                                allEds.get(i).setFocusable(true);
                                                allEds.get(i).requestFocus();
                                            }
                                        }
                                    }
                                }

                            }else{
                                edt_add_link.setFocusable(true);
                                edt_add_link.requestFocus();
                            }
                        }else{
                            Toast.makeText(context, "Please Enter Text Message..!", Toast.LENGTH_SHORT).show();
                        }
                    }
                   
                    
                }

            }
        });

        btn_text_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1.dismiss();
            }
        });

        rb_plain_text.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    ll_plain_text.setVisibility(View.VISIBLE);
                    ll_formated_text.setVisibility(View.GONE);
                    ll_attchments_layout_text.setVisibility(View.GONE);
                    ll_link_layout.setVisibility(View.GONE);
                    txtTemplateType="1";
                }
            }
        });

        rb_formatted_text.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    ll_formated_text.setVisibility(View.VISIBLE);
                    ll_plain_text.setVisibility(View.GONE);
                    ll_attchments_layout_text.setVisibility(View.VISIBLE);
                    ll_link_layout.setVisibility(View.VISIBLE);
                    txtTemplateType="2";
                    editor_text_msg_contents.setHtml(edt_text_message.getText().toString());
                }
            }
        });

        rb_plain_text.setChecked(true);

        if (textMessageList.get(position).getTxtTemplateType().equals("1")){
            rb_plain_text.setChecked(true);
            edt_text_message.setText(textMessageList.get(position).getTxtTemplateMsg());
            editor_text_msg_contents.setHtml(textMessageList.get(position).getTxtTemplateMsg());
        }else if (textMessageList.get(position).getTxtTemplateType().equals("2")){
            rb_formatted_text.setChecked(true);
            editor_text_msg_contents.setHtml(textMessageList.get(position).getTxtTemplateMsg());
        }

        dialog1.show();
        Window window = dialog1.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
    private void editorFunctions(Dialog dialog1) {
        editor_text_msg_contents = (RichEditor) dialog1.findViewById(R.id.edt_text_contents);
        editor_text_msg_contents.setEditorHeight(200);
        editor_text_msg_contents.setEditorFontSize(14);
        //edt_email_contents.startNestedScroll(R.layout.edt_email_contents);
        //edt_email_contents.setEditorFontColor(Color.RED);
        //mEditor.setEditorBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        editor_text_msg_contents.setPadding(10, 20, 10, 10);
        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        editor_text_msg_contents.setPlaceholder("Enter Text Message here..");
        //edt_email_contents.setInputEnabled(false);

        mPreview = (TextView) dialog1.findViewById(R.id.previewt);
        editor_text_msg_contents.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override public void onTextChange(String text) {
                mPreview.setText(text);
            }
        });

        dialog1.findViewById(R.id.action_undot).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                editor_text_msg_contents.undo();
            }
        });

        dialog1.findViewById(R.id.action_redot).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                editor_text_msg_contents.redo();
            }
        });

        dialog1.findViewById(R.id.action_boldt).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                editor_text_msg_contents.setBold();
            }
        });

        dialog1.findViewById(R.id.action_italict).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                editor_text_msg_contents.setItalic();
            }
        });

        dialog1.findViewById(R.id.action_subscriptt).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                editor_text_msg_contents.setSubscript();
            }
        });

        dialog1.findViewById(R.id.action_superscriptt).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                editor_text_msg_contents.setSuperscript();
            }
        });

        dialog1.findViewById(R.id.action_strikethrought).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                editor_text_msg_contents.setStrikeThrough();
            }
        });

        dialog1.findViewById(R.id.action_underlinet).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                editor_text_msg_contents.setUnderline();
            }
        });


        dialog1.findViewById(R.id.action_txt_colort).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override public void onClick(View v) {
                editor_text_msg_contents.setTextColor(isChanged ? Color.BLACK : Color.RED);
                isChanged = !isChanged;
            }
        });

        dialog1.findViewById(R.id.action_bg_colort).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override public void onClick(View v) {
                editor_text_msg_contents.setTextBackgroundColor(isChanged ? Color.TRANSPARENT : Color.YELLOW);
                isChanged = !isChanged;
            }
        });

        dialog1.findViewById(R.id.action_align_leftt).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                editor_text_msg_contents.setAlignLeft();
            }
        });

        dialog1.findViewById(R.id.action_align_centert).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                editor_text_msg_contents.setAlignCenter();
            }
        });

        dialog1.findViewById(R.id.action_align_rightt).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                editor_text_msg_contents.setAlignRight();
            }
        });

        dialog1.findViewById(R.id.action_insert_bulletst).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                editor_text_msg_contents.setBullets();
            }
        });

        dialog1.findViewById(R.id.action_insert_numberst).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                editor_text_msg_contents.setNumbers();
            }
        });

        dialog1.findViewById(R.id.action_insert_imaget).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
//                edt_email_contents.insertImage(" https://successentellus.com/check/assets/uploads/ckEditorImage/841/2-watercolor-painting-by-vilas_kulkarni.jpg",
//                        "ckEditorImage");
                selectImage(context);
            }
        });

        dialog1.findViewById(R.id.action_insert_linkt).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                editor_text_msg_contents.insertLink("https://www.successentellus.com", "success");
            }
        });
        dialog1.findViewById(R.id.action_insert_checkboxt).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                editor_text_msg_contents.insertTodo();

            }
        });


        //edt_email_contents.setHtml(editEmailContents);
        //mPreview.setText(editEmailContents);
    }

    private void selectImage(Context context) {
        //this.iv_category_image = iv_category_image;
        final CharSequence[] options = {"Choose from Gallery", "Cancel"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle("Please Select ");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Choose from Gallery")) {
                    Log.d("mytag", "Choose from Gallery");
                    Intent GalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    textMessagesListFragment.startActivityForResult(GalleryIntent, 2);

                } else if (options[item].equals("Cancel")) {
                    Log.d("mytag", "Cancel");
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void selectWeekDays(String name) {
        if (!name.equals("")){
            if (name.equalsIgnoreCase("mon")){
                ch_tc_monday.setChecked(true);
            }else if (name.equalsIgnoreCase("tue")){
                ch_tc_tuesday.setChecked(true);
            }else if (name.equalsIgnoreCase("wed")){
                ch_tc_wednesday.setChecked(true);
            }else if (name.equalsIgnoreCase("thu")){
                ch_tc_thursday.setChecked(true);
            }else if (name.equalsIgnoreCase("fri")){
                ch_tc_friday.setChecked(true);
            }else if (name.equalsIgnoreCase("sat")){
                ch_tc_saturday.setChecked(true);
            }else if (name.equalsIgnoreCase("sun")){
                ch_tc_sunday.setChecked(true);
            }
        }
    }

    private void selectCurrentDay() {
        Date curDate = null;
        try {
            curDate = new SimpleDateFormat("MM-dd-yyyy").parse((month+1)+"-"+days+"-"+year);
            Log.d(Global.TAG, "Current Date: "+curDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        currDayName = new SimpleDateFormat("EEE").format(curDate);
        Log.d(Global.TAG, "Current day name: "+currDayName);

        if (!currDayName.equals("")){
            if (currDayName.equalsIgnoreCase("mon")){
                ch_tc_monday.setChecked(true);
            }else if (currDayName.equalsIgnoreCase("tue")){
                ch_tc_tuesday.setChecked(true);
            }else if (currDayName.equalsIgnoreCase("wed")){
                ch_tc_wednesday.setChecked(true);
            }else if (currDayName.equalsIgnoreCase("thu")){
                ch_tc_thursday.setChecked(true);
            }else if (currDayName.equalsIgnoreCase("fri")){
                ch_tc_friday.setChecked(true);
            }else if (currDayName.equalsIgnoreCase("sat")){
                ch_tc_saturday.setChecked(true);
            }else if (currDayName.equalsIgnoreCase("sun")){
                ch_tc_sunday.setChecked(true);
            }
        }

    }

    private void fillSpinnerwith12() {
        repeat_every.clear();
        for (int i=1;i<=13;i++){
            repeat_every.add(String.valueOf(i));
        }
        applySpinner1(repeat_every,sp_tc_repeate_every,"-Select-");
        sp_tc_repeate_every.setSelection(0);
    }
    private void applySpinner1(final List<String> taglist, Spinner sp_name, String tag_string) {
        //Adding spinner on Repeate By:: When Monthly Selected Add 12
        AddContactActivity.spinnerAdapter adapterRepeateDaily = new AddContactActivity.spinnerAdapter(context, android.R.layout.simple_list_item_1);
        // adapterRepeateDaily.add(tag_string);
        adapterRepeateDaily.addAll(taglist);
        //adapterRepeateDaily.add(tag_string);
        sp_name.setAdapter(adapterRepeateDaily);
        sp_name.setSelection(adapterRepeateDaily.getCount());
        sp_name.setEnabled(true);
    }
    private void openDocuments() {
        Log.d(Global.TAG, "Select File: ");
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("*/*");
        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        textMessagesListFragment.startActivityForResult(chooseFile, 200);
    }

    private void addLinkView(String linkText) {
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View add_link_view = inflater.inflate(R.layout.add_link_view, null);
        final EditText edt_link_view = (EditText) add_link_view.findViewById(R.id.edt_link_view);
        edt_link_view.setText(linkText);

        allEds.add(edt_link_view);
        ImageButton ib_cancel_link=(ImageButton) add_link_view.findViewById(R.id.ib_cancel_link);
        //tv_attachment_name.setText(attached_file_name);

        ib_cancel_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_add_link.removeView(add_link_view);
                allEds.remove(edt_link_view);

            }
        });
        ll_add_link.addView(add_link_view);
    }
    private void getDaysSelection() {

        dayList.clear();

        if (ch_tc_monday.isChecked()){
            dayList.add("Mon");
        }
        if (ch_tc_tuesday.isChecked()){
            dayList.add("Tue");
        }
        if (ch_tc_wednesday.isChecked()){
            dayList.add("Wed");
        }
        if (ch_tc_thursday.isChecked()){
            dayList.add("Thu");
        }
        if (ch_tc_friday.isChecked()){
            dayList.add("Fri");
        }
        if (ch_tc_saturday.isChecked()){
            dayList.add("Sat");
        }
        if (ch_tc_sunday.isChecked()){
            dayList.add("Sun");
        }

        dayString="";
        for (String s : dayList)
        {
            dayString += s + ",";
        }
        if (dayString.endsWith(",")) {
            dayString = dayString.substring(0, dayString.length() - 1);
        }
        Log.d(Global.TAG, "getDaysSelection: "+dayString);

    }

    private void deleteAttachment(final int index, final View attachmentView) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", user_id);
            paramObj.put("platform", "2");
            paramObj.put("txtCampAttachId", textMessagesListFragment.deleteAttachmentId.get(index).toString());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "deleteAttachment: " + e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "deleteAttachment: " + paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call = service.delete_attachment(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    Log.d(Global.TAG, ""+jsonResult.getResult());
                    ll_attched_files.removeView(attachmentView);
                    textMessagesListFragment.deleteAttachmentId.remove(index);
                }else{
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    Log.d(Global.TAG, ""+jsonResult.getResult());
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: deleteAttachment:"+t);
            }
        });

    }
    private void updateTextMessage(int position, final Dialog dialog) {
        String interval="";
        String interval_type="";
        String select_type="";
        if (ll_scheduled_interval.getVisibility()==View.VISIBLE){
            interval=edt_time_interval_text_message.getText().toString();
            interval_type=text_message_interval_type.toLowerCase();
            select_type="2";
            dayString="";
        }else if (ll_repeat_text_camp.getVisibility()==View.VISIBLE){
            interval="";
            interval_type="";
            select_type="3";
        }else{
            interval="0";
            interval_type="hours";
            select_type="1";
            dayString="";
        }
        getDaysSelection();

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",user_id);
            paramObj.put("platform", "2");
            paramObj.put("txtTemplateId",textMessageList.get(position).getTxtTemplateId());
            paramObj.put("txtTemplateFooterFlag",txtTemplateFooterFlag);
            paramObj.put("txtTemplateAddSignature",txtTemplateAddSignature);
            paramObj.put("txtTemplateCampId",textMessageList.get(position).getTxtTemplateCampId());
            paramObj.put("txtTemplateTitle",edt_text_message_name.getText().toString());
            paramObj.put("txtTemplateInterval",interval);
            paramObj.put("txtTemplateIntervalType",interval_type);
            paramObj.put("txtTemplateType", txtTemplateType);
            paramObj.put("selectType",select_type);

            if (txtTemplateType.equals("1")){
                paramObj.put("txtTemplateMsg", edt_text_message.getText().toString());
            }else if (txtTemplateType.equals("2")){
                paramObj.put("txtTemplateMsg", mPreview.getText().toString());
            }

            paramObj.put("addLinkUrl",linkArray);
            paramObj.put("repeat_every_weeks", repeat_every_string);
            paramObj.put("repeat_on", dayString);
            paramObj.put("repeat_ends_after", edt_ends_after.getText().toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "updateTextMessage: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "updateTextMessage: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.update_text_message(paramObj.toString());
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(context, ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                    notifyListRefresh.refreshList();
                    linkArray=new JSONArray();
                    allEds.clear();
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
}
