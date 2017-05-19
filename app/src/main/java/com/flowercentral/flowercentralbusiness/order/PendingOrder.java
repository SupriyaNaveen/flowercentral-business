package com.flowercentral.flowercentralbusiness.order;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.rest.BaseModel;
import com.flowercentral.flowercentralbusiness.rest.QueryBuilder;
import com.flowercentral.flowercentralbusiness.util.Util;
import com.flowercentral.flowercentralbusiness.volley.ErrorData;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 17-05-2017.
 */

public class PendingOrder extends Fragment {

    private String TAG = PendingOrder.class.getSimpleName();
    private View view;
    private Context mContext;

    @BindView(R.id.pending_order_recyclerview)
    RecyclerView mOrderItemRecyclerView;

    @BindView(R.id.textview_empty)
    TextView mListEmptyMessageView;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.root_layout)
    RelativeLayout rootLayout;

    /**
     * Default Constructor
     */
    public PendingOrder() {
    }

    /**
     * Fragment instance to instantiate pending order.
     *
     * @return
     */
    public static PendingOrder newInstance() {
        PendingOrder fragment = new PendingOrder();
        return fragment;
    }

    /**
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

        mContext = getActivity();

        // For recycler view use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mOrderItemRecyclerView.setLayoutManager(mLayoutManager);

        getPendingOrderItems();

        //On swipe refresh the screen.
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

        //No internet connection then return
        if (!Util.checkInternet(mContext)) {
            Toast.makeText(mContext, getResources().getString(R.string.msg_internet_unavailable), Toast.LENGTH_LONG).show();
            return;
        }

        // Make web api call to get the pending order item list.
        BaseModel<JSONObject> baseModel = new BaseModel<JSONObject>(mContext) {
            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, JSONObject response) {
                // Construct the order item list from web api response.
                List<OrderItem> orderItemList = constructOrderItemList(response);
                updatePendingOrderViews(orderItemList);
            }

            @Override
            public void onError(ErrorData error) {
                hideRefreshLayout();
                if (error != null) {
                    mListEmptyMessageView.setVisibility(View.VISIBLE);
                    error.setErrorMessage("Data fetch failed. Cause of fail : " + error.getErrorMessage());
                    switch (error.getErrorType()) {
                        case NETWORK_NOT_AVAILABLE:
                            Snackbar.make(rootLayout, getResources().getString(R.string.msg_internet_unavailable), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INTERNAL_SERVER_ERROR:
                            Snackbar.make(rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case CONNECTION_TIMEOUT:
                            Snackbar.make(rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case APPLICATION_ERROR:
                            Snackbar.make(rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INVALID_INPUT_SUPPLIED:
                            Snackbar.make(rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case AUTHENTICATION_ERROR:
                            Snackbar.make(rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case UNAUTHORIZED_ERROR:
                            Snackbar.make(rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case SERVER_ERROR:
                            Snackbar.make(rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        };

        String url = QueryBuilder.getPendingOrderListUrl();

        //TODO Construct input parameters
        JSONObject user = new JSONObject();
        if (user != null) {
            baseModel.executePostJsonRequest(url, user, TAG);
        } else {
            Snackbar.make(rootLayout, getResources().getString(R.string.msg_reg_user_missing_input), Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * @param response
     * @return
     */
    private List<OrderItem> constructOrderItemList(JSONObject response) {
        List<OrderItem> orderItemList = new ArrayList<>();
        //TODO construct list from json response
        //TODO remove hardcode data

        // Check for internet connection, on no network  show message.
        OrderItem orderItem = new OrderItem();
        orderItem.setName("Rose bunch");
        orderItem.setAddress("#9C, 59, 1st main, 9th cross, Srinidhi Layout, Konanakunte, Bangalore");
        orderItem.setCategory(OrderItem.CATEGORY.S);
        orderItem.setPaidStatus(OrderItem.PAID_STATUS.PAID);
        orderItem.setPrice(100.0);
        orderItem.setQuantity(10);
        orderItem.setScheduleInMillis(Calendar.getInstance().getTimeInMillis());
        orderItem.setDeliveryStatus(OrderItem.DELIVERY_STATUS.PENDING);
        orderItemList.add(orderItem);
        orderItemList.add(orderItem);
        return orderItemList;
    }

    /**
     * Hide the swipe refresh layout.
     * If the list is empty show empty view. Else show the recycler view.
     *
     * @param orderItemList
     */
    private void updatePendingOrderViews(List<OrderItem> orderItemList) {

        hideRefreshLayout();
        if (null == orderItemList || orderItemList.isEmpty()) {
            mListEmptyMessageView.setVisibility(View.VISIBLE);
        } else {
            mListEmptyMessageView.setVisibility(View.GONE);
        }

        PendingOrderAdapter adapter = new PendingOrderAdapter(orderItemList);
        mOrderItemRecyclerView.setAdapter(adapter);
    }

    /**
     * Hide the swipe refresh layout.
     */
    private void hideRefreshLayout() {
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
