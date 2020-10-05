package com.example.cardiotracker;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class AllSportActivities implements Parcelable {

    ArrayList<CardioActivity> activities = new ArrayList<CardioActivity>();

    public AllSportActivities() {
    }

    public AllSportActivities(ArrayList<CardioActivity> activities) {
        this.activities = activities;
    }

    protected AllSportActivities(Parcel in) {
        activities = in.createTypedArrayList(CardioActivity.CREATOR);
    }

    public static final Creator<AllSportActivities> CREATOR = new Creator<AllSportActivities>() {
        @Override
        public AllSportActivities createFromParcel(Parcel in) {
            return new AllSportActivities(in);
        }

        @Override
        public AllSportActivities[] newArray(int size) {
            return new AllSportActivities[size];
        }
    };

    public ArrayList<CardioActivity> getActivities() {
        return activities;
    }

    public void setActivities(ArrayList<CardioActivity> activities) {
        this.activities = activities;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(activities);
    }

    @Override
    public String toString() {
        return "AllSportActivities{" +
                "activities=" + activities +
                '}';
    }
}
