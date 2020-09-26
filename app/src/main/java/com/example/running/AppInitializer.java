package com.example.running;

import android.app.Application;

public class AppInitializer extends Application {

    private static final String TAG = "AppInitializer";

    @Override
    public void onCreate() {
        super.onCreate();

        Utils.init(this);
        Toaster.init(this);
        MySP.init(this);
        FirebaseInitializer.init(this, getString(R.string.test_email),  getString(R.string.test_password));

    }
}
