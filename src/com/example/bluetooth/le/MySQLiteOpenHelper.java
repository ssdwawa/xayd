package com.example.bluetooth.le;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    public MySQLiteOpenHelper(Context context, int version) {
        /**
         * context 上下文 
         * name 数据库名称
         * version 数据库版本
         * */
        super(context, "BTRssi.db", null, version);
    }

    /**
     * 数据库文件创建成功后调用，创建表
     * */

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("creatdb", "onCreate: ");
        db.execSQL("create table btrssi(_id INTEGER PRIMARY KEY AUTOINCREMENT,device TEXT,rssi INTEGER)");
    }

    /**
     * 数据库文件需要更新时调用
     * */

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("creatdb", "onUpgrade: ");

    }
}