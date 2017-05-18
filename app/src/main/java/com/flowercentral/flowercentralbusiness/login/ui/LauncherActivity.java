package com.flowercentral.flowercentralbusiness.login.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.binarysoft.sociallogin.facebook.FacebookGraphListner;
import com.binarysoft.sociallogin.facebook.FacebookUser;
import com.binarysoft.sociallogin.google.GoogleUser;
import com.binarysoft.sociallogin.instagram.InstagramUser;
import com.flowercentral.flowercentralbusiness.BaseActivity;
import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.dashboard.DashboardActivity;
import com.flowercentral.flowercentralbusiness.preference.UserPreference;
import com.flowercentral.flowercentralbusiness.rest.BaseModel;
import com.flowercentral.flowercentralbusiness.rest.QueryBuilder;
import com.flowercentral.flowercentralbusiness.setting.AppConstant;
import com.flowercentral.flowercentralbusiness.util.Util;
import com.flowercentral.flowercentralbusiness.volley.ErrorData;

import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 16-05-2017.
 */

public class LauncherActivity extends BaseActivity {

    private static final String TAG = LauncherActivity.class.getSimpleName();

    @BindView(R.id.btn_google)
    Button mButtonGoogle;

    @BindView(R.id.btn_fb)
    Button mButtonFacebook;

    @BindView(R.id.btn_instagram)
    Button mButtonInstagram;

    @BindView(R.id.btn_try_again)
    Button mButtonTryAgain;

    @BindView(R.id.login_wrapper)
    LinearLayout mLinearLayoutSocialLogin;

    @BindView(R.id.fl_no_internet)
    FrameLayout mFrameLayoutNoInternet;

    @BindView(R.id.outer_wrapper)
    FrameLayout mFrameLayoutRoot;

    private MaterialDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launcher);
        ButterKnife.bind(this);

        mContext = this;
        initializeActivity(mContext);
    }

    private void initializeActivity(Context _context) {
        //Check internet connectivity
        if (Util.checkInternet(_context)) {
            mFrameLayoutNoInternet.setVisibility(View.GONE);
            mLinearLayoutSocialLogin.setVisibility(View.VISIBLE);
            //Initialize social plugins
            initSocialPlugins();

        } else {
            mFrameLayoutNoInternet.setVisibility(View.VISIBLE);
            mLinearLayoutSocialLogin.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btn_google)
    void registerThroughGoogle() {
        if (UserPreference.getAccessToken() != null) {
            UserPreference.deleteProfileInformation();
        }

        mLoginType = AppConstant.LOGIN_TYPE.GOOGLE.ordinal();
        mGoogleHelper.performSignIn(this);
    }

    @OnClick(R.id.btn_fb)
    void registerThroughFacebook() {
        if (UserPreference.getAccessToken() != null) {
            UserPreference.deleteProfileInformation();
        }

        mLoginType = AppConstant.LOGIN_TYPE.FACEBOOK.ordinal();
        mFacebookHelper.performSignIn(this);
    }

    @OnClick(R.id.btn_instagram)
    void registerThroughInstagram() {
        if (UserPreference.getAccessToken() != null) {
            UserPreference.deleteProfileInformation();
        }

        mLoginType = AppConstant.LOGIN_TYPE.INSTAGRAM.ordinal();
        mInstagramHelper.performSignIn();
    }

    @OnClick(R.id.btn_try_again)
    void tryAgainToRegister() {
        initializeActivity(mContext);
    }

    @Override
    public void onGoogleAuthSignIn(String authToken, GoogleUser _userAccount) {
        //No internet connection then return
        if (!Util.checkInternet(mContext)) {
            Snackbar.make(mFrameLayoutRoot, getResources().getString(R.string.msg_internet_unavailable), Snackbar.LENGTH_SHORT).show();
            return;
        }
        //Prepare json object by getting required user info from GoogleUser object
        JSONObject user = new JSONObject();
        registerUser(mContext, user);
    }

    @Override
    public void onGoogleAuthSignInFailed(String errorMessage) {
        Snackbar.make(mFrameLayoutRoot, errorMessage, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onGoogleAuthSignOut() {
        UserPreference.deleteProfileInformation();
        Snackbar.make(mFrameLayoutRoot, getString(R.string.msg_logout_success), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onFbSignInFail(String errorMessage) {
        Snackbar.make(mFrameLayoutRoot, errorMessage, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onFbSignInSuccess(String authToken, String userId) {
//        String msg = String.format(Locale.US, "User id:%s\n\nAuthToken:%s", userId, authToken);

        //Must implement this interface before calling get Profile
        mFacebookHelper.registerProfileListener(new FacebookGraphListner() {
            @Override
            public void onProfileReceived(FacebookUser _profile) {
                //No internet connection then return
                if (!Util.checkInternet(mContext)) {
                    Snackbar.make(mFrameLayoutRoot, getResources().getString(R.string.msg_internet_unavailable), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                //Prepare json object by getting required user info from FacebookUser object
                JSONObject user = new JSONObject();
                registerUser(mContext, user);
            }
        });

        try {
            mFacebookHelper.getProfile();
        } catch (RuntimeException rEx) {
            rEx.printStackTrace();
        }
    }

    @Override
    public void onFBSignOut() {
        UserPreference.deleteProfileInformation();
        Snackbar.make(mFrameLayoutRoot, getString(R.string.msg_logout_success), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onInstagramSignInFail(String _errorMessage) {
        Snackbar.make(mFrameLayoutRoot, _errorMessage, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onInstagramSignInSuccess(String _authToken, InstagramUser _instagramUser) {

        //No internet connection then return
        if (!Util.checkInternet(mContext)) {
            Snackbar.make(mFrameLayoutRoot, getResources().getString(R.string.msg_internet_unavailable), Snackbar.LENGTH_SHORT).show();
            return;
        }
        //Prepare json object by getting required user info from InstagramUser object
        JSONObject user = new JSONObject();
        registerUser(mContext, user);
    }

    /**
     * Register User using Social Login
     */
    private void registerUser(Context _context, JSONObject _user) {
        //Start Progress dialog
        dismissDialog();

        mProgressDialog = Util.showProgressDialog(_context, null, getString(R.string.msg_registering_user), false);

        BaseModel<JSONObject> baseModel = new BaseModel<JSONObject>(_context) {
            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, JSONObject response) {
                //CLose Progress dialog
                dismissDialog();
                mContext.startActivity(new Intent(LauncherActivity.this, DashboardActivity.class));
            }

            @Override
            public void onError(ErrorData error) {
                //Close Progress dialog
                dismissDialog();

                if (error != null) {

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

}
