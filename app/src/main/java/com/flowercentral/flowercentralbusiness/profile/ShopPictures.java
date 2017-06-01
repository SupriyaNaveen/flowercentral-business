package com.flowercentral.flowercentralbusiness.profile;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.profile.model.ShopPictureDetails;
import com.flowercentral.flowercentralbusiness.rest.BaseModel;
import com.flowercentral.flowercentralbusiness.rest.QueryBuilder;
import com.flowercentral.flowercentralbusiness.util.Util;
import com.flowercentral.flowercentralbusiness.volley.ErrorData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 */
public class ShopPictures extends Fragment {

    private static final String TAG = ShopPictures.class.getSimpleName();

    @BindView(R.id.shop_pictures_recycler_view)
    RecyclerView mShopPicturesRecyclerView;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private Context mContext;

    @BindView(R.id.root_layout)
    RelativeLayout mRootLayout;

    /**
     * @param inflater           inflater
     * @param container          container
     * @param savedInstanceState savedInstance
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_pictures, container, false);
        ButterKnife.bind(this, view);

        mContext = getActivity();

        // For recycler view use a grid layout manager
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
        mShopPicturesRecyclerView.setLayoutManager(mLayoutManager);

        mSwipeRefreshLayout.setRefreshing(true);
        getShopPictures();

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
        getShopPictures();
    }

    private void getShopPictures() {
        //No internet connection then return
        if (!Util.checkInternet(mContext)) {
            Toast.makeText(mContext, getResources().getString(R.string.msg_internet_unavailable), Toast.LENGTH_LONG).show();
            return;
        }

        // Make web api call to get the shop picture list.
        BaseModel<JSONArray> baseModel = new BaseModel<JSONArray>(mContext) {
            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, JSONArray response) {
                ArrayList<ShopPictureDetails> shopPictureDetails = constructShopPicturesList(response);
                updateShopPicturesViews(shopPictureDetails);
            }

            @Override
            public void onError(ErrorData error) {
                hideRefreshLayout();
                if (error != null) {

                    updateShopPicturesViews(null);

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

        String url = QueryBuilder.getShopPictureUrl();
        baseModel.executeGetJsonArrayRequest(url, TAG);
    }

    /**
     * @param response response
     * @return shop pictures list
     */
    private ArrayList<ShopPictureDetails> constructShopPicturesList(JSONArray response) {

        return new Gson().fromJson(String.valueOf(response),
                new TypeToken<ArrayList<ShopPictureDetails>>() {
                }.getType());
    }

    /**
     * Hide the swipe refresh layout.
     * If the list is empty show empty view. Else show the recycler view.
     *
     * @param shopPictureDetails list
     */
    private void updateShopPicturesViews(ArrayList<ShopPictureDetails> shopPictureDetails) {

        hideRefreshLayout();
        if (null == shopPictureDetails) {
            shopPictureDetails = new ArrayList<>();
        }
        ShopPicturesAdapter adapter = new ShopPicturesAdapter(shopPictureDetails, mRootLayout, new RefreshViews() {
            @Override
            public void performRefreshView() {
                mSwipeRefreshLayout.setRefreshing(true);
                refreshItems();
            }
        });
        mShopPicturesRecyclerView.setAdapter(adapter);
    }

    /**
     * Hide the swipe refresh layout.
     */
    private void hideRefreshLayout() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    interface RefreshViews {
        void performRefreshView();
    }

}
