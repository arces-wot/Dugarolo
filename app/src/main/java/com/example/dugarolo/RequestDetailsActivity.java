package com.example.dugarolo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class RequestDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_REQUEST_ID = "id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);
        //abilita il bottone "Up"
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //prendi la request dall'intent
        int requestId = (Integer) getIntent().getExtras().get(EXTRA_REQUEST_ID);
        Request request = Request.requests.get(requestId);
        TextView farmName = (TextView) findViewById(R.id.request);
        farmName.setText(request.getName());
    }
}
