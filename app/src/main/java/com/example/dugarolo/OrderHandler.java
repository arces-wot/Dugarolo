package com.example.dugarolo;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class OrderHandler {

    public void buildOrderDialog(Context context, ArrayList<Request> requests){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.order_dialog);
        TextView negativeButton=dialog.findViewById(R.id.negativeOrderButton);
        TextView positiveButton=dialog.findViewById(R.id.positiveOrderButton);
        RadioGroup radioGroup=dialog.findViewById(R.id.radioOrderGroup);

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selected= radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton=dialog.findViewById(selected);
                Toast.makeText(context,radioButton.getText().toString(), Toast.LENGTH_SHORT).show();
                if (radioButton.getText().toString().equals(context.getString(R.string.radio_order_1))){
                    Collections.sort(requests,new Request.SortByDate());
                }else if (radioButton.getText().toString().equals(context.getString(R.string.radio_order_2))){
                    Collections.sort(requests,new Request.SortByChannel());
                }else if (radioButton.getText().toString().equals(context.getString(R.string.radio_order_3))){
                    Collections.sort(requests,new Request.SortByStatus());
                }
                TodayTab.setChanged(requests);
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
}
