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

public class PendingOrder extends Fragment {

    private View view;

    @BindView(R.id.pending_order_recyclerview)
    RecyclerView mOrderItemRecyclerView;

    @BindView(R.id.textview_empty)
    TextView mListEmptyMessageView;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    /**
     * Default Constructor
     */
    public PendingOrder() {
    }

    /**
     * Fragment instance to instantiate pending order.
     * @return
     */
    public static PendingOrder newInstance() {
        PendingOrder fragment = new PendingOrder();
        return fragment;
    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pending_order, container, false);
        ButterKnife.bind(this, view);

        // in content do not change the layout size of the RecyclerView
        mOrderItemRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mOrderItemRecyclerView.setLayoutManager(mLayoutManager);

        getPendingOrderItems();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });
        return view;
    }

    private void refreshItems() {
        getPendingOrderItems();
    }

    /**
     * Get the pending order list and present it on UI.
     */
    private void getPendingOrderItems() {

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

        PendingOrderAdapter adapter = new PendingOrderAdapter(orderItemList);
        mOrderItemRecyclerView.setAdapter(adapter);
    }

    private void hideRefreshLayout() {
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
