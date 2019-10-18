package com.example.dugarolo;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import androidx.annotation.NonNull;

public class RequestsAdapter extends ArrayAdapter<Request> {

    public RequestsAdapter(@NonNull Context context, Request[] requests) {
        super(context, 0, requests);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Request request = getItem(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_request, parent, false);
        }
        ImageView basicIcon = (ImageView) convertView.findViewById(R.id.basic_icon);
        TextView farmName = (TextView) convertView.findViewById(R.id.farm_name);
        ImageView statusIcon = (ImageView) convertView.findViewById(R.id.status_icon);

        basicIcon.setImageResource(request.getBasicIconId());
        farmName.setText(Html.fromHtml(request.getName() + "<br />" + "<small>" + request.getDate().toString() + "</small"));
        statusIcon.setImageResource(request.getStatusIconId());
        return convertView;
    }
}
