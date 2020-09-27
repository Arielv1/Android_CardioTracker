package com.example.running;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class PathPoints {

    private static PathPoints single_instance = null;

    private ArrayList<LatLng> points;

    public PathPoints() {
        points = new ArrayList<>();
    }

    // static method to create instance of Singleton class
    public static PathPoints getInstance()
    {
        if (single_instance == null)
            single_instance = new PathPoints();

        return single_instance;
    }
    public PathPoints(ArrayList<LatLng > points) {
        this.points = points;
    }

    public ArrayList<LatLng> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<LatLng> points) {
        this.points = points;
    }

    public void addPoint( LatLng point) { this.points.add(point); }

    @Override
    public String toString() {
        return "PathPoints{" +
                "points=" + points +
                '}';
    }
}
