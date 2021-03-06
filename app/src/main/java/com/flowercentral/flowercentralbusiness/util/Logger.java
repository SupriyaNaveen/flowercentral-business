package com.flowercentral.flowercentralbusiness.util;

import android.os.Environment;
import android.util.Log;

import com.flowercentral.flowercentralbusiness.setting.AppConstant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

/**
 * Logger utility class, helps to log information.
 */
public class Logger {
    private static boolean print_log_to_console = true;
    public static boolean print_log_to_file = false;

    /**
     * Used to log an information to  LogCat.
     *
     * @param className  : Name of the class file
     * @param methodName : Name of the method
     * @param info       : Information to be print in LogCat.
     */
    public static void log(String className, String methodName, String info, int logLevel) {
        if (print_log_to_console) {
            String log = "Method: " + methodName + " | ";
            log = log + "Info: " + info;
            if (print_log_to_file) {
                printLogToFile(log);
            }
            switch (logLevel) {
                case AppConstant.LOG_LEVEL_ERR:
                    Log.e(className, log);
                    break;
                case AppConstant.LOG_LEVEL_DEBUG:
                    Log.d(className, log);
                    break;
                case AppConstant.LOG_LEVEL_WARN:
                    Log.w(className, log);
                    break;
                case AppConstant.LOG_LEVEL_INFO:
                    Log.i(className, log);
                    break;
            }
        }
    }

    /**
     * Used to log an error to SD-card and LogCat.
     *
     * @param className  : Name of the class file
     * @param methodName : Name of the method
     * @param errorMsg   : Error Message
     */
    static void logError(String className, String methodName, String errorMsg) {
        String log = "Class: " + className + " | ";
        log = log + "Method: " + methodName + " | ";
        log = log + "Error: " + errorMsg;
        if (print_log_to_console) {
            Log.e(className, log);
        }
        if (print_log_to_file) {
            printLogToFile(log);
        }
    }

    /**
     * Print log message to file.
     * A file namely log.txt is created in Florist folder of external directory.
     * Message is written to it.
     * Calendar details append to same file.
     *
     * @param message message
     */
    private static void printLogToFile(String message) {
        // write log info to file

        // check for SD-card mounted or not
        if (Util.hasStorage(true)) {

            String folderPath = Environment.getExternalStorageDirectory().getPath() + "/" + AppConstant.APP_NAME + "/";
            String filePath = folderPath + "log.txt";
            File directory = new File(folderPath);
            boolean isDirCreated = directory.mkdirs();
            if (isDirCreated) {
                File logFile = new File(filePath);
                if (!logFile.exists()) {
                    try {
                        boolean isFileCreated = logFile.createNewFile();
                        if (isFileCreated) {
                            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                            buf.append(AppConstant.APP_NAME);
                            buf.newLine();
                            buf.close();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                //fetch system date and time and append it to the error text
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);
                int mSecond = c.get(Calendar.SECOND);
                String text = mDay + "/" + mMonth + "/" + mYear + " - " + mHour + " : " + mMinute + " : " + mSecond + " :: " + message;
                try {
                    //BufferedWriter for performance, true to set append to file flag
                    BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                    buf.append(text);
                    buf.newLine();
                    buf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
