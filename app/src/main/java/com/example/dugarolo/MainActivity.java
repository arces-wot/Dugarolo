package com.example.dugarolo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import net.danlew.android.joda.JodaTimeAndroid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private RecyclerView requestRecyclerView;
    private RecyclerView.Adapter requestAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public static final String REQUEST_STATUS= "id";
    private Integer requestId;
    private ArrayList<Farm> farms = new ArrayList<>();
    private MyMapView map = null;
    private AssetLoader assetLoader = new AssetLoader();
    private ArrayList<Request> requests = new ArrayList<>();
    //debug only
    //private static final String TAG = "MainActivity";



    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);
        //RequestLab requestLab = RequestLab.get(this);
        //List<Request> requestList = requestLab.getRequestList();
        loadMap();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        new LoadFarmsAndRequests().execute();
        //loadRequestsRecyclerView(requestList);
        if(requestId != null) {
            Request request = requests.get(requestId);
            String status = (String) getIntent().getExtras().get(REQUEST_STATUS);
            request.setStatus(status);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.show_key_activity:
                Intent intent = new Intent(MainActivity.this, KeyActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadMap() {
        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's tile servers will get you banned based on this string

        //inflate and create the map
        setContentView(R.layout.activity_main);
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.setTilesScaledToDpi(true);
        map.setClickable(true);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        IMapController mapController = map.getController();
        mapController.setZoom(14.0);
        GeoPoint startPoint = new GeoPoint(44.778325, 10.720202);
        mapController.setCenter(startPoint);
    }

    private void loadRequestsRecyclerView(List<Request> requestList) {
        Collections.sort(requestList);
        requestRecyclerView = findViewById(R.id.list_requests);
        layoutManager = new LinearLayoutManager(this);
        requestRecyclerView.setLayoutManager(layoutManager);
        requestAdapter = new RequestAdapter(requestList);
        requestRecyclerView.setAdapter(requestAdapter);
    }

    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onClickExpandMap(View view) {
        Intent intent = new Intent(MainActivity.this, MapDetailActivity.class);
        startActivity(intent);
    }


    private class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestHolder> {

        private List<Request> requests;

        private class RequestHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            public ImageView farmColor;
            public ImageView basicIcon;
            public TextView dateAndFarmName;
            public ImageView statusIcon;

            public RequestHolder(View itemView) {
                super(itemView);
                farmColor = itemView.findViewById(R.id.farm_color);
                basicIcon = itemView.findViewById(R.id.basic_icon);
                dateAndFarmName = itemView.findViewById(R.id.farm_name);
                statusIcon = itemView.findViewById(R.id.status_icon);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int position = getLayoutPosition();
                Intent intent = new Intent(MainActivity.this, RequestDetailsActivity.class);
                intent.putParcelableArrayListExtra("REQUEST_LIST", (ArrayList<? extends Parcelable>) requests);
                intent.putExtra("REQUEST_CLICKED", position);
                requestId = position;
                startActivity(intent);
            }
        }

        public RequestAdapter(List<Request> requests) {
                this.requests = requests;
        }

        @NonNull
        @Override
        public RequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.item_request, parent, false);
            return new RequestHolder(v);
        }


        @Override
        public void onBindViewHolder(@NonNull RequestAdapter.RequestHolder holder, int position) {
            Request currentRequest = requests.get(position);
            holder.farmColor.setImageResource(R.drawable.farm_color);
            String name = currentRequest.getName();
            if(name.equals("Bertacchini's Farm")) {
                int bertacchini = ResourcesCompat.getColor(getResources(), R.color.colorBertacchini, null);
                holder.farmColor.setColorFilter(bertacchini);
            } else if(name.equals("Ferrari's Farm")) {
                int ferrari = ResourcesCompat.getColor(getResources(), R.color.colorFerrari,null);
                holder.farmColor.setColorFilter(ferrari);
            }
            DateTime dateTime = currentRequest.getDateTime();
            DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
            String formattedDateTime = dateTime.toString(dtf);
            holder.dateAndFarmName.setText(Html.fromHtml(currentRequest.getName() + "<br />" + "<small>" + "<small>" + formattedDateTime
                    + " , water: " + currentRequest.getWaterVolume() + " h" + "</small" + "</small"));
            String status = currentRequest.getStatus();
            switch (status) {
                case "Cancelled":
                    int cancelled = ResourcesCompat.getColor(getResources(), R.color.colorCancelled, null);
                    holder.statusIcon.setColorFilter(cancelled);
                    break;
                case "Interrupted":
                    int interrupted = ResourcesCompat.getColor(getResources(), R.color.colorInterrupted, null);
                    holder.statusIcon.setColorFilter(interrupted);
                    break;
                case "Satisfied":
                    int satisfied = ResourcesCompat.getColor(getResources(), R.color.colorSatisfied, null);
                    holder.statusIcon.setColorFilter(satisfied);
                    break;
                case "Accepted":
                    int accepted = ResourcesCompat.getColor(getResources(), R.color.colorAccepted, null);
                    holder.statusIcon.setColorFilter(accepted);
                    break;
                case "Ongoing":
                    int ongoing = ResourcesCompat.getColor(getResources(), R.color.colorOngoing, null);
                    holder.statusIcon.setColorFilter(ongoing);
                    break;
                default:
                    holder.statusIcon.setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC);
            }
        }

        @Override
        public int getItemCount() {
            return requests.size();
        }
    }


    private class LoadFarmsAndRequests extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            assetLoader.loadGeoPointsFarms(farms);
            assetLoader.loadRequests(farms, requests);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean) {
                map.drawFarms(farms);
                loadRequestsRecyclerView(requests);
            }
        }
    }
}
