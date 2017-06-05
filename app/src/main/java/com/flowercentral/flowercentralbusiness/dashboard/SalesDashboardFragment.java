package com.flowercentral.flowercentralbusiness.dashboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flowercentral.flowercentralbusiness.R;

import butterknife.ButterKnife;

public class SalesDashboardFragment extends Fragment {

    public static SalesDashboardFragment newInstance() {
        return new SalesDashboardFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
