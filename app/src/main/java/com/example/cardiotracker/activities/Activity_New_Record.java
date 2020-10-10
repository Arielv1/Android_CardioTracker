package com.example.cardiotracker.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.cardiotracker.models.AllSportActivities;
import com.example.cardiotracker.interfaces.Callback_RadioChoice;
import com.example.cardiotracker.utilities.CaloriesCalculator;
import com.example.cardiotracker.models.CardioActivity;
import com.example.cardiotracker.services.GPS_Service;
import com.example.cardiotracker.interfaces.Keys;
import com.example.cardiotracker.utilities.MySP;
import com.example.cardiotracker.R;
import com.example.cardiotracker.utilities.Toaster;
import com.example.cardiotracker.utilities.Utils;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Activity_New_Record extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "Activity_New_Record";
    private AllSportActivities allSportActivities;

    private TextView lblDistance;
    private TextView lblPace;
    private TextView lblCalories;

    private long timeInSeconds = 0;
    private double distance = 0;
    private double caloriesBurned = 0;
    private double pace = 0;

    private Button btnStart;
    private Button btnPause;
    private Button btnStop;
    private Button btnConfirm;
    private Button btnCancel;

    private Chronometer tmrChronometer;
    private long timeOfPause;
    private boolean runningChronometer;
    private boolean firstStartClick;
    private long savedChronometerState;


    private String cardioType;
    private GoogleMap mGoogleMap;
    private BroadcastReceiver broadcastReceiver;
    private Marker lastLocationMarkerOnMap;
    private LatLng lastLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final int ZOOM_VALUE = 16;

    private final int REQUEST_CODE = 101;


    private Calendar calendar;
    private SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private String sTime, eTime, date;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private Callback_RadioChoice callback = new Callback_RadioChoice() {
        @Override
        public void setRadioButtonChoice(String radioChoiceValue) {
            cardioType = radioChoiceValue;
            MySP.getInstance().putString(Keys.RADIO_CHOICE_NEW_RECORD, cardioType);
        }
    };


    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        savedChronometerState = savedInstanceState.getLong("chronometer_state");
        tmrChronometer.setBase(savedChronometerState);
        tmrChronometer.start();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo ();
        if (!(activeNetworkInfo != null && activeNetworkInfo.isConnected())) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Internet Connection Problem");
            builder.setMessage("There's seems to be a problem connecting to the WiFi / Mobile Data - this activity won't work until connection is restored");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else{
            if (!confirmPermissions()) {
                fetchLastKnownLocation();
                enableButtons();
            }
        }



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

                    DecimalFormat df = new DecimalFormat("###.##");
                    float[] results = new float[1];
                    Location.distanceBetween(lastLocation.latitude, lastLocation.longitude, currentLocation.latitude, currentLocation.longitude,results);

                    distance += (results[0]/1000);
                    pace = Utils.getInstance().calculatePaceFromDistanceAndSeconds(distance, timeInSeconds);
                    caloriesBurned += CaloriesCalculator.getInstance().calculateBurnedCalories(pace, (Keys.INTERVAL / 1000));

                    updateTextView(lblDistance, df.format(distance));
                    updateTextView(lblPace, df.format(pace));
                    updateTextView(lblCalories, df.format(caloriesBurned));

                    mGoogleMap.addPolyline(new PolylineOptions().add(currentLocation, lastLocation));
                    lastLocation = currentLocation;

                    lastLocationMarkerOnMap = mGoogleMap.addMarker(new MarkerOptions().position(currentLocation));

                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, ZOOM_VALUE));

                }

            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));

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

        /* Get all sport activities sent by the main menu (from firebase) */
        allSportActivities = Utils.getInstance().getAllSportsActivitiesBundleFromActivity(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void setUpFragments() {
        Utils.getInstance().createFragmentRadioButtons(this, callback, R.id.new_run_LAY_radio_buttons, false);
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

    private void enableButtons() {

        

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!firstStartClick) {
                    calendar = Calendar.getInstance();
                    sTime = (simpleTimeFormat.format(calendar.getTime()));
                    date = simpleDateFormat.format(calendar.getTime());
                }
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
                btnStart.setEnabled(false);
                btnPause.setEnabled(false);
                stopChronometer();
                Intent i = new Intent(getApplicationContext(), GPS_Service.class);
                stopService(i);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closingDialog();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cardioType != null && !cardioType.equals(Utils.CardioActivityTypes.ALL)){
                    addNewCardioActiviy();
                    finish();
                }
                else {
                    Toaster.getInstance().showToast("Please Select Type");
                }
            }
        });
    }

    private void closingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want to discard this activity?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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

    private boolean confirmPermissions() {
        Log.d(TAG, "confirmPermissions: called");
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
            return true;
        }
        Log.d(TAG, "confirmPermissions: return false");
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: called");
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                fetchLastKnownLocation();
                enableButtons();
            } else {
                Toaster.getInstance().showToast("Please Activate Location Services");
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

    private void addNewCardioActiviy() {
        long durationValue = Utils.getInstance().calculateTimeDifference(sTime, eTime);
        String sDuration = Utils.getInstance().formatTimeToString(durationValue);
        CardioActivity newCardioActivity = new CardioActivity(
                date,
                sDuration,
                distance,
                pace,
                caloriesBurned,
                new Date().getTime(),
                cardioType,
                sTime,
                eTime
        );

        allSportActivities = Utils.getInstance().addNewCardioActivityDatabase(allSportActivities, newCardioActivity);

        databaseReference.setValue(allSportActivities);
    }

    private AllSportActivities getBundleFromMainMenu() {
        allSportActivities =  getIntent().getParcelableExtra(Keys.ALL_CARDIO_ACTIVITIES);
        if (allSportActivities  == null) {
            allSportActivities = new AllSportActivities(new ArrayList<CardioActivity>());
        }
        return allSportActivities;
    }
}