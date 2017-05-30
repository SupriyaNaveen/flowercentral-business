package com.flowercentral.flowercentralbusiness.login.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.dashboard.DashboardActivity;
import com.flowercentral.flowercentralbusiness.login.ui.model.Vendor;
import com.flowercentral.flowercentralbusiness.preference.UserPreference;
import com.flowercentral.flowercentralbusiness.rest.BaseModel;
import com.flowercentral.flowercentralbusiness.rest.QueryBuilder;
import com.flowercentral.flowercentralbusiness.util.PermissionUtil;
import com.flowercentral.flowercentralbusiness.util.Util;
import com.flowercentral.flowercentralbusiness.volley.ErrorData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 16-05-2017.
 */

public class LauncherActivity extends AppCompatActivity {

    private static final String TAG = LauncherActivity.class.getSimpleName();

    @BindView(R.id.btn_try_again)
    Button mButtonTryAgain;

    @BindView(R.id.login_wrapper)
    LinearLayout mLinearLayoutLogin;

    @BindView(R.id.fl_no_internet)
    FrameLayout mFrameLayoutNoInternet;

    @BindView(R.id.outer_wrapper)
    FrameLayout mFrameLayoutRoot;

    private MaterialDialog mProgressDialog;

    private Context mContext;

    @BindView(R.id.textview_vendor_name)
    TextInputEditText mTextViewVendorName;

    @BindView(R.id.textview_password)
    TextInputEditText mTextViewPassword;

    @BindView(R.id.textview_forgot_password)
    TextView mTextViewForgotPassword;

    @BindView(R.id.txt_link_flower_central_account)
    TextView mTextViewRegisterAccount;

    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    private ActionBar mActionBar;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        if (UserPreference.getApiToken() != null) {
            Intent intent = new Intent(LauncherActivity.this, DashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
            overridePendingTransition(0,0);
        } else {
            setContentView(R.layout.activity_launcher);
            ButterKnife.bind(this);
            initializeActivity(mContext);
        }

    }

    /**
     * Check for internet connection and update view accordingly.
     *
     * @param _context
     */
    private void initializeActivity(Context _context) {
        //Check internet connectivity
        if (Util.checkInternet(_context)) {
            mFrameLayoutNoInternet.setVisibility(View.GONE);
            mLinearLayoutLogin.setVisibility(View.VISIBLE);

        } else {
            mFrameLayoutNoInternet.setVisibility(View.VISIBLE);
            mLinearLayoutLogin.setVisibility(View.GONE);
        }

        setSupportActionBar(mToolBar);

        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
            mActionBar = getSupportActionBar();
            if (mActionBar != null) {
                mActionBar.setHomeButtonEnabled(true);
                mActionBar.setTitle("");
                mActionBar.setDisplayHomeAsUpEnabled(true);
                mActionBar.setDisplayShowHomeEnabled(true);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Validate the input and make web api call.
     */
    @OnClick(R.id.btn_login)
    void loginSelected() {
        if (UserPreference.getApiToken() != null) {
            UserPreference.deleteProfileInformation();
        }

        boolean isValidInput = isValidInput();
        if (isValidInput) {
           requestPermissionBeforeLogin();
        }
    }

    /**
     * Validator class
     *
     * @return
     */
    private boolean isValidInput() {
        boolean isValid = true;

        if (mTextViewVendorName.getText().toString().isEmpty()) {
            mTextViewVendorName.setError("Please enter vendor name.");
            isValid = false;
        } else {
            mTextViewVendorName.setError(null);
        }

        if (mTextViewPassword.getText().toString().isEmpty()) {
            mTextViewPassword.setError("Please enter password.");
            isValid = false;
        } else {
            mTextViewPassword.setError(null);
        }

        return isValid;
    }

    @OnClick(R.id.textview_forgot_password)
    void forgotPasswordSelected() {

    }

    @OnClick(R.id.txt_link_flower_central_account)
    void registerAccountSelected() {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    @OnClick(R.id.btn_try_again)
    void tryAgainToRegister() {
        initializeActivity(mContext);
    }

    /**
     * Register VendorDetails using Social Login
     */
    private void registerUser(Context _context, JSONObject _user) {
        //Start Progress dialog
        dismissDialog();

        mProgressDialog = Util.showProgressDialog(_context, null, getString(R.string.msg_registering_user), false);

        BaseModel<JSONObject> baseModel = new BaseModel<JSONObject>(_context) {
            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, JSONObject response) {
                Vendor vendor = new Gson().<Vendor>fromJson(String.valueOf(response),
                        new TypeToken<Vendor>() {
                        }.getType());
                UserPreference.setProfileInformation(vendor);
                //CLose Progress dialog
                dismissDialog();
                mContext.startActivity(new Intent(LauncherActivity.this, DashboardActivity.class));
                finish();
            }

            @Override
            public void onError(ErrorData error) {
                //Close Progress dialog
                dismissDialog();

                if (error != null) {
                    error.setErrorMessage("Login failed. Cause -> " + error.getErrorMessage());

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

        String url = QueryBuilder.getLoginUrl();
        if (_user != null) {
            baseModel.executePostJsonRequest(url, _user, TAG);
        } else {
            Snackbar.make(mFrameLayoutRoot, getResources().getString(R.string.msg_reg_user_missing_input), Snackbar.LENGTH_SHORT).show();
        }
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

    /**
     * Request for the runtime permission (SDK >= Marshmallow devices)
     */
    private void requestPermissionBeforeLogin() {
        if (PermissionUtil.hasPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            performLogin();
        } else {
            PermissionUtil.requestPermission(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    PermissionUtil.REQUEST_CODE_READ_PHONE_STATE);
        }
    }

    private void performLogin() {
        try {
            JSONObject user = new JSONObject();
            user.put("username", mTextViewVendorName.getText());
            user.put("password", mTextViewPassword.getText());
            String android_id = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            user.put("deviceid", android_id);
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            user.put("imei", telephonyManager.getDeviceId());
            registerUser(mContext, user);
        } catch (JSONException e) {
            Snackbar.make(mFrameLayoutRoot, getResources().getString(R.string.msg_reg_user_missing_input), Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PermissionUtil.REQUEST_CODE_READ_PHONE_STATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    performLogin();
                } else {
                    if (PermissionUtil.showRationale(this, Manifest.permission.READ_PHONE_STATE)) {
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
                .s_required_permission_phone_state), Snackbar.LENGTH_INDEFINITE).setAction(getResources().getString(R.string
                .s_action_request_again), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissionBeforeLogin();
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
                        startActivityForResult(intent, PermissionUtil.REQUEST_CODE_READ_PHONE_STATE);
                    }
                });
        snackbar.show();
    }
}
