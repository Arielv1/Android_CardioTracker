package com.example.running;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DecimalFormat;
import java.util.Arrays;

public class NewRunDetails implements Parcelable {

    private double pace;
    private double distance;
    private String[] date;
    private long timeInSeconds;


    public NewRunDetails(){}

    public NewRunDetails(double pace, double distance, String[] date, long timeInSeconds){
        this.pace = pace;
        this.distance = distance;
        this.date = date;
        this.timeInSeconds = timeInSeconds;
    }


    protected NewRunDetails(Parcel in) {
        pace = in.readDouble();
        distance = in.readDouble();
        date = in.createStringArray();
        timeInSeconds  = in.readLong();
    }

    public static final Creator<NewRunDetails> CREATOR = new Creator<NewRunDetails>() {
        @Override
        public NewRunDetails createFromParcel(Parcel in) {
            return new NewRunDetails(in);
        }

        @Override
        public NewRunDetails[] newArray(int size) {
            return new NewRunDetails[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(this.pace);
        parcel.writeDouble(this.distance);
        parcel.writeStringArray(this.date);
        parcel.writeLong(this.timeInSeconds);
    }

    public double getPace() {
        return pace;
    }

    public void setPace(double pace) {
        this.pace = pace;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }


    public void setDate(String[] date) {
        this.date = date;
    }

    public String[] getDate() {
        return date;
    }

    public void setTimeInSeconds(long timeInSeconds) {
        this.timeInSeconds = timeInSeconds;
    }

    public long getTimeInSeconds() {
        return timeInSeconds;
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("###.##");
        return "NewRunDetails{" +
                "pace=" +  df.format(pace) +
                ", distance=" + distance +
                ", date=" + Arrays.toString(date) +
                ", timeInSeconds=" + timeInSeconds +
                '}';
    }
}


