package com.example.dugarolo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class WeirActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weir);
        TextView farmName = (TextView) findViewById(R.id.farm_name);
        farmName.setText((String) getIntent().getExtras().get("Farm") + "\'s farm");
        TextView weirNumber = findViewById(R.id.weir_number);
        weirNumber.setText("Weir #" + getIntent().getExtras().get("Number").toString());
        TextView waterLevel = findViewById(R.id.water_level);
        waterLevel.setText("Water level is: " + getIntent().getExtras().get("Water Level").toString() + "mm");
    }
}
