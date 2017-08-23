package com.flowercentral.flowercentralbusiness.rest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.login.ui.LauncherActivity;
import com.flowercentral.flowercentralbusiness.preference.UserPreference;
import com.flowercentral.flowercentralbusiness.setting.AppConstant;
import com.flowercentral.flowercentralbusiness.util.Logger;
import com.flowercentral.flowercentralbusiness.volley.AsyncHttpClient;
import com.flowercentral.flowercentralbusiness.volley.CustomJsonArrayObjectRequest;
import com.flowercentral.flowercentralbusiness.volley.CustomJsonObjectRequest;
import com.flowercentral.flowercentralbusiness.volley.ErrorData;
import com.flowercentral.flowercentralbusiness.volley.HttpResponseListener;
import com.flowercentral.flowercentralbusiness.volley.RestUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.flowercentral.flowercentralbusiness.preference.UserPreference.deleteProfileInformation;

/**
 * Base class to execute web api's.
 *
 * @param <T> generic type
 */
public abstract class BaseModel<T> implements Response.ErrorListener, HttpResponseListener<T> {

    public static final int AUTHENTICATION_ERROR = 401;
    public static final int UNAUTHORIZED_ERROR = 403;

    private String TAG = BaseModel.class.getSimpleName();
    private static final int MY_SOCKET_TIMEOUT_MS = 10000;
    public Context mContext;
    private AsyncHttpClient mAsyncHttpClient;

    /**
     * Constructor init the context.
     *
     * @param context context
     */
    public BaseModel(Context context) {
        this.mContext = context;
        mAsyncHttpClient = new AsyncHttpClient(mContext);
    }

    /**
     * Init the lister instance.
     */
    private Response.Listener<T> listener = new Response.Listener<T>() {
        @Override
        public void onResponse(T response) {

        }
    };

    /**
     * If the VolleyError is null then it is Application Error or connection time out error.
     * Or else construct ErrorData based on network response status code, message.
     *
     * @param error VolleyError
     */
    @Override
    public void onErrorResponse(VolleyError error) {
        // write logic here for handling error and preparing custom error;
        ErrorData errorData = new ErrorData();

        if (error != null && error.networkResponse != null) {

            errorData.setErrorCode(error.networkResponse.statusCode);

            byte[] dataU = error.networkResponse.data;
            if (dataU != null) {
                String s = new String(dataU);
                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.isNull("errorCode")) {
                        String code = obj.getString("errorCode");
                        errorData.setErrorCodeOfResponseData(code);
                        errorData.setErrorMessage(obj.getString("errorMessage"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (!TextUtils.isEmpty(error.getMessage())) {
                errorData.setErrorMessage(error.getMessage());
            }

            switch (error.networkResponse.statusCode) {

                case UNAUTHORIZED_ERROR:
                    break;
                case AUTHENTICATION_ERROR:
                    if (!(mContext instanceof LauncherActivity)) {
                        showSessionExpiredDialog();
                    } else {
                        errorData.setErrorMessage("Invalid user name or password.");
                    }

                    break;
                case 408:
                    errorData.setErrorType(ErrorData.ERROR_TYPE.CONNECTION_TIMEOUT);
                    break;
                case 500:
                    errorData.setErrorType(ErrorData.ERROR_TYPE.SERVER_ERROR);
                    break;
                case 400:
                    byte[] data = error.networkResponse.data;
                    String s = new String(data);
                    try {
                        JSONObject obj = new JSONObject(s);
                        if (!obj.isNull("errorCode")) {
                            String code = obj.getString("errorCode");
                            errorData.setErrorCodeOfResponseData(code);
                            errorData.setErrorMessage(obj.getString("errorMessage"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    errorData.setErrorType(ErrorData.ERROR_TYPE.SERVER_ERROR);
                    break;

            }
        } else {
            errorData.setErrorType(ErrorData.ERROR_TYPE.APPLICATION_ERROR);
            if (error != null && error.getMessage() != null) {

                errorData.setErrorMessage(error.getMessage());
                errorData.setNetworkTimems(error.getNetworkTimeMs());
            } else if (error instanceof TimeoutError) {
                errorData.setErrorType(ErrorData.ERROR_TYPE.CONNECTION_TIMEOUT);
                if (mContext != null) {
                    errorData.setErrorMessage(mContext.getString(R.string.msg_connection_time_out));

                } else {
                    errorData.setErrorMessage("Connection time out.");
                }
            } else {
                errorData.setErrorMessage("Error response data is null");
            }
        }
        onError(errorData);
    }

    /**
     * On 401 show session expired dialog.
     * Clear login details from preference.
     * Finish all activities and show login page.
     */
    private void showSessionExpiredDialog() {
        if (mContext != null && mContext instanceof Activity) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Authentication Error");
            builder.setMessage("Session expired. Please login again!");

            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (mContext != null && mContext instanceof Activity) {
                        ((Activity) mContext).finish();
                    }
                    deleteProfileInformation(mContext);
                    Intent intent = new Intent(mContext, LauncherActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mContext.startActivity(intent);
                    mAsyncHttpClient.cancelAllRequests();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setCancelable(false);
            alertDialog.show();
        }
    }

    @Override
    public abstract void onSuccess(int statusCode, Map<String, String> headers, T response);


    @Override
    public abstract void onError(ErrorData error);

    /**
     * GET web api with JsonObject response.
     * Check for network availability.
     * If available, then append common parameters build no, version no so on.
     * Create CustomJsonObjectRequest instance, add header params  like api token.
     * Add retry policy parameters to it (timeout, max retry required, backoffMultiplier)
     * Setup response listener, and add request to request queue.
     *
     * @param url url
     * @param tag tag
     */
    public void executeGetJsonRequest(String url, @Nullable String tag) {
        if (RestUtil.isNetworkAvailable(mContext)) {
            JSONObject params = appendCommonParams(mContext, new JSONObject());
            CustomJsonObjectRequest request = new CustomJsonObjectRequest(Request.Method.GET, url, params, listener, this);
            request.setRetryPolicy(new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            request.setCustomResponseListener(this);
            request.appendHeaderValues(getCommonAuthorizationHeader());
            // addCommonHeaderParams(request);
            mAsyncHttpClient.addToRequestQueue(request, tag);
        } else {
            ErrorData errorData = new ErrorData();
            errorData.setErrorType(ErrorData.ERROR_TYPE.NETWORK_NOT_AVAILABLE);
            errorData.setErrorMessage(mContext.getResources().getString(R.string.msg_internet_unavailable));
            onError(errorData);
        }
    }

    /**
     * POST web api with JsonObject response, JsonObject as request.
     * Check for network availability.
     * If available, then append common parameters build no, version no so on.
     * Create CustomJsonObjectRequest instance, add header params  like api token.
     * Add retry policy parameters to it (timeout, max retry required, backoffMultiplier)
     * Setup response listener, and add request to request queue.
     *
     * @param url url
     * @param tag tag
     */
    public void executePostJsonRequest(String url, JSONObject jsonObjectData, @Nullable String tag) {

        if (RestUtil.isNetworkAvailable(mContext)) {
            JSONObject params = appendCommonParams(mContext, jsonObjectData);
            CustomJsonObjectRequest request = new CustomJsonObjectRequest(Request.Method.POST, url, params, listener, this);
            request.setRetryPolicy(new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            request.setCustomResponseListener(this);
            request.appendHeaderValues(getCommonAuthorizationHeader());
            //addCommonHeaderParams(request);
            mAsyncHttpClient.addToRequestQueue(request, tag);
        } else {
            ErrorData errorData = new ErrorData();
            errorData.setErrorType(ErrorData.ERROR_TYPE.NETWORK_NOT_AVAILABLE);
            errorData.setErrorMessage(mContext.getResources().getString(R.string.msg_internet_unavailable));
            onError(errorData);
        }
    }

//    public void executePostJsonArrayRequest(String url, JSONObject requestObject, String tag) {
//        if (RestUtil.isNetworkAvailable(mContext)) {
//            JSONObject params = appendCommonParams(mContext, requestObject);
//            try {
//                JSONArray jsonArray = new JSONArray();
//                params.toJSONArray(jsonArray);
//                CustomJsonArrayObjectRequest request = new CustomJsonArrayObjectRequest(Request.Method.POST, url, jsonArray, listener, this);
//                request.setRetryPolicy(new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS,
//                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//                request.setCustomResponseListener(this);
//                request.appendHeaderValues(getCommonAuthorizationHeader());
//                //addCommonHeaderParams(request);
//                mAsyncHttpClient.addToRequestQueue(request, tag);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        } else {
//            ErrorData errorData = new ErrorData();
//            errorData.setErrorType(ErrorData.ERROR_TYPE.NETWORK_NOT_AVAILABLE);
//            errorData.setErrorMessage(mContext.getResources().getString(R.string.msg_internet_unavailable));
//            onError(errorData);
//        }
//    }

    /**
     * GET web api with JsonObjectArray response.
     * Check for network availability.
     * If available, then append common parameters build no, version no so on.
     * Create CustomJsonArrayObjectRequest instance, add header params  like api token.
     * Add retry policy parameters to it (timeout, max retry required, backoffMultiplier)
     * Setup response listener, and add request to request queue.
     *
     * @param url url
     * @param tag tag
     */
    public void executeGetJsonArrayRequest(String url, @Nullable String tag) {
        if (RestUtil.isNetworkAvailable(mContext)) {
//            JSONObject params = appendCommonParams(mContext, new JSONObject());
            CustomJsonArrayObjectRequest request = new CustomJsonArrayObjectRequest(Request.Method.GET, url, null, listener, this);
            request.setRetryPolicy(new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            request.setCustomResponseListener(this);
            request.appendHeaderValues(getCommonAuthorizationHeader());
            // addCommonHeaderParams(request);
            mAsyncHttpClient.addToRequestQueue(request, tag);
        } else {
            ErrorData errorData = new ErrorData();
            errorData.setErrorType(ErrorData.ERROR_TYPE.NETWORK_NOT_AVAILABLE);
            errorData.setErrorMessage(mContext.getResources().getString(R.string.msg_internet_unavailable));
            onError(errorData);
        }
    }

//    private void addCommonHeaderParams(Request request) {
//        try {
//            if (request.getHeaders() != null) {
//                request.getHeaders().putAll(getCommonAuthorizationHeader());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
/*
    public void processRequest(Context mContext,Request request)
    {
        addCommonHeaderParams(request);
        mAsyncHttpClient.addToRequestQueue(request);
    }*/



   /* private Map<String, String> getCommonParams(Context mContext)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("platform","android");
        params.put("build_number",String.valueOf(Utils.getVersionCode(mContext)));
        params.put("version_number",Utils.getVersionName(mContext));
        *//*params.put("latitude",);
        params.put("longitude",);*//*
        return params;
    }*/


    /**
     * Append common parameters required for all web api calls.
     *
     * @param context    context
     * @param jsonObject jsonObject
     * @return json object
     */
    private JSONObject appendCommonParams(Context context, JSONObject jsonObject) {
        if (jsonObject == null) {
            jsonObject = new JSONObject();
        }
        try {
            jsonObject.put("platform", "android");
            jsonObject.put("build_number", String.valueOf(getVersionCode(context)));
            jsonObject.put("version_number", getVersionName(context));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * Append header values to web api call.
     * Content-Type : application/json
     * Accept : application/json
     * Authorization Bearer "api_token"
     *
     * @return Map
     */
    private Map<String, String> getCommonAuthorizationHeader() {
        Map<String, String> headerValues = new HashMap<>();
        try {
           /* Logger.logInfo(TAG, "getCommonAuthorizationHeader","Access token " + StorageManager.getApiToken());
            Logger.logInfo(TAG,"getCommonAuthorizationHeader", "Refresh token " + StorageManager.getRefreshToken());
            Logger.logInfo(TAG,"getCommonAuthorizationHeader", "TimeStamp " + StorageManager.getTimestamp());

            if(!TextUtils.isEmpty(StorageManager.getApiToken())) {
                String encodedString = Base64.encodeToString(StorageManager.getApiToken().getBytes(), Base64.NO_WRAP);
                headerValues.put("Authorization", "Basic " + encodedString);
                headerValues.put("Content-Type", "application/json");
                headerValues.put("Accept-Encoding", "gzip");
            }*/
            headerValues.put("Content-Type", "application/json");
            headerValues.put("Accept", "application/json");
            String apiToken = UserPreference.getApiToken(mContext);
            if (apiToken != null && !apiToken.isEmpty()) {
                headerValues.put("Authorization", "Bearer " + apiToken);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.log(TAG, "getCommonAuthorizationHeader", "Header values " + headerValues, AppConstant.LOG_LEVEL_INFO);
        return headerValues;
    }

    /**
     * Get the version name of application
     *
     * @param mContext context
     * @return version name.
     */
    private static String getVersionName(Context mContext) {
        String versionName = null;
        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            if (pInfo != null) {
                versionName = pInfo.versionName;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionName;
    }

    /**
     * Get the version code of the app.
     *
     * @param mContext context
     * @return version code
     */
    private static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            if (pInfo != null) {
                versionCode = pInfo.versionCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionCode;
    }


}
