package com.example.dugarolo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

public class RequestDetailsActivity extends AppCompatActivity {

    private ArrayList<Request> requestList = new ArrayList<>();
    private Integer requestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);
        RequestLab requestLab = RequestLab.get(this);
        //List<Request> requestList = requestLab.getRequestList();
        requestList = getIntent().getParcelableArrayListExtra("REQUEST_LIST");
        //abilita il bottone "Up"
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //prendi la request dall'intent
        int requestId = (Integer) getIntent().getExtras().get("REQUEST_CLICKED");
        setRequestId(requestId);
        //Request request = Request.requests[requestId];
        Request request = requestList.get(requestId);
        TextView farmName = findViewById(R.id.request);
        DateTime dateTime = request.getDateTime();
        DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
        String formattedDateTime = dateTime.toString(dtf);
        farmName.setText(Html.fromHtml(request.getName() + "<br />"+ "<small>"  + formattedDateTime
                + " , water: " + request.getWaterVolume() + " mm" + "</small"));
    }

    public void onClickSubmit(View view) {
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        //Request request = Request.requests[requestId];
        Request request = requestList.get(requestId);
        //RadioButton radioButton = findViewById(radioButtonId);
        switch (radioButtonId) {
            case R.id.cancelled:
                request.setStatus("cancelled");
                break;
            case R.id.interrupted:
                request.setStatus("interrupted");
                break;
            case R.id.satisfied:
                request.setStatus("satisfied");
                break;
            default:
        }
        Intent intent = new Intent(RequestDetailsActivity.this, MainActivity.class);
        intent.putExtra(MainActivity.REQUEST_STATUS, request.getStatus());
        startActivity(intent);
    }

    public Integer getRequestId() {
        return this.requestId;
    }
    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }
}
