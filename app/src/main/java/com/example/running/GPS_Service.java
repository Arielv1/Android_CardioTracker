package com.example.running;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;


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
            public void onStatusChanged(String s, int i, Bundle bundle) {
                    /*TODO - check this shit*/
                Log.d(TAG, "onStatusChanged: ");
            }

            @Override
            public void onProviderEnabled(String s) {
                /*TODO - check this shit*/
                Log.d(TAG, "onProviderEnabled: ");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,0,listener);
            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
      //  checkRequirement();

        //noinspection MissingPermission

        /*TODO - define time interval */
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,0,listener);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager != null){
            //noinspection MissingPermission
            locationManager.removeUpdates(listener);
        }
    }

    private void checkRequirement() {

        gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!gps_enabled){
            Toaster.getInstance().showToast("GPS ARE NOT Enable");
        }
        network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(!network_enabled) {
            Toaster.getInstance().showToast("Network ARE NOT Enable");
        }

        Log.d(TAG, "checkRequirement: called" + gps_enabled + " " + network_enabled);
    }
}