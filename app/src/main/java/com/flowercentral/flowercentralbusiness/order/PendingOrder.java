package com.flowercentral.flowercentralbusiness.order;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
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
import com.flowercentral.flowercentralbusiness.order.adapters.PendingOrderAdapter;
import com.flowercentral.flowercentralbusiness.order.model.OrderItem;
import com.flowercentral.flowercentralbusiness.rest.BaseModel;
import com.flowercentral.flowercentralbusiness.rest.QueryBuilder;
import com.flowercentral.flowercentralbusiness.setting.AppConstant;
import com.flowercentral.flowercentralbusiness.util.Util;
import com.flowercentral.flowercentralbusiness.volley.ErrorData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 */
public class PendingOrder extends Fragment {

    private String TAG = PendingOrder.class.getSimpleName();
    private Context mContext;

    @BindView(R.id.pending_order_recyclerview)
    RecyclerView mOrderItemRecyclerView;

    @BindView(R.id.textview_empty)
    TextView mListEmptyMessageView;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.root_layout)
    RelativeLayout mRootLayout;

    public interface RefreshViews {
        void performRefreshView();
    }

    /**
     * Fragment instance to instantiate pending order.
     *
     * @return instance
     */
    public static PendingOrder newInstance() {
        return new PendingOrder();
    }

    /**
     * @param inflater           inflater
     * @param container          container
     * @param savedInstanceState savedInstance
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_order, container, false);
        ButterKnife.bind(this, view);

        mContext = getActivity();

        // For recycler view use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mOrderItemRecyclerView.setLayoutManager(mLayoutManager);

        mSwipeRefreshLayout.setRefreshing(true);
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter(AppConstant.BROADCAST_ACTION_ORDER_ACCEPTED));
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
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
        BaseModel<JSONArray> baseModel = new BaseModel<JSONArray>(mContext) {
            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, JSONArray response) {
                // Construct the order item list from web api response.
                List<OrderItem> orderItemList = constructOrderItemList(response);
                updatePendingOrderViews(orderItemList);
            }

            @Override
            public void onError(ErrorData error) {
                hideRefreshLayout();
                if (error != null) {

                    List<OrderItem> orderItemList = new ArrayList<>();
                    updatePendingOrderViews(orderItemList);

                    error.setErrorMessage("Data fetch failed. Cause -> " + error.getErrorMessage());
                    switch (error.getErrorType()) {
                        case NETWORK_NOT_AVAILABLE:
                            Snackbar.make(mRootLayout, getResources().getString(R.string.msg_internet_unavailable), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INTERNAL_SERVER_ERROR:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case CONNECTION_TIMEOUT:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case APPLICATION_ERROR:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INVALID_INPUT_SUPPLIED:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case AUTHENTICATION_ERROR:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case UNAUTHORIZED_ERROR:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case SERVER_ERROR:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        default:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        };

        String url = QueryBuilder.getPendingOrderListUrl();
        baseModel.executeGetJsonArrayRequest(url, TAG);
    }

    /**
     * @param response response
     * @return order item list
     */
    private List<OrderItem> constructOrderItemList(JSONArray response) {

        return new Gson().fromJson(String.valueOf(response),
                new TypeToken<List<OrderItem>>() {
                }.getType());
    }

    /**
     * Hide the swipe refresh layout.
     * If the list is empty show empty view. Else show the recycler view.
     *
     * @param orderItemList order item list
     */
    private void updatePendingOrderViews(List<OrderItem> orderItemList) {

        hideRefreshLayout();
        if (null == orderItemList || orderItemList.isEmpty()) {
            mListEmptyMessageView.setVisibility(View.VISIBLE);
            orderItemList = new ArrayList<>();
        } else {
            mListEmptyMessageView.setVisibility(View.GONE);
        }

        PendingOrderAdapter adapter = new PendingOrderAdapter(orderItemList, mRootLayout, new RefreshViews() {
            @Override
            public void performRefreshView() {
                mSwipeRefreshLayout.setRefreshing(true);
                refreshItems();
            }
        });
        mOrderItemRecyclerView.setAdapter(adapter);
    }

    /**
     * Hide the swipe refresh layout.
     */
    private void hideRefreshLayout() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mSwipeRefreshLayout.setRefreshing(true);
            refreshItems();
        }
    };
}
