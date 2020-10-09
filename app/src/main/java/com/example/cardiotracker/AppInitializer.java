package com.example.cardiotracker;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.example.cardiotracker.interfaces.Keys;
import com.example.cardiotracker.utilities.CaloriesCalculator;
import com.example.cardiotracker.utilities.MySP;
import com.example.cardiotracker.utilities.Toaster;
import com.example.cardiotracker.utilities.Utils;

public class AppInitializer extends Application {

    private static final String TAG = "AppInitializer";

    @Override
    public void onCreate() {
        super.onCreate();

        Utils.init(this);
        Toaster.init(this);
        MySP.init(this);
        CaloriesCalculator.init(this);
        FirebaseInitializer.init(this, getString(R.string.test_email),  getString(R.string.test_password));
        initNotificationChannel();

    }

    private void initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(Keys.NOTIFICATION_CHANNEL, "CardioTracker", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
