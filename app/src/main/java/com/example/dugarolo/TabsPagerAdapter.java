package com.example.dugarolo;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {
    @StringRes
    private static final int[] TAB_TITLES =
            new int[] { R.string.tab_text_1, R.string.tab_text_2};
    private final Context mContext;
    public TabsPagerAdapter(Context context, FragmentManager fm) {
        super(fm,FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mContext = context;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return TodayRequest.newInstance();
            case 1:
                return TomorrowRequest.newInstance();
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
}