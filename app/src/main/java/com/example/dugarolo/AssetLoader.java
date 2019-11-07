package com.example.dugarolo;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polygon;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class AssetLoader {
    public AssetLoader(){

    }

    public void loadGeoPointsFarms(ArrayList<Farm> farms, Context context) {
        try {
            JSONArray jsonArray = new JSONArray(loadJSONFromAssetFarms(context));
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
    private String loadJSONFromAssetFarms(Context context) {
        String json;
        try {
            InputStream is = context.getAssets().open("farms_swamp.json");
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

    public void loadGeoPointsWDN(ArrayList<Canal> canals, ArrayList<Weir> weirs, Context context) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(loadJSONFromAssetWDN(context));
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

    private String loadJSONFromAssetWDN(Context context) {
        String json;
        try {
            InputStream is = context.getAssets().open("wdn_data.json");
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
}
