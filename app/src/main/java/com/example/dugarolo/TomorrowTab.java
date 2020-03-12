package com.example.dugarolo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class TomorrowTab extends Fragment {
    private static final String TAG = "Domani";

    public TomorrowTab() {
        // Required empty public constructor
    }

    public static TomorrowTab newInstance() {
        return new TomorrowTab();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_tomorrow, container, false);
        final TextView textView = root.findViewById(R.id.section_label);

        return root;
    }
}