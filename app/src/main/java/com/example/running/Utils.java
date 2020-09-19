package com.example.running;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

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


    interface Spinner_Values {
        final int ALL_INDEX = 0;
        final String ALL = "All";

        final int JOGGING_INDEX = 1;
        final String JOGGING = "Jogging";

        final int RUNNING_INDEX = 2;
        final String RUNNING = "Running";

        final int CYCLING_INDEX = 3;
        final String CYCLING = "Cycling";

    }

    public int getCardioActivityPositionIndexInSpinner(String cardioActivityName) {
        if (cardioActivityName.equals(Spinner_Values.ALL)){
            return Spinner_Values.ALL_INDEX;
        }
        else  if (cardioActivityName.equals(Spinner_Values.JOGGING)){
            return Spinner_Values.JOGGING_INDEX;
        }
        else  if (cardioActivityName.equals(Spinner_Values.RUNNING)){
            return Spinner_Values.RUNNING_INDEX;
        }
        return Spinner_Values.CYCLING_INDEX;
    }

    public ArrayList<CardioActivity> filterCardioActivitiesByType(ArrayList <CardioActivity> cardioActivityList, String activityType){
        ArrayList <CardioActivity> filteredList = new ArrayList<CardioActivity>();

        if (activityType.equals(Spinner_Values.ALL)){
            return cardioActivityList;
        }
        else {
            for (CardioActivity cardioActivity : cardioActivityList) {
                if (cardioActivity.getCardioActivityType().equals(activityType)){
                    filteredList.add(cardioActivity);
                }
            }
        }

        return filteredList;
    }
}
