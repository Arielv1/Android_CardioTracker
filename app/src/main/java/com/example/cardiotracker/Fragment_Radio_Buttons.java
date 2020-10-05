package com.example.cardiotracker;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

public class Fragment_Radio_Buttons extends Fragment {
    private static final String TAG = "Fragment_Radio_Buttons";

    private RadioButton radioJogging;
    private RadioButton radioRunning;
    private RadioButton radioCycling;
    private RadioButton radioAll;

    private String referenceKey;
    private boolean needAllRadioButton;

    private Callback_RadioChoice callback_radioChoice;

    public Fragment_Radio_Buttons(boolean needAllRadioButton, String referenceKey) {
        this.needAllRadioButton = needAllRadioButton;
        this.referenceKey = referenceKey;
    }

    public static Fragment_Radio_Buttons newInstance(boolean needAllRadioButton, String refrenceKey) {
        return new Fragment_Radio_Buttons(needAllRadioButton, refrenceKey);
    }

    public void setActivityCallback(Callback_RadioChoice callback_radioChoice) {
        this.callback_radioChoice = callback_radioChoice;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: called");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_radio_buttons, container, false);

        setUpViewsInFragment(view);

        if (!needAllRadioButton) {
            radioAll.setVisibility(View.INVISIBLE);
        }

        radioJogging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioHandler(Utils.CardioActivityTypes.JOGGING);
            }
        });
        radioRunning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioHandler(Utils.CardioActivityTypes.RUNNING);
            }
        });
        radioCycling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioHandler(Utils.CardioActivityTypes.CYCLING);
            }
        });

        radioAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioHandler(Utils.CardioActivityTypes.ALL);
            }
        });

        setLastPressedRadioButton();

        return view;
    }

    private void setLastPressedRadioButton() {
        String lastChoice = MySP.getInstance().getString(referenceKey, Keys.DEFAULT_RADIO_BUTTONS_HISTORY_VALUE);

        if(lastChoice.equals(Utils.CardioActivityTypes.ALL)){
            radioAll.performClick();
        }
        else if(lastChoice.equals(Utils.CardioActivityTypes.JOGGING)){
            radioJogging.performClick();
        }
        else if(lastChoice.equals(Utils.CardioActivityTypes.RUNNING)){
            radioRunning.performClick();
        }
        else {
            radioCycling.performClick();
        }
    }

    private void radioHandler(String radioChoiceValue) {
        if (callback_radioChoice != null) {
            MySP.getInstance().putString(referenceKey, radioChoiceValue);
            callback_radioChoice.setRadioButtonChoice(radioChoiceValue);
        }
    }


    private void setUpViewsInFragment(View view) {
        radioJogging = view.findViewById(R.id.fragment_RAD_jogging);
        radioRunning = view.findViewById(R.id.fragment_RAD_running);
        radioCycling = view.findViewById(R.id.fragment_RAD_cycling);
        radioAll = view.findViewById(R.id.fragment_RAD_all);
    }
}