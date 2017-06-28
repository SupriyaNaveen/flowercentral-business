package com.flowercentral.flowercentralbusiness.help;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.rest.BaseModel;
import com.flowercentral.flowercentralbusiness.rest.QueryBuilder;
import com.flowercentral.flowercentralbusiness.volley.ErrorData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HelpFragment extends Fragment {

    private static final String TAG = HelpFragment.class.getSimpleName();
    @BindView(R.id.help_list)
    ExpandableListView mHelpList;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.root_layout)
    RelativeLayout mRootLayout;

    public static HelpFragment newInstance() {
        return new HelpFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getHelpData();
    }

    private void getHelpData() {
        mProgressBar.setVisibility(View.VISIBLE);

        BaseModel<JSONArray> baseModel = new BaseModel<JSONArray>(getActivity()) {
            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, JSONArray response) {
                List<HelpDetails> helpDetailsArrayList = new Gson().fromJson(String.valueOf(response),
                        new TypeToken<List<HelpDetails>>() {
                        }.getType());
                mProgressBar.setVisibility(View.GONE);

                ExpandableListAdapter expandableListAdapter = new ExpandableListAdapter(getActivity(), helpDetailsArrayList);
                mHelpList.setAdapter(expandableListAdapter);
            }

            @Override
            public void onError(ErrorData error) {
                mProgressBar.setVisibility(View.GONE);

                if (error != null) {
                    error.setErrorMessage("Help details fetch failed. Cause -> " + error.getErrorMessage());

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
                        default:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        };

        String url = QueryBuilder.getHelpDetailsUrl();
        baseModel.executeGetJsonArrayRequest(url, TAG);
    }
}