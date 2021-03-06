package com.flowercentral.flowercentralbusiness.profile;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.andexert.library.RippleView;
import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.databinding.FragmentShopPicturesBinding;
import com.flowercentral.flowercentralbusiness.profile.model.ShopPictureDetails;
import com.flowercentral.flowercentralbusiness.rest.BaseModel;
import com.flowercentral.flowercentralbusiness.rest.QueryBuilder;
import com.flowercentral.flowercentralbusiness.setting.AppConstant;
import com.flowercentral.flowercentralbusiness.util.Logger;
import com.flowercentral.flowercentralbusiness.util.MultipartUtility;
import com.flowercentral.flowercentralbusiness.util.PermissionUtil;
import com.flowercentral.flowercentralbusiness.util.Util;
import com.flowercentral.flowercentralbusiness.volley.ErrorData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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

import static android.os.Build.VERSION_CODES.HONEYCOMB;

/**
 *
 */
public class ShopPictures extends Fragment implements RippleView.OnRippleCompleteListener {

    private static final String TAG = ShopPictures.class.getSimpleName();

    private Context mContext;

    private ShopPicturesAdapter shopPicturesAdapter;
    private MaterialDialog mMaterialDialog;
    private final int TYPE_PICTURES_UPLOAD = 100;
    private UploadFilesAsync mUploadFileAsync;

    private FragmentShopPicturesBinding mBinder;

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
        mBinder = DataBindingUtil.inflate(inflater, R.layout.fragment_shop_pictures, container, false);
        mContext = getActivity();

        // For recycler view use a grid layout manager
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mBinder.shopPicturesRecyclerView.setLayoutManager(mLayoutManager);

        mBinder.swipeRefreshLayout.setRefreshing(true);
        getShopPictures();

        mBinder.imageViewDelete.setOnRippleCompleteListener(this);

        //On swipe refresh the screen.
        mBinder.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });

        View view = mBinder.getRoot();
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
        mBinder.imageViewDelete.setVisibility(View.GONE);
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
                    mBinder.imageViewDelete.setVisibility(View.VISIBLE);
                } else {
                    mBinder.imageViewDelete.setVisibility(View.GONE);
                }
            }

            @Override
            public void uploadNewImage() {
                requestPermission();
            }

            @Override
            public void previewImage(String imageUrl) {
                previewShopImage(imageUrl);
            }
        });
        mBinder.shopPicturesRecyclerView.setAdapter(shopPicturesAdapter);
    }

    private void previewShopImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            final Dialog settingsDialog = new Dialog(getActivity());
            settingsDialog.setCanceledOnTouchOutside(false);
            settingsDialog.setContentView(getActivity().getLayoutInflater().inflate(R.layout.layout_preview_image
                    , null));
            settingsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            final ImageView previewImageView = (ImageView) settingsDialog.findViewById(R.id.image_view_preview);
            final FloatingActionButton closeImageView = (FloatingActionButton) settingsDialog.findViewById(R.id.image_view_close);

            Picasso.
                    with(getActivity()).
                    load(imageUrl).
                    into(previewImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            closeImageView.setVisibility(View.VISIBLE);
                            previewImageView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            previewImageView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_background));
                            previewImageView.setVisibility(View.VISIBLE);
                            closeImageView.setVisibility(View.VISIBLE);
                        }
                    });

            closeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    settingsDialog.dismiss();
                }
            });
            settingsDialog.show();
        }
    }

    /**
     * Hide the swipe refresh layout.
     */
    private void hideRefreshLayout() {
        mBinder.swipeRefreshLayout.setRefreshing(false);
    }

    interface RefreshViews {
        void refreshDeleteIcon();

        void uploadNewImage();

        void previewImage(String imageUrl);
    }

    @Override
    public void onComplete(RippleView view) {
        if (view.getId() == R.id.image_view_delete)
            removePicture(shopPicturesAdapter.getSelectedPictureIdList().toString());
    }

    private void removePicture(String pictureId) {
        mMaterialDialog = Util.showProgressDialog(mContext, "Shop Pictures", "Removing pictures, please wait", false);

        BaseModel<JSONObject> baseModel = new BaseModel<JSONObject>(mContext) {
            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, JSONObject response) {
                try {
                    if (response.getInt(mContext.getString(R.string.api_res_status)) == 1) {
                        Snackbar.make(mBinder.rootLayout, "Remove picture successfull!", Snackbar.LENGTH_SHORT).show();
                        mBinder.swipeRefreshLayout.setRefreshing(true);
                        refreshItems();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(mBinder.rootLayout, "Unable to remove picture.!", Snackbar.LENGTH_SHORT).show();
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
                            Snackbar.make(mBinder.rootLayout, mContext.getResources().getString(R.string.msg_internet_unavailable), Snackbar.LENGTH_SHORT).show();
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
        Snackbar snackbar = Snackbar.make(mBinder.rootLayout, getResources().getString(R.string
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
        Snackbar snackbar = Snackbar.make(mBinder.rootLayout, getResources()
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
                    multipart.addFilePart("images", file);

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
                mBinder.swipeRefreshLayout.setRefreshing(true);
                refreshItems();
            } else {
                Snackbar.make(mBinder.rootLayout, getResources().getString(R.string.msg_data_upload_failed), Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}
