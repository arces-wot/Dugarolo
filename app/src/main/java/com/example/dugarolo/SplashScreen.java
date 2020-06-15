package com.example.dugarolo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.appcompat.app.AppCompatActivity;

import org.joda.time.DateTime;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class SplashScreen extends AppCompatActivity {
    private AssetLoader assetLoader = new AssetLoader();
    private ArrayList<Farm> farms = new ArrayList<>();
    private ArrayList<Farm> farms2 = new ArrayList<>();
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
            //assetLoader.loadRequests(farms, requests);
            assetLoader.loadWDN(canals);
            assetLoader.loadGeoPointsWeirs(weirs);
            assetLoader.updateCurrentOpenLevels(weirs);

            return true;
        }

        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (aBoolean) {
                for(int i=0;i<250;i++)
                    farms2.add(farms.get(i));
                //i.putParcelableArrayListExtra("REQUESTS", requests);
                i.putParcelableArrayListExtra("FARMS", farms2);
                i.putParcelableArrayListExtra("WEIRS", weirs);
                i.putParcelableArrayListExtra("CANALS", canals);
                startActivity(i);
                finish();
            }

        }


    }


}
