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
import java.util.Calendar;
import java.util.Date;

public class Activity_Add_Manually extends AppCompatActivity implements Callback_RadioChoice {

    private static final String TAG = "Activity_Add_Manually";

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
    private TextView lblCalories;

    private long duration = 0;
    private double distance = 0;
    private double pace = 0;
    private double caloriesBurned = 0;

    private Fragment_Radio_Buttons fragment_radio_buttons;

    private String cardioActivityChoice;
    private AllSportActivities allSportActivities;


    private FirebaseDatabase database;
    private DatabaseReference databaseReference;


    private boolean calledFromHistory = false;
    private CardioActivity theCurrentActivity;

    private DecimalFormat df = new DecimalFormat("###.##");

    private Callback_RadioChoice callback = new Callback_RadioChoice() {
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

        /*
        * This activity is accessed by both Main Menu and History
        *  If it's history need to pass a CardioActivity back (not creating a new one)
        * */
        theCurrentActivity = (CardioActivity)getIntent().getParcelableExtra(Keys.NEW_DATA_PACKAGE);

        getAllActivitiesFromFirebase();


        /* if not null -> got here from History */
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
                if (verifyInputFields()) {
                    setLblDuration();
                    setLblPaceCaloriesAndDistance();
                    sendNewRunData();
                    finish();
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
                setLblDuration();
                setLblPaceCaloriesAndDistance();

                           }
        });

        edtEndTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                enterTimeHandler(view, focus);
                setLblDuration();
                setLblPaceCaloriesAndDistance();
            }
        });

        edtDistance.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                setLblPaceCaloriesAndDistance();
            }
        });
 }

    private void displayCardioActivityInFields(CardioActivity cardioActivity) {

        edtDate.setText(cardioActivity.getDate());
        edtDistance.setText(df.format(cardioActivity.getDistance()));

        /* TODO - disable keyboard for date, start and end time */
        edtStartTime.setText(cardioActivity.getTimeStart());
        edtEndTime.setText(cardioActivity.getTimeEnd());
        lblPace.setText(df.format(cardioActivity.getPace()));
        lblDuration.setText(cardioActivity.getDuration());
        lblCalories.setText(df.format(cardioActivity.getCaloriesBurned()));
        cardioActivityChoice = cardioActivity.getCardioActivityType();
        Log.d(TAG, "displayCardioActivityInFields: before calling SetRadio Button with: "+cardioActivityChoice);
        setRadioButtonChoice(cardioActivityChoice);
    }

    private void setLblDuration() {
        if (edtStartTime.getText().length() != 0 && edtEndTime.getText().length() != 0){
            duration = Utils.getInstance().calculateTimeDifference(edtStartTime.getText().toString(), edtEndTime.getText().toString());
            lblDuration.setText(Utils.getInstance().formatTimeToString(duration));
        }
    }

    private void setLblPaceCaloriesAndDistance() {
        if (edtDistance.getText().length()!=0 && edtStartTime.getText().length()!=0 && edtEndTime.getText().length()!=0) {
            distance = Double.parseDouble(edtDistance.getText().toString());
            pace = Utils.getInstance().calculatePaceFromDistanceAndSeconds(distance, Utils.getInstance().calculateTimeDifference(edtStartTime.getText().toString(), edtEndTime.getText().toString()));
            caloriesBurned = CaloriesCalculator.getInstance().calculateBurnedCalories(pace, duration);
            lblPace.setText(df.format(pace % 100));
            lblCalories.setText(df.format(caloriesBurned));

        }
    }

    private void setUpFragments() {
        fragment_radio_buttons = Utils.getInstance().createFragmentRadioButtons(this, callback, R.id.manual_fragment_radio_group, false, Keys.RADIO_HISTORY_CHOICE_EDIT);

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
        lblCalories = findViewById(R.id.manual_LBL_calories);
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

    private boolean verifyInputFields() {

        return  checkRadioBox("Please Select An Activity Type")&&
                checkEdtField(edtDate, "Date Field Is Empty") &&
                checkEdtField(edtStartTime, "Start Time Field Is Empty") &&
                checkEdtField(edtEndTime, "End Time Field Is Empty") &&
                checkEdtField(edtDistance, "Distance Field Is Empty");
    }

    private void cancelManualActivity(){
        finish();
    }

    private void sendNewRunData() {
        String sTime = edtStartTime.getText().toString();
        String eTime = edtEndTime.getText().toString();


        /*
        * If calledFromHistory is True, then the instance of theCardioActivity is not null, thus we only need to edit its attributes
        * Otherwise, the call was from Main Menu, so we need to create a new CardioActivity instance
        * */
        if (calledFromHistory) {
            theCurrentActivity.setDate(edtDate.getText().toString());
            theCurrentActivity.setDuration(lblDuration.getText().toString());
            theCurrentActivity.setDistance(distance);
            theCurrentActivity.setPace(pace);
            theCurrentActivity.setCaloriesBurned(caloriesBurned);
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
                    distance,
                    pace,
                    caloriesBurned,
                    new Date().getTime(),
                    cardioActivityChoice,
                    sTime,
                    eTime);

            allSportActivities = Utils.getInstance().addNewCardioActivityDatabase(allSportActivities, theCurrentActivity);

            databaseReference.setValue(allSportActivities);
        }
        MySP.getInstance().putString(Keys.RADIO_HISTORY_CHOICE_EDIT, Keys.DEFAULT_RADIO_BUTTONS_HISTORY_VALUE);
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