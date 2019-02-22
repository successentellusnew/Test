package com.success.successEntellus.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.success.successEntellus.R;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.MenorListCount;
import com.success.successEntellus.model.MentorWithCount;
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
 * Created by user on 6/22/2018.
 */

public class CFTMentorFeedbackFragment extends Fragment {
    View layout;
    SPLib spLib;
    RecyclerView rv_mentor_list;
    SearchView search_mentor;
    List<MentorWithCount> mentorWithCountList,search_list;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout=inflater.inflate(R.layout.fragment_cft_mentor_feedback,container,false);
        setHasOptionsMenu(true);
        init();

        search_mentor.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search_list.clear();
                for (int i=0;i<mentorWithCountList.size();i++){
                    String mentor_name=mentorWithCountList.get(i).getFullName();
                    if ( Pattern.compile(Pattern.quote(query), Pattern.CASE_INSENSITIVE).matcher(mentor_name).find()){
                        search_list.add(mentorWithCountList.get(i));
                    }
                }
                Log.d(Global.TAG, "onQueryTextSubmit: Search_list"+search_list.size());
                if (search_list.size()>0){
                    MentorListFeedbackAdapter mentorListFeedbackAdapter= new MentorListFeedbackAdapter(getActivity(),search_list,spLib.getPref(SPLib.Key.USER_ID), true);
                    rv_mentor_list.setAdapter(mentorListFeedbackAdapter);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (mentorWithCountList!=null) {
                    search_list.clear();
                    for (int i = 0; i < mentorWithCountList.size(); i++) {
                        String mentor_name = mentorWithCountList.get(i).getFullName();
                        if (Pattern.compile(Pattern.quote(newText), Pattern.CASE_INSENSITIVE).matcher(mentor_name).find()) {
                            search_list.add(mentorWithCountList.get(i));
                        }
                    }
                    Log.d(Global.TAG, "onQueryTextSubmit: Search_list" + search_list.size());
                    if (search_list.size() > 0) {
                        MentorListFeedbackAdapter mentorListFeedbackAdapter = new MentorListFeedbackAdapter(getActivity(), search_list, spLib.getPref(SPLib.Key.USER_ID), true);
                        rv_mentor_list.setAdapter(mentorListFeedbackAdapter);
                    }
                }

                return false;
            }
        });
        getMentorListWithCount();

        return layout;
    }

    private void init() {
        spLib=new SPLib(getActivity());
        rv_mentor_list=layout.findViewById(R.id.rv_mentor_list);
        search_mentor=layout.findViewById(R.id.search_mentor);
        search_mentor.setIconified(false);
        search_mentor.setFocusable(false);
        search_mentor.clearFocus();
        rv_mentor_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        search_list=new ArrayList<>();
    }

    private void getMentorListWithCount() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId",spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "getMentorListWithCount: "+paramObj.toString());
        final Dialog myLoader = Global.showDialog(getActivity());
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService servive= APIClient.getRetrofit().create(APIService.class);
        Call<MenorListCount> call=servive.getMentorListFeedback(paramObj.toString());
        call.enqueue(new Callback<MenorListCount>() {
            @Override
            public void onResponse(Call<MenorListCount> call, Response<MenorListCount> response) {
                MenorListCount menorListCount=response.body();
                if (menorListCount.isSuccess()){
                    mentorWithCountList=menorListCount.getResult();
                    Log.d(Global.TAG, "mentorWithCountList: "+mentorWithCountList.size());
                    if (mentorWithCountList.size()>0){
                        MentorListFeedbackAdapter mentorListFeedbackAdapter= new MentorListFeedbackAdapter(getActivity(),mentorWithCountList,spLib.getPref(SPLib.Key.USER_ID), true);
                        rv_mentor_list.setAdapter(mentorListFeedbackAdapter);
                    }

                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<MenorListCount> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: "+t);
            }
        });
    }
}
