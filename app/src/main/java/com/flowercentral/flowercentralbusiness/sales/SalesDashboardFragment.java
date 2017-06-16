package com.flowercentral.flowercentralbusiness.sales;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.util.ViewPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SalesDashboardFragment extends Fragment {

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @BindView(R.id.tabs)
    TabLayout mTabLayout;

    @BindView(R.id.view_pager_container)
    LinearLayout linearLayoutContainer;

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
     * @param inflater inflater
     * @param container container
     * @param savedInstanceState savedInstance
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_pager, container, false);
        ButterKnife.bind(this, view);
        setupViewPager();
        return view;
    }

    /**
     * Set up view pager.
     */
    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(SalesGraphFragment.newInstance(SalesGraphFragment.VIEW_TYPE.TODAY), getString(R.string.title_today));
        adapter.addFragment(SalesGraphFragment.newInstance(SalesGraphFragment.VIEW_TYPE.WEEKLY), getString(R.string.title_weekly));
        adapter.addFragment(SalesGraphFragment.newInstance(SalesGraphFragment.VIEW_TYPE.MONTHLY), getString(R.string.title_monthly));

        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(3);

        linearLayoutContainer.setBackgroundResource(R.drawable.ic_background);
        mTabLayout.setTabTextColors(ContextCompat.getColorStateList(getActivity(), R.color.colorWhite));
        mTabLayout.setupWithViewPager(mViewPager);
    }
}
