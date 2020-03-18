package com.example.dugarolo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TomorrowTab extends Fragment {

    public TomorrowTab() {
        // Required empty public constructor
    }

    public static TomorrowTab newInstance(ArrayList<Request> requests) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("list", requests);
        TomorrowTab tomorrowTab = new TomorrowTab();
        tomorrowTab.setArguments(bundle);
        return tomorrowTab;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_tomorrow, container, false);
        assert getArguments() != null;
        ArrayList<Request> requests = getArguments().getParcelableArrayList("list");
        final RecyclerView recyclerView = root.findViewById(R.id.list_requests);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        RequestAdapter mAdapter = new RequestAdapter(requests);
        recyclerView.setAdapter(mAdapter);

        return root;
    }

    private class RequestAdapter extends RecyclerView.Adapter<TomorrowTab.RequestAdapter.RequestHolder> {

        private List<Request> requests;
        private Globals sharedData = Globals.getInstance();

        private class RequestHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


            public ImageView basicIcon;
            public TextView farmName;
            public TextView time;
            public TextView irrigationTime;
            public ImageView check;


            @SuppressLint("ResourceType")
            public RequestHolder(View itemView) {
                super(itemView);
                basicIcon = itemView.findViewById(R.id.basic_icon);
                farmName = itemView.findViewById(R.id.farm_name);
                time = itemView.findViewById(R.id.time);
                irrigationTime = itemView.findViewById(R.id.irrigation_time);
                check = itemView.findViewById(R.id.check);
                itemView.setOnClickListener(this);

            }

            @Override
            public void onClick(View v) {
                int position = getLayoutPosition();
                Intent intent = new Intent(Objects.requireNonNull(getActivity()).getApplication(), InfoTomorrowRequest.class);
                intent.putParcelableArrayListExtra("REQUEST_LIST", (ArrayList<? extends Parcelable>) requests);
                intent.putExtra("REQUEST_CLICKED", position);
                int requestId = position;
                startActivity(intent);
            }
        }

        RequestAdapter(List<Request> requests) {
            this.requests = requests;
        }

        @NonNull
        @Override
        public TomorrowTab.RequestAdapter.RequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.item_request, parent, false);
            return new TomorrowTab.RequestAdapter.RequestHolder(v);
        }


        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull TomorrowTab.RequestAdapter.RequestHolder holder, int position) {
            Request currentRequest = requests.get(position);
            //holder.farmColor.setImageResource(R.drawable.farm_color);
            String name = currentRequest.getName();
            DateTime dateTime = currentRequest.getDateTime();
            //for (FarmColor f : sharedData.getFarmColors())
            // if(f.getNameFarm().equalsIgnoreCase(name))
            if (name.equals("Bertacchini's Farm")) {
                int bertacchini = ResourcesCompat.getColor(getResources(), R.color.colorCompany1, null);
                //holder.farmColor.setColorFilter(bertacchini);
                holder.basicIcon.setBackgroundResource(R.drawable.ic_farmercolor1);
            } else if (name.equals("Ferrari's Farm")) {
                int ferrari = ResourcesCompat.getColor(getResources(), R.color.colorCompany2, null);
                //holder.farmColor.setColorFilter(ferrari);
                holder.basicIcon.setBackgroundResource(R.drawable.ic_farmercolor2);
            }


            DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm");
            String formattedDateTime = dateTime.toString(dtf);
            holder.farmName.setText(currentRequest.getName());
            holder.time.setText(getResources().getString(R.string.expected_time) + ": " + formattedDateTime);
            holder.irrigationTime.setText(getResources().getString(R.string.total_irrigation_time) + ": " + currentRequest.getWaterVolume());

            if(currentRequest.getStatus() == "Scheduled"){
                holder.check.setVisibility(View.INVISIBLE);
            }else{
                holder.check.setBackgroundResource(R.drawable.check);
                holder.check.setVisibility(View.VISIBLE);
            }

        }


        @Override
        public int getItemCount() {

            return requests.size();
        }


    }


}