package com.example.dugarolo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class Planning extends AppCompatActivity {
    private AdapterPlan adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planning);
        ArrayList<String> place = new ArrayList<>();
        place.add("San Michele");
        place.add("Fosdondo Nord");
        place.add("Guaspari");
        place.add("San Michele");
        place.add("Fosdondo Sud");
        ArrayList<String> flow = new ArrayList<>();
        flow.add("3 l/s");
        flow.add("4 l/s");;
        flow.add("1 l/s");
        flow.add("2 l/s");
        flow.add("3 l/s");
        ArrayList<String> time = new ArrayList<>();
        time.add("6:30");
        time.add("7:30");
        time.add("9:00");
        time.add("11:30");
        time.add("12:00");

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.timeLine);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdapterPlan( place,flow,time);
        //adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }


}


