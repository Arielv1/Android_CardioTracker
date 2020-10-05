package com.example.cardiotracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
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

    private static final String TAG = "Activity_History";

    private String chosenRadioButtonValue;

    private Button btnList;
    private Button btnCard;
    private Button btnReset;
    
    private ListCardAdapter listCardAdapter;
    private RecyclerView history_LAY_recyclerview;
    private int lastDisplayChoice;
    private AllSportActivities allSportActivities;

    private CardioActivity selectedToEdit;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private Callback_RadioChoice callback = new Callback_RadioChoice() {
        @Override
        public void setRadioButtonChoice(String radioChoiceValue) {
            chosenRadioButtonValue = radioChoiceValue;
            refreshAllCardioActivitiesDisplayInAdapter(radioChoiceValue);
        }


    };


    @Override
    protected void onStop() {
        super.onStop();

        /* If we edited a sports acitivity, the Main Menu will read this package as well
        * since it doesn't know if it returned from History or Manual activities
        * Setting the key to default avoids reading the same activity twice - once by History and another by Main
        * */
        MySP.getInstance().putString(Keys.NEW_DATA_PACKAGE, Keys.DEFAULT_NEW_DATA_PACKAGE_VALUE);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "History - onStart Invoked");
        super.onStart();

        Gson gson = new Gson();

        /* Check if got back from Manual, in the case of edit */
        CardioActivity cardioActivity = gson.fromJson(MySP.getInstance().getString(Keys.NEW_DATA_PACKAGE, Keys.DEFAULT_NEW_DATA_PACKAGE_VALUE), CardioActivity.class);

        if (cardioActivity != null) {
            ArrayList<CardioActivity> activities = allSportActivities.getActivities();
            activities.remove(selectedToEdit);
            activities.add(cardioActivity);
            Collections.sort(activities);
            allSportActivities.setActivities(activities);
            databaseReference.setValue(allSportActivities);
            setAdapterViewOption(MySP.getInstance().getInteger(Keys.HISTORY_VIEW_OPTION, Keys.DEFAULT_HISTORY_VIEW_OPTION_VALUE));
            refreshAllCardioActivitiesDisplayInAdapter(chosenRadioButtonValue);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        chosenRadioButtonValue =  MySP.getInstance().getString(Keys.RADIO_HISTORY_CHOICE, Keys.DEFAULT_RADIO_BUTTONS_HISTORY_VALUE);

        setUpViews();
        setUpFragments();

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(Keys.FIREBASE_ALL_RUNNING);

        lastDisplayChoice = MySP.getInstance().getInteger(Keys.HISTORY_VIEW_OPTION, Keys.DEFAULT_HISTORY_VIEW_OPTION_VALUE);

        /* Get all sport activities sent by the main menu (from firebase) */
        allSportActivities = getBundleFromMainMenu();

        initializeAdapter();

        /* Set up the adapter with the last chosen display option - list or card */
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
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAlertReset();
            }
        });
    }


    private void setUpViews() {
        btnList = findViewById(R.id.history_BTN_list);
        btnCard = findViewById(R.id.history_BTN_card);
        btnReset = findViewById(R.id.history_LBL_reset);
        history_LAY_recyclerview = findViewById(R.id.history_LAY_recyclerview);
    }

    private void setUpFragments() {
        Utils.getInstance().createFragmentRadioButtons(this, callback, R.id.history_LAY_radio_buttons, true, Keys.RADIO_HISTORY_CHOICE);
    }

    private void createAlertReset() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want to reset your history and progress?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                reset();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }



    private void reset() {
        allSportActivities  = new AllSportActivities();
        listCardAdapter.notifyDataSetChanged();
        databaseReference.setValue(allSportActivities);
        refreshAllCardioActivitiesDisplayInAdapter(Keys.DEFAULT_ALL_CARDIO_ACTIVITIES_VALUE);
        MySP.getInstance().putDouble(Keys.WEIGHT_KEY, Keys.DEFAULT_DOUBLE_VALUE);
        MySP.getInstance().putString(Keys.ALL_CARDIO_ACTIVITIES, Keys.DEFAULT_ALL_CARDIO_ACTIVITIES_VALUE);
        MySP.getInstance().putString(Keys.SPINNER_CHOICE, Keys.DEFAULT_SPINNER_CHOICE_VALUE);
        MySP.getInstance().putString(Keys.RADIO_HISTORY_CHOICE, Keys.DEFAULT_RADIO_BUTTONS_HISTORY_VALUE);
        MySP.getInstance().putDouble(Keys.WEIGHT_KEY, Keys.DEFAULT_DOUBLE_VALUE);
        MySP.getInstance().putBoolean(Keys.WEIGHT_WARNING, Keys.DEFAULT_VALUE_WEIGHT_WARNING);


    }
    private void initializeAdapter() {
        try {
            listCardAdapter = new ListCardAdapter(allSportActivities.getActivities(), this, lastDisplayChoice);
        }
        catch (Exception e) {

            // In the case that there are no recorded sport activities at all - displays no details
            listCardAdapter = new ListCardAdapter(new ArrayList<CardioActivity>(),  this, lastDisplayChoice);
        }

        history_LAY_recyclerview.setHasFixedSize(true);

    }

    private AllSportActivities getBundleFromMainMenu() {
        allSportActivities =  getIntent().getParcelableExtra(Keys.ALL_CARDIO_ACTIVITIES);
        if (allSportActivities  == null) {
            allSportActivities = new AllSportActivities(new ArrayList<CardioActivity>());
        }
        return allSportActivities;
    }

    private void refreshAllCardioActivitiesDisplayInAdapter(String radioChoiceValue) {
        /* Refresh the adapter when allSportsActivity is changed - when edit/deleted occurs */
        Log.d(TAG, "refreshAllCardioActivitiesDisplayInAdapter:\nradioChoiceValue " + radioChoiceValue + " chosenRadio " + chosenRadioButtonValue);
        listCardAdapter.setCardioActivities(Utils.getInstance().filterCardioActivitiesByType(allSportActivities.getActivities(), radioChoiceValue));
        listCardAdapter.notifyDataSetChanged();
        history_LAY_recyclerview.addItemDecoration(new DividerItemDecoration(history_LAY_recyclerview.getContext(),LinearLayoutManager.VERTICAL));
        history_LAY_recyclerview.setAdapter(listCardAdapter);
    }

    private void setAdapterViewOption(int viewType) {
        /* Change display of the adapter - list or card */
        MySP.getInstance().putInteger(Keys.HISTORY_VIEW_OPTION, viewType);
        listCardAdapter.setViewTypeRequset(viewType);
        listCardAdapter.notifyDataSetChanged();
        history_LAY_recyclerview.addItemDecoration(new DividerItemDecoration(history_LAY_recyclerview.getContext(),LinearLayoutManager.VERTICAL));
        history_LAY_recyclerview.setAdapter(listCardAdapter);
    }

    @Override
    public void onItemEdit(int position) {
        /* Saves the selected cardio activity in a variable, then uses it in onStart method after returning form Manual activity */
        Intent intent = new Intent(getApplicationContext(), Activity_Add_Manually.class);
        selectedToEdit = allSportActivities.getActivities().get(position);
        String currentActivityType = selectedToEdit.getCardioActivityType();
        MySP.getInstance().putString(Keys.RADIO_HISTORY_CHOICE_EDIT, currentActivityType);
        intent.putExtra(Keys.NEW_DATA_PACKAGE, selectedToEdit);
        startActivity(intent);
    }


    public void deleteItem(int position)    {
        Gson gson = new Gson();
        ArrayList <CardioActivity> activities = allSportActivities.getActivities();
        activities.remove(position);
        allSportActivities.setActivities(activities);
        listCardAdapter.notifyDataSetChanged();
        MySP.getInstance().putString(Keys.ALL_CARDIO_ACTIVITIES, gson.toJson(allSportActivities));
        databaseReference.setValue(allSportActivities);
    }

    private void createAlertDelete(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want to delete this record?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                deleteItem(position);
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onItemDelete(int position) {
        createAlertDelete(position);

    }
}