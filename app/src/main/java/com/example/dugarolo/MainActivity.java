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
import android.content.SharedPreferences;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.danlew.android.joda.JodaTimeAndroid;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {


    public static final String REQUEST_STATUS = "id";
    private ArrayList<Farm> farms = new ArrayList<>();
    private MyMapView map;
    private ArrayList<Request> requests = new ArrayList<>();
    private ArrayList<Marker> farmerMarkers = new ArrayList<>();
    private ArrayList<Weir> weirs = new ArrayList<>();
    private ArrayList<Canal> canals = new ArrayList<>();
    private FloatingActionButton fab;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);

        if (getIntent().hasExtra("REQUESTS")) {
            farms = Objects.requireNonNull(getIntent().getExtras()).getParcelableArrayList("FARMS");
            requests = Objects.requireNonNull(getIntent().getExtras()).getParcelableArrayList("REQUESTS");
            weirs = Objects.requireNonNull(getIntent().getExtras()).getParcelableArrayList("WEIRS");
            canals = Objects.requireNonNull(getIntent().getExtras()).getParcelableArrayList("CANALS");
            saveData();
        } else {
            loadData();
        }


        GeoPoint startPoint = new GeoPoint(44.778325, 10.720202);
        Context ctx = getApplicationContext();
        loadMap(startPoint, ctx);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(false);


        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getApplicationContext(), getSupportFragmentManager(), requests, map);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(tabsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                if (position == 0)
                    fab.show();
                else
                    fab.hide();
            }
        });


        fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Planning.class);
                startActivity(intent);
            }
        });


    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)


    public void loadMap(GeoPoint startPoint, Context ctx) {
        //load/initialize the osmdroid configuration, this can be done

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
        map = findViewById(R.id.map);
        IMapController mapController = map.getController();
        mapController.setZoom(14.0);
        mapController.setCenter(startPoint);
        map.drawFarms(farms);
        map.drawIcon(farms, farmerMarkers, 70);

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

    public void onStop() {
        super.onStop();
    }

    public void onRestart() {
        super.onRestart();
    }

    public void onClickExpandMap(View view) {

        Intent intent = new Intent(MainActivity.this, MapDetailActivity.class);
        intent.putParcelableArrayListExtra("FARMS", farms);
        intent.putParcelableArrayListExtra("WEIRS", weirs);
        intent.putParcelableArrayListExtra("CANALS", canals);
        startActivity(intent);
    }


    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonFarms = gson.toJson(farms);
        String jsonRequests = gson.toJson(requests);
        String jsonWeirs = gson.toJson(weirs);
        String jsonCanals = gson.toJson(canals);
        editor.putString("FARMS", jsonFarms);
        editor.putString("REQUESTS", jsonRequests);
        editor.putString("WEIRS", jsonWeirs);
        editor.putString("CANALS", jsonCanals);
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonFarms = sharedPreferences.getString("FARMS", null);
        String jsonRequests = sharedPreferences.getString("REQUESTS", null);
        String jsonWeirs = sharedPreferences.getString("WEIRS", null);
        String jsonCanals = sharedPreferences.getString("CANALS", null);
        Type typeFarm = new TypeToken<ArrayList<Farm>>() {
        }.getType();
        Type typeRequest = new TypeToken<ArrayList<Request>>() {
        }.getType();
        Type typeWeir = new TypeToken<ArrayList<Weir>>() {
        }.getType();
        Type typeCanal = new TypeToken<ArrayList<Canal>>() {
        }.getType();
        farms = gson.fromJson(jsonFarms, typeFarm);
        requests = gson.fromJson(jsonRequests, typeRequest);
        weirs = gson.fromJson(jsonWeirs, typeWeir);
        canals = gson.fromJson(jsonCanals, typeCanal);
    }

   /* public TabsPagerAdapter getTabsPagerAdapter(ArrayList<Request> requests, MyMapView map) {
        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getApplicationContext(), getSupportFragmentManager(),
                requests, map);
        return tabsPagerAdapter;
    }*/

    public ViewPager getViewPager(TabsPagerAdapter tabsPagerAdapter) {
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(tabsPagerAdapter);
        return viewPager;
    }
}




