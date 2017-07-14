package com.flowercentral.flowercentralbusiness.help;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.databinding.FragmentHelpBinding;
import com.flowercentral.flowercentralbusiness.rest.BaseModel;
import com.flowercentral.flowercentralbusiness.rest.QueryBuilder;
import com.flowercentral.flowercentralbusiness.volley.ErrorData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.List;
import java.util.Map;

public class HelpFragment extends Fragment {

    private static final String TAG = HelpFragment.class.getSimpleName();
    private FragmentHelpBinding mBinder;

    public static HelpFragment newInstance() {
        return new HelpFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinder = DataBindingUtil.inflate(inflater, R.layout.fragment_help, container, false);
        return mBinder.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getHelpData();
    }

    private void getHelpData() {
        mBinder.progressBar.setVisibility(View.VISIBLE);

        BaseModel<JSONArray> baseModel = new BaseModel<JSONArray>(getActivity()) {
            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, JSONArray response) {
                List<HelpDetails> helpDetailsArrayList = new Gson().fromJson(String.valueOf(response),
                        new TypeToken<List<HelpDetails>>() {
                        }.getType());
                 mBinder.progressBar.setVisibility(View.GONE);

                ExpandableListAdapter expandableListAdapter = new ExpandableListAdapter(getActivity(), helpDetailsArrayList);
                mBinder.helpList.setAdapter(expandableListAdapter);
            }

            @Override
            public void onError(ErrorData error) {
                 mBinder.progressBar.setVisibility(View.GONE);

                if (error != null) {
                    error.setErrorMessage("Help details fetch failed. Cause -> " + error.getErrorMessage());

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
                        default:
                            Snackbar.make(mBinder.rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        };

        String url = QueryBuilder.getHelpDetailsUrl();
        baseModel.executeGetJsonArrayRequest(url, TAG);
    }
}