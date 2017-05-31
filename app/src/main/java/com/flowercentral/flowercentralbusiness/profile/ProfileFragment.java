package com.flowercentral.flowercentralbusiness.profile;

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
public class ProfileFragment extends Fragment {

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @BindView(R.id.tabs)
    TabLayout mTabLayout;

    /**
     * Default Constructor
     */
    public ProfileFragment() {
    }

    /**
     * Instantiate the order fragment to hold pending order and completed order.
     *
     * @return instance of fragment
     */
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        setupViewPager();
        return view;
    }

    /**
     * Set up view pager for pending order and completed order list.
     */
    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new UpdateProfile(), getString(R.string.title_profile));
        adapter.addFragment(new ShopPictures(), getString(R.string.title_shop_pictures));
        adapter.addFragment(new ChangePassword(), getString(R.string.title_change_password));


        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(3);
        mTabLayout.setupWithViewPager(mViewPager);
    }
}
