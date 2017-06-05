package com.flowercentral.flowercentralbusiness.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.flowercentral.flowercentralbusiness.setting.AppConstant;
import com.flowercentral.flowercentralbusiness.util.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class DBHelper extends SQLiteOpenHelper {
    /**
     * Created by Ashish Upadhyay on 9/12/2015.
     */

    private static final String TAG = DBHelper.class.getSimpleName();
    private static SQLiteDatabase sqliteDb;
    private static DBHelper instance;
    private static final int DATABASE_VERSION = AppConstant.DB_VERSION;
    private static String DB_PATH_PREFIX;

    private static String DB_PATH_SUFFIX = "/databases/";

    /**
     * Constructor
     *
     * @param _context : application context
     * @param _name    : database name
     * @param _factory : Cursor Factory
     * @param _version : DB version
     */
    private DBHelper(Context _context, String _name, SQLiteDatabase.CursorFactory _factory,
                     int _version) {
        super(_context, _name, _factory, _version);
        DB_PATH_PREFIX = _context.getFilesDir().getPath();
        Logger.printLogToFile("Create or Open Database : " + _name);
    }

    /**
     * Initialise method
     *
     * @param _context      : application context
     * @param _databaseName : database name
     */
    private static void initialize(Context _context, String _databaseName) {
        if (instance == null) {
            //Check there is an original copy of the database in the assets directory of the application
            if (!checkDatabase(_context, _databaseName)) {
                // if not then copy database from assets directory
                try {
                    copyDatabase(_context, _databaseName);
                } catch (Exception e) {
                    Logger.printLogToFile("Database does not exists and there is no copy of database in asset directory");
                }
            }
            // Create instance of database
            Logger.printLogToFile("Creating database instance ....");

            instance = new DBHelper(_context, _databaseName, null, DATABASE_VERSION);
            sqliteDb = instance.getWritableDatabase();
            Logger.printLogToFile("Database instance created!");
        }
    }

    /**
     * static method for getting singleton instance
     *
     * @param _context      : application context
     * @param _databaseName : database name
     * @return singleton instance
     */
    public static DBHelper getInstance(Context _context, String _databaseName) {
        initialize(_context, _databaseName);
        return instance;
    }

    /**
     * Method to get database instance
     *
     * @return SQLiteDatabase instance
     */
    SQLiteDatabase getDatabase() {
        return sqliteDb;
    }

    /**
     * Copy database from assets directory to application's data directory
     *
     * @param _context      : application context
     * @param _databaseName : database name
     * @throws IOException exception
     */
    private static void copyDatabase(Context _context, String _databaseName) throws IOException {
        // Open your local database as the input stream
        InputStream myInput = _context.getAssets().open(_databaseName);
        // Path to the just created empty db
        String outFileName = getDatabasePath(_context, _databaseName);
        // Check if the path exists or not, if not then create
        String dataDir = DB_PATH_PREFIX + _context.getPackageName() + DB_PATH_SUFFIX;
        File dbFile = new File(dataDir);
        boolean isDirCreated = false;
        if (!dbFile.exists()) {
            isDirCreated = dbFile.mkdir();
        }

        if(isDirCreated) {
            // Copy the file into data directory
            Logger.printLogToFile("Copying local database into : " + outFileName);

            // Open the empty database as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);

            // transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            // Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
            Logger.printLogToFile("Database (" + _databaseName + ") copied successfully!");
        } else {
            Logger.printLogToFile("Database (" + _databaseName + ") copy failed");
        }
    }

    /**
     * check original copy of the database
     *
     * @param _context      : application context
     * @param _databaseName : database name
     * @return : true ? false
     */
    private static boolean checkDatabase(Context _context, String _databaseName) {
        SQLiteDatabase checkDB;
        boolean result = false;
        try {
            String myPath = getDatabasePath(_context, _databaseName);
            // Log.e(TAG, "Trying to conntect to : " + myPath);
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
            Logger.printLogToFile("Database " + _databaseName + " found!");
            if (checkDB != null) {
                int version = checkDB.getVersion();
                checkDB.close();
                if (version == DATABASE_VERSION) {
                    result = true;
                }
            }
        } catch (SQLiteException e) {
            Logger.printLogToFile("Database " + _databaseName + " does not exists!");
        }
        return result;
    }

    /***
     * Static Method that returns database path in the application's data directory
     *
     * @param _context      : application context
     * @param _databaseName : database name
     * @return : complete path
     */
    private static String getDatabasePath(Context _context, String _databaseName) {
        return DB_PATH_PREFIX + _context.getPackageName() + DB_PATH_SUFFIX
                + _databaseName;
    }

    @Override
    public void onCreate(SQLiteDatabase _db) {
        Log.e(TAG, "OnCreate :: Called ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
        Log.e(TAG, "OnCreate :: Upgrade ");
    }
}
