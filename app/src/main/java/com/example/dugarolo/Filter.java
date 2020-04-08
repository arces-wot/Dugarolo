package com.example.dugarolo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class Filter extends ArrayAdapter {

    String[] Text;
    Context context;

    public Filter(Context context, int resource, String[] text) {
        super(context, resource, text);
        Text = text;
        this.context = context;
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View view = inflater.inflate(R.layout.item_filter, parent, false);

        //Set Custom View
        TextView tv = (TextView)view.findViewById(R.id.nameCanal);

        tv.setText(Text[position]);

        return view;
    }

    @Override
    public View getDropDownView(int position,View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
}