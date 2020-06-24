package com.example.dugarolo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.joda.time.DateTime;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.LinkedList;

public class SplashScreen extends AppCompatActivity {
    private AssetLoader assetLoader = new AssetLoader();
    private ArrayList<Farm> farms = new ArrayList<>();
    private ArrayList<Request> requests = new ArrayList<>();
    private ArrayList<Weir> weirs = new ArrayList<>();
    private ArrayList<Canal> canals = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new LoadFarmsAndRequests().execute();
    }

    private class LoadFarmsAndRequests extends AsyncTask<Void, Void, Boolean> {
        final Intent i = new Intent(getApplicationContext(), MainActivity.class);

        @Override
        protected Boolean doInBackground(Void... voids) {
            assetLoader.loadGeoPointsFarms(farms);
            assetLoader.loadRequests(farms, requests);
            assetLoader.loadWDN(canals);
            assetLoader.loadGeoPointsWeirs(weirs);
            assetLoader.updateCurrentOpenLevels(weirs);

            //GUAI A CHI LA TOCCA
            //Sample Request
            /*final GeoPoint startPoint = new GeoPoint(44.778325, 10.720202);
            ArrayList<GeoPoint> geoPoints = new ArrayList<GeoPoint>();
            geoPoints.add(startPoint);
            Field field = new Field("farmName", "2", geoPoints);
            Request request0 = new Request("1", "Name", DateTime.now(), "Accepted", "10", field , "message", "Channel", "cbec");
            requests.add(request0);*/


            return true;
        }

        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (aBoolean) {
                saveData();
                startActivity(i);
                finish();
            }

        }


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


}
