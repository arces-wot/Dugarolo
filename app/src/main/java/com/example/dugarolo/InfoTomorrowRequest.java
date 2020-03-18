package com.example.dugarolo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class InfoTomorrowRequest extends AppCompatActivity {

    ArrayList<Request> requests;
    int positionRequest;
    String requestState;

    TextView companyName, requestNumber, requestStateView, requestDateTime, requestVolume, requestIrrigationTime, requestMessage;
    Button refusePlanning, acceptPlanning;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_tomorrow_request);

        companyName = findViewById(R.id.companyName);
        requestNumber = findViewById(R.id.requestNumber);
        requestStateView = findViewById(R.id.stateRequest);
        requestDateTime = findViewById(R.id.dateTimeRequest);
        requestVolume = findViewById(R.id.volumeRequested);
        requestIrrigationTime = findViewById(R.id.PhoneRequest);
        refusePlanning = findViewById(R.id.refusePlanning);
        acceptPlanning = findViewById(R.id.acceptPlanning);
        requestMessage = findViewById(R.id.messageRequest);

        getExtraFromIntent();
        requestState = getRequestState();
        setElementsInLayout();
    }

    public void getExtraFromIntent(){
        Bundle extras = getIntent().getExtras();
        this.requests = extras.getParcelableArrayList("REQUEST_LIST");
        this.positionRequest = extras.getInt("REQUEST_CLICKED");
    }


    public void setElementsInLayout(){
        DateTime dateTime = requests.get(positionRequest).getDateTime();
        String timeToIrrigate = Integer.toString(dateTime.getHourOfDay()) + ":" + Integer.toString(dateTime.getMinuteOfHour());

        companyName.setText(requests.get(positionRequest).getName());
        requestNumber.setText("Richiesta n°" + requests.get(positionRequest).getId());
        requestVolume.setText(requests.get(positionRequest).getWaterVolume());
        requestDateTime.setText(timeToIrrigate);
        requestStateView.setText(requests.get(positionRequest).getStatus());
        requestMessage.setText(requests.get(positionRequest).getMessage());


        if(requestState.equals("Accepted")){
            acceptPlanning.setVisibility(View.INVISIBLE);
            refusePlanning.setVisibility(View.INVISIBLE);

            acceptPlanning.setEnabled(false);
            refusePlanning.setEnabled(false);
        }
    }

    public String getRequestState(){
        return requests.get(positionRequest).getStatus();
    }

    public void onClickRefusePlanning(View v){

    }

    public void onClickAcceptPlanning(View v){

    }
}
