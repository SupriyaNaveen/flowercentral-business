package com.flowercentral.flowercentralbusiness.order;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.databinding.FragmentCompletedOrderBinding;
import com.flowercentral.flowercentralbusiness.order.adapters.CompletedOrderAdapter;
import com.flowercentral.flowercentralbusiness.order.model.Order;
import com.flowercentral.flowercentralbusiness.rest.BaseModel;
import com.flowercentral.flowercentralbusiness.rest.QueryBuilder;
import com.flowercentral.flowercentralbusiness.util.Util;
import com.flowercentral.flowercentralbusiness.volley.ErrorData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

/**
 *
 */
public class CompletedOrder extends Fragment {

    private String TAG = CompletedOrder.class.getSimpleName();
    private Context mContext;
    private Calendar mStartDateSearch = Calendar.getInstance();
    private Calendar mEndDateSearch = Calendar.getInstance();
    private FragmentCompletedOrderBinding mBinder;

    /**
     * Instantiate completed order fragment.
     *
     * @return instance of fragment
     */
    public static CompletedOrder newInstance() {
        return new CompletedOrder();
    }

    /**
     * @param inflater           inflater
     * @param container          container
     * @param savedInstanceState savedInstanceState
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinder = DataBindingUtil.inflate(inflater, R.layout.fragment_completed_order, container, false);

        mContext = getActivity();

        resetSearchBar();
        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mBinder.completedOrderRecyclerview.setLayoutManager(mLayoutManager);

        mBinder.swipeRefreshLayout.setRefreshing(true);
        getCompletedOrderItems();

        mBinder.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });

        mBinder.imageViewDateRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateRangePickerDialog();
            }
        });

        mBinder.textDateRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateRangePickerDialog();
            }
        });

        mBinder.imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinder.swipeRefreshLayout.setRefreshing(true);
                refreshItems();
            }
        });

        return mBinder.getRoot();
    }

    private void showDateRangePickerDialog() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
                        mStartDateSearch.set(year, monthOfYear, dayOfMonth);
                        mEndDateSearch.set(yearEnd, monthOfYearEnd, dayOfMonthEnd);
                        setSearchBar();
                        searchOrderByDate();
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setThemeDark(true);
        dpd.setMaxDate(now);
        dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
    }

    private void setSearchBar() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        mBinder.imageViewClose.setVisibility(View.VISIBLE);
        mBinder.textDateRange.setText(format.format(mStartDateSearch.getTime()) + " to "
                + format.format(mEndDateSearch.getTime()));
    }

    private void searchOrderByDate() {

        mBinder.swipeRefreshLayout.setRefreshing(true);
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
                Order order = constructOrder(response);
                updateCompletedOrderViews(order);
            }

            @Override
            public void onError(ErrorData error) {
                hideRefreshLayout();
                if (error != null) {

                    updateCompletedOrderViews(null);

                    error.setErrorMessage("Data fetch failed. Cause -> " + error.getErrorMessage());
                    switch (error.getErrorType()) {
                        case NETWORK_NOT_AVAILABLE:
                            Snackbar.make(mBinder.rootLayout, getResources().getString(R.string.msg_internet_unavailable), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INTERNAL_SERVER_ERROR:
                            Snackbar.make(mBinder.rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case CONNECTION_TIMEOUT:
                            Snackbar.make(mBinder.rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case APPLICATION_ERROR:
                            Snackbar.make(mBinder.rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INVALID_INPUT_SUPPLIED:
                            Snackbar.make(mBinder.rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case AUTHENTICATION_ERROR:
                            Snackbar.make(mBinder.rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case UNAUTHORIZED_ERROR:
                            Snackbar.make(mBinder.rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case SERVER_ERROR:
                            Snackbar.make(mBinder.rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        default:
                            Snackbar.make(mBinder.rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        };

        String url = QueryBuilder.getDeliveredOrderByDateUrl();
        JSONObject requestObject = new JSONObject();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            requestObject.put(getString(R.string.api_key_start_date), formatter.format(mStartDateSearch.getTime()));
            requestObject.put(getString(R.string.api_key_end_date), formatter.format(mEndDateSearch.getTime()));
            baseModel.executePostJsonRequest(url, requestObject, TAG);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * On swipe refresh the layout.
     */
    private void refreshItems() {
        resetSearchBar();
        getCompletedOrderItems();
    }

    private void resetSearchBar() {
        mBinder.imageViewClose.setVisibility(View.GONE);
        mBinder.textDateRange.setText("");
    }

    /**
     * Get the completed order list and present it on UI.
     */
    private void getCompletedOrderItems() {

        //No internet connection then return
        if (!Util.checkInternet(mContext)) {
            Toast.makeText(mContext, getResources().getString(R.string.msg_internet_unavailable), Toast.LENGTH_LONG).show();
            return;
        }

        BaseModel<JSONObject> baseModel = new BaseModel<JSONObject>(mContext) {
            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, JSONObject response) {
                Order order = constructOrder(response);
                updateCompletedOrderViews(order);
            }

            @Override
            public void onError(ErrorData error) {
                hideRefreshLayout();
                if (error != null) {

                    updateCompletedOrderViews(null);

                    error.setErrorMessage("Data fetch failed. Cause -> " + error.getErrorMessage());
                    switch (error.getErrorType()) {
                        case NETWORK_NOT_AVAILABLE:
                            Snackbar.make(mBinder.rootLayout, getResources().getString(R.string.msg_internet_unavailable), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INTERNAL_SERVER_ERROR:
                            Snackbar.make(mBinder.rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case CONNECTION_TIMEOUT:
                            Snackbar.make(mBinder.rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case APPLICATION_ERROR:
                            Snackbar.make(mBinder.rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INVALID_INPUT_SUPPLIED:
                            Snackbar.make(mBinder.rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case AUTHENTICATION_ERROR:
                            Snackbar.make(mBinder.rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case UNAUTHORIZED_ERROR:
                            Snackbar.make(mBinder.rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case SERVER_ERROR:
                            Snackbar.make(mBinder.rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        default:
                            Snackbar.make(mBinder.rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        };

        String url = QueryBuilder.getCompletedOrderListUrl();
        baseModel.executeGetJsonRequest(url, TAG);
    }

    /**
     * @param response response
     * @return list of order items
     */
    private Order constructOrder(JSONObject response) {

        try {
            return new Gson().fromJson(String.valueOf(response),
                    new TypeToken<Order>() {
                    }.getType());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Update view for ordered item list.
     *
     * @param order order item list
     */
    private void updateCompletedOrderViews(Order order) {

        hideRefreshLayout();

        CompletedOrderAdapter adapter = new CompletedOrderAdapter(order);
        mBinder.completedOrderRecyclerview.setAdapter(adapter);
    }

    /**
     * Hide the refresh layout.
     */
    private void hideRefreshLayout() {
        mBinder.swipeRefreshLayout.setRefreshing(false);
    }
}
