package com.example.dugarolo;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String STATUS_ICON_ID = "id";
    private Integer requestId;


    MapView map = null;
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        map.setTileSource(TileSourceFactory.USGS_SAT);
        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();
        mapController.setZoom(9.5);
        GeoPoint startPoint = new GeoPoint(44.778325, 10.720202);
        mapController.setCenter(startPoint);
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


}
