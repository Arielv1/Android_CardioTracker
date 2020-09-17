package com.example.running;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.ArrayList;

public class Fragment_Detailed_Log extends Fragment {

    private ArrayList<CardioActivity> runs;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;




    public Fragment_Detailed_Log() {
    }

    public static Fragment_Detailed_Log newInstance() {
        return new Fragment_Detailed_Log();
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment__detailed__log, container, false);

        Gson gson  = new Gson();
        try {
            runs = gson.fromJson(MySP.getInstance().getString(Keys.ALL_CARDIO_ACTIVITIES, Keys.DEFAULT_ALL_CARDIO_ACTIVITIES_VALUE), AllSportActivities.class).getActivities();
        }
        catch (Exception e) {
            runs = new ArrayList<CardioActivity>();
        }


        Log.d("HistoryLogged", "In Detailed Log, runs is " + runs);
        Context context = view.getContext();

        // Loads recycler view where the players details will be put
        mRecyclerView = view.findViewById(R.id.detailed_log_recyclerview);
        mRecyclerView.setHasFixedSize(true);

        // Creates the actual recycler view adapter
        mAdapter = new MyItemRecyclerViewAdapter(runs);

        mRecyclerView.setAdapter(mAdapter);

        return view;
    }
}