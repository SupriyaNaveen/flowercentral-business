package com.flowercentral.flowercentralbusiness.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.flowercentral.flowercentralbusiness.login.ui.model.Vendor;

/**
 * Vendor login details manager here.
 */
public class UserPreference extends PreferenceActivity {

    private final static String API_TOKEN = "api_token";

    public static String getApiToken(Context context) {
        return readString(context, API_TOKEN, null);
    }

    public static void setApiToken(Context context, String _token) {
        writeString(context, API_TOKEN, _token);
    }

    private final static String VENDOR_LOGIN_STATUS = "vendor_login_status";
    private final static String VENDOR_LOGIN_MESSAGE = "vendor_login_message";
    private final static String VENDOR_SHOP_NAME = "vendor_shop_name";
    private final static String VENDOR_SHOP_ADDRESS = "vendor_shop_address";
    private final static String VENDOR_SHOP_EMAIL = "vendor_shop_email";

    @Override
    public void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
    }

//    public static void registerSharedPreferenceChangedListner(Context context, SharedPreferences.OnSharedPreferenceChangeListener _listner) {
//        if (_listner != null) {
//            PreferenceManager.getDefaultSharedPreferences(context)
//                    .registerOnSharedPreferenceChangeListener(_listner);
//        }
//    }
//
//    public static void unregisterSharedPreferenceChangedListner(Context context, SharedPreferences.OnSharedPreferenceChangeListener _listner) {
//        if (_listner != null) {
//            PreferenceManager.getDefaultSharedPreferences(context)
//                    .unregisterOnSharedPreferenceChangeListener(_listner);
//        }
//    }

    private static String readString(Context context, final String _key, String _defaultValue) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(_key, _defaultValue);
    }

    private static void writeString(Context context, final String _key, final String _value) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(_key, _value);
        editor.apply();
    }

//    private static boolean readBoolean(Context context, final String _key, final boolean _defaultValue) {
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
//        return settings.getBoolean(_key, _defaultValue);
//    }
//
//    private static void writeBoolean(Context context, final String _key, final boolean _value) {
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putBoolean(_key, _value);
//        editor.apply();
//    }
//
//    private static float readFloat(Context context, final String _key, final Float _defaultValue) {
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
//        return settings.getFloat(_key, _defaultValue);
//    }
//
//    private static void writeFloat(Context context, final String _key, final Float _value) {
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putFloat(_key, _value);
//        editor.apply();
//    }
//
//    private static int readInt(Context context, final String _key, final int _defaultValue) {
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
//        return settings.getInt(_key, _defaultValue);
//    }
//
//    private static void writeInt(Context context, final String _key, final int _value) {
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putInt(_key, _value);
//        editor.apply();
//    }
//
//    private static long readLong(Context context, final String _key, final long _defaultValue) {
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
//        return settings.getLong(_key, _defaultValue);
//    }
//
//    private static void writeLong(Context context, final String _key, final long _value) {
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putLong(_key, _value);
//        editor.apply();
//    }

    public static void setVendorShopName(Context context, String _token) {
        writeString(context, VENDOR_SHOP_NAME, _token);
    }

    public static void setVendorShopEmail(Context context, String _token) {
        writeString(context, VENDOR_SHOP_EMAIL, _token);
    }

    public static void setVendorShopAddress(Context context, String _token) {
        writeString(context, VENDOR_SHOP_ADDRESS, _token);
    }

    public static void setVendorLoginStatus(Context context, String _token) {
        writeString(context, VENDOR_LOGIN_STATUS, _token);
    }

    public static void setVendorLoginMessage(Context context, String _token) {
        writeString(context, VENDOR_LOGIN_MESSAGE, _token);
    }

//    public static Vendor getProfileInformation(Context context) {
//        Vendor vendor = new Vendor();
//        vendor.setApiToken(readString(context, API_TOKEN, null));
//        vendor.setStatus(readString(context, VENDOR_LOGIN_STATUS, null));
//        vendor.setMessage(readString(context, VENDOR_LOGIN_MESSAGE, null));
//        VendorDetails vendorDetails = new VendorDetails();
//        vendorDetails.setVendorEmail(readString(context, VENDOR_SHOP_EMAIL, null));
//        vendorDetails.setVendorAddress(readString(context, VENDOR_SHOP_ADDRESS, null));
//        vendorDetails.setVendorShopName(readString(context, VENDOR_SHOP_NAME, null));
//        vendor.setVendorDetails(vendorDetails);
//
//        return vendor;
//    }

    /**
     * Write profile information to preference.
     *
     * @param context context
     * @param vendor profile data
     */
    public static void setProfileInformation(Context context, Vendor vendor) {
        setApiToken(context, vendor.getApiToken());
        setVendorShopName(context, vendor.getVendorDetails().getVendorShopName());
        setVendorShopAddress(context, vendor.getVendorDetails().getVendorAddress());
        setVendorShopEmail(context, vendor.getVendorDetails().getVendorEmail());
        setVendorLoginStatus(context, vendor.getStatus());
        setVendorLoginMessage(context, vendor.getMessage());
    }

    /**
     * Delete profile information from preference.
     *
     * @param context context
     */
    public static void deleteProfileInformation(Context context) {
        setApiToken(context, null);
        setVendorShopName(context, null);
        setVendorShopAddress(context, null);
        setVendorShopEmail(context, null);
        setVendorLoginStatus(context, null);
        setVendorLoginMessage(context, null);
    }
}
