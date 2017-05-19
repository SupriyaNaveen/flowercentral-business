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

public class SalesDashboardFragment extends Fragment {

    private View view;

    public static SalesDashboardFragment newInstance() {
        SalesDashboardFragment fragment = new SalesDashboardFragment();
        fragment = new SalesDashboardFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sales_dashboard, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
