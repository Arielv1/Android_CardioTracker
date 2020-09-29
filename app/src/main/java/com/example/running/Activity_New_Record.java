package com.example.running;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Activity_New_Record extends AppCompatActivity implements OnMapReadyCallback {

    private TextView lblDistance;
    private TextView lblPace;
    private TextView lblCalories;
    private Button btnStart;
    private Button btnPause;
    private Button btnStop;
    private Button btnConfirm;
    private Button btnCancel;
    private Chronometer tmrChronometer;
    private long timeOfPause;
    private boolean runningChronometer;
    private boolean firstStartClick;
    private String cardioType;
    private GoogleMap mGoogleMap;

    private BroadcastReceiver broadcastReceiver;
    private long timeInSeconds = 0;
    private double distance = 0;

    private long savedChronometerState;

    private Marker lastLocationMarkerOnMap;
    private LatLng lastLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final int ZOOM_VALUE = 13;

    private final int REQUEST_CODE = 101;
    private static final String TAG = "GPSActivity";

    Calendar calendar;
    SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private String sTime, eTime, date;

    private AllSportActivities allSportActivities;


    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "NewRun - onSavedInstance " + tmrChronometer.getBase());

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        savedChronometerState = savedInstanceState.getLong("CHRONO_STATE");
        Log.d(TAG, "NewRun - onRestoreInstanceState " + savedChronometerState);
        tmrChronometer.setBase(savedChronometerState);
        tmrChronometer.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    timeInSeconds = (long)((SystemClock.elapsedRealtime() - tmrChronometer.getBase())/1000);
                    if (lastLocationMarkerOnMap != null) {
                        lastLocationMarkerOnMap.remove();
                    }

                    LatLng currentLocation = new LatLng(Double.parseDouble(intent.getExtras().get("lat").toString())
                            , Double.parseDouble(intent.getExtras().get("lng").toString()));

                    if (lastLocation == null) {
                        lastLocation = currentLocation;
                    }

                    float[] results = new float[1];
                    Location.distanceBetween(lastLocation.latitude, lastLocation.longitude, currentLocation.latitude, currentLocation.longitude,results);
                    distance += (results[0]/1000);
                    calculateAndDisplayPerformance(distance, (double)timeInSeconds/3600);

                    mGoogleMap.addPolyline(new PolylineOptions().add(currentLocation, lastLocation));
                    lastLocation = currentLocation;

                    lastLocationMarkerOnMap = mGoogleMap.addMarker(new MarkerOptions().position(currentLocation));

                    Log.d(TAG, "currentLocation: " + currentLocation + " lastlocation " + lastLocation + " time: " + timeInSeconds + " distance " + distance);

                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, ZOOM_VALUE));

                }


            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
    }

    private void calculateAndDisplayPerformance(double distance, double seconds) {
        double pace = (distance/seconds);
        Log.d(TAG, "pace " + pace);
        DecimalFormat df = new DecimalFormat("###.##");
        updateTextView(lblDistance, df.format(distance));
        updateTextView(lblPace, df.format(pace));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_record);

        setUpViews();
        setUpFragments();

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(Keys.FIREBASE_ALL_RUNNING);

        getAllActivitiesFromFirebase();

        fetchLastKnownLocation();
        if (!confirmPermissions()) {
            enableButtons();

        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    Callback_RadioChoice callback = new Callback_RadioChoice() {
        @Override
        public void setRadioButtonChoice(String radioChoiceValue) {
            cardioType = radioChoiceValue;
            MySP.getInstance().putString(Keys.RADIO_CHOICE_NEW_RECORD, cardioType);
        }
    };


    private void setUpFragments() {
        Utils.getInstance().createFragmentRadioButtons(this, callback, R.id.new_run_LAY_radio_buttons, false, Keys.RADIO_CHOICE_NEW_RECORD);
    }

    private void setUpViews() {
        lblDistance = findViewById(R.id.new_run_LBL_distance);
        lblPace = findViewById(R.id.new_run_LBL_pace);
        lblCalories = findViewById(R.id.new_run_LBL_calories);
        btnStart = findViewById(R.id.new_run_BTN_start);
        btnPause = findViewById(R.id.new_run_BTN_pause);
        btnStop = findViewById(R.id.new_run_BTN_stop);
        tmrChronometer = findViewById(R.id.new_run_TMR_chronometer);
        btnCancel = findViewById(R.id.new_run_BTN_cancel);
        btnConfirm = findViewById(R.id.new_run_BTN_confirm);
    }

    private void startChronometer() {
        tmrChronometer.setBase(SystemClock.elapsedRealtime() - timeOfPause);
        tmrChronometer.start();
        runningChronometer = true;
    }

    private void pauseChronometer() {

        if (runningChronometer) {
            timeOfPause = SystemClock.elapsedRealtime() - tmrChronometer.getBase();
            tmrChronometer.stop();
            runningChronometer = false;
        } else  {
            tmrChronometer.setBase(SystemClock.elapsedRealtime() - timeOfPause);
            tmrChronometer.start();
            runningChronometer = true;
        }
    }

    private void stopChronometer() {

        tmrChronometer.stop();
        runningChronometer = false;
        btnCancel.setVisibility(View.VISIBLE);
        btnConfirm.setVisibility(View.VISIBLE);

    }

    private void enableButtons() {

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!firstStartClick) {
                    calendar = Calendar.getInstance();
                    sTime = (simpleTimeFormat.format(calendar.getTime()));
                    date = simpleDateFormat.format(calendar.getTime());

                    if (date.charAt(0)=='0'){
                        date = date.substring(1);
                    }

                    if (date.charAt(3)=='0'){
                        String start = date.substring(0,3);
                        date = start + date.substring(4);
                    }


                }
                Log.d(TAG, "onClick: sTime " + sTime + " date " + date);
                if (!runningChronometer){
                    btnPause.setEnabled(true);
                    btnStop.setEnabled(true);
                    startChronometer();
                    Intent i = new Intent(getApplicationContext(), GPS_Service.class);
                    startService(i);
                }
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseChronometer();
                Intent i = new Intent(getApplicationContext(), GPS_Service.class);
                stopService(i);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                eTime = (simpleTimeFormat.format(calendar.getTime()));
                Log.d(TAG, "onClick: eTime " + eTime);
                btnStart.setEnabled(false);
                btnPause.setEnabled(false);
                calculateAndDisplayPerformance(distance, (double)timeInSeconds/3600);
                stopChronometer();
                Intent i = new Intent(getApplicationContext(), GPS_Service.class);
                stopService(i);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cardioType != null && !cardioType.equals(Utils.CardioActivityTypes.ALL)){
                    addNewCartioActiviy();
                    finish();
                }
                else {
                    Toaster.getInstance().showToast("Please Select Type");
                }

            }
        });
    }

    private boolean confirmPermissions() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);

            return true;
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                fetchLastKnownLocation();
                enableButtons();
            } else {
                confirmPermissions();
            }
        }
    }

    private void fetchLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Task <Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    lastLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    lastLocationMarkerOnMap = mGoogleMap.addMarker(new MarkerOptions().position(lastLocation));
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(lastLocation));
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, ZOOM_VALUE));
                }
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }

    private void updateTextView(TextView textView, String text){
        textView.setText(text);
    }

    private void addNewCartioActiviy() {
        long durationValue = Utils.getInstance().calculateTimeDifference(sTime, eTime);
        String sDuration = Utils.getInstance().formatTimeToString(durationValue);
        CardioActivity newCardioActivity = new CardioActivity(
                date,
                sDuration,
                Double.parseDouble(lblDistance.getText().toString()),
                Double.parseDouble(lblPace.getText().toString()),
                new Date().getTime(),
                cardioType,
                sTime,
                eTime
        );

        allSportActivities = Utils.getInstance().addNewCardioActivityDatabase(allSportActivities, newCardioActivity);

        databaseReference.setValue(allSportActivities);
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
}