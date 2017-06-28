package com.flowercentral.flowercentralbusiness.feedback;

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
import android.widget.Toast;

import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.rest.BaseModel;
import com.flowercentral.flowercentralbusiness.rest.QueryBuilder;
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
public class FeedbackFragment extends Fragment {

    private static final String TAG = FeedbackFragment.class.getSimpleName();
    private Context mContext;

    @BindView(R.id.feedback_recyclerview)
    RecyclerView mFeedbackRecyclerView;

    @BindView(R.id.root_layout)
    RelativeLayout mRootLayout;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    /**
     *
     * @return instance of fragment
     */
    public static FeedbackFragment newInstance() {
        return new FeedbackFragment();
    }

    /**
     *
     * @param inflater inflater
     * @param container container
     * @param savedInstanceState savedInstanceState
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);
        ButterKnife.bind(this, view);

        mContext = getActivity();

        // For recycler view use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mFeedbackRecyclerView.setLayoutManager(mLayoutManager);

        mSwipeRefreshLayout.setRefreshing(true);
        getFeedbackItems();

        //On swipe refresh the screen.
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });
        return view;
    }

    /**
     * Refresh feedback items list.
     */
    private void refreshItems() {
        getFeedbackItems();
    }

    /**
     * Get the feedback items list and present it on UI.
     */
    private void getFeedbackItems() {

        //No internet connection then return
        if (!Util.checkInternet(mContext)) {
            Toast.makeText(mContext, getResources().getString(R.string.msg_internet_unavailable), Toast.LENGTH_LONG).show();
            return;
        }

        // Make web api call to get the feedback item list.
        BaseModel<JSONArray> baseModel = new BaseModel<JSONArray>(mContext) {
            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, JSONArray response) {
                // Construct the feedback item list from web api response.
                List<FeedbackItem> feedbackItemList = constructFeedbackItemList(response);
                updateFeedbackListViews(feedbackItemList);
            }

            @Override
            public void onError(ErrorData error) {
                hideRefreshLayout();
                if (error != null) {

                    List<FeedbackItem> feedbackItemList = new ArrayList<>();
                    updateFeedbackListViews(feedbackItemList);

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

        String url = QueryBuilder.getFeedbackListUrl();
        baseModel.executeGetJsonArrayRequest(url, TAG);
    }

    /**
     * @param response response
     * @return feedback item list
     */
    private List<FeedbackItem> constructFeedbackItemList(JSONArray response) {

        return new Gson().fromJson(String.valueOf(response),
                new TypeToken<List<FeedbackItem>>(){}.getType());
    }

    /**
     * Hide the swipe refresh layout.
     * If the list is empty show empty view. Else show the recycler view.
     *
     * @param feedbackItemList feedbackItemList
     */
    private void updateFeedbackListViews(List<FeedbackItem> feedbackItemList) {

        hideRefreshLayout();

        ViewFeedbackAdapter adapter = new ViewFeedbackAdapter(feedbackItemList);
        mFeedbackRecyclerView.setAdapter(adapter);
    }

    /**
     * Hide the swipe refresh layout.
     */
    private void hideRefreshLayout() {
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
