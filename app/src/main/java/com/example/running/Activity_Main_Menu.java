package com.example.running;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.security.Key;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Activity_Main_Menu extends AppCompatActivity{

    private static final String TAG = "ViewLogger";

    private GraphView graph;
    private Button btnNewActivity;
    private Button btnManualActivity;

    private TextView lblNumRuns;
    private TextView lblTotalDistance;
    private TextView lblAvgPace;
    private TextView lblReset;
    private TextView lblHistory;

    private LineGraphSeries<DataPoint> lineGraphSeries;
    private BarGraphSeries<DataPoint> barGraphSeries;
    private AllSportActivities allSportActivities = new AllSportActivities();

    private DecimalFormat df = new DecimalFormat("###.##");

    private Spinner spinner;
    private String spinnerChoice;

    FirebaseDatabase database;
    DatabaseReference myRefAll_Running;

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

        /*allSportActivities = Utils.getInstance().getAllCardioSportActivitiesFromSP();
        updateAllTextViewsAtributes();
        showGraph();*/

        getAllActivitiesFromFirebase();

    }




    @Override
    protected void onStop() {
        Log.d("ViewLogger", "MainMenu - onStop Invoked");
        super.onStop();
       //MySP.getInstance().putString(Keys.NEW_DATA_PACKAGE, Keys.DEFAULT_NEW_DATA_PACKAGE_VALUE);
        MySP.getInstance().putString(Keys.SPINNER_CHOICE, Keys.DEFAULT_SPINNER_CHOICE_VALUE);

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
        setContentView(R.layout.activity_mainmenu);

        database = FirebaseDatabase.getInstance();
        myRefAll_Running = database.getReference(Keys.FIREBASE_ALL_RUNNING);

        Log.d(TAG, "onCreate: from MySP " + Utils.getInstance().getAllCardioSportActivitiesFromSP());

        setUpViews();
        setUpSpinner();


        btnNewActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(), Activity_New_Record.class));
                startActivity(new Intent(getApplicationContext(), MapActivity.class));
            }
        });

        btnManualActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Activity_Add_Manually.class));
            }
        });

        lblReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
                showGraph();
            }
        });

        lblHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Activity_History.class);
                intent.putExtra(Keys.ALL_CARDIO_ACTIVITIES, allSportActivities);
                startActivity(intent);
            }
        });
    }

    private void setUpSpinner() {
        spinnerChoice = MySP.getInstance().getString(Keys.SPINNER_CHOICE, Keys.DEFAULT_SPINNER_CHOICE_VALUE);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.cardio_activity_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerChoice = (parent.getItemAtPosition(position).toString());
                if (allSportActivities != null) {
                    updateAllTextViewsAtributes();
                    showGraph();
                }

                MySP.getInstance().putString(Keys.SPINNER_CHOICE, spinnerChoice);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        spinner.setSelection(Utils.getInstance().getCardioActivityPositionIndexInSpinner(spinnerChoice));
    }

    private void reset() {

        allSportActivities  = new AllSportActivities();


        myRefAll_Running.setValue(allSportActivities);


        spinnerChoice = Keys.DEFAULT_SPINNER_CHOICE_VALUE;
        spinner.setSelection(0);

        updateTextView(lblTotalDistance, Keys.LABEL_DEFAULT_VALUE);
        updateTextView(lblNumRuns, Keys.LABEL_DEFAULT_VALUE);
        updateTextView(lblAvgPace, Keys.LABEL_DEFAULT_VALUE);

        MySP.getInstance().putString(Keys.ALL_CARDIO_ACTIVITIES, Keys.DEFAULT_ALL_CARDIO_ACTIVITIES_VALUE);
        MySP.getInstance().putString(Keys.SPINNER_CHOICE, Keys.DEFAULT_SPINNER_CHOICE_VALUE);

        /*
        TODO - move this to history
         */
        MySP.getInstance().putString(Keys.RADIO_HISTORY_CHOICE, Keys.DEFAULT_RADIO_BUTTONS_HISTORY_VALUE);
    }

    private void setUpViews() {
        graph = findViewById(R.id.main_menu_graph);
        btnNewActivity = findViewById(R.id.main_menu_BTN_new_activity);
        btnManualActivity = findViewById(R.id.main_menu_BTN_manual_activity);
        lblNumRuns = findViewById(R.id.main_menu_LBL_num_runs);
        lblTotalDistance = findViewById(R.id.main_menu_LBL_total_distance);
        lblAvgPace = findViewById(R.id.main_menu_LBL_avg_pace);
        lblReset = findViewById(R.id.main_menu_LBL_reset);
        lblHistory = findViewById(R.id.main_menu_LBL_history);
        spinner = findViewById(R.id.main_menu_spinner);
    }

    private void updateNumRuns() {
        ArrayList <CardioActivity> releventActivities = getCardioActivitiesBySpinnerChoice();
        updateTextView(lblNumRuns, releventActivities.size()+"");
    }

    private void updateTotalDistance() {
        double totalDistance = Keys.DEFAULT_DOUBLE_VALUE;
        ArrayList <CardioActivity> releventActivities = getCardioActivitiesBySpinnerChoice();

        for (CardioActivity cardioActivity :releventActivities) {
            totalDistance += cardioActivity.getDistance();
        }

        updateTextView(lblTotalDistance, df.format(totalDistance));
    }

    private void updatePace() {
        double totalPace = Keys.DEFAULT_DOUBLE_VALUE, avgPace = Keys.DEFAULT_DOUBLE_VALUE;
        ArrayList <CardioActivity> releventActivities = getCardioActivitiesBySpinnerChoice();
        for (CardioActivity cardioActivity :releventActivities) {
            totalPace += cardioActivity.getPace();
        }

        if (releventActivities.size() != 0) {
            avgPace = totalPace/releventActivities.size();
        }
        else {
            avgPace = Keys.DEFAULT_DOUBLE_VALUE;
        }
        updateTextView(lblAvgPace, df.format (avgPace));
    }

    private void updateAllTextViewsAtributes() {
        updateNumRuns();
        updatePace();
        updateTotalDistance();
    }

    private void updateTextView(TextView tv, String update) {
        tv.setText(update);
    }

    private void showGraph() {
        graph.removeAllSeries();

        HashMap<Integer, Double> monthDistanceMap = new HashMap<Integer, Double>();
        ArrayList <Integer> releventMonths = new ArrayList <Integer>();
        ArrayList <CardioActivity> releventActivities = getCardioActivitiesBySpinnerChoice();

        if(releventActivities == null || allSportActivities == null || allSportActivities.getActivities().size() == 0)
            return;

        for (CardioActivity current : releventActivities) {

            String[] date = current.getDate().split("/");

            int month = Integer.parseInt(date[1]);

            if (monthDistanceMap.containsKey(month)){
                monthDistanceMap.put(month, monthDistanceMap.get(month) + current.getDistance());
            }
            else{
                monthDistanceMap.put(month, current.getDistance());
                releventMonths.add(month);
            }
        }

        DataPoint dp[] = new DataPoint[200];

        for (int i = 0 ; i < dp.length; i++) {
            dp[i] = new DataPoint(i, 0);
        }

        for (int month : releventMonths) {
            dp[month] = new DataPoint (month, monthDistanceMap.get(month));
        }

        barGraphSeries = new BarGraphSeries<DataPoint>(dp);
//        barGraphSeries.setDrawValuesOnTop(true);
//        barGraphSeries.setValuesOnTopSize(34);
//        barGraphSeries.setValuesOnTopColor(Color.BLACK);
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
//        barGraphSeries.setSpacing(30);
//        graph.addSeries(barGraphSeries);
        graph.addSeries(barGraphSeries);

//        graph.setHorizontalScrollBarEnabled(true);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(30);

//        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalable(true);
//        graph.getViewport().setScrollableY(true);
    }

    private ArrayList <CardioActivity> getCardioActivitiesBySpinnerChoice() {
        return Utils.getInstance().filterCardioActivitiesByType(allSportActivities.getActivities(), spinnerChoice);
    }

    private void getAllActivitiesFromFirebase() {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                allSportActivities = dataSnapshot.getValue(AllSportActivities.class);

                Utils.getInstance().putAllCardioSportActivitiesInSP(allSportActivities);

                Log.d(TAG, "onDataChange: " + allSportActivities);
                if(allSportActivities != null){
                    updateAllTextViewsAtributes();
                    showGraph();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        };
        myRefAll_Running.addListenerForSingleValueEvent(postListener);
        Log.d(TAG, "after getAllActivitiesFromFirebase() " + allSportActivities);
    }
}


