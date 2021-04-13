package com.example.dugarolo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

    public class HistoryTab extends Fragment {
        public static ArrayList<Request> requests;
        private static int instance = 0;
        private static com.example.dugarolo.HistoryTab historyTab;
        private static com.example.dugarolo.HistoryTab.RequestAdapter mAdapter;
        private static RecyclerView recyclerView;
        private TextView dateText;
        private MyMapView map;
        private MyMapView map2;
        private MyMapView historyMap;
        private static TextView resultView;
        private static DateTime selectedDateTab;
        private static int resultSize = 0;

        public HistoryTab() {
            // Required empty public constructor
        }

        public static com.example.dugarolo.HistoryTab newInstance(ArrayList<Request> requests, MyMapView map, MyMapView map2, MyMapView historyMap) {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("list", requests);
            bundle.putParcelable("Mappa", map);
            bundle.putParcelable("Mappa2", map2);
            bundle.putParcelable("MappaStorico", historyMap);
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
            selectedDateTab = null;
            requests = getArguments().getParcelableArrayList("list");
            map = getArguments().getParcelable("Mappa");
            map2 = getArguments().getParcelable("Mappa2");
            historyMap = getArguments().getParcelable("MappaStorico");
            Collections.sort(requests,new Request.SortByChannel());
            recyclerView = root.findViewById(R.id.list_requests);
            resultView = root.findViewById(R.id.resultView);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            mAdapter = new com.example.dugarolo.HistoryTab.RequestAdapter(requests);
            recyclerView.setAdapter(mAdapter);
            resultView.setText("Selezionare una data");

            return root;
        }

        public class RequestAdapter extends RecyclerView.Adapter<HistoryTab.RequestAdapter.RequestHolder> {

            public List<Request> requests;
            public Globals sharedData = Globals.getInstance();


            public class RequestHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

                public TextView farmName, irrigationTime, time, selectedDateView;
                public TextView canalName, nAppezamento, mapsText;
                public ImageView mapsImage, basicIcon;
                public Button mapsArea;
                public View lineView;
                public TextView textDatePicker;
                public ImageView arrowImage;

                LinearLayout expandibleView = null;
                CardView cardView;


                @SuppressLint("ResourceType")
                public RequestHolder(View itemView) {
                    super(itemView);
                    basicIcon = itemView.findViewById(R.id.basic_iconH);
                    farmName = itemView.findViewById(R.id.farm_nameH);
                    time = itemView.findViewById(R.id.timeH);
                    irrigationTime = itemView.findViewById(R.id.irrigation_timeH);
                    canalName = itemView.findViewById(R.id.canal_nameH);
                    nAppezamento = itemView.findViewById(R.id.nAppezamentoH);
                    mapsText = itemView.findViewById(R.id.mapsTextH);
                    mapsImage = itemView.findViewById(R.id.mapsImageH);
                    mapsArea = itemView.findViewById(R.id.areaMapsH);
                    selectedDateView = itemView.findViewById(R.id.areaMapsH);
                    lineView = itemView.findViewById(R.id.lineView);
                    textDatePicker = itemView.findViewById(R.id.PickDate);
                    arrowImage = itemView.findViewById(R.id.arrowImage);

                    itemView.setOnClickListener(this);

                }

                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition();
                    Intent intent = new Intent(Objects.requireNonNull(getActivity()).getApplication(), TodayRequestDetailsActivity.class);
                    intent.putParcelableArrayListExtra("REQUEST_LIST", (ArrayList<? extends Parcelable>) requests);
                    intent.putExtra("REQUEST_CLICKED", position);
                    int requestId = position;
                    startActivity(intent);
                }
            }

            RequestAdapter(List<Request> requests) {
                this.requests = requests;
            }

            public List<Request> getRequests() {
                return this.requests;
            }

            @NonNull
            @Override
            public HistoryTab.RequestAdapter.RequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View v = inflater.inflate(R.layout.item_request_history, parent, false);
                return new HistoryTab.RequestAdapter.RequestHolder(v);
            }


            @SuppressLint({"SetTextI18n", "ResourceType"})
            @Override
            public void onBindViewHolder(@NonNull final HistoryTab.RequestAdapter.RequestHolder holder, final int position) {
                final Request currentRequest = requests.get(position);

                if(!currentRequest.getName().equalsIgnoreCase("DATE")) {
                    String name = currentRequest.getName();
                    DateTime dateTime = currentRequest.getDateTime();
                    int color1 = ResourcesCompat.getColor(getResources(), R.color.colorCompany3, null);
                    int color2 = ResourcesCompat.getColor(getResources(), R.color.colorCompany3, null);


                    for (FarmColor f : sharedData.getFarmColors())
                        if (f.getNameFarm().equals(name)) {
                            color1 = f.getIdColor();
                            color2 = manipulateColor(f.getIdColor(), 0.8f);
                            break;
                        }

                    boolean isExpanded = requests.get(position).getIsExpanded();

                    buildIconFarmer(currentRequest, holder.basicIcon);
                    DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm");
                    String formattedDateTime = dateTime.toString(dtf);

                    String farmerNameToModify = currentRequest.getName();

                    String[] partsName = farmerNameToModify.split("/");
                    String[] partsName1 = partsName[4].split("_");
                    String finalId = partsName1[1];

                    holder.farmName.setText("Farm id: " + finalId);
                    holder.time.setText(getResources().getString(R.string.expected_time) + ": " + formattedDateTime);
                    holder.irrigationTime.setText(getResources().getString(R.string.total_irrigation_time) + ": " + currentRequest.getWaterVolume());

                    holder.canalName.setText(currentRequest.getNameChannel());

                    String idFieldNameToModify = currentRequest.getField().getId();

                    String[] partsField = idFieldNameToModify.split("/");
                    String[] partsField1 = partsField[4].split("_");
                    String finalField = partsField1[1];

                    holder.nAppezamento.setText("Appezzamento n°" + finalField);


                    holder.mapsArea.setOnClickListener(new View.OnClickListener() {
                        //@Override
                        public void onClick(View v) {
                            centerMapWithPosition(currentRequest);
                        }
                    });
                }
                else{
                    holder.basicIcon.setVisibility(View.INVISIBLE);
                    holder.farmName.setVisibility(View.INVISIBLE);
                    holder.time.setVisibility(View.INVISIBLE);
                    holder.irrigationTime.setVisibility(View.INVISIBLE);
                    holder.canalName.setVisibility(View.INVISIBLE);
                    holder.nAppezamento.setVisibility(View.INVISIBLE);
                    holder.mapsText.setVisibility(View.INVISIBLE);
                    holder.mapsImage.setVisibility(View.INVISIBLE);
                    holder.mapsArea.setVisibility(View.INVISIBLE);
                    holder.lineView.setVisibility(View.INVISIBLE);
                    holder.selectedDateView.setVisibility(View.VISIBLE);
                    holder.selectedDateView.setText(currentRequest.getDateTime().toString());
                }


            }

            @Override
            public int getItemCount() {
                return requests.size();
            }


            public void centerMapWithPosition(Request currentRequest) {

                Field f = currentRequest.getField();
                ArrayList<GeoPoint> area = f.getArea();
                GeoPoint geoPoint = area.get(0);

                IMapController mapController = map.getController();
                IMapController mapController2 = map2.getController();
                IMapController mapHistoryController = historyMap.getController();
                mapController.setZoom(14.5);
                mapController.animateTo(geoPoint, 14.5, (long) 500);
                mapController2.setZoom(14.5);
                mapController2.animateTo(geoPoint, 14.5, (long) 500);
                mapHistoryController.setZoom(14.5);
                mapHistoryController.animateTo(geoPoint, 14.5, (long) 500);
            }

        }


        public static void setChanged(ArrayList<Request> req, DateTime selectedDate) {
            requests.clear();
            requests.addAll(req);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String date = dateFormat.format(selectedDate.toDate());
            resultView.setText("Data: " + date + "\nRisultati: " + req.size());
            selectedDateTab = selectedDate;
            resultSize = req.size();

            mAdapter.notifyDataSetChanged();
            if (req.size() == 0)
                recyclerView.setVisibility(View.INVISIBLE);
            else
                recyclerView.setVisibility(View.VISIBLE);
        }


        public void buildIconFarmer(Request currentRequest, ImageView basicIcon) {
            int color1 = getColorByStatus(currentRequest);
            int color2 = manipulateColor(color1, 0.8f);


            VectorChildFinder vector;
            if (currentRequest.getType().equalsIgnoreCase("CBEC")) {
                vector = new VectorChildFinder(getContext(), R.drawable.ic_farmercolor, basicIcon);
                VectorDrawableCompat.VFullPath path1 = vector.findPathByName("background");
                VectorDrawableCompat.VFullPath path2 = vector.findPathByName("backgroundShadow");
                path1.setFillColor(color1);
                path2.setFillColor(color2);
            } else if (currentRequest.getType().equalsIgnoreCase("criteria")) {
                vector = new VectorChildFinder(getContext(), R.drawable.ic_robo_swamp, basicIcon);
                VectorDrawableCompat.VFullPath path1 = vector.findPathByName("background");
                VectorDrawableCompat.VFullPath path2 = vector.findPathByName("backgroundShadow");
                VectorDrawableCompat.VFullPath path3 = vector.findPathByName("backgroundShadow2");
                path1.setFillColor(color1);
                path2.setFillColor(color2);
                path3.setFillColor(color2);
            }

        }
        private int getColorByStatus(Request r) {
            if (r.getStatus().equalsIgnoreCase("Accepted"))
                return getResources().getColor(R.color.acceptedColor);
            else if (r.getStatus().equalsIgnoreCase("Scheduled"))
                return getResources().getColor(R.color.scheduledColor);
            else if (r.getStatus().equalsIgnoreCase("Ongoing"))
                return getResources().getColor(R.color.ongoingColor);
            else if (r.getStatus().equalsIgnoreCase("Interrupted"))
                return getResources().getColor(R.color.interruptedColor);
            else
                return getResources().getColor(R.color.colorOtherStatus);

        }

        public int manipulateColor(int color, float factor) {
            int a = Color.alpha(color);
            int r = Math.round(Color.red(color) * factor);
            int g = Math.round(Color.green(color) * factor);
            int b = Math.round(Color.blue(color) * factor);
            return Color.argb(a,
                    Math.min(r, 255),
                    Math.min(g, 255),
                    Math.min(b, 255));
        }
}