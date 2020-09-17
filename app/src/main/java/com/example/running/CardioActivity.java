package com.example.running;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.Date;

public class CardioActivity implements Parcelable {

    private String[] date;
    private String activityDuration;
    private double distance;
    private double pace;
    private long createdTimestamp = new Date().getTime();
    private String cardioActivityType;

    public CardioActivity() {

    }

    public CardioActivity(String[] date, String activityDuration, double distance, double pace, String cardioActivityType) {
        this.date = date;
        this.activityDuration = activityDuration;
        this.distance = distance;
        this.pace = pace;
        this.cardioActivityType = cardioActivityType;
    }


    protected CardioActivity(Parcel in) {
        date = in.createStringArray();
        activityDuration = in.readString();
        distance = in.readDouble();
        pace = in.readDouble();
        createdTimestamp = in.readLong();
        cardioActivityType = in.readString();
    }

    public static final Creator<CardioActivity> CREATOR = new Creator<CardioActivity>() {
        @Override
        public CardioActivity createFromParcel(Parcel in) {
            return new CardioActivity(in);
        }

        @Override
        public CardioActivity[] newArray(int size) {
            return new CardioActivity[size];
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
        parcel.writeLong(createdTimestamp);
        parcel.writeString(cardioActivityType);

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

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getCardioActivityType() {
        return cardioActivityType;
    }

    public void setCardioActivityType(String cardioActivityType) {
        this.cardioActivityType = cardioActivityType;
    }

    @Override
    public String toString() {
        return "RunDetails{" +
                "date=" + Arrays.toString(date) +
                ", activityDuration='" + activityDuration + '\'' +
                ", distance=" + distance +
                ", pace=" + pace +
                ", createdTimestamp=" + createdTimestamp +
                ", cardioActivityType='" + cardioActivityType + '\'' +
                '}';
    }
}
