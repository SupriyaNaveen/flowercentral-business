package com.flowercentral.flowercentralbusiness.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.flowercentral.flowercentralbusiness.setting.AppConstant;


class LocalDAO {

    private static final String TAG = LocalDAO.class.getSimpleName();
    private final Context mContext;
    private DBHelper mDBHelper;
    private SQLiteDatabase mIPostOp;

    public LocalDAO(Context _ctx) {
        mContext = _ctx;
        // Get dbHelper instance
        mDBHelper = DBHelper.getInstance(_ctx, AppConstant.LOCAL_DB_NAME);
        mIPostOp = mDBHelper.getDatabase();
    }
}