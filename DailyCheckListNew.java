package com.success.successEntellus.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.success.successEntellus.R;
import com.success.successEntellus.activity.AddContactActivity;
import com.success.successEntellus.activity.NetworkCheckActivity;
import com.success.successEntellus.activity.OnBoardingFlowActivity;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.DateModel;
import com.success.successEntellus.model.GoalDetails;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.model.SingleGoal;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Bhagyashri on 3/27/2018.
 */

public class DailyCheckListNew extends Fragment{
    View layout;
    Spinner spin_selectday;
    LinearLayout ll_checklist,ll_checkdata,ll_checkEditdata,ll_checklist_button,ll_edit;
    Button btn_checklist_submit,btn_edit_submit,btn_checklist_save,btn_show_hidden,btn_repositioning;
    ImageView iv_spinner_day;
    EditText[] check_edit_value_date,check_edit_value;
    CheckBox[] checkBox;
    SPLib spLib;
    String user_id;
    TableLayout tl;
    TableRow tr,tr1,tr2;
    Bundle bundleEffect;
    public static List<SingleGoal> goalData,userGoalDetails,hiddengoalsList;
    String currDayName,selected_date,week_start_date,week_end_date;
    int positionForDiable=0, day,year,hour,min,monthOfYear;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    String[]  weekDayName,weekDates,testarrayEditSave;
    ImageView[] iv_eye;
    TypedValue outValue = new TypedValue();
    TextView goal_name, tv_my_goal,tv_goal_remaining,tv_completed_goal,companyTV,value4,value2,value6,  textCount,tv_my_score,check_goal_name;
    ArrayList<String> goalNameArray,goalIdArray,goalCompletedArray,listUncheck,goalRepositioningString; @Nullable
    List<TextView> allTexViewtGoalName = new ArrayList<TextView>();
    List<EditText> allEditTextGoal = new ArrayList<EditText>();
    List<EditText> allEditTextGoalOnDate = new ArrayList<EditText>();
    int flag=0,flagUnhide=0;
    private Calendar cal;
    private String checkUncheckList;
    ArrayList<String> dayNamecopy;
    String CurrentUSDate="";


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        layout=inflater.inflate(R.layout.fragment_daily_checklist,container,false);
        setHasOptionsMenu(true);
        init();
        getCurrentDay();
        //openDialogGoalDetails(user_id);
        if(Global.isNetworkAvailable(getActivity())) {
            getGoalDetailsForChecklistOnUserId();
        }else{
            Toast.makeText(getActivity(), "Please Check your Internet Connections...!", Toast.LENGTH_SHORT).show();
        }
        Log.d(Global.TAG, "Business Start: "+spLib.getPref(SPLib.Key.BusinessStart));
        Log.d(Global.TAG, "Business end: "+spLib.getPref(SPLib.Key.BUSINESS_END));
        Log.d(Global.TAG, "Business IsStart: "+spLib.getPrefBoolean(SPLib.Key.IsBusinessStart));

        iv_spinner_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickMeToOpenSpinner(view);
            }
        });
        btn_checklist_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag=0;
                view = getActivity().getCurrentFocus();
                /*if (view != null) {          //Check for is soft keyboard is open if open close it
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }*/
                if(Global.isNetworkAvailable(getActivity())) {
                    goalCompletedArray.clear();
                    Log.d(Global.TAG, "userGoalDetails: size"+goalData.size());
                    for(int i=0;i<goalData.size();i++){
                        Log.d(Global.TAG, "userGoalDetails: Goal id:"+goalData.get(i).getGoal_id());
                        goalCompletedArray.add(goalData.get(i).getGoal_id()+":"+allEditTextGoal.get(i).getText().toString());
                        Log.d(Global.TAG, "Selected Edittext: "+allEditTextGoal.get(i).getText().toString().length());
                        if(allEditTextGoal.get(i).getText().toString().length()>0) {
//                            if (allEditTextGoal.get(i).getText().toString().equals("0")){ //Check if user enter zero value
//                                allEditTextGoal.get(i).setError("Enter valid goal numbers. Exclusion for zero.");
//                                flag = 2;
//                            }else {
                            flag = 1;
//                            }
                        }
                    }
                    //if (flag==1){
                    if(spLib.getPref(SPLib.Key.BusinessStart).equals(true))
                    {
                        Toast.makeText(getActivity(), "Business Date Not Started Yet", Toast.LENGTH_SHORT).show();
                    }else {
                        setChecklistDataToDatabase();
                    }
                   /* }else{
                        Toast.makeText(getActivity(), "Please fill-up at least one text box & submit your checklist", Toast.LENGTH_SHORT).show();
                    }*/
                }else{
                    /*Intent intent = new Intent(getActivity(), ActivityNetworkCheck.class);
                    startActivity(intent, bundleEffect);
                    finish();*/
                    Toast.makeText(getActivity(), "Please Check your Internet Connection..!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_show_hidden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Global.isNetworkAvailable(getActivity())) {
                    getHiddenGoalOnUserId();
                }else{
                    Intent intent = new Intent(getActivity(), NetworkCheckActivity.class);
                    startActivity(intent, bundleEffect);
                }
            }

        });


        return layout;
    }

    private void openDialogGoalDetails(final String user_id) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_view_goal_details);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Conformation");

        TextView tv_goal_details = (TextView) dialog.findViewById(R.id.tv_goal_details);
        Button btn_go = (Button) dialog.findViewById(R.id.btn_go);

        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),OnBoardingFlowActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("user_id",user_id);
                intent.putExtras(bundle);
                startActivity(intent);
                dialog.dismiss();
            }
        });


        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void getHiddenGoalOnUserId() {
        APIService service=APIClient.getRetrofit().create(APIService.class);
        Call<GoalDetails> call=service.get_hidden_goals(user_id);
        call.enqueue(new Callback<GoalDetails>() {
            @Override
            public void onResponse(Call<GoalDetails> call, Response<GoalDetails> response) {
                GoalDetails goalDetails=response.body();
                hiddengoalsList.clear();
                int totalGoal=0;
                if (goalDetails.isSuccess()){
                    List<SingleGoal> goalList=goalDetails.getResult();
                    Log.d(Global.TAG, "Hidden Goal List: "+goalList);
                    totalGoal=goalList.size();
                    for (int i = 0; i < totalGoal; i++) {
                        SingleGoal datarow = goalList.get(i);
                        SingleGoal goal = new SingleGoal();
                        goal.setGoal_name(datarow.getGoal_name());
                        goal.setGoal_id(datarow.getZo_goal_id());
                        Log.d(Global.TAG, "onResponse:Goal Id: getHiddenGoalOnUserId"+datarow.getZo_goal_id());
                        hiddengoalsList.add(goal);
                    }
                    createCustomDialog(totalGoal);
                }else{
                    Toast.makeText(getActivity(), "No Hidden Goals..", Toast.LENGTH_SHORT).show();
                    createCustomDialog(0);
                }
            }

            @Override
            public void onFailure(Call<GoalDetails> call, Throwable t) {
                createCustomDialog(0);
                Log.d(Global.TAG, "onFailure: getHiddenGoalOnUserId"+t);
            }
        });

    }

    @SuppressLint("RestrictedApi")
    private void createCustomDialog(int totalGoal) {
        Log.d(Global.TAG, "createCustomDialog: ");
        if(totalGoal!=0) {
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.custom_display_hidden_gaol_design);
            dialog.setTitle("Conformation");
            LinearLayout ll_hidden_goal = (LinearLayout) dialog.findViewById(R.id.ll_hidden_goal);
            Button dialogButton = (Button) dialog.findViewById(R.id.btn_change);
            Button btn_dia_cancle = (Button) dialog.findViewById(R.id.btn_dia_cancle);

            int j = 0;
            checkBox = new CheckBox[totalGoal];

            for (int i = 0; i < totalGoal; i++) {
                checkBox[i] = new CheckBox(new ContextThemeWrapper(getActivity(), R.style.AppTheme));
                checkBox[i].setId(i);
                checkBox[i].setText(hiddengoalsList.get(i).getGoal_name());
                Log.d(Global.TAG, "createCustomDialog: "+hiddengoalsList.get(i).getGoal_id());
                checkBox[i].setTextColor(getResources().getColor(R.color.colorBlack));
                ll_hidden_goal.addView(checkBox[i]);
                j++;

            }
            //ll_hidden_goal.setBackground(getResources().getDrawable(R.drawable.rounded_view));

            for (int i = 0; i < totalGoal; i++) {
                final int k = i;

                checkBox[k].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            listUncheck.add(hiddengoalsList.get(k).getGoal_id());
                            //set the flag if the checklist is emppty to hide the goal
                            if (listUncheck.size() > 0) {
                                flagUnhide = 1;
                            } else {
                                flagUnhide = 0;
                            }
                            Log.d(Global.TAG, "onCheckedChanged: listUncheck"+goalData.get(k).getGoal_id());
                        } else {
                            listUncheck.remove(hiddengoalsList.get(k).getGoal_id());
                            //set the flag if the checklist is emppty to hide the goal
                            if (listUncheck.size() > 0) {
                                flagUnhide = 1;
                            } else {
                                flagUnhide = 0;
                            }
                        }
                    }
                });
            }
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (flagUnhide==1){
                        dialog.dismiss();
                        unHideSelectedGoalOnId();
                    }else{
                        Toast.makeText(getActivity(), "Please Select Goal to Unhide ", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            btn_dia_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    flagUnhide = 0;
                    listUncheck.clear();
                }
            });

            dialog.show();
            Window window = dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        }else{

            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.custom_display_hidden_gaol_design);
            LinearLayout ll_hidden_goal = (LinearLayout) dialog.findViewById(R.id.ll_hidden_goal);
            Button dialogButton = (Button) dialog.findViewById(R.id.btn_change);
            Button btn_dia_cancle = (Button) dialog.findViewById(R.id.btn_dia_cancle);

            //hide the button for no record found
            dialogButton.setVisibility(View.GONE);
            btn_dia_cancle.setText("Ok");

            //Add the textview to display the message for no record found
            TextView message = new TextView(getActivity());
            message.setText("Sorry! No records found");
            ll_hidden_goal.addView(message);

            btn_dia_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
            Window window = dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
    }

    private void unHideSelectedGoalOnId() {
        Log.d(Global.TAG, "unHideSelectedGoalOnId: "+listUncheck.size());
        checkUncheckList="";
        for (String str : listUncheck)
        {
            checkUncheckList += str + ",";
        }
        if (checkUncheckList.endsWith(",")) {
            checkUncheckList = checkUncheckList.substring(0, checkUncheckList.length() - 1);
        }

        Log.d(Global.TAG, "unHideSelectedGoalOnId: checkUncheckList "+checkUncheckList);
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call = service.show_hidden_goals(user_id, checkUncheckList);
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult result=response.body();
                if (result.isSuccess()){
                    if (checkUncheckList!=null){
                        Snackbar.make(layout, " Program goal un-hidden successfully", Snackbar.LENGTH_LONG)
                                .setAction("CLOSE", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                    }
                                })
                                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                                .show();
                        //Refresh the current activity after the snackbar get hide using thread
                        final Intent intent = getActivity().getIntent();
                        Thread thread = new Thread(){
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(3000); // As I am using LENGTH_LONG in Toast
                                    startActivity(intent,bundleEffect);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        thread.start();
                    }

                }
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                Log.d(Global.TAG, "onFailure: unHideSelectedGoalOnId "+t);
            }
        });

    }
    private void init() {

        iv_spinner_day=(ImageView)layout.findViewById(R.id.iv_spinner_day);

        goalData = new ArrayList<>();
        userGoalDetails = new ArrayList<>();
        goalNameArray= new ArrayList<>();
        goalIdArray= new ArrayList<>();
        goalCompletedArray= new ArrayList<>();
        goalRepositioningString= new ArrayList<>();
        hiddengoalsList=new ArrayList<>();

        //userSetGoalDetails= new ArrayList<>();
        tl = (TableLayout) layout.findViewById(R.id.tableDashboard);
        btn_checklist_submit= (Button) layout.findViewById(R.id.btn_checklist_submit);
        //btn_checklist_save= (Button) layout.findViewById(R.id.btn_checklist_save);
        btn_show_hidden= (Button) layout.findViewById(R.id.btn_show_hidden);
        ll_checklist=(LinearLayout)layout.findViewById(R.id.ll_checklist);
        ll_checklist_button=(LinearLayout)layout.findViewById(R.id.ll_checklist_button);
        ll_edit=(LinearLayout)layout.findViewById(R.id.ll_edit);
        spin_selectday = (Spinner)layout.findViewById(R.id.spin_selectday);
        bundleEffect = ActivityOptionsCompat.makeCustomAnimation(getActivity(), android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
        spLib=new SPLib(getActivity());
        user_id=spLib.getPref(SPLib.Key.USER_ID);
        listUncheck=new ArrayList<>();
        dayNamecopy=new ArrayList<>();
    }
    private void setChecklistDataToDatabase() {
        Log.d(Global.TAG, "setChecklistDataToDatabase: ");
        cal = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String CurrentDate = df.format(cal.getTime());
        //getUSDate();
        goalCompletedArray.clear();
        for(int i=0;i<goalData.size();i++){
            String et_value=allEditTextGoal.get(i).getText().toString();
            if (et_value==null)
                et_value="0";
            goalCompletedArray.add(goalData.get(i).getGoal_id()+":"+allEditTextGoal.get(i).getText().toString());
        }

        String listString ="";
        for (String s : goalCompletedArray)
        {
            listString += s + ";";
        }
        if (listString.endsWith(";")) {
            listString = listString.substring(0, listString.length() - 1);
        }

        Log.d(Global.TAG, "setChecklistDataToDatabase:CurrentDate "+CurrentDate+" ListString:"+listString+"CurrentDate"+CurrentDate+"user Id:"+user_id);
        APIService service=APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.updateCheckList(user_id,CurrentDate,listString,"submit");
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    Toast.makeText(getActivity(), "List Updated Successfully..!", Toast.LENGTH_SHORT).show();
                    for(int i=0;i<goalData.size();i++){
                        allEditTextGoal.get(i).setText("");
                    }
                    startActivity(getActivity().getIntent(),bundleEffect);
                }else{
                    Toast.makeText(getActivity(), "Error! Click on 'Dashboard' from the left menu & try again submit the goals.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                Log.d(Global.TAG, "onFailure: setChecklistDataToDatabase: "+t);
            }
        });

    }

    private void getUSDate() {
        Log.d(Global.TAG, "getUSDate: ");
        APIService service=APIClient.getRetrofit().create(APIService.class);
        Call<DateModel> call=service.getDate();
        call.enqueue(new Callback<DateModel>() {
            @Override
            public void onResponse(Call<DateModel> call, Response<DateModel> response) {
                DateModel dateModel=response.body();
                if (dateModel.isSuccess()){
                    Log.d(Global.TAG, "onResponse: Current Us Date:"+dateModel.getDate());
                    CurrentUSDate=dateModel.getDate();
                }
            }

            @Override
            public void onFailure(Call<DateModel> call, Throwable t) {
                Log.d(Global.TAG, "onFailure: CurrentUSDate"+t);
            }
        });


    }

    private void getGoalDetailsForChecklistOnUserId() {
        final Dialog myLoader = Global.showDialog(getActivity());
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        Log.d(Global.TAG, "getGoalDetailsForChecklistOnUserId: ");
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<GoalDetails> call=service.getGoalDetailsForCheckList(user_id);
        call.enqueue(new Callback<GoalDetails>() {
            @Override
            public void onResponse(Call<GoalDetails> call, Response<GoalDetails> response) {
                GoalDetails goalDetails=response.body();
                if (goalDetails.isSuccess()){
                    Log.d(Global.TAG, "Week_start(): "+goalDetails.getWeek_start());
                    Log.d(Global.TAG, "Week_end(): "+goalDetails.getWeek_end());
                    if (goalDetails.getWeek_start().equals("")){
                        fillSpinnerOfDay(0);
                        spLib.sharedpreferences.edit().putBoolean(SPLib.Key.IsBusinessStart,true).commit();
                        spLib.sharedpreferences.edit().putString(SPLib.Key.BusinessStart,goalDetails.getBusiness_start()).commit();
                        spLib.sharedpreferences.edit().putString(SPLib.Key.BUSINESS_END,goalDetails.getBusiness_end()).commit();

                    }else{
                        week_start_date =goalDetails.getWeek_start();
                        week_end_date = goalDetails.getWeek_end();
                        fillSpinnerOfDay(1);
                        spLib.sharedpreferences.edit().putBoolean(String.valueOf(SPLib.Key.IsBusinessStart),false).commit();
                        spLib.sharedpreferences.edit().putString(SPLib.Key.BusinessStart,goalDetails.getBusiness_start()).commit();
                        spLib.sharedpreferences.edit().putString(SPLib.Key.BUSINESS_END,goalDetails.getBusiness_end()).commit();

                    }

                    List<SingleGoal> goalList=goalDetails.getResult();
                    Log.d(Global.TAG, "goalList: "+goalList.size());
                    testarrayEditSave = new String[goalList.size()];
                    goalData=goalList;
                   /* for (int i = 0; i < goalList.size(); i++) {

                        SingleGoal goal = new SingleGoal();
                        goal.setGoal_id(goalList.get(i).getGoal_id());
                        goal.setGoal_name(goalList.get(i).getGoal_name());
                        goal.setGoal_count(goalList.get(i).getGoal_count());
                        goal.setCumplusoryFlag(goalList.get(i).getCumplusoryFlag());
                        goal.setWeekly(goalList.get(i).getWeekly());
                        Log.d(Global.TAG, "Goal Id:: "+goal.getGoal_id());
                        goalData.add(goal);
                        Log.d(Global.TAG, "onResponse: Gosl Flag "+goalList.get(i).getCumplusoryFlag());
                    }*/
                    Log.d(Global.TAG, "onResponse: ");
                }
                myLoader.dismiss();
                createChecklist();

            }

            @Override
            public void onFailure(Call<GoalDetails> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: getGoalDetailsForChecklistOnUserId "+t);
            }
        });

    }

    private void createChecklist() {
        Log.d(Global.TAG, "createChecklist: "+goalData.size());
        ll_checklist.removeAllViews();
        allEditTextGoal.clear();
        ll_edit.removeAllViews();
        if (btn_checklist_submit.getVisibility()==View.GONE){
            btn_checklist_submit.setVisibility(View.VISIBLE);
            btn_checklist_submit.setText("Submit");
            Log.d(Global.TAG, "btn_checklist_submit: visible ");
        }
        btn_checklist_submit.setVisibility(View.VISIBLE);

        int j=1;
        iv_eye = new ImageView[goalData.size()];
        check_edit_value= new EditText[goalData.size()];
        for (int i = 0; i < goalData.size(); i++)
        {
            ll_checkdata = new LinearLayout(getActivity());
            ll_checkdata.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.FILL_PARENT));
            ll_checkdata.setPadding(20,20,20,20);
            ll_checkdata.setOrientation(LinearLayout.HORIZONTAL);
            ll_checkdata.setBackground(getResources().getDrawable(R.drawable.row_border_bottom));

            iv_eye[i]= new ImageView(getActivity());
            iv_eye[i].setId(i);
            iv_eye[i].setTag("goalName"+i);
            iv_eye[i].setPadding(10,10,10,10);
            getActivity().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            iv_eye[i].setBackgroundResource(outValue.resourceId); //Set the ripple effect to the image button dynamcially

            if (goalData.get(i).getCumplusoryFlag().equals("1")){
                iv_eye[i].setImageDrawable(getResources().getDrawable(R.mipmap.hide_grey));
            }else {
                iv_eye[i].setImageDrawable(getResources().getDrawable(R.mipmap.hide_black));
            }

            //iv_eye[i].setBackground(getResources().getDrawable(R.drawable.custom_ripple_border));
            ll_checkdata.addView(iv_eye[i]);

            check_goal_name = new TextView(getActivity());
            ViewGroup.LayoutParams params = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2.0f);
            check_goal_name.setLayoutParams(params);
            check_goal_name.setText(goalData.get(i).getGoal_name()+"("+goalData.get(i).getGoal_count()+")");
            goalNameArray.add(goalData.get(i).getGoal_name());
            allTexViewtGoalName.add(check_goal_name);
            check_goal_name.setTag("goalName"+i);
            check_goal_name.setTextColor(Color.BLACK);
            check_goal_name.setPadding(10,10,10,10);
            check_goal_name.setGravity(Gravity.LEFT);
            check_goal_name.setTypeface(Typeface.MONOSPACE);
            ll_checkdata.addView(check_goal_name); // Adding textView to tablerow.

            goalIdArray.add(goalData.get(i).getGoal_id());

            check_edit_value[i] = new EditText(getActivity());

            ll_checkEditdata = new LinearLayout(getActivity());
            ViewGroup.LayoutParams params1 = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2.0f);
            ll_checkEditdata.setLayoutParams(params1);

            allEditTextGoal.add(check_edit_value[i]);
            ll_checkEditdata.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            check_edit_value[i].setText(testarrayEditSave[i]);
            check_edit_value[i].setId(i);
            if (goalData.get(i).getGoal_count().equals("0")){
                check_edit_value[i].setFocusable(false);
                check_edit_value[i].setFocusableInTouchMode(false);
            }
            check_edit_value[i].setWidth(170);
            check_edit_value[i].setTextSize(15);
            check_edit_value[i].setBackground(getResources().getDrawable(R.drawable.rounded_view));
            check_edit_value[i].setTextColor(Color.BLACK);
            check_edit_value[i].setInputType(InputType.TYPE_CLASS_NUMBER);
            check_edit_value[i].setPadding(10,10,10,10);
            check_edit_value[i].setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            check_edit_value[i].setTypeface(Typeface.SANS_SERIF);
            //check_edit_value[i].setWidth(150);
            check_edit_value[i].setFilters(new InputFilter[] {new InputFilter.LengthFilter(5)});
            ll_checkEditdata.addView(check_edit_value[i]);
            ll_checkdata.addView(ll_checkEditdata); // Adding textView to tablerow.

            textCount= new TextView(getActivity());
            ViewGroup.LayoutParams params223 = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            textCount.setLayoutParams(params223);
            textCount.setId(i);
            textCount.setTypeface(Typeface.SANS_SERIF);
            textCount.setText(""+goalData.get(i).getRemainingGoals());
            Log.d(Global.TAG, "createChecklist:getRemainingGoals "+goalData.get(i).getRemainingGoals());
            ll_checkdata.addView(textCount); // Adding textView to Row for Count Value

            // Add the TableRow to the TableLayout
            ll_checklist.addView(ll_checkdata, new TableLayout.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT));
            j++;
        }

        Log.e("goalName",goalNameArray.toString());
        //if (goalData.size()>0){
        Log.d(Global.TAG, "createChecklist: Before Loop: GoalData"+goalData.size());

        for (int i = 0; i < goalData.size(); i++) {
            final int k = i;

            iv_eye[k].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(Global.TAG, "goalData iv_eye: "+goalData.size());
                    Log.d(Global.TAG, "iv_eye goaldata: "+goalData.get(k).getCumplusoryFlag()+":K:"+k);
                    if (goalData.get(k).getCumplusoryFlag().equals("1")){
                        Toast.makeText(getActivity(), "Unable to Hide !! Compulsory Goal.", Toast.LENGTH_SHORT).show();
                    }else {
                        new AlertDialog.Builder(getActivity())
                                .setMessage("Are you sure you want to hide " + goalData.get(k).getGoal_name())
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        hideSelectedGoalOnId(goalData.get(k).getGoal_id());
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                    }
                }
            });
            check_edit_value[k].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (goalData.get(k).getGoal_count().equals("0")){
                        Toast.makeText(getActivity(), "You have set '0' goals for this task. Please update your goal count from Add/Edit goals", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        //  }
    }
    /* @Override
     public void onClick(View view) {
       if (goalData.size()>0){
           for(int i=0;i<goalData.size();i++){
               if(view==iv_eye[i]){
                   Log.d(Global.TAG, "eye onClick: "+goalData.size()+" i:"+i);
                   String flag=goalData.get(i).getCumplusoryFlag();
                   if (flag!=null){
                       if (flag.equals("1")){
                           Toast.makeText(getActivity(), "Unable to Hide !! Compulsory Goal.", Toast.LENGTH_SHORT).show();
                       }else {
                           final String goal_id=goalData.get(i).getGoal_id();
                           new AlertDialog.Builder(getActivity())
                                   .setMessage("Are you sure you want to hide " + goalData.get(i).getGoal_name())
                                   .setCancelable(false)
                                   .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                       public void onClick(DialogInterface dialog, int id) {
                                           hideSelectedGoalOnId(goal_id);
                                       }
                                   })
                                   .setNegativeButton("No", null)
                                   .show();
                       }
                   }

               }
               if (view==check_edit_value[i]){
                   Log.d(Global.TAG, "Check onClick: ");
                   if (goalData.get(i).getGoal_count().equals("0")){
                       Toast.makeText(getActivity(), "You have set '0' goals for this task. Please update your goal count from Add/Edit goals", Toast.LENGTH_LONG).show();
                   }
               }
           }

       }
     }*/
    private void hideSelectedGoalOnId(String goal_id) {
        Log.d(Global.TAG, "hideSelectedGoalOnId: goal id:"+goal_id);
        APIService service=APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.hide_goalsonId(user_id,goal_id);
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult result=response.body();
                if (result.isSuccess()){

                    Snackbar.make(layout, "Program goal hidden successfully", Snackbar.LENGTH_LONG)
                            .setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                            .show();
                    //Refresh the current activity after the snackbar get hide using thread
                    final Intent intent = getActivity().getIntent();
                    Thread thread = new Thread(){
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000); // As I am using LENGTH_LONG in Toast
                                startActivity(intent,bundleEffect);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    thread.start();
                }
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                Log.d(Global.TAG, "onFailure: hideSelectedGoalOnId"+t);
            }
        });
    }

    private void ClickMeToOpenSpinner(View view) {
        spin_selectday.performClick();
    }

    private void getCurrentDay() {
        Calendar Curcal = Calendar.getInstance();
        //Curcal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        int CurrDay = Curcal.get(Calendar.DAY_OF_MONTH);
        int CurrMonth = Curcal.get(Calendar.MONTH);
        int CurrYear = Curcal.get(Calendar.YEAR);
        //Getting the current Day name for filling the checklist
        Date curDate = null;
        try {
            curDate = new SimpleDateFormat("MM-dd-yyyy").parse((CurrMonth+1)+"-"+CurrDay+"-"+CurrYear);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        currDayName = new SimpleDateFormat("EEEE").format(curDate);
        Log.d(Global.TAG, "currDayName: "+currDayName);

    }


  /*  public class spinnerAdapter extends ArrayAdapter<String> {

        public spinnerAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
            // TODO Auto-generated constructor stub
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            int count = super.getCount();
            return count>0 ? count-1 : count ;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Log.d(Global.TAG, "spinnerAdapter getView: ");
            return super.getView(position, convertView, parent);
        }
    }*/

    public  void fillSpinnerOfDay(int flagToFillSpinner) {
        //Gett the Current Day
        if (flagToFillSpinner == 0) { //Check 0 for is business not start then fill the spinner with default value else from date
            weekDates = null;
            ArrayList<String> dayName = new ArrayList<>(7);
            dayName.add(0, "Monday");
            dayName.add(1, "Tuesday");
            dayName.add(2, "Wednesday");
            dayName.add(3, "Thursday");
            dayName.add(4, "Friday");
            dayName.add(5, "Saturday");
            dayName.add(6, "Sunday");
            Log.d("mytag", dayName.toString());

            dayNamecopy=dayName;
            Log.d(Global.TAG, "fillSpinnerOfDay: dayNamecopy"+dayNamecopy.size());

            fillSpinnerDayName(dayName);
        } else {
            try {
                Date date1 = simpleDateFormat.parse(week_start_date);
                Date date2 = simpleDateFormat.parse(week_end_date);

                long difference = Math.abs(date1.getTime() - date2.getTime());
                long differenceDates = (difference / (24 * 60 * 60 * 1000)) + 1;
                Calendar c = Calendar.getInstance();
                c.setTime(date1);
                weekDates = new String[(int) differenceDates];
                weekDayName = new String[(int) differenceDates];
                ArrayList<String> dayName = new ArrayList<>((int) differenceDates);

                for (int i = 0; i < differenceDates; i++) {

                    day = c.get(Calendar.DAY_OF_MONTH);
                    monthOfYear = c.get(Calendar.MONTH);
                    year = c.get(Calendar.YEAR);
                    hour = c.get(Calendar.HOUR);
                    min = c.get(Calendar.MINUTE);

                    String month = String.valueOf(monthOfYear + 1);
                    String selectedDay = String.valueOf(day);

                    String CurrentTestDate = year + "-" + ((month.length() == 1 ? "0" + month.toString() : month.toString())) + "-" + ((selectedDay.toString().length() == 1 ? "0" + selectedDay.toString() : selectedDay.toString()));
                    Log.d(Global.TAG, "fillSpinnerOfDay:CurrentTestDate "+CurrentTestDate);
                    weekDates[i] = CurrentTestDate;
                    Date curDayDate = null;
                    try {
                        curDayDate = new SimpleDateFormat("yyyy-MM-dd").parse(CurrentTestDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String currDay = new SimpleDateFormat("EEEE").format(curDayDate);
                    weekDayName[i] = currDay;
                    dayName.add(i, weekDayName[i]);
                    Log.d(Global.TAG, "fillSpinnerOfDay:currDayName: "+currDayName+" currDay"+currDay+"curDayDate:"+curDayDate);

                    if (currDayName.equals(currDay)) {

                        positionForDiable = i;
                    }
                    c.add(Calendar.DATE, 1);
                }
                Log.d(Global.TAG, dayName.toString());
                fillSpinnerDayName(dayName);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
    private void fillSpinnerDayName(ArrayList<String> dayName) {

        Log.e("weekDayName 3",dayName.toString());

        AddContactActivity.spinnerAdapter adapter = new AddContactActivity.spinnerAdapter(getActivity(), android.R.layout.simple_list_item_1)
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                Log.d(Global.TAG, "getView: ");
                textView.setTextColor(Color.parseColor("#FFFFFF"));
                return textView;
            }
        };

        adapter.add("Today("+currDayName+")");
        adapter.addAll(dayName);
        spin_selectday.setAdapter(adapter);
        spin_selectday.setSelection(0);
        spin_selectday.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

//                int index = parent.getSelectedItemPosition();
//                ((TextView) spin_selectday.getSelectedView()).setTextColor(getResources().getColor(R.color.colorBlue));
                //spin_selectday.getChildAt(2).setBackgroundColor(getResources().getColor(R.color.colorRed);

                if(spin_selectday.getSelectedItem() == "Select Day")
                {
                    //getGoalDetailsForChecklistOnUserId();
                }else if(spin_selectday.getSelectedItem().toString().contains("Today"))
                {
                    Log.d(Global.TAG, "onItemSelected1: Today ");
                    if(spLib.getPrefBoolean(SPLib.Key.IsBusinessStart)==true)
                    {
                        Toast.makeText(getActivity(), "Business Date Not Started Yet", Toast.LENGTH_SHORT).show();
                    }else {
                        Log.d(Global.TAG, "onItemSelected2: Today ");
                        getGoalDetailsForChecklistOnUserId();
                        //startActivity(getActivity().getIntent(),bundleEffect);
                    }
                }
                else{
                    if(spLib.getPrefBoolean(SPLib.Key.IsBusinessStart)==true)
                    {
                        Log.d(Global.TAG, "onItemSelected3:Other day ");
                        Toast.makeText(getActivity(), "Business Date Not Started Yet", Toast.LENGTH_SHORT).show();
                    }else{
                        Log.d(Global.TAG, "onItemSelected4:  Other day Today ");
                        if(Global.isNetworkAvailable(getActivity())) {
                            if (positionForDiable < (position-1)) {

                                //  View parentLayout = layout.findViewById(android.R.id.content);
                                Snackbar.make(layout, "Future WeekDays are not Allowed to Select", Snackbar.LENGTH_LONG)
                                        .setAction("CLOSE", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                            }
                                        })
                                        .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                                        .show();
                            }else{
                                if(position!=0){
                                    selected_date = weekDates[position - 1];
                                    Log.d(Global.TAG, "onItemSelected: selected_date:"+weekDates[position - 1]);
                                }
                                getSelectedDayDataInChecklist(selected_date);
                            }
                        }else{
                            /*Intent intent = new Intent(getActivity(), ActivityNetworkCheck.class);
                            startActivity(intent, bundleEffect);
                            finish();*/

                            Toast.makeText(getActivity(), "Please Check Your Internet Connection...!", Toast.LENGTH_SHORT).show();}
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
                int index = parent.getSelectedItemPosition();
                ((TextView) spin_selectday.getSelectedView()).setTextColor(getResources().getColor(R.color.colorBlue));
            }
        });

    }

    private void getSelectedDayDataInChecklist(String selected_date) {
        final Dialog myLoader = Global.showDialog(getActivity());
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        Log.d(Global.TAG, "getSelectedDayDataInChecklist: selected_date "+selected_date);
        APIService service=APIClient.getRetrofit().create(APIService.class);
        Call<GoalDetails> call=service.getSelectedDayDataInChecklist(user_id,selected_date);
        call.enqueue(new Callback<GoalDetails>() {
            @Override
            public void onResponse(Call<GoalDetails> call, Response<GoalDetails> response) {
                GoalDetails goalDetails=response.body();
                userGoalDetails.clear();
                if (goalDetails.isSuccess()){

                    int j=1;
                    List<SingleGoal> goalList=goalDetails.getResult();
                    Log.d(Global.TAG, "getSelectedDayDataInChecklist: "+goalList.size());
                    testarrayEditSave = new String[goalList.size()];
                    userGoalDetails=goalList;
                    for (int i=0;i<userGoalDetails.size();i++){
                        Log.d(Global.TAG, "Admin Goals: "+userGoalDetails.get(i).getAdmin_goal_count());
                    }

                    /*for (int i = 0; i < goalList.size(); i++) {
                        SingleGoal row = goalList.get(i);

                        SingleGoal goal = new SingleGoal();
                        goal.setGoal_id(row.getGoal_id());
                        goal.setGoal_name(row.getGoal_name());
                        goal.setAdmin_goal_count(row.getGoal_count());
                        goal.setGoal_done_count(row.getGoal_done_count());
                        goal.setWeekly(row.getWeekly());
                        Log.d(Global.TAG, "Goal Count: "+row.getGoal_done_count());
                        userGoalDetails.add(goal);
                        j++;
                    }*/
                    createChecklistForEdit();
                }
                myLoader.dismiss();

            }

            @Override
            public void onFailure(Call<GoalDetails> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: getSelectedDayDataInChecklist"+t);
            }
        });

    }

    public void createChecklistForEdit(){
        //    check_edit_value.setText("");
        ll_checklist.removeAllViews();
        ll_edit.removeAllViews();
        // btn_checklist_save.setVisibility(View.GONE);

        btn_checklist_submit.setVisibility(View.GONE);
        goalIdArray.clear();
        allEditTextGoalOnDate.clear();
        check_edit_value_date= new EditText[userGoalDetails.size()];

        // spin_selectday
        int j=1;
        for (int i = 0; i < userGoalDetails.size(); i++)
        {
            ll_checkdata = new LinearLayout(getActivity());
            ll_checkdata.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.FILL_PARENT));
            ll_checkdata.setPadding(20,20,20,20);
            ll_checkdata.setOrientation(LinearLayout.HORIZONTAL);
            ll_checkdata.setBackground(getResources().getDrawable(R.drawable.row_border_bottom));

            check_goal_name = new TextView(getActivity());
            ViewGroup.LayoutParams params = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2.0f);
            check_goal_name.setLayoutParams(params);
            check_goal_name.setText(userGoalDetails.get(i).getGoal_name()+"("+userGoalDetails.get(i).getGoal_count()+")");
            goalNameArray.add(userGoalDetails.get(i).getGoal_name());
            allTexViewtGoalName.add(check_goal_name);
            check_goal_name.setTag("goalName"+i);
            check_goal_name.setTextColor(Color.BLACK);
            check_goal_name.setPadding(10,10,10,10);
            check_goal_name.setGravity(Gravity.LEFT);
            check_goal_name.setTypeface(Typeface.MONOSPACE);
            ll_checkdata.addView(check_goal_name); // Adding textView to tablerow.

            goalIdArray.add(userGoalDetails.get(i).getGoal_id());

            check_edit_value_date[i] = new EditText(getActivity());
            ll_checkEditdata = new LinearLayout(getActivity());
            ViewGroup.LayoutParams params1 = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2.0f);
            ll_checkEditdata.setLayoutParams(params1);
            allEditTextGoalOnDate.add(check_edit_value_date[i]);
            ll_checkEditdata.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            check_edit_value_date[i].setText(userGoalDetails.get(i).getGoal_done_count());
            if (userGoalDetails.get(i).getGoal_count().equals("0")){
                check_edit_value_date[i].setFocusable(false);
                check_edit_value_date[i].setFocusableInTouchMode(false);
            }
            check_edit_value_date[i].setId(i);
            check_edit_value_date[i].setWidth(100);
            //goalCompletedArray.add(check_edit_value.getText().toString());
            check_edit_value_date[i].setBackground(getResources().getDrawable(R.drawable.rounded_view));
            check_edit_value_date[i].setTextColor(Color.BLACK);
            check_edit_value_date[i].setPadding(10,10,10,10);
            check_edit_value_date[i].setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            check_edit_value_date[i].setInputType(InputType.TYPE_CLASS_NUMBER);
            check_edit_value_date[i].setTypeface(Typeface.SANS_SERIF);
            check_edit_value_date[i].setWidth(150);
            check_edit_value_date[i].setFilters(new InputFilter[] {new InputFilter.LengthFilter(5)});
            ll_checkEditdata.addView(check_edit_value_date[i]);
            ll_checkdata.addView(ll_checkEditdata); // Adding textView to tablerow.

            textCount= new TextView(getActivity());
            ViewGroup.LayoutParams params223 = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            textCount.setLayoutParams(params223);
            textCount.setId(i);
            textCount.setTypeface(Typeface.SANS_SERIF);
            textCount.setText(""+userGoalDetails.get(i).getRemainingGoals());
            ll_checkdata.addView(textCount); // Adding textView to Row for Count Value


            // Add the TableRow to the TableLayout
            ll_checklist.addView(ll_checkdata, new TableLayout.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT));
            j++;
        }
        btn_edit_submit = new Button(getActivity());
        btn_edit_submit.setText("Edit And Submit");
        ViewGroup.LayoutParams params1 = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btn_edit_submit.setLayoutParams(params1);
        btn_edit_submit.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        btn_edit_submit.setTextColor(Color.WHITE);
        btn_edit_submit.setPadding(10,10,10,10);
        btn_edit_submit.setBackground(getResources().getDrawable(R.drawable.rounded_button));
        btn_edit_submit.setTypeface(Typeface.MONOSPACE);
        ll_edit.addView(btn_edit_submit, new TableLayout.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        btn_edit_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag=0;
                View view = getActivity().getCurrentFocus();
               /* if (view != null) {          //Check for is soft keyboard is open if open close it
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }*/
                if(Global.isNetworkAvailable(getActivity())) {
                    goalCompletedArray.clear();
                    for(int i=0;i<userGoalDetails.size();i++){
                        goalCompletedArray.add(userGoalDetails.get(i).getGoal_id()+":"+allEditTextGoalOnDate.get(i).getText().toString());
                        if(allEditTextGoalOnDate.get(i).getText().toString().length()>0) {
//                            if (allEditTextGoalOnDate.get(i).getText().toString().equals("0")){//Check if user enter zero value
//                                allEditTextGoalOnDate.get(i).setError("Enter valid goal numbers. Exclusion for zero.");
//                                flag = 2;
//                            }else {
                            flag = 1;
//                            }
                        }
                    }
                    if (flag==1){       //Check user not submit on empty edit text
                        if (spLib.getPref(SPLib.Key.BusinessStart).equals(true)) {
                            Toast.makeText(getActivity(), "Business Date Not Started Yet", Toast.LENGTH_LONG).show();
                        } else {
                            setEditChecklistDataToDatabaseOnDay();
                        }
                    }else{
                        Toast.makeText(getActivity(), "Please fill-up at least one text box & submit your checklist", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Intent intent = new Intent(getActivity(), NetworkCheckActivity.class);
                    startActivity(intent, bundleEffect);
                }
            }
        });

      /*  for (int i = 0; i < userGoalDetails.size(); i++) {
            final int k = i;
            check_edit_value_date[k].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userGoalDetails.get(k).getAdmin_goal_count().equals("0")){
                        Toast.makeText(getActivity(), "You have set '0' goals for this task. Please update your goal count from Add/Edit goals", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }*/
    }
    private void setEditChecklistDataToDatabaseOnDay() {
        Log.d(Global.TAG, "setEditChecklistDataToDatabaseOnDay: ");
        cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String CurrentDate = df.format(cal.getTime());

     /*   goalCompletedArray.clear();
        for(int i=0;i<goalData.size();i++){
            String et_value=allEditTextGoal.get(i).getText().toString();
            goalCompletedArray.add(goalData.get(i).getGoal_id()+":"+allEditTextGoal.get(i).getText().toString());
        }

        String listString = "";

        for (String s : goalCompletedArray)
        {
            listString += s + ";";
        }
        if (listString.endsWith(";")) {
            listString = listString.substring(0, listString.length() - 1);
        }*/
        goalCompletedArray.clear();
        for(int i=0;i<userGoalDetails.size();i++){
            goalCompletedArray.add(userGoalDetails.get(i).getGoal_id()+":"+allEditTextGoalOnDate.get(i).getText().toString());
        }
        //Convert the valuer array list into string and convert the last , commas from string
        String listString = "",nameEve="",goalId="";

        for (String s : goalCompletedArray)
        {
            listString += s + ";";
        }
        if (listString.endsWith(";")) {
            listString = listString.substring(0, listString.length() - 1);
        }
        Log.d(Global.TAG, "setEditChecklistDataToDatabaseOnDay:selected_date "+selected_date+" ListString:"+listString);
        APIService service=APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> call=service.updateCheckList(user_id,selected_date,listString,"");
        call.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){

                    Snackbar.make(layout, "Your Daily Checklist Details Updated Successfully!", Snackbar.LENGTH_LONG)
                            .setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                            .show();
                    //Refresh the current activity after the snackbar get hide using thread
                    final Intent intent = getActivity().getIntent();
                    Thread thread = new Thread(){
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000); // As I am using LENGTH_LONG in Toast
                                startActivity(intent,bundleEffect);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    thread.start();
                }
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                Log.d(Global.TAG, "onFailure:getSelectedDayDataInChecklist "+t);
                Toast.makeText(getActivity(), ""+t, Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }
}
