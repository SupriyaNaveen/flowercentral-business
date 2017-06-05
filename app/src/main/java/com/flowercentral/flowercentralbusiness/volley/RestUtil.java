package com.flowercentral.flowercentralbusiness.volley;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.flowercentral.flowercentralbusiness.setting.AppConstant;
import com.flowercentral.flowercentralbusiness.util.Logger;

public class RestUtil {

    private static String TAG = RestUtil.class.getSimpleName();

    public static boolean isNetworkAvailable(Context context) {
        boolean isNetworkAvailable = false;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            Logger.log(TAG, "isNetworkAvailable", "network type" + activeInfo.getTypeName(), AppConstant.LOG_LEVEL_INFO);
            isNetworkAvailable = true;
        }

        return isNetworkAvailable;
    }
}
