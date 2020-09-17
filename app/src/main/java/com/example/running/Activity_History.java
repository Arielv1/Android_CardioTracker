package com.example.running;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.button.MaterialButton;

public class Activity_History extends AppCompatActivity {

    private MaterialButton tabs_BTN_list;
    private MaterialButton tabs_BTN_map;

    private String cardioActivityChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__history);
        setUpViews();
        setUpFragments();

    }
    Callback_RadioChoice callback = new Callback_RadioChoice() {
        @Override
        public void setRadioButtonChoice(String choice) {
            cardioActivityChoice = choice;
        }
    };
    private void setUpViews() {

    }

    private void setUpFragments() {
        Fragment_Radio_Buttons fragment_radio_buttons = Utils.getInstance().createFragmentRadioButtons(this, callback, R.id.history_LAY_radio_buttons, true);
    }
}