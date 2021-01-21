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
import com.example.dugarolo.R;
import com.example.dugarolo.Request;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

    public class HistoryTab extends Fragment {
        public static List<Request> requests;
        private static int instance = 0;
        private static com.example.dugarolo.HistoryTab historyTab;
        private static com.example.dugarolo.HistoryTab.RequestAdapter mAdapter;
        private static RecyclerView recyclerView;
        private static TextView textDatePicker;
        private static ImageView arrowImage;
        public HistoryTab() {
            // Required empty public constructor
        }

        public static com.example.dugarolo.HistoryTab newInstance(ArrayList<Request> requests) {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("list", requests);
            if(instance == 0) {
                historyTab = new com.example.dugarolo.HistoryTab();
                historyTab.setArguments(bundle);
                instance++;
            }

            return historyTab;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View root = inflater.inflate(R.layout.history_tab, container, false);
            assert getArguments() != null;
            requests = getArguments().getParcelableArrayList("list");
            //requestsList = requests;
            Collections.sort(requests,new Request.SortByChannel());
            recyclerView = root.findViewById(R.id.list_requests);
            textDatePicker = root.findViewById(R.id.PickDate);
            arrowImage = root.findViewById(R.id.arrowImage);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            mAdapter = new com.example.dugarolo.HistoryTab.RequestAdapter(requests);
            recyclerView.setAdapter(mAdapter);
            recyclerView.setVisibility(View.GONE);
            textDatePicker.setVisibility(View.VISIBLE);

            return root;
        }

        private class RequestAdapter extends RecyclerView.Adapter<com.example.dugarolo.HistoryTab.RequestAdapter.RequestHolder> {

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
                    //check = itemView.findViewById(R.id.check);
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
            public com.example.dugarolo.HistoryTab.RequestAdapter.RequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View v = inflater.inflate(R.layout.item_request_tomorrow, parent, false);
                return new com.example.dugarolo.HistoryTab.RequestAdapter.RequestHolder(v);
            }


            @SuppressLint("SetTextI18n")
            @Override
            public void onBindViewHolder(@NonNull com.example.dugarolo.HistoryTab.RequestAdapter.RequestHolder holder, int position) {
                Request currentRequest = requests.get(position);
                //holder.farmColor.setImageResource(R.drawable.farm_color);
                String name = currentRequest.getName();
                DateTime dateTime = currentRequest.getDateTime();
                int color1= ResourcesCompat.getColor(getResources(),R.color.colorCompany3, null);
                int color2=ResourcesCompat.getColor(getResources(),R.color.colorCompany3, null);

                for (FarmColor f : sharedData.getFarmColors())
                    if(f.getNameFarm().equalsIgnoreCase(name)){
                        color1= f.getIdColor();
                        color2= manipulateColor(f.getIdColor(),0.8f);
                        break;
                    }
                //funzioni importate da GIT
                VectorChildFinder vector;
                vector = new VectorChildFinder(getContext(),R.drawable.ic_farmercolor, holder.basicIcon);
                VectorDrawableCompat.VFullPath path1 = vector.findPathByName("background");
                VectorDrawableCompat.VFullPath path2 = vector.findPathByName("backgroundShadow");
                path1.setFillColor(color1);
                path2.setFillColor(color2);

                DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm");
                String formattedDateTime = dateTime.toString(dtf);

                String farmerNameToModify = currentRequest.getName();

                String[] partsName = farmerNameToModify.split("/");
                String[] partsName1 = partsName[4].split("_");
                String finalId = partsName1[1];

                String canalName = currentRequest.getNameChannel();

                holder.farmName.setText(canalName + "-" + finalId);

                holder.time.setText(getResources().getString(R.string.expected_time) + ": " + formattedDateTime);
                holder.irrigationTime.setText(getResources().getString(R.string.total_irrigation_time) + ": " + currentRequest.getWaterVolume());

                //CHECK CONTROL
            /*if(currentRequest.getStatus() == "Scheduled"){
                holder.check.setVisibility(View.INVISIBLE);
            }else{
                holder.check.setBackgroundResource(R.drawable.check);
                holder.check.setVisibility(View.VISIBLE);
            }*/

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
        public static void setChanged(ArrayList<Request> req) {
            requests.clear();
            requests.addAll(req);

            mAdapter.notifyDataSetChanged();
            if (req.size() == 0) {
                recyclerView.setVisibility(View.INVISIBLE);
                textDatePicker.setVisibility(View.VISIBLE);
                arrowImage.setVisibility(View.VISIBLE);
            }
            else{
                recyclerView.setVisibility(View.VISIBLE);
                textDatePicker.setVisibility(View.GONE);
                arrowImage.setVisibility(View.GONE);
            }
        }
}