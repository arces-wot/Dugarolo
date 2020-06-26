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

public class FilterHandler {

    public void buildFilterDialog(Context context, ArrayList<Request> requestsFiltering){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_filter);
        TextView negativeButton=dialog.findViewById(R.id.negativeFilterButton);
        TextView positiveButton=dialog.findViewById(R.id.positiveFilterButton);
        RadioGroup radioGroup=dialog.findViewById(R.id.radioFilterGroup);

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selected= radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton=dialog.findViewById(selected);
                //Toast.makeText(MainActivity.this,radioButton.getText().toString(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                String selectedS = radioButton.getText().toString();
                checkFilterWants(selectedS, context, requestsFiltering);
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

    public void checkFilterWants(String s, Context context, ArrayList<Request> requestsFiltering){


        switch (s){

            case "Status":

                Dialog dialogS = new Dialog(context);
                dialogS.setContentView(R.layout.get_filter_status);
                TextView negativeButtonS=dialogS.findViewById(R.id.negativeFilterButton1);
                TextView positiveButtonS=dialogS.findViewById(R.id.positiveFilterButton1);
                RadioGroup radioGroupS=dialogS.findViewById(R.id.radioFilterGroup1);


                positiveButtonS.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View v) {
                        int selected= radioGroupS.getCheckedRadioButtonId();
                        RadioButton radioButton=dialogS.findViewById(selected);


                        if (radioButton.getText().toString().equals(context.getString(R.string.accepted_request)))
                            requestsFiltering.removeIf(requests -> !(requests.getStatus().equals("Accepted") || (requests.getStatus().equals("Scheduled"))));
                        else if (radioButton.getText().toString().equals(context.getString(R.string.interrupted_request))) 
                            requestsFiltering.removeIf(requests -> !(requests.getStatus().equals("Interrupted")));
                        else if (radioButton.getText().toString().equals(context.getString(R.string.ongoing_request)))
                            requestsFiltering.removeIf(requests -> !(requests.getStatus().equals("Ongoing")));

                        TodayTab.setChanged(requestsFiltering);
                        dialogS.dismiss();
                    }
                });

                negativeButtonS.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogS.dismiss();
                        buildFilterDialog(context, requestsFiltering);
                    }
                });

                dialogS.show();

                break;

            case "Type":

                Dialog dialogT = new Dialog(context);
                dialogT.setContentView(R.layout.get_type_request);
                TextView negativeButtonT=dialogT.findViewById(R.id.negativeFilterButton2);
                TextView positiveButtonT=dialogT.findViewById(R.id.positiveFilterButton2);
                RadioGroup radioGroupT=dialogT.findViewById(R.id.radioFilterGroup2);


                positiveButtonT.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View v) {
                        int selected= radioGroupT.getCheckedRadioButtonId();
                        RadioButton radioButton=dialogT.findViewById(selected);

                        if (radioButton.getText().toString().equals(context.getString(R.string.radio_filter_getType_1))){
                            requestsFiltering.removeIf(requests -> !(requests.getType().equals("CBEC")));
                        }else if (radioButton.getText().toString().equals(context.getString(R.string.radio_filter_getType_2))) {
                            requestsFiltering.removeIf(requests -> !(requests.getType().equals("criteria")));
                        }

                        TodayTab.setChanged(requestsFiltering);
                        dialogT.dismiss();
                    }
                });

                negativeButtonT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogT.dismiss();
                        buildFilterDialog(context, requestsFiltering);
                    }
                });

                dialogT.show();

                break;

            case "Canals":

                Dialog dialogC = new Dialog(context);
                dialogC.setContentView(R.layout.get_canal_name);
                TextView negativeButtonC=dialogC.findViewById(R.id.negativeFilterButton3);
                TextView positiveButtonC=dialogC.findViewById(R.id.positiveFilterButton3);
                final String[] selected = {""};

                ArrayList<String> canalsName = getCanalsNameForFilter(requestsFiltering);

                Spinner spinner = (Spinner)dialogC.findViewById(R.id.spinnerCanals);

                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                        (context, android.R.layout.simple_spinner_item,
                                canalsName);

                spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                        .simple_spinner_dropdown_item);

                spinner.setAdapter(spinnerArrayAdapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int position, long arg3)
                    {
                        selected[0] = parent.getItemAtPosition(position).toString();
                    }

                    public void onNothingSelected(AdapterView<?> arg0){
                        selected[0] = "";
                    }
                });

                positiveButtonC.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View v) {
                        requestsFiltering.removeIf(requests -> !(requests.getNameChannel().equals(selected[0])));
                        TodayTab.setChanged(requestsFiltering);
                        dialogC.dismiss();
                    }
                });

                negativeButtonC.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogC.dismiss();
                        buildFilterDialog(context, requestsFiltering);
                    }
                });

                dialogC.show();

                break;
        }
    }

    public ArrayList<String> getCanalsNameForFilter(ArrayList<Request> requests){

        ArrayList<String> canalsName = new ArrayList<String>();
        int check=0;

        for(int i=0;i<requests.size();i++){
            if(canalsName.size()!=0) {
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
