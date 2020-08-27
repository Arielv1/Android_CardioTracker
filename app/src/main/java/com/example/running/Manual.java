package com.example.running;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ikovac.timepickerwithseconds.MyTimePickerDialog;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Manual extends AppCompatActivity {

    private MySP mySP;

    private Button btnConfirm;
    private Button btnAdd;
    private Button btnCancel;

    private EditText edtDistance;
    private EditText edtStartTime;
    private EditText edtEndTime;
    private EditText edtDate;

    private Calendar calendar;

    private DatePickerDialog datePickerDialog;
    private MyTimePickerDialog timePickerDialog;

    private TextView lblDuration;
    private TextView lblActualDuration;
    private TextView lblPace;
    private TextView lblActualPace;
    private TextView lblPaceKmh;

    private Double pace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        mySP = new MySP(this);

        setUpViews();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verifyEdtFields()){
                    showActualPaceDistance();
                    btnAdd.setVisibility(View.VISIBLE);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelManualActivity();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNewRunData();
            }
        });

        edtDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                enterDateHandler(focus);
                            }
        });

        edtStartTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                enterTimeHandler(view, focus);
                           }
        });


        edtEndTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                enterTimeHandler(view, focus);
                            }
        });

    }



    private String[] splitEditTextByString(EditText et, String symbol){
        return et.getText().toString().split(symbol);
    }


    private void setUpViews() {

        btnConfirm = findViewById(R.id.manual_BTN_confirm);
        btnCancel = findViewById(R.id.manual_BTN_cancel);
        btnAdd = findViewById(R.id.manual_BTN_add);
        edtDistance = findViewById(R.id.manual_EDT_distance);
        edtStartTime = findViewById(R.id.manual_EDT_start_time);
        edtEndTime = findViewById(R.id.manual_EDT_end_time);
        edtDate = findViewById(R.id.manual_EDT_date);
        calendar = Calendar.getInstance();
        lblDuration = findViewById(R.id.manual_LBL_duration);
        lblActualDuration = findViewById(R.id.manual_LBL_actual_duration);
        lblPace = findViewById(R.id.manual_LBL_pace);
        lblActualPace = findViewById(R.id.manual_LBL_actual_pace);
        lblPaceKmh = findViewById(R.id.manual_LBL_pace_kmh);

    }



    private void enterDateHandler(boolean focus){
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        if (focus) {
            datePickerDialog = new DatePickerDialog(Manual.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            edtDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        }
                    }, year, month, day);
            datePickerDialog.show();
        }
        else {
            datePickerDialog.hide();
        }

    }

    private void enterTimeHandler(View view, boolean focus){
        final EditText edt = (EditText) findViewById(view.getId());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);

        if (focus) {
            // time picker dialog
            timePickerDialog = new MyTimePickerDialog(this, new MyTimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(com.ikovac.timepickerwithseconds.TimePicker view, int hourOfDay, int minute, int seconds) {
                    edt.setText(hourOfDay + ":" + minute + ":" + seconds);
                }

            },hour, minutes, seconds, true);
            timePickerDialog.show();
        }
        else {
            timePickerDialog.hide();
        }

        if (lblPace.getVisibility() == View.VISIBLE){
            showActualPaceDistance();
        }
    }

    private boolean verifyEdtField(EditText edt){
        return edtDate.getText().toString().trim().length() == 0;
    }

    private boolean checkEdtField(EditText editText, String message) {
        if (editText.getText().toString().trim().length() == 0) {
            Toaster.getInstance().showToast(message);
            return false;
        }
        return true;
    }

    private boolean verifyEdtFields() {

        return checkEdtField(edtDate, "Date Field Is Empty") &&
                checkEdtField(edtStartTime, "Start Time Field Is Empty") &&
                checkEdtField(edtEndTime, "End Time Field Is Empty") &&
                checkEdtField(edtDistance, "Distance Field Is Empty");

    }

    public void showActualPaceDistance(){
        if (!verifyEdtField(edtDate) && !verifyEdtField(edtStartTime) && !verifyEdtField(edtEndTime) && !verifyEdtField(edtDistance)){

            lblDuration.setVisibility(View.VISIBLE);
            lblPace.setVisibility(View.VISIBLE);

            String[] startTime =  splitEditTextByString(edtStartTime,":");
            String[] endTime = splitEditTextByString(edtEndTime, ":");
            double distance = Double.parseDouble(edtDistance.getText().toString());


            lblActualDuration.setVisibility(View.VISIBLE);

            Long duration = getDuration(startTime, endTime, distance);
            setLblDuration(distance, duration);


            DecimalFormat df = new DecimalFormat("###.##");

            lblActualPace.setVisibility(View.VISIBLE);
            lblPaceKmh.setVisibility(View.VISIBLE);
            lblActualPace.setText(df.format(pace% 100));
        }
    }

    private Long getDuration(String[] startTime, String[] endTime, double distance) {
        Long end = 3600 * Long.parseLong(endTime[0]) + 60 * Long.parseLong(endTime[1]) +  Long.parseLong(endTime[2]);
        Long start = 3600 * Long.parseLong(startTime[0]) + 60 * Long.parseLong(startTime[1]) + Long.parseLong(startTime[2]);
        return end - start;
    }

    private void setLblDuration(double distance, long duration) {
        Long hours, minutes, seconds;
        hours = duration / 3600;
        minutes = (duration / 60) % 60;
        seconds = (duration % 60);
        pace = (distance/( (double)duration/3600)) ;

        String sSeconds = "", sMinutes, sHours;
        if (seconds < 10) {
            sSeconds = "0" + seconds;
        }
        else {
            sSeconds = seconds.toString();
        }

        if (minutes < 10) {
            sMinutes = "0" + minutes;

        }
        else {
            sMinutes = minutes.toString();
        }

        if(hours == 0) {
            lblActualDuration.setText(sMinutes +":" + sSeconds);
        }
        else {
            lblActualDuration.setText(hours+ ":" + sMinutes +":" + sSeconds);
        }
    }

    private void cancelManualActivity(){
        Gson gson = new Gson();
        mySP.putString(Keys.NEW_RUN_DATA_PACKAGE, "");
        finish();
    }

    private void sendNewRunData() {
        RunDetails runDetails = new RunDetails(edtDate.getText().toString().split("/"),
                lblActualDuration.getText().toString(),
                Double.parseDouble(edtDistance.getText().toString()),
                pace);

        Gson gson = new Gson();
        String json = gson.toJson(runDetails);
        Log.d("Gson", json);

        RunDetails newRunDetails = gson.fromJson(json, RunDetails.class);
        Log.d("Gson", newRunDetails.toString());

        mySP.putString(Keys.NEW_RUN_DATA_PACKAGE, json);
        finish();
    }

}