package com.example.cardiotracker;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Activity_Main_Menu extends AppCompatActivity{

    private static final String TAG = "Activity_Main_Menu";

    private GraphView graph;
    private Button btnNewActivity;
    private Button btnManualActivity;
    private Button btnHistory;

    private TextView lblNumRuns;
    private TextView lblTotalDistance;
    private TextView lblAvgPace;
    private TextView lblTotalCalories;

    private EditText edtWeight;

    private BarGraphSeries<DataPoint> barGraphSeries;
    private AllSportActivities allSportActivities = new AllSportActivities();

    private DecimalFormat df = new DecimalFormat("###.##");

    private Spinner spinner;
    private String spinnerChoice;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private String[] month_letters = {"Ja"," Feb "," Mar ","Apr ","May ","Jun ","Jul ","Aug ","Sep ","Oc ","N","Dec"};

    private ProgressBar progressBar;

    private LinearLayout mainMenuLayout;


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        try{
            MySP.getInstance().putDouble(Keys.WEIGHT_KEY, Double.parseDouble(edtWeight.getText().toString()));
        }
        catch (Exception e){
            MySP.getInstance().putDouble(Keys.WEIGHT_KEY, Keys.DEFAULT_DOUBLE_VALUE);
            weightAlert();
        }
        CaloriesCalculator.getInstance().setWeight(MySP.getInstance().getDouble(Keys.WEIGHT_KEY, Keys.DEFAULT_DOUBLE_VALUE));

    }


    @Override
    protected void onStart() {
        Log.d(TAG , "onStart Invoked");
        super.onStart();

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean b = cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        
        if (!(activeNetworkInfo != null && activeNetworkInfo.isConnected())) {
            
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Internet Connection Problem");
            builder.setMessage("There's seems to be a problem connecting to the WiFi / Mobile Data, as such some services won't work properly or at all");
            builder.setPositiveButton("I UNDERSTAND", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    mainMenuLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }

        /* TextFields and Graph are updated here */
        getAllActivitiesFromFirebase();

    }




    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
        mainMenuLayout.setVisibility(View.INVISIBLE);
        edtWeight.clearFocus();
        MySP.getInstance().putString(Keys.SPINNER_CHOICE, Keys.DEFAULT_SPINNER_CHOICE_VALUE);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);


        setUpViews();
        setUpSpinner();
        setWeightEditText(MySP.getInstance().getDouble(Keys.WEIGHT_KEY, Keys.DEFAULT_DOUBLE_VALUE));

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(Keys.FIREBASE_ALL_RUNNING);

        /* Each of the activities can modify the CardioActivity list, so we pass allSportsActivity
        * so we won't have to connect to firebase from each activity
        *  */
        btnNewActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Activity_New_Record.class);
                intent.putExtra(Keys.ALL_CARDIO_ACTIVITIES, allSportActivities);
                startActivity(intent);

            }
        });

        btnManualActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Activity_Add_Manually.class);
                intent.putExtra(Keys.ALL_CARDIO_ACTIVITIES, allSportActivities);
                startActivity(intent);
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
        progressBar = findViewById(R.id.main_menu_PB_progress_bar);
        mainMenuLayout = findViewById(R.id.main_menu_LAY_all);
    }



    private void setWeightEditText(double weight) {
        Log.d(TAG, "setWeightEditText: " + MySP.getInstance().getDouble(Keys.WEIGHT_KEY, Keys.DEFAULT_DOUBLE_VALUE));
        if (weight == Keys.DEFAULT_DOUBLE_VALUE){
            edtWeight.setHint("My Weight (kg)");
            edtWeight.setText("");
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
                updateAllTextViewsAttributes();
                showGraph();
                MySP.getInstance().putString(Keys.SPINNER_CHOICE, spinnerChoice);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        spinner.setSelection(getCardioActivityPositionIndexInSpinner(spinnerChoice));


    }

    public int getCardioActivityPositionIndexInSpinner(String cardioActivityName) {
        if (cardioActivityName.equals(Utils.CardioActivityTypes.ALL)){
            return Utils.SpinnerValues.ALL_INDEX;
        }
        else  if (cardioActivityName.equals(Utils.CardioActivityTypes.JOGGING)){
            return Utils.SpinnerValues.JOGGING_INDEX;
        }
        else  if (cardioActivityName.equals(Utils.CardioActivityTypes.RUNNING)){
            return Utils.SpinnerValues.RUNNING_INDEX;
        }
        return Utils.SpinnerValues.CYCLING_INDEX;
    }

    private void updateLblNumRuns() {
        ArrayList <CardioActivity> releventActivities = getCardioActivitiesBySpinnerChoice();
        updateTextView(lblNumRuns, releventActivities.size()+"");
    }

    private void updateLblTotalDistance() {
        double totalDistance = Keys.DEFAULT_DOUBLE_VALUE;
        ArrayList <CardioActivity> releventActivities = getCardioActivitiesBySpinnerChoice();

        for (CardioActivity cardioActivity :releventActivities) {
            totalDistance += cardioActivity.getDistance();
        }

        updateTextView(lblTotalDistance, df.format(totalDistance));
    }

    private void updateLblPace() {
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

    private void updateLblTotalCalories() {
        ArrayList <CardioActivity> releventActivities = getCardioActivitiesBySpinnerChoice();
        double totalCaloriesBurned = 0;
        for (CardioActivity cardioActivity :releventActivities) {
            totalCaloriesBurned += cardioActivity.getCaloriesBurned();
        }
        lblTotalCalories.setText(df.format(totalCaloriesBurned));

    }

    private void updateAllTextViewsAttributes() {

        if (allSportActivities == null) {
            allSportActivities = new AllSportActivities(new ArrayList<CardioActivity>());
        }
        updateLblNumRuns();
        updateLblPace();
        updateLblTotalDistance();
        updateLblTotalCalories();
    }

    private void updateTextView(TextView tv, String update) {
        tv.setText(update);
    }

    private void getAllActivitiesFromFirebase() {
        progressBar.setVisibility(View.VISIBLE);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allSportActivities = dataSnapshot.getValue(AllSportActivities.class);
                Log.d(TAG, "onDataChange: " + allSportActivities);
                mainMenuLayout.setVisibility(View.VISIBLE);
                updateAllTextViewsAttributes();
                showGraph();
                progressBar.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        };
        databaseReference.addListenerForSingleValueEvent(postListener);
    }
    private void weightAlert() {
        Toaster.getInstance().showToast("No Weight Detected - Cannot Calculate Burnt Calories");
    }

    private void showGraph() {

        graph.removeAllSeries();
        if(allSportActivities.getActivities() == null) {
            allSportActivities.setActivities(new ArrayList<CardioActivity>());

        }

        HashMap<Integer, Double> monthDistanceMap = new HashMap<>();
        ArrayList <Integer> releventMonths = new ArrayList <>();
        ArrayList <CardioActivity> releventActivities = getCardioActivitiesBySpinnerChoice();

        /* Creates HashMap <month, kmForThatMonth>*/
        for (CardioActivity currentActivity : releventActivities) {

            double currentDistance = currentActivity.getDistance();

            String[] date = currentActivity.getDate().split("/");
            int month = Integer.parseInt(date[1]);
            if (monthDistanceMap.containsKey(month)){
                monthDistanceMap.put(month, monthDistanceMap.get(month) + currentDistance);
            }
            else{
                monthDistanceMap.put(month, currentDistance);
                releventMonths.add(month);
            }
        }

        DataPoint dp[] = new DataPoint[13];

        for (int i = 0 ; i < dp.length; i++) {
            dp[i] = new DataPoint(i, 0);
        }

        double maxDistance = 0;

        for (int month : releventMonths) {
            dp[month-1] = new DataPoint (month, monthDistanceMap.get(month));
            if (monthDistanceMap.get(month) > maxDistance){
                maxDistance = monthDistanceMap.get(month);
            }
        }
        barGraphSeries = new BarGraphSeries<>(dp);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        barGraphSeries.setDataWidth(0.5);

        graph.addSeries(barGraphSeries);

        staticLabelsFormatter.setHorizontalLabels(month_letters);
        graph.getGridLabelRenderer().setNumHorizontalLabels(12);
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        graph.setTitle("Km For Each Month");
        graph.setClickable(false);
        graph.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        graph.setVerticalScrollBarEnabled(false);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinX(1);
        graph.getViewport().setMaxX(13);
        graph.getViewport().setMaxY(((maxDistance/40)+1)*40);
        graph.getViewport().setScrollableY(true);
    }

    private ArrayList <CardioActivity> getCardioActivitiesBySpinnerChoice() {
        return Utils.getInstance().filterCardioActivitiesByType(allSportActivities.getActivities(), spinnerChoice);
    }


}


