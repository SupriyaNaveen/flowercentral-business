package com.flowercentral.flowercentralbusiness.login.ui;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.dao.MultipartUtility;
import com.flowercentral.flowercentralbusiness.login.ui.adapter.UploadListAdapter;
import com.flowercentral.flowercentralbusiness.login.ui.model.FileDetails;
import com.flowercentral.flowercentralbusiness.login.ui.model.RegisterVendorDetails;
import com.flowercentral.flowercentralbusiness.profile.model.ProfileDetails;
import com.flowercentral.flowercentralbusiness.rest.BaseModel;
import com.flowercentral.flowercentralbusiness.rest.QueryBuilder;
import com.flowercentral.flowercentralbusiness.setting.AppConstant;
import com.flowercentral.flowercentralbusiness.util.Logger;
import com.flowercentral.flowercentralbusiness.util.MapActivity;
import com.flowercentral.flowercentralbusiness.util.PermissionUtil;
import com.flowercentral.flowercentralbusiness.util.Util;
import com.flowercentral.flowercentralbusiness.volley.ErrorData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.os.Build.VERSION_CODES.HONEYCOMB;

/**
 *
 */
public class RegisterActivity extends AppCompatActivity {

    private static final int TYPE_MAP = 3;
    private String TAG = RegisterActivity.class.getSimpleName();
    private static final int TYPE_DOC_UPLOAD = 1;
    private static final int TYPE_PICTURES_UPLOAD = 2;

    private static int mCurrentUploadType;
    private Context mContext;

    @BindView(R.id.fl_no_internet)
    FrameLayout mFrameLayoutNoInternet;

    @BindView(R.id.outer_wrapper)
    FrameLayout mFrameLayoutRoot;

    @BindView(R.id.register_wrapper)
    LinearLayout mLinearLayoutRegister;

    @BindView(R.id.btn_register)
    Button mButtonRegister;

    private MaterialDialog mProgressDialog;

    @BindView(R.id.textview_vendor_name)
    TextInputEditText mEditTextShopName;

    @BindView(R.id.textview_address)
    TextInputEditText mEditTextAddress;

    @BindView(R.id.textview_state)
    TextInputEditText mEditTextState;

    @BindView(R.id.textview_city)
    TextInputEditText mEditTextCity;

    @BindView(R.id.textview_zip)
    TextInputEditText mEditTextZip;

    @BindView(R.id.textview_phone1)
    TextInputEditText mEditTextPhone1;

    @BindView(R.id.textview_phone2)
    TextInputEditText mEditTextPhone2;

    @BindView(R.id.textview_tin)
    TextInputEditText mEditTextTIN;

    @BindView(R.id.textView_doc_upload)
    TextView mTextViewDocUpload;

    @BindView(R.id.text_view_locate)
    TextView mTextViewLocate;

    @BindView(R.id.list_view_doc)
    RecyclerView mListViewDoc;

    @BindView(R.id.list_view_picture)
    RecyclerView mListViewImage;

    private double mLongitude;
    private double mLatitude;

    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    private UploadListAdapter mDocUploadAdapter;
    private UploadListAdapter mImageUploadAdapter;

    private RegisterVendorDetails mRegisterVendorDetails = new RegisterVendorDetails();
    private UploadFilesAsync mUploadFileAsync;

    /**
     * @param savedInstanceState instance
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        mContext = this;
        initializeActivity(mContext);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * @param _context context
     */
    private void initializeActivity(Context _context) {
        //Check internet connectivity
        if (Util.checkInternet(_context)) {
            mFrameLayoutNoInternet.setVisibility(View.GONE);
            mLinearLayoutRegister.setVisibility(View.VISIBLE);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            mListViewDoc.setLayoutManager(mLayoutManager);
            mLayoutManager = new LinearLayoutManager(this);
            mListViewImage.setLayoutManager(mLayoutManager);

            mDocUploadAdapter = new UploadListAdapter(mRegisterVendorDetails.getUploadDocList());
            mListViewDoc.setAdapter(mDocUploadAdapter);

            mImageUploadAdapter = new UploadListAdapter(mRegisterVendorDetails.getUploadImageList());
            mListViewImage.setAdapter(mImageUploadAdapter);

        } else {
            mFrameLayoutNoInternet.setVisibility(View.VISIBLE);
            mLinearLayoutRegister.setVisibility(View.GONE);
        }

        setSupportActionBar(mToolBar);

        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
            ActionBar mActionBar = getSupportActionBar();
            if (mActionBar != null) {
                mActionBar.setHomeButtonEnabled(true);
                mActionBar.setTitle("");
                mActionBar.setDisplayHomeAsUpEnabled(true);
                mActionBar.setDisplayShowHomeEnabled(true);
            }
        }
    }

    /**
     * @param item item
     * @return true/false
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_register)
    void registerSelected() {

        boolean isValidInput = isValidInput();
        if (isValidInput) {
            uploadData();
        }
    }

    private void registerUser(Context _context, JSONObject _user) {
        //Start Progress dialog
        dismissDialog();

        mProgressDialog = Util.showProgressDialog(_context, null, getString(R.string.msg_registering_user), false);

        BaseModel<JSONObject> baseModel = new BaseModel<JSONObject>(_context) {
            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, JSONObject response) {
                //CLose Progress dialog
                dismissDialog();
                showRegisterSuccessMessage(response);
            }

            @Override
            public void onError(ErrorData error) {
                //Close Progress dialog
                dismissDialog();

                if (error != null) {
                    error.setErrorMessage("Register failed. Cause -> " + error.getErrorMessage());
                    switch (error.getErrorType()) {
                        case NETWORK_NOT_AVAILABLE:
                            Snackbar.make(mFrameLayoutRoot, getResources().getString(R.string.msg_internet_unavailable), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INTERNAL_SERVER_ERROR:
                            Snackbar.make(mFrameLayoutRoot, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case CONNECTION_TIMEOUT:
                            Snackbar.make(mFrameLayoutRoot, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case APPLICATION_ERROR:
                            Snackbar.make(mFrameLayoutRoot, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INVALID_INPUT_SUPPLIED:
                            Snackbar.make(mFrameLayoutRoot, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case AUTHENTICATION_ERROR:
                            Snackbar.make(mFrameLayoutRoot, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case UNAUTHORIZED_ERROR:
                            Snackbar.make(mFrameLayoutRoot, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        default:
                            Snackbar.make(mFrameLayoutRoot, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        };

        String url = QueryBuilder.getRegisterUrl();
        if (_user != null) {
            baseModel.executePostJsonRequest(url, _user, TAG);
        } else {
            Snackbar.make(mFrameLayoutRoot, getResources().getString(R.string.msg_reg_user_missing_input), Snackbar.LENGTH_SHORT).show();
        }
    }

    private void showRegisterSuccessMessage(final JSONObject response) {

        try {
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(response.getString("message"));
            alertDialogBuilder.setPositiveButton("Okay",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                if (response.getString("status").equals("success")) {
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            final AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        } catch (JSONException e) {
            Snackbar.make(mFrameLayoutRoot, getResources().getString(R.string.msg_reg_user_missing_input), Snackbar.LENGTH_SHORT).show();
        }
    }

    private boolean isValidInput() {
        boolean isValid = true;
        if (mEditTextShopName.getText().toString().isEmpty()) {
            mEditTextShopName.setError(getString(R.string.fld_error_vendor_name));
            isValid = false;
        } else {
            mEditTextShopName.setError(null);
        }
        return isValid;
    }

    public void dismissDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing() && !isFinishing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.textView_doc_upload)
    void uploadDocuments() {
        mCurrentUploadType = TYPE_DOC_UPLOAD;
        requestPermission();
    }

    private void browseDocuments() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        startActivityForResult(intent, TYPE_DOC_UPLOAD);
    }

    @OnClick(R.id.text_view_picture_upload)
    void uploadPictures() {
        mCurrentUploadType = TYPE_PICTURES_UPLOAD;
        requestPermission();
    }

    private void browsePictures() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, TYPE_PICTURES_UPLOAD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case TYPE_DOC_UPLOAD:
                if (data != null) {
                    if (mRegisterVendorDetails.getUploadDocList() == null) {
                        mRegisterVendorDetails.setUploadDocList(new ArrayList<FileDetails>());
                    }
                    try {
                        Uri contentUri = data.getData();
                        if (contentUri != null) {
                            ContentResolver contentResolver = this.getContentResolver();
                            String mime = contentResolver.getType(contentUri);
                            String realPath = Util.getPath(mContext, contentUri);

                            FileDetails fileDetails = new FileDetails();

                            fileDetails.setFilePath(realPath);
                            fileDetails.setFileType(mime);
                            fileDetails.setUri(contentUri.toString());
                            fileDetails.setFileName(contentUri.getLastPathSegment());

                            mRegisterVendorDetails.addDocumentToList(fileDetails);
                            mDocUploadAdapter.notifyDataSetChanged();

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
            case TYPE_PICTURES_UPLOAD:
                if (data != null) {
                    if (mRegisterVendorDetails.getUploadImageList() == null) {
                        mRegisterVendorDetails.setUploadImageList(new ArrayList<FileDetails>());
                    }
                    try {
                        Uri contentUri = data.getData();
                        if (contentUri != null) {
                            ContentResolver contentResolver = this.getContentResolver();
                            String mime = contentResolver.getType(contentUri);
                            String realPath = Util.getPath(mContext, contentUri);

                            FileDetails fileDetails = new FileDetails();

                            fileDetails.setFilePath(realPath);
                            fileDetails.setFileType(mime);
                            fileDetails.setUri(contentUri.toString());
                            fileDetails.setFileName(contentUri.getLastPathSegment());

                            mRegisterVendorDetails.addImageToList(fileDetails);
                            mImageUploadAdapter.notifyDataSetChanged();

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
            case TYPE_MAP:
                mLatitude = data.getDoubleExtra(getString(R.string.key_latitude), 0.0);
                mLongitude = data.getDoubleExtra(getString(R.string.key_longitude), 0.0);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Request for the runtime permission (SDK >= Marshmallow devices)
     */
    private void requestPermission() {
        if (PermissionUtil.hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                PermissionUtil.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            if (mCurrentUploadType == TYPE_DOC_UPLOAD) {
                browseDocuments();
            } else {
                browsePictures();
            }
        } else {
            PermissionUtil.requestPermission(this,
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
                    if (mCurrentUploadType == TYPE_DOC_UPLOAD) {
                        browseDocuments();
                    } else {
                        browsePictures();
                    }
                } else {
                    if (PermissionUtil.showRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            || PermissionUtil.showRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
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
        Snackbar snackbar = Snackbar.make(mFrameLayoutRoot, getResources().getString(R.string
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
        Snackbar snackbar = Snackbar.make(mFrameLayoutRoot, getResources()
                .getString(R.string.s_required_permission_settings), Snackbar.LENGTH_INDEFINITE)
                .setAction(getResources().getString(R.string.s_action_redirect_settings), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Navigate to app details settings
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, PermissionUtil.REQUEST_CODE_READ_EXTERNAL_STORAGE);
                    }
                });
        snackbar.show();
    }

    @OnClick(R.id.text_view_locate)
    void locateAddressSelected() {
        if (mEditTextAddress.getText().length() > 0) {
            if (isLocated(mEditTextAddress.getText().toString())) {
                Intent mapIntent = new Intent(this, MapActivity.class);
                mapIntent.putExtra(getString(R.string.key_latitude), mLatitude);
                mapIntent.putExtra(getString(R.string.key_longitude), mLongitude);
                mapIntent.putExtra(getString(R.string.key_address), mEditTextAddress.getText());
                mapIntent.putExtra(getString(R.string.key_is_draggable), true);
                startActivityForResult(mapIntent, TYPE_MAP);
            }
        } else {
            Toast.makeText(this, getString(R.string.fld_error_address_for_map), Toast.LENGTH_LONG).show();
        }
    }

    private boolean isLocated(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                Toast.makeText(this, getString(R.string.map_error_unable_locate_address), Toast.LENGTH_LONG).show();
                return false;
            }

            Address location = address.get(0);
            mLongitude = location.getLongitude();
            mLatitude = location.getLatitude();
            return true;
        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.map_error_unable_locate_address), Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private void uploadData() {

        ProfileDetails profileDetails = new ProfileDetails();
        if (mEditTextShopName.getText().length() > 0) {
            profileDetails.setShopName(mEditTextShopName.getText().toString());
        }
        if (mEditTextAddress.getText().length() > 0) {
            profileDetails.setAddress(mEditTextAddress.getText().toString());
            if (mLatitude == 0 || mLongitude == 0) {
                isLocated(mEditTextAddress.getText().toString());
            }
            profileDetails.setLatitude(mLatitude);
            profileDetails.setLongitude(mLongitude);
        }

        if (mEditTextCity.getText().length() > 0) {
            profileDetails.setCity(mEditTextCity.getText().toString());
        }
        if (mEditTextState.getText().length() > 0) {
            profileDetails.setState(mEditTextState.getText().toString());
        }
        if (mEditTextZip.getText().length() > 0) {
            profileDetails.setPin(mEditTextZip.getText().toString());
        }

        if (mEditTextPhone1.getText().length() > 0) {
            profileDetails.setPhone1(mEditTextPhone1.getText().toString());
        }
        if (mEditTextPhone2.getText().length() > 0) {
            profileDetails.setPhone2(mEditTextPhone2.getText().toString());
        }
        if (mEditTextTIN.getText().length() > 0) {
            profileDetails.setTinNumber(mEditTextTIN.getText().toString());
        }

        mRegisterVendorDetails.setProfileDetails(profileDetails);
        mUploadFileAsync = new UploadFilesAsync();
        if (mUploadFileAsync.getStatus() != AsyncTask.Status.RUNNING) {
            if (HONEYCOMB <= Build.VERSION.SDK_INT) {
                mUploadFileAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mRegisterVendorDetails);
            } else {
                mUploadFileAsync.execute(mRegisterVendorDetails);
            }
        }
    }

    // Asynchronous Task
    private class UploadFilesAsync extends AsyncTask<RegisterVendorDetails, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = Util.showProgressDialog(RegisterActivity.this, "Upload", getResources().getString(R.string.msg_uploading_data), false);
        }

        @Override
        protected Boolean doInBackground(RegisterVendorDetails... params) {
            if (mUploadFileAsync.isCancelled()) {
                return false;
            }
            boolean status = false;
            RegisterVendorDetails registerVendorDetails = params[0];
            String url = QueryBuilder.getRegisterUrl();
            String charset = "UTF-8";
            try {
                if (registerVendorDetails != null) {
                    MultipartUtility multipart = new MultipartUtility(url, charset);

                    ProfileDetails profileDetails = registerVendorDetails.getProfileDetails();
                    multipart.addFormField(getString(R.string.api_key_shop_name), profileDetails.getShopName());
                    multipart.addFormField(getString(R.string.api_key_address), profileDetails.getAddress());
                    multipart.addFormField(getString(R.string.api_key_latitude), String.valueOf(profileDetails.getLatitude()));
                    multipart.addFormField(getString(R.string.api_key_longitude), String.valueOf(profileDetails.getLongitude()));
                    multipart.addFormField(getString(R.string.api_key_city), profileDetails.getCity());
                    multipart.addFormField(getString(R.string.api_key_state), profileDetails.getState());
                    multipart.addFormField(getString(R.string.api_key_pin), profileDetails.getPin());
                    multipart.addFormField(getString(R.string.api_key_phone1), profileDetails.getPhone1());
                    multipart.addFormField(getString(R.string.api_key_phone2), profileDetails.getPhone2());
                    multipart.addFormField(getString(R.string.api_key_tin_num), profileDetails.getTinNumber());

                    ArrayList<FileDetails> dataList = registerVendorDetails.getUploadDocList();
                    if (dataList.size() > 0) {
                        for (int i = 0; i < dataList.size(); i++) {
                            FileDetails fileDetails = dataList.get(i);
                            File file = new File(fileDetails.getFilePath());
                            multipart.addFilePart("verification_docs" + String.valueOf(i), file);
                        }
                    }

                    dataList = registerVendorDetails.getUploadImageList();
                    if (dataList.size() > 0) {
                        for (int i = 0; i < dataList.size(); i++) {
                            FileDetails fileDetails = dataList.get(i);
                            File file = new File(fileDetails.getFilePath());
                            multipart.addFilePart("shop_images" + String.valueOf(i), file);
                        }
                    }
                    String response = multipart.finish(HttpURLConnection.HTTP_CREATED);
                    Logger.log(TAG, "doInBackground : ", response, AppConstant.LOG_LEVEL_INFO);
                    status = true;
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
            dismissDialog();
            if (_status) {
                mRegisterVendorDetails.removeDocList();
                mDocUploadAdapter.notifyDataSetChanged();

                mRegisterVendorDetails.removeImageList();
                mImageUploadAdapter.notifyDataSetChanged();
                Snackbar.make(mFrameLayoutRoot, getResources().getString(R.string.msg_data_upload_succes), Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(mFrameLayoutRoot, getResources().getString(R.string.msg_data_upload_failed), Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}
