package com.flowercentral.flowercentralbusiness.dashboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flowercentral.flowercentralbusiness.R;

import butterknife.ButterKnife;

/**
 * Created by admin on 19-05-2017.
 */
public class ProfileFragment extends Fragment {

    private View view;

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
