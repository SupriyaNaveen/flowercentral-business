package com.flowercentral.flowercentralbusiness.setting;


import com.flowercentral.flowercentralbusiness.BuildConfig;

/**
 * Created by Ashish Upadhyay on 4/29/17.
 */

public final class AppConstant {

    public final static String APP_NAME = "Florist";
    public static final String LOCAL_DB_NAME = "florist.db";
    public static final int DB_VERSION = 1;

    public enum APP_MODE {DEVELOPMENT, TEST, PRODUCTION}

    public static final int VERSION_CODE = BuildConfig.VERSION_CODE;
    public static final String VERSION_NAME = BuildConfig.VERSION_NAME;
    public static final String BUILD_MODE = BuildConfig.BUILD_MODE;
    public static final String BUILD_TYPE = BuildConfig.BUILD_TYPE;

    // Log Level
    public static final int LOG_LEVEL_ERR = 1;
    public static final int LOG_LEVEL_DEBUG = 2;
    public static final int LOG_LEVEL_WARN = 3;
    public static final int LOG_LEVEL_INFO = 4;

    // Date Format
    public static final String DISPLAY_DATE_FORMAT = "dd-MMM-yyyy";
    public static final String DB_DATE_FORMAT = "yyyy-MM-dd";
    public static final String SHORT_DATE_FORMAT = "dd-MMM";

    public enum LOGIN_TYPE {FACEBOOK, GOOGLE, INSTAGRAM, CUSTOM}
}
