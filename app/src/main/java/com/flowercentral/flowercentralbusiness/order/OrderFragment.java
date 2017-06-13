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
 *
 */
public class OrderFragment extends Fragment {

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @BindView(R.id.tabs)
    TabLayout mTabLayout;

    /**
     * Default Constructor
     */
    public OrderFragment() {
    }

    /**
     * Instantiate the order fragment to hold pending order and completed order.
     *
     * @return instance
     */
    public static OrderFragment newInstance() {
        return new OrderFragment();
    }

    /**
     * Set up view pager.
     *
     * @param inflater inflater
     * @param container container
     * @param savedInstanceState savedInstanceState
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
     * Set up view pager for pending order and completed order list.
     */
    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(PendingOrder.newInstance(), getString(R.string.title_pending_order));
        adapter.addFragment(CompletedOrder.newInstance(), getString(R.string.title_completed_order));

        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(2);
        mTabLayout.setupWithViewPager(mViewPager);
    }
}
