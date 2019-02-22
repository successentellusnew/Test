package com.success.successEntellus.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.success.successEntellus.R;
import com.success.successEntellus.activity.AddContactActivity;
import com.success.successEntellus.activity.DashboardActivity;
import com.success.successEntellus.activity.NetworkCheckActivity;
import com.success.successEntellus.activity.OnboardingTextCampaign;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.MyValidator;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.GetReferralLink;
import com.success.successEntellus.model.GoalDetails;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.model.SingleGoal;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 7/13/2018.
 */

public class DailyCheckListFragmentNew extends Fragment {
View layout;
    private String currDayName;
    private String week_start_date,week_end_date;
    public List<String> day_list;
    Spinner spin_selectday;
    ImageView iv_spinner_day;
    public static SPLib spLib;
    private String user_id;
    TextView textCount;
    TypedValue outValue = new TypedValue();
    String[]  weekDayName,weekDates,testarrayEditSave;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    LinearLayout ll_checklist,ll_checkdata,ll_checkEditdata,ll_checklist_button,ll_edit;
    Button btn_checklist_submit,btn_edit_submit,btn_checklist_save,btn_show_hidden,btn_repositioning;
    ArrayList<String> goalNameArray,goalIdArray,goalCompletedArray,listUncheck,goalRepositioningString; @Nullable
    List<TextView> allTexViewtGoalName = new ArrayList<TextView>();
    List<EditText> allEditTextGoal = new ArrayList<EditText>();
    List<EditText> allEditTextGoalOnDate = new ArrayList<EditText>();
    boolean isBusinessStart=false;
    static int positionForDiable=0;
    int day;
    int year;
    int hour;
    int min;
    int monthOfYear;
    TableLayout tl;
    TableRow tr,tr1,tr2;
    Bundle bundleEffect;
    ImageView[] iv_eye;
    EditText[] check_edit_value_date,check_edit_value;
    TextView check_goal_name;
    String selected_date;
    boolean today_flag=false;
    CheckBox[] checkBox;
    int flag=0,flagUnhide=0;
    private String checkUncheckList;
    public static List<SingleGoal> goalData,userGoalDetails,hiddengoalsList;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_daily_checklist, container, false);
        setHasOptionsMenu(true);
        init();
        getCurrentDay();
        if (Global.isNetworkAvailable(getActivity())) {
           // applySpinner(day_list,spin_selectday,"Today("+currDayName+")");
            getGoalDetailsForChecklistOnUserId();
        } else {
            Toast.makeText(getActivity(), "Please Check your Internet Connections...!", Toast.LENGTH_SHORT).show();
        }
        //getAllWeekDates();
        Log.d(Global.TAG, "Business Start: " + spLib.getPref(SPLib.Key.BusinessStart));
        Log.d(Global.TAG, "Business end: " + spLib.getPref(SPLib.Key.BUSINESS_END));
        Log.d(Global.TAG, "Business IsStart: " + spLib.getPrefBoolean(SPLib.Key.IsBusinessStart));

        iv_spinner_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickMeToOpenSpinner(view);
            }
        });

        spin_selectday.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                // Log.d(Global.TAG, "onItemSelected: "+spin_selectday.getSelectedItem().toString());
                if (spin_selectday.getSelectedItem().toString().contains("Today")) {
                    Log.d(Global.TAG, "onItemSelected:Today: "+isBusinessStart);
                 //   if (isBusinessStart){
                        if (!spLib.getPrefBoolean(SPLib.Key.IsBusinessStart)) {
                            Toast.makeText(getActivity(), "Business Date Not Started Yet", Toast.LENGTH_SHORT).show();
                        } else {
                        Log.d(Global.TAG, "onItemSelected: Today "+today_flag);
                        if (!today_flag){
                            getGoalDetailsForChecklistOnUserId();
                        }

                    }
                } else {
                    if (Global.isNetworkAvailable(getActivity())) {
                        if (spLib.getPrefBoolean(SPLib.Key.IsBusinessStart)) {
                            if (positionForDiable < (position - 1)) {
                                Snackbar.make(layout, "Future WeekDays are not Allowed to Select", Snackbar.LENGTH_LONG)
                                        .setAction("CLOSE", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                            }
                                        })
                                        .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                        .show();
                                // startActivity(getActivity().getIntent(),bundleEffect);

                                // spin_selectday.setSelection(0);
                            } else {
                                if (position != 0) {
                                    selected_date = weekDates[position - 1];
                                    Log.d(Global.TAG, "onItemSelected: selected_date:" + weekDates[position - 1]);
                                }
                                if (selected_date != "") {
                                    today_flag = false;
                                    getSelectedDayDataInChecklist(selected_date);

                                }

                            }
                        }else{
                            Toast.makeText(getActivity(), "Business Date Not Started Yet", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Please Check Your Internet Connection...!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(Global.TAG, "onNothingSelected: ");
            }
        });

    btn_checklist_submit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            flag=0;
            if(Global.isNetworkAvailable(getActivity())) {
                goalCompletedArray.clear();
                Log.d(Global.TAG, "Submit Goals: size"+goalData.size());
                for(int i=0;i<goalData.size();i++){
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
                if(!spLib.getPrefBoolean(SPLib.Key.IsBusinessStart))
                {
                    Toast.makeText(getActivity(), "Business Date Not Started Yet", Toast.LENGTH_SHORT).show();
                }else {
                    submitCheckList();
                }
                   /* }else{
                        Toast.makeText(getActivity(), "Please fill-up at least one text box & submit your checklist", Toast.LENGTH_SHORT).show();
                    }*/
            }else{
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

       /* InputMethodManager inputMethodManager = (InputMethodManager)  getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
*/
        return layout;
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
        Log.d(Global.TAG, "unHideSelectedGoalOnId: UserId "+user_id);
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
    private void submitCheckList() {
        Log.d(Global.TAG, "submitCheckList: ");
         Calendar cal = Calendar.getInstance();

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
                    Toast.makeText(getActivity(), "List Updated Successfully..!", Toast.LENGTH_LONG).show();
                    for(int i=0;i<goalData.size();i++){
                        allEditTextGoal.get(i).setText("");
                    }
                    startActivity(getActivity().getIntent(),bundleEffect);
                }else{
                    Toast.makeText(getActivity(), "Please enter at least 1 goal to submit checklist..!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                Log.d(Global.TAG, "onFailure: setChecklistDataToDatabase: "+t);
            }
        });

    }

    private void getAllWeekDates() {
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

            applySpinner(dayName,spin_selectday,"Today("+currDayName+")");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void ClickMeToOpenSpinner(View view) {
        spin_selectday.performClick();
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
                if (goalDetails!=null){
                    if (goalDetails.isSuccess()){
                        Log.d(Global.TAG, "Week_start(): "+goalDetails.getWeek_start());
                        Log.d(Global.TAG, "Week_end(): "+goalDetails.getWeek_end());
                        if (goalDetails.getWeek_start().equals("")){
                            //fillSpinnerOfDay(0);
                            weekDates = null;
                            applySpinner(day_list,spin_selectday,"Today("+currDayName+")");
                            isBusinessStart=false;
                            spLib.sharedpreferences.edit().putBoolean(SPLib.Key.IsBusinessStart,false).commit();
                            spLib.sharedpreferences.edit().putString(SPLib.Key.BusinessStart,goalDetails.getBusiness_start()).commit();
                            spLib.sharedpreferences.edit().putString(SPLib.Key.BUSINESS_END,goalDetails.getBusiness_end()).commit();

                        }else{
                            //applySpinner(day_list,spin_selectday,"Today("+currDayName+")");
                            week_start_date =goalDetails.getWeek_start();
                            week_end_date = goalDetails.getWeek_end();
                            isBusinessStart=true;
                            spLib.sharedpreferences.edit().putBoolean(String.valueOf(SPLib.Key.IsBusinessStart),true).commit();
                            spLib.sharedpreferences.edit().putString(SPLib.Key.BusinessStart,goalDetails.getBusiness_start()).commit();
                            spLib.sharedpreferences.edit().putString(SPLib.Key.BUSINESS_END,goalDetails.getBusiness_end()).commit();
                            getAllWeekDates();

                        }

                        List<SingleGoal> goalList=goalDetails.getResult();
                        Log.d(Global.TAG, "goalList: "+goalList.size());
                        testarrayEditSave = new String[goalList.size()];
                        goalData=goalList;

                    }

                    Log.d(Global.TAG, "Before CheckList: ");
                    createChecklist();
                    Log.d(Global.TAG, "After CheckList: ");
                    myLoader.dismiss();
                }

            }

            @Override
            public void onFailure(Call<GoalDetails> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure: getGoalDetailsForChecklistOnUserId "+t);
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
               /* View view = getActivity().getCurrentFocus();
                if (view != null) {          //Check for is soft keyboard is open if open close it
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
                        if (!spLib.getPrefBoolean(SPLib.Key.IsBusinessStart)) {
                            Toast.makeText(getActivity(), "Business Date Not Started Yet", Toast.LENGTH_LONG).show();
                        } else {
                            updateCheckList();
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
    private void updateCheckList() {
        Log.d(Global.TAG, "updateCheckList: ");
        Calendar cal = Calendar.getInstance();
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

    private void init() {
        spLib=new SPLib(getActivity());
        day_list=new ArrayList<>();
        day_list.add(0, "Monday");
        day_list.add(1, "Tuesday");
        day_list.add(2, "Wednesday");
        day_list.add(3, "Thursday");
        day_list.add(4, "Friday");
        day_list.add(5, "Saturday");
        day_list.add(6, "Sunday");
        spLib=new SPLib(getActivity());
        iv_spinner_day=(ImageView)layout.findViewById(R.id.iv_spinner_day);
        user_id=spLib.getPref(SPLib.Key.USER_ID);

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
        //dayNamecopy=new ArrayList<>();

    }
    private void getCurrentDay() {
        Calendar Curcal = Calendar.getInstance();
        Curcal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        int CurrDay = Curcal.get(Calendar.DAY_OF_MONTH);
        int CurrMonth = Curcal.get(Calendar.MONTH);
        int CurrYear = Curcal.get(Calendar.YEAR);

        Date curDate = null;
        try {
            curDate = new SimpleDateFormat("MM-dd-yyyy").parse((CurrMonth+1)+"-"+CurrDay+"-"+CurrYear);
            Log.d(Global.TAG, "curDate: "+curDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        currDayName = new SimpleDateFormat("EEEE").format(curDate);
        Log.d(Global.TAG, "currDayName: "+currDayName);

    }
    private void applySpinner(final List<String> taglist, Spinner sp_name, String tag_string) {
        //Adding spinner on Repeate By:: When Monthly Selected Add 12
        spinnerAdapter adapterRepeateDaily = new spinnerAdapter(getActivity(), android.R.layout.simple_list_item_1);
        adapterRepeateDaily.add(tag_string);
        adapterRepeateDaily.addAll(taglist);
        adapterRepeateDaily.add(tag_string);
        sp_name.setAdapter(adapterRepeateDaily);
        //sp_name.setSelection(0);
        sp_name.setEnabled(true);

    }
    public static class spinnerAdapter extends ArrayAdapter<String> {

        public spinnerAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
            // TODO Auto-generated constructor stub
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            TextView textView = (TextView) super.getView(position, convertView, parent);
            Log.d(Global.TAG, "getView: Text: "+textView.getText().toString());
            textView.setTextColor(Color.parseColor("#FFFFFF"));
            return textView;

           /* if ( positionForDiable < (position - 1)){
                textView.setTextColor(context.getResources().getColor(R.color.colorGrey));
            }else{
                textView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            }*/

        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View view = super.getDropDownView(position, convertView, parent);
            TextView tv = (TextView) view;
            if (spLib.getPrefBoolean(SPLib.Key.IsBusinessStart)) {
                if(positionForDiable < (position - 1)) {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
            }else{
                tv.setTextColor(Color.GRAY);
            }

            return view;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            int count =super.getCount();
            return count>0 ? count-1 : count ;
        }

        @Override
        public boolean isEnabled(int position) {

            if (spLib.getPrefBoolean(SPLib.Key.IsBusinessStart)) {
                if ( positionForDiable < (position - 1)){
                    Log.d(Global.TAG, "isEnabled: false ");
                    return false;
                }else{
                    Log.d(Global.TAG, "isEnabled: true ");
                    return true;
                }
            }else{
                return false;
            }

        }
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
            check_goal_name.setTypeface(Typeface.DEFAULT);
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
        today_flag=true;
    }
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.dashboard_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==R.id.action_checklist_help){
            Intent intent=new Intent(getActivity(),OnboardingTextCampaign.class);
            Bundle bundle=new Bundle();
            bundle.putString("keyString","daily_checklist");
            intent.putExtras(bundle);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

   /* private void openDialogEarnRefferalMoney() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.earn_refferal_money_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Confirmation");

        Button btn_earn_dialog_dissmiss=dialog.findViewById(R.id.btn_earn_dialog_dissmiss);
        final TextView tv_refferal_link=dialog.findViewById(R.id.tv_refferal_link);
        final EditText edt_phone_referral1=dialog.findViewById(R.id.edt_phone_referral1);
        final EditText edt_email_referral1=dialog.findViewById(R.id.edt_email_referral1);
        ImageButton ib_add_email_phone=dialog.findViewById(R.id.ib_add_email_phone);
        Button btn_share_referral_link=dialog.findViewById(R.id.btn_share_referral_link);
        Button btn_send_referral_link=dialog.findViewById(R.id.btn_send_referral_link);
        final LinearLayout ll_add_email_phone=dialog.findViewById(R.id.ll_add_email_phone);
        final List<String> emailList=new ArrayList<>();
        final List<String> phoneList=new ArrayList<>();
        emailEditList=new ArrayList<>();
        phoneEditList=new ArrayList<>();

        getReferralLink(tv_refferal_link);

        ib_add_email_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyValidator.isValidEmailAdd(edt_email_referral1) || MyValidator.isValidMobileNo(edt_phone_referral1)){
                    edt_email_referral1.setError(null);
                    edt_phone_referral1.setError(null);
                    if (emailEditList.size()>0){
                        int lastposition=emailEditList.size()-1;
                        if (MyValidator.isValidEmailAdd(emailEditList.get(lastposition)) || MyValidator.isValidMobileNo(phoneEditList.get(lastposition))){
                            addEmailPhoneView(ll_add_email_phone);
                        }
                    }else{
                        addEmailPhoneView(ll_add_email_phone);
                    }

                }else{
                    Toast.makeText(getActivity(), "Please Enter Email or Phone..!", Toast.LENGTH_SHORT).show();
                }

            }
        });


        btn_earn_dialog_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_share_referral_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = tv_refferal_link.getText().toString();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String ShareSub = "Sign Up Link By "+spLib.getPref(SPLib.Key.USER_NAME);
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ShareSub);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, url);
                startActivity(Intent.createChooser(sharingIntent, "Share Referral Link Via"));
            }
        });

        btn_send_referral_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Global.TAG, "emailEditList: "+emailEditList.size());
                Log.d(Global.TAG, "phoneEditList: "+phoneEditList.size());
                emailList.clear();
                phoneList.clear();

                emailList.add(edt_email_referral1.getText().toString());
                phoneList.add(edt_phone_referral1.getText().toString());

                if (emailEditList.size()>0 && phoneEditList.size()>0){

                    if (emailEditList.size()==phoneEditList.size()){
                        for (int i=0;i<emailEditList.size();i++){
                            emailList.add(emailEditList.get(i).getText().toString());
                            phoneList.add(phoneEditList.get(i).getText().toString());
                        }
                    }else{
                        Log.d(Global.TAG, "Both size are mismatched..!: ");
                    }

                }
                Log.d(Global.TAG, "emailList: "+emailList);
                Log.d(Global.TAG, "phoneList: "+phoneList);

                String emailReferral = "";
                for (String s : emailList) {
                    emailReferral += s + ",";
                }
                if (emailReferral.endsWith(",")) {
                    emailReferral = emailReferral.substring(0, emailReferral.length() - 1);
                }

                String phoneReferral = "";
                for (String s : phoneList) {
                    phoneReferral += s + ",";
                }
                if (phoneReferral.endsWith(",")) {
                    phoneReferral = phoneReferral.substring(0, phoneReferral.length() - 1);
                }
                Log.d(Global.TAG, "emailReferral: "+emailReferral);
                Log.d(Global.TAG, "phoneReferral: "+phoneReferral);

                JSONObject paramObj = new JSONObject();
                try {
                    paramObj.put("userId", user_id);
                    paramObj.put("platform", "2");
                    paramObj.put("emailReferral", emailReferral);
                    paramObj.put("phoneReferral", phoneReferral);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Log.d(Global.TAG, "Send Link Param: "+paramObj.toString());
                final Dialog myLoader = Global.showDialog(getActivity());
                myLoader.show();
                myLoader.setCanceledOnTouchOutside(true);
                    APIService service= APIClient.getRetrofit().create(APIService.class);
                    Call<JsonResult> call = service.send_referral_link(paramObj.toString());
                    call.enqueue(new Callback<JsonResult>() {
                        @Override
                        public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                            if (response.isSuccessful()){
                                JsonResult jsonResult=response.body();
                                if (jsonResult.isSuccess()){
                                    Toast.makeText(getActivity(), ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(getActivity(), ""+jsonResult.getResult(), Toast.LENGTH_LONG).show();
                                }

                            }
                            myLoader.dismiss();
                        }

                        @Override
                        public void onFailure(Call<JsonResult> call, Throwable t) {

                            myLoader.dismiss();
                            Log.d(Global.TAG, "onFailure: sendLink "+t);
                        }
                    });
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void getReferralLink(final TextView tv_refferal_link) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", user_id);
            paramObj.put("platform", "2");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(Global.TAG, "Send Link Param: "+paramObj.toString());
        final Dialog myLoader = Global.showDialog(getActivity());
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<GetReferralLink> call = service.getReferralLink(paramObj.toString());
        call.enqueue(new Callback<GetReferralLink>() {
            @Override
            public void onResponse(Call<GetReferralLink> call, Response<GetReferralLink> response) {
                if (response.isSuccessful()){
                    GetReferralLink getReferralLink=response.body();
                    if (getReferralLink.isSuccess()){

                      GetReferralLink.RLink listLink=getReferralLink.getResult();
                           tv_refferal_link.setText(listLink.getLink());
                           Log.d(Global.TAG, "onResponse: Link:"+listLink.getLink());

                    }
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetReferralLink> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:GetReferralLink "+t);
            }
        });


    }

    private void addEmailPhoneView(LinearLayout ll_add_email_phone) {
        LayoutInflater inflater= (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View attachmentView = inflater.inflate(R.layout.earn_refferal_add_email_row, null);

        final EditText edt_email_referral = (EditText) attachmentView.findViewById(R.id.edt_email_referral);
        final EditText edt_phone_referral = (EditText) attachmentView.findViewById(R.id.edt_phone_referral);
        ImageButton ib_remove_view = (ImageButton) attachmentView.findViewById(R.id.ib_remove_view);
        ll_add_email_phone.addView(attachmentView);
        emailEditList.add(edt_email_referral);
        phoneEditList.add(edt_phone_referral);

        ib_remove_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attachmentView.setVisibility(View.GONE);
                emailEditList.remove(edt_email_referral);
                phoneEditList.remove(edt_phone_referral);
            }
        });
    }*/
}
