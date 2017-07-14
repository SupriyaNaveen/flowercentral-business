package com.flowercentral.flowercentralbusiness.profile;

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

/**
 *
 */
public class ProfileFragment extends Fragment {

   FragmentViewPagerBinding mBinder;

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
     * Set up view pager for pending order and completed order list.
     */
    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(UpdateProfile.newInstance(), getString(R.string.title_profile));
        adapter.addFragment(ShopPictures.newInstance(), getString(R.string.title_shop_pictures));
        adapter.addFragment(ChangePassword.newInstance(), getString(R.string.title_change_password));


        mBinder.viewPager.setAdapter(adapter);
        mBinder.viewPager.setOffscreenPageLimit(3);

        mBinder.viewPagerContainer.setBackgroundResource(R.drawable.ic_background);
        mBinder.tabs.setTabTextColors(ContextCompat.getColorStateList(getActivity(), R.color.colorWhite));
        mBinder.tabs.setupWithViewPager(mBinder.viewPager);
    }
}
