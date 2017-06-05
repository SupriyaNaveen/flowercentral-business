package com.flowercentral.flowercentralbusiness.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.flowercentral.flowercentralbusiness.setting.AppConstant;
import com.flowercentral.flowercentralbusiness.util.Logger;
import com.flowercentral.flowercentralbusiness.util.Util;


class LocalDAO {

    private static final String TAG = LocalDAO.class.getSimpleName();
    private DBHelper mDBHelper;
    private SQLiteDatabase mIPostOp;

    public LocalDAO(Context _ctx) {
//        Context mContext = _ctx;
        // Get dbHelper instance
        mDBHelper = DBHelper.getInstance(_ctx, AppConstant.LOCAL_DB_NAME);
        mIPostOp = mDBHelper.getDatabase();
    }

    public boolean addPostOpInstructions(String _data) {
        boolean status;

        try {
            if (_data != null && !_data.isEmpty()) {
                //Delete existing PostOpInstructions data from table
                deletePostOpInstructions();
                ContentValues contentValues = new ContentValues();

                contentValues.put("data", _data);
                contentValues.put("creationDate",
                        Util.getCurrentDateTimeStamp("d-M-y H:m:s"));

                if (mIPostOp == null) {
                    mIPostOp = mDBHelper.getDatabase();
                }

                long rowID = mIPostOp.insert("post_op_instructions", null, contentValues);

                status = rowID != -1;

            } else {
                Logger.log(TAG, "addPostOpInstructions", "No data available to add into local database.", AppConstant.LOG_LEVEL_INFO);
                status = false;
            }

        } catch (SQLException sqlEx) {
            Logger.log(TAG, "addPostOpInstructions", sqlEx.getMessage(), AppConstant.LOG_LEVEL_ERR);
            status = false;
//        } finally {
//            if (mIPostOp != null && mIPostOp.isOpen()) {
//                mIPostOp.close();
//                mIPostOp = null;
//            }
        }
        return status;
    }

    private boolean deletePostOpInstructions() {
        boolean status;
        try {
            String sqlQuery = "DELETE FROM post_op_instructions";
            if (mIPostOp == null) {
                mIPostOp = mDBHelper.getDatabase();
            }
            mIPostOp.execSQL(sqlQuery);
            status = true;
        } catch (SQLException sqlEx) {
            Logger.log(TAG, "deletePostOpInstructions", sqlEx.getMessage(), AppConstant.LOG_LEVEL_ERR);
            status = false;
//        }  finally {
//            if (mIPostOp != null && mIPostOp.isOpen()) {
//                mIPostOp.close();
//                mIPostOp = null;
//            }
        }
        return status;
    }

    public String getPostOpInstruction() {
        String data = null;
        Cursor cursor = null;
        try {
            String sqlQuery = "SELECT id, data, creationDate FROM post_op_instructions";

            if (mIPostOp == null) {
                mIPostOp = mDBHelper.getDatabase();
            }

            cursor = mIPostOp.rawQuery(sqlQuery, null);
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    data = cursor.getString(cursor.getColumnIndex("data"));
                    cursor.moveToNext();
                }
            } else {
                data = null;
            }


        } catch (SQLException sqlEx) {
            Logger.log(TAG, "getPostOpInstruction", sqlEx.getMessage(), AppConstant.LOG_LEVEL_ERR);
            data = null;

        } finally {
            if (cursor != null) {
                cursor.close();
            }
//            if (mIPostOp != null && mIPostOp.isOpen()) {
//                mIPostOp.close();
//                mIPostOp = null;
//            }
        }

        return data;
    }

}