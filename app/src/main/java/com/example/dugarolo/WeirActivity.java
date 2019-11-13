package com.example.dugarolo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class WeirActivity extends AppCompatActivity {
    private SeekBar openLevelSeekBar;
    private Button updateOpenLevelButton;
    private TextView farmName;
    private TextView weirNumber;
    private TextView openLevel;
    private Integer newOpenLevel;
    private String weirToUpdate;
    private Integer minOpenLevel;
    private Integer maxOpenLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weir);
        minOpenLevel = 15;
        maxOpenLevel = 300;
        farmName = findViewById(R.id.farm_name);
        farmName.setText(getIntent().getExtras().get("Farm") + "\'s farm");
        weirNumber = findViewById(R.id.weir_number);
        weirNumber.setText("Weir #" + getIntent().getExtras().get("Number").toString());
        openLevel = findViewById(R.id.water_level);
        openLevel.setText("Open level is: " + getIntent().getExtras().get("Open Level").toString() + "mm");
        updateOpenLevelButton = findViewById(R.id.udpate_water_level);
        updateOpenLevelButton.setEnabled(false);
        openLevelSeekBar = findViewById(R.id.new_level);
        openLevelSeekBar.setMax(maxOpenLevel);
        openLevelSeekBar.setProgress((Integer) getIntent().getExtras().get("Open Level"));
        weirToUpdate = getIntent().getExtras().get("Number").toString();

        openLevelSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress == 0) {
                    newOpenLevel = minOpenLevel;
                }
                else if(progress == seekBar.getMax()) {
                    newOpenLevel = maxOpenLevel - minOpenLevel;
                }
                else {
                    newOpenLevel = progress;
                    openLevel.setText("Open level is: " + newOpenLevel + "mm");
                    updateOpenLevelButton.setEnabled(true);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void onClickUpdateOpenLevel(View view) {
        Intent intent = new Intent(WeirActivity.this, MapDetailActivity.class);
        intent.putExtra("Weir Number", weirToUpdate);
        intent.putExtra("Open Level", newOpenLevel);
        setResult(RESULT_OK, intent);
        finish();
    }
}
