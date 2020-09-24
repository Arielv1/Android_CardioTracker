package com.example.running;

public interface Keys {

    public static final String NEW_DATA_PACKAGE = "NEW_RUN_DATA_PACKAGE";
    public static final String NUM_OF_RUNS = "NUM_OF_RUNS";
    public static final String TOTAL_DISTANCE = "TOTAL_DISTANCE";
    public static final String TOTAL_PACE = "TOTAL_PACE";
    public static final String AVERAGE_PACE = "AVERAGE_PACE";
    public static final String LABEL_DEFAULT_VALUE = "0";

    public static final String FIREBASE_ALL_RUNNING = "FIREBASE_ALL_RUNNING";

    public static final int DEFAULT_INT_VALUE = 0;
    public static final double DEFAULT_DOUBLE_VALUE = 0.0;

    public static final String ALL_CARDIO_ACTIVITIES = "ALL_SPORT_ACTIVITIES";
    public static final String DEFAULT_ALL_CARDIO_ACTIVITIES_VALUE = "";
    public static final String DEFAULT_NEW_DATA_PACKAGE_VALUE = "";

    public static final String SPINNER_CHOICE = "SPINNER_CHOICE";
    public static final String DEFAULT_SPINNER_CHOICE_VALUE = Utils.CardioActivityTypes.ALL;

    public static String RADIO_HISTORY_CHOICE = "";
    public static String DEFAULT_RADIO_BUTTONS_HISTORY_VALUE = Utils.CardioActivityTypes.ALL;

    public static String HISTORY_VIEW_OPTION = "HISTORY_VIEW_OPTION";
    public static int DEFAULT_HISTORY_VIEW_OPTION_VALUE = Utils.AdapterViewOptions.LIST;
}
