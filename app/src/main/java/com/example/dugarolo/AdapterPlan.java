package com.example.dugarolo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class AdapterPlan extends RecyclerView.Adapter<AdapterPlan.ViewHolder> {

    private ArrayList<String> places;
    private ArrayList<String> flows;
    private ArrayList<String> times;
    private LayoutInflater layoutInflater;
    private ItemClickListener mClickListener;

    public AdapterPlan(ArrayList<String> places, ArrayList<String> flows, ArrayList<String> times) {
        this.places = places;
        this.flows = flows;
        this.times = times;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.planning_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.place.setText( places.get(position));
        holder.time.setText(times.get(position));
        holder.flow.setText(flows.get(position));
        holder.finalPoint.setVisibility(View.GONE);
        holder.startPoint.setVisibility(View.GONE);
        holder.operationPlan.setVisibility(View.GONE);
        if (position == 0) {
            holder.startPoint.setVisibility(View.VISIBLE);
            holder.operationPlan.setVisibility(View.VISIBLE);
        }
        if (position == (getItemCount() - 1))
            holder.finalPoint.setVisibility(View.VISIBLE);


    }

    // total number of rows
    @Override
    public int getItemCount() {
        return places.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView place;
        TextView flow;
        TextView time;
        TextView operationPlan;
        ImageView startPoint;
        ImageView finalPoint;

        ViewHolder(View itemView) {
            super(itemView);
            place = itemView.findViewById(R.id.textViewPlace);
            flow= itemView.findViewById(R.id.textViewFlow);
            time = itemView.findViewById(R.id.textViewTime);
            operationPlan = itemView.findViewById(R.id.operationPlan);
            startPoint = itemView.findViewById(R.id.startPoint);
            finalPoint = itemView.findViewById(R.id.finalPoint);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return places.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}