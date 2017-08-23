package com.flowercentral.flowercentralbusiness.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    /**
     * Initialize the fragment manager to fragment page adapter.
     *
     * @param manager fragment manager
     */
    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    /**
     * Get the fragment of specified position(page).
     *
     * @param position position
     * @return fragment
     */
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    /**
     * Total number of pages(fragments) in viewpager.
     *
     * @return size
     */
    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    /**
     * Add fragment to view pager.
     *
     * @param fragment fragment
     * @param title    title
     */
    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    /**
     * Get the title of specified position(page).
     *
     * @param position position
     * @return title
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}