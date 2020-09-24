package com.example.running;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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


    interface OnItemClickListener {
        void onItemEdit(int position);
        void onItemDelete(int position);
    }



    class ListViewHolder extends RecyclerView.ViewHolder implements  OnClickListener{

        TextView mDate, mActivityType, mPace, mDuration, mDistance, mEdit, mDelete;
        OnItemClickListener listener;
        public ListViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mDate = itemView.findViewById(R.id.history_list_LBL_date);
            mActivityType = itemView.findViewById(R.id.history_list_LBL_type);
            mPace = itemView.findViewById(R.id.history_list_LBL_pace);
            mDuration = itemView.findViewById(R.id.history_list_LBL_duration);
            mDistance = itemView.findViewById(R.id.history_list_LBL_km);
            mEdit= itemView.findViewById(R.id.history_list_LBL_edit);
            mDelete= itemView.findViewById(R.id.history_list_LBL_delete);
            itemView.setOnClickListener(this);

            mEdit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemEdit(position);
                        }
                    }
                }
            });

            mDelete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemDelete(position);
                        }
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {

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
    private OnItemClickListener mListener;
    public ListCardAdapter(Context context){
        this.context=context;
    }

    public ListCardAdapter(ArrayList<CardioActivity> cardioActivities, OnItemClickListener listener, int viewTypeRequset) {
        this.context = context;
        this.mListener = listener;
        this.cardioActivities = cardioActivities;
        this.viewTypeRequset = viewTypeRequset;

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.mListener = onItemClickListener;
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
                return new ListViewHolder(view, mListener);
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
