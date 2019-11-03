package com.example.dugarolo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polygon;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.danlew.android.joda.JodaTimeAndroid;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView requestRecyclerView;
    private RecyclerView.Adapter requestAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public static final String STATUS_ICON_ID = "id";
    private Integer requestId;
    private ArrayList<Farm> farms = new ArrayList<>();
    private MapView map = null;
    //debug only
    //private static final String TAG = "MainActivity";



    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);
        //Log.d("Requests", "Reqs" + Arrays.toString(Request.requests));
        //Arrays.sort(Request.requests);
        //Request.requests.add(new Request(R.drawable.swamp_leaf, "Bertacchini\'s farm", new LocalDate(2019, 9, 18, null), R.drawable.request_interrupted));
        //Request.requests.add(new Request(R.drawable.swamp_leaf, "Ferrari\'s farm", new LocalDate(2019, 9, 19, null), R.drawable.status_unknown));
        RequestLab requestLab = RequestLab.get(this);
        List<Request> requestList = requestLab.getRequestList();
        loadMap();
        loadRequests(requestList);
        if(requestId != null) {
            //Request request = Request.requests[requestId];
            Request request = requestList.get(requestId);
            Integer statusIcon = (Integer) getIntent().getExtras().get(STATUS_ICON_ID);
            request.setStatusIconId(statusIcon);
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
        drawFarms();
    }

    private void loadGeoPointsFarms() {
        try {
            JSONArray jsonArray = new JSONArray(loadJSONFromAssetFarms());
            for(int index = 0; index < jsonArray.length(); index++) {
                JSONObject jsonObject = jsonArray.getJSONObject(index);
                JSONArray jsonArrayPoints = jsonObject.getJSONArray("area");
                Polygon polygon = new Polygon();
                for(int i = 0; i < jsonArrayPoints.length(); i++) {
                    JSONObject jsonObjectPoint = jsonArrayPoints.getJSONObject(i);
                    double lat = jsonObjectPoint.getDouble("lat");
                    double lon = jsonObjectPoint.getDouble("lon");
                    GeoPoint geoPoint = new GeoPoint(lat, lon);
                    polygon.addPoint(geoPoint);
                }
                Farm farm = new Farm(jsonObject.getString("name"), polygon);
                farms.add(farm);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void drawFarms() {
        loadGeoPointsFarms();
        //disegna una linea per ogni canale
        for(Farm farm: farms) {
            Polygon polygon = farm.getArea();
            switch (farm.getName()) {
                case "Bertacchini's Farm":
                    polygon.getOutlinePaint().setColor(Color.YELLOW);
                    break;
                case "Ferrari's Farm":
                    polygon.getOutlinePaint().setColor(Color.BLUE);
                    break;
                default:
            }
            polygon.getFillPaint().setColor(Color.TRANSPARENT);
            polygon.getOutlinePaint().setStrokeWidth(3);
            map.getOverlayManager().add(polygon);
            map.invalidate();
        }
    }
    private void loadRequests(List<Request> requestList) {
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

    public String loadJSONFromAssetFarms() {
        String json;
        try {
            InputStream is = this.getAssets().open("farms_swamp.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public void onClickExpandMap(View view) {
        Intent intent = new Intent(MainActivity.this, MapDetailActivity.class);
        startActivity(intent);
    }


    private class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestHolder> {

        private List<Request> requests;

        private class RequestHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            public ImageView basicIcon;
            public TextView dateAndFarmName;
            public ImageView statusIcon;

            public RequestHolder(View itemView) {
                super(itemView);
                basicIcon = itemView.findViewById(R.id.basic_icon);
                dateAndFarmName = itemView.findViewById(R.id.farm_name);
                statusIcon = itemView.findViewById(R.id.status_icon);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int position = getLayoutPosition();
                Intent intent = new Intent(MainActivity.this, RequestDetailsActivity.class);
                intent.putExtra(RequestDetailsActivity.EXTRA_REQUEST_ID, position);
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
            holder.basicIcon.setImageResource(currentRequest.getBasicIconId());
            holder.dateAndFarmName.setText(Html.fromHtml(currentRequest.getName() + "<br />" + "<small>" + currentRequest.getDate().toString() + "</small"));
            holder.statusIcon.setImageResource(currentRequest.getStatusIconId());
        }

        @Override
        public int getItemCount() {
            return requests.size();
        }
    }

}
