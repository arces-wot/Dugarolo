package com.example.dugarolo;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import androidx.annotation.Nullable;

public class JSONIntentService extends IntentService {

    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;
    private static final String TAG = "MapDetailActivity";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public JSONIntentService() {
        super("JSONIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            ResultReceiver jsonReceiver = intent.getParcelableExtra("receiver");
            Bundle bundle = new Bundle();
            JSONObject jsonObject;
            ArrayList<Canal> canals = intent.getParcelableArrayListExtra("canals");
            try {
                //prendi il json
                String jsonText = getJSONFromURL(new URL( "http://192.168.43.209:3000/canals"));
                /*
                Iterator x = jsonObject.keys();
                JSONArray jsonArray = new JSONArray();
                while(x.hasNext()) {
                    String key = (String) x.next();
                    jsonArray.put(jsonObject.get(key));
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonArrayElem = jsonArray.getJSONObject(i);
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
                 */
                bundle.putString("results", jsonText);
                jsonReceiver.send(STATUS_FINISHED, bundle);
            } catch (Exception e) {
                jsonReceiver.send(STATUS_ERROR, bundle);
            }
        }
    }

    private String getJSONFromURL (URL url) {
        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;
        StringBuffer jsonText = new StringBuffer();

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                jsonText.append(line + "\n");
            }

            return jsonText.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
