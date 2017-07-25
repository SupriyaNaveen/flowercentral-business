package com.flowercentral.flowercentralbusiness.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.andexert.library.RippleView;
import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.databinding.FragmentUpdateProfileBinding;
import com.flowercentral.flowercentralbusiness.databinding.LayoutRegisterBinding;
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

public class UpdateProfile extends Fragment implements RippleView.OnRippleCompleteListener {

    private static final String TAG = UpdateProfile.class.getSimpleName();
    private static final int TYPE_MAP = 100;
    private double mLongitude;
    private double mLatitude;
    private MaterialDialog mProgressDialog;
    private FragmentUpdateProfileBinding mBinder;
    private LayoutRegisterBinding ltRegBinding;

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
        mBinder = DataBindingUtil.inflate(inflater, R.layout.fragment_update_profile, container, false);
        ltRegBinding = mBinder.register;
        initializeActivity(getActivity());

        mBinder.register.textViewLocate.setOnRippleCompleteListener(this);
        mBinder.btnCancel.setOnRippleCompleteListener(this);
        mBinder.btnUpdate.setOnRippleCompleteListener(this);

        return mBinder.getRoot();
    }

    private void initializeActivity(Context _context) {
        //Check internet connectivity
        if (Util.checkInternet(_context)) {
            mBinder.flNoInternet.setVisibility(View.GONE);
            mBinder.registerWrapper.setVisibility(View.VISIBLE);

            mBinder.register.textviewEmail.setEnabled(false);
            mBinder.register.textviewPassword.setVisibility(View.GONE);
            getProfileDetails();
        } else {
            mBinder.flNoInternet.setVisibility(View.VISIBLE);
            mBinder.registerWrapper.setVisibility(View.GONE);
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
                            Snackbar.make(mBinder.outerWrapper, getResources().getString(R.string.msg_internet_unavailable), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INTERNAL_SERVER_ERROR:
                            Snackbar.make(mBinder.outerWrapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case CONNECTION_TIMEOUT:
                            Snackbar.make(mBinder.outerWrapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case APPLICATION_ERROR:
                            Snackbar.make(mBinder.outerWrapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INVALID_INPUT_SUPPLIED:
                            Snackbar.make(mBinder.outerWrapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case AUTHENTICATION_ERROR:
                            Snackbar.make(mBinder.outerWrapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case UNAUTHORIZED_ERROR:
                            Snackbar.make(mBinder.outerWrapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        default:
                            Snackbar.make(mBinder.outerWrapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
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
        mLatitude = profileDetail.getLatitude();
        mLongitude = profileDetail.getLongitude();
        mBinder.register.setProfile(profileDetail);
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
                            Snackbar.make(mBinder.outerWrapper, getResources().getString(R.string.msg_internet_unavailable), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INTERNAL_SERVER_ERROR:
                            Snackbar.make(mBinder.outerWrapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case CONNECTION_TIMEOUT:
                            Snackbar.make(mBinder.outerWrapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case APPLICATION_ERROR:
                            Snackbar.make(mBinder.outerWrapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INVALID_INPUT_SUPPLIED:
                            Snackbar.make(mBinder.outerWrapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case AUTHENTICATION_ERROR:
                            Snackbar.make(mBinder.outerWrapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case UNAUTHORIZED_ERROR:
                            Snackbar.make(mBinder.outerWrapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        default:
                            Snackbar.make(mBinder.outerWrapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        };

        String url = QueryBuilder.getUpdateUrl();
        if (_user != null) {
            baseModel.executePostJsonRequest(url, _user, TAG);
        } else {
            Snackbar.make(mBinder.outerWrapper, getResources().getString(R.string.msg_reg_user_missing_input), Snackbar.LENGTH_SHORT).show();
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
                Snackbar.make(mBinder.outerWrapper, "Update profile failed", Snackbar.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Snackbar.make(mBinder.outerWrapper, getResources().getString(R.string.msg_reg_user_missing_input), Snackbar.LENGTH_SHORT).show();
        }
    }

    private boolean isValidInput() {
        boolean isValid = true;
        if (ltRegBinding.textviewVendorName.getText().toString().isEmpty()) {
            ltRegBinding.textviewVendorName.setError(getString(R.string.fld_error_vendor_name));
            isValid = false;
        } else {
            ltRegBinding.textviewVendorName.setError(null);
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
        } catch (IOException | IndexOutOfBoundsException e) {
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

    @Override
    public void onComplete(RippleView v) {
        switch (v.getId()) {
            case R.id.text_view_locate:
                locateAddressSelected();
                break;

            case R.id.btn_cancel:
                onClickCancel();
                break;

            case R.id.btn_update:
                onClickUpdate();
                break;
        }
    }

    public void onClickCancel() {

    }

    public void onClickUpdate() {
        boolean isValidInput = isValidInput();
        if (isValidInput) {
            try {
                JSONObject updateJson = new JSONObject();
                if (ltRegBinding.textviewAddress.getText().length() > 0) {
                    updateJson.put(getString(R.string.api_key_shop_name), ltRegBinding.textviewVendorName.getText());
                }
                if (ltRegBinding.textviewAddress.getText().length() > 0) {
                    updateJson.put(getString(R.string.api_key_address), ltRegBinding.textviewAddress.getText());
                    if (mLatitude == 0 || mLongitude == 0) {
                        isLocated(ltRegBinding.textviewAddress.getText().toString());
                    }
                    updateJson.put(getString(R.string.api_key_latitude), mLatitude);
                    updateJson.put(getString(R.string.api_key_longitude), mLongitude);
                }

                if (ltRegBinding.textviewCity.getText().length() > 0) {
                    updateJson.put(getString(R.string.api_key_city), ltRegBinding.textviewCity.getText());
                }
                if (ltRegBinding.textviewState.getText().length() > 0) {
                    updateJson.put(getString(R.string.api_key_state), ltRegBinding.textviewState.getText());
                }
                updateJson.put(getString(R.string.api_key_country), "India");
                if (ltRegBinding.textviewZip.getText().length() > 0) {
                    updateJson.put(getString(R.string.api_key_pin), ltRegBinding.textviewZip.getText());
                }

                if (ltRegBinding.textviewPhone1.getText().length() > 0) {
                    updateJson.put(getString(R.string.api_key_phone1), ltRegBinding.textviewPhone1.getText());
                }
                if (ltRegBinding.textviewPhone2.getText().length() > 0) {
                    updateJson.put(getString(R.string.api_key_phone2), ltRegBinding.textviewPhone2.getText());
                }
                if (ltRegBinding.textviewTin.getText().length() > 0) {
                    updateJson.put(getString(R.string.api_key_tin_num), ltRegBinding.textviewTin.getText());
                }
                mProgressDialog = Util.showProgressDialog(getActivity(), getString(R.string.msg_registering_user), null, false);
                updateUser(getActivity(), updateJson);
            } catch (JSONException e) {

                Snackbar.make(mBinder.outerWrapper, getResources().getString(R.string.msg_reg_user_missing_input), Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    public void locateAddressSelected() {
        if (ltRegBinding.textviewAddress.getText().length() > 0) {
            if (isLocated(ltRegBinding.textviewAddress.getText().toString())) {
                Intent mapIntent = new Intent(getActivity(), MapActivity.class);
                mapIntent.putExtra(getString(R.string.key_latitude), mLatitude);
                mapIntent.putExtra(getString(R.string.key_longitude), mLongitude);
                mapIntent.putExtra(getString(R.string.key_address), ltRegBinding.textviewAddress.getText());
                mapIntent.putExtra(getString(R.string.key_is_draggable), true);
                startActivityForResult(mapIntent, TYPE_MAP);
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.fld_error_address_for_map), Toast.LENGTH_LONG).show();
        }
    }
}
