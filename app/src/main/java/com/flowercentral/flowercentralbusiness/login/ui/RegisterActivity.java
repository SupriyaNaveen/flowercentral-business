package com.flowercentral.flowercentralbusiness.login.ui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.login.ui.adapter.UploadListAdapter;
import com.flowercentral.flowercentralbusiness.rest.BaseModel;
import com.flowercentral.flowercentralbusiness.rest.QueryBuilder;
import com.flowercentral.flowercentralbusiness.util.MapActivity;
import com.flowercentral.flowercentralbusiness.util.Util;
import com.flowercentral.flowercentralbusiness.volley.ErrorData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 18-05-2017.
 */

public class RegisterActivity extends AppCompatActivity {

    private static final int REQ_CODE_READ_STORAGE = 100;
    private static final String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int TYPE_MAP = 3;
    private String TAG = RegisterActivity.class.getSimpleName();
    private static final int TYPE_DOC_UPLOAD = 1;
    private static final int TYPE_PICTURES_UPLOAD = 2;
    private static int currentUploadType;
    private Context mContext;

    @BindView(R.id.fl_no_internet)
    FrameLayout mFrameLayoutNoInternet;

    @BindView(R.id.outer_wrapper)
    FrameLayout mFrameLayoutRoot;

    @BindView(R.id.register_wrapper)
    LinearLayout mLinearLayoutRegister;

    @BindView(R.id.btn_register)
    Button buttonRegister;

    private MaterialDialog mProgressDialog;

    @BindView(R.id.textview_vendor_name)
    TextInputEditText editTextShopName;

    @BindView(R.id.textview_address)
    TextInputEditText editTextAddress;

    @BindView(R.id.textview_state)
    TextInputEditText editTextState;

    @BindView(R.id.textview_city)
    TextInputEditText editTextCity;

    @BindView(R.id.textview_zip)
    TextInputEditText editTextZip;

    @BindView(R.id.textview_phone1)
    TextInputEditText editTextPhone1;

    @BindView(R.id.textview_phone2)
    TextInputEditText editTextPhone2;

    @BindView(R.id.textview_tin)
    TextInputEditText editTextTIN;

    @BindView(R.id.textView_doc_upload)
    TextView textViewDocUpload;

    @BindView(R.id.image_view_locate)
    ImageView imageViewLocate;

    @BindView(R.id.list_view_doc)
    RecyclerView listViewDoc;

    @BindView(R.id.list_view_picture)
    RecyclerView listViewImage;

    private double mLongitude;
    private double mLatitude;
    private ArrayList<Uri> docPathList = new ArrayList<>();
    private ArrayList<Uri> imagePathList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        mContext = this;
        initializeActivity(mContext);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void initializeActivity(Context _context) {
        //Check internet connectivity
        if (Util.checkInternet(_context)) {
            mFrameLayoutNoInternet.setVisibility(View.GONE);
            mLinearLayoutRegister.setVisibility(View.VISIBLE);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            listViewDoc.setLayoutManager(mLayoutManager);
            mLayoutManager = new LinearLayoutManager(this);
            listViewImage.setLayoutManager(mLayoutManager);
            listViewDoc.setAdapter(new UploadListAdapter(this, docPathList));
            listViewImage.setAdapter(new UploadListAdapter(this, imagePathList));

        } else {
            mFrameLayoutNoInternet.setVisibility(View.VISIBLE);
            mLinearLayoutRegister.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btn_register)
    void registerSelected() {

        boolean isValidInput = isValidInput();
        if (isValidInput) {
            try {
                JSONObject register = new JSONObject();
                if (editTextShopName.getText().length() > 0) {
                    register.put("shop_name", editTextShopName.getText());
                }
                if (editTextAddress.getText().length() > 0) {
                    register.put("add1", editTextAddress.getText());
                    if (mLatitude == 0 || mLongitude == 0) {
                        isLocated(editTextAddress.getText().toString());
                    }
                    register.put("latitude", mLatitude);
                    register.put("longitude", mLongitude);
                }

                if (editTextCity.getText().length() > 0) {
                    register.put("city", editTextCity.getText());
                }
                if (editTextState.getText().length() > 0) {
                    register.put("state", editTextState.getText());
                }
                if (editTextZip.getText().length() > 0) {
                    register.put("pin", editTextZip.getText());
                }

                if (editTextPhone1.getText().length() > 0) {
                    register.put("phone1", editTextPhone1.getText());
                }
                if (editTextPhone2.getText().length() > 0) {
                    register.put("phone2", editTextPhone2.getText());
                }
                if (editTextTIN.getText().length() > 0) {
                    register.put("tin", editTextTIN.getText());
                }
                registerUser(mContext, register);
            } catch (JSONException e) {

                Snackbar.make(mFrameLayoutRoot, getResources().getString(R.string.msg_reg_user_missing_input), Snackbar.LENGTH_SHORT).show();
            }
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
        if (editTextShopName.getText().toString().isEmpty()) {
            editTextShopName.setError("Please enter vendor name.");
            isValid = false;
        } else {
            editTextShopName.setError(null);
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
        currentUploadType = TYPE_DOC_UPLOAD;
        requestPermission();
    }

    private void browseDocuments() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        startActivityForResult(intent, TYPE_DOC_UPLOAD);
    }

    @OnClick(R.id.text_view_picture_upload)
    void uploadPictures() {
        currentUploadType = TYPE_PICTURES_UPLOAD;
        requestPermission();
    }

    private void browsePictures() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, TYPE_PICTURES_UPLOAD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        UploadListAdapter listAdapter;
        switch (requestCode) {
            case TYPE_DOC_UPLOAD:
                if(data != null) {
                    docPathList.add(data.getData());
                    listViewDoc.setAdapter(new UploadListAdapter(this, docPathList));
                }
                break;
            case TYPE_PICTURES_UPLOAD:
                if(data != null) {
                    imagePathList.add(data.getData());
                    listViewImage.setAdapter(new UploadListAdapter(this, imagePathList));
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
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {

            if (currentUploadType == TYPE_DOC_UPLOAD) {
                browseDocuments();
            } else {
                browsePictures();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, REQ_CODE_READ_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQ_CODE_READ_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (currentUploadType == TYPE_DOC_UPLOAD) {
                        browseDocuments();
                    } else {
                        browsePictures();
                    }
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
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
                        startActivityForResult(intent, REQ_CODE_READ_STORAGE);
                    }
                });
        snackbar.show();
    }

    @OnClick(R.id.image_view_locate)
    void locateAddressSelected() {
        if (editTextAddress.getText().length() > 0) {
            if (isLocated(editTextAddress.getText().toString())) {
                Intent mapIntent = new Intent(this, MapActivity.class);
                mapIntent.putExtra(getString(R.string.key_latitude), mLatitude);
                mapIntent.putExtra(getString(R.string.key_longitude), mLongitude);
                mapIntent.putExtra(getString(R.string.key_address), editTextAddress.getText());
                mapIntent.putExtra(getString(R.string.key_is_draggable), true);
                startActivityForResult(mapIntent, TYPE_MAP);
            }
        } else {
            Toast.makeText(this, "Please enter address to locate on map.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isLocated(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                Toast.makeText(this, "Unable to locate the address.", Toast.LENGTH_LONG).show();
                return false;
            }

            Address location = address.get(0);
            mLongitude = location.getLongitude();
            mLatitude = location.getLatitude();
            return true;
        } catch (IOException e) {
            Toast.makeText(this, "Unable to locate the address.", Toast.LENGTH_LONG).show();
        }
        return false;
    }
}
