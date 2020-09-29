package com.example.running;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.ikovac.timepickerwithseconds.MyTimePickerDialog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class Activity_Add_Manually extends AppCompatActivity implements Callback_RadioChoice {

    private static final String TAG = "ViewLogger";


    private Button btnAddSave;
    private Button btnCancel;

    private EditText edtDistance;
    private EditText edtStartTime;
    private EditText edtEndTime;
    private EditText edtDate;

    private Calendar calendar;

    private DatePickerDialog datePickerDialog;
    private MyTimePickerDialog timePickerDialog;

    private TextView lblDuration;
    private TextView lblPace;

    private Fragment_Radio_Buttons fragment_radio_buttons;

    private String cardioActivityChoice;
    private AllSportActivities allSportActivities;


    private FirebaseDatabase database;
    private DatabaseReference databaseReference;


    private boolean calledFromHistory = false;
    private CardioActivity theCurrentActivity;

    Callback_RadioChoice callback = new Callback_RadioChoice() {
        @Override
        public void setRadioButtonChoice(String radioChoiceValue) {
            cardioActivityChoice = radioChoiceValue;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmanually);

        setUpViews();
        setUpFragments();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(Keys.FIREBASE_ALL_RUNNING);

        theCurrentActivity = (CardioActivity)getIntent().getParcelableExtra(Keys.NEW_DATA_PACKAGE);

        getAllActivitiesFromFirebase();

        if (theCurrentActivity != null) {
            displayCardioActivityInFields(theCurrentActivity);
            btnAddSave.setText("save");
            calledFromHistory = true;
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelManualActivity();
            }
        });

        btnAddSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verifyEdtFields()) {
                    sendNewRunData();
                }
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

        edtDistance.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                setLblPace();
            }
        });
 }

    private void displayCardioActivityInFields(CardioActivity cardioActivity) {

        edtDate.setText(cardioActivity.getDate());
        edtDistance.setText(cardioActivity.getDistance() + "");
        edtStartTime.setText(cardioActivity.getTimeStart());
        edtEndTime.setText(cardioActivity.getTimeEnd());
        lblPace.setText(cardioActivity.getPace() + "");
        lblDuration.setText(cardioActivity.getDuration());
        cardioActivityChoice = cardioActivity.getCardioActivityType();
        Log.d(TAG, "displayCardioActivityInFields: before calling SetRadio Button with: "+cardioActivityChoice);
        setRadioButtonChoice(cardioActivityChoice);
    }

    private void setLblPace() {
        if (edtDistance.getText().length()!=0 && edtStartTime.getText().length()!=0 && edtEndTime.getText().length()!=0) {
            DecimalFormat df = new DecimalFormat("###.##");
            double distance = Double.parseDouble(edtDistance.getText().toString());
            double pace = (distance/( (double)calculateDuration()/3600)) ;
            lblPace.setText(df.format(pace% 100));
        }
    }

    private void setUpFragments() {
        fragment_radio_buttons = Utils.getInstance().createFragmentRadioButtons(this, callback, R.id.manual_fragment_radio_group, false);

    }

    private String[] splitEditTextByString(EditText et, String symbol){
        return et.getText().toString().split(symbol);
    }


    private void setUpViews() {

        btnCancel = findViewById(R.id.manual_BTN_cancel);
        btnAddSave = findViewById(R.id.manual_BTN_addsave_button);
        edtDistance = findViewById(R.id.manual_EDT_distance);
        edtStartTime = findViewById(R.id.manual_EDT_start_time);
        edtEndTime = findViewById(R.id.manual_EDT_end_time);
        edtDate = findViewById(R.id.manual_EDT_date);
        calendar = Calendar.getInstance();
        lblDuration = findViewById(R.id.manual_LBL_duration);
        lblPace = findViewById(R.id.manual_LBL_pace);
    }



    private void enterDateHandler(boolean focus){
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        if (focus) {
            datePickerDialog = new DatePickerDialog(Activity_Add_Manually.this,
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
                    updateLblDuration();
                    setLblPace();
                }

            },hour, minutes, seconds, true);
            timePickerDialog.show();
        }
        else {
            timePickerDialog.hide();

        }


    }

    private boolean checkEdtField(EditText editText, String message) {
        if (editText.getText().toString().trim().length() == 0) {
            Toaster.getInstance().showToast(message);
            return false;
        }
        return true;
    }

    private boolean checkRadioBox(String message){
        if (cardioActivityChoice == null || cardioActivityChoice == Utils.CardioActivityTypes.ALL) {
            Toaster.getInstance().showToast(message);
            return false;
        }
        return true;
    }

    private boolean verifyEdtFields() {

        return  checkRadioBox("Please Select An Activity Type")&&
                checkEdtField(edtDate, "Date Field Is Empty") &&
                checkEdtField(edtStartTime, "Start Time Field Is Empty") &&
                checkEdtField(edtEndTime, "End Time Field Is Empty") &&
                checkEdtField(edtDistance, "Distance Field Is Empty");

    }


    private Long calculateDuration() {
        /*
        TODO - implement possibility of endTime < startTime
         */
        String[] startTime =  splitEditTextByString(edtStartTime,":");
        String[] endTime = splitEditTextByString(edtEndTime, ":");
        Long end = 3600 * Long.parseLong(endTime[0]) + 60 * Long.parseLong(endTime[1]) +  Long.parseLong(endTime[2]);
        Long start = 3600 * Long.parseLong(startTime[0]) + 60 * Long.parseLong(startTime[1]) + Long.parseLong(startTime[2]);
        return end - start;
    }

    private void setLblDuration(long duration) {
        Long hours, minutes, seconds;
        hours = duration / 3600;
        minutes = (duration / 60) % 60;
        seconds = (duration % 60);

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
            lblDuration.setText(sMinutes +":" + sSeconds);
        }
        else {
            lblDuration.setText(hours+ ":" + sMinutes +":" + sSeconds);
        }
    }

    private void updateLblDuration() {
        if (edtStartTime.getText().length() != 0 && edtEndTime.getText().length() != 0){
            setLblDuration(calculateDuration());
        }

    }

    private void cancelManualActivity(){
        finish();
    }

    private void sendNewRunData() {
        setLblPace();
        String sTime = edtStartTime.getText().toString();
        String eTime = edtEndTime.getText().toString();

        if (calledFromHistory) {
            theCurrentActivity.setDate(edtDate.getText().toString());
            theCurrentActivity.setDuration(lblDuration.getText().toString());
            theCurrentActivity.setDistance(Double.parseDouble(edtDistance.getText().toString()));
            theCurrentActivity.setPace(Double.parseDouble(lblPace.getText().toString()));
            theCurrentActivity.setCardioActivityType(cardioActivityChoice);
            theCurrentActivity.setTimeStart(sTime);
            theCurrentActivity.setTimeEnd(eTime);

            Gson gson = new Gson();
            String json = gson.toJson(theCurrentActivity);
            Log.d(TAG, "sending to history\n" + json);
            MySP.getInstance().putString(Keys.NEW_DATA_PACKAGE, json);
        }
        else{
             theCurrentActivity = new CardioActivity(
                     edtDate.getText().toString(),
                     lblDuration.getText().toString(),
                    Double.parseDouble(edtDistance.getText().toString()),
                    Double.parseDouble(lblPace.getText().toString()),
                    new Date().getTime(),
                    cardioActivityChoice,
                    sTime,
                    eTime);

            if(allSportActivities == null) {
                allSportActivities = new AllSportActivities(new ArrayList<CardioActivity>());
            }
            ArrayList<CardioActivity> recordedActivities = allSportActivities.getActivities();
            recordedActivities.add(theCurrentActivity);
            Collections.sort(recordedActivities);

            databaseReference.setValue(allSportActivities);
        }

        finish();
    }

    private void getAllActivitiesFromFirebase() {

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Log.w(TAG, "onDataChange Called !!!");
                allSportActivities = dataSnapshot.getValue(AllSportActivities.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        };
        databaseReference.addListenerForSingleValueEvent(postListener);
    }

    @Override
    public void setRadioButtonChoice(String radioChoiceValue) {
        Log.d(TAG, "setRadioButtonChoice called with:" +radioChoiceValue);
    }
}