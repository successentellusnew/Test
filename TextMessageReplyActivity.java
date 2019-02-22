package com.success.successEntellus.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.success.successEntellus.R;
import com.success.successEntellus.adapter.ReplyTextFromAdapter;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.GetTextMessageReplyList;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.model.ReplyFrom;
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
 * Created by user on 2/14/2019.
 */

public class TextMessageReplyActivity extends Activity{

    SPLib spLib;
    RecyclerView rv_text_reply_from_list;
    String txtTemplateId="";
    ImageButton ib_reply_list_back,ib_search_reply;
    TextView tv_text_step_name_reply_title;
    SearchView sv_reply_text;
    List<ReplyFrom> replyFromList,search_list;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_message_reply);

        Bundle bundle=getIntent().getExtras();
        if (bundle!=null){
            txtTemplateId=bundle.getString("textCampStepId");
            Log.d(Global.TAG, "Text Camp Step Id Bundle: "+txtTemplateId);
        }

        init();

        getAllReplyList();

        ib_reply_list_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ib_search_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sv_reply_text.getVisibility()==View.VISIBLE){
                    sv_reply_text.setVisibility(View.GONE);
                }else if (sv_reply_text.getVisibility()==View.GONE){
                    sv_reply_text.setVisibility(View.VISIBLE);
                }
            }
        });


        sv_reply_text.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                search_list.clear();
                if (replyFromList.size()>0){

                    for(int i=0;i<replyFromList.size();i++){
                        String from_name=replyFromList.get(i).getReplyfromName();

                        if (Pattern.compile(Pattern.quote(newText), Pattern.CASE_INSENSITIVE).matcher(from_name).find()){
                            search_list.add(replyFromList.get(i));
                        }
                    }

                    if (search_list.size()>0){
                        ReplyTextFromAdapter adapter=new ReplyTextFromAdapter(TextMessageReplyActivity.this,search_list);
                        rv_text_reply_from_list.setAdapter(adapter);
                    }
                }
                return false;
            }
        });

    }

    private void init() {
        spLib=new SPLib(TextMessageReplyActivity.this);
        rv_text_reply_from_list=findViewById(R.id.rv_text_reply_from_list);
        tv_text_step_name_reply_title=findViewById(R.id.tv_text_step_name_reply_title);
        ib_reply_list_back=findViewById(R.id.ib_reply_list_back);
        ib_search_reply=findViewById(R.id.ib_search_reply);
        sv_reply_text=findViewById(R.id.sv_reply_text);
        rv_text_reply_from_list.setLayoutManager(new LinearLayoutManager(TextMessageReplyActivity.this));

        sv_reply_text.setIconified(false);
        sv_reply_text.setQueryHint("Search Name");
        sv_reply_text.setFocusable(false);
        sv_reply_text.clearFocus();
        search_list=new ArrayList<>();
    }

    private void getAllReplyList() {

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
            paramObj.put("txtTemplateId", txtTemplateId);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "getAllReplyList: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getAllReplyList: "+paramObj.toString());

        final Dialog myLoader = Global.showDialog(TextMessageReplyActivity.this);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<GetTextMessageReplyList> call=service.getCampaignTextMessageReply(paramObj.toString());
        call.enqueue(new Callback<GetTextMessageReplyList>() {
            @Override
            public void onResponse(Call<GetTextMessageReplyList> call, Response<GetTextMessageReplyList> response) {
                if (response.isSuccessful()){

                    GetTextMessageReplyList getTextMessageReplyList=response.body();
                    if (getTextMessageReplyList.isSuccess()){

                       replyFromList=getTextMessageReplyList.getResult();
                        Log.d(Global.TAG, "replyFromList: "+replyFromList.size());


                        if (replyFromList.size()>0){
                            ReplyTextFromAdapter adapter=new ReplyTextFromAdapter(TextMessageReplyActivity.this,replyFromList);
                            rv_text_reply_from_list.setAdapter(adapter);

                        }


                    }


                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetTextMessageReplyList> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: GetTextMessageReplyList "+t);
            }
        });


    }
}
