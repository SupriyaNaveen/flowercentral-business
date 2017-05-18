package com.flowercentral.flowercentralbusiness.order;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flowercentral.flowercentralbusiness.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 17-05-2017.
 */

public class CompletedOrder extends Fragment {

    private View view;

    @BindView(R.id.completed_order_recyclerview)
    RecyclerView mOrderItemRecyclerView;

    @BindView(R.id.textview_empty)
    TextView mListEmptyMessageView;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    /**
     * Default Constructor
     */
    public CompletedOrder() {
    }

    public static CompletedOrder newInstance() {
        CompletedOrder fragment = new CompletedOrder();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_completed_order, container, false);
        ButterKnife.bind(this, view);

        // in content do not change the layout size of the RecyclerView
        mOrderItemRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mOrderItemRecyclerView.setLayoutManager(mLayoutManager);

        getCompletedOrderItems();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });
        return view;
    }

    private void refreshItems() {
        getCompletedOrderItems();
    }
    /**
     * Get the completed order list and present it on UI.
     */
    private void getCompletedOrderItems() {

        // TODO: Call web api to get the order item list.
        // Check for internet connection, on no network  show message.
        List<OrderItem> orderItemList = new ArrayList<>();
        orderItemList.add(new OrderItem());
        orderItemList.add(new OrderItem());

        hideRefreshLayout();
        if(null == orderItemList || orderItemList.isEmpty()) {
            mListEmptyMessageView.setVisibility(View.VISIBLE);
        } else {
            mListEmptyMessageView.setVisibility(View.GONE);
        }

        CompletedOrderAdapter adapter = new CompletedOrderAdapter(orderItemList);
        mOrderItemRecyclerView.setAdapter(adapter);
    }

    private void hideRefreshLayout() {
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
