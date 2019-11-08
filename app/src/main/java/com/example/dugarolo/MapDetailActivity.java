package com.example.dugarolo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
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
        //registerService();
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
        map.drawCanals(canals, textMarkers);
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
        Intent intent = new Intent(MapDetailActivity.this, JSONIntentService.class);
        jsonReceiver = new JSONReceiver(new Handler());
        intent.putExtra("receiver", jsonReceiver);
        startService(intent);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        //gestisci i risultati ottenuti dall'intent service
        switch(resultCode) {
            case STATUS_FINISHED:
                //do something interesting
            break;
            case STATUS_ERROR:
                //gestsci l'errore
            break;
        }
    }
}
