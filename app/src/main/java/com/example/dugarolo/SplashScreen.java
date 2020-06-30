package com.example.dugarolo;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;
import org.osmdroid.util.GeoPoint;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
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
            Gson gson = new Gson();
            String jsonFarms=readFarmFromFile();
            if (jsonFarms == null) {
                assetLoader.loadGeoPointsFarms(farms);
                writeFarmToFile();
            }else{
            Type typeFarm = new TypeToken<ArrayList<Farm>>() {
            }.getType();
            farms = gson.fromJson(jsonFarms, typeFarm);
            }
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
            Request request0 = new Request("1", "Name", DateTime.now(), "Accepted", "10", field , "message", "Channel", "cbec","anal");
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
        //String jsonFarms = gson.toJson(farms);
        String jsonFarms = readFarmFromFile();
        String jsonRequests = gson.toJson(requests);
        String jsonWeirs = gson.toJson(weirs);
        String jsonCanals = gson.toJson(canals);
        editor.putString("FARMS", jsonFarms);
        editor.putString("REQUESTS", jsonRequests);
        editor.putString("WEIRS", jsonWeirs);
        editor.putString("CANALS", jsonCanals);
        editor.apply();
    }

    private void writeFarmToFile() {
        Gson gson = new Gson();
        String jsonFarms = gson.toJson(farms);
        FileOutputStream fos = null;
        try {
            fos = openFileOutput("farmData.txt", MODE_PRIVATE);
            fos.write(jsonFarms.getBytes());
            fos.close();
            //Toast.makeText(this, "Saved to " + getFilesDir() + "/" + "farmData.txt", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    private String readFarmFromFile() {
        try {
            FileInputStream fileInputStream = openFileInput("farmData.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();

            String lines;
            while ((lines = bufferedReader.readLine()) != null) {
                stringBuffer.append(lines);
            }

            return stringBuffer.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}
