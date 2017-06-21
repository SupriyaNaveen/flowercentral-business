package com.flowercentral.flowercentralbusiness.sales;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.rest.BaseModel;
import com.flowercentral.flowercentralbusiness.rest.QueryBuilder;
import com.flowercentral.flowercentralbusiness.util.Util;
import com.flowercentral.flowercentralbusiness.volley.ErrorData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class SalesGraphFragment extends Fragment {

    private static final String TAG = SalesGraphFragment.class.getSimpleName();

    @BindView(R.id.root_layout)
    RelativeLayout mRootLayout;

    @BindView(R.id.total_orders)
    TextView mTextViewTotalOrders;

    @BindView(R.id.total_sales)
    TextView mTextViewTotalSales;

    @BindView(R.id.title_sales_graph)
    TextView mTextViewTitle;

    @BindView(R.id.graph_view)
    GraphView mGraphView;

    @BindView(R.id.todays_sales_wrapper)
    LinearLayout mTtodaysSalesWrapper;

    private VIEW_TYPE mViewType;

    enum VIEW_TYPE {
        TODAY,
        WEEKLY,
        MONTHLY
    }

    public static SalesGraphFragment newInstance(VIEW_TYPE typeOfGraph) {
        SalesGraphFragment salesGraph = new SalesGraphFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("VIEW_TYPE", typeOfGraph);
        salesGraph.setArguments(bundle);
        return salesGraph;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mViewType = (VIEW_TYPE) getArguments().get("VIEW_TYPE");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sales_graph, container, false);
        ButterKnife.bind(this, view);

        getSalesGraphDetails();
        return view;
    }

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

    private void getSalesDetails(String url) {

        //No internet connection then return
        if (!Util.checkInternet(getActivity())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.msg_internet_unavailable), Toast.LENGTH_LONG).show();
            return;
        }

        // Make web api call to get the pending order item list.
        BaseModel<JSONObject> baseModel = new BaseModel<JSONObject>(getActivity()) {
            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, JSONObject response) {
                // Construct the order item list from web api response.
                SalesDetails salesDetails = constructSalesDetails(response);
                updateSalesGraphView(salesDetails);
            }

            @Override
            public void onError(ErrorData error) {
                if (error != null) {

                    updateSalesGraphView(null);

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

        baseModel.executeGetJsonRequest(url, TAG);
    }

    private void updateSalesGraphView(SalesDetails salesDetails) {

        DataPoint[] dataPoints;
        ArrayList<GraphData> graphDataArrayList;
        if (null != salesDetails) {

            switch (mViewType) {

                case TODAY:
                    mTtodaysSalesWrapper.setVisibility(View.VISIBLE);
                    mGraphView.setVisibility(View.GONE);
                    mTextViewTotalOrders.setText(String.valueOf(salesDetails.getTotalOrders()));
                    mTextViewTotalSales.setText(String.format("$%s", salesDetails.getTotalSales()));
                    break;
                case WEEKLY:
                    mTextViewTitle.setText(mTextViewTitle.getText()
                            + "\n\n" + salesDetails.getStartDate()
                            + " to " + salesDetails.getEndDate());
                    mGraphView.setVisibility(View.VISIBLE);
                    mTtodaysSalesWrapper.setVisibility(View.GONE);
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
                    mGraphView.getGridLabelRenderer().setLabelFormatter(
                            new DateAsXAxisLabelFormatter(getActivity(),
                                    new SimpleDateFormat("EEE", Locale.getDefault())));
                    drawGraph(dataPoints, graphDataArrayList.size());
                    break;
                case MONTHLY:
                    mTextViewTitle.setText(mTextViewTitle.getText()
                            + "\n\n" + salesDetails.getMonth());
                    mGraphView.setVisibility(View.VISIBLE);
                    mTtodaysSalesWrapper.setVisibility(View.GONE);
                    graphDataArrayList = salesDetails.getGraphDataArrayList();
                    dataPoints = new DataPoint[graphDataArrayList.size()];
                    for (int i = 0; i < graphDataArrayList.size(); i++) {
                        GraphData graphData = graphDataArrayList.get(i);
                        dataPoints[i] = new DataPoint(i + 1, graphData.getTotalSales());
                    }
                    mGraphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                        @Override
                        public String formatLabel(double value, boolean isValueX) {
                            if (isValueX) {
                                return super.formatLabel(value, isValueX) + " Week";
                            } else {
                                return super.formatLabel(value, isValueX);
                            }
                        }
                    });
                    drawGraph(dataPoints, graphDataArrayList.size());
                    break;
            }
        }
    }

    private void drawGraph(DataPoint[] dataPoints, int noOfRows) {
        LineGraphSeries<DataPoint> monthlySeries = new LineGraphSeries<>(dataPoints);
        mGraphView.getGridLabelRenderer().setNumHorizontalLabels(noOfRows);

        monthlySeries.setColor(Color.argb(255, 236, 64, 132));
        monthlySeries.setDrawDataPoints(true);

        mGraphView.addSeries(monthlySeries);
        mGraphView.getGridLabelRenderer().setGridColor(R.color.colorGrey);
        mGraphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        mGraphView.getGridLabelRenderer().setHorizontalLabelsColor(R.color.colorGrey);
        mGraphView.getGridLabelRenderer().setVerticalLabelsColor(R.color.colorGrey);

        mGraphView.getGridLabelRenderer().setHighlightZeroLines(false);
        mGraphView.getGridLabelRenderer().reloadStyles();
    }

    private SalesDetails constructSalesDetails(JSONObject response) {
        return new Gson().fromJson(String.valueOf(response),
                new TypeToken<SalesDetails>() {
                }.getType());
    }
}
