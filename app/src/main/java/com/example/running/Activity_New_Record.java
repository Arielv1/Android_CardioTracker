package com.example.running;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

public class Activity_New_Record extends AppCompatActivity {

    private Button btnStart;
    private Button btnPause;
    private Button btnStop;
    private Button btnConfirm;
    private Button btnCancel;
    private Chronometer tmrChronometer;
    private long timeOfPause;
    private boolean runningChronometer;
    private boolean stoppedChronometer;

    private long savedState;
    private String cardioActivityChoice;

    @Override
   protected void onSaveInstanceState(@NonNull Bundle outState) {
       super.onSaveInstanceState(outState);
       Log.d("ViewLogger", "NewRun - onSavedInstance " + tmrChronometer.getBase());

   }

   @Override
   protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
       super.onRestoreInstanceState(savedInstanceState);
       savedState = savedInstanceState.getLong("CHRONO_STATE");
       Log.d("ViewLogger", "NewRun - onRestoreInstanceState " + savedState);
       tmrChronometer.setBase(savedState);
       tmrChronometer.start();
   }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("ViewLogger", "NewRun - onPause Invoked");
    }

    @Override
    protected void onStart() {
        Log.d("ViewLogger", "NewRun - onStart Invoked");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d("ViewLogger", "NewRun - onResume Invoked");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d("ViewLogger", "NewRun - onStop Invoked");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.d("ViewLogger", "NewRun - onDestroy Invoked");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.d("ViewLogger", "NewRun - onDestroy Invoked");
        super.onDestroy();
    }

    int counter = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ViewLogger", "NewRun - onCreate Invoked");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_record);

        setUpViews();

        setUpFragments();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start(view);
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pause(view);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop(view);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Activity_Main_Menu.class));
            }
        });

        tmrChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                Toast.makeText(Activity_New_Record.this, "" + counter, Toast.LENGTH_SHORT).show();
                counter++;
            }
        });
    }




    private void start(View view) {

        if(!runningChronometer && !stoppedChronometer) {
            tmrChronometer.setBase(SystemClock.elapsedRealtime() - timeOfPause);
            tmrChronometer.start();
            runningChronometer = true;
        }
    }

    private void pause(View view) {

        if(runningChronometer && !stoppedChronometer) {
            timeOfPause = SystemClock.elapsedRealtime() - tmrChronometer.getBase();
            tmrChronometer.stop();
            runningChronometer = false;
        }
        else if (!stoppedChronometer){
            tmrChronometer.setBase(SystemClock.elapsedRealtime() - timeOfPause);
            tmrChronometer.start();
            runningChronometer = true;
        }
    }

    private void stop(View view) {

        tmrChronometer.stop();
        runningChronometer = false;
        stoppedChronometer = true;
        btnCancel.setVisibility(View.VISIBLE);
        btnConfirm.setVisibility(View.VISIBLE);

    }

    private void setUpViews() {
        btnStart = findViewById(R.id.new_run_BTN_start);
        btnPause = findViewById(R.id.new_run_BTN_pause);
        btnStop = findViewById(R.id.new_run_BTN_stop);
        tmrChronometer = findViewById(R.id.new_run_TMR_chronometer);
        btnCancel = findViewById(R.id.new_run_BTN_cancel);
        btnConfirm = findViewById(R.id.new_run_BTN_confirm);
    }

    private void setUpFragments() {

        Fragment_Radio_Buttons fragment_radio_buttons = Utils.getInstance().createFragmentRadioButtons(this, callback, R.id.new_run_LAY_radio_buttons, false);

    }

    Callback_RadioChoice callback = new Callback_RadioChoice() {
        @Override
        public void setRadioButtonChoice(String choice) {
            cardioActivityChoice = choice;

        }
    };

}