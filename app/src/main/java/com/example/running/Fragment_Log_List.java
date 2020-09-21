package com.example.running;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.ArrayList;

public class Fragment_Log_List extends Fragment {

    private ArrayList<CardioActivity> runs;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    public Fragment_Log_List() {
    }

    public static Fragment_Log_List newInstance() {

        return new Fragment_Log_List();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_log_list, container, false);

        Gson gson  = new Gson();
        try {
            runs = gson.fromJson(MySP.getInstance().getString(Keys.ALL_CARDIO_ACTIVITIES, Keys.DEFAULT_ALL_CARDIO_ACTIVITIES_VALUE), AllSportActivities.class).getActivities();
        }
        catch (Exception e) {
            runs = new ArrayList<CardioActivity>();
        }

        Context context = view.getContext();

        String lastChoice = MySP.getInstance().getString(Keys.RADIO_HISTORY_CHOICE, Keys.DEFAULT_RADIO_BUTTONS_HISTORY_VALUE);
        ArrayList<CardioActivity> filteredByRadioChoice = Utils.getInstance().filterCardioActivitiesByType(runs, lastChoice);
        // Loads recycler view where the players details will be put
        mRecyclerView = view.findViewById(R.id.detailed_log_recyclerview_list);
        mRecyclerView.setHasFixedSize(true);

        // Creates the actual recycler view adapter
        mAdapter = new MyItemRecyclerViewAdapter(filteredByRadioChoice);

        mRecyclerView.setAdapter(mAdapter);

        return view;
    }
}