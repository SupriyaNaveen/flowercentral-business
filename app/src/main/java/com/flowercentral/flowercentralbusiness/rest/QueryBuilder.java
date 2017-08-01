package com.flowercentral.flowercentralbusiness.rest;

import com.flowercentral.flowercentralbusiness.BuildConfig;

/**
 *
 */
public class QueryBuilder {

    public static String getLoginUrl() {
        return BuildConfig.SERVER +
                "login";
    }

    public static String getPendingOrderListUrl() {
        return BuildConfig.SERVER +
                "pendingorders";
    }

    public static String getCompletedOrderListUrl() {
        return BuildConfig.SERVER +
                "deliveredorders";
    }

    public static String getRegisterUrl() {
        return BuildConfig.SERVER +
                "registration";
    }

    public static String getOrderDetailsUrl() {
        return BuildConfig.SERVER +
                "orderdetails";
    }

    public static String getUpdateUrl() {
        return BuildConfig.SERVER +
                "updateprofile";
    }

    public static String getProfileInformationUrl() {
        return BuildConfig.SERVER +
                "getprofile";
    }

    public static String getMarkDeliveredUrl() {
        return BuildConfig.SERVER +
                "markdelivered";
    }

    public static String getChangePasswordUrl() {
        return BuildConfig.SERVER +
                "changepassword";
    }

    public static String getFeedbackListUrl() {
        return BuildConfig.SERVER +
                "feedbacks";
    }

    public static String getAcceptOrderUrl() {
        return BuildConfig.SERVER +
                "acceptorder";
    }

    public static String getShopPictureUrl() {
        return BuildConfig.SERVER +
                "shoppictures";
    }

    public static String getRemovePictureUrl() {
        return BuildConfig.SERVER +
                "removeshoppictures";
    }

    public static String getUploadShopPicturesUrl() {
        return BuildConfig.SERVER +
                "updateshoppics";
    }

    public static String getTodaysSalesDataUrl() {
        return BuildConfig.SERVER +
                "salesdashboard_daily";
    }

    public static String getWeeklySalesDataUrl() {
        return BuildConfig.SERVER +
                "salesdashboard_weekly";
    }

    public static String getMonthlySalesDataUrl() {
        return BuildConfig.SERVER +
                "salesdashboard_monthly";
    }

    public static String getHelpDetailsUrl() {
        return BuildConfig.SERVER +
                "help";
    }

    public static String getPendingOrderByDateUrl() {
        return BuildConfig.SERVER +
                "pendingordersbydate";
    }

    public static String getDeliveredOrderByDateUrl() {
        return BuildConfig.SERVER +
                "deliveredordersbydate";
    }

    public static String getForgotPasswordUrl() {
        return BuildConfig.SERVER +
                "forgotpassword";
    }
}
