package com.flowercentral.flowercentralbusiness;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.binarysoft.sociallogin.facebook.FacebookHelper;
import com.binarysoft.sociallogin.facebook.FacebookListener;
import com.binarysoft.sociallogin.google.GoogleHelper;
import com.binarysoft.sociallogin.google.GoogleListener;
import com.binarysoft.sociallogin.google.GoogleUser;
import com.binarysoft.sociallogin.instagram.InstagramHelper;
import com.binarysoft.sociallogin.instagram.InstagramListener;
import com.binarysoft.sociallogin.instagram.InstagramUser;
import com.flowercentral.flowercentralbusiness.preference.UserPreference;
import com.flowercentral.flowercentralbusiness.setting.AppConstant;

import static android.R.attr.data;

/**
 * Created by admin on 16-05-2017.
 */

public class BaseActivity extends AppCompatActivity implements FacebookListener, GoogleListener, InstagramListener {

    protected Context mContext;
    protected int mLoginType;
    protected FacebookHelper mFacebookHelper;
    protected GoogleHelper mGoogleHelper;
    protected InstagramHelper mInstagramHelper;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        mContext = this;
        initSocialPlugins();
    }

    protected void initSocialPlugins() {
        //Social Login
        if (mFacebookHelper == null) {
            mFacebookHelper = new FacebookHelper(this);
        }

        if (mGoogleHelper == null) {
            mGoogleHelper = new GoogleHelper(this, this, null);
        }

        if (mInstagramHelper == null) {
            mInstagramHelper = new InstagramHelper(this, mContext, getString(R.string.instagram_client_id)
                    , getString(R.string.instagram_client_secret), getString(R.string.instagram_callback_url));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mFacebookHelper.onActivityResult(requestCode, resultCode, data);
        mGoogleHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onFbSignInFail(String _errorMessage) {

    }

    @Override
    public void onFbSignInSuccess(String _authToken, String _userId) {

    }

    @Override
    public void onFBSignOut() {

    }

    @Override
    public void onInstagramSignInFail(String _errorMessage) {

    }

    @Override
    public void onInstagramSignInSuccess(String _authToken, InstagramUser _instagramUser) {

    }

    @Override
    public void onGoogleAuthSignIn(String _authToken, GoogleUser _userAccount) {

    }

    @Override
    public void onGoogleAuthSignInFailed(String _errorMessage) {

    }

    @Override
    public void onGoogleAuthSignOut() {

    }
}
