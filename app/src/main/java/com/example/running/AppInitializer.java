package com.example.running;

import android.app.Application;
import android.widget.Toast;

public class AppInitializer extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Utils.init(this);
        Toaster.init(this);
        MySP.init(this);
    }
}