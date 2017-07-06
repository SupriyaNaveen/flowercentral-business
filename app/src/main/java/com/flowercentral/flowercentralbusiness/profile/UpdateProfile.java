package com.flowercentral.flowercentralbusiness.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.map.MapActivity;
import com.flowercentral.flowercentralbusiness.profile.model.ProfileDetails;
import com.flowercentral.flowercentralbusiness.rest.BaseModel;
import com.flowercentral.flowercentralbusiness.rest.QueryBuilder;
import com.flowercentral.flowercentralbusiness.util.Util;
import com.flowercentral.flowercentralbusiness.volley.ErrorData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UpdateProfile extends Fragment {

    private static final String TAG = UpdateProfile.class.getSimpleName();
    private static final int TYPE_MAP = 100;
    private Context mContext;

    @BindView(R.id.fl_no_internet)
    FrameLayout mFrameLayoutNoInternet;

    @BindView(R.id.outer_wrapper)
    FrameLayout mFrameLayoutRoot;

    @BindView(R.id.register_wrapper)
    LinearLayout mLinearLayoutRegister;

    @BindView(R.id.btn_update)
    Button mButtonRegister;

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

    @BindView(R.id.text_view_locate)
    TextView mTextViewLocate;

    private double mLongitude;
    private double mLatitude;
    private MaterialDialog mProgressDialog;

    /**
     * Instantiate UpdateProfile fragment.
     *
     * @return instance of fragment
     */
    public static UpdateProfile newInstance() {
        return new UpdateProfile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_profile, container, false);
        ButterKnife.bind(this, view);

        mContext = getActivity();
        initializeActivity(getActivity());
        return view;
    }

    private void initializeActivity(Context _context) {
        //Check internet connectivity
        if (Util.checkInternet(_context)) {
            mFrameLayoutNoInternet.setVisibility(View.GONE);
            mLinearLayoutRegister.setVisibility(View.VISIBLE);
            getProfileDetails();
        } else {
            mFrameLayoutNoInternet.setVisibility(View.VISIBLE);
            mLinearLayoutRegister.setVisibility(View.GONE);
        }
    }

    private void getProfileDetails() {

        BaseModel<JSONObject> baseModel = new BaseModel<JSONObject>(getActivity()) {
            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, JSONObject response) {
                displayProfileInformation(response);
            }

            @Override
            public void onError(ErrorData error) {
                if (error != null) {
                    error.setErrorMessage("Fetch profile details failed. Cause -> " + error.getErrorMessage());
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

        String url = QueryBuilder.getProfileInformationUrl();
        baseModel.executeGetJsonRequest(url, TAG);
    }

    private void displayProfileInformation(JSONObject response) {
        ProfileDetails profileDetail = new Gson().fromJson(String.valueOf(response),
                new TypeToken<ProfileDetails>() {
                }.getType());
        mEditTextShopName.setText(profileDetail.getShopName());
        mEditTextAddress.setText(profileDetail.getAddress());
        mEditTextCity.setText(profileDetail.getCity());
        mEditTextState.setText(profileDetail.getState());
        mEditTextPhone1.setText(profileDetail.getPhone1());
        mEditTextPhone2.setText(profileDetail.getPhone2());
        mLatitude = profileDetail.getLatitude();
        mLongitude = profileDetail.getLongitude();
        mEditTextTIN.setText(profileDetail.getTinNumber());
        mEditTextZip.setText(profileDetail.getPin());
    }

    @OnClick(R.id.btn_cancel)
    void cancelSelected() {
        //TODO
    }

    @OnClick(R.id.btn_update)
    void updateSelected() {

        boolean isValidInput = isValidInput();
        if (isValidInput) {
            try {
                JSONObject updateJson = new JSONObject();
                if (mEditTextShopName.getText().length() > 0) {
                    updateJson.put(getString(R.string.api_key_shop_name), mEditTextShopName.getText());
                }
                if (mEditTextAddress.getText().length() > 0) {
                    updateJson.put(getString(R.string.api_key_address), mEditTextAddress.getText());
                    if (mLatitude == 0 || mLongitude == 0) {
                        isLocated(mEditTextAddress.getText().toString());
                    }
                    updateJson.put(getString(R.string.api_key_latitude), mLatitude);
                    updateJson.put(getString(R.string.api_key_longitude), mLongitude);
                }

                if (mEditTextCity.getText().length() > 0) {
                    updateJson.put(getString(R.string.api_key_city), mEditTextCity.getText());
                }
                if (mEditTextState.getText().length() > 0) {
                    updateJson.put(getString(R.string.api_key_state), mEditTextState.getText());
                }
                if (mEditTextZip.getText().length() > 0) {
                    updateJson.put(getString(R.string.api_key_pin), mEditTextZip.getText());
                }

                if (mEditTextPhone1.getText().length() > 0) {
                    updateJson.put(getString(R.string.api_key_phone1), mEditTextPhone1.getText());
                }
                if (mEditTextPhone2.getText().length() > 0) {
                    updateJson.put(getString(R.string.api_key_phone2), mEditTextPhone2.getText());
                }
                if (mEditTextTIN.getText().length() > 0) {
                    updateJson.put(getString(R.string.api_key_tin_num), mEditTextTIN.getText());
                }
                mProgressDialog = Util.showProgressDialog(getActivity(), getString(R.string.msg_registering_user), null, false);
                updateUser(mContext, updateJson);
            } catch (JSONException e) {

                Snackbar.make(mFrameLayoutRoot, getResources().getString(R.string.msg_reg_user_missing_input), Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void updateUser(Context _context, JSONObject _user) {
        BaseModel<JSONObject> baseModel = new BaseModel<JSONObject>(_context) {
            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, JSONObject response) {
                dismissDialog();
                showUpdateSuccessMessage(response);
            }

            @Override
            public void onError(ErrorData error) {
                dismissDialog();
                if (error != null) {
                    error.setErrorMessage("Update failed. Cause -> " + error.getErrorMessage());
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

        String url = QueryBuilder.getUpdateUrl();
        if (_user != null) {
            baseModel.executePostJsonRequest(url, _user, TAG);
        } else {
            Snackbar.make(mFrameLayoutRoot, getResources().getString(R.string.msg_reg_user_missing_input), Snackbar.LENGTH_SHORT).show();
        }
    }

    private void showUpdateSuccessMessage(final JSONObject response) {

        try {
            if (response.getInt("status") == 1) {

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage("Profile updated successfully");
                alertDialogBuilder.setPositiveButton("Okay",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            } else {
                Snackbar.make(mFrameLayoutRoot, "Update profile failed", Snackbar.LENGTH_SHORT).show();
            }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case TYPE_MAP:
                mLatitude = data.getDoubleExtra(getString(R.string.key_latitude), 0.0);
                mLongitude = data.getDoubleExtra(getString(R.string.key_longitude), 0.0);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.text_view_locate)
    void locateAddressSelected() {
        if (mEditTextAddress.getText().length() > 0) {
            if (isLocated(mEditTextAddress.getText().toString())) {
                Intent mapIntent = new Intent(getActivity(), MapActivity.class);
                mapIntent.putExtra(getString(R.string.key_latitude), mLatitude);
                mapIntent.putExtra(getString(R.string.key_longitude), mLongitude);
                mapIntent.putExtra(getString(R.string.key_address), mEditTextAddress.getText());
                mapIntent.putExtra(getString(R.string.key_is_draggable), true);
                startActivityForResult(mapIntent, TYPE_MAP);
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.fld_error_address_for_map), Toast.LENGTH_LONG).show();
        }
    }

    private boolean isLocated(String strAddress) {

        Geocoder coder = new Geocoder(getActivity());
        List<Address> address;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                Toast.makeText(getActivity(), getString(R.string.map_error_unable_locate_address), Toast.LENGTH_LONG).show();
                return false;
            }

            Address location = address.get(0);
            mLongitude = location.getLongitude();
            mLatitude = location.getLatitude();
            return true;
        } catch (IOException e) {
            Toast.makeText(getActivity(), getString(R.string.map_error_unable_locate_address), Toast.LENGTH_LONG).show();
        }
        return false;
    }

    public void dismissDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
