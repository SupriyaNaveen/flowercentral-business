package com.flowercentral.flowercentralbusiness.sales;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.databinding.FragmentSalesGraphBinding;
import com.flowercentral.flowercentralbusiness.rest.BaseModel;
import com.flowercentral.flowercentralbusiness.rest.QueryBuilder;
import com.flowercentral.flowercentralbusiness.util.Util;
import com.flowercentral.flowercentralbusiness.volley.ErrorData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * Sales details, sales data graph shown here.
 */
public class SalesGraphFragment extends Fragment {

    private static final String TAG = SalesGraphFragment.class.getSimpleName();
    private FragmentSalesGraphBinding mBinder;
    private MaterialDialog mProgressDialog;

    private VIEW_TYPE mViewType;

    enum VIEW_TYPE {
        TODAY,
        WEEKLY,
        MONTHLY
    }

    /**
     * Instantiate the sales graph fragment.
     *
     * @param typeOfGraph today, weekly, monthly
     * @return fragment instance
     */
    public static SalesGraphFragment newInstance(VIEW_TYPE typeOfGraph) {
        SalesGraphFragment salesGraph = new SalesGraphFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("VIEW_TYPE", typeOfGraph);
        salesGraph.setArguments(bundle);
        return salesGraph;
    }

    /**
     * Init viewtype from arguments.
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mViewType = (VIEW_TYPE) getArguments().get("VIEW_TYPE");
        }
    }

    /**
     * Set up view.
     * Get the graph details to show in UI.
     *
     * @param inflater           inflater
     * @param container          container
     * @param savedInstanceState savedInstanceState
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinder = DataBindingUtil.inflate(inflater, R.layout.fragment_sales_graph, container, false);

        getSalesGraphDetails();
        return mBinder.getRoot();
    }

    /**
     * Based on view type setup the url and make web api call.
     */
    private void getSalesGraphDetails() {

        String url;
        switch (mViewType) {
            case TODAY:
                url = QueryBuilder.getTodaysSalesDataUrl();
                getSalesDetails(url);
                break;
            case WEEKLY:
                url = QueryBuilder.getWeeklySalesDataUrl();
                getSalesDetails(url);
                break;
            case MONTHLY:
                url = QueryBuilder.getMonthlySalesDataUrl();
                getSalesDetails(url);
                break;
        }
    }

    /**
     * Make sales details url call.
     * On success, construct model class of json response. Then update the UI.
     * On failure show appropriate messages.
     *
     * @param url url
     */
    private void getSalesDetails(String url) {

        //No internet connection then return
        if (!Util.checkInternet(getActivity())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.msg_internet_unavailable), Toast.LENGTH_LONG).show();
            return;
        }

        mProgressDialog = Util.showProgressDialog(getActivity(), "Sales", "Fetching data...", false);

        // Make web api call to get the pending order item list.
        BaseModel<JSONObject> baseModel = new BaseModel<JSONObject>(getActivity()) {
            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, JSONObject response) {

                dismissDialog();
                // Construct the order item list from web api response.
                SalesDetails salesDetails = constructSalesDetails(response);
                updateSalesGraphView(salesDetails);
            }

            @Override
            public void onError(ErrorData error) {
                dismissDialog();
                if (error != null) {

                    updateSalesGraphView(null);

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

        baseModel.executeGetJsonRequest(url, TAG);
    }

    /**
     * Update today, weekly, monthly sales view based on view type.
     * Today : Update total order, total sales.
     * Weekly: Set the title, construct and populate graph point.
     * Monthly: Set the title, construct and populate graph point.
     *
     * @param salesDetails sales details
     */
    private void updateSalesGraphView(SalesDetails salesDetails) {

        DataPoint[] dataPoints;
        ArrayList<GraphData> graphDataArrayList;
        if (null != salesDetails) {

            switch (mViewType) {

                case TODAY:
                    mBinder.ltTodayReport.todaysSalesWrapper.setVisibility(View.VISIBLE);
                    mBinder.graphView.setVisibility(View.GONE);
                    mBinder.ltTodayReport.totalOrders.setText(String.valueOf(salesDetails.getTotalOrders()));
                    mBinder.ltTodayReport.totalSales.setText(String.format("$%s", salesDetails.getTotalSales()));
                    break;
                case WEEKLY:
                    mBinder.titleSalesGraph.setText(mBinder.titleSalesGraph.getText()
                            + "\n\n" + salesDetails.getStartDate()
                            + " to " + salesDetails.getEndDate());
                    mBinder.graphView.setVisibility(View.VISIBLE);
                    mBinder.ltTodayReport.todaysSalesWrapper.setVisibility(View.GONE);
                    graphDataArrayList = salesDetails.getGraphDataArrayList();
                    dataPoints = new DataPoint[graphDataArrayList.size()];
                    for (int i = 0; i < graphDataArrayList.size(); i++) {
                        GraphData graphData = graphDataArrayList.get(i);
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        try {
                            Date date = format.parse(graphData.getWeekDate());
                            dataPoints[i] = new DataPoint(date, graphData.getTotalSales());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    mBinder.graphView.getGridLabelRenderer().setLabelFormatter(
                            new DateAsXAxisLabelFormatter(getActivity(),
                                    new SimpleDateFormat("EEE", Locale.getDefault())));
                    drawGraph(dataPoints, graphDataArrayList.size());
                    break;
                case MONTHLY:
                    mBinder.titleSalesGraph.setText(mBinder.titleSalesGraph.getText()
                            + "\n\n" + salesDetails.getMonth());
                    mBinder.graphView.setVisibility(View.VISIBLE);
                    mBinder.ltTodayReport.todaysSalesWrapper.setVisibility(View.GONE);
                    graphDataArrayList = salesDetails.getGraphDataArrayList();
                    dataPoints = new DataPoint[graphDataArrayList.size()];
                    for (int i = 0; i < graphDataArrayList.size(); i++) {
                        GraphData graphData = graphDataArrayList.get(i);
                        dataPoints[i] = new DataPoint(i + 1, graphData.getTotalSales());
                    }
                    mBinder.graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                        @Override
                        public String formatLabel(double value, boolean isValueX) {
                            if (isValueX) {
                                return super.formatLabel(value, true) + " Week";
                            } else {
                                return super.formatLabel(value, false);
                            }
                        }
                    });
                    drawGraph(dataPoints, graphDataArrayList.size());
                    break;
            }
        }
    }

    /**
     * Set up the style for graph view and plot data point on graph view.
     *
     * @param dataPoints data points
     * @param noOfRows   number of rows
     */
    private void drawGraph(DataPoint[] dataPoints, int noOfRows) {
        LineGraphSeries<DataPoint> monthlySeries = new LineGraphSeries<>(dataPoints);
        mBinder.graphView.getGridLabelRenderer().setNumHorizontalLabels(noOfRows);

        monthlySeries.setColor(Color.argb(255, 236, 64, 132));
        monthlySeries.setDrawDataPoints(true);

        mBinder.graphView.addSeries(monthlySeries);
        mBinder.graphView.getGridLabelRenderer().setGridColor(R.color.colorGrey);
        mBinder.graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        mBinder.graphView.getGridLabelRenderer().setHorizontalLabelsColor(R.color.colorGrey);
        mBinder.graphView.getGridLabelRenderer().setVerticalLabelsColor(R.color.colorGrey);

        mBinder.graphView.getGridLabelRenderer().setHighlightZeroLines(false);
        mBinder.graphView.getGridLabelRenderer().reloadStyles();
    }

    /**
     * Construct SalesDetails model from json response.
     *
     * @param response response
     * @return SalesDetails model
     */
    private SalesDetails constructSalesDetails(JSONObject response) {
        return new Gson().fromJson(String.valueOf(response),
                new TypeToken<SalesDetails>() {
                }.getType());
    }

    public void dismissDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
