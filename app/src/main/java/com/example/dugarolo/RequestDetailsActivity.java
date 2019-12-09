package com.example.dugarolo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.RadioButton;
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
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);
        radioGroup = findViewById(R.id.radioGroup);
        //RequestLab requestLab = RequestLab.get(this);
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
        buildRadioGroup(request.getStatus());
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.satisfied:
                        
                }
            }
        });
    }

    public void onClickSubmit(View view) {
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

    private void buildRadioGroup(String status) {
        RadioButton radioButton = new RadioButton(this);
        RadioButton radioButton1 = new RadioButton(this);
        RadioButton radioButton2 = new RadioButton(this);
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        switch (status) {
            case "scheduled":
                radioButton.setText(R.string.accepted_request);
                radioButton.setId(R.id.accepted);
                radioGroup.addView(radioButton, params);

                radioButton1.setText(R.string.cancelled_request);
                radioButton1.setId(R.id.cancelled);
                radioGroup.addView(radioButton1, params);
                break;
            case "accepted":
                radioButton.setId(R.id.cancelled);
                radioButton.setText(R.string.cancelled_request);
                radioGroup.addView(radioButton, params);

                radioButton1.setId(R.id.ongoing);
                radioButton1.setText(R.string.ongoing_request);
                radioGroup.addView(radioButton, params);
                break;
            case "ongoing":
                radioButton.setId(R.id.cancelled);
                radioButton.setText(R.string.cancelled_request);
                radioGroup.addView(radioButton, params);

                radioButton1.setId(R.id.interrupted);
                radioButton1.setText(R.string.interrupted_request);
                radioGroup.addView(radioButton1, params);

                radioButton2.setId(R.id.satisfied);
                radioButton2.setText(R.string.satisfied_request);
                radioGroup.addView(radioButton2, params);

                break;

            case "interrupted":
                radioButton.setId(R.id.cancelled);
                radioButton.setText(R.string.cancelled_request);
                radioGroup.addView(radioButton, params);

                radioButton1.setId(R.id.ongoing);
                radioButton1.setText(R.string.ongoing_request);
                radioGroup.addView(radioButton1, params);

                break;

            default:
        }
    }

    public Integer getRequestId() {
        return this.requestId;
    }
    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }
}
