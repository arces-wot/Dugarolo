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
    private ArrayList<Farm> farms1 = new ArrayList<>();
    private ArrayList<Farm> farms2 = new ArrayList<>();
    private ArrayList<Farm> farms3 = new ArrayList<>();
    private ArrayList<Farm> farms4 = new ArrayList<>();
    private ArrayList<Farm> farms5 = new ArrayList<>();
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
                for (int i = 0; i < farms.size() - 150; i++)
                    if (i < 200)
                        farms1.add(farms.get(i));
                    else if (i < 400)
                        farms2.add(farms.get(i));
                    else if (i < 600)
                        farms3.add(farms.get(i));
                    else if (i < 800)
                        farms4.add(farms.get(i));
                    else if (i < 1000)
                        farms5.add(farms.get(i));


                //i.putParcelableArrayListExtra("REQUESTS", requests);
               /* i.putParcelableArrayListExtra("FARMS1", farms1);
                i.putParcelableArrayListExtra("FARMS2", farms2);
                i.putParcelableArrayListExtra("FARMS3", farms3);
                i.putParcelableArrayListExtra("FARMS4", farms4);
                //i.putParcelableArrayListExtra("FARMS5", farms5);
>>>>>>> 63f91c6bc22c240e0dd1b8fda23b2bbd643e890b
                i.putParcelableArrayListExtra("WEIRS", weirs);
                i.putParcelableArrayListExtra("CANALS", canals);*/
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
