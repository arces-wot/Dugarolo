package com.example.dugarolo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class RequestDetailsActivity extends AppCompatActivity {

    private ArrayList<Request> requestList = new ArrayList<>();
    private Integer requestId;
    private RadioGroup radioGroup;
    private ViewGroup vg;
    private EditText editTextReasonCancelled;
    private EditText editTextStartDateTimeSatisfied;
    private EditText editTextEndDateTimeSatisfied;
    private EditText waterVolumeSatisfied;
    private TextView currentStatusTextView;
    private ImageView currentStatusImageView;
    private TextView messageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        radioGroup = findViewById(R.id.radioGroup);
        vg = findViewById(R.id.detailsForm);
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
        currentStatusTextView = findViewById(R.id.current_status);
        currentStatusTextView.setText(request.getStatus());
        messageTextView = findViewById(R.id.message);
        String message = request.getMessage();
        if(message == null || message.equals("")) {
            messageTextView.setText(getResources().getString(R.string.no_message));
        }
        else {
            messageTextView.setText(message);
        }
        colorStatusIcon(request.getStatus());
        buildLayout(request.getStatus());
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(vg.getChildCount() > 0) {
                    vg.removeAllViews();
                }
                LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                switch (checkedId) {
                    case R.id.satisfied:
                        View v = vi.inflate(R.layout.satisfied_request_form, null);
                        vg.addView(v, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        break;
                    case R.id.cancelled:
                        View v1 = vi.inflate(R.layout.cancelled_request_form,  null);
                        vg.addView(v1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        break;
                    default:
                }
            }
        });
    }

    private void colorStatusIcon(String status) {
        currentStatusImageView = findViewById(R.id.status_icon);
        switch(status) {
            case "Ongoing":
                currentStatusImageView.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorOngoing, null));
                break;
            case "Accepted":
                currentStatusImageView.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorAccepted, null));
                break;
            case "Interrupted":
                currentStatusImageView.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorInterrupted, null));
                break;
            case "Satisfied":
                currentStatusImageView.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorSatisfied, null));
                break;
            case "Scheduled":
                currentStatusImageView.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorScheduled, null));
                break;
            case "Cancelled":
                currentStatusImageView.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorCancelled, null));
                break;
        }

    }

    public void onClickSubmit(View view) {
        try {
            int radioButtonId = radioGroup.getCheckedRadioButtonId();
            //Request request = Request.requests[requestId];
            Request request = requestList.get(requestId);
            //RadioButton radioButton = findViewById(radioButtonId);
            JSONObject json = new JSONObject();
            String message = "";
            switch (radioButtonId) {
                case R.id.cancelled:
                    editTextReasonCancelled = findViewById(R.id.reason_edit_text);
                    request.setStatus("Cancelled");
                    message = editTextReasonCancelled.getText().toString();
                    break;
                case R.id.interrupted:
                    request.setStatus("Interrupted");
                    break;
                case R.id.satisfied:
                    request.setStatus("Satisfied");
                    editTextStartDateTimeSatisfied = findViewById(R.id.start_date_time);
                    editTextEndDateTimeSatisfied = findViewById(R.id.end_date_time);
                    waterVolumeSatisfied = findViewById(R.id.water_volume);
                    String sdts = editTextStartDateTimeSatisfied.getText().toString();
                    String edts = editTextEndDateTimeSatisfied.getText().toString();
                    String wvs = waterVolumeSatisfied.getText().toString();
                    message = "Start: " +  sdts + ", End: " + edts + ", Water Volume: " + wvs + " mm";
                    break;
                case R.id.accepted:
                    request.setStatus("Accepted");
                    message = "The request has been accepted";
                    break;
                case R.id.ongoing:
                    request.setStatus("Ongoing");
                    break;
                default:
            }
            json.put("message", message);
            json.put("status", request.getStatus());
            new PostNewStatus(json, request).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void buildLayout(String status) {
        if(radioGroup.getChildCount() > 0) {
            radioGroup.removeAllViews();
        }
        RadioButton radioButton = new RadioButton(this);
        RadioButton radioButton1 = new RadioButton(this);
        RadioButton radioButton2 = new RadioButton(this);
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);

        ConstraintLayout cl = findViewById(R.id.constraint_layout);
        TextView select_status = findViewById(R.id.selectStatus);
        switch (status) {
            case "Scheduled":
                radioButton.setText(R.string.accepted_request);
                radioButton.setId(R.id.accepted);
                radioGroup.addView(radioButton, params);

                radioButton1.setText(R.string.cancelled_request);
                radioButton1.setId(R.id.cancelled);
                radioGroup.addView(radioButton1, params);
                break;
            case "Accepted":
                radioButton.setId(R.id.cancelled);
                radioButton.setText(R.string.cancelled_request);
                radioGroup.addView(radioButton, params);

                radioButton1.setId(R.id.ongoing);
                radioButton1.setText(R.string.ongoing_request);
                radioGroup.addView(radioButton1, params);
                break;
            case "Ongoing":
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

            case "Interrupted":
                radioButton.setId(R.id.cancelled);
                radioButton.setText(R.string.cancelled_request);
                radioGroup.addView(radioButton, params);

                radioButton1.setId(R.id.ongoing);
                radioButton1.setText(R.string.ongoing_request);
                radioGroup.addView(radioButton1, params);

                break;
            case "Satisfied":
                select_status.setText("The request has been satisfied.");
                cl.removeView(findViewById(R.id.submitButton));
                break;
            case "Cancelled":
                select_status.setText("The request has been cancelled.");
                cl.removeView(findViewById(R.id.submitButton));
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

    private class PostNewStatus extends AsyncTask<Void, Void, String> {

        private JSONObject jsonObject;
        private Request request;

        public PostNewStatus(JSONObject newStatus, Request requestToUpdate) {
            jsonObject = newStatus;
            request = requestToUpdate;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://mml.arces.unibo.it:3000/v0/WDmanager/{id}/WDMInspector/{ispector}/AssignedFarms/" + this.request.getField().getId() + "/irrigation_plan/" + this.request.getId() + "/status");
                /*
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("json", jsonObject);

                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                byte[] postDataBytes = postData.toString().getBytes("UTF-8");
                */

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(15000);
                conn.connect();

                String jsonInputString = jsonObject.toString();
                try(OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
                conn.getOutputStream().flush();
                String res = "";
                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    res = response.toString();
                }
                conn.disconnect();
                return res;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String aString) {
            super.onPostExecute(aString);
            System.out.println("---- POST REQUEST RESPONSE ----");
            System.out.print(aString);
            Intent intent = new Intent(RequestDetailsActivity.this, MainActivity.class);
            intent.putExtra(MainActivity.REQUEST_STATUS, request.getStatus());
            startActivity(intent);
        }
    }
}
