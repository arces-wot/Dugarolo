package com.example.dugarolo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

public class RequestDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_REQUEST_ID = "id";
    private Integer requestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);
        RequestLab requestLab = RequestLab.get(this);
        List<Request> requestList = requestLab.getRequestList();
        //abilita il bottone "Up"
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //prendi la request dall'intent
        int requestId = (Integer) getIntent().getExtras().get(EXTRA_REQUEST_ID);
        setRequestId(requestId);
        //Request request = Request.requests[requestId];
        Request request = requestList.get(requestId);
        TextView farmName = findViewById(R.id.request);
        farmName.setText(request.getName());
    }

    public void onClickSubmit(View view) {
        RequestLab requestLab = RequestLab.get(this);
        List<Request> requestList = requestLab.getRequestList();
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        //Request request = Request.requests[requestId];
        Request request = requestList.get(requestId);
        //RadioButton radioButton = findViewById(radioButtonId);
        switch (radioButtonId) {
            case R.id.cancelled:
                request.setStatusIconId(R.drawable.request_cancelled);
                break;
            case R.id.interrupted:
                request.setStatusIconId(R.drawable.request_interrupted);
                break;
            case R.id.satisfied:
                request.setStatusIconId(R.drawable.request_completed);
                break;
            default:
        }
        Intent intent = new Intent(RequestDetailsActivity.this, MainActivity.class);
        intent.putExtra(MainActivity.STATUS_ICON_ID, request.getStatusIconId());
        startActivity(intent);
    }

    public Integer getRequestId() {
        return this.requestId;
    }
    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }
}
