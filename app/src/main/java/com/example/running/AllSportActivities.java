package com.example.running;

import java.util.ArrayList;

public class AllSportActivities {

    ArrayList<CardioActivity> activities = new ArrayList<CardioActivity>();

    public AllSportActivities() {
    }

    public AllSportActivities(ArrayList<CardioActivity> activities) {
        this.activities = activities;
    }

    public ArrayList<CardioActivity> getActivities() {
        return activities;
    }

    public void setActivities(ArrayList<CardioActivity> activities) {
        this.activities = activities;
    }

    public void addActivity(CardioActivity activity) {
        this.activities.add(activity);
    }

    @Override
    public String toString() {
        return "AllSportActivities{" +
                "activities=" + activities +
                '}';
    }
}
