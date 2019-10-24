package com.example.dugarolo;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import net.danlew.android.joda.JodaTimeAndroid;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String STATUS_ICON_ID = "id";
    private Integer requestId;
    private ArrayList<Canal> canals = new ArrayList<>();
    private MapView map = null;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);
        Arrays.sort(Request.requests);
        loadMap();
        ListView listRequests = (ListView) findViewById(R.id.list_requests);
        loadRequests(listRequests);
        setListViewListener(listRequests);
        if(requestId != null) {
            Request request = Request.requests[requestId];
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
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.setTilesScaledToDpi(true);
        map.setClickable(true);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        IMapController mapController = map.getController();
        mapController.setZoom(13.0);
        GeoPoint startPoint = new GeoPoint(44.778325, 10.720202);
        mapController.setCenter(startPoint);
        loadGeoPoints();
        drawCanals();
    }

    private void loadGeoPoints() {
        try {
            JSONArray jsonArray = new JSONArray(loadJSONFromAsset());
            for(int index = 0; index < jsonArray.length(); index++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(index);
                double geoLanStart = jsonObject.getJSONObject("start").getDouble("lan");
                double geoLongStart = jsonObject.getJSONObject("start").getDouble("long");
                GeoPoint start = new GeoPoint(geoLanStart, geoLongStart);
                double geoLanEnd = jsonObject.getJSONObject("end").getDouble("lan");
                double geoLongEnd = jsonObject.getJSONObject("end").getDouble("long");
                GeoPoint end = new GeoPoint(geoLanEnd, geoLongEnd);
                Canal canal = new Canal(start, end);
                canals.add(canal);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void drawCanals() {
        //disegna una linea per ogni canale
        for(Canal canal: canals) {
            Polyline line = new Polyline();
            List<GeoPoint> geoPoints = new ArrayList<>();
            geoPoints.add(canal.getStart());
            geoPoints.add(canal.getEnd());
            line.setPoints(geoPoints);
            line.getOutlinePaint().setColor(Color.parseColor("#ADD8E6"));
            map.getOverlayManager().add(line);
            map.invalidate();
        }
    }
    private void loadRequests(ListView listRequests) {
        //ArrayList<Request> arrayOfRequests = new ArrayList<>();
        RequestsAdapter adapter = new RequestsAdapter(this, Request.requests);
        listRequests = (ListView) findViewById(R.id.list_requests);
        listRequests.setAdapter(adapter);
        /*
        Request request1 = new Request(R.drawable.request_cancelled, "Bertacchini\'s farm", R.drawable.request_interrupted);
        Request request2 = new Request(R.drawable.request_completed, "Ferrari\'s farm", R.drawable.status_unknown);
        adapter.add(request1);
        adapter.add(request2);
        */
        /*
        Request.requests.add(request1);
        Request.requests.add(request2);
         */
    }

    private void setListViewListener(ListView listRequests) {
        //crea il listener per i click sulla list view
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listRequests, View itemView, int position, long id) {
                //passa la fattoria cliccata a RequestDetailActivity
                Intent intent = new Intent(MainActivity.this, RequestDetailsActivity.class);
                intent.putExtra(RequestDetailsActivity.EXTRA_REQUEST_ID, (int) id);
                requestId = (int) id;
                startActivity(intent);
            }
        };
        //assegna il listener alla list view
        listRequests.setOnItemClickListener(itemClickListener);
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

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("canals.json");
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

}
