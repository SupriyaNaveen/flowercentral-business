package com.flowercentral.flowercentralbusiness.login.ui;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.andexert.library.RippleView;
import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.dao.MultipartUtility;
import com.flowercentral.flowercentralbusiness.databinding.ActivityRegisterBinding;
import com.flowercentral.flowercentralbusiness.databinding.LayoutRegisterBinding;
import com.flowercentral.flowercentralbusiness.databinding.LayoutUploadShopDetailsBinding;
import com.flowercentral.flowercentralbusiness.login.ui.adapter.UploadListAdapter;
import com.flowercentral.flowercentralbusiness.login.ui.model.FileDetails;
import com.flowercentral.flowercentralbusiness.login.ui.model.RegisterVendorDetails;
import com.flowercentral.flowercentralbusiness.map.MapActivity;
import com.flowercentral.flowercentralbusiness.profile.model.ProfileDetails;
import com.flowercentral.flowercentralbusiness.rest.QueryBuilder;
import com.flowercentral.flowercentralbusiness.setting.AppConstant;
import com.flowercentral.flowercentralbusiness.util.Logger;
import com.flowercentral.flowercentralbusiness.util.PermissionUtil;
import com.flowercentral.flowercentralbusiness.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import static android.os.Build.VERSION_CODES.HONEYCOMB;

/**
 *
 */
public class RegisterActivity extends AppCompatActivity implements RippleView.OnRippleCompleteListener {

    private static final int TYPE_MAP = 3;
    private String TAG = RegisterActivity.class.getSimpleName();
    private static final int TYPE_DOC_UPLOAD = 1;
    private static final int TYPE_PICTURES_UPLOAD = 2;

    private static int mCurrentUploadType;
    private Context mContext;
    private MaterialDialog mProgressDialog;

    private double mLongitude;
    private double mLatitude;

    private UploadListAdapter mDocUploadAdapter;
    private UploadListAdapter mImageUploadAdapter;
    private RegisterVendorDetails mRegisterVendorDetails = new RegisterVendorDetails();
    private UploadFilesAsync mUploadFileAsync;

    private ActivityRegisterBinding mBinder;
    private LayoutUploadShopDetailsBinding mUploadBinder;
    private LayoutRegisterBinding mRegisterBinder;

    /**
     * @param savedInstanceState instance
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinder = DataBindingUtil.setContentView(this, R.layout.activity_register);
        mUploadBinder = mBinder.ltUploadShopDetails;
        mRegisterBinder = mBinder.ltRegister;
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
            mBinder.flNoInternet.setVisibility(View.GONE);
            mBinder.registerWrapper.setVisibility(View.VISIBLE);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            mUploadBinder.listViewDoc.setLayoutManager(mLayoutManager);
            mLayoutManager = new LinearLayoutManager(this);
            mUploadBinder.listViewPicture.setLayoutManager(mLayoutManager);

            mDocUploadAdapter = new UploadListAdapter(mRegisterVendorDetails.getUploadDocList());
            mUploadBinder.listViewDoc.setAdapter(mDocUploadAdapter);

            mImageUploadAdapter = new UploadListAdapter(mRegisterVendorDetails.getUploadImageList());
            mUploadBinder.listViewPicture.setAdapter(mImageUploadAdapter);

            mUploadBinder.textViewDocUpload.setOnRippleCompleteListener(this);
            mUploadBinder.textViewPictureUpload.setOnRippleCompleteListener(this);
            mBinder.btnRegister.setOnRippleCompleteListener(this);
            mRegisterBinder.textViewLocate.setOnRippleCompleteListener(this);
            mBinder.ltNoInternet.btnTryAgain.setOnRippleCompleteListener(this);

        } else {
            mBinder.flNoInternet.setVisibility(View.VISIBLE);
            mBinder.registerWrapper.setVisibility(View.GONE);
        }

        setSupportActionBar(mBinder.toolbar);

        if (mBinder.toolbar != null) {
            setSupportActionBar(mBinder.toolbar);
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

    private void showRegisterSuccessMessage(final JSONObject response) {

        try {
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(response.getString("message"));
            alertDialogBuilder.setPositiveButton("Okay",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            final AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        } catch (JSONException e) {
            Snackbar.make(mBinder.outerWrapper, getResources().getString(R.string.msg_reg_user_missing_input), Snackbar.LENGTH_SHORT).show();
        }
    }

    private boolean isValidInput() {
        boolean isValid = true;
        if (mRegisterBinder.textviewVendorName.getText().toString().isEmpty()) {
            mRegisterBinder.textviewVendorName.setError(getString(R.string.fld_error_vendor_name));
            isValid = false;
        } else {
            mRegisterBinder.textviewVendorName.setError(null);
        }

        if (mRegisterBinder.textviewEmail.getText().toString().isEmpty()) {
            mRegisterBinder.textviewEmail.setError(getString(R.string.fld_error_email));
            isValid = false;
        } else {
            mRegisterBinder.textviewEmail.setError(null);
        }

        if (mRegisterBinder.textviewPassword.getText().toString().isEmpty()) {
            mRegisterBinder.textviewPassword.setError(getString(R.string.fld_error_password));
            isValid = false;
        } else {
            mRegisterBinder.textviewPassword.setError(null);
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

    private void browseDocuments() {
        final String[] ACCEPT_MIME_TYPES = {
                "file/*",
                "image/*"
        };
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, ACCEPT_MIME_TYPES);
        startActivityForResult(intent, TYPE_DOC_UPLOAD);
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
        Snackbar snackbar = Snackbar.make(mBinder.outerWrapper, getResources().getString(R.string
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
        Snackbar snackbar = Snackbar.make(mBinder.outerWrapper, getResources()
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
        } catch (IOException | IndexOutOfBoundsException e) {
            Toast.makeText(this, getString(R.string.map_error_unable_locate_address), Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private void uploadData() {

        ProfileDetails profileDetails = new ProfileDetails();
        if (mRegisterBinder.textviewEmail.getText().length() > 0) {
            profileDetails.setEmail(mRegisterBinder.textviewEmail.getText().toString());
        }

        if (mRegisterBinder.textviewVendorName.getText().length() > 0) {
            profileDetails.setShopName(mRegisterBinder.textviewVendorName.getText().toString());
        }

        if (mRegisterBinder.textviewPassword.getText().length() > 0) {
            profileDetails.setPassword(mRegisterBinder.textviewPassword.getText().toString());
        }
        if (mRegisterBinder.textviewAddress.getText().length() > 0) {
            profileDetails.setAddress(mRegisterBinder.textviewAddress.getText().toString());
            if (mLatitude == 0 || mLongitude == 0) {
                isLocated(mRegisterBinder.textviewAddress.getText().toString());
            }
            profileDetails.setLatitude(mLatitude);
            profileDetails.setLongitude(mLongitude);
        }

        if (mRegisterBinder.textviewCity.getText().length() > 0) {
            profileDetails.setCity(mRegisterBinder.textviewCity.getText().toString());
        }
        if (mRegisterBinder.textviewState.getText().length() > 0) {
            profileDetails.setState(mRegisterBinder.textviewState.getText().toString());
        }
        if (mRegisterBinder.textviewCountry.getText().length() > 0) {
            profileDetails.setCountry(mRegisterBinder.textviewCountry.getText().toString());
        }
        if (mRegisterBinder.textviewZip.getText().length() > 0) {
            profileDetails.setPin(mRegisterBinder.textviewZip.getText().toString());
        }

        if (mRegisterBinder.textviewPhone1.getText().length() > 0) {
            profileDetails.setPhone1(mRegisterBinder.textviewPhone1.getText().toString());
        }
        if (mRegisterBinder.textviewPhone2.getText().length() > 0) {
            profileDetails.setPhone2(mRegisterBinder.textviewPhone2.getText().toString());
        }
        if (mRegisterBinder.textviewTin.getText().length() > 0) {
            profileDetails.setTinNumber(mRegisterBinder.textviewTin.getText().toString());
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

        JSONObject responseObject;

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
                    MultipartUtility multipart = new MultipartUtility(url, charset, RegisterActivity.this);

                    ProfileDetails profileDetails = registerVendorDetails.getProfileDetails();
                    multipart.addFormField(getString(R.string.api_key_email), profileDetails.getEmail());
                    multipart.addFormField(getString(R.string.api_key_shop_name), profileDetails.getShopName());
                    multipart.addFormField(getString(R.string.api_key_password), profileDetails.getPassword());
                    multipart.addFormField(getString(R.string.api_key_address), profileDetails.getAddress());
                    multipart.addFormField(getString(R.string.api_key_latitude), String.valueOf(profileDetails.getLatitude()));
                    multipart.addFormField(getString(R.string.api_key_longitude), String.valueOf(profileDetails.getLongitude()));
                    multipart.addFormField(getString(R.string.api_key_city), profileDetails.getCity());
                    multipart.addFormField(getString(R.string.api_key_state), profileDetails.getState());
                    multipart.addFormField(getString(R.string.api_key_country), profileDetails.getCountry());
                    multipart.addFormField(getString(R.string.api_key_pin), profileDetails.getPin());
                    multipart.addFormField(getString(R.string.api_key_phone1), profileDetails.getPhone1());
                    multipart.addFormField(getString(R.string.api_key_phone2), profileDetails.getPhone2());
                    multipart.addFormField(getString(R.string.api_key_tin_num), profileDetails.getTinNumber());

                    ArrayList<FileDetails> dataList = registerVendorDetails.getUploadDocList();
                    if (dataList.size() > 0) {
                        for (int i = 0; i < dataList.size(); i++) {
                            FileDetails fileDetails = dataList.get(i);
                            File file = new File(fileDetails.getFilePath());
                            multipart.addFilePart("verification_docs[]", file);
                        }
                    }

                    dataList = registerVendorDetails.getUploadImageList();
                    if (dataList.size() > 0) {
                        for (int i = 0; i < dataList.size(); i++) {
                            FileDetails fileDetails = dataList.get(i);
                            File file = new File(fileDetails.getFilePath());
                            multipart.addFilePart("shop_images[]", file);
                        }
                    }
                    String response = multipart.finish(HttpURLConnection.HTTP_OK);
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
            dismissDialog();
            if (_status) {
                mRegisterVendorDetails.removeDocList();
                mDocUploadAdapter.notifyDataSetChanged();

                mRegisterVendorDetails.removeImageList();
                mImageUploadAdapter.notifyDataSetChanged();
                showRegisterSuccessMessage(responseObject);
            } else {
                Snackbar.make(mBinder.outerWrapper, getResources().getString(R.string.msg_data_upload_failed), Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.text_view_locate:
                locateAddressSelected();
                break;

            case R.id.textView_doc_upload:
                uploadDocuments();
                break;

            case R.id.text_view_picture_upload:
                uploadPictures();
                break;

            case R.id.btn_register:
                registerSelected();
                break;

            case R.id.btn_try_again:
                initializeActivity(mContext);
                break;
        }
    }

    public void locateAddressSelected() {
        if (mRegisterBinder.textviewAddress.getText().length() > 0) {
            if (isLocated(mRegisterBinder.textviewAddress.getText().toString())) {
                Intent mapIntent = new Intent(this, MapActivity.class);
                mapIntent.putExtra(getString(R.string.key_latitude), mLatitude);
                mapIntent.putExtra(getString(R.string.key_longitude), mLongitude);
                mapIntent.putExtra(getString(R.string.key_address), mRegisterBinder.textviewAddress.getText());
                mapIntent.putExtra(getString(R.string.key_is_draggable), true);
                startActivityForResult(mapIntent, TYPE_MAP);
            }
        } else {
            Toast.makeText(RegisterActivity.this, getString(R.string.fld_error_address_for_map), Toast.LENGTH_LONG).show();
        }
    }

    public void registerSelected() {

        boolean isValidInput = isValidInput();
        if (isValidInput) {
            uploadData();
        }
    }

    public void uploadDocuments() {
        mCurrentUploadType = TYPE_DOC_UPLOAD;
        requestPermission();
    }

    public void uploadPictures() {
        mCurrentUploadType = TYPE_PICTURES_UPLOAD;
        requestPermission();
    }
}
