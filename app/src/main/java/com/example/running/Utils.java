package com.example.running;

import android.content.Context;

import androidx.fragment.app.FragmentActivity;
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

    interface CardioActivityTypes {
        final String ALL = "All";
        final String JOGGING = "Jogging";
        final String RUNNING = "Running";
        final String CYCLING = "Cycling";
    }

    interface SpinnerValues {
        final int ALL_INDEX = 0;
        final int JOGGING_INDEX = 1;
        final int RUNNING_INDEX = 2;
        final int CYCLING_INDEX = 3;

    }

    interface AdapterViewOptions {
        final int LIST = 0;
        final int CARD = 1;
    }

    public int getCardioActivityPositionIndexInSpinner(String cardioActivityName) {
        if (cardioActivityName.equals(CardioActivityTypes.ALL)){
            return SpinnerValues.ALL_INDEX;
        }
        else  if (cardioActivityName.equals(CardioActivityTypes.JOGGING)){
            return SpinnerValues.JOGGING_INDEX;
        }
        else  if (cardioActivityName.equals(CardioActivityTypes.RUNNING)){
            return SpinnerValues.RUNNING_INDEX;
        }
        return SpinnerValues.CYCLING_INDEX;
    }

    public ArrayList<CardioActivity> filterCardioActivitiesByType(ArrayList <CardioActivity> cardioActivityList, String activityType){
        ArrayList <CardioActivity> filteredList = new ArrayList<CardioActivity>();

        if (activityType.equals(CardioActivityTypes.ALL)){
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
