package com.example.running;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ikovac.timepickerwithseconds.MyTimePickerDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Manual extends AppCompatActivity {

    private Button btnAdd;
    private Button btnCancel;
    private EditText edtDistance;
    private EditText edtStartTime;
    private EditText edtEndTime;
    private EditText edtDate;
    private Calendar calendar;
    private DatePickerDialog dpd;
    private TimePickerDialog tpd;
    private MyTimePickerDialog mtpd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        setUpViews();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //complicatedTimeHandler();
                complicatedTimeHandler();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelManualActivity();
            }
        });

        edtDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                enterActivityDate(focus);
            }
        });

        edtStartTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
               // enterActivityTimeWithSeconds(view, focus);
               // enterActivityTime(view, focus);
                enterActivityTimeWithSeconds(view, focus);
            }
        });


        edtEndTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                //enterActivityTimeWithSeconds(view, focus);
                //enterActivityTime(view, focus);
                enterActivityTimeWithSeconds(view, focus);
            }
        });

    }

    private void simpleTimeHandler() {
        if (true) {
            String distance = edtDistance.getText().toString();
            String startTime[] = edtStartTime.getText().toString().split(":");
            String endTime[] = edtEndTime.getText().toString().split(":");



            Integer start = 60 * Integer.parseInt(startTime[1].toString()) + Integer.parseInt(startTime[0]);
            Integer end = 60 * Integer.parseInt(endTime[1].toString()) + Integer.parseInt(endTime[0]);
            Integer time = end - start;
            Integer hours = time / 3600;
            Integer minutes = (time / 60) % 60;
            Log.d("passs", "Activity Hours: " + hours);
            Log.d("passs", "Activity Minutes: " + minutes.toString());
            Log.d("passs", "AVG pace: " + Float.parseFloat(distance) / time);
        }
    }

    private void complicatedTimeHandler() {
        if (verifyEdtFields()) {
           /* String distance = edtDistance.getText().toString();
            String dateParts[] = edtDate.getText().toString().split("/");
            String startTime[] = edtStartTime.getText().toString().split(":");
            String endTime[] = edtEndTime.getText().toString().split(":");

            Long end = 3600 * Long.parseLong(endTime[0]) + 60 * Long.parseLong(endTime[1]) +  Long.parseLong(endTime[2]);
            Long start = 3600 * Long.parseLong(startTime[0]) + 60 * Long.parseLong(startTime[1]) + Long.parseLong(startTime[2]);
            Long duration = end - start;
            Long hours, minutes, seconds;
            hours = duration / 3600;
            minutes = (duration / 60) % 60;
            seconds = (duration % 60);
            Double avgPace = (Double.parseDouble(distance)/( (double)duration/3600)) % 100;


            Log.d("passs", distance);
            Log.d("passs", "End " + end.toString());
            Log.d("passs", "Start " + start.toString());
            Log.d("passs", "Duration " + duration.toString());
            Log.d("passs", "Activity Hours: " + hours );
            Log.d("passs", "Activity Minutes: " + minutes);
            Log.d("passs", "Activity Seconds: " + seconds);
            Log.d("passs", "Activity Duration: " + hours + ":" + minutes + ":" + seconds);
            Log.d("passs", "Activity Pace: " + avgPace);

            NewRunDetails newRunDetails = new NewRunDetails(avgPace, Double.parseDouble(distance), dateParts, duration);

            Intent intent = new Intent(getBaseContext(), MainMenu.class);

            intent.putExtra("manualKey", newRunDetails);

            startActivity(intent);*/

           RunDetails runDetails = new RunDetails(splitEditTextByString(edtEndTime, "/."),
                   splitEditTextByString(edtStartTime,":"),
                   splitEditTextByString(edtEndTime, ":"),
                   Double.parseDouble(edtDistance.getText().toString()));

           Log.d("Send", runDetails.toString());

            Intent intent = new Intent(getBaseContext(), MainMenu.class);

            intent.putExtra("manualKey", runDetails);

            startActivity(intent);

        }
    }

    private String[] splitEditTextByString(EditText et, String symbol){
        return et.getText().toString().split(symbol);
    }


    private void setUpViews() {


        btnAdd = findViewById(R.id.manual_BTN_add);
        btnCancel = findViewById(R.id.manual_BTN_cancel);
        edtDistance = findViewById(R.id.manual_EDT_distance);
        edtStartTime = findViewById(R.id.manual_EDT_start_time);
        edtEndTime = findViewById(R.id.manual_EDT_end_time);
        edtDate = findViewById(R.id.manual_EDT_date);
        calendar = Calendar.getInstance();

    }

    private void cancelManualActivity(){
        startActivity(new Intent(getApplicationContext(), MainMenu.class));
    }

    private void enterActivityDate(boolean focus){
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        if (focus) {
            dpd = new DatePickerDialog(Manual.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            edtDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        }
                    }, year, month, day);
            dpd.show();
        }
        else {
            dpd.hide();
        }

    }

    private void enterActivityTimeWithSeconds(View view, boolean focus){
        final EditText edt = (EditText) findViewById(view.getId());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);

        if (focus) {
            // time picker dialog
            mtpd = new MyTimePickerDialog(this, new MyTimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(com.ikovac.timepickerwithseconds.TimePicker view, int hourOfDay, int minute, int seconds) {
                    edt.setText(hourOfDay + ":" + minute + ":" + seconds);
                }

            },hour, minutes, seconds, true);
            mtpd.show();
        }
        else {
            mtpd.hide();
        }
    }

    private void enterActivityTime(View view, boolean focus){
        final EditText edt = (EditText) findViewById(view.getId());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);

        if (focus) {
            // time picker dialog
            tpd = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    edt.setText(hourOfDay + ":" + minute);
                }

            },hour, minutes, true);
            tpd.show();
        }
        else {
            tpd.hide();
        }
    }

    private boolean verifyEdtFields() {

        if (edtDate.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Date Field Is Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edtStartTime.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Start Time Field Is Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edtEndTime.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "End Time Field Is Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edtDistance.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Distance Field Is Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}