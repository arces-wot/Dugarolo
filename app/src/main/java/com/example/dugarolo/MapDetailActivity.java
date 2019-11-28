package com.example.dugarolo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import static com.example.dugarolo.JSONIntentService.STATUS_ERROR;
import static com.example.dugarolo.JSONIntentService.STATUS_FINISHED;

public class MapDetailActivity extends AppCompatActivity implements JSONReceiver.Receiver{

    private ArrayList<Canal> canals = new ArrayList<>();
    private MyMapView map = null;
    private GpsMyLocationProvider gpsMyLocationProvider;
    private ArrayList<Weir> weirs = new ArrayList<>();
    private ArrayList<Marker> textMarkers = new ArrayList<>();
    private ArrayList<Marker> weirMarkers = new ArrayList<>();
    private ArrayList<Farm> farms = new ArrayList<>();
    private AssetLoader assetLoader = new AssetLoader();
    private JSONReceiver jsonReceiver;

    private static final int REQUEST_CODE_WATER = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gpsMyLocationProvider = new GpsMyLocationProvider(this);
        //carica i valori relativi al livello dell'acqua dei canali tramite intent sevice
        registerService();
        assetLoader.loadGeoPointsWDN(canals, weirs, this);
        assetLoader.loadGeoPointsFarms(farms, this);
        loadMap();
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
        setContentView(R.layout.activity_map_detail);
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.setTilesScaledToDpi(true);
        map.setClickable(true);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        IMapController mapController = map.getController();
        mapController.setZoom(15.0);
        GeoPoint startPoint = new GeoPoint(44.778325, 10.720202);
        mapController.setCenter(startPoint);
        //map.drawCanals(canals);
        map.drawWeirs(weirs, weirMarkers);
        map.drawFarms(farms);
        setWeirListeners(weirMarkers);
    }

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        MyLocationNewOverlay locationNewOverlay = new MyLocationNewOverlay(gpsMyLocationProvider, map);
        locationNewOverlay.disableMyLocation();
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        MyLocationNewOverlay locationNewOverlay = new MyLocationNewOverlay(gpsMyLocationProvider, map);
        locationNewOverlay.enableMyLocation();
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
        //drawCanals();
    }

    public void onClickResizeMap(View view) {
        Intent intent = new Intent(MapDetailActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void onClickShowMyLocation(View view) {
        gpsMyLocationProvider.addLocationSource(LocationManager.NETWORK_PROVIDER);
        MyLocationNewOverlay locationOverlay = new MyLocationNewOverlay(gpsMyLocationProvider, map);
        locationOverlay.enableFollowLocation();
        locationOverlay.enableMyLocation();
        Drawable currentDraw = ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_launcher, null);
        Bitmap currentIcon = null;
        if (currentDraw != null) {
            currentIcon = ((BitmapDrawable) currentDraw).getBitmap();
        }
        locationOverlay.setPersonIcon(currentIcon);
        map.getOverlayManager().add(locationOverlay);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK) {
            return;
        }

        if(requestCode == REQUEST_CODE_WATER) {
            if(data == null) {
                return;
            }
            String weirNumber = data.getExtras().getString("Weir Number");
            Integer newOpenLevel = data.getExtras().getInt("Open Level");
            for (Weir weir : weirs) {
                if (weir.getNumber().equals(weirNumber)) {
                    weir.setOpenLevel(newOpenLevel);
                }
            }
        }
    }

    private void setWeirListeners(ArrayList<Marker> weirMarkers) {
        for(Marker marker : weirMarkers) {
            marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {
                    Intent intent = new Intent(MapDetailActivity.this, WeirActivity.class);
                    for (Weir weir : weirs) {
                        if (marker.getId().equals(weir.getNumber())) {
                            intent.putExtra("Farm", weir.getFarm());
                            intent.putExtra("Number", weir.getNumber());
                            intent.putExtra("Open Level", weir.getOpenLevel());
                        }
                    }
                    startActivityForResult(intent, REQUEST_CODE_WATER);
                    return true;
                }
            });
        }
    }

    private void registerService() {
        final Intent intent = new Intent(MapDetailActivity.this, JSONIntentService.class);
        jsonReceiver = new JSONReceiver(new Handler());
        jsonReceiver.setmReceiver(this);
        intent.putExtra("receiver", jsonReceiver);
        ConnectivityManager connectivityManager =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if (isConnected) {
            ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(1);

            scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    startService(intent);
                }
            }, 1, 5, TimeUnit.SECONDS);
        }
        else {
            Toast.makeText(MapDetailActivity.this   , "Device is not connected, can't fetch water level data", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        //gestisci i risultati ottenuti dall'intent service
        try {
            parseResult(resultCode, resultData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseResult(int resultCode, Bundle resultData) throws JSONException {
        if(textMarkers.size() > 0) {
            for(Marker textMarker : textMarkers) {
                textMarker.setVisible(false);
            }
        }
        switch(resultCode) {
            case STATUS_FINISHED:
                String jsonText = resultData.getString("results");
                if(jsonText == null) {
                    Toast.makeText(MapDetailActivity.this, "No results received, server might be offline", Toast.LENGTH_LONG).show();
                } else {
                    jsonText = jsonText.replace("\n", "");
                    JSONArray jsonArray = new JSONArray(jsonText);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonArrayElem = jsonArray.getJSONObject(i);
                        //considero solo i canali
                        if (jsonArrayElem.getString("type").equals("Channel")) {
                            String id = jsonArrayElem.getString("id");
                            double geoLanStart = jsonArrayElem.getJSONObject("start").getDouble("lan");
                            double geoLongStart = jsonArrayElem.getJSONObject("start").getDouble("long");
                            GeoPoint start = new GeoPoint(geoLanStart, geoLongStart);
                            double geoLanEnd = jsonArrayElem.getJSONObject("end").getDouble("lan");
                            double geoLongEnd = jsonArrayElem.getJSONObject("end").getDouble("long");
                            GeoPoint end = new GeoPoint(geoLanEnd, geoLongEnd);
                            Integer waterLevel = jsonArrayElem.getInt("waterLevel");
                            Canal canal = new Canal(id, start, end, waterLevel);
                            canals.add(canal);
                            Marker marker = new Marker(map);
                            marker.setPosition(map.midPoint(canal.getStart(), canal.getEnd()));
                            marker.setTextLabelBackgroundColor(Color.TRANSPARENT);
                            marker.setTextLabelForegroundColor(Color.RED);
                            marker.setTextLabelFontSize(20);
                            marker.setTextIcon(canal.getWaterLevel().toString() + " mm");
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_TOP);
                            marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker, MapView mapView) {
                                    //nascondo la info window e impedisco lo zoom-in automatico sul click
                                    return true;
                                }
                            });
                            textMarkers.add(marker);
                        }
                    }
                    map.drawCanals(canals, textMarkers);
                }
                break;
            case STATUS_ERROR:
                Toast.makeText(MapDetailActivity.this, "Something went wrong!", Toast.LENGTH_LONG);
                break;
        }
    }
}
