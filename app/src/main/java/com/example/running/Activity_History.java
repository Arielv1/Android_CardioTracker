package com.example.running;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

import java.util.ArrayList;

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
    protected void onStop() {
        Log.d("ViewLogger", "History - onStop Invoked");

        super.onStop();
        MySP.getInstance().putString(Keys.NEW_DATA_PACKAGE, Keys.DEFAULT_NEW_DATA_PACKAGE_VALUE);
    }

    @Override
    protected void onDestroy() {
        Log.d("ViewLogger", "History - onDestroy Invoked");

        super.onDestroy();
        MySP.getInstance().putString(Keys.NEW_DATA_PACKAGE, Keys.DEFAULT_NEW_DATA_PACKAGE_VALUE);
    }

    @Override
    protected void onStart() {
        Log.d("ViewLogger", "History - onStart Invoked");
        super.onStart();

        Gson gson = new Gson();
        CardioActivity cardioActivity = gson.fromJson(MySP.getInstance().getString(Keys.NEW_DATA_PACKAGE, Keys.DEFAULT_NEW_DATA_PACKAGE_VALUE), CardioActivity.class);

        if (cardioActivity != null) {
            ArrayList<CardioActivity> activities = allSportActivities.getActivities();
            for (CardioActivity current : allSportActivities.getActivities()) {
                if (current.getId().equals(cardioActivity.getId())) {
                    activities.remove(current);
                    break;
                }
            }
            activities.add(cardioActivity);
            listCardAdapter.notifyDataSetChanged();
            MySP.getInstance().putString(Keys.ALL_CARDIO_ACTIVITIES, gson.toJson(allSportActivities));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__history);
        lastChoice =  MySP.getInstance().getString(Keys.RADIO_HISTORY_CHOICE, Keys.DEFAULT_RADIO_BUTTONS_HISTORY_VALUE);
        setUpViews();

        setUpFragments();


        lastDisplayChoice = MySP.getInstance().getInteger(Keys.HISTORY_VIEW_OPTION, Keys.DEFAULT_HISTORY_VIEW_OPTION_VALUE);

        Gson gson = new Gson();
        allSportActivities = gson.fromJson(MySP.getInstance().getString(Keys.ALL_CARDIO_ACTIVITIES, Keys.DEFAULT_ALL_CARDIO_ACTIVITIES_VALUE), AllSportActivities.class);

       try {
           listCardAdapter = new ListCardAdapter(allSportActivities.getActivities(),  lastDisplayChoice, Activity_History.this);
       }
       catch (Exception e) {
           listCardAdapter = new ListCardAdapter(new ArrayList<CardioActivity>(),  lastDisplayChoice, Activity_History.this);
       }

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
        btnList = findViewById(R.id.history_BTN_list);
        btnCard = findViewById(R.id.history_BTN_card);
        history_LAY_recyclerview = findViewById(R.id.history_LAY_recyclerview);
    }

    private void setUpFragments() {
        Utils.getInstance().createFragmentRadioButtons(this, callback, R.id.history_LAY_radio_buttons, true);
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