package com.example.cardiotracker;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.NoCopySpan;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


public class GPS_Service extends Service {

    private static final String TAG = "GPS_Service";
    private LocationListener listener;
    private LocationManager locationManager;
    private boolean network_enabled, gps_enabled;




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {

        initNotification();

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Intent i = new Intent("location_update");
                i.putExtra("coordinates",location.getLongitude()+" "+location.getLatitude());
                i.putExtra("lat",location.getLatitude());
                i.putExtra("lng",location.getLongitude());
                sendBroadcast(i);
            }

            @Override
            public void onProviderEnabled(String provider) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,Keys.INTERVAL,0,listener);
            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Keys.INTERVAL,0,listener);

    }


    private void initNotification() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Intent notificationIntent = new Intent(this, Activity_New_Record.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            Notification notification = null;
            notification = new Notification.Builder(this, Keys.NOTIFICATION_CHANNEL)
                    .setSmallIcon(R.drawable.ic_location_on)
                    .setContentText("CardioTracker is using location & GPS services")
                    .build();
            startForeground(1, notification);

        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager != null){
            locationManager.removeUpdates(listener);
        }
    }

}