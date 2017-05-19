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

public class HelpFragment extends Fragment {

    private View view;

    public static HelpFragment newInstance() {
        HelpFragment fragment = new HelpFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_help, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}