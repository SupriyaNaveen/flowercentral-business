package com.flowercentral.flowercentralbusiness.help;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.databinding.FragmentHelpBinding;
import com.flowercentral.flowercentralbusiness.rest.BaseModel;
import com.flowercentral.flowercentralbusiness.rest.QueryBuilder;
import com.flowercentral.flowercentralbusiness.util.Util;
import com.flowercentral.flowercentralbusiness.volley.ErrorData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.List;
import java.util.Map;

/**
 * Help details shown.
 * List of questions and answer is shown.
 */
public class HelpFragment extends Fragment {

    private static final String TAG = HelpFragment.class.getSimpleName();
    private FragmentHelpBinding mBinder;
    private ExpandableListAdapter mExpandableListAdapter;

    /**
     * Init the fragment.
     *
     * @return fragment instance.
     */
    public static HelpFragment newInstance() {
        return new HelpFragment();
    }

    /**
     * Init the view.
     *
     * @param inflater           inflater
     * @param container          container
     * @param savedInstanceState savedInstanceState
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinder = DataBindingUtil.inflate(inflater, R.layout.fragment_help, container, false);
        return mBinder.getRoot();
    }

    /**
     * Get the help data list from web api.
     *
     * @param view view
     * @param savedInstanceState savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getHelpData();
    }

    /**
     * Get the help items list and present it on UI.
     * On error in fetching the data, show appropriate message.
     */
    private void getHelpData() {

        //No internet connection then return
        if (!Util.checkInternet(getActivity())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.msg_internet_unavailable), Toast.LENGTH_LONG).show();
            return;
        }

        mBinder.progressBar.setVisibility(View.VISIBLE);

        BaseModel<JSONArray> baseModel = new BaseModel<JSONArray>(getActivity()) {
            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, JSONArray response) {
                mBinder.progressBar.setVisibility(View.GONE);
                List<HelpDetails> helpDetailsArrayList = new Gson().fromJson(String.valueOf(response),
                        new TypeToken<List<HelpDetails>>() {
                        }.getType());
                updateHelpListViews(helpDetailsArrayList);
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

    /**
     * If the list is empty show empty view. Else show the recycler view.
     *
     * @param helpItemList helpItemList
     */
    private void updateHelpListViews(List<HelpDetails> helpItemList) {

        if (mExpandableListAdapter == null) {
            mExpandableListAdapter = new ExpandableListAdapter(getActivity(), helpItemList);
            mBinder.helpList.setAdapter(mExpandableListAdapter);
        } else {
            mExpandableListAdapter.replaceAll(helpItemList);
        }
    }
}