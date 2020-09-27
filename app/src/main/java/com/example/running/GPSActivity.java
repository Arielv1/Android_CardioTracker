package com.example.running;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

public class GPSActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Button btnStart;
    private Button btnPause;
    private Button btnStop;
    private Button btnConfirm;
    private Button btnCancel;
    private Chronometer tmrChronometer;
    private long timeOfPause;
    private boolean runningChronometer;
    private boolean stoppedChronometer;
    private String cardioType;
    private GoogleMap mGoogleMap;
    private Button btn_start, btn_stop;
    private TextView textView;
    private BroadcastReceiver broadcastReceiver;
    private long timeInSeconds = 0;

    private long savedChronometerState;
    private LocationListener listener;
    private LocationManager locationManager;
    private Marker lastLocationMarkerOnMap;
    private LatLng lastLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final int ZOOM_VALUE = 13;

    private static final String TAG = "GPSActivity";

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
                    timeInSeconds++;

                    if (lastLocationMarkerOnMap != null) {
                        lastLocationMarkerOnMap.remove();
                    }

                    LatLng currentLocation = new LatLng(Double.parseDouble(intent.getExtras().get("lat").toString())
                            , Double.parseDouble(intent.getExtras().get("lng").toString()));

                    if (lastLocation == null) {
                        lastLocation = currentLocation;
                    }

                    mGoogleMap.addPolyline(new PolylineOptions().add(currentLocation, lastLocation));
                    lastLocation = currentLocation;

                    lastLocationMarkerOnMap = mGoogleMap.addMarker(new MarkerOptions().position(currentLocation));

                    Log.d(TAG, "currentLocation: " + currentLocation + "time: " + timeInSeconds);

                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, ZOOM_VALUE));

                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
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
        setContentView(R.layout.activity__record);

        setUpViews();
        setUpFragments();
        fetchLastKnownLocation();
        if (!confirmPermissions()) {
            enable_buttons();

        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    Callback_RadioChoice callback = new Callback_RadioChoice() {
        @Override
        public void setRadioButtonChoice(String radioChoiceValue) {
            cardioType = radioChoiceValue;
        }
    };


    private void setUpFragments() {
        Utils.getInstance().createFragmentRadioButtons(this, callback, R.id.new_run_LAY_radio_buttons, false);
    }

    private void setUpViews() {
        btnStart = findViewById(R.id.new_run_BTN_start);
        btnPause = findViewById(R.id.new_run_BTN_pause);
        btnStop = findViewById(R.id.new_run_BTN_stop);
        tmrChronometer = findViewById(R.id.new_run_TMR_chronometer);
        btnCancel = findViewById(R.id.new_run_BTN_cancel);
        btnConfirm = findViewById(R.id.new_run_BTN_confirm);
    }

    private void startChronometer() {

        if (!runningChronometer && !stoppedChronometer) {
            tmrChronometer.setBase(SystemClock.elapsedRealtime() - timeOfPause);
            tmrChronometer.start();
            runningChronometer = true;
        }
    }

    private void pauseChronometer() {

        if (runningChronometer && !stoppedChronometer) {
            timeOfPause = SystemClock.elapsedRealtime() - tmrChronometer.getBase();
            tmrChronometer.stop();
            runningChronometer = false;
        } else if (!stoppedChronometer) {
            tmrChronometer.setBase(SystemClock.elapsedRealtime() - timeOfPause);
            tmrChronometer.start();
            runningChronometer = true;
        }
    }

    private void stopChronometer() {

        tmrChronometer.stop();
        runningChronometer = false;
        stoppedChronometer = true;
        btnCancel.setVisibility(View.VISIBLE);
        btnConfirm.setVisibility(View.VISIBLE);

    }

    private void enable_buttons() {

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                TODO - find a way to pause restart listener after pause/ stop
                */
                startChronometer();
                Intent i = new Intent(getApplicationContext(), GPS_Service.class);
                startService(i);
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                TODO - find a way to pause listener / manager
                 */
                pauseChronometer();
                Intent i = new Intent(getApplicationContext(), GPS_Service.class);
                stopService(i);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopChronometer();
                Intent i = new Intent(getApplicationContext(), GPS_Service.class);
                stopService(i);
            }
        });
    }

    private boolean confirmPermissions() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);

            return true;
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                fetchLastKnownLocation();
                enable_buttons();
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
                }
            }
        });

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }
}