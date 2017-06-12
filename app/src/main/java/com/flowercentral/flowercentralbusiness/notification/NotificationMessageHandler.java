package com.flowercentral.flowercentralbusiness.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.login.ui.LauncherActivity;
import com.flowercentral.flowercentralbusiness.preference.UserPreference;

import java.util.Map;
import java.util.Set;

/**
 */

public class NotificationMessageHandler {

    // Will queue the notification, or else replace the notification.
    private static int mNotificationId = 0;

    //    private static final String TAG = NotificationMessageHandler.class.getSimpleName();
    private static NotificationMessageHandler notificationMessageHandler;

    public static NotificationMessageHandler getInstance() {
        if (null == notificationMessageHandler) {
            notificationMessageHandler = new NotificationMessageHandler();
        }
        return notificationMessageHandler;
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     * When app is in foreground.
     *
     * @param context          context
     * @param messageTitle     title
     * @param messageBody      body
     * @param notificationData data
     */
    void handleNotificationData(Context context, String messageTitle, String messageBody, Map<String, String> notificationData) {

        if (notificationData.containsKey("NotificationType")) {
            String valueStr = notificationData.get("NotificationType");
            switch (valueStr) {
                case "Pending":
                    parseNotificationData(context, messageTitle, messageBody, notificationData.entrySet());
                    break;
            }
        }
    }

    private void parseNotificationData(Context context, String messageTitle, String messageBody, Set<Map.Entry<String, String>> entries) {

        Intent intent;
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(),
                R.mipmap.flower_central_vendor_logo);

        int orderId = 0;
        for (Map.Entry<String, String> entry : entries) {
            if (entry.getKey().compareTo(context.getString(R.string.key_order_id)) == 0) {
                try {
                    orderId = Integer.parseInt(entry.getValue());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        if (isUserAlreadyLoggedIn(context)) {
            intent = new Intent(context, NotificationOverlay.class);
            intent.putExtra(context.getString(R.string.key_order_id), orderId);
        } else {
            intent = new Intent(context, LauncherActivity.class);
            intent.putExtra(context.getString(R.string.key_order_id), orderId);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_store_white)
                .setLargeIcon(largeIcon)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(mNotificationId++, notificationBuilder.build());
    }

    /**
     * When app running in background.
     *
     * @param context context
     * @param bundle  bundle
     */
    public void handleNotificationData(Context context, Bundle bundle) {

        if (bundle.containsKey("NotificationType")) {
            String valueString = bundle.getString("NotificationType");
            if (valueString != null) {
                switch (valueString) {
                    case "Pending":
                        parseNotificationData(context, bundle);
                        break;
                }
            }
        }
    }

    private void parseNotificationData(Context context, Bundle bundle) {

        Intent intent;
        int orderId = 0;
        if (null != bundle) {
            try {
                orderId = bundle.getInt(context.getString(R.string.key_order_id));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        if (isUserAlreadyLoggedIn(context)) {
            intent = new Intent(context, NotificationOverlay.class);
            intent.putExtra(context.getString(R.string.key_order_id), orderId);
        } else {
            intent = new Intent(context, LauncherActivity.class);
            intent.putExtra(context.getString(R.string.key_order_id), orderId);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    private boolean isUserAlreadyLoggedIn(Context context) {
        Boolean isUserAlreadyLoggedIn = false;
        if (UserPreference.getApiToken(context) != null) {
            isUserAlreadyLoggedIn = true;
        }
        return isUserAlreadyLoggedIn;
    }

}
