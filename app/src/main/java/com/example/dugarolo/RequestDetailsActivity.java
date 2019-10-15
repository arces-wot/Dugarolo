package com.example.dugarolo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class RequestDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_FARM_ID = "id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);
        //abilita il bottone "Up"
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //prendi la farm dall'intent
        int farmId = (Integer) getIntent().getExtras().get(EXTRA_FARM_ID);
        Farm farm = Farm.farms[farmId];
        TextView farmName = (TextView) findViewById(R.id.farm);
        farmName.setText(farm.getName());
    }
}
