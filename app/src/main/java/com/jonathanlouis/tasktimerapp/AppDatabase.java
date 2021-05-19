package com.jonathanlouis.tasktimerapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * basic database class for app
 *
 * should only be used by {@link AppProvider}.
 */

class AppDatabase extends SQLiteOpenHelper {

    //--fields--
    private static final String TAG = "AppDatabase";

    public static final String DATABASE_NAME = "TaskTimer.db";
    public static final int DATABASE_VERSION = 1;

    //implement AppDatabase as singleton
    private static AppDatabase instance = null;

    //--constructor--
    private AppDatabase(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "AppDatabase: Constructor");
    }

    //--methods--
    /**
     * Get instance of apps singleton database helper object
     * @param context
     * @return a SQLite database helper object
     */
    static AppDatabase getInstance(Context context){
        if(instance == null){
            Log.d(TAG, "getInstance: creating new instance");
            instance = new AppDatabase(context);
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: starts");
        String sSQL; //variable string for logging
        sSQL = "CREATE TABLE " + TasksContract.TABLE_NAME + " ("
                + TasksContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + TasksContract.Columns.TASKS_NAME + " TEXT NOT NULL, "
                + TasksContract.Columns.TASKS_DESCRIPTION + " TEXT, "
                + TasksContract.Columns.TASKS_SORTORDER + " INTEGER, "
                + TasksContract.Columns.TASKS_CATEGORYID + " INTEGER);";
        Log.d(TAG, "onCreate: sql command: " + sSQL);
        db.execSQL(sSQL);

        Log.d(TAG, "onCreate: ends");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: starts");
        switch(oldVersion){
            case 1:
                //upgrade logic from version 1
                break;
            default:
                throw new IllegalStateException("onUpgrade() with unknown new Version: " + newVersion);
        }
        Log.d(TAG, "onUpgrade: ends");
    }
}