package com.example.dugarolo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.overlay.Marker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private RecyclerView requestRecyclerView;
    private RecyclerView.Adapter requestAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public static final String REQUEST_STATUS = "id";
    private Integer requestId;
    private ArrayList<Farm> farms = new ArrayList<>();
    private MyMapView map = null;
    private AssetLoader assetLoader = new AssetLoader();
    private ArrayList<Request> requests = new ArrayList<>();
    private ArrayList<Marker> farmerMarkers = new ArrayList<>();
    private FloatingActionButton fab;

    //debug only
    //private static final String TAG = "MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);
        //RequestLab requestLab = RequestLab.get(this);
        //List<Request> requestList = requestLab.getRequestList();
        //trial requests:

        DateTime date1 = new DateTime(2020, 12, 12, 12, 12);
        ArrayList<GeoPoint> geoList1 = new ArrayList<GeoPoint>();
        geoList1.add(new GeoPoint(44.777572, 10.715764));
        geoList1.add(new GeoPoint(44.777201, 10.717981));
        geoList1.add(new GeoPoint(44.778039, 10.719505));
        geoList1.add(new GeoPoint(44.777572, 10.715764));

        Field f1 = new Field("Bertacchini's Farm", "001", geoList1);
        Request r1 = new Request("001", "Bertacchini's Farm", date1, "Scheduled", "1h", f1, "speriamo bene");
        requests.add(r1);

        DateTime date2 = new DateTime();
        Request r2 = new Request("001", "Bertacchini's Farm", date1, "Accepted", "1h", f1, "speriamo bene");
        requests.add(r2);
        Request r3 = new Request("001", "Ferrari's Farm", date1, "Accepted", "1h", f1, "speriamo bene");
        requests.add(r3);
        Request r4 = new Request("001", "Bertacchini's Farm", date2, "Accepted", "12h", f1, "speriamo bene");
        requests.add(r4);
        Request r5 = new Request("001", "Ferrari's Farm", date2, "Accepted", "10h", f1, "speriamo bene");
        requests.add(r5);
        Request r6 = new Request("001", "Bertacchini's Farm", date2, "Accepted", "12h", f1, "speriamo bene");
        requests.add(r6);
        Request r7 = new Request("001", "Bertacchini's Farm", date2, "Accepted", "12h", f1, "speriamo bene");
        requests.add(r7);

        loadMap();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(false);
        new LoadFarmsAndRequests().execute();
        //loadRequestsRecyclerView(requestList);

        if (requestId != null) {
            Request request = requests.get(requestId);
            String status = (String) Objects.requireNonNull(getIntent().getExtras()).get(REQUEST_STATUS);
            request.setStatus(status);
        }

        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(this, getSupportFragmentManager(), requests);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(tabsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);


        fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Planning.class);
                startActivity(intent);
            }
        });


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


    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause() {
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


    private class LoadFarmsAndRequests extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            assetLoader.loadGeoPointsFarms(farms);
            assetLoader.loadRequests(farms, requests);
            return true;
        }

        int check = 0;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (aBoolean) {
                map.drawFarms(farms);
                map.drawIcon(farms, farmerMarkers, 70);
            }
        }
    }
}
