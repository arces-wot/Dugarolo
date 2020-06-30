package com.example.dugarolo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;

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

import java.lang.reflect.Type;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import static com.example.dugarolo.JSONIntentService.STATUS_ERROR;
import static com.example.dugarolo.JSONIntentService.STATUS_FINISHED;

public class MapDetailActivity extends AppCompatActivity implements JSONReceiver.Receiver {

    private ArrayList<Canal> canals = new ArrayList<>();
    private MyMapView map;
    private GpsMyLocationProvider gpsMyLocationProvider;
    private ArrayList<Weir> weirs = new ArrayList<>();
    private ArrayList<Marker> textMarkers = new ArrayList<>();
    private ArrayList<Marker> weirMarkers = new ArrayList<>();
    private ArrayList<Marker> farmerMarkers = new ArrayList<>();
    private ArrayList<Farm> farms = new ArrayList<>();
    private AssetLoader assetLoader = new AssetLoader();
    private JSONReceiver jsonReceiver;
    private boolean isInFront;
    GeoPoint startPoint = new GeoPoint(44.778325, 10.720202);
    Globals globals = Globals.getInstance();

    //per GPS
    private LocationManager locationManager;
    private LocationListener listener;
    GeoPoint myPosition;
    Button GPSbutton1;
    private Marker markerPosition;

    private static final int REQUEST_CODE_WATER = 0;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().hasExtra("WEIRS")) {
            //farms = Objects.requireNonNull(getIntent().getExtras()).getParcelableArrayList("FARMS");
            weirs = Objects.requireNonNull(getIntent().getExtras()).getParcelableArrayList("WEIRS");
            canals = Objects.requireNonNull(getIntent().getExtras()).getParcelableArrayList("CANALS");
            saveData();
        } else {
            loadData();
        }


        loadMap(startPoint);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        //disattivo l'hardware acceleration per risolvere i problemi reativi alle icone in Android >= 8
        map.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        gpsMyLocationProvider = new GpsMyLocationProvider(this);
        //setGPSButton();




    }


    public void setGPSButton() {
        GPSbutton1 = findViewById(R.id.GPSbutton1);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        listener = new LocationListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

            @Override
            public void onLocationChanged(Location location) {
                Marker newMarkerPosition;
                if (markerPosition != null)
                    markerPosition.remove(map);
                myPosition = new GeoPoint(location.getLatitude(), location.getLongitude());
                newMarkerPosition = map.drawPosition(myPosition);
                markerPosition = newMarkerPosition;

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        configure_button();
    }

    void configure_button() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
        // this code won'textView execute IF permissions are not allowed, because in the line above there is return statement.
        GPSbutton1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                //noinspection MissingPermission
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates("gps", 5000, 10, listener);

                centerMap(myPosition);
            }
        });


    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    private void loadMap(GeoPoint startPoint) {
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
        map = new MyMapView(this);
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.setTilesScaledToDpi(true);
        map.setClickable(true);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        centerMap(startPoint);
        map.drawFarms(farms);
        map.drawCanals(canals);
        map.drawWeirs(weirs, weirMarkers);
        //map.drawIcon(farms, farmerMarkers, 80);
        setWeirListeners(weirMarkers);
        registerService();

    }

    public void centerMap(GeoPoint g) {
        IMapController mapController = map.getController();
        mapController.setZoom(15.0);
        mapController.setCenter(g);
    }


    public void onPause() {

        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        MyLocationNewOverlay locationNewOverlay = new MyLocationNewOverlay(gpsMyLocationProvider, map);
        locationNewOverlay.disableMyLocation();
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
        isInFront = false;
    }


    public void onResume() {

        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        isInFront = true;
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
        checkLocationPermission();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            if (data != null) {
                String num = data.getExtras().getString("Weir Number");
                Integer newLevel = data.getExtras().getInt("Open Level");
                for (Weir weir : weirs) {
                    if (weir.getId().equals(num)) weir.setOpenLevel(newLevel);

                }
            }
            return;
        }

        if (requestCode == REQUEST_CODE_WATER) {
            if (data == null) {
                return;
            }
            gpsMyLocationProvider.addLocationSource(LocationManager.NETWORK_PROVIDER);
            MyLocationNewOverlay locationOverlay = new MyLocationNewOverlay(gpsMyLocationProvider, map);
            locationOverlay.enableFollowLocation();
            locationOverlay.enableMyLocation();
            map.getOverlayManager().add(locationOverlay);

            String weirNumber = data.getExtras().getString("Weir Number");
            Integer newOpenLevel = data.getExtras().getInt("Open Level");
            for (Weir weir : weirs) {
                if (weir.getId().equals(weirNumber)) {
                    weir.setOpenLevel(newOpenLevel);
                }
            }
        }
    }

    private void setWeirListeners(ArrayList<Marker> weirMarkers) {
        for (Marker marker : weirMarkers) {

            marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {
                    Intent intent = new Intent(MapDetailActivity.this, WeirActivity.class);
                    for (Weir weir : weirs) {
                        if (marker.getId().equals(weir.getId())) {
                            intent.putExtra("Number", weir.getId());
                            intent.putExtra("Open Level", weir.getOpenLevel());
                            intent.putExtra("Max Level", weir.getMaxLevel());
                            intent.putExtra("Min Level", weir.getMinLevel());
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
            startService(intent);
        } else {
            Toast.makeText(MapDetailActivity.this, "Device is not connected, can't fetch water level data", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        //gestisci i risultati ottenuti dall'intent service
        try {
            if (isInFront) {

                parseResult(resultCode, resultData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseResult(int resultCode, Bundle resultData) throws JSONException {
        switch (resultCode) {
            case STATUS_FINISHED:
                String jsonText = resultData.getString("results");
                if (jsonText == null) {
                    Toast.makeText(MapDetailActivity.this, "No results received, server might be offline", Toast.LENGTH_LONG).show();
                } else {
                    if (textMarkers.size() > 0) {
                        for (Marker textMarker : textMarkers) {
                            textMarker.setVisible(false);
                        }
                        textMarkers.clear();
                    }
                    jsonText = jsonText.replace("\n", "");
                    JSONArray jsonArray = new JSONArray(jsonText);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonArrayElem = jsonArray.getJSONObject(i);
                        Marker marker = new Marker(map);
                        JSONObject location = jsonArrayElem.getJSONObject("location");
                        Double lat = location.getDouble("lat");
                        Double lon = location.getDouble("lon");
                        marker.setPosition(new GeoPoint(lat, lon));
                        marker.setTextLabelBackgroundColor(Color.WHITE);
                        marker.setTextLabelForegroundColor(R.color.text_map_detail_color);
                        marker.setTextLabelFontSize(25);
                        Double level = Double.parseDouble(jsonArrayElem.getString("level"));
                        DecimalFormat df = new DecimalFormat("#.##");
                        df.setRoundingMode(RoundingMode.CEILING);
                        String newLevel = df.format(level);
                        marker.setTextIcon(newLevel + " " + getResources().getString(R.string.measurement_unit));
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
                    map.drawTextMarkers(textMarkers);
                }
                break;
            case STATUS_ERROR:
                Toast.makeText(MapDetailActivity.this, "Something went wrong!", Toast.LENGTH_LONG);
                break;
        }
    }


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Position")
                        .setMessage("Grant permissions to access your position?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapDetailActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            gpsMyLocationProvider.addLocationSource(LocationManager.NETWORK_PROVIDER);
            MyLocationNewOverlay locationOverlay = new MyLocationNewOverlay(gpsMyLocationProvider, map);
            locationOverlay.enableFollowLocation();
            locationOverlay.enableMyLocation();
            map.getOverlayManager().add(locationOverlay);
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        gpsMyLocationProvider.addLocationSource(LocationManager.NETWORK_PROVIDER);
                        MyLocationNewOverlay locationOverlay = new MyLocationNewOverlay(gpsMyLocationProvider, map);
                        locationOverlay.enableFollowLocation();
                        locationOverlay.enableMyLocation();
                        map.getOverlayManager().add(locationOverlay);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonFarms = gson.toJson(farms);
        String jsonWeirs = gson.toJson(weirs);
        String jsonCanals = gson.toJson(canals);
        editor.putString("FARMS", jsonFarms);
        editor.putString("WEIRS", jsonWeirs);
        editor.putString("CANALS", jsonCanals);
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonFarms = sharedPreferences.getString("FARMS", null);
        String jsonWeirs = sharedPreferences.getString("WEIRS", null);
        String jsonCanals = sharedPreferences.getString("CANALS", null);
        Type typeFarm = new TypeToken<ArrayList<Farm>>() {
        }.getType();
        Type typeWeir = new TypeToken<ArrayList<Weir>>() {
        }.getType();
        Type typeCanal = new TypeToken<ArrayList<Canal>>() {
        }.getType();
        farms = gson.fromJson(jsonFarms, typeFarm);
        weirs = gson.fromJson(jsonWeirs, typeWeir);
        canals = gson.fromJson(jsonCanals, typeCanal);
    }

}

