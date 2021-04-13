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
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;

import static org.joda.time.Period.days;

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

            assetLoader.loadRequests( farms,requests);
            assetLoader.loadWDN(canals);
            assetLoader.loadGeoPointsWeirs(weirs);
            assetLoader.updateCurrentOpenLevels(weirs);
            /*
            try {
                assetLoader.loadSpecificRequest(from, from.plusDays(1),farms, requests);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            */


            //GUAI A CHI LA TOCCA
            //Sample Request
            final GeoPoint startPoint = new GeoPoint(44.778325, 10.720202);
            ArrayList<GeoPoint> geoPoints = new ArrayList<GeoPoint>();
            geoPoints.add(startPoint);
            Field field = new Field("http://swamp-project.org/cbec/farmer_91268487", "http://swamp-project.org/cbec/field_25905", geoPoints);
            field=farms.get(0).getFields().get(0);
            Request request0 = new Request("http://swamp-project.org/cbec/farmer_91268487", "http://swamp-project.org/cbec/farmer_91268487", DateTime.now(), "Ongoing", "10", field , "message", "Channel", "CBEC","Fosdondo");
            requests.add(request0);
            requests.add(new Request("http://swamp-project.org/cbec/farmer_91018883", "http://swamp-project.org/cbec/farmer_91018883", DateTime.now(),
                    "Accepted", "10", farms.get(1).getFields().get(0), "message", "Channel", "Criteria","Fosdondo"));
            requests.add(new Request("http://swamp-project.org/cbec/farmer_83801", "http://swamp-project.org/cbec/farmer_83801", DateTime.now(),
                    "Scheduled", "10", farms.get(2).getFields().get(0), "message", "Channel", "CBEC","Fosdondo"));
            requests.add(new Request("http://swamp-project.org/cbec/farmer_103059", "http://swamp-project.org/cbec/farmer_103059", DateTime.now(),
                    "Accepted", "10", farms.get(3).getFields().get(0), "message", "Channel", "CBEC","Fosdondo"));
            requests.add(new Request("http://swamp-project.org/cbec/farmer_120092", "http://swamp-project.org/cbec/farmer_120092", DateTime.now().plusDays(1),
                    "Accepted", "10", farms.get(4).getFields().get(0), "message", "Channel", "CBEC","Fosdondo"));
            requests.add(new Request("http://swamp-project.org/cbec/farmer_21545", "http://swamp-project.org/cbec/farmer_21545", DateTime.now().plusDays(1),
                    "Accepted", "10", farms.get(5).getFields().get(0), "message", "Channel", "CBEC","Fosdondo"));
            requests.add(new Request("http://swamp-project.org/cbec/farmer_67356", "http://swamp-project.org/cbec/farmer_67356", DateTime.now().plusDays(1),
                    "Accepted", "10", farms.get(6).getFields().get(0), "message", "Channel", "CBEC","Fosdondo"));


            requests.add(new Request("http://swamp-project.org/cbec/farmer_67356", "http://swamp-project.org/cbec/farmer_67356", DateTime.now().minusMonths(1),
                    "Accepted", "10", farms.get(6).getFields().get(0), "message", "Channel", "CBEC","Fosdondo"));
            requests.add(new Request("http://swamp-project.org/cbec/farmer_67356", "http://swamp-project.org/cbec/farmer_67356", DateTime.now().minusMonths(1),
                    "Accepted", "10", farms.get(6).getFields().get(0), "message", "Channel", "CBEC","Fosdondo"));
            requests.add(new Request("http://swamp-project.org/cbec/farmer_67356", "http://swamp-project.org/cbec/farmer_67356", DateTime.now().minusMonths(1),
                    "Accepted", "10", farms.get(6).getFields().get(0), "message", "Channel", "CBEC","Fosdondo"));
            requests.add(new Request("http://swamp-project.org/cbec/farmer_67356", "http://swamp-project.org/cbec/farmer_67356", DateTime.now().minusMonths(1),
                    "Accepted", "10", farms.get(6).getFields().get(0), "message", "Channel", "CBEC","Fosdondo"));

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
