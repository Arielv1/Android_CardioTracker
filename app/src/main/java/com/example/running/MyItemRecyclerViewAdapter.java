package com.example.running;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Arrays;
import java.util.List;

public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<CardioActivity> mValues;

    public MyItemRecyclerViewAdapter(List<CardioActivity> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_history_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        CardioActivity cardioActivity = mValues.get(position);
        holder.mDate.setText(cardioActivity.getDate());
        holder.mTimeCreated.setText(cardioActivity.getCreatedTimestamp()+"");
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mDate;
        public final TextView mTimeCreated;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mDate = (TextView) view.findViewById(R.id.fragment_detailed_log_LBL_date);
            mTimeCreated = (TextView) view.findViewById(R.id.fragment_detailed_log_time_created);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + "'";
        }
    }
}