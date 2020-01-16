package com.example.dugarolo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.TextView;

public class KeyActivity extends AppCompatActivity {

    private TextView satisfiedRequestTextView;
    private TextView cancelledRequestTextView;
    private TextView interruptedRequestTextView;
    private TextView acceptedRequestTextView;
    private TextView ongoingRequestTextView;
    private TextView scheduledRequestTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key);
        satisfiedRequestTextView = findViewById(R.id.status_satisfied_label);
        satisfiedRequestTextView.setText(satisfiedRequestTextView.getText() + ": ");
        cancelledRequestTextView = findViewById(R.id.status_cancelled_label);
        cancelledRequestTextView.setText(cancelledRequestTextView.getText() + ": ");
        interruptedRequestTextView = findViewById(R.id.status_interrupted_label);
        interruptedRequestTextView.setText(interruptedRequestTextView.getText() + ": ");
        acceptedRequestTextView = findViewById(R.id.status_accepted_label);
        acceptedRequestTextView.setText(acceptedRequestTextView.getText() + ": ");
        ongoingRequestTextView = findViewById(R.id.status_ongoing_label);
        ongoingRequestTextView.setText(ongoingRequestTextView.getText() + ": ");
        scheduledRequestTextView = findViewById(R.id.status_scheduled_label);
        scheduledRequestTextView.setText(scheduledRequestTextView.getText() + ": ");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
