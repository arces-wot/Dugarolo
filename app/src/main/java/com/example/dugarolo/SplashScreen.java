package com.example.dugarolo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.google.android.material.tabs.TabLayout;

import org.joda.time.DateTime;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class SplashScreen extends AppCompatActivity {
    private AssetLoader assetLoader= new AssetLoader();
    private ArrayList<Farm> farms=new ArrayList<>();
    private ArrayList<Request> requests=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new LoadFarmsAndRequests().execute();
        final Intent i=new Intent(this, MainActivity.class);
        i.putParcelableArrayListExtra("REQUESTS",requests);
        i.putExtra("FARMS",farms);
        new CountDownTimer(1500, 1000) {

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
            return true;
        }

    }




}
