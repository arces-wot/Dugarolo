package com.example.dugarolo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class TodayTab extends Fragment {
    public TodayTab() {
        // Required empty public constructor
    }

    public static TodayTab newInstance(ArrayList<Request> requests) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("list", requests);
        TodayTab todayTab = new TodayTab();
        todayTab.setArguments(bundle);
        return todayTab;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_today, container, false);
        assert getArguments() != null;
        ArrayList<Request> requests = getArguments().getParcelableArrayList("list");
        final RecyclerView recyclerView = root.findViewById(R.id.list_requests);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        RequestAdapter mAdapter = new RequestAdapter(requests);
        recyclerView.setAdapter(mAdapter);


        return root;
    }

    private class RequestAdapter extends RecyclerView.Adapter<TodayTab.RequestAdapter.RequestHolder> {

        private List<Request> requests;
        private Globals sharedData = Globals.getInstance();

        private class RequestHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


            public ImageView basicIcon;
            public TextView farmName;
            public TextView time;
            public TextView irrigationTime;


            @SuppressLint("ResourceType")
            public RequestHolder(View itemView) {
                super(itemView);
                basicIcon = itemView.findViewById(R.id.basic_icon);
                farmName = itemView.findViewById(R.id.farm_name);
                time = itemView.findViewById(R.id.time);
                irrigationTime = itemView.findViewById(R.id.irrigation_time);
                itemView.setOnClickListener(this);

            }

            @Override
            public void onClick(View v) {
                int position = getLayoutPosition();
                Intent intent = new Intent(Objects.requireNonNull(getActivity()).getApplication(), RequestDetailsActivity.class);
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
        public TodayTab.RequestAdapter.RequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.item_request, parent, false);
            return new TodayTab.RequestAdapter.RequestHolder(v);
        }


        @SuppressLint({"SetTextI18n", "ResourceType"})
        @Override
        public void onBindViewHolder(@NonNull TodayTab.RequestAdapter.RequestHolder holder, int position) {
            Request currentRequest = requests.get(position);
            //holder.farmColor.setImageResource(R.drawable.farm_color);
            String name = currentRequest.getName();
            DateTime dateTime = currentRequest.getDateTime();
            int color1=ResourcesCompat.getColor(getResources(),R.color.colorCompany3, null);
            int color2=ResourcesCompat.getColor(getResources(),R.color.colorCompany3, null); //sembra una variabile final perche' nel ciclo non viene modificata pur entrando nel if

            for (FarmColor f : sharedData.getFarmColors())
                if(f.getNameFarm().equalsIgnoreCase(name)){
                    color1= f.getIdColor();
                    color2= manipulateColor(f.getIdColor(),0.8f);// <<<<<<<<<<<<<<<<<<<<<<<<<<<<------------------------------------------------------------------------ QUA   PROBLEMA
                    break;
                }
            //funzioni importate da GIT
            VectorChildFinder vector;
            vector = new VectorChildFinder(getContext(),R.drawable.ic_farmercolor1, holder.basicIcon);
            VectorDrawableCompat.VFullPath path1 = vector.findPathByName("background"); // Path coinvolta con label background
            VectorDrawableCompat.VFullPath path2 = vector.findPathByName("backgroundShadow"); // Path coinvolta con label background
            path1.setFillColor(color1);    // funzione che colora la path
            path2.setFillColor(color2);    // funzione che colora la path


            //for (FarmColor f : sharedData.getFarmColors())
            // if(f.getNameFarm().equalsIgnoreCase(name))
                   /* if (name.equals("Bertacchini's Farm")) {
                        int bertacchini = ResourcesCompat.getColor(getResources(), R.color.colorCompany1, null);
                        //holder.farmColor.setColorFilter(bertacchini);
                        holder.basicIcon.setBackgroundResource(R.drawable.ic_farmercolor1);
                    } else if (name.equals("Ferrari's Farm")) {
                        int ferrari = ResourcesCompat.getColor(getResources(), R.color.colorCompany2, null);
                        //holder.farmColor.setColorFilter(ferrari);
                        holder.basicIcon.setBackgroundResource(R.drawable.ic_farmercolor2);
                    }*/


            DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm");
            String formattedDateTime = dateTime.toString(dtf);
            holder.farmName.setText(currentRequest.getName());
            holder.time.setText(getResources().getString(R.string.expected_time) + ": " + formattedDateTime);
            holder.irrigationTime.setText(getResources().getString(R.string.total_irrigation_time) + ": " + currentRequest.getWaterVolume());

        }

        public  int manipulateColor(int color, float factor) {
            int a = Color.alpha(color);
            int r = Math.round(Color.red(color) * factor);
            int g = Math.round(Color.green(color) * factor);
            int b = Math.round(Color.blue(color) * factor);
            return Color.argb(a,
                    Math.min(r,255),
                    Math.min(g,255),
                    Math.min(b,255));
        }


        @Override
        public int getItemCount() {

            return requests.size();
        }


    }


}