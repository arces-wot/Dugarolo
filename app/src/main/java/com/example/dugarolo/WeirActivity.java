package com.example.dugarolo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class WeirActivity extends AppCompatActivity {
    private SeekBar openLevelSeekBar;
    private Button updateOpenLevelButton;
    private TextView weirNumber;
    private TextView openLevel;
    private Integer currentLevel;
    private String weirToUpdate;
    private Integer minOpenLevel;
    private Integer maxOpenLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weir);
        minOpenLevel = getIntent().getExtras().getInt("Min Level");
        maxOpenLevel = getIntent().getExtras().getInt("Max Level");
        currentLevel = getIntent().getExtras().getInt("Open Level");
        weirNumber = findViewById(R.id.weir_number);
        weirNumber.setText("Weir #" + getIntent().getExtras().get("Number").toString());
        openLevel = findViewById(R.id.water_level);
        openLevel.setText("Open level is: " + getIntent().getExtras().get("Open Level").toString() + "mm");
        updateOpenLevelButton = findViewById(R.id.udpate_water_level);
        updateOpenLevelButton.setEnabled(false);
        openLevelSeekBar = findViewById(R.id.new_level);
        openLevelSeekBar.setMax(maxOpenLevel);
        openLevelSeekBar.setProgress(currentLevel);
        weirToUpdate = getIntent().getExtras().get("Number").toString();

        openLevelSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress <= minOpenLevel) {
                    seekBar.setProgress(minOpenLevel);
                }
                currentLevel = seekBar.getProgress();
                openLevel.setText("Open level is: " + currentLevel + "mm");
                updateOpenLevelButton.setEnabled(true);
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
        new PostNewOpenLevel(currentLevel).execute();
    }

    private class PostNewOpenLevel extends AsyncTask<Void, Void, Boolean> {

        private Integer openLevel;

        public PostNewOpenLevel(Integer newOpenLevel) {
            openLevel = newOpenLevel;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            try {
                URL url = new URL("http://mml.arces.unibo.it:3000/v0/WDmanager/{id}/wdn/nodes/" + weirToUpdate +"/open_level");
                /*
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("current", currentLevel);

                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String,Object> param : params.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                byte[] postDataBytes = postData.toString().getBytes("UTF-8");
                */
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(15000);
                conn.connect();

                String openLevelString = openLevel.toString();
                try(OutputStream os = conn.getOutputStream()) {
                    byte[] input = openLevelString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                conn.getOutputStream().flush();
                conn.disconnect();
                return true;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Intent intent = new Intent(WeirActivity.this, MapDetailActivity.class);
            intent.putExtra("Weir Number", weirToUpdate);
            intent.putExtra("Open Level", currentLevel);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

}
