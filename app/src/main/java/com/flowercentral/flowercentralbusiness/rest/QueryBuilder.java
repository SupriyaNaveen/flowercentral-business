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
                "pending";
    }

    public static String getCompletedOrderListUrl() {
        return BuildConfig.SERVER +
                "delivered";
    }

    public static String getRegisterUrl() {
        return BuildConfig.SERVER +
                "registration";
    }

    public static String getOrderDetailsUrl() {
        return BuildConfig.SERVER +
                "order_details";
    }

    public static String getUpdateUrl() {
        return BuildConfig.SERVER +
                "update_profile";
    }

    public static String getProfileInformationUrl() {
        return BuildConfig.SERVER +
                "get_profile";
    }

    public static String getMarkDeliveredUrl() {
        return BuildConfig.SERVER +
                "mark_delivered";
    }

    public static String getChangePasswordUrl() {
        return BuildConfig.SERVER +
                "change_password";
    }

    public static String getFeedbackListUrl() {
        return BuildConfig.SERVER +
                "feedbacks";
    }

    public static String getAcceptOrderUrl() {
        return BuildConfig.SERVER +
                "accept_order";
    }

    public static String getShopPictureUrl() {
        return BuildConfig.SERVER +
                "shop_picture";
    }

    public static String getRemovePictureUrl() {
        return BuildConfig.SERVER +
                "remove_shop_picture";
    }

    public static String getUploadShopPicturesUrl() {
        return BuildConfig.SERVER +
                "update_new";
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
                "pendingorders_bydate";
    }

    public static String getDeliveredOrderByDateUrl() {
        return BuildConfig.SERVER +
                "deliveredorders_bydate";
    }
}
