package com.example.running;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.lang.reflect.Array;
import java.security.Key;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class MainMenu extends AppCompatActivity {

    private MySP mySP;

    private GraphView graph;
    private Button btnNewActivity;
    private Button btnManualActivity;

    private TextView lblNumRuns;
    private TextView lblTotalDistance;
    private TextView lblAvgPace;
    private TextView lblReset;

    private Double totalPace;
    private Double avgPace;
    private Integer numRuns;
    private Double totalDistance;

    private LineGraphSeries<DataPoint> lineGraphSeries;
    private BarGraphSeries<DataPoint> barGraphSeries;
    private Set<String> activitySet;

    private DecimalFormat df = new DecimalFormat("###.##");

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("ViewLogger", "MainMenu - onPause Invoked");

    }

    @Override
    protected void onResume() {
        Log.d("ViewLogger", "MainMenu - onResume Invoked");
        super.onResume();


    }

    @Override
    protected void onStart() {
        Log.d("ViewLogger", "MainMenu - onStart Invoked");
        super.onStart();

        Gson gson = new Gson();
        RunDetails runDetails = gson.fromJson(MySP.getInstance().getString(Keys.NEW_RUN_DATA_PACKAGE, ""), RunDetails.class);

        if(runDetails != null) {

            updateNumRuns();

            updateTotalDistance(runDetails.getDistance());

            updatePace(runDetails.getPace());

            addNewRunToLog(runDetails);

        }
        else {

            updateTextView(lblNumRuns, numRuns.toString());
            updateTextView(lblAvgPace, df.format(avgPace));
            updateTextView(lblTotalDistance, df.format(totalDistance));

        }

        if (!activitySet.isEmpty()) {
            updateGraph();
        }
        else {
            showInitialGraph();
        }


    }




    @Override
    protected void onStop() {
        Log.d("ViewLogger", "MainMenu - onStop Invoked");
        super.onStop();
        MySP.getInstance().putString(Keys.NEW_RUN_DATA_PACKAGE, "");
    }

    @Override
    protected void onDestroy() {
        Log.d("ViewLogger", "MainMenu - onDestroy Invoked");
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.d("ViewLogger", "MainMenu - onDestroy Invoked");
        super.onRestart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ViewLogger", "MainMenu - onCreate Invoked");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);


        MySP.getInstance().putString(Keys.NEW_RUN_DATA_PACKAGE, "");
        setUpViews();

        numRuns = MySP.getInstance().getInteger(Keys.NUM_OF_RUNS, Keys.DEFAULT_INT_VALUE);
        totalDistance = MySP.getInstance().getDouble(Keys.TOTAL_DISTANCE, Keys.DEFAULT_DOUBLE_VALUE);
        totalPace = MySP.getInstance().getDouble(Keys.TOTAL_PACE, Keys.DEFAULT_DOUBLE_VALUE);
        avgPace = MySP.getInstance().getDouble(Keys.AVERAGE_PACE, Keys.DEFAULT_DOUBLE_VALUE);
        activitySet = MySP.getInstance().getSetStrings(Keys.ALL_ACTIVITIES, Keys.DEFAULT_ALL_ACTIVITIES_VALUE);


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

        lblReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
                showInitialGraph();
            }
        });

    }

    private void reset() {

        numRuns = Keys.DEFAULT_INT_VALUE;
        totalPace = Keys.DEFAULT_DOUBLE_VALUE;
        totalDistance = Keys.DEFAULT_DOUBLE_VALUE;
        avgPace = Keys.DEFAULT_DOUBLE_VALUE;
        activitySet = Keys.DEFAULT_ALL_ACTIVITIES_VALUE;

        updateTextView(lblTotalDistance, Keys.LABEL_DEFAULT_VALUE);
        updateTextView(lblNumRuns, Keys.LABEL_DEFAULT_VALUE);
        updateTextView(lblAvgPace, Keys.LABEL_DEFAULT_VALUE);

        MySP.getInstance().putInteger(Keys.NUM_OF_RUNS, Keys.DEFAULT_INT_VALUE);
        MySP.getInstance().putDouble(Keys.AVERAGE_PACE, Keys.DEFAULT_DOUBLE_VALUE);
        MySP.getInstance().putDouble(Keys.TOTAL_PACE, Keys.DEFAULT_DOUBLE_VALUE);
        MySP.getInstance().putDouble(Keys.TOTAL_DISTANCE, Keys.DEFAULT_DOUBLE_VALUE);
        MySP.getInstance().putSetStrings(Keys.ALL_ACTIVITIES, Keys.DEFAULT_ALL_ACTIVITIES_VALUE);
    }

    private void setUpViews() {
        graph = findViewById(R.id.main_menu_graph);
        btnNewActivity = findViewById(R.id.main_menu_BTN_new_activity);
        btnManualActivity = findViewById(R.id.main_menu_BTN_manual_activity);
        lblNumRuns = findViewById(R.id.main_menu_LBL_num_runs);
        lblTotalDistance = findViewById(R.id.main_menu_LBL_total_distance);
        lblAvgPace = findViewById(R.id.main_menu_LBL_avg_pace);
        lblReset = findViewById(R.id.main_menu_LBL_reset);
    }

    private void updateNumRuns() {
        numRuns++;
        updateTextView(lblNumRuns, numRuns.toString());
        MySP.getInstance().putInteger(Keys.NUM_OF_RUNS, numRuns);
    }

    private void updateTotalDistance(double distance) {
        totalDistance += distance;
        updateTextView(lblTotalDistance, df.format(totalDistance));
        MySP.getInstance().putDouble(Keys.TOTAL_DISTANCE, totalDistance);
    }

    private void updatePace(double pace) {
        totalPace += pace;
        avgPace = totalPace / numRuns;
        updateTextView(lblAvgPace, df.format (avgPace));
        MySP.getInstance().putDouble(Keys.TOTAL_PACE, totalPace);
        MySP.getInstance().putDouble(Keys.AVERAGE_PACE, avgPace);

    }

    private void addNewRunToLog(RunDetails runDetails) {
        Gson gson = new Gson();
        activitySet.add(gson.toJson(runDetails));
        MySP.getInstance().putSetStrings(Keys.ALL_ACTIVITIES, activitySet);
    }

    private void updateTextView(TextView tv, String update) {
        tv.setText(update);
    }

    private void showInitialGraph() {
        graph.removeAllSeries();
    }

    private void accurateGraph(){
        DataPoint dp[] = new DataPoint [activitySet.size()];
        Gson gson = new Gson();
        ArrayList <RunDetails> allRuns = new ArrayList<RunDetails>();
        for (String json : activitySet) {
            RunDetails current = gson.fromJson(json, RunDetails.class);
            allRuns.add(current);
        }

       for (int i = 0; i < dp.length; i++) {
          dp[i] = new DataPoint(i, allRuns.get(i).getDistance());
       }

        barGraphSeries = new BarGraphSeries<>(dp);
        graph.addSeries(barGraphSeries);
    }

    private void updateGraph() {
        graph.removeAllSeries();

        ArrayList <RunDetails> allRuns = new ArrayList<RunDetails>();
        Gson gson = new Gson();

        HashMap<Integer, Double> monthDistanceMap = new HashMap<Integer, Double>();
        ArrayList <Integer> releventMonths = new ArrayList <Integer>();
        for (String json : activitySet) {
            RunDetails current = gson.fromJson(json, RunDetails.class);
            allRuns.add(current);

            int month = Integer.parseInt(current.getDate()[1]);
            if (monthDistanceMap.containsKey(month)){
                monthDistanceMap.put(month, monthDistanceMap.get(month) + current.getDistance());
            }
            else{
                monthDistanceMap.put(month, current.getDistance());
                releventMonths.add(month);
            }
        }

        DataPoint dp[] = new DataPoint [30];

        for (int i = 0 ; i < dp.length; i++) {
            dp[i] = new DataPoint(i, 0);
        }

        for (int month : releventMonths) {
            dp[month] = new DataPoint (month, monthDistanceMap.get(month));
        }

        barGraphSeries = new BarGraphSeries<DataPoint>(dp);
        barGraphSeries.setSpacing(20);
        barGraphSeries.setDrawValuesOnTop(true);
        barGraphSeries.setValuesOnTopSize(34);
        barGraphSeries.setValuesOnTopColor(Color.BLACK);
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        graph.addSeries(barGraphSeries);

    }
}

