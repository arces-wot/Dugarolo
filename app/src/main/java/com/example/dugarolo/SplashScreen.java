package com.example.dugarolo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SplashScreen extends AppCompatActivity {
    private AssetLoader assetLoader= new AssetLoader();
    private ArrayList<Farm> farms=new ArrayList<>();
    private ArrayList<Request> requests=new ArrayList<>();
    private ArrayList<Weir> weirs = new ArrayList<>();
    private ArrayList<Canal> canals = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new LoadFarmsAndRequests().execute();
        final Intent i=new Intent(this, MainActivity.class);
        i.putParcelableArrayListExtra("REQUESTS",requests);
        i.putParcelableArrayListExtra("FARMS",farms);
        i.putParcelableArrayListExtra("WEIRS",weirs);
        i.putParcelableArrayListExtra("CANALS",canals);

        new CountDownTimer(1500, 1500) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                    startActivity(i);
                    finish();

            }
        }.start();

    }

    private class LoadFarmsAndRequests extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            assetLoader.loadGeoPointsFarms(farms);
            assetLoader.loadRequests(farms, requests);
            assetLoader.loadWDN(canals);
            assetLoader.loadGeoPointsWeirs(weirs);
            assetLoader.updateCurrentOpenLevels(weirs);

            return true;
        }

    }




}
