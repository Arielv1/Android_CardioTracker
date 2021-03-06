package com.example.cardiotracker.utilities;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.cardiotracker.interfaces.Callback_RadioChoice;
import com.example.cardiotracker.interfaces.Keys;
import com.example.cardiotracker.models.AllSportActivities;
import com.example.cardiotracker.models.CardioActivity;
import com.example.cardiotracker.fragments.Fragment_Radio_Buttons;

import java.util.ArrayList;
import java.util.Collections;

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

    public Fragment_Radio_Buttons createFragmentRadioButtons(FragmentActivity fragmentActivity, Callback_RadioChoice callback, int replecedId, boolean allButtons) {
        Fragment_Radio_Buttons fragment = Fragment_Radio_Buttons.newInstance(allButtons);
        fragment.setActivityCallback(callback);
        FragmentTransaction transaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(replecedId, fragment);
        transaction.commit();
        return fragment;
    }

    public interface CardioActivityTypes {
        final String ALL = "All";
        final String JOGGING = "Jogging";
        final String RUNNING = "Running";
        final String CYCLING = "Cycling";
    }

    public interface SpinnerValues {
        final int ALL_INDEX = 0;
        final int JOGGING_INDEX = 1;
        final int RUNNING_INDEX = 2;
        final int CYCLING_INDEX = 3;

    }

    public interface AdapterViewOptions {
        final int LIST = 0;
        final int CARD = 1;
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

    private String[] splitEditTextByString(String str, String symbol){
        return str.split(symbol);
    }

    public Long calculateTimeDifference(String sTime, String eTime) {

        String[] startTime =  splitEditTextByString(sTime,":");
        String[] endTime = splitEditTextByString(eTime, ":");

        /*          (num hours * 3600 seconds in an hour)  +   (num minutes * 60 seconds in a minute) + seconds*/
        Long endTimeInSeconds = 3600 * Long.parseLong(endTime[0]) + 60 * Long.parseLong(endTime[1]) +  Long.parseLong(endTime[2]);
        Long startTimeInSeconds = 3600 * Long.parseLong(startTime[0]) + 60 * Long.parseLong(startTime[1]) + Long.parseLong(startTime[2]);

        if (startTimeInSeconds <= endTimeInSeconds) {
            return endTimeInSeconds - startTimeInSeconds;
        }

        /* there are 86400 seconds in a day */
        return 86400 - startTimeInSeconds + endTimeInSeconds;
    }

    public String formatTimeToString(long time) {
        Long hours, minutes, seconds;
        hours = time / 3600;
        minutes = (time / 60) % 60;
        seconds = (time % 60);

        String sSeconds = "", sMinutes, sHours, sTime="";
        if (seconds < 10) {
            sSeconds = "0" + seconds;
        }
        else {
            sSeconds = seconds.toString();
        }

        if (minutes < 10) {
            sMinutes = "0" + minutes;

        }
        else {
            sMinutes = minutes.toString();
        }

        if(hours == 0) {
            sTime = sMinutes +":" + sSeconds;
        }
        else {
            sTime = hours+ ":" + sMinutes +":" + sSeconds;
        }
        return sTime;
    }

    public double calculatePaceFromDistanceAndSeconds(double distance, double timeInSeconds){
        return (distance/((double)(timeInSeconds/3600)));
    }

    public AllSportActivities addNewCardioActivityDatabase(AllSportActivities allSportActivities, CardioActivity cardioActivity){
        if(allSportActivities == null) {
            allSportActivities = new AllSportActivities(new ArrayList<CardioActivity>());
        }
        ArrayList<CardioActivity> recordedActivities = allSportActivities.getActivities();
        recordedActivities.add(cardioActivity);
        Collections.sort(recordedActivities);
        return allSportActivities;

    }

    public AllSportActivities getAllSportsActivitiesBundleFromActivity(Activity activity){
        AllSportActivities allSportActivities =  activity.getIntent().getParcelableExtra(Keys.ALL_CARDIO_ACTIVITIES);
        if (allSportActivities  == null) {
            allSportActivities = new AllSportActivities(new ArrayList<CardioActivity>());
        }
        return allSportActivities;
    }

}
