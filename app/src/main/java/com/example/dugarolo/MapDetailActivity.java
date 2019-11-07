package com.example.dugarolo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

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
import org.osmdroid.views.overlay.Marker.OnMarkerClickListener;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class MapDetailActivity extends AppCompatActivity {

    private ArrayList<Canal> canals = new ArrayList<>();
    private MapView map = null;
    private GpsMyLocationProvider gpsMyLocationProvider;
    private ArrayList<Weir> weirs = new ArrayList<>();
    private ArrayList<Marker> textMarkers = new ArrayList<>();

    private static final int REQUEST_CODE_WATER = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gpsMyLocationProvider = new GpsMyLocationProvider(this);
        loadGeoPoints();
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
        drawCanals();
        drawWeirs();
    }

    private void drawWeirs() {
        for(Weir weir : weirs) {
            Marker marker = new Marker(map);
            marker.setPosition(weir.getPosition());
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            Drawable weirIcon = getResources().getDrawable(R.drawable.weir);
            Bitmap bitmap = ((BitmapDrawable) weirIcon).getBitmap();
            Drawable resizedWeirIcon = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 40, 40, true));
            marker.setIcon(resizedWeirIcon);
            marker.setInfoWindow(null);
            marker.setId(weir.getNumber().toString());
            marker.setOnMarkerClickListener(new OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {
                    Intent intent = new Intent(MapDetailActivity.this, WeirActivity.class);
                    for(Weir weir : weirs) {
                        if(marker.getId().equals(weir.getNumber())) {
                            intent.putExtra("Farm", weir.getFarm());
                            intent.putExtra("Number", weir.getNumber());
                            intent.putExtra("Open Level", weir.getOpenLevel());
                        }
                    }
                    startActivityForResult(intent, REQUEST_CODE_WATER);
                    return true;
                }
            });
            map.getOverlays().add(marker);
            map.invalidate();
        }
    }

    private void loadGeoPoints() {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(loadJSONFromAssetWDN());
            //ottengo i dati dei "nodes"
            JSONArray jsonArray = jsonObject.getJSONArray("nodes");
            for (int index = 0; index < jsonArray.length(); index++) {
                JSONObject jsonArrayElem = jsonArray.getJSONObject(index);
                //considero solo le chiuse
                if (jsonArrayElem.getString("type").equals("Weir")) {
                    String id = jsonArrayElem.getString("id");
                    int openLevel = jsonArrayElem.getInt("openLevel");
                    GeoPoint geoPoint = new GeoPoint(jsonArrayElem.getJSONObject("location").getDouble("lat"),
                            jsonArrayElem.getJSONObject("location").getDouble("lon"));
                    Weir weir = new Weir(id, "X", openLevel, geoPoint);
                    weirs.add(weir);
                }
            }
            //ottengo i dati delle "connections"
            jsonArray = jsonObject.getJSONArray("connections");
            for (int index = 0; index < jsonArray.length(); index++) {
                JSONObject jsonArrayElem = jsonArray.getJSONObject(index);
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
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String loadJSONFromAssetWDN() {
        String json;
        try {
            InputStream is = this.getAssets().open("wdn_data.json");
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

    private void drawCanals() {
        /*
        - il codice commentato all'interno di questa funzione potrebbe essere molto utile(in quanto
        - funzionante) in futuro. NON CANCELLARE!
        if(textMarkers.size() > 0) {
            for(Iterator<Marker> iterator = textMarkers.iterator(); iterator.hasNext();) {
                Marker textMarker = iterator.next();
                map.getOverlayManager().remove(textMarker);
                iterator.remove();        ;
            }
        }
         */
        for(Canal canal: canals) {
            Polyline line = new Polyline();
            List<GeoPoint> geoPoints = new ArrayList<>();
            geoPoints.add(canal.getStart());
            geoPoints.add(canal.getEnd());
            line.setPoints(geoPoints);
            line.getOutlinePaint().setColor(Color.parseColor("#ADD8E6"));
            map.getOverlayManager().add(line);
            map.invalidate();
            Marker marker = new Marker(map);
            marker.setPosition(midPoint(canal.getStart(), canal.getEnd()));
            marker.setTextLabelBackgroundColor(Color.TRANSPARENT);
            marker.setTextLabelForegroundColor(Color.RED);
            marker.setTextLabelFontSize(20);
            marker.setTextIcon(canal.getWaterLevel().toString() + " mm");
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_TOP);
            marker.setOnMarkerClickListener(new OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {
                    //nascondo la info window e impedisco lo zoom-in automatico sul click
                    return true;
                }
            });
            textMarkers.add(marker);
            for (Marker textMarker : textMarkers) {
                map.getOverlayManager().add(textMarker);
                map.invalidate();
            }
            /*
            for(Weir weir : weirs) {
                if(weir.getNumber().equals(canal.getWeirId())) {
                    Marker marker = new Marker(map);
                    marker.setPosition(midPoint(canal.getStart(), canal.getEnd()));
                    marker.setTextLabelBackgroundColor(Color.TRANSPARENT);
                    marker.setTextLabelForegroundColor(Color.RED);
                    marker.setTextLabelFontSize(20);
                    marker.setTextIcon(weir.getWaterLevel().toString() + " mm");
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_TOP);
                    marker.setOnMarkerClickListener(new OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker, MapView mapView) {
                            //nascondo la info window e impedisco lo zoom-in automatico sul click
                            return true;
                        }
                    });
                    textMarkers.add(marker);
                }
            }
             */
        }
        /*
        for (Marker textMarker : textMarkers) {
            map.getOverlayManager().add(textMarker);
            map.invalidate();
        }
         */
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

    private GeoPoint midPoint(GeoPoint geoPoint1, GeoPoint geoPoint2) {
        return GeoPoint.fromCenterBetween(geoPoint1, geoPoint2);
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
}
