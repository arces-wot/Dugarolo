package com.example.dugarolo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class TabsPagerAdapter extends FragmentPagerAdapter {
    @StringRes
    private static final int[] TAB_TITLES =
            new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private final Context mContext;
    private ArrayList<Request> requests;
    private FloatingActionButton fab;
    private MyMapView map;

    public TabsPagerAdapter(Context context, FragmentManager fm, ArrayList<Request> requests, MyMapView map) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mContext = context;
        this.requests = requests;
        this.map = map;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return TodayTab.newInstance(todayRequests(requests), map);
            case 1:
                return TomorrowTab.newInstance(tomorrowRequests(requests));
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return 2;
    }

    public ArrayList<Request> todayRequests(ArrayList<Request> requests) {
        DateTime now = new DateTime();
        ArrayList<Request> todayRequests = new ArrayList<>();
        for (Request r : requests) {
            if ((r.getDateTime().getDayOfYear()) == (now.getDayOfYear()) && r.getStatus().equals("Accepted")) {
                todayRequests.add(r);
            }
        }
        return todayRequests;

    }

    public ArrayList<Request> tomorrowRequests(ArrayList<Request> requests) {
        DateTime now = new DateTime();
        ArrayList<Request> tomorrowRequests = new ArrayList<>();
        for (Request r : requests) {
            if ((r.getDateTime().getDayOfYear())>(now.getDayOfYear())){
                tomorrowRequests.add(r);
            }
        }

        return tomorrowRequests;
    }
}

