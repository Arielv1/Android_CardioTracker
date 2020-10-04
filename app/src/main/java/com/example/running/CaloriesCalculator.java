package com.example.running;

import android.content.Context;
import android.util.Log;

import java.util.LinkedHashMap;
import java.util.Map;

public class CaloriesCalculator {

    private double weight = 0.0;
    private static CaloriesCalculator instance;
    private Context context;
    private LinkedHashMap<Double, Double> METSValues = new LinkedHashMap<Double, Double>();

    private CaloriesCalculator(Context context) {
        this.context = context;
        initiateMETValues();
    }

    public static CaloriesCalculator init(Context context) {
        if (instance == null){
            instance = new CaloriesCalculator(context);
        }

        return instance;
    }

    public static CaloriesCalculator getInstance() {
        return instance;
    }


    private void initiateMETValues() {
        METSValues.put(17.54185, 18.0);
        METSValues.put(16.09344, 16.0);
        METSValues.put(14.4841, 15.0);
        METSValues.put(13.84036, 14.0);
        METSValues.put(12.87475, 13.5);
        METSValues.put(12.07008, 12.5);
        METSValues.put(11.26541, 11.5);
        METSValues.put(10.7826, 11.0);
        METSValues.put(9.656064, 10.0);
        METSValues.put(8.3685, 9.0);
        METSValues.put(5.632704, 3.8);
        METSValues.put(4.828032, 3.3);
        METSValues.put(3.218688, 2.0);

    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getWeight(){
        return weight;
    }

    public double calculateBurnedCalories(double pace, long seconds){
        double METSValue = 1.5;

        for (Map.Entry<Double, Double> entry : METSValues.entrySet()) {
            if(pace > entry.getKey()){
                METSValue = entry.getValue();
                break;
            }
        }
        return METSValue * ((double)seconds / 3600) * weight;
    }
}
