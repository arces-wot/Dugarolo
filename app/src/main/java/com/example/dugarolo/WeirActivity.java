package com.example.dugarolo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class WeirActivity extends AppCompatActivity {
    private SeekBar waterLevelSeekBar;
    private Button updateWaterLevelButton;
    private TextView farmName;
    private TextView weirNumber;
    private TextView waterLevel;
    private Integer newWaterLevel;
    private String weirToUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weir);
        farmName = findViewById(R.id.farm_name);
        farmName.setText(getIntent().getExtras().get("Farm") + "\'s farm");
        weirNumber = findViewById(R.id.weir_number);
        weirNumber.setText("Weir #" + getIntent().getExtras().get("Number").toString());
        waterLevel = findViewById(R.id.water_level);
        waterLevel.setText("Water level is: " + getIntent().getExtras().get("Water Level").toString() + "mm");
        updateWaterLevelButton = findViewById(R.id.udpate_water_level);
        updateWaterLevelButton.setEnabled(false);
        waterLevelSeekBar = findViewById(R.id.new_level);
        weirToUpdate = getIntent().getExtras().get("Number").toString();

        waterLevelSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                waterLevel.setText("Water level is: " + progress + "mm");
                updateWaterLevelButton.setEnabled(true);
                newWaterLevel = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void onClickUpdateWaterLevel(View view) {
        Intent intent = new Intent(WeirActivity.this, MapDetailActivity.class);
        intent.putExtra("Weir Number", weirToUpdate);
        intent.putExtra("Water Level", newWaterLevel);
        setResult(RESULT_OK, intent);
        finish();
    }
}
