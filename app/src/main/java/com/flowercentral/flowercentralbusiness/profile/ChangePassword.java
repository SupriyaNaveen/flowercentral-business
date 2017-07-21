package com.flowercentral.flowercentralbusiness.profile;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andexert.library.RippleView;
import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.databinding.FragmentChangePasswordBinding;
import com.flowercentral.flowercentralbusiness.rest.BaseModel;
import com.flowercentral.flowercentralbusiness.rest.QueryBuilder;
import com.flowercentral.flowercentralbusiness.volley.ErrorData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 *
 */
public class ChangePassword extends Fragment implements RippleView.OnRippleCompleteListener {

    private static final String TAG = ChangePassword.class.getSimpleName();
    private Context mContext;

    private FragmentChangePasswordBinding mBinder;

    /**
     * Instantiate ChangePassword fragment.
     *
     * @return instance of fragment
     */
    public static ChangePassword newInstance() {
        return new ChangePassword();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinder = DataBindingUtil.inflate(inflater, R.layout.fragment_change_password, container, false);

        mContext = getActivity();
        mBinder.btnResetPassword.setOnRippleCompleteListener(this);
        return mBinder.getRoot();
    }

    @Override
    public void onComplete(RippleView view) {
        if (view.getId() == R.id.btn_reset_password) {
            if (mBinder.editTextOldPassword.getText().length() > 0) {
                if (mBinder.editTextNewPassword.getText().length() > 0) {
                    if (mBinder.editTextConfirmNewPassword.getText().length() > 0) {
                        if (mBinder.editTextNewPassword.getText().toString().equals(mBinder.editTextConfirmNewPassword.getText().toString())) {
                            performResetPassword();
                        } else {
                            Snackbar.make(mBinder.rootLayout, getString(R.string.message_password_mismatch), Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        mBinder.editTextConfirmNewPassword.setError(getString(R.string.fld_error_confirm_password));
                    }
                } else {
                    mBinder.editTextNewPassword.setError(getString(R.string.fld_error_new_password));
                }
            } else {
                mBinder.editTextOldPassword.setError(getString(R.string.fld_error_old_password));
            }
        }
    }

    private void performResetPassword() {

        try {
            JSONObject requestObject = new JSONObject();
            if (mBinder.editTextOldPassword.getText().length() > 0) {
                requestObject.put(getString(R.string.api_key_old_password), mBinder.editTextOldPassword.getText());
            }
            if (mBinder.editTextNewPassword.getText().length() > 0) {
                requestObject.put(getString(R.string.api_key_new_password), mBinder.editTextNewPassword.getText());
            }
            if (mBinder.editTextConfirmNewPassword.getText().length() > 0) {
                requestObject.put(getString(R.string.api_key_confirm_password), mBinder.editTextConfirmNewPassword.getText());
            }

            BaseModel<JSONObject> baseModel = new BaseModel<JSONObject>(mContext) {
                @Override
                public void onSuccess(int statusCode, Map<String, String> headers, JSONObject response) {
                    try {
                        if (response.getInt(getString(R.string.api_res_status)) == 1) {
                            mBinder.editTextNewPassword.setText("");
                            mBinder.editTextConfirmNewPassword.setText("");
                            mBinder.editTextOldPassword.setText("");
                            Snackbar.make(mBinder.rootLayout, "Password reset successful", Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(mBinder.rootLayout, "Password reset failed", Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Snackbar.make(mBinder.rootLayout, "Password reset failed", Snackbar.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(ErrorData error) {
                    if (error != null) {
                        error.setErrorMessage("Update failed. Cause -> " + error.getErrorMessage());
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

            String url = QueryBuilder.getChangePasswordUrl();
            baseModel.executePostJsonRequest(url, requestObject, TAG);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
