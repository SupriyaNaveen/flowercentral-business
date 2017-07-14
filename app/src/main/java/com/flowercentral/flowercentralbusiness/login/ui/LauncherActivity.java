package com.flowercentral.flowercentralbusiness.login.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.dashboard.DashboardActivity;
import com.flowercentral.flowercentralbusiness.databinding.ActivityLauncherBinding;
import com.flowercentral.flowercentralbusiness.databinding.LayoutLoginBinding;
import com.flowercentral.flowercentralbusiness.login.ui.model.Vendor;
import com.flowercentral.flowercentralbusiness.notification.NotificationMessageHandler;
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

/**
 *
 */
public class LauncherActivity extends AppCompatActivity {

    private static final String TAG = LauncherActivity.class.getSimpleName();
    private ActivityLauncherBinding mBinder;
    private MaterialDialog mProgressDialog;

    private Context mContext;
    private LayoutLoginBinding mLtLoginBinder;

    /**
     * @param savedInstanceState saved instance
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        if (UserPreference.getApiToken(this) != null) {
            Intent intent = new Intent(LauncherActivity.this, DashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        } else {
            mBinder = DataBindingUtil.setContentView(this, R.layout.activity_launcher);
            mLtLoginBinder = mBinder.ltLogin;
            initializeActivity(mContext);
        }

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            NotificationMessageHandler.getInstance().handleNotificationData(this, bundle);
        }
    }

    /**
     * Check for internet connection and update view accordingly.
     *
     * @param _context context
     */
    private void initializeActivity(Context _context) {
        //Check internet connectivity
        if (Util.checkInternet(_context)) {
            mBinder.flNoInternet.setVisibility(View.GONE);
            mBinder.loginWrapper.setVisibility(View.VISIBLE);

        } else {
            mBinder.flNoInternet.setVisibility(View.VISIBLE);
            mBinder.loginWrapper.setVisibility(View.GONE);
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
     * Validator class
     *
     * @return is valid or not
     */
    private boolean isValidInput() {
        boolean isValid = true;

        if (mLtLoginBinder.textviewVendorName.getText().toString().isEmpty()) {
            mLtLoginBinder.textviewVendorName.setError(getString(R.string.fld_error_user_name));
            isValid = false;
        } else {
            mLtLoginBinder.textviewVendorName.setError(null);
        }

        if (mLtLoginBinder.textviewPassword.getText().toString().isEmpty()) {
            mLtLoginBinder.textviewPassword.setError(getString(R.string.fld_error_password));
            isValid = false;
        } else {
            mLtLoginBinder.textviewPassword.setError(null);
        }

        return isValid;
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
                Vendor vendor = new Gson().fromJson(String.valueOf(response),
                        new TypeToken<Vendor>() {
                        }.getType());
                UserPreference.setProfileInformation(mContext, vendor);
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

        String url = QueryBuilder.getLoginUrl();
        if (_user != null) {
            baseModel.executePostJsonRequest(url, _user, TAG);
        } else {
            Snackbar.make(mBinder.rootLayout, getResources().getString(R.string.msg_reg_user_missing_input), Snackbar.LENGTH_SHORT).show();
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

    @SuppressLint("HardwareIds")
    private void performLogin() {
        try {
            JSONObject user = new JSONObject();
            user.put(getString(R.string.api_key_username), mLtLoginBinder.textviewVendorName.getText());
            user.put(getString(R.string.api_key_password), mLtLoginBinder.textviewPassword.getText());
            user.put(getString(R.string.api_key_device_id), Util.getDeviceId(this));
            user.put(getString(R.string.api_key_imei), Util.getIEMINumber(this));
            registerUser(mContext, user);
        } catch (JSONException e) {
            Snackbar.make(mBinder.rootLayout, getResources().getString(R.string.msg_reg_user_missing_input), Snackbar.LENGTH_SHORT).show();
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
        Snackbar snackbar = Snackbar.make(mBinder.rootLayout, getResources().getString(R.string
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
        Snackbar snackbar = Snackbar.make(mBinder.rootLayout, getResources()
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

    /**
     * Validate the input and make web api call.
     */
    public void loginSelected(View view) {
        if (UserPreference.getApiToken(LauncherActivity.this) != null) {
            UserPreference.deleteProfileInformation(LauncherActivity.this);
        }

        boolean isValidInput = isValidInput();
        if (isValidInput) {
            requestPermissionBeforeLogin();
        }
    }

    public void forgotPasswordSelected(View view) {

    }

    public void registerAccountSelected(View view) {
        startActivity(new Intent(LauncherActivity.this, RegisterActivity.class));
    }

    public void tryAgainToRegister(View view) {
        initializeActivity(mContext);
    }
}
