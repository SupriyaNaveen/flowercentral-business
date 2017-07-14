package com.flowercentral.flowercentralbusiness.order;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.databinding.FragmentViewPagerBinding;
import com.flowercentral.flowercentralbusiness.util.ViewPagerAdapter;

/**
 *
 */
public class OrderFragment extends Fragment {

    private FragmentViewPagerBinding mBinder;

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
     * @param inflater           inflater
     * @param container          container
     * @param savedInstanceState savedInstanceState
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
     * Set up view pager for pending order and completed order list.
     */
    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(PendingOrder.newInstance(), getString(R.string.title_pending_order));
        adapter.addFragment(CompletedOrder.newInstance(), getString(R.string.title_completed_order));

        mBinder.viewPager.setAdapter(adapter);
        mBinder.viewPager.setOffscreenPageLimit(2);

        mBinder.viewPagerContainer.setBackgroundResource(R.color.colorBackground);
        mBinder.tabs.setTabTextColors(ContextCompat.getColorStateList(getActivity(), R.color.colorGrey));

        mBinder.tabs.setupWithViewPager(mBinder.viewPager);

        mBinder.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mBinder.tabs));
        mBinder.tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mBinder.tabs.setScrollPosition(tab.getPosition(), 0f, true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }
}
