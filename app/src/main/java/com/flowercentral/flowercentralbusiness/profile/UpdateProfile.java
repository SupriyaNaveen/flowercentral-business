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

import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.preference.UserPreference;
import com.flowercentral.flowercentralbusiness.rest.BaseModel;
import com.flowercentral.flowercentralbusiness.rest.QueryBuilder;
import com.flowercentral.flowercentralbusiness.util.MapActivity;
import com.flowercentral.flowercentralbusiness.util.Util;
import com.flowercentral.flowercentralbusiness.volley.ErrorData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 26-05-2017.
 */

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
        try {
            JSONObject requestObject = new JSONObject();
            requestObject.put("profile_id", UserPreference.getApiToken());
            if (requestObject != null) {
                baseModel.executePostJsonRequest(url, requestObject, TAG);
            } else {
                Snackbar.make(mFrameLayoutRoot, getResources().getString(R.string.msg_reg_user_missing_input), Snackbar.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {

        }
    }

    private void displayProfileInformation(JSONObject response) {
        //TODO display the profile information.
    }

    @OnClick(R.id.btn_cancel)
    void cancelSelected() {
        //TODO
    }

    @OnClick(R.id.btn_update)
    void updateSelected() {

        //TODO Put the id of the profile which is to be edited.
        boolean isValidInput = isValidInput();
        if (isValidInput) {
            try {
                JSONObject updateJson = new JSONObject();
                if (mEditTextShopName.getText().length() > 0) {
                    updateJson.put("shop_name", mEditTextShopName.getText());
                }
                if (mEditTextAddress.getText().length() > 0) {
                    updateJson.put("add1", mEditTextAddress.getText());
                    if (mLatitude == 0 || mLongitude == 0) {
                        isLocated(mEditTextAddress.getText().toString());
                    }
                    updateJson.put("latitude", mLatitude);
                    updateJson.put("longitude", mLongitude);
                }

                if (mEditTextCity.getText().length() > 0) {
                    updateJson.put("city", mEditTextCity.getText());
                }
                if (mEditTextState.getText().length() > 0) {
                    updateJson.put("state", mEditTextState.getText());
                }
                if (mEditTextZip.getText().length() > 0) {
                    updateJson.put("pin", mEditTextZip.getText());
                }

                if (mEditTextPhone1.getText().length() > 0) {
                    updateJson.put("phone1", mEditTextPhone1.getText());
                }
                if (mEditTextPhone2.getText().length() > 0) {
                    updateJson.put("phone2", mEditTextPhone2.getText());
                }
                if (mEditTextTIN.getText().length() > 0) {
                    updateJson.put("tin", mEditTextTIN.getText());
                }
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
                showUpdateSuccessMessage(response);
            }

            @Override
            public void onError(ErrorData error) {
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
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setMessage(response.getString("message"));
            alertDialogBuilder.setPositiveButton("Okay",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //TODO
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
        if (mEditTextShopName.getText().toString().isEmpty()) {
            mEditTextShopName.setError("Please enter vendor name.");
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
            Toast.makeText(getActivity(), "Please enter address to locate on map.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isLocated(String strAddress) {

        Geocoder coder = new Geocoder(getActivity());
        List<Address> address;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                Toast.makeText(getActivity(), "Unable to locate the address.", Toast.LENGTH_LONG).show();
                return false;
            }

            Address location = address.get(0);
            mLongitude = location.getLongitude();
            mLatitude = location.getLatitude();
            return true;
        } catch (IOException e) {
            Toast.makeText(getActivity(), "Unable to locate the address.", Toast.LENGTH_LONG).show();
        }
        return false;
    }
}
