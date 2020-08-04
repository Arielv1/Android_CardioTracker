package com.example.running;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

public class NewRun extends AppCompatActivity {

    private Button new_run_BTN_start;
    private Button new_run_BTN_pause;
    private Button new_run_BTN_stop;
    private Chronometer new_run_TMR_chronometer;
    private long time_of_pause;
    private boolean running_chronometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_run);

        setUpViews();

        new_run_BTN_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start(view);
            }
        });

        new_run_BTN_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pause(view);
            }
        });

        new_run_BTN_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop(view);
            }
        });

    }



    private void start(View view) {
        if(!running_chronometer) {
            new_run_TMR_chronometer.setBase(SystemClock.elapsedRealtime() - time_of_pause);
            new_run_TMR_chronometer.start();
            running_chronometer = true;
        }
    }

    private void pause(View view) {

        if(running_chronometer) {
            time_of_pause = SystemClock.elapsedRealtime() - new_run_TMR_chronometer.getBase();
            new_run_TMR_chronometer.stop();
            running_chronometer = false;
        }
        else {
            new_run_TMR_chronometer.setBase(SystemClock.elapsedRealtime() - time_of_pause);
            new_run_TMR_chronometer.start();
            running_chronometer = true;
        }
    }

    private void stop(View view) {
        /*new_run_TMR_chronometer.setBase(SystemClock.elapsedRealtime());
        time_of_pause = 0;*/
        startActivity(new Intent(getApplicationContext(), MainMenu.class));

    }

    private void setUpViews() {
        new_run_BTN_start = findViewById(R.id.new_run_BTN_start);
        new_run_BTN_pause = findViewById(R.id.new_run_BTN_pause);
        new_run_BTN_stop = findViewById(R.id.new_run_BTN_stop);
        new_run_TMR_chronometer = findViewById(R.id.new_run_TMR_chronometer);
    }
}