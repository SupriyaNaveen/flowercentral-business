package com.flowercentral.flowercentralbusiness.login.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.rest.BaseModel;
import com.flowercentral.flowercentralbusiness.rest.QueryBuilder;
import com.flowercentral.flowercentralbusiness.util.Util;
import com.flowercentral.flowercentralbusiness.volley.ErrorData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 18-05-2017.
 */

public class RegisterActivity extends AppCompatActivity {

    private String TAG = RegisterActivity.class.getSimpleName();
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        mContext = this;
        initializeActivity(mContext);
    }

    private void initializeActivity(Context _context) {
        //Check internet connectivity
        if (Util.checkInternet(_context)) {
            mFrameLayoutNoInternet.setVisibility(View.GONE);
            mLinearLayoutRegister.setVisibility(View.VISIBLE);

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
}
