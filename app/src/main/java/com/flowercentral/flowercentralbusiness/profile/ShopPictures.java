package com.flowercentral.flowercentralbusiness.profile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.dao.MultipartUtility;
import com.flowercentral.flowercentralbusiness.profile.model.ShopPictureDetails;
import com.flowercentral.flowercentralbusiness.rest.BaseModel;
import com.flowercentral.flowercentralbusiness.rest.QueryBuilder;
import com.flowercentral.flowercentralbusiness.setting.AppConstant;
import com.flowercentral.flowercentralbusiness.util.Logger;
import com.flowercentral.flowercentralbusiness.util.PermissionUtil;
import com.flowercentral.flowercentralbusiness.util.Util;
import com.flowercentral.flowercentralbusiness.volley.ErrorData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.os.Build.VERSION_CODES.HONEYCOMB;

/**
 *
 */
public class ShopPictures extends Fragment {

    private static final String TAG = ShopPictures.class.getSimpleName();

    @BindView(R.id.shop_pictures_recycler_view)
    RecyclerView mShopPicturesRecyclerView;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.image_view_delete)
    ImageView imageViewDeletePictures;

    private Context mContext;

    @BindView(R.id.root_layout)
    RelativeLayout mRootLayout;
    private ShopPicturesAdapter shopPicturesAdapter;
    private MaterialDialog mMaterialDialog;
    private final int TYPE_PICTURES_UPLOAD = 100;
    private UploadFilesAsync mUploadFileAsync;

    /**
     * Instantiate ShopPictures fragment.
     *
     * @return instance of fragment
     */
    public static ShopPictures newInstance() {
        return new ShopPictures();
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
        View view = inflater.inflate(R.layout.fragment_shop_pictures, container, false);
        ButterKnife.bind(this, view);

        mContext = getActivity();

        // For recycler view use a grid layout manager
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
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

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (shopPicturesAdapter.isImageSelectable) {
                        shopPicturesAdapter.clearSelectedPathList();
                        return true;
                    } else
                        return false;
                }
                return false;
            }
        });

        return view;
    }

    private void refreshItems() {
        imageViewDeletePictures.setVisibility(View.GONE);
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
        shopPicturesAdapter = new ShopPicturesAdapter(shopPictureDetails, new RefreshViews() {
            @Override
            public void refreshDeleteIcon() {
                if (shopPicturesAdapter.isImageSelectable) {
                    imageViewDeletePictures.setVisibility(View.VISIBLE);
                } else {
                    imageViewDeletePictures.setVisibility(View.GONE);
                }
            }

            @Override
            public void uploadNewImage() {
                requestPermission();
            }
        });
        mShopPicturesRecyclerView.setAdapter(shopPicturesAdapter);
    }

    /**
     * Hide the swipe refresh layout.
     */
    private void hideRefreshLayout() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    interface RefreshViews {
        void refreshDeleteIcon();

        void uploadNewImage();
    }

    @OnClick(R.id.image_view_delete)
    void deletePictures() {
        removePicture(shopPicturesAdapter.getSelectedPictureIdList().toString());
    }

    private void removePicture(String pictureId) {
        mMaterialDialog = Util.showProgressDialog(mContext, "Shop Pictures", "Removing pictures, please wait", false);

        BaseModel<JSONObject> baseModel = new BaseModel<JSONObject>(mContext) {
            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, JSONObject response) {
                try {
                    if (response.getInt(mContext.getString(R.string.api_res_status)) == 1) {
                        Snackbar.make(mRootLayout, "Remove picture successfull!", Snackbar.LENGTH_SHORT).show();
                        mSwipeRefreshLayout.setRefreshing(true);
                        refreshItems();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(mRootLayout, "Unable to remove picture.!", Snackbar.LENGTH_SHORT).show();
                }
                dismissMaterialDialog();
            }

            @Override
            public void onError(ErrorData error) {
                dismissMaterialDialog();
                if (error != null) {
                    error.setErrorMessage("Shop pictures remove failed. Cause -> " + error.getErrorMessage());

                    switch (error.getErrorType()) {
                        case NETWORK_NOT_AVAILABLE:
                            Snackbar.make(mRootLayout, mContext.getResources().getString(R.string.msg_internet_unavailable), Snackbar.LENGTH_SHORT).show();
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

        String url = QueryBuilder.getRemovePictureUrl();
        try {
            JSONObject requestObject = new JSONObject();
            requestObject.put(mContext.getString(R.string.api_key_picture_id), pictureId);
            String srcFormat = "yyyy-MM-dd HH:mm";
            SimpleDateFormat formatSrc = new SimpleDateFormat(srcFormat, Locale.getDefault());
            requestObject.put(mContext.getString(R.string.api_key_timestamp), formatSrc.format(Calendar.getInstance().getTime()));
            baseModel.executePostJsonRequest(url, requestObject, TAG);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void dismissMaterialDialog() {
        if (null != mMaterialDialog && mMaterialDialog.isShowing()) {
            mMaterialDialog.dismiss();
            mMaterialDialog = null;
        }
    }

    private void browsePictures() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, TYPE_PICTURES_UPLOAD);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case TYPE_PICTURES_UPLOAD:
                if (data != null) {
                    try {
                        Uri contentUri = data.getData();
                        if (contentUri != null) {
                            String realPath = Util.getPath(mContext, contentUri);
                            mUploadFileAsync = new UploadFilesAsync();
                            if (mUploadFileAsync.getStatus() != AsyncTask.Status.RUNNING) {
                                if (HONEYCOMB <= Build.VERSION.SDK_INT) {
                                    mUploadFileAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, realPath);
                                } else {
                                    mUploadFileAsync.execute(realPath);
                                }
                            }
                        } else {
                            Toast.makeText(mContext, "Error: Unable to get path", Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Error: unable to get path, content uri is null");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    Toast.makeText(mContext, "Data is null", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void requestPermission() {
        if (PermissionUtil.hasPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) &&
                PermissionUtil.hasPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            browsePictures();
        } else {
            PermissionUtil.requestPermission(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PermissionUtil.REQUEST_CODE_READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PermissionUtil.REQUEST_CODE_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    browsePictures();
                } else {
                    if (PermissionUtil.showRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            || PermissionUtil.showRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        snackBarRequestPermission();
                    } else {
                        snackBarRedirectToSettings();
                    }
                }
                break;
        }
    }

    /**
     * Displays the snack bar to request the permission from user
     */
    private void snackBarRequestPermission() {
        Snackbar snackbar = Snackbar.make(mRootLayout, getResources().getString(R.string
                .s_required_permission_read_storage), Snackbar.LENGTH_INDEFINITE).setAction(getResources().getString(R.string
                .s_action_request_again), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
            }
        });
        snackbar.show();
    }

    /**
     * If the user checked "Never ask again" option and deny the permission then request dialog
     * cannot be invoked. So display SnackBar to redirect to Settings to grant the permissions
     */
    private void snackBarRedirectToSettings() {
        Snackbar snackbar = Snackbar.make(mRootLayout, getResources()
                .getString(R.string.s_required_permission_settings), Snackbar.LENGTH_INDEFINITE)
                .setAction(getResources().getString(R.string.s_action_redirect_settings), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Navigate to app details settings
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, PermissionUtil.REQUEST_CODE_READ_EXTERNAL_STORAGE);
                    }
                });
        snackbar.show();
    }

    private class UploadFilesAsync extends AsyncTask<String, Void, Boolean> {

        JSONObject responseObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mMaterialDialog = Util.showProgressDialog(getActivity(), "Upload", getResources().getString(R.string.msg_uploading_data), false);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            if (mUploadFileAsync.isCancelled()) {
                return false;
            }
            boolean status = false;
            String filePath = params[0];
            String url = QueryBuilder.getUploadShopPicturesUrl();
            String charset = "UTF-8";
            try {
                if (filePath != null) {
                    MultipartUtility multipart = new MultipartUtility(url, charset, getActivity());
                    File file = new File(filePath);
                    multipart.addFilePart("image", file);

                    String srcFormat = "yyyy-MM-dd HH:mm";
                    SimpleDateFormat formatSrc = new SimpleDateFormat(srcFormat, Locale.getDefault());
                    multipart.addFormField(getString(R.string.api_key_timestamp), formatSrc.format(Calendar.getInstance().getTime()));

                    String response = multipart.finish(HttpURLConnection.HTTP_CREATED);
                    responseObject = new JSONObject(response);
                    if (responseObject.getInt(getString(R.string.api_res_status)) == 1) {
                        Logger.log(TAG, "doInBackground : ", response, AppConstant.LOG_LEVEL_INFO);
                        status = true;
                    } else {
                        status = false;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                status = false;
            }
            return status;
        }

        @Override
        protected void onPostExecute(Boolean _status) {
            super.onPostExecute(_status);
            dismissMaterialDialog();
            if (_status) {
                mSwipeRefreshLayout.setRefreshing(true);
                refreshItems();
            } else {
                Snackbar.make(mRootLayout, getResources().getString(R.string.msg_data_upload_failed), Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}
