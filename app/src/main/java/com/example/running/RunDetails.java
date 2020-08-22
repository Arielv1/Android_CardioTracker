package com.example.running;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

public class RunDetails implements Parcelable {

    private String[] date;
    private String activityDuration;
    private double distance;
    private double pace;

    public RunDetails() {

    }

    public RunDetails(String[] date, String activityDuration, double distance, double pace) {
        this.date = date;
        this.activityDuration = activityDuration;
        this.distance = distance;
        this.pace = pace;
    }


    protected RunDetails(Parcel in) {
        date = in.createStringArray();
        activityDuration = in.readString();
        distance = in.readDouble();
        pace = in.readDouble();
    }

    public static final Creator<RunDetails> CREATOR = new Creator<RunDetails>() {
        @Override
        public RunDetails createFromParcel(Parcel in) {
            return new RunDetails(in);
        }

        @Override
        public RunDetails[] newArray(int size) {
            return new RunDetails[size];
        }
    };



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(date);
        parcel.writeString(activityDuration);
        parcel.writeDouble(distance);
        parcel.writeDouble(pace);
    }

    public String[] getDate() {
        return date;
    }

    public void setDate(String[] date) {
        this.date = date;
    }

    public String getActivityDuration() {
        return activityDuration;
    }

    public void setActivityDuration(String activityDuration) {
        this.activityDuration = activityDuration;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getPace() {
        return pace;
    }

    public void setPace(double pace) {
        this.pace = pace;
    }

    @Override
    public String toString() {
        return "RunDetails{" +
                "date=" + Arrays.toString(date) +
                ", activityDuration='" + activityDuration + '\'' +
                ", distance=" + distance +
                ", pace=" + pace +
                '}';
    }
}
