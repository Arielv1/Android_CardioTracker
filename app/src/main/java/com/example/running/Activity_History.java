package com.example.running;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

public class Activity_History extends AppCompatActivity implements ListCardAdapter.OnItemClickListener {

    private static final String TAG = "ViewLogger";

    private String chosenRadioButtonValue;

    private Button btnList;
    private Button btnCard;

    private ListCardAdapter listCardAdapter;
    private RecyclerView history_LAY_recyclerview;
    private int lastDisplayChoice;
    private AllSportActivities allSportActivities;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @Override
    protected void onStop() {
        Log.d(TAG , "History - onStop Invoked");

        super.onStop();
        MySP.getInstance().putString(Keys.NEW_DATA_PACKAGE, Keys.DEFAULT_NEW_DATA_PACKAGE_VALUE);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "History - onDestroy Invoked");

        super.onDestroy();
        MySP.getInstance().putString(Keys.NEW_DATA_PACKAGE, Keys.DEFAULT_NEW_DATA_PACKAGE_VALUE);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "History - onStart Invoked");
        super.onStart();

        Gson gson = new Gson();
        CardioActivity cardioActivity = gson.fromJson(MySP.getInstance().getString(Keys.NEW_DATA_PACKAGE, Keys.DEFAULT_NEW_DATA_PACKAGE_VALUE), CardioActivity.class);

        if (cardioActivity != null) {
            ArrayList<CardioActivity> activities = allSportActivities.getActivities();
            for (CardioActivity current : activities) {
                if (current.getId().equals(cardioActivity.getId())){
                    activities.remove(current);
                    activities.add(cardioActivity);
                    Collections.sort(activities);
                    allSportActivities.setActivities(activities);
                    break;
                }
            }
            databaseReference.setValue(allSportActivities);
        }
        else {
        }
        setAdapterViewOption(MySP.getInstance().getInteger(Keys.HISTORY_VIEW_OPTION, Keys.DEFAULT_HISTORY_VIEW_OPTION_VALUE));
        refreshAllCardioActivitiesDisplayInAdapter(chosenRadioButtonValue);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__history);

        chosenRadioButtonValue =  MySP.getInstance().getString(Keys.RADIO_HISTORY_CHOICE, Keys.DEFAULT_RADIO_BUTTONS_HISTORY_VALUE);

        setUpViews();
        setUpFragments();

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(Keys.FIREBASE_ALL_RUNNING);

        lastDisplayChoice = MySP.getInstance().getInteger(Keys.HISTORY_VIEW_OPTION, Keys.DEFAULT_HISTORY_VIEW_OPTION_VALUE);
        allSportActivities = getBundleFromMainMenu();

        initializeAdapter();
        setAdapterViewOption(lastDisplayChoice);

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAdapterViewOption(Utils.AdapterViewOptions.LIST);
            }
        });


        btnCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAdapterViewOption(Utils.AdapterViewOptions.CARD);
            }
        });
    }

    private void initializeAdapter() {
        try {
            listCardAdapter = new ListCardAdapter(allSportActivities.getActivities(), this, lastDisplayChoice);
        }
        catch (Exception e) {
            listCardAdapter = new ListCardAdapter(new ArrayList<CardioActivity>(),  this, lastDisplayChoice);
        }

        history_LAY_recyclerview.setHasFixedSize(true);

    }

    private AllSportActivities getBundleFromMainMenu() {
        allSportActivities = (AllSportActivities) getIntent().getParcelableExtra(Keys.ALL_CARDIO_ACTIVITIES);
        if (allSportActivities  == null) {
            allSportActivities = new AllSportActivities(new ArrayList<CardioActivity>());
        }
        return allSportActivities;
    }

    Callback_RadioChoice callback = new Callback_RadioChoice() {
        @Override
        public void setRadioButtonChoice(String radioChoiceValue) {
            MySP.getInstance().putString(Keys.RADIO_HISTORY_CHOICE, radioChoiceValue);
            refreshAllCardioActivitiesDisplayInAdapter(radioChoiceValue);
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

    private void refreshAllCardioActivitiesDisplayInAdapter(String radioChoiceValue) {
        listCardAdapter.setCardioActivities(Utils.getInstance().filterCardioActivitiesByType(allSportActivities.getActivities(), radioChoiceValue));
        listCardAdapter.notifyDataSetChanged();
        history_LAY_recyclerview.addItemDecoration(new DividerItemDecoration(history_LAY_recyclerview.getContext(),LinearLayoutManager.VERTICAL));
        history_LAY_recyclerview.setAdapter(listCardAdapter);
    }

    private void setAdapterViewOption(int viewType) {
        MySP.getInstance().putInteger(Keys.HISTORY_VIEW_OPTION, viewType);
        listCardAdapter.setViewTypeRequset(viewType);
        listCardAdapter.notifyDataSetChanged();
        history_LAY_recyclerview.addItemDecoration(new DividerItemDecoration(history_LAY_recyclerview.getContext(),LinearLayoutManager.VERTICAL));
        history_LAY_recyclerview.setAdapter(listCardAdapter);
    }

    @Override
    public void onItemEdit(int position) {
        Intent intent = new Intent(getApplicationContext(), Activity_Add_Manually.class);
        chosenRadioButtonValue = allSportActivities.getActivities().get(position).getCardioActivityType();
        MySP.getInstance().putString(Keys.RADIO_HISTORY_CHOICE, chosenRadioButtonValue);
        intent.putExtra(Keys.NEW_DATA_PACKAGE, allSportActivities.getActivities().get(position));
        startActivity(intent);
    }

    @Override
    public void onItemDelete(int position) {

        Gson gson = new Gson();

        ArrayList <CardioActivity> activities = allSportActivities.getActivities();
        activities.remove(position);
        allSportActivities.setActivities(activities);
        listCardAdapter.notifyDataSetChanged();
        MySP.getInstance().putString(Keys.ALL_CARDIO_ACTIVITIES, gson.toJson(allSportActivities));
        databaseReference.setValue(allSportActivities);
    }
}