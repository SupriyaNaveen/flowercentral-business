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
 * Created by admin on 19-05-2017.
 */
public class ProfileFragment extends Fragment {

    private View view;

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
     * @return
     */
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    /**
     * Set up view pager.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
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
