package com.example.running;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.text.DecimalFormat;

public class MainMenu extends AppCompatActivity {

    private GraphView gv;
    private Button btnNewActivity;
    private Button btnManualActivity;
    private TextView lblNumRuns;
    private TextView lblTotalDistance;
    private TextView lblAvgPace;
    private BarGraphSeries<DataPoint> bgs;
    private DataPoint dp[] = new DataPoint [12];;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        setUpViews();
        showGraph();

       /*Intent getterIntent = getIntent();
        NewRunDetails extras = (NewRunDetails)getterIntent.getParcelableExtra("manualKey");

        if (extras != null) {
            DecimalFormat df = new DecimalFormat("###.##");
            Log.d("MainMenu", extras.toString());

           blNumRuns.setText(Integer.parseInt(lblNumRuns.getText().toString()) + 1 + "");
            lblTotalDistance.setText(df.format(Double.parseDouble(lblTotalDistance.getText().toString()) + extras.getDistance()));
            lblAvgPace.setText(df.format((Double.parseDouble(lblAvgPace.getText().toString()) + extras.getPace()) / Integer.parseInt(lblNumRuns.getText().toString()) ));

            updateGraph(1, 20);
        }*/
        Intent getterIntent = getIntent();
        RunDetails extras = (RunDetails)getterIntent.getParcelableExtra("manualKey");

        if (extras != null) {
            DecimalFormat df = new DecimalFormat("###.##");
            Log.d("MainMenu", extras.toString());
            String[] startTime = extras.getStartTime();
            String[] endTime = extras.getEndTime();
            double distance = extras.getDistance();
            Long end = 3600 * Long.parseLong(endTime[0]) + 60 * Long.parseLong(endTime[1]) +  Long.parseLong(endTime[2]);
            Long start = 3600 * Long.parseLong(startTime[0]) + 60 * Long.parseLong(startTime[1]) + Long.parseLong(startTime[2]);
            Long duration = end - start;
            Long hours, minutes, seconds;
            hours = duration / 3600;
            minutes = (duration / 60) % 60;
            seconds = (duration % 60);
            Double avgPace = (distance/( (double)duration/3600)) % 100;


            Log.d("passs", distance+"");
            Log.d("passs", "End " + end.toString());
            Log.d("passs", "Start " + start.toString());
            Log.d("passs", "Duration " + duration.toString());
            Log.d("passs", "Activity Hours: " + hours );
            Log.d("passs", "Activity Minutes: " + minutes);
            Log.d("passs", "Activity Seconds: " + seconds);
            Log.d("passs", "Activity Duration: " + hours + ":" + minutes + ":" + seconds);
            Log.d("passs", "Activity Pace: " + avgPace);
            lblNumRuns.setText(Integer.parseInt(lblNumRuns.getText().toString()) + 1 + "");
            lblTotalDistance.setText(df.format(Double.parseDouble(lblTotalDistance.getText().toString()) + extras.getDistance()));
            lblAvgPace.setText(df.format((Double.parseDouble(lblAvgPace.getText().toString()) + avgPace) / Integer.parseInt(lblNumRuns.getText().toString()) ));

            updateGraph(1, 20);
        }

        btnNewActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NewRun.class));
            }
        });

        btnManualActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Manual.class));
            }
        });


    }

    private void updateGraph(int x, int y) {
        dp[x] = new DataPoint(x, y);
    }

    private void showGraph() {
        for (int i = 0; i < dp.length; i++){
            dp[i] = new DataPoint(i, i);
        }
        bgs = new BarGraphSeries<DataPoint>(dp);
        bgs.setSpacing(20);
        gv.addSeries(bgs);

    }

    private void setUpViews() {
        gv = findViewById(R.id.main_menu_graph);
        btnNewActivity = findViewById(R.id.main_menu_BTN_new_activity);
        btnManualActivity = findViewById(R.id.main_menu_BTN_manual_activity);
        lblNumRuns = findViewById(R.id.main_menu_LBL_num_runs);
        lblTotalDistance = findViewById(R.id.main_menu_LBL_total_distance);
        lblAvgPace = findViewById(R.id.main_menu_LBL_avg_pace);
    }

}
