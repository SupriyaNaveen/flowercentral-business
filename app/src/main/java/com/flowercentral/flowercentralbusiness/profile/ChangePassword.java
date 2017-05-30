package com.flowercentral.flowercentralbusiness.profile;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.rest.BaseModel;
import com.flowercentral.flowercentralbusiness.rest.QueryBuilder;
import com.flowercentral.flowercentralbusiness.volley.ErrorData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 26-05-2017.
 */

public class ChangePassword extends Fragment {

    private static final String TAG = ChangePassword.class.getSimpleName();
    private Context mContext;

    @BindView(R.id.edit_text_old_password)
    TextInputEditText mEditTextOldPassword;

    @BindView(R.id.edit_text_new_password)
    TextInputEditText mEditTextNewPassword;

    @BindView(R.id.edit_text_confirm_new_password)
    TextInputEditText mEditTextConfirmNewPassword;

    @BindView(R.id.btn_reset_password)
    Button mButtonResetPassword;

    @BindView(R.id.root_layout)
    LinearLayout mLinearRootLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        ButterKnife.bind(this, view);

        mContext = getActivity();
        return view;
    }

    @OnClick(R.id.btn_reset_password)
    void buttonResetPasswordSelected() {
        if (mEditTextOldPassword.getText().length() > 0) {
            if (mEditTextNewPassword.getText().length() > 0) {
                if (mEditTextConfirmNewPassword.getText().length() > 0) {
                    if (mEditTextNewPassword.getText().toString().equals(mEditTextConfirmNewPassword.getText().toString())) {
                        performResetPassword();
                    } else {
                        Snackbar.make(mLinearRootLayout, "New password and confirm password does'nt match", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    mEditTextConfirmNewPassword.setError("Please enter confirm new password");
                }
            } else {
                mEditTextNewPassword.setError("Please enter new password");
            }
        } else {
            mEditTextOldPassword.setError("Please enter old password");
        }
    }

    private void performResetPassword() {

        try {
            JSONObject requestObject = new JSONObject();
            if (mEditTextOldPassword.getText().length() > 0) {
                requestObject.put("old_password", mEditTextOldPassword.getText());
            }
            if (mEditTextNewPassword.getText().length() > 0) {
                requestObject.put("new_pass", mEditTextNewPassword.getText());
            }
            if (mEditTextConfirmNewPassword.getText().length() > 0) {
                requestObject.put("confirm_new_password", mEditTextConfirmNewPassword.getText());
            }

            BaseModel<JSONObject> baseModel = new BaseModel<JSONObject>(mContext) {
                @Override
                public void onSuccess(int statusCode, Map<String, String> headers, JSONObject response) {
                    try {
                        if (response.getInt("status") == 1) {
                            mEditTextOldPassword.setText("");
                            mEditTextNewPassword.setText("");
                            mEditTextConfirmNewPassword.setText("");
                            Snackbar.make(mLinearRootLayout, "Password reset successful", Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(mLinearRootLayout, "Password reset failed", Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Snackbar.make(mLinearRootLayout, "Password reset failed", Snackbar.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(ErrorData error) {
                    if (error != null) {
                        error.setErrorMessage("Update failed. Cause -> " + error.getErrorMessage());
                        switch (error.getErrorType()) {
                            case NETWORK_NOT_AVAILABLE:
                                Snackbar.make(mLinearRootLayout, getResources().getString(R.string.msg_internet_unavailable), Snackbar.LENGTH_SHORT).show();
                                break;
                            case INTERNAL_SERVER_ERROR:
                                Snackbar.make(mLinearRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                                break;
                            case CONNECTION_TIMEOUT:
                                Snackbar.make(mLinearRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                                break;
                            case APPLICATION_ERROR:
                                Snackbar.make(mLinearRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                                break;
                            case INVALID_INPUT_SUPPLIED:
                                Snackbar.make(mLinearRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                                break;
                            case AUTHENTICATION_ERROR:
                                Snackbar.make(mLinearRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                                break;
                            case UNAUTHORIZED_ERROR:
                                Snackbar.make(mLinearRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                                break;
                            default:
                                Snackbar.make(mLinearRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }
            };

            String url = QueryBuilder.getChangePasswordUrl();
            if (requestObject != null) {
                baseModel.executePostJsonRequest(url, requestObject, TAG);
            } else {
                Snackbar.make(mLinearRootLayout, getResources().getString(R.string.msg_reg_user_missing_input), Snackbar.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {

        }
    }
}
