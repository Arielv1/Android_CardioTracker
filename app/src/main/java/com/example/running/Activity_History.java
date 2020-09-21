package com.example.running;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Button;

public class Activity_History extends AppCompatActivity {

    private Button history_BTN_list;
    private Button history_BTN_card;

    private int lastRadioChoiceIndex = 3;
    private String cardioActivityChoice;
    private String lastChoice;

    private Fragment_Log_List listLog;
    private Fragment_Log_Card cardLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__history);
        lastChoice =  MySP.getInstance().getString(Keys.RADIO_HISTORY_CHOICE, Keys.DEFAULT_RADIO_BUTTONS_HISTORY_VALUE);
        setUpViews();

        setUpFragments();

    }

    Callback_RadioChoice callback = new Callback_RadioChoice() {
        @Override
        public void setRadioButtonChoice(String radioChoiceValue) {
            cardioActivityChoice = radioChoiceValue;
            MySP.getInstance().putString(Keys.RADIO_HISTORY_CHOICE, cardioActivityChoice);

            if (!cardioActivityChoice.equals(lastChoice)) {
                setUpCardioActivityHistoryList();
            }
            lastChoice = cardioActivityChoice;
        }
    };
    private void setUpViews() {
        history_BTN_list = findViewById(R.id.history_BTN_list);
        history_BTN_card = findViewById(R.id.history_BTN_card);
    }

    private void setUpFragments() {
        Utils.getInstance().createFragmentRadioButtons(this, callback, R.id.history_LAY_radio_buttons, true);
        setUpCardioActivityHistoryList();
    }

    private void setUpCardioActivityHistoryList() {
        Fragment_Log_List detailed_log = Fragment_Log_List.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.history_LAY_content, detailed_log);
        transaction.commit();
    }
}