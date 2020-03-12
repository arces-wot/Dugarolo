package com.example.dugarolo;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class TabsPagerAdapter extends FragmentPagerAdapter {
    @StringRes
    private static final int[] TAB_TITLES =
            new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private final Context mContext;
    private ArrayList<Request> requests;

    public TabsPagerAdapter(Context context, FragmentManager fm, ArrayList<Request> requests) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mContext = context;
        this.requests = requests;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return TodayTab.newInstance(todayRequests(requests));
            case 1:
                return TomorrowTab.newInstance();
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
        for (Request r : requests)
            if ((r.getDateTime().dayOfYear().equals(now.dayOfYear()))&&(r.getStatus().equalsIgnoreCase("accepted")))
                todayRequests.add(r);
        /*for (Request r1 : requests)
            for (Request r2 : requests)
                if(r1.getName().equals(r2.getName()))
                    requests.remove(r2);*/


        return todayRequests;

    }
}