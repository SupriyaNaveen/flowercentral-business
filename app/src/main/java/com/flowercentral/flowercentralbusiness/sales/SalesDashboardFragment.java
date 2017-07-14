package com.flowercentral.flowercentralbusiness.sales;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.databinding.FragmentViewPagerBinding;
import com.flowercentral.flowercentralbusiness.util.ViewPagerAdapter;

public class SalesDashboardFragment extends Fragment {

    private FragmentViewPagerBinding mBinder;

    /**
     * Instantiate the order fragment to hold pending order and completed order.
     *
     * @return instance of fragment
     */
    public static SalesDashboardFragment newInstance() {
        return new SalesDashboardFragment();
    }

    /**
     * Set up view pager.
     *
     * @param inflater           inflater
     * @param container          container
     * @param savedInstanceState savedInstance
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinder = DataBindingUtil.inflate(inflater, R.layout.fragment_view_pager, container, false);
        setupViewPager();
        return mBinder.getRoot();
    }

    /**
     * Set up view pager.
     */
    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(SalesGraphFragment.newInstance(SalesGraphFragment.VIEW_TYPE.TODAY), getString(R.string.title_today));
        adapter.addFragment(SalesGraphFragment.newInstance(SalesGraphFragment.VIEW_TYPE.WEEKLY), getString(R.string.title_weekly));
        adapter.addFragment(SalesGraphFragment.newInstance(SalesGraphFragment.VIEW_TYPE.MONTHLY), getString(R.string.title_monthly));

        mBinder.viewPager.setAdapter(adapter);
        mBinder.viewPager.setOffscreenPageLimit(3);

        mBinder.viewPagerContainer.setBackgroundResource(R.color.colorBackground);
        mBinder.tabs.setTabTextColors(ContextCompat.getColorStateList(getActivity(), R.color.colorGrey));
        mBinder.tabs.setupWithViewPager(mBinder.viewPager);
    }
}
