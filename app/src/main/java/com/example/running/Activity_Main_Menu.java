package com.example.running;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Activity_Main_Menu extends AppCompatActivity{

    private static final String TAG = "ViewLogger";

    private GraphView graph;
    private Button btnNewActivity;
    private Button btnManualActivity;
    private Button btnHistory;

    private TextView lblNumRuns;
    private TextView lblTotalDistance;
    private TextView lblAvgPace;
    private TextView lblTotalCalories;

    private EditText edtWeight;

    private LineGraphSeries<DataPoint> lineGraphSeries;
    private BarGraphSeries<DataPoint> barGraphSeries;
    private AllSportActivities allSportActivities = new AllSportActivities();

    private DecimalFormat df = new DecimalFormat("###.##");

    private Spinner spinner;
    private String spinnerChoice;

    private FirebaseDatabase database;
    private DatabaseReference myRefAll_Running;
    private SimpleDateFormat sdf = new SimpleDateFormat("M");
    private String[] month_letters = {"Je"," Feb "," Mar ","Apr ","May ","Jun ","Jul ","Aug ","Sep ","Oct ","N","Dec"};

    private boolean weightWarning = false;

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
        try{
            MySP.getInstance().putDouble(Keys.WEIGHT_KEY, Double.parseDouble(edtWeight.getText().toString()));
        }
        catch (Exception e){
            /*TODO - add alert when no weight is entered */
            MySP.getInstance().putDouble(Keys.WEIGHT_KEY, Keys.DEFAULT_DOUBLE_VALUE);
        }
        CaloriesCalculator.getInstance().setWeight(MySP.getInstance().getDouble(Keys.WEIGHT_KEY, Keys.DEFAULT_DOUBLE_VALUE));

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
        setWeightEditText(MySP.getInstance().getDouble(Keys.WEIGHT_KEY, Keys.DEFAULT_DOUBLE_VALUE));
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (!(activeNetworkInfo != null && activeNetworkInfo.isConnected())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Internet Connection Problem");
            builder.setMessage("There's seems to be a problem connecting to the WiFi / Mobile Data, as such some services won't work properly or at all");
            builder.setPositiveButton("I UNDERSTAND", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {}
            });
            AlertDialog alert = builder.create();
            alert.show();
        }


        getAllActivitiesFromFirebase();
    }




    @Override
    protected void onStop() {
        Log.d("ViewLogger", "MainMenu - onStop Invoked");
        super.onStop();

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

        setUpViews();
        setUpSpinner();
        database = FirebaseDatabase.getInstance();
        myRefAll_Running = database.getReference(Keys.FIREBASE_ALL_RUNNING);
        weightWarning = MySP.getInstance().getBoolean(Keys.WEIGHT_WARNING, Keys.DEFAULT_VALUE_WEIGHT_WARNING);
        btnNewActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weightAlertDialog();
                startActivity(new Intent(getApplicationContext(), Activity_New_Record.class));
            }
        });

        btnManualActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weightAlertDialog();
                startActivity(new Intent(getApplicationContext(), Activity_Add_Manually.class));
            }
        });


        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Activity_History.class);
                intent.putExtra(Keys.ALL_CARDIO_ACTIVITIES, allSportActivities);
                startActivity(intent);
            }
        });

    }

    private void setWeightEditText(double weight) {
        if (weight == Keys.DEFAULT_DOUBLE_VALUE){
            edtWeight.setHint("My Weight (kg)");
        }
        else{
            edtWeight.setText(weight + "");
        }
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
        btnHistory = findViewById(R.id.main_menu_LBL_history);
        lblNumRuns = findViewById(R.id.main_menu_LBL_num_runs);
        lblTotalDistance = findViewById(R.id.main_menu_LBL_total_distance);
        lblAvgPace = findViewById(R.id.main_menu_LBL_avg_pace);
        lblTotalCalories = findViewById(R.id.main_menu_LBL_total_calories);
        edtWeight = findViewById(R.id.main_menu_EDT_weight);
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
        double totalPace = Keys.DEFAULT_DOUBLE_VALUE, avgPace;
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

    private void updateTotalCalories() {
        ArrayList <CardioActivity> releventActivities = getCardioActivitiesBySpinnerChoice();
        double totalCaloriesBurned = 0;
        for (CardioActivity cardioActivity :releventActivities) {
            totalCaloriesBurned += cardioActivity.getCaloriesBurned();
        }
        lblTotalCalories.setText(totalCaloriesBurned+"");

    }

    private void updateAllTextViewsAtributes() {
        updateNumRuns();
        updatePace();
        updateTotalDistance();
        updateTotalCalories();
    }

    private void updateTextView(TextView tv, String update) {
        tv.setText(update);
    }

    private void weightAlertDialog() {

        /*if (CaloriesCalculator.getInstance().getWeight() == Keys.DEFAULT_DOUBLE_VALUE && !weightWarning){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Weight Value Detected");
            builder.setMessage("No weight value was detected, you won't be able to view how many calories you have burned.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setNegativeButton("Never Show Me This Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    weightWarning = true;
                }
            });
            AlertDialog alert = builder.create();
            alert.show();

        }*/
     
    }

    private void showGraph() {
        Log.d(TAG, "showGraph: " + allSportActivities);
        
        graph.removeAllSeries();

        HashMap<Integer, Double> monthDistanceMap = new HashMap<>();
        ArrayList <Integer> releventMonths = new ArrayList <>();
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

        DataPoint dp[] = new DataPoint[13];

        for (int i = 0 ; i < dp.length; i++) {
            dp[i] = new DataPoint(i, 0);
        }

        for (int month : releventMonths) {
            Log.d(TAG, "showGraph: " + month + " : " + monthDistanceMap.get(month));
            dp[month-1] = new DataPoint (month, monthDistanceMap.get(month));
        }
        barGraphSeries = new BarGraphSeries<DataPoint>(dp);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        String[] month_numbers = {"1","2","3","4","5","6","7","8","9","10","11","12"};
        barGraphSeries.setDataWidth(0.5);

        graph.addSeries(barGraphSeries);

        staticLabelsFormatter.setHorizontalLabels(month_letters);
        graph.getGridLabelRenderer().setNumHorizontalLabels(12);
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        graph.setTitle("Yearly Tracking on Activities");
        graph.setClickable(false);
        graph.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        graph.setVerticalScrollBarEnabled(false);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinX(1);
        graph.getViewport().setMaxX(12);
        graph.getViewport().setMaxY(100);
        graph.getViewport().setScrollableY(true);
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


