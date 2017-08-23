package com.flowercentral.flowercentralbusiness;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.crashlytics.android.Crashlytics;
import com.flowercentral.flowercentralbusiness.util.Logger;

import io.fabric.sdk.android.Fabric;

/**
 * Application class.
 */
public class FlowerCentral extends Application {

    private Application mAppInstance;

    /**
     * Initialise the fabric.
     * Enable strict mode logging for debug mode.
     * Set the logging flag based on build configuration.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
//        Context mContext = this;
        mAppInstance = this;

        // Enable verbose logging and strict mode in debug builds
        if (BuildConfig.DEBUG) {
            Logger.print_log_to_file = true;
            Logger.print_log_to_file = true;

            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build());
        } else {

            Logger.print_log_to_file = false;
            Logger.print_log_to_file = false;
        }
    }

    /**
     * Attach to parents base context.
     *
     * @param base base context
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public Application getInstance() {
        return mAppInstance;
    }

//    public static boolean isAppIsInBackground(Context context) {
//        boolean isInBackground = true;
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
//            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
//            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
//                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//                    for (String activeProcess : processInfo.pkgList) {
//                        if (activeProcess.equals(context.getPackageName())) {
//                            isInBackground = false;
//                        }
//                    }
//                }
//            }
//        } else {
//            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
//            ComponentName componentInfo = taskInfo.get(0).topActivity;
//            if (componentInfo.getPackageName().equals(context.getPackageName())) {
//                isInBackground = false;
//            }
//        }
//        return isInBackground;
//    }

}
