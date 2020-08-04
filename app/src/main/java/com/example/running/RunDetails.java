package com.example.running;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

public class RunDetails implements Parcelable {

    private String[] date;
    private String[] startTime;
    private String[] endTime;
    private double distance;

    public RunDetails(String[] date, String[] startTime, String[] endTime, double distance){
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.distance = distance;

    }

    protected RunDetails(Parcel in) {
        date = in.createStringArray();
        startTime = in.createStringArray();
        endTime = in.createStringArray();
        distance = in.readDouble();
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(date);
        parcel.writeStringArray(startTime);
        parcel.writeStringArray(endTime);
        parcel.writeDouble(distance);
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

    public String[] getDate() {
        return date;
    }

    public void setDate(String[] date) {
        this.date = date;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String[] getEndTime() {
        return endTime;
    }

    public void setEndTime(String[] endTime) {
        this.endTime = endTime;
    }

    public String[] getStartTime() {
        return startTime;
    }

    public void setStartTime(String[] startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "RunDetails{" +
                "date=" + Arrays.toString(date) +
                ", startTime=" + Arrays.toString(startTime) +
                ", endTime=" + Arrays.toString(endTime) +
                ", distance=" + distance +
                '}';
    }
}
