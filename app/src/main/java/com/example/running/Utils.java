package com.example.running;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class Utils {

    private static Utils utils;
    private Context context;

    public static Utils getInstance() {
        return utils;
    }

    private Utils(Context context) {
        this.context = context;
    }

    public static Utils init(Context context) {
        if (utils == null)
            utils = new Utils(context);
        return utils;
    }

    public Fragment_Radio_Buttons createFragmentRadioButtons(FragmentActivity fActivity, Callback_RadioChoice callback, int replecedId, boolean allButtons) {
        Fragment_Radio_Buttons fragment = Fragment_Radio_Buttons.newInstance(allButtons);
        fragment.setActivityCallback(callback);
        FragmentTransaction transaction = fActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(replecedId, fragment);
        transaction.commit();
        return fragment;
    }
}
