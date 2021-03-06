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
    public static final int DATABASE_VERSION = 3;

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

        addTimingsTable(db);
        addDurationsView(db);

        Log.d(TAG, "onCreate: ends");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: starts");
        switch(oldVersion){
            case 1:
                //upgrade logic from version 1
                addTimingsTable(db);
                //fall through to include version 2 upgrade logic

            case 2:
                //upgrade logic from version 2
                addDurationsView(db);
                break;

            default:
                throw new IllegalStateException("onUpgrade() with unknown new Version: " + newVersion);
        }
        Log.d(TAG, "onUpgrade: ends");
    }

    private void addTimingsTable(SQLiteDatabase db){
        String sSQL;
        sSQL = "CREATE TABLE " + TimingsContract.TABLE_NAME + " ("
                + TimingsContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + TimingsContract.Columns.TIMINGS_TASK_ID + " INTEGER NOT NULL, "
                + TimingsContract.Columns.TIMINGS_START_TIME + " INTEGER, "
                + TimingsContract.Columns.TIMINGS_DURATION + " INTEGER);";
        Log.d(TAG, "addTimingsTable: sql command: " + sSQL);
        db.execSQL(sSQL);

        sSQL = " CREATE TRIGGER Remove_Task"
                + " AFTER DELETE ON " + TasksContract.TABLE_NAME
                + " FOR EACH ROW"
                + " BEGIN"
                + " DELETE FROM " + TimingsContract.TABLE_NAME
                + " WHERE " + TimingsContract.Columns.TIMINGS_TASK_ID
                + " = OLD." + TasksContract.Columns._ID + ";"
                + " END;";
        Log.d(TAG, "addTimingsTable: sql command: " + sSQL);
        db.execSQL(sSQL);
    }

    private void addDurationsView(SQLiteDatabase db){
        String sSQL;
        sSQL = "CREATE VIEW " + DurationsContract.VIEW_NAME
                + " AS SELECT " + TimingsContract.TABLE_NAME + "." + TimingsContract.Columns._ID + ", "
                + TasksContract.TABLE_NAME + "." + TasksContract.Columns.TASKS_NAME + ", "
                + TasksContract.TABLE_NAME + "." + TasksContract.Columns.TASKS_DESCRIPTION + ", "
                + TimingsContract.TABLE_NAME + "." + TimingsContract.Columns.TIMINGS_START_TIME + ", "
                + "DATE(" + TimingsContract.TABLE_NAME + "." + TimingsContract.Columns.TIMINGS_START_TIME + ", 'unixepoch') "
                + "AS " + DurationsContract.Columns.DURATIONS_START_DATE + ", "
                + "SUM(" + TimingsContract.TABLE_NAME + "." + TimingsContract.Columns.TIMINGS_DURATION + ") "
                + "AS " + DurationsContract.Columns.DURATIONS_DURATION
                + " FROM " + TasksContract.TABLE_NAME + " JOIN " + TimingsContract.TABLE_NAME
                + " ON " + TasksContract.TABLE_NAME + "." + TasksContract.Columns._ID + " = "
                + TimingsContract.TABLE_NAME + "." + TimingsContract.Columns.TIMINGS_TASK_ID
                + " GROUP BY " + DurationsContract.Columns.DURATIONS_START_DATE + ", " + DurationsContract.Columns.DURATIONS_NAME
                + ";";
        Log.d(TAG, "addDurationsView: sSQL = " + sSQL);
        db.execSQL(sSQL);
     }
}
