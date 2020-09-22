package com.example.running;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

public class Activity_History extends AppCompatActivity {

    private Button history_BTN_list;
    private Button history_BTN_card;

    private int lastRadioChoiceIndex = 3;
    private String cardioActivityChoice;
    private String lastChoice;

    private Fragment_Log_List log_list;
    private Fragment_Log_Card log_card;

    private Button btnList;
    private Button btnCard;


    private ListCardAdapter listCardAdapter;
    private RecyclerView history_LAY_recyclerview;
    private int lastDisplayChoice;
    private AllSportActivities allSportActivities;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__history);
        lastChoice =  MySP.getInstance().getString(Keys.RADIO_HISTORY_CHOICE, Keys.DEFAULT_RADIO_BUTTONS_HISTORY_VALUE);
        setUpViews();

        //setUpFragments();
        Utils.getInstance().createFragmentRadioButtons(this, callback, R.id.history_LAY_radio_buttons, true);

        lastDisplayChoice = MySP.getInstance().getInteger(Keys.HISTORY_VIEW_OPTION, Keys.DEFAULT_HISTORY_VIEW_OPTION_VALUE);

        Gson gson = new Gson();
        allSportActivities = gson.fromJson(MySP.getInstance().getString(Keys.ALL_CARDIO_ACTIVITIES, Keys.DEFAULT_ALL_CARDIO_ACTIVITIES_VALUE), AllSportActivities.class);
        listCardAdapter = new ListCardAdapter(allSportActivities.getActivities(),  lastDisplayChoice, Activity_History.this);
        history_LAY_recyclerview.setHasFixedSize(true);

        adapterHandler(lastDisplayChoice);

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterHandler(Utils.AdapterViewOptions.LIST);
            }
        });


        btnCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterHandler(Utils.AdapterViewOptions.CARD);
            }
        });
    }

    Callback_RadioChoice callback = new Callback_RadioChoice() {
        @Override
        public void setRadioButtonChoice(String radioChoiceValue) {
            cardioActivityChoice = radioChoiceValue;
            MySP.getInstance().putString(Keys.RADIO_HISTORY_CHOICE, cardioActivityChoice);

            if (!cardioActivityChoice.equals(lastChoice)) {
                setUpCardioActivityHistoryList();
                updateCardioActivitiesInAdapter(cardioActivityChoice);
            }
            lastChoice = cardioActivityChoice;
        }


    };
    private void setUpViews() {
       /* history_BTN_list = findViewById(R.id.history_BTN_list);
        history_BTN_card = findViewById(R.id.history_BTN_card);*/
        btnList = findViewById(R.id.history_BTN_list);
        btnCard = findViewById(R.id.history_BTN_card);
        history_LAY_recyclerview = findViewById(R.id.history_LAY_recyclerview);
    }

    private void setUpFragments() {
        Utils.getInstance().createFragmentRadioButtons(this, callback, R.id.history_LAY_radio_buttons, true);
        setUpCardioActivityHistoryList();
    }

    private void setUpCardioActivityHistoryList() {
        log_list = Fragment_Log_List.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
       // transaction.replace(R.id.history_LAY_content, log_list);
        transaction.commit();
    }

    private void updateCardioActivitiesInAdapter(String radioChoiceValue) {

        listCardAdapter.setCardioActivities(Utils.getInstance().filterCardioActivitiesByType(allSportActivities.getActivities(), radioChoiceValue));
        listCardAdapter.notifyDataSetChanged();
        history_LAY_recyclerview.setAdapter(listCardAdapter);
    }
    private void adapterHandler(int viewType) {
        MySP.getInstance().putInteger(Keys.HISTORY_VIEW_OPTION, viewType);
        listCardAdapter.setViewTypeRequset(viewType);
        listCardAdapter.notifyDataSetChanged();
        history_LAY_recyclerview.setAdapter(listCardAdapter);
    }
}