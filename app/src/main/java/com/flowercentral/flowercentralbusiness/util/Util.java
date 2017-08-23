package com.flowercentral.flowercentralbusiness.util;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class includes common functionality.
 */
public class Util {
    private static final String TAG = Util.class.getSimpleName();

    /**
     * Check internet connection is available or not
     *
     * @param _context:Context
     * @return boolean
     */
    public static boolean checkInternet(Context _context) {
        boolean isConnected;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        } catch (Exception ex) {
            isConnected = false;
            ex.printStackTrace();
        }
        return isConnected;

    }

//    /**
//     * Get Network connection type
//     *
//     * @param _context : Context
//     * @return ConnectivityType : Int
//     */
//    public static int getNetworkType(Context _context) {
//        int networkType;
//        try {
//            ConnectivityManager connectivityManager = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
//            if (activeNetwork != null) {
//                networkType = activeNetwork.getType();
//            } else {
//                networkType = -1;
//            }
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return -1;
//        }
//        return networkType;
//    }

//    /**
//     * Check whether a particular service is running or not
//     *
//     * @param _context:   Context
//     * @param _className: String
//     */
//
//    public static boolean isServiceRunning(Context _context, String _className) {
//        boolean isServiceRunning = false;
//        try {
//            ActivityManager manager = (ActivityManager) _context.getSystemService(Context.ACTIVITY_SERVICE);
//            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//                if (_className.equals(service.service.getClassName())) {
//                    isServiceRunning = true;
//                }
//            }
//        } catch (Exception ex) {
//            isServiceRunning = false;
//        }
//
//        return isServiceRunning;
//    }

    /**
     * Get IMEI Number of the device
     *
     * @param _context : Context
     * @return _imei : String
     */
    @SuppressLint("HardwareIds")
    public static String getIEMINumber(Context _context) {
        String imeiNumber;
        try {
            TelephonyManager telephonyManger = (TelephonyManager) _context.getSystemService(Context.TELEPHONY_SERVICE);
            imeiNumber = telephonyManger.getDeviceId();

        } catch (Exception ex) {
            imeiNumber = null;
            ex.printStackTrace();
        }
        return imeiNumber;

    }

    /**
     * Get id of the device
     *
     * @param _context : Context
     * @return deviceID : String
     */
    @SuppressLint("HardwareIds")
    public static String getDeviceId(Context _context) {
        String deviceId;
        try {
            deviceId = Secure.getString(_context.getContentResolver(), Secure.ANDROID_ID);
        } catch (Exception ex) {
            deviceId = null;
            ex.printStackTrace();
        }
        return deviceId;
    }

//    /**
//     * Get Device Manufacturer
//     *
//     * @return String
//     */
//    public static String getManufacturer() {
//        // Android VERSION
//        String manufacturer = Build.MANUFACTURER;
//        if (manufacturer == null) {
//            return null;
//        } else {
//            return manufacturer;
//        }
//    }

//    /**
//     * Get Device Model
//     *
//     * @return String
//     */
//    public static String getModelNumber() {
//        String PhoneModel = Build.MODEL;
//        if (PhoneModel == null) {
//            return null;
//        } else {
//            return PhoneModel;
//        }
//    }

//    /**
//     * Get Android version
//     *
//     * @return string
//     */
//    public static String getAndroidVersion() {
//        // Android VERSION
//        String AndroidVersion = Build.VERSION.RELEASE;
//        if (AndroidVersion == null) {
//            return null;
//        } else {
//            return AndroidVersion;
//        }
//    }

//    /**
//     * Get Current date & time
//     *
//     * @param _dateFormat dateformat
//     * @return string
//     */
//    public static String getCurrentDateTimeStamp(String _dateFormat) {
//        DateFormat dateFormat = new SimpleDateFormat(_dateFormat, Locale.getDefault());
//        Calendar calendar = Calendar.getInstance(Locale.getDefault());
//        return dateFormat.format(calendar.getTime());
//    }
//
//    public static long getCurrentDateTimeStamp() {
//        Calendar calendar = Calendar.getInstance(Locale.getDefault());
//        return calendar.getTimeInMillis();
//    }

    public static String formatDate(String _date, String _fromFormat, String _toFormat) {
        String strDate;
        try {
            SimpleDateFormat format1 = new SimpleDateFormat(_fromFormat, Locale.getDefault());
            SimpleDateFormat format2 = new SimpleDateFormat(_toFormat, Locale.getDefault());
            Date date = format1.parse(_date);
            strDate = format2.format(date);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            strDate = _date;
        }
        return strDate;

    }

    /**
     * Check for SD-card is available to write or not.
     *
     * @param requireWriteAccess : write access is required or not
     * @return boolean
     */
    static boolean hasStorage(boolean requireWriteAccess) {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        } else if (!requireWriteAccess
                && Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     */
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

//    public String getQueryString(Map<String, String> _params) {
//        StringBuilder builder = new StringBuilder();
//
//        for (String key : _params.keySet()) {
//            Object value = _params.get(key);
//            if (value != null) {
//                try {
//                    value = URLEncoder.encode(String.valueOf(value), "utf-8");
//                    if (builder.length() > 0)
//                        builder.append("&");
//                    builder.append(key).append("=").append(value);
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return builder.toString();
//    }

    static String streamToString(InputStream stream) throws IOException {
        int n;
        char[] buffer = new char[1024 * 4];
        InputStreamReader reader = new InputStreamReader(stream, "UTF8");
        StringWriter writer = new StringWriter();
        while (-1 != (n = reader.read(buffer))) writer.write(buffer, 0, n);
        return writer.toString();
    }

    /**
     * Shows the progress with title, message.
     *
     * @param _context     context
     * @param _title       title
     * @param _message     message
     * @param _cancellable cancelable or not
     * @return dialog instance
     */
    public static MaterialDialog showProgressDialog(Context _context, String _title, String _message, boolean _cancellable) {

        MaterialDialog progressDialog;

        MaterialDialog.Builder builder = new MaterialDialog.Builder(_context);
        if (_title != null) {
            builder.title(_title);
        }
        if (_message != null) {
            builder.content(_message);
        }
        builder.progress(true, 0);
        builder.cancelable(_cancellable);

        progressDialog = builder.build();

        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.cancel();
            }
        });

        if (!progressDialog.isShowing())
            progressDialog.show();

        return progressDialog;
    }
}
