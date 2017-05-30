package com.flowercentral.flowercentralbusiness.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.flowercentral.flowercentralbusiness.login.ui.model.Vendor;
import com.flowercentral.flowercentralbusiness.login.ui.model.VendorDetails;

/**
 * Created by Ashish Upadhyay on 4/29/17.
 */

public class UserPreference extends PreferenceActivity {

    private final String TAG = UserPreference.class.getSimpleName();
    private static Context mContext;

    private final static String API_TOKEN = "api_token";

    public static String getApiToken() {
        return readString(mContext, API_TOKEN, null);
    }

    public static void setApiToken(String _token) {
        writeString(mContext, API_TOKEN, _token);
    }

    //    private final static String LOGIN_ACCESS_TOKEN = "login_access_token";
    private final static String VENDOR_LOGIN_STATUS = "vendor_login_status";
    private final static String VENDOR_LOGIN_MESSAGE = "vendor_login_message";
    private final static String VENDOR_SHOP_NAME = "vendor_shop_name";
    private final static String VENDOR_SHOP_ADDRESS = "vendor_shop_address";
    private final static String VENDOR_SHOP_EMAIL = "vendor_shop_email";

    @Override
    public void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
    }

    public static void init(Context _context) {
        mContext = _context;
    }

    public static void registerSharedPreferenceChangedListner(Context mContext, SharedPreferences.OnSharedPreferenceChangeListener _listner) {
        if (_listner != null) {
            PreferenceManager.getDefaultSharedPreferences(mContext)
                    .registerOnSharedPreferenceChangeListener(_listner);
        }
    }

    public static void unregisterSharedPreferenceChangedListner(Context mContext, SharedPreferences.OnSharedPreferenceChangeListener _listner) {
        if (_listner != null) {
            PreferenceManager.getDefaultSharedPreferences(mContext)
                    .unregisterOnSharedPreferenceChangeListener(_listner);
        }
    }

    private static String readString(Context mContext, final String _key, String _defaultValue) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        return pref.getString(_key, _defaultValue);
    }

    private static void writeString(Context mContext, final String _key, final String _value) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(_key, _value);
        editor.commit();
    }

    private static boolean readBoolean(Context mContext, final String _key, final boolean _defaultValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        return settings.getBoolean(_key, _defaultValue);
    }

    private static void writeBoolean(Context mContext, final String _key, final boolean _value) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(_key, _value);
        editor.commit();
    }

    private static float readFloat(Context mContext, final String _key, final Float _defaultValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        return settings.getFloat(_key, _defaultValue);
    }

    private static void writeFloat(Context mContext, final String _key, final Float _value) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(_key, _value);
        editor.commit();
    }

    private static int readInt(Context mContext, final String _key, final int _defaultValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        return settings.getInt(_key, _defaultValue);
    }

    private static void writeInt(Context mContext, final String _key, final int _value) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(_key, _value);
        editor.commit();
    }

    private static long readLong(Context mContext, final String _key, final long _defaultValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        return settings.getLong(_key, _defaultValue);
    }

    private static void writeLong(Context mContext, final String _key, final long _value) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(_key, _value);
        editor.commit();
    }

//    public static String getAccessToken() {
//        return readString(mContext, LOGIN_ACCESS_TOKEN, null);
//    }
//
//    public static void setAccessToken(String _token) {
//        writeString(mContext, LOGIN_ACCESS_TOKEN, _token);
//    }

    public static String getVendorShopName() {
        return readString(mContext, VENDOR_SHOP_NAME, null);
    }

    public static void setVendorShopName(String _token) {
        writeString(mContext, VENDOR_SHOP_NAME, _token);
    }

    public static String getVendorShopEmail() {
        return readString(mContext, VENDOR_SHOP_EMAIL, null);
    }

    public static void setVendorShopEmail(String _token) {
        writeString(mContext, VENDOR_SHOP_EMAIL, _token);
    }

    public static String getVendorShopAddress() {
        return readString(mContext, VENDOR_SHOP_ADDRESS, null);
    }

    public static void setVendorShopAddress(String _token) {
        writeString(mContext, VENDOR_SHOP_ADDRESS, _token);
    }

    public static String getVendorLoginStatus() {
        return VENDOR_LOGIN_STATUS;
    }

    public static void setVendorLoginStatus(String _token) {
        writeString(mContext, VENDOR_LOGIN_STATUS, _token);
    }

    public static String getVendorLoginMessage() {
        return VENDOR_LOGIN_MESSAGE;
    }

    public static void setVendorLoginMessage(String _token) {
        writeString(mContext, VENDOR_LOGIN_MESSAGE, _token);
    }

    public static Vendor getProfileInformation() {
        Vendor vendor = new Vendor();
        vendor.setApiToken(readString(mContext, API_TOKEN, null));
        vendor.setStatus(readString(mContext, VENDOR_LOGIN_STATUS, null));
        vendor.setMessage(readString(mContext, VENDOR_LOGIN_MESSAGE, null));
        VendorDetails vendorDetails = new VendorDetails();
        vendorDetails.setVendorEmail(readString(mContext, VENDOR_SHOP_EMAIL, null));
        vendorDetails.setVendorAddress(readString(mContext, VENDOR_SHOP_ADDRESS, null));
        vendorDetails.setVendorShopName(readString(mContext, VENDOR_SHOP_NAME, null));
        vendor.setVendorDetails(vendorDetails);

        return vendor;
    }

    public static void setProfileInformation(Vendor vendor) {
        setApiToken(vendor.getApiToken());
        setVendorShopName(vendor.getVendorDetails().getVendorShopName());
        setVendorShopAddress(vendor.getVendorDetails().getVendorAddress());
        setVendorShopEmail(vendor.getVendorDetails().getVendorEmail());
        setVendorLoginStatus(vendor.getStatus());
        setVendorLoginMessage(vendor.getMessage());
    }

    public static void deleteProfileInformation() {
        setApiToken(null);
        setVendorShopName(null);
        setVendorShopAddress(null);
        setVendorShopEmail(null);
        setVendorLoginStatus(null);
        setVendorLoginMessage(null);
    }
}
