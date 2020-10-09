package com.example.cardiotracker.models;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Date;
import java.util.UUID;

public class CardioActivity implements Parcelable, Comparable<CardioActivity> {

    private String id;
    private String date;
    private String duration; /* is a string since time format is 12:34:56 */
    private double distance;
    private double pace;
    private double caloriesBurned;
    private long createdTimestamp = new Date().getTime();
    private String cardioActivityType;
    private String timeStart;
    private String timeEnd;

    public CardioActivity() {
    }

    public CardioActivity(String date,String duration, double distance, double pace, double caloriesBurned, long createdTimestamp, String cardioActivityType, String timeStart, String timeEnd) {
    this.id = UUID.randomUUID().toString();
    this.date = date;
    this.duration = duration;
    this.distance = distance;
    this.pace = pace;
    this.caloriesBurned = caloriesBurned;
    this.createdTimestamp = createdTimestamp;
    this.cardioActivityType = cardioActivityType;
    this.timeStart = timeStart;
    this.timeEnd = timeEnd;
    }

    protected CardioActivity(Parcel in) {
        id = in.readString();
        date = in.readString();
        duration = in.readString();
        distance = in.readDouble();
        pace = in.readDouble();
        caloriesBurned = in.readDouble();
        createdTimestamp = in.readLong();
        cardioActivityType = in.readString();
        timeStart = in.readString();
        timeEnd = in.readString();
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
        parcel.writeString(id);
        parcel.writeString(date);
        parcel.writeString(duration);
        parcel.writeDouble(distance);
        parcel.writeDouble(pace);
        parcel.writeDouble(caloriesBurned);
        parcel.writeLong(createdTimestamp);
        parcel.writeString(cardioActivityType);
        parcel.writeString(timeStart);
        parcel.writeString(timeEnd);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public String getId() {
        return id;
    }

    public void setDuration(String duration) {
        this.duration = duration;
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

    public double getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(double caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
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

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "CardioActivity{" +
                "id='" + id + '\'' +
                ", date='" + date + '\'' +
                ", duration='" + duration + '\'' +
                ", distance=" + distance +
                ", pace=" + pace +
                ", caloriesBurned=" + caloriesBurned +
                ", createdTimestamp=" + createdTimestamp +
                ", cardioActivityType='" + cardioActivityType + '\'' +
                ", timeStart='" + timeStart + '\'' +
                ", timeEnd='" + timeEnd + '\'' +
                '}';
    }

    @Override
    public int compareTo(CardioActivity cardioActivity) {
        return Long.compare(cardioActivity.getCreatedTimestamp(), this.getCreatedTimestamp());
    }
}
