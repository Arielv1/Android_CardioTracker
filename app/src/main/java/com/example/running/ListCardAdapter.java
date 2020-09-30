package com.example.running;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class ListCardAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder> {


    interface OnItemClickListener {
        void onItemEdit(int position);
        void onItemDelete(int position);
    }

    class ListViewHolder extends RecyclerView.ViewHolder {

        TextView mDate, mActivityType, mPace, mDuration, mDistance, mCalories;
        ImageView mEdit, mDelete;

        OnItemClickListener listener;

        public ListViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mDate = itemView.findViewById(R.id.history_list_LBL_date);
            mActivityType = itemView.findViewById(R.id.history_list_LBL_type);
            mPace = itemView.findViewById(R.id.history_list_LBL_pace);
            mDuration = itemView.findViewById(R.id.history_list_LBL_duration);
            mDistance = itemView.findViewById(R.id.history_list_LBL_distance);
            mEdit= itemView.findViewById(R.id.list_IMG_edit);
            mDelete= itemView.findViewById(R.id.list_IMG_delete);
            mCalories = itemView.findViewById(R.id.history_list_LBL_calories);

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
    }

    class CardViewHodler extends RecyclerView.ViewHolder {

        TextView mDate, mDuration, mPace, mDistance, mCalories, mStartTime, mEndTime;
        ImageView mType;
        MaterialButton mEdit, nDelete;


        public CardViewHodler(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mDate =itemView.findViewById(R.id.card_LBL_date);
            mDuration = itemView.findViewById(R.id.card_LBL_duration);
            mDistance = itemView.findViewById(R.id.card_LBL_distance);
            mPace =  itemView.findViewById(R.id.card_LBL_pace);
            mCalories = itemView.findViewById(R.id.card_LBL_calories);
            mStartTime = itemView.findViewById(R.id.card_LBL_start_time);
            mEndTime = itemView.findViewById(R.id.card_LBL_end_time);
            mType = itemView.findViewById(R.id.card_IMG_type);
            mEdit =  itemView.findViewById(R.id.card_IMG_edit);
            nDelete = itemView.findViewById(R.id.card_IMG_delete);

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
            nDelete.setOnClickListener(new OnClickListener() {
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
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_card, parent, false);
                return new CardViewHodler(view, mListener);
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
                ((ListViewHolder) holder).mActivityType.setText(cardioActivity.getCardioActivityType());
                ((ListViewHolder) holder).mDate.setText(cardioActivity.getDate());
                ((ListViewHolder) holder).mDistance.setText(cardioActivity.getDistance()+"");
                ((ListViewHolder) holder).mPace.setText(cardioActivity.getPace()+"");
                ((ListViewHolder) holder).mCalories.setText(cardioActivity.getCaloriesBurned()+"");
                ((ListViewHolder) holder).mDuration.setText(cardioActivity.getDuration());
                break;
            case Utils.AdapterViewOptions.CARD:
                ((CardViewHodler)holder).mDate.setText(cardioActivity.getDate());
                ((CardViewHodler)holder).mDistance.setText(cardioActivity.getDistance()+"");
                ((CardViewHodler)holder).mPace.setText(cardioActivity.getPace()+"");
                ((CardViewHodler) holder).mCalories.setText(cardioActivity.getCaloriesBurned()+"");
                ((CardViewHodler)holder).mDuration.setText(cardioActivity.getDuration());
                ((CardViewHodler)holder).mStartTime.setText(cardioActivity.getTimeStart());
                ((CardViewHodler)holder).mEndTime.setText(cardioActivity.getTimeEnd());

                if (cardioActivity.getCardioActivityType().equals(Utils.CardioActivityTypes.JOGGING)) {
                    ((CardViewHodler)holder).mType.setImageResource(R.drawable.ic_jogging);
                }
                else if (cardioActivity.getCardioActivityType().equals(Utils.CardioActivityTypes.RUNNING)) {
                    ((CardViewHodler)holder).mType.setImageResource(R.drawable.ic_running);
                }
                else {
                    ((CardViewHodler)holder).mType.setImageResource(R.drawable.ic_cycling);
                }
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

