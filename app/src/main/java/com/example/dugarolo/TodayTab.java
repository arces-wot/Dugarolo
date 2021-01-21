package com.example.dugarolo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
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
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TodayTab extends Fragment {

    MyMapView map;
    MyMapView map2;
    static RequestAdapter mAdapter;
    static ArrayList<Request> requests=new ArrayList<>() ;
    static ArrayList<Request> totalRequests;

    public TodayTab() {
        // Required empty public constructor
    }

    public static TodayTab newInstance(ArrayList<Request> requests, MyMapView map, MyMapView map2) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("list", requests);
        bundle.putParcelable("Mappa", map);
        bundle.putParcelable("Mappa2", map2);
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
        totalRequests = getArguments().getParcelableArrayList("list");
        requests=unsatisfiedRequests(totalRequests);
        final RecyclerView recyclerView = root.findViewById(R.id.list_requests);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new RequestAdapter(requests);
        recyclerView.setAdapter(mAdapter);
        map = getArguments().getParcelable("Mappa");
        map2 = getArguments().getParcelable("Mappa2");

        return root;

    }

    static public ArrayList<Request> unsatisfiedRequests(ArrayList<Request> totalRequests) {
        ArrayList<Request> requests = new ArrayList<Request>();
        for(Request r : totalRequests)
            if(!r.getStatus().equalsIgnoreCase("Satisfied") && !r.getStatus().equalsIgnoreCase("Cancelled"))
            requests.add(r);
            return requests;
    }

    static public void setChanged(ArrayList<Request> req) {
        DateTime now;
        now = DateTime.now();
        requests.clear();
        //requests.addAll(req);

        for (Request r : req) {
            if (((r.getDateTime().getDayOfYear()) == (now.getDayOfYear()))
                    && !r.getStatus().equals("Satisfied") && !r.getStatus().equals("Cancelled")
                    && ((r.getDateTime().getYear()) == (now.getYear()))) {
                requests.add(r);

            }
        }

        mAdapter.notifyDataSetChanged();
    }


    public class RequestAdapter extends RecyclerView.Adapter<TodayTab.RequestAdapter.RequestHolder> {

        public List<Request> requests;
        public Globals sharedData = Globals.getInstance();


        public class RequestHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public TextView farmName, irrigationTime, time, completeText;
            public TextView canalName, nAppezamento, cancelText, mapsText, playText, pauseText;
            public ImageView statusWaitingImage, playImage, mapsImage, cancelImage, pauseImage, basicIcon,
                    statusOperatingImage, collapse, uncollapse, completeImage;
            public Button playArea, mapsArea, deleteArea, collapsing;

            LinearLayout expandibleView = null;
            CardView cardView;


            @SuppressLint("ResourceType")
            public RequestHolder(View itemView) {
                super(itemView);
                playArea = itemView.findViewById(R.id.areaPlay);
                mapsArea = itemView.findViewById(R.id.areaMaps);
                deleteArea = itemView.findViewById(R.id.areaDelete);

                basicIcon = itemView.findViewById(R.id.basic_icon);
                farmName = itemView.findViewById(R.id.farm_name);
                time = itemView.findViewById(R.id.time);
                irrigationTime = itemView.findViewById(R.id.irrigation_time);
                canalName = itemView.findViewById(R.id.canal_name);
                nAppezamento = itemView.findViewById(R.id.nAppezamento);
                cancelText = itemView.findViewById(R.id.deleteText);
                mapsText = itemView.findViewById(R.id.mapsText);
                playText = itemView.findViewById(R.id.playText);
                playImage = itemView.findViewById(R.id.playImage);
                mapsImage = itemView.findViewById(R.id.mapsImage);
                cancelImage = itemView.findViewById(R.id.deleteImage);
                pauseImage = itemView.findViewById(R.id.pauseImage);
                pauseText = itemView.findViewById(R.id.pauseText);
                statusWaitingImage = itemView.findViewById(R.id.statusWaitingImage);
                statusOperatingImage = itemView.findViewById(R.id.statusOperatingImage);

                completeText = itemView.findViewById(R.id.completeText);
                completeImage = itemView.findViewById(R.id.checkImage);

                collapse = itemView.findViewById(R.id.collapse);
                collapsing = itemView.findViewById(R.id.collapsing);
                uncollapse = itemView.findViewById(R.id.uncollapse);
                expandibleView = itemView.findViewById(R.id.collapsedArea1);

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
        public TodayTab.RequestAdapter.RequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.item_request_today, parent, false);
            return new TodayTab.RequestAdapter.RequestHolder(v);
        }


        @SuppressLint({"SetTextI18n", "ResourceType"})
        @Override
        public void onBindViewHolder(@NonNull final TodayTab.RequestAdapter.RequestHolder holder, final int position) {
            final Request currentRequest = requests.get(position);
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
            holder.expandibleView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

            /*if(isExpanded == false){
                holder.uncollapse.setVisibility(View.VISIBLE);
                holder.collapse.setVisibility(View.INVISIBLE);
            }else{
                holder.collapse.setVisibility(View.VISIBLE);
                holder.uncollapse.setVisibility(View.INVISIBLE);
            }*/

            holder.uncollapse.setVisibility(View.INVISIBLE);
            holder.collapse.setVisibility(View.INVISIBLE);

            /*for (FarmColor f : sharedData.getFarmColors())
                if(f.getNameFarm().equalsIgnoreCase(name)){
                    color1= f.getIdColor();
                    color2= manipulateColor(f.getIdColor(),0.8f);
                    break;
                }*/

            buildIconFarmer(currentRequest, holder.basicIcon);
            /*color1 = getColorByStatus(currentRequest);
            color2 = manipulateColor(color1, 0.8f);


            VectorChildFinder vector;
            if (currentRequest.getType().equals("CBEC")) {
                vector = new VectorChildFinder(getContext(), R.drawable.ic_farmercolor, holder.basicIcon);
                VectorDrawableCompat.VFullPath path1 = vector.findPathByName("background");
                VectorDrawableCompat.VFullPath path2 = vector.findPathByName("backgroundShadow");
                path1.setFillColor(color1);
                path2.setFillColor(color2);
            } else if (currentRequest.getType().equals("criteria")) {
                vector = new VectorChildFinder(getContext(), R.drawable.ic_robo_swamp, holder.basicIcon);
                VectorDrawableCompat.VFullPath path1 = vector.findPathByName("background");
                VectorDrawableCompat.VFullPath path2 = vector.findPathByName("backgroundShadow");
                VectorDrawableCompat.VFullPath path3 = vector.findPathByName("backgroundShadow2");
                path1.setFillColor(color1);
                path2.setFillColor(color2);
                path3.setFillColor(color2);
            }*/

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

            if (currentRequest.getStatus().equals("Accepted")
                    || currentRequest.getStatus().equals("Scheduled")
                    || currentRequest.getStatus().equals("Interrupted")) {
                //Log.d("ProvaEx", currentRequest.getChannel() + currentRequest.getStatus());
                holder.playText.setVisibility(View.VISIBLE);
                holder.playImage.setVisibility(View.VISIBLE);

                holder.pauseText.setVisibility(View.INVISIBLE);
                holder.pauseImage.setVisibility(View.INVISIBLE);

                holder.statusWaitingImage.setVisibility(View.VISIBLE);
                holder.statusOperatingImage.setVisibility(View.INVISIBLE);

                holder.cancelText.setVisibility(View.VISIBLE);
                holder.cancelImage.setVisibility(View.VISIBLE);

                holder.completeText.setVisibility(View.GONE);
                holder.completeImage.setVisibility(View.GONE);
            } else if (currentRequest.getStatus().equals("Ongoing")
                    || currentRequest.getStatus().equals("2")) {
                //Log.d("ProvaEx", currentRequest.getChannel() + currentRequest.getStatus());
                holder.playText.setVisibility(View.INVISIBLE);
                holder.playImage.setVisibility(View.INVISIBLE);

                holder.pauseText.setVisibility(View.VISIBLE);
                holder.pauseImage.setVisibility(View.VISIBLE);

                holder.statusWaitingImage.setVisibility(View.INVISIBLE);
                holder.statusOperatingImage.setVisibility(View.VISIBLE);

                holder.cancelImage.setVisibility(View.GONE);
                holder.cancelText.setVisibility(View.GONE);

                holder.completeText.setVisibility(View.VISIBLE);
                holder.completeImage.setVisibility(View.VISIBLE);
            }

            //Sezione Onclick delle varie sezioni

            holder.collapsing.setOnClickListener(new View.OnClickListener() {
                //@Override
                public void onClick(View v) {
                    if (holder.expandibleView.getVisibility() == View.GONE) {
                        currentRequest.setIsExpanded(true);
                        notifyItemChanged(position);
                        holder.collapse.setVisibility(View.VISIBLE);
                        holder.uncollapse.setVisibility(View.INVISIBLE);

                    } else {
                        currentRequest.setIsExpanded(false);
                        notifyItemChanged(position);
                        holder.collapse.setVisibility(View.INVISIBLE);
                        holder.uncollapse.setVisibility(View.VISIBLE);

                    }
                }
            });

            holder.playArea.setOnClickListener(new View.OnClickListener() {
                //@Override
                public void onClick(View v) {
                    playClicked(v, currentRequest, holder.statusWaitingImage, holder.statusOperatingImage,
                            holder.playImage, holder.playText, holder.pauseImage, holder.pauseText, holder.cancelImage, holder.cancelText,
                            holder.completeImage, holder.completeText,holder.basicIcon);
                }
            });

            holder.mapsArea.setOnClickListener(new View.OnClickListener() {
                //@Override
                public void onClick(View v) {
                    centerMapWithPosition(currentRequest);
                }
            });

            holder.deleteArea.setOnClickListener(new View.OnClickListener() {
                //@Override
                public void onClick(View v) {
                    deleteClicked(v, requests, currentRequest, position);
                }
            });

        }


        public void playClicked(View v, Request currentRequest, ImageView waitingImage, ImageView operatingImage, ImageView playImage,
                                TextView playText, ImageView pauseImage, TextView pauseText, ImageView cancelImage, TextView cancelText,
                                ImageView completeImage, TextView completeText, ImageView basicIcon) {
            //se non è mai stata eseguita
            if (currentRequest.getStatus().equals("Accepted")
                    || currentRequest.getStatus().equals("Scheduled")
                    || currentRequest.getStatus().equals("Interrupted")) {
                waitingImage.setVisibility(View.INVISIBLE);
                operatingImage.setVisibility(View.VISIBLE);

                playImage.setVisibility(View.INVISIBLE);
                playText.setVisibility(View.INVISIBLE);

                pauseImage.setVisibility(View.VISIBLE);
                pauseText.setVisibility(View.VISIBLE);

                cancelImage.setVisibility(View.GONE);
                cancelText.setVisibility(View.GONE);

                completeImage.setVisibility(View.VISIBLE);
                completeText.setVisibility(View.VISIBLE);

                String currentStatus = currentRequest.getStatus();
                sendInfoToServerActivate(currentRequest, currentStatus);
                map.drawField(currentRequest.getField(), (ArrayList<Request>) requests);
                map2.drawField(currentRequest.getField(), (ArrayList<Request>) requests);
                buildIconFarmer(currentRequest, basicIcon);
                //se è già stata eseguita quindi è ongoing.
            } else {
                waitingImage.setVisibility(View.VISIBLE);
                operatingImage.setVisibility(View.INVISIBLE);

                playImage.setVisibility(View.VISIBLE);
                playText.setVisibility(View.VISIBLE);

                pauseImage.setVisibility(View.INVISIBLE);
                pauseText.setVisibility(View.INVISIBLE);

                cancelImage.setVisibility(View.VISIBLE);
                cancelText.setVisibility(View.VISIBLE);

                completeImage.setVisibility(View.GONE);
                completeText.setVisibility(View.GONE);

                sendInfoToServerStop(currentRequest);
                map.drawField(currentRequest.getField(), (ArrayList<Request>) requests);
                map2.drawField(currentRequest.getField(), (ArrayList<Request>) requests);
                buildIconFarmer(currentRequest, basicIcon);
            }


        }

        public void updateFilterStatus(String s) {
            if (s.equals("Criteria")) {
                for (int i = 0; i < requests.size(); i++) {
                    if (requests.get(i).getType().equals("cbec")) {
                        requests.remove(i);
                        mAdapter.notifyItemRemoved(i);
                    }
                }
            } else if (s.equals("cbec")) {
                for (int i = 0; i < requests.size(); i++) {
                    if (requests.get(i).getType().equals("Criteria")) {
                        requests.remove(i);
                        mAdapter.notifyItemRemoved(i);
                    }
                }
            }
        }


        public void deleteClicked(View v,  List<Request> requests,  Request currentRequest, final int position) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

            if (currentRequest.getStatus().equals("Accepted")
                    || currentRequest.getStatus().equals("Scheduled")
                    || currentRequest.getStatus().equals("Interrupted")) {
                // set title
                alertDialogBuilder.setTitle("Sei sicuro di voler cancellare questa richiesta?");

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                sendInfoToServerDelete(currentRequest);
                                requests.remove(currentRequest);
                                mAdapter.notifyItemRemoved(position);
                                mAdapter.notifyDataSetChanged();
                                map.drawField(currentRequest.getField(), (ArrayList<Request>) requests);
                                map2.drawField(currentRequest.getField(), (ArrayList<Request>) requests);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                sendInfoToServerDelete(currentRequest);
            } else if (currentRequest.getStatus().equals("Ongoing")) {

                alertDialogBuilder.setTitle("La richiesta è stata soddisfatta?");

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                sendInfoToServerComplete(currentRequest);
                                requests.remove(currentRequest);
                                mAdapter.notifyItemRemoved(position);
                                mAdapter.notifyDataSetChanged();
                                map.drawField(currentRequest.getField(), (ArrayList<Request>) requests);
                                map2.drawField(currentRequest.getField(), (ArrayList<Request>) requests);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                sendInfoToServerComplete(currentRequest);
            }
        }


        public void centerMapWithPosition(Request currentRequest) {

            Field f = currentRequest.getField();
            ArrayList<GeoPoint> area = f.getArea();
            GeoPoint geoPoint = area.get(0);

            IMapController mapController = map.getController();
            mapController.setZoom(14.5);
            mapController.animateTo(geoPoint, 14.5, (long) 500);
            //mapController.setCenter(geoPoint);
            IMapController mapController2 = map2.getController();
            mapController2.setZoom(14.5);
            //mapController2.setCenter(geoPoint);
            mapController2.animateTo(geoPoint, 14.5, (long) 500);
        }



        PostNewStatus postNewStatus;

        //metodo che manda
        public void sendInfoToServerActivate(Request currentRequest, String currentStatus) {
            JSONObject json = new JSONObject();
            //currentRequest.setCurrentStat(1);

            if (currentRequest.getStatus().equals("Accepted")
                    || currentRequest.getStatus().equals("Scheduled")
                    || currentRequest.getStatus().equals("Interrupted")) {
                try {
                    currentRequest.setStatus("Ongoing");
                    json.put("message", "Changing status from Accepted to Ongoing");
                    json.put("status", currentRequest.getStatus());
                    postNewStatus = (PostNewStatus) new PostNewStatus(json, currentRequest).execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (currentRequest.getStatus().equals("Interrupted")) {
                try {
                    currentRequest.setStatus("Ongoing");
                    json.put("message", "Changing status from Interrupted to Ongoing");
                    json.put("status", currentRequest.getStatus());
                    postNewStatus = (PostNewStatus) new PostNewStatus(json, currentRequest).execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        public void sendInfoToServerStop(Request currentRequest) {
            JSONObject json = new JSONObject();
            //currentRequest.setCurrentStat(1);
            currentRequest.setStatus("Interrupted");

            try {
                json.put("message", "Changing status from Ongoing to Interrupted");
                json.put("status", currentRequest.getStatus());
                postNewStatus = (PostNewStatus) new PostNewStatus(json, currentRequest).execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void sendInfoToServerComplete(Request currentRequest) {
            JSONObject json = new JSONObject();
            //currentRequest.setCurrentStat(1);
            currentRequest.setStatus("Satisfied");

            try {
                json.put("message", "Request Completed");
                json.put("status", currentRequest.getStatus());
                postNewStatus = (PostNewStatus) new PostNewStatus(json, currentRequest).execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void sendInfoToServerDelete(Request currentRequest) {
            JSONObject json = new JSONObject();
            //currentRequest.setCurrentStat(1);
            currentRequest.setStatus("Cancelled");

            try {
                json.put("message", "Cancelled request");
                json.put("status", currentRequest.getStatus());
                postNewStatus = (PostNewStatus) new PostNewStatus(json, currentRequest).execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        private class PostNewStatus extends AsyncTask<Void, Void, String> {

            private JSONObject jsonObject;
            private Request request;

            public PostNewStatus(JSONObject newStatus, Request requestToUpdate) {
                jsonObject = newStatus;
                request = requestToUpdate;
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {

                    Log.d("ProvaExcIdField", this.request.getField().getId());
                    Log.d("ProvaExc", this.request.getId());

                    String idreq = this.request.getId();
                    String idForUrlreq = idreq.replace(":", "%3A");
                    idForUrlreq = idForUrlreq.replace("/", "%2F");
                    idForUrlreq = idForUrlreq.replace("#", "%23");

                    String idfield = this.request.getField().getId();
                    String idForUrlfield = idfield.replace(":", "%3A");
                    idForUrlfield = idForUrlfield.replace("/", "%2F");
                    idForUrlfield = idForUrlfield.replace("#", "%23");

                    URL url = new URL("http://mml.arces.unibo.it:3000/v0/WDmanager/{id}/WDMInspector/{ispector}/AssignedFarms/" + idForUrlfield + "/irrigation_plan/" + idForUrlreq + "/status");

                    Log.d("ProvaExcIdField", idForUrlfield);
                    Log.d("ProvaExc", idForUrlreq);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);
                    conn.setConnectTimeout(15000);
                    conn.connect();

                    String jsonInputString = jsonObject.toString();
                    try (OutputStream os = conn.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }
                    conn.getOutputStream().flush();
                    String res = "";
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine = null;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        res = response.toString();
                    }
                    conn.disconnect();
                    return res;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String aString) {
                super.onPostExecute(aString);
                Log.d("ProvaExc", "---- POST REQUEST RESPONSE ----");
            }
        }

        @Override
        public int getItemCount() {
            return requests.size();
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