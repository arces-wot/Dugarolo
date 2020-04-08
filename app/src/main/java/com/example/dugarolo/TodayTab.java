package com.example.dugarolo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
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
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TodayTab extends Fragment{

    MyMapView map;
    RequestAdapter mAdapter;

    public TodayTab() {
        // Required empty public constructor
    }

    public static TodayTab newInstance(ArrayList<Request> requests, MyMapView map) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("list", requests);
        bundle.putParcelable("Mappa", map);
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
        mAdapter = new RequestAdapter(requests);
        recyclerView.setAdapter(mAdapter);
        map = getArguments().getParcelable("Mappa");


        /*recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy<0 && !fab.isShown())
                    fab.show();
                else if(dy>0 && fab.isShown())
                    fab.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });*/


        return root;

    }


    public class RequestAdapter extends RecyclerView.Adapter<TodayTab.RequestAdapter.RequestHolder>{

        public ArrayList<Request> requests;
        public Globals sharedData = Globals.getInstance();

        public class RequestHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public TextView farmName, irrigationTime, time;
            public TextView canalName, nAppezamento, cancelText, mapsText, playText, pauseText;
            public ImageView statusWaitingImage,  playImage, mapsImage, cancelImage, pauseImage, basicIcon, statusOperatingImage;


            @SuppressLint("ResourceType")
            public RequestHolder(View itemView) {
                super(itemView);
                basicIcon = itemView.findViewById(R.id.basic_icon);
                farmName = itemView.findViewById(R.id.farm_name);
                time = itemView.findViewById(R.id.time);
                irrigationTime = itemView.findViewById(R.id.irrigation_time);
                canalName = itemView.findViewById(R.id.canal_name);
                nAppezamento = itemView.findViewById(R.id.nAppezamento);
                cancelText = itemView.findViewById(R.id.cancelText);
                mapsText = itemView.findViewById(R.id.mapsText);
                playText = itemView.findViewById(R.id.playText);
                playImage = itemView.findViewById(R.id.playImage);
                mapsImage = itemView.findViewById(R.id.mapsImage);
                cancelImage = itemView.findViewById(R.id.cancelImage);
                pauseImage = itemView.findViewById(R.id.pauseImage);
                pauseText = itemView.findViewById(R.id.pauseText);
                statusWaitingImage = itemView.findViewById(R.id.statusWaitingImage);
                statusOperatingImage = itemView.findViewById(R.id.statusOperatingImage);

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

        RequestAdapter(ArrayList<Request> requests) {
            this.requests = requests;
        }

        public List<Request> getRequests(){
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
            int color1=ResourcesCompat.getColor(getResources(),R.color.colorCompany3, null);
            int color2=ResourcesCompat.getColor(getResources(),R.color.colorCompany3, null);

            for (FarmColor f : sharedData.getFarmColors())
                if(f.getNameFarm().equalsIgnoreCase(name)){
                    color1= f.getIdColor();
                    color2= manipulateColor(f.getIdColor(),0.8f);
                    break;
                }


            /*VectorChildFinder vector;
            vector = new VectorChildFinder(getContext(),R.drawable.ic_farmercolor, holder.basicIcon);
            VectorDrawableCompat.VFullPath path1 = vector.findPathByName("background");
            VectorDrawableCompat.VFullPath path2 = vector.findPathByName("backgroundShadow");
            path1.setFillColor(color1);
            path2.setFillColor(color2);*/

            //qui viene definito il farmer, deve essere aggiunto un campo in request che mi dice
            //se la richiesta è fatta da farmer o dall'algoritmo
            //da attivare quando ci sarà la variabile in request

            Log.d("Exxxx", currentRequest.getType());

            VectorChildFinder vector;
            if(currentRequest.getType().equals("cbec")){
                vector = new VectorChildFinder(getContext(),R.drawable.ic_farmercolor, holder.basicIcon);
                VectorDrawableCompat.VFullPath path1 = vector.findPathByName("background");
                VectorDrawableCompat.VFullPath path2 = vector.findPathByName("backgroundShadow");
                path1.setFillColor(color1);
                path2.setFillColor(color2);
            }else if(currentRequest.getType().equals("criteria")){
                vector = new VectorChildFinder(getContext(),R.drawable.ic_requestcriteria, holder.basicIcon);
                VectorDrawableCompat.VFullPath path1 = vector.findPathByName("background");
                VectorDrawableCompat.VFullPath path2 = vector.findPathByName("backgroundShadow");
                path1.setFillColor(color1);
                path2.setFillColor(color2);
            }

            DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm");
            String formattedDateTime = dateTime.toString(dtf);
            holder.farmName.setText(currentRequest.getName());
            holder.time.setText(getResources().getString(R.string.expected_time) + ": " + formattedDateTime);
            holder.irrigationTime.setText(getResources().getString(R.string.total_irrigation_time) + ": " + currentRequest.getWaterVolume());
            //poi li scrivo in strings
            holder.canalName.setText(currentRequest.getChannel());
            holder.nAppezamento.setText("Appezzamento n°");


            if(currentRequest.getStatus().equals("Accepted")){
                //Log.d("ProvaEx", currentRequest.getChannel() + currentRequest.getStatus());
                holder.pauseText.setVisibility(View.INVISIBLE);
                holder.pauseImage.setVisibility(View.INVISIBLE);
                holder.statusWaitingImage.setVisibility(View.VISIBLE);
                holder.statusOperatingImage.setVisibility(View.INVISIBLE);
            }else if(currentRequest.getStatus().equals("Ongoing")){
                //Log.d("ProvaEx", currentRequest.getChannel() + currentRequest.getStatus());
                holder.playText.setVisibility(View.INVISIBLE);
                holder.playImage.setVisibility(View.INVISIBLE);
                holder.statusWaitingImage.setVisibility(View.INVISIBLE);
                holder.statusOperatingImage.setVisibility(View.VISIBLE);
                holder.cancelImage.setVisibility(View.INVISIBLE);
                holder.cancelText.setVisibility(View.INVISIBLE);
            }


            //Sezione Onclick delle varie sezioni

            holder.playImage.setOnClickListener(new View.OnClickListener() {
                //@Override
                public void onClick(View v) {
                    playClicked(v, currentRequest, holder.statusWaitingImage, holder.statusOperatingImage,
                            holder.playImage, holder.playText, holder.pauseImage, holder. pauseText, holder.cancelImage, holder.cancelText);
                }
            });

            holder.playText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playClicked(v, currentRequest, holder.statusWaitingImage, holder.statusOperatingImage,
                            holder.playImage, holder.playText, holder.pauseImage, holder. pauseText, holder.cancelImage, holder.cancelText);
                }
            });

            holder.pauseImage.setOnClickListener(new View.OnClickListener() {
                //@Override
                public void onClick(View v) {
                    pauseClicked(v, currentRequest, holder.statusWaitingImage, holder.statusOperatingImage,
                            holder.playImage, holder.playText, holder.pauseImage, holder. pauseText, holder.cancelImage, holder.cancelText);
                }
            });

            holder.pauseText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pauseClicked(v, currentRequest, holder.statusWaitingImage, holder.statusOperatingImage,
                            holder.playImage, holder.playText, holder.pauseImage, holder. pauseText, holder.cancelImage, holder.cancelText);
                }
            });

            holder.mapsImage.setOnClickListener(new View.OnClickListener() {
                //@Override
                public void onClick(View v) {
                    centerMapWithPosition(currentRequest);
                }
            });

            holder.mapsText.setOnClickListener(new View.OnClickListener() {
                //@Override
                public void onClick(View v) {
                    centerMapWithPosition(currentRequest);
                }
            });

            holder.cancelImage.setOnClickListener(new View.OnClickListener() {
                //@Override
                public void onClick(View v) {
                    deleteClicked(v, requests, currentRequest, position);
                }
            });

        }


        public void playClicked(View v, Request currentRequest, ImageView waitingImage, ImageView operatingImage, ImageView playImage,
                                TextView playText, ImageView pauseImage, TextView pauseText, ImageView cancelImage, TextView cancelText){
            waitingImage.setVisibility(View.INVISIBLE);
            operatingImage.setVisibility(View.VISIBLE);

            playImage.setVisibility(View.INVISIBLE);
            playText.setVisibility(View.INVISIBLE);

            pauseImage.setVisibility(View.VISIBLE);
            pauseText.setVisibility(View.VISIBLE);

            cancelImage.setVisibility(View.INVISIBLE);
            cancelText.setVisibility(View.INVISIBLE);

            sendInfoToServerActivate(currentRequest);
        }

        public void pauseClicked(View v, Request currentRequest, ImageView waitingImage, ImageView operatingImage, ImageView playImage,
                                TextView playText, ImageView pauseImage, TextView pauseText, ImageView cancelImage, TextView cancelText){
            waitingImage.setVisibility(View.VISIBLE);
            operatingImage.setVisibility(View.INVISIBLE);

            playImage.setVisibility(View.VISIBLE);
            playText.setVisibility(View.VISIBLE);

            pauseImage.setVisibility(View.INVISIBLE);
            pauseText.setVisibility(View.INVISIBLE);

            cancelImage.setVisibility(View.VISIBLE);
            cancelText.setVisibility(View.VISIBLE);

            sendInfoToServerStop(currentRequest);
        }

        public void deleteClicked(View v, final ArrayList<Request> requests, final Request currentRequest, final int position) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

            // set title
            alertDialogBuilder.setTitle("Vuoi sicuro di voler cancellare questa richiesta?");

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            sendInfoToServerDelete(currentRequest);
                            requests.remove(currentRequest);
                            mAdapter.notifyItemRemoved(position);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }


        public void centerMapWithPosition(Request currentRequest){

            Field f = currentRequest.getField();
            ArrayList<GeoPoint> area = f.getArea();
            GeoPoint geoPoint = area.get(0);

            IMapController mapController = map.getController();
            mapController.setZoom(15.0);
            mapController.setCenter(geoPoint);
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

        PostNewStatus postNewStatus;

        //metodo che manda
        public void sendInfoToServerActivate(Request currentRequest){
            JSONObject json = new JSONObject();
            currentRequest.setCurrentStat(1);
            currentRequest.setStatus("Ongoing");

            try {
                json.put("message", "Changing status from Accepted to Ongoing");
                json.put("status", currentRequest.getStatus());
                postNewStatus = (PostNewStatus) new PostNewStatus(json, currentRequest).execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        public void sendInfoToServerStop(Request currentRequest){
            JSONObject json = new JSONObject();
            currentRequest.setCurrentStat(1);
            currentRequest.setStatus("Accepted"); //(?)

            try {
                json.put("message", "Changing status from Ongoing to Accepted");
                json.put("status", currentRequest.getStatus());
                postNewStatus = (PostNewStatus) new PostNewStatus(json, currentRequest).execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void sendInfoToServerDelete(Request currentRequest){
            JSONObject json = new JSONObject();
            currentRequest.setCurrentStat(1);
            currentRequest.setStatus("Cancelled"); //(?)

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
                    URL url = new URL("http://mml.arces.unibo.it:3000/v0/WDmanager/{id}/WDMInspector/{ispector}/AssignedFarms/" + this.request.getField().getId() + "/irrigation_plan/" + this.request.getId() + "/status");

                    Log.d("ProvaExcIdField", this.request.getField().getId());
                    Log.d("ProvaExc",  this.request.getId());

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);
                    conn.setConnectTimeout(15000);
                    conn.connect();

                    String jsonInputString = jsonObject.toString();
                    try(OutputStream os = conn.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }
                    conn.getOutputStream().flush();
                    String res = "";
                    try(BufferedReader br = new BufferedReader(
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
                System.out.println("---- POST REQUEST RESPONSE ----");
                System.out.print(aString);
                Log.d("ProvaExc", "---- POST REQUEST RESPONSE ----");
                Log.d("ProvaExc", aString);
            }
        }

        @Override
        public int getItemCount() {
            return requests.size();
        }

    }


}