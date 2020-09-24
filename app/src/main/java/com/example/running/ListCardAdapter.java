package com.example.running;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;

public class ListCardAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder> {

    class ListViewHolder extends RecyclerView.ViewHolder {

        TextView mDate, mActivityType, mPace, mDuration, mDistance, mEdit, mDelete;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            mDate = itemView.findViewById(R.id.history_list_LBL_date);
            mActivityType = itemView.findViewById(R.id.history_list_LBL_type);
            mPace = itemView.findViewById(R.id.history_list_LBL_pace);
            mDuration = itemView.findViewById(R.id.history_list_LBL_duration);
            mDistance = itemView.findViewById(R.id.history_list_LBL_km);
            mEdit= itemView.findViewById(R.id.history_list_LBL_edit);
            mDelete= itemView.findViewById(R.id.history_list_LBL_delete);
        }
    }

    class CardViewHodler extends RecyclerView.ViewHolder {

        TextView lower_text, upper_text;
        ImageView card_image;

        public CardViewHodler(@NonNull View itemView) {
            super(itemView);
            lower_text = (TextView) itemView.findViewById(R.id.card_lower_text);
            upper_text = (TextView) itemView.findViewById(R.id.card_upper_text);
            card_image = (ImageView) itemView.findViewById(R.id.cardview_image);
        }
    }



    private ArrayList<CardioActivity> cardioActivities;
    private int viewTypeRequset;
    private Context context;

    public ListCardAdapter(Context context){
        this.context=context;
    }

    public ListCardAdapter(ArrayList<CardioActivity> cardioActivities, int viewTypeRequset, Context context) {
        this.cardioActivities = cardioActivities;
        this.viewTypeRequset = viewTypeRequset;
        this.context = context;
    }

    public void setCardioActivities(ArrayList<CardioActivity> cardioActivities) {
        this.cardioActivities = cardioActivities;
    }

    public void setViewTypeRequset(int viewTypeRequset) {
        this.viewTypeRequset = viewTypeRequset;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewTypeRequset) {
            case Utils.AdapterViewOptions.LIST:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_list, parent, false);
                return new ListViewHolder(view);
            case Utils.AdapterViewOptions.CARD:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_log_card, parent, false);
                return new CardViewHodler(view);
            default:
                break;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final CardioActivity cardioActivity = cardioActivities.get(position);

        switch (viewTypeRequset) {
            case Utils.AdapterViewOptions.LIST:
                ((ListViewHolder) holder).mDate.setText(cardioActivity.getDate());
                ((ListViewHolder) holder).mActivityType.setText(cardioActivity.getCardioActivityType());
                ((ListViewHolder) holder).mDistance.setText(cardioActivity.getDistance()+"");
                ((ListViewHolder) holder).mPace.setText(cardioActivity.getPace()+"");
                ((ListViewHolder) holder).mDuration.setText(cardioActivity.getDuration()+"");
                ((ListViewHolder) holder).mEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, Activity_Add_Manually.class);
                        intent.putExtra(Keys.NEW_DATA_PACKAGE, cardioActivity);
                        context.startActivity(intent);
                    }
                });
                ((ListViewHolder) holder).mDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cardioActivities.remove(cardioActivity);
                        notifyDataSetChanged();

                        Gson gson = new Gson();
                        AllSportActivities allSportActivities = gson.fromJson(MySP.getInstance()
                                .getString(Keys.ALL_CARDIO_ACTIVITIES, Keys.DEFAULT_ALL_CARDIO_ACTIVITIES_VALUE), AllSportActivities.class);
                        allSportActivities.setActivities(cardioActivities);
                        MySP.getInstance().putString(Keys.ALL_CARDIO_ACTIVITIES, gson.toJson(allSportActivities));
                    }
                });
               break;
            case Utils.AdapterViewOptions.CARD:
                ((CardViewHodler)holder).upper_text.setText("Changed Upper Text");
                ((CardViewHodler)holder).lower_text.setText("Changed Lower Text");
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return cardioActivities.size();
    }


}
