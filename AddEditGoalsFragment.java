package com.success.successEntellus.fragment;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.view.ContextThemeWrapper;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.success.successEntellus.R;
import com.success.successEntellus.activity.DashboardActivity;
import com.success.successEntellus.activity.DashboardFragment;
import com.success.successEntellus.activity.SignUpActivity;
import com.success.successEntellus.lib.Global;
import com.success.successEntellus.lib.SPLib;
import com.success.successEntellus.model.AdminGoalDetails;
import com.success.successEntellus.model.AdminGoals;
import com.success.successEntellus.model.GetCustomizeModuleList;
import com.success.successEntellus.model.JsonResult;
import com.success.successEntellus.model.SingleModule;
import com.success.successEntellus.network.APIClient;
import com.success.successEntellus.service.APIService;
import com.success.successEntellus.tooltip.SimpleTooltip;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 5/25/2018.
 */

public class AddEditGoalsFragment extends Fragment {
    Button btn_critical,btn_core,btn_baseline,btn_save,btn_core_next,btn_baseline_prev,btn_critical_next,btn_core_prev;
    EditText edt_start_date;
    ImageView ibtn_calender_start_date;
    String user_id;
    SPLib spLib;
    Context context;
    List<AdminGoalDetails> adminGoalList;
    TextView tv_startdate;
    View layout;
    LinearLayout myView,ll_critical,ll_core,ll_baseline,ll_edit_goal_hint,ll_edit_goal_data,ll_goal_data_hori,ll_edit_goal_data2,ll_edit_goal_data1;
    LinearLayout core_activity_layout,critical_success_layout,baseline_layout,linear_main;
    private ImageView[] iv_hint;
    List<EditText> allEditTextEnteredGoal = new ArrayList<EditText>();
    private boolean without_goalset;
    private String dayOfWeek;
    int cday,cmonth,cyear,hour,min;
    Calendar cal;
    private int flag;
    ArrayList<String> goalEnteredCriticalArray,goalEnteredCoreArray,goalEnteredBaselineArray,alladmingoals;
    DashboardActivity dashboardActivity;
    private String listAllAdminGoals="";
    List<SingleModule> moduleList=new ArrayList<>();
    List<String> moduleIds=new ArrayList<>();
    String current_date;

    @SuppressLint("ValidFragment")
    public AddEditGoalsFragment(boolean onBoardFlag) {
        this.without_goalset=onBoardFlag;
    }

    public AddEditGoalsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        layout=inflater.inflate(R.layout.fragment_add_edit_goals,container,false);
        setHasOptionsMenu(true);

       /* if (!Global.isModulePresent(getActivity(),"2")){
            openDialogDisplayAlert();
        }*/

        init();
        getModuleDetails();
        getAdminGoals();

       if (!without_goalset){
            if (spLib.getPref(SPLib.Key.BusinessStart)!=null) {//Check if Business date not Null then set it
                tv_startdate.setText(spLib.getPref(SPLib.Key.BusinessStart));
            }
        }

       // bundleEffect = ActivityOptionsCompat.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
        cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        cday = cal.get(Calendar.DAY_OF_MONTH);
        cmonth = cal.get(Calendar.MONTH);
        cyear = cal.get(Calendar.YEAR);
        hour = cal.get(Calendar.HOUR);
        min =cal.get(Calendar.MINUTE);
        current_date=(cmonth+1)+"-"+cday+"-"+cyear;
        Log.d(Global.TAG, "Current Date: "+(cmonth+1)+"-"+cday+"-"+cyear);

        if (without_goalset){
            edt_start_date.setText((cmonth+1)+"-"+cday+"-"+cyear);
        }

        btn_critical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View views = getActivity().getCurrentFocus();
                if (views != null) {          //Check for is soft keyboard is open if open close it
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                RoundRectShape rect = new RoundRectShape(
                        new float[] {10,10, 0,0, 0,0,0,0},
                        null,
                        null);
                ShapeDrawable bg = new ShapeDrawable(rect);
                bg.getPaint().setColor(getResources().getColor(R.color.colorOragne));
                btn_critical.setBackgroundDrawable(bg);
                RoundRectShape rect1 = new RoundRectShape(
                        new float[] {0,0,10,10,0,0,0,0},
                        null,
                        null);
                ShapeDrawable bg1 = new ShapeDrawable(rect1);
                bg1.getPaint().setColor(getResources().getColor(R.color.colorPrimary));
                btn_baseline.setBackgroundDrawable(bg1);

                critical_success_layout.setVisibility(View.VISIBLE);
                core_activity_layout.setVisibility(View.GONE);
                baseline_layout.setVisibility(View.GONE);

                btn_core.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btn_core.setTextColor(getResources().getColor(R.color.colorWhite));
            }
        });
        btn_core.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getActivity().getCurrentFocus();
                if (view != null) {          //Check for is soft keyboard is open if open close it
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                RoundRectShape rect = new RoundRectShape(
                        new float[] {10,10, 0,0, 0,0,0,0},
                        null,
                        null);
                ShapeDrawable bg = new ShapeDrawable(rect);
                bg.getPaint().setColor(getResources().getColor(R.color.colorPrimary));
                btn_critical.setBackgroundDrawable(bg);
                RoundRectShape rect1 = new RoundRectShape(
                        new float[] {0,0,10,10,0,0,0,0},
                        null,
                        null);
                ShapeDrawable bg1 = new ShapeDrawable(rect1);
                bg1.getPaint().setColor(getResources().getColor(R.color.colorPrimary));
                btn_baseline.setBackgroundDrawable(bg1);

                critical_success_layout.setVisibility(View.GONE);
                core_activity_layout.setVisibility(View.VISIBLE);
                baseline_layout.setVisibility(View.GONE);

                btn_core.setBackgroundColor(getResources().getColor(R.color.colorOragne));
                btn_core.setTextColor(getResources().getColor(R.color.colorWhite));
            }
        });

        btn_baseline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getActivity().getCurrentFocus();
                if (view != null) {          //Check for is soft keyboard is open if open close it
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                RoundRectShape rect = new RoundRectShape(
                        new float[] {10,10, 0,0, 0,0,0,0},
                        null,
                        null);
                ShapeDrawable bg = new ShapeDrawable(rect);
                bg.getPaint().setColor(getResources().getColor(R.color.colorPrimary));
                btn_critical.setBackgroundDrawable(bg);
                RoundRectShape rect1 = new RoundRectShape(
                        new float[] {0,0,10,10,0,0,0,0},
                        null,
                        null);
                ShapeDrawable bg1 = new ShapeDrawable(rect1);
                bg1.getPaint().setColor(getResources().getColor(R.color.colorOragne));
                btn_baseline.setBackgroundDrawable(bg1);

                critical_success_layout.setVisibility(View.GONE);
                core_activity_layout.setVisibility(View.GONE);
                baseline_layout.setVisibility(View.VISIBLE);
                btn_core.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btn_core.setTextColor(getResources().getColor(R.color.colorWhite));
            }
        });
        btn_critical_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getActivity().getCurrentFocus();
                if (view != null) {          //Check for is soft keyboard is open if open close it
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                RoundRectShape rect = new RoundRectShape(
                        new float[] {10,10, 0,0, 0,0,0,0},
                        null,
                        null);
                ShapeDrawable bg = new ShapeDrawable(rect);
                bg.getPaint().setColor(getResources().getColor(R.color.colorPrimary));
                btn_critical.setBackgroundDrawable(bg);
                RoundRectShape rect1 = new RoundRectShape(
                        new float[] {0,0,10,10,0,0,0,0},
                        null,
                        null);
                ShapeDrawable bg1 = new ShapeDrawable(rect1);
                bg1.getPaint().setColor(getResources().getColor(R.color.colorPrimary));
                btn_baseline.setBackgroundDrawable(bg1);

                critical_success_layout.setVisibility(View.GONE);
                core_activity_layout.setVisibility(View.VISIBLE);
                baseline_layout.setVisibility(View.GONE);

                btn_core.setBackgroundColor(getResources().getColor(R.color.colorOragne));
                btn_core.setTextColor(getResources().getColor(R.color.colorWhite));
            }
        });
        btn_core_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getActivity().getCurrentFocus();
                if (view != null) {          //Check for is soft keyboard is open if open close it
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                RoundRectShape rect = new RoundRectShape(
                        new float[] {10,10, 0,0, 0,0,0,0},
                        null,
                        null);
                ShapeDrawable bg = new ShapeDrawable(rect);
                bg.getPaint().setColor(getResources().getColor(R.color.colorOragne));
                btn_critical.setBackgroundDrawable(bg);
                RoundRectShape rect1 = new RoundRectShape(
                        new float[] {0,0,10,10,0,0,0,0},
                        null,
                        null);
                ShapeDrawable bg1 = new ShapeDrawable(rect1);
                bg1.getPaint().setColor(getResources().getColor(R.color.colorPrimary));
                btn_baseline.setBackgroundDrawable(bg1);

                critical_success_layout.setVisibility(View.VISIBLE);
                core_activity_layout.setVisibility(View.GONE);
                baseline_layout.setVisibility(View.GONE);
                btn_core.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btn_core.setTextColor(getResources().getColor(R.color.colorWhite));
            }
        });
        btn_baseline_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getActivity().getCurrentFocus();
                if (view != null) {          //Check for is soft keyboard is open if open close it
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                RoundRectShape rect = new RoundRectShape(
                        new float[] {10,10, 0,0, 0,0,0,0},
                        null,
                        null);
                ShapeDrawable bg = new ShapeDrawable(rect);
                bg.getPaint().setColor(getResources().getColor(R.color.colorPrimary));
                btn_critical.setBackgroundDrawable(bg);
                RoundRectShape rect1 = new RoundRectShape(
                        new float[] {0,0,10,10,0,0,0,0},
                        null,
                        null);
                ShapeDrawable bg1 = new ShapeDrawable(rect1);
                bg1.getPaint().setColor(getResources().getColor(R.color.colorPrimary));
                btn_baseline.setBackgroundDrawable(bg1);

                critical_success_layout.setVisibility(View.GONE);
                core_activity_layout.setVisibility(View.VISIBLE);
                baseline_layout.setVisibility(View.GONE);

                btn_core.setBackgroundColor(getResources().getColor(R.color.colorOragne));
                btn_core.setTextColor(getResources().getColor(R.color.colorWhite));
            }
        });
        btn_core_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getActivity().getCurrentFocus();
                if (view != null) {          //Check for is soft keyboard is open if open close it
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                RoundRectShape rect = new RoundRectShape(
                        new float[] {10,10, 0,0, 0,0,0,0},
                        null,
                        null);
                ShapeDrawable bg = new ShapeDrawable(rect);
                bg.getPaint().setColor(getResources().getColor(R.color.colorPrimary));
                btn_critical.setBackgroundDrawable(bg);
                RoundRectShape rect1 = new RoundRectShape(
                        new float[] {0,0,10,10,0,0,0,0},
                        null,
                        null);
                ShapeDrawable bg1 = new ShapeDrawable(rect1);
                bg1.getPaint().setColor(getResources().getColor(R.color.colorOragne));
                btn_baseline.setBackgroundDrawable(bg1);

                critical_success_layout.setVisibility(View.GONE);
                core_activity_layout.setVisibility(View.GONE);
                baseline_layout.setVisibility(View.VISIBLE);

                btn_core.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btn_core.setTextColor(getResources().getColor(R.color.colorWhite));
            }
        });

        //Gettting  Date Listener On TextView to opne the Calender Intent
        ibtn_calender_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_start_date.setError(null);

            }
        });
        //Gettting  Date Listener On TextView to opne the Calender Intent
        edt_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_start_date.setError(null);
                DatePickerDialog dpd = new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT,userDateSetListener2,cmonth,cday,cyear);
               /* Calendar newCal= Calendar.getInstance();
                newCal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
                int cday1 = newCal.get(Calendar.DAY_OF_MONTH);
                int cmonth1 = newCal.get(Calendar.MONTH);
                int cyear1 = newCal.get(Calendar.YEAR);
                int hour1 = newCal.get(Calendar.HOUR);
                int min1 =newCal.get(Calendar.MINUTE);
                Log.d(Global.TAG, "NewCal Date: "+cday1+"-"+(cmonth1+1)+"-"+cyear1);*/
                dpd.getDatePicker().setMinDate(System.currentTimeMillis());
                dpd.show();

            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               
                if (view != null) {          //Check for is soft keyboard is open if open close it
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                if (Global.isNetworkAvailable(context)) {
                    flag = 1;
                    if(without_goalset) { //If Goal not set yet
                        if (edt_start_date.getText().toString().equals("")) {
                            edt_start_date.setError("Please Select Start Date");
                        } else {
                            setDataToDatabase();
                        }
                    }else{ //For changing the goal Not compulsory to set the date again
                        setDataToDatabase();
                    }
                } else {
                    Toast.makeText(context, "Please Check Your Internet Connection", Toast.LENGTH_LONG).show();
                }
            }
        });

        return layout;
    }
    public Dialog onCreateDialog(int id){
        if(id==102){
            DatePickerDialog dpd = new DatePickerDialog(getActivity(),userDateSetListener2,cmonth,cday,cyear);
            dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            dpd.setButton(DialogInterface.BUTTON_NEUTRAL, "Clear", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    edt_start_date.setText("");
                }
            });
            return  dpd;
        }
        return  null;
    }
    private void getModuleDetails() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("userId", spLib.getPref(SPLib.Key.USER_ID));
            paramObj.put("platform", "2");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        final Dialog myLoader = Global.showDialog(getActivity());
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        Log.d(Global.TAG, "getModuleDetails: "+paramObj.toString());
        APIService service = APIClient.getRetrofit().create(APIService.class);
        Call<GetCustomizeModuleList> call = service.getModuleDetails(paramObj.toString());
        call.enqueue(new Callback<GetCustomizeModuleList>() {
            @Override
            public void onResponse(Call<GetCustomizeModuleList> call, Response<GetCustomizeModuleList> response) {
                GetCustomizeModuleList getCustomizeModuleList=response.body();
                if (getCustomizeModuleList!=null){
                    if (getCustomizeModuleList.isSuccess()){
                        moduleList=getCustomizeModuleList.getResult();
                        Log.d(Global.TAG, "onResponse: Module List:"+moduleList.size());
                        moduleIds.clear();
                        for(int i=0;i<moduleList.size();i++){
                            moduleIds.add(moduleList.get(i).getModuleId());
                        }
                        Log.d(Global.TAG, "moduleIds: "+moduleIds.size());
                        spLib.saveArrayList(moduleList,SPLib.Key.MODULELIST);
                        //moduleList=spLib.getArrayList(SPLib.Key.MODULELIST);
                        // Log.d(Global.TAG, "ModuleList from spLib: "+moduleList.size());
                        if (!moduleIds.contains("2")){
                            openDialogDisplayAlert();
                        }
                    }else{
                        Toast.makeText(getActivity(), ""+getCustomizeModuleList.getResult(), Toast.LENGTH_SHORT).show();
                    }
                }

                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<GetCustomizeModuleList> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure:getModuleDetails: "+t);
            }
        });

    }


    public void openDialogDisplayAlert() {
        Button btn_upgrade_dissmiss,btn_upgrade_ok;
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.upgrade_package_alert_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Confirmation");

        btn_upgrade_dissmiss=(Button) dialog.findViewById(R.id.btn_upgrade_dissmiss);
      //  btn_upgrade_ok=(Button) dialog.findViewById(R.id.btn_upgrade_ok);

        btn_upgrade_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                dialog.dismiss();
            }
        });

      /*  btn_upgrade_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),SignUpActivity.class));
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                dialog.dismiss();
            }
        });*/

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }


    //Manual Date Picker Fuction
    private DatePickerDialog.OnDateSetListener userDateSetListener2 = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            String month = String.valueOf(monthOfYear+1);
            String selectedDay = String.valueOf(dayOfMonth);
            edt_start_date.setText(year+"-"+((month.length()   == 1 ? "0"+month.toString():month.toString()) )+"-"+((selectedDay.toString().length() == 1 ? "0"+selectedDay.toString():selectedDay.toString())));
           /* edt_start_date.setText(""+month+"-"+dayOfMonth+"-"+year);
            // First convert to Date. This is one of the many ways.
            String dateString = String.format("%d-%d-%d", month, dayOfMonth, year);
            Date date = null;
            try {
                date = new SimpleDateFormat("MM-DD-YY").parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // Then get the day of week from the Date based on specific locale.
            dayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date);*/
        }
    };

    private void setDataToDatabase() {
        goalEnteredCriticalArray.clear();
        goalEnteredCoreArray.clear();
        goalEnteredBaselineArray.clear();

      /*  if (without_goalset){
            Log.d(Global.TAG, "setDataToDatabase:adminGoalList: "+adminGoalList.size());
            for (int i=0;i<adminGoalList.size();i++){
                alladmingoals.add(adminGoalList.get(i).getZo_goal_id() + ":" + allEditTextEnteredGoal.get(i).getText().toString());
            }
            Log.d(Global.TAG, "setDataToDatabase:alladmingoals: "+alladmingoals.size());
           setUserGoalsOnBoard();
        }else{*/
            for(int i=0;i<adminGoalList.size();i++) {
                if (adminGoalList.get(i).getGoal_type_id().equals("1") && (allEditTextEnteredGoal.get(i).getText().toString().equals("") || allEditTextEnteredGoal.get(i).getText().toString().equals("0"))) {
                    flag = 0;//Set flag =0 if any of value for criticalSuccessArea goes zero or null
                    allEditTextEnteredGoal.get(i).setError(" Enter valid goal numbers. Exclusion for zero & empty textboxes.");
                    allEditTextEnteredGoal.get(i).requestFocus();
                    //    Toast.makeText(context, "You have set '0' goals for this task. Please update your goal count from Add/Edit goals", Toast.LENGTH_LONG).show();
                    functionForVisibleViewWithError();
                }
                if (adminGoalList.get(i).getGoal_type_id().equals("1")){
                    goalEnteredCriticalArray.add(adminGoalList.get(i).getZo_goal_id() + ":" + allEditTextEnteredGoal.get(i).getText().toString());
                }else if (adminGoalList.get(i).getGoal_type_id().equals("2")) {
                    goalEnteredCoreArray.add(adminGoalList.get(i).getZo_goal_id() + ":" + allEditTextEnteredGoal.get(i).getText().toString());
                }else if (adminGoalList.get(i).getGoal_type_id().equals("3")) {
                    goalEnteredBaselineArray.add(adminGoalList.get(i).getZo_goal_id() + ":" + allEditTextEnteredGoal.get(i).getText().toString());
                }
            }


            if (flag==1){
                if (without_goalset){
                    setUserGoalsOnBoard();
                }else{
                    if (!edt_start_date.getText().toString().equals("")){
                        new android.support.v7.app.AlertDialog.Builder(getActivity())
                                .setMessage("You are about to change your Business Start Date & all Program Goals details. Your all current & previous working details will be get vanished. Are you sure?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        updateUserGoals();
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                    }else{
                        updateUserGoals();
                    }

                }

            }

    }

    private void setUserGoalsOnBoard() {

        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        String listStringCritical = "",listStringCore="",listStringBaseline="";

        for (String s : goalEnteredCriticalArray)
        {
            listStringCritical += s + ",";
        }
        if (listStringCritical.endsWith(",")) {
            listStringCritical = listStringCritical.substring(0, listStringCritical.length() - 1);
        }

        for (String s : goalEnteredCoreArray)
        {
            listStringCore += s + ",";
        }
        if (listStringCore.endsWith(",")) {
            listStringCore = listStringCore.substring(0, listStringCore.length() - 1);
        }

        for (String s : goalEnteredBaselineArray)
        {
            listStringBaseline += s + ",";
        }
        if (listStringBaseline.endsWith(",")) {
            listStringBaseline = listStringBaseline.substring(0, listStringBaseline.length() - 1);
        }

/*

        for (String s : alladmingoals)
        {
            listAllAdminGoals += s + ",";
        }
        if (listAllAdminGoals.endsWith(",")) {
            listAllAdminGoals = listAllAdminGoals.substring(0, listAllAdminGoals.length() - 1);
        }
*/

        /*JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("start_date",edt_start_date.getText().toString());
            paramObj.put("user_id", user_id);
            paramObj.put("goalDetails", listAllAdminGoals);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(Global.TAG, "setUserGoals: "+e);
            e.printStackTrace();
        }
        Log.d(Global.TAG, "setUserGoals:param "+paramObj.toString());*/

       // String start_date1=edt_start_date.getText().toString();
        String dateString = String.format("%d-%d-%d", cyear, cmonth+1, cday);

        Log.d(Global.TAG, "setUserGoals:dateString "+dateString);
        Log.d(Global.TAG, "setUserGoals: listStringCritical: "+listStringCritical);
        Log.d(Global.TAG, "setUserGoals: listStringCore: "+listStringCore);
        Log.d(Global.TAG, "setUserGoals: listStringBaseline: "+listStringBaseline);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> callSet=service.setBusinessDateAndGoals(user_id,"2",dateString,listStringCritical,listStringCore,listStringBaseline);
        callSet.enqueue(new Callback<JsonResult>() {
            @Override
            public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                JsonResult jsonResult=response.body();
                if (jsonResult.isSuccess()){
                    spLib.sharedpreferences.edit().putString(SPLib.Key.BusinessStart, edt_start_date.getText().toString()).commit();
                    Toast.makeText(context, "New Program Goal Numbers Added Successfully..!", Toast.LENGTH_SHORT).show();
                   dashboardActivity.goalsetFlag=true;
                   dashboardActivity.replaceFragments(new DashboardFragment(true));
                }else{
                    Toast.makeText(context, "Error in Adding Goals..!", Toast.LENGTH_SHORT).show();
                }
                myLoader.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResult> call, Throwable t) {
                myLoader.dismiss();
                Log.d(Global.TAG, "onFailure setGoals: "+t);
            }
        });

    }

    private void updateUserGoals() {
        final Dialog myLoader = Global.showDialog(context);
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        String listStringCritical = "",listStringCore="",listStringBaseline="";

        for (String s : goalEnteredCriticalArray)
        {
            listStringCritical += s + ",";
        }
        if (listStringCritical.endsWith(",")) {
            listStringCritical = listStringCritical.substring(0, listStringCritical.length() - 1);
        }

        for (String s : goalEnteredCoreArray)
        {
            listStringCore += s + ",";
        }
        if (listStringCore.endsWith(",")) {
            listStringCore = listStringCore.substring(0, listStringCore.length() - 1);
        }

        for (String s : goalEnteredBaselineArray)
        {
            listStringBaseline += s + ",";
        }
        if (listStringBaseline.endsWith(",")) {
            listStringBaseline = listStringBaseline.substring(0, listStringBaseline.length() - 1);
        }
        String start_date="";
        if (!edt_start_date.getText().toString().equals("")){
            start_date=edt_start_date.getText().toString();
        }

        Log.d(Global.TAG, "setUserGoals:Start Date: "+start_date+" user_id"+user_id);
        Log.d(Global.TAG, "setUserGoals:listStringCritical: "+listStringCritical+" listStringCore: "+listStringCore+"listStringBaseline: "+listStringBaseline);

        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<JsonResult> callUpdate=service.updateBuinessDateAndGoals(user_id,start_date,listStringCritical,listStringCore,listStringBaseline);
        callUpdate.enqueue(new Callback<JsonResult>() {
               @Override
               public void onResponse(Call<JsonResult> call, Response<JsonResult> response) {
                   JsonResult jsonResult=response.body();
                   if (jsonResult.isSuccess()){
                       Toast.makeText(context, " New Program Goal Numbers Updated Successfully..!!", Toast.LENGTH_SHORT).show();
                       spLib.sharedpreferences.edit().putString(SPLib.Key.BusinessStart, edt_start_date.getText().toString()).commit();
                       dashboardActivity.replaceFragments(new DashboardFragment());
                   }else{
                       Toast.makeText(context, "Error in Updating Goals..!", Toast.LENGTH_SHORT).show();
                   }
                   myLoader.dismiss();
               }

               @Override
               public void onFailure(Call<JsonResult> call, Throwable t) {
                   myLoader.dismiss();
                   Log.d(Global.TAG, "onFailure UpdateGoals: "+t);
               }
           });

    }

    private void functionForVisibleViewWithError() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {          //Check for is soft keyboard is open if open close it
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        RoundRectShape rect = new RoundRectShape(
                new float[] {10,10, 0,0, 0,0,0,0},
                null,
                null);
        ShapeDrawable bg = new ShapeDrawable(rect);
        bg.getPaint().setColor(getResources().getColor(R.color.colorOragne));
        btn_critical.setBackgroundDrawable(bg);
        RoundRectShape rect1 = new RoundRectShape(
                new float[] {0,0,10,10,0,0,0,0},
                null,
                null);
        ShapeDrawable bg1 = new ShapeDrawable(rect1);
        bg1.getPaint().setColor(getResources().getColor(R.color.colorPrimary));
        btn_baseline.setBackgroundDrawable(bg1);

        critical_success_layout.setVisibility(View.VISIBLE);
        core_activity_layout.setVisibility(View.GONE);
        baseline_layout.setVisibility(View.GONE);

        btn_core.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        btn_core.setTextColor(getResources().getColor(R.color.colorWhite));
    }

    private void init() {
        context=getActivity();
        spLib=new SPLib(getActivity());
        user_id=spLib.getPref(SPLib.Key.USER_ID);
        adminGoalList =new ArrayList<>();

        goalEnteredCriticalArray= new ArrayList<>();
        goalEnteredCoreArray= new ArrayList<>();
        goalEnteredBaselineArray= new ArrayList<>();
        alladmingoals= new ArrayList<>();

        ll_critical=(LinearLayout)layout.findViewById(R.id.ll_critical);
        ll_baseline=(LinearLayout)layout.findViewById(R.id.ll_baseline);
        ll_core=(LinearLayout)layout.findViewById(R.id.ll_core);

        edt_start_date=(EditText) layout.findViewById(R.id.edt_start_date);
        linear_main=(LinearLayout) layout.findViewById(R.id.linear_main);
        tv_startdate=(TextView)layout.findViewById(R.id.tv_startdate);

        btn_critical=(Button)layout.findViewById(R.id.btn_critical);
        btn_core=(Button)layout.findViewById(R.id.btn_core);
        btn_baseline=(Button)layout.findViewById(R.id.btn_baseline);
        btn_save=(Button)layout.findViewById(R.id.btn_save);
        ibtn_calender_start_date =(ImageView) layout.findViewById(R.id.ibtn_calender_start_date);
        btn_core_next=(Button)layout.findViewById(R.id.btn_core_next);
        btn_baseline_prev=(Button)layout.findViewById(R.id.btn_baseline_prev);
        btn_critical_next=(Button)layout.findViewById(R.id.btn_critical_next);
        btn_core_prev =(Button)layout.findViewById(R.id.btn_core_prev );

        critical_success_layout=(LinearLayout)layout.findViewById(R.id.critical_success_layout);
        core_activity_layout=(LinearLayout)layout.findViewById(R.id.core_activity_layout);
        baseline_layout=(LinearLayout)layout.findViewById(R.id.baseline_layout);

        edt_start_date.setInputType(InputType.TYPE_NULL);
        dashboardActivity= (DashboardActivity) getActivity();
    }

    private void getAdminGoals() {
        Log.d(Global.TAG, "getAdminGoals: "+user_id);
        final Dialog myLoader = Global.showDialog(getActivity());
        myLoader.show();
        myLoader.setCanceledOnTouchOutside(true);
        APIService service= APIClient.getRetrofit().create(APIService.class);
        Call<AdminGoals> call=service.getAdminGoals(user_id);
        call.enqueue(new Callback<AdminGoals>() {
            @Override
            public void onResponse(Call<AdminGoals> call, Response<AdminGoals> response) {
                AdminGoals adminGoals=response.body();
                if (adminGoals.isSuccess()){
                    adminGoalList=adminGoals.getResult();
                    CreateGoalList();
                    if (without_goalset){
                        setDataToDatabase();
                    }

                }else{
                    Toast.makeText(context, "Error in getting Admin Goals..!", Toast.LENGTH_SHORT).show();
                }
                myLoader.dismiss();
            }
            @Override
            public void onFailure(Call<AdminGoals> call, Throwable t) {
                Log.d(Global.TAG, "onFailure:Admin Goals: "+t);
                myLoader.dismiss();
            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void CreateGoalList() {
        ll_critical.removeAllViews();
        int j=1;
        iv_hint =new  ImageView[adminGoalList.size()];
        for (int i = 0; i < adminGoalList.size(); i++)
        {
            ll_goal_data_hori = new LinearLayout(context);
            ll_goal_data_hori.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.FILL_PARENT));
            ll_goal_data_hori.setPadding(10,0,0,0);
            ll_goal_data_hori.setOrientation(LinearLayout.HORIZONTAL);

            TextView goal_name = new TextView(context);
            ViewGroup.LayoutParams params223 = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.8f);
            goal_name.setLayoutParams(params223);
            goal_name.setText(adminGoalList.get(i).getGoal_name());
            goal_name.setTextColor(Color.BLACK);
            goal_name.setGravity(Gravity.LEFT);
            goal_name.setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
            ll_goal_data_hori.addView(goal_name); // Adding textView to tablerow.

            TextView goal_name1 = new TextView(context);
            ViewGroup.LayoutParams params2244 = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.8f);
            goal_name1.setLayoutParams(params2244);
            goal_name1.setText(adminGoalList.get(i).getGoal_name());
            goal_name1.setTextColor(Color.BLACK);
            goal_name1.setGravity(Gravity.LEFT);
            goal_name1.setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
            ll_goal_data_hori.addView(goal_name1); // Adding textView to tablerow.

            TextView goal_name123 = new TextView(context);
            ViewGroup.LayoutParams params2222 = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.4f);
            goal_name123.setLayoutParams(params2222);
            goal_name123.setTextColor(Color.BLACK);
            goal_name123.setGravity(Gravity.LEFT);
            goal_name123.setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
            ll_goal_data_hori.addView(goal_name123); // Adding textView to tablerow.

            //Create an user editable goal dyanmically
            ll_edit_goal_data = new LinearLayout(context);
            ll_edit_goal_data.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.FILL_PARENT));
            ll_edit_goal_data.setPadding(20,20,20,20);
            ll_edit_goal_data.setOrientation(LinearLayout.HORIZONTAL);

            ll_edit_goal_data1 = new LinearLayout(context);
            LinearLayout.LayoutParams params22 =  new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.7f);
            ll_edit_goal_data1.setLayoutParams(params22);
            ll_edit_goal_data1.setOrientation(LinearLayout.HORIZONTAL);

            ll_edit_goal_data2 = new LinearLayout(context);
            LinearLayout.LayoutParams params_goal =  new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.7f);
            params_goal.setMargins(20,0,0,0);
            ll_edit_goal_data2.setLayoutParams(params_goal);
            ll_edit_goal_data2.setOrientation(LinearLayout.HORIZONTAL);

            ll_edit_goal_hint = new LinearLayout(context);
            LinearLayout.LayoutParams params_hint =  new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.6f);
            params_hint.setMargins(20,0,0,0);
            ll_edit_goal_hint.setLayoutParams(params_hint);
            ll_edit_goal_hint.setOrientation(LinearLayout.HORIZONTAL);

            EditText admin_edit_value = new EditText(context);
            admin_edit_value.setText(adminGoalList.get(i).getGoal_count_admin());
            admin_edit_value.setBackground(getResources().getDrawable(R.drawable.rounded_view));
            admin_edit_value.setTextColor(Color.BLACK);
            admin_edit_value.setFocusable(false);
            admin_edit_value.setInputType(InputType.TYPE_CLASS_NUMBER);
            admin_edit_value.setPadding(20,10,10,10);
            admin_edit_value.setGravity(Gravity.CENTER);
            admin_edit_value.setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
            admin_edit_value.setWidth(300);
            admin_edit_value.setFilters(new InputFilter[] {new InputFilter.LengthFilter(5)});
            ll_edit_goal_data1.addView(admin_edit_value);
            ll_edit_goal_data.addView(ll_edit_goal_data1);

            EditText user_edit_value = new EditText(context);
            user_edit_value.setId(i);
            allEditTextEnteredGoal.add(user_edit_value);
            if (without_goalset){ //If user come from on boarding flow it show the Admin goal for editable
                user_edit_value.setText(adminGoalList.get(i).getGoal_count_admin());
            }else {//else user set goal will be set to edit text
                user_edit_value.setText(adminGoalList.get(i).getGoal_count_user());
            }
            user_edit_value.setBackground(getResources().getDrawable(R.drawable.rounded_view));
            user_edit_value.setTextColor(Color.BLACK);
            user_edit_value.setInputType(InputType.TYPE_CLASS_NUMBER);
            user_edit_value.setPadding(20,10,10,10);
            user_edit_value.setGravity(Gravity.CENTER);
            user_edit_value.setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
            user_edit_value.setWidth(300);
            user_edit_value.setFilters(new InputFilter[] {new InputFilter.LengthFilter(5)});
            ll_edit_goal_data2.addView(user_edit_value);
            ll_edit_goal_data.addView(ll_edit_goal_data2);

            //ViewGroup.LayoutParams params11 = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            iv_hint[i]= new ImageView(new ContextThemeWrapper(context, R.style.AppTheme));
            iv_hint[i].setId(i);
            iv_hint[i].setTag("goalName"+i);
            iv_hint[i].setBackgroundDrawable(getResources().getDrawable(R.mipmap.hint_ic));

            ll_edit_goal_hint.addView(iv_hint[i]); // Adding textView to tablerow.
            ll_edit_goal_data.addView(ll_edit_goal_hint);


            //Added the custom layout to xml
            if (adminGoalList.get(i).getGoal_type_id().equals("1")) {
                ll_critical.addView(ll_goal_data_hori, new TableLayout.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT));

                ll_critical.addView(ll_edit_goal_data, new TableLayout.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT));
            }else if (adminGoalList.get(i).getGoal_type_id().equals("2")) {
                ll_core.addView(ll_goal_data_hori, new TableLayout.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT));

                ll_core.addView(ll_edit_goal_data, new TableLayout.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT));
            }else if (adminGoalList.get(i).getGoal_type_id().equals("3")) {
                ll_baseline.addView(ll_goal_data_hori, new TableLayout.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT));

                ll_baseline.addView(ll_edit_goal_data, new TableLayout.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT));
            }
            j++;
        }

        //Set and Give the listerner to the image view for the hint
        for (int i = 0; i < adminGoalList.size(); i++) {
            final int k = i;

            iv_hint[k].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SimpleTooltip.Builder(context)
                            .anchorView(v)
                            .text(adminGoalList.get(k).getHelp_tip())
                            .gravity(Gravity.TOP)
                            .textColor(getResources().getColor(R.color.colorBlack))
                            .backgroundColor(getResources().getColor(R.color.colorOffWhite))
                            .arrowColor(getResources().getColor(R.color.colorOffWhite))
                            .animated(true)
                            .build()
                            .show();
                }
            });


        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Add/Edit Goals");

    }

    @Override
    public void onPause() {
        if (without_goalset){
            setUserGoalsOnBoard();
        }
        super.onPause();
    }
}
