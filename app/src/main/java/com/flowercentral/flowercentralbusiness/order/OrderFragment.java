package com.flowercentral.flowercentralbusiness.order;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.util.ViewPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 17-05-2017.
 */

public class OrderFragment extends Fragment {

    private View view;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    /**
     * Default Constructor
     */
    public OrderFragment() {
    }

    public static OrderFragment newInstance() {
        OrderFragment fragment = new OrderFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sales_dashboard, container, false);
        ButterKnife.bind(this, view);
        setupViewPager();
        return view;
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new PendingOrder(), getString(R.string.title_pending_order));
        adapter.addFragment(new CompletedOrder(), getString(R.string.title_completed_order));

        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(mViewPager);
    }
}
