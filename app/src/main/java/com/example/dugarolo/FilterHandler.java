package com.example.dugarolo;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collections;

public class FilterHandler {

    public void buildFilterDialog(Context context, ArrayList<Request> requestsFiltering) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_filter);
        TextView negativeButton = dialog.findViewById(R.id.negativeFilterButton);
        TextView positiveButton = dialog.findViewById(R.id.positiveFilterButton);
        Spinner spinnerStatus = (Spinner) dialog.findViewById(R.id.spinner_status);
        Spinner spinnerTypes = (Spinner) dialog.findViewById(R.id.spinner_type);
        Spinner spinnerCanals = (Spinner) dialog.findViewById(R.id.spinner_canals);
        final String[] selectedStatus = new String[1];
        final String[] selectedType = new String[1];
        final String[] selectedCanal = new String[1];


        ArrayList<String> canalsName = getCanalsNameForFilter(requestsFiltering);
        ArrayList<String> types = new ArrayList<String>();
        types.add("");
        types.add("Manuale");
        types.add("Criteria");
        ArrayList<String> status = new ArrayList<String>();
        status.add("");
        status.add(context.getResources().getString(R.string.accepted_request));
        status.add(context.getResources().getString(R.string.interrupted_request));
        status.add(context.getResources().getString(R.string.ongoing_request));


        ArrayAdapter<String> spinnerArrayAdapterCanals = new ArrayAdapter<String>
                (context, android.R.layout.simple_spinner_item,
                        canalsName);
        ArrayAdapter<String> spinnerArrayAdapterTypes = new ArrayAdapter<String>
                (context, android.R.layout.simple_spinner_item,
                        types);
        ArrayAdapter<String> spinnerArrayAdapterStatus = new ArrayAdapter<String>
                (context, android.R.layout.simple_spinner_item,
                        status);

        spinnerArrayAdapterCanals.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinnerArrayAdapterTypes.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinnerArrayAdapterStatus.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);

        spinnerCanals.setAdapter(spinnerArrayAdapterCanals);
        spinnerTypes.setAdapter(spinnerArrayAdapterTypes);
        spinnerStatus.setAdapter(spinnerArrayAdapterStatus);

        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long arg3) {
                selectedStatus[0] = parent.getItemAtPosition(position).toString();
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                selectedStatus[0] = "";
            }
        });

        spinnerTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long arg3) {
                selectedType[0] = parent.getItemAtPosition(position).toString();
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                selectedType[0] = "";
            }
        });

        spinnerCanals.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long arg3) {
                selectedCanal[0] = parent.getItemAtPosition(position).toString();
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                selectedCanal[0] = "";
            }
        });

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if (!selectedCanal[0].equals(""))
                    requestsFiltering.removeIf(requests -> !(requests.getNameChannel().equals(selectedCanal[0])));
                if (!selectedStatus[0].equals(""))
                    requestsFiltering.removeIf(requests -> !(requests.getStatus().equals(selectedStatus[0])));
                if (!selectedType[0].equals(""))
                    requestsFiltering.removeIf(requests -> !(requests.getType().equals(selectedType[0])));
                TodayTab.setChanged(requestsFiltering);
                dialog.dismiss();
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public ArrayList<String> getCanalsNameForFilter(ArrayList<Request> requests) {

        ArrayList<String> canalsName = new ArrayList<String>();
        int check = 0;

        for (int i = 0; i < requests.size(); i++) {
            if (canalsName.size() != 0) {
                for (int y = 0; y < canalsName.size(); y++) {
                    if (canalsName.get(y).equals(requests.get(i).getNameChannel())) {
                        check = 1;
                    }
                }
            }

            if (check == 0)
                canalsName.add(requests.get(i).getNameChannel());

            check = 0;

        }

        return canalsName;
    }
}
