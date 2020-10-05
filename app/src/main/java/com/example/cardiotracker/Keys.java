package com.example.cardiotracker;

public interface Keys {

    public static final String NEW_DATA_PACKAGE = "NEW_RUN_DATA_PACKAGE";

    public static final String FIREBASE_ALL_RUNNING = "FIREBASE_ALL_RUNNING";
    public static final double DEFAULT_DOUBLE_VALUE = 0.0;

    public static final String ALL_CARDIO_ACTIVITIES = "ALL_SPORT_ACTIVITIES";
    public static final String DEFAULT_ALL_CARDIO_ACTIVITIES_VALUE = "";
    public static final String DEFAULT_NEW_DATA_PACKAGE_VALUE = "";

    public static final String SPINNER_CHOICE = "SPINNER_CHOICE";
    public static final String DEFAULT_SPINNER_CHOICE_VALUE = Utils.CardioActivityTypes.ALL;

    public static final String RADIO_HISTORY_CHOICE = "";
    public static final String DEFAULT_RADIO_BUTTONS_HISTORY_VALUE = Utils.CardioActivityTypes.ALL;

    public static final String RADIO_HISTORY_CHOICE_EDIT = "";
    public static final String RADIO_CHOICE_NEW_RECORD = "";

    public static final String HISTORY_VIEW_OPTION = "HISTORY_VIEW_OPTION";
    public static final int DEFAULT_HISTORY_VIEW_OPTION_VALUE = Utils.AdapterViewOptions.LIST;

    public static final String WEIGHT_KEY = "WEIGHT_KEY";

    public static final String WEIGHT_WARNING = "WEIGHT_WARNING";
    public static final boolean DEFAULT_VALUE_WEIGHT_WARNING = false;

    public static final int INTERVAL = 3000;

    public static final String WEIGHT_ALERT_DIALOG = "WEIGHT_ALERT_DIALOG";
    public static final boolean DEFAULT_VALUE_SHOW_WEIGHT_ALERT_DIALOG = true;

    public static final String NOTIFICATION_CHANNEL = "Notification_Channel";
}
