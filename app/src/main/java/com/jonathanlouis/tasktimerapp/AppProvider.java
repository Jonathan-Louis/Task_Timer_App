package com.jonathanlouis.tasktimerapp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Provider for the Task Timer app.
 *
 * This is the only class that knows about {@link AppDatabase}
 */

public class AppProvider extends ContentProvider {

    //--fields--
    private static final String TAG = "AppProvider";

    private AppDatabase openHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final String CONTENT_AUTHORITY = "com.jonathanlouis.tasktimerapp.provider";
    public static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final int TASKS = 100;
    private static final int TASKS_ID = 101;

    private static final int TIMINGS = 200;
    private static final int TIMINGS_ID = 201;

    //private static final int TIMINGS = 300;
    //private static final int TIMINGS_ID = 301;

    private static final int TASK_DURATION = 400;
    private static final int TASK_DURATION_ID = 401;

    //--constructor--

    //--methods--
    @Override
    public boolean onCreate() {
        openHelper = AppDatabase.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.d(TAG, "query: called with uri " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "query: match is " + match);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (match){
            case TASKS:
                queryBuilder.setTables(TasksContract.TABLE_NAME);
                break;

            case TASKS_ID:
                queryBuilder.setTables(TasksContract.TABLE_NAME);
                long taskID = TasksContract.getTaskID(uri);
                queryBuilder.appendWhere(TasksContract.Columns._ID + " = " + taskID);
                break;

            case TIMINGS:
                queryBuilder.setTables(TimingsContract.TABLE_NAME);
                break;
            case TIMINGS_ID:
                queryBuilder.setTables(TimingsContract.TABLE_NAME);
                long timingID = TimingsContract.getTimingID(uri);
                queryBuilder.appendWhere(TimingsContract.Columns._ID + " = " + timingID);
                break;

//            case TASK_DURATION:
//                queryBuilder.setTables(DurationsContract.TABLE_NAME);
//                break;

//            case TASK_DURATION_ID:
//                queryBuilder.setTables(DurationsContract.TABLE_NAME);
//                long durationID = DurationsContract.getDurationID(uri);
//                queryBuilder.appendWhere(DurationsContract.Columns._ID + " = " + durationID);
//                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        Log.d(TAG, "query: rows in cursor = " + cursor.getCount());

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case TASKS:
                return TasksContract.CONTENT_TYPE;

            case TASKS_ID:
                return TasksContract.CONTENT_ITEM_TYPE;

            case TIMINGS:
                return TimingsContract.CONTENT_TYPE;

            case TIMINGS_ID:
                return TimingsContract.CONTENT_ITEM_TYPE;

//            case TASK_DURATION:
//                return DurationsContract.CONTENT_TYPE;
//
//            case TASK_DURATION_ID:
//                return DurationsContract.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Log.d(TAG, "insert: called with URI: " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "insert: match = " + match);
        final SQLiteDatabase db;

        Uri returnUri;
        long recordID;

        switch (match){
            case TASKS:
                db = openHelper.getWritableDatabase();
                recordID = db.insert(TasksContract.TABLE_NAME, null, values);
                if(recordID >= 0){
                    returnUri = TasksContract.buildTaskUri(recordID);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
                }
                break;

            case TIMINGS:
                db = openHelper.getWritableDatabase();
                recordID = db.insert(TimingsContract.TABLE_NAME, null, values);
                if(recordID >= 0){
                    returnUri = TimingsContract.buildTimingUri(recordID);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
                }
                break;

            default:
                throw new IllegalArgumentException("unknown uri: " + uri);
        }

        if(recordID >= 0){
            Log.d(TAG, "insert: setting notifyChanged with " + uri);
            getContext().getContentResolver().notifyChange(uri, null);
        }

        Log.d(TAG, "insert: returning uri: " + returnUri);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "update: called with uri " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "update: match = " + match);

        final SQLiteDatabase db;
        int count;

        String selectionCriteria;

        switch (match){
            case TASKS:
                db = openHelper.getWritableDatabase();
                count = db.delete(TasksContract.TABLE_NAME, selection, selectionArgs);
                break;

            case TASKS_ID:
                db = openHelper.getWritableDatabase();
                long taskID = TasksContract.getTaskID(uri);
                selectionCriteria = TasksContract.Columns._ID + " = " + taskID;
                if(selection != null && selection.length() > 0){
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.delete(TasksContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;

            case TIMINGS:
                db = openHelper.getWritableDatabase();
                count = db.delete(TimingsContract.TABLE_NAME, selection, selectionArgs);
                break;

            case TIMINGS_ID:
                db = openHelper.getWritableDatabase();
                long timingsID = TimingsContract.getTimingID(uri);
                selectionCriteria = TimingsContract.Columns._ID + " = " + timingsID;
                if(selection != null && selection.length() > 0){
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.delete(TimingsContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }

        if(count > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        Log.d(TAG, "update: exiting and returning: " + count);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "update: called with uri " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "update: match = " + match);

        final SQLiteDatabase db;
        int count;

        String selectionCriteria;

        switch (match){
            case TASKS:
                db = openHelper.getWritableDatabase();
                count = db.update(TasksContract.TABLE_NAME, values, selection, selectionArgs);
                break;

            case TASKS_ID:
                db = openHelper.getWritableDatabase();
                long taskID = TasksContract.getTaskID(uri);
                selectionCriteria = TasksContract.Columns._ID + " = " + taskID;
                if(selection != null && selection.length() > 0){
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.update(TasksContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;

            case TIMINGS:
                db = openHelper.getWritableDatabase();
                count = db.update(TimingsContract.TABLE_NAME, values, selection, selectionArgs);
                break;

            case TIMINGS_ID:
                db = openHelper.getWritableDatabase();
                long timingsID = TimingsContract.getTimingID(uri);
                selectionCriteria = TimingsContract.Columns._ID + " = " + timingsID;
                if(selection != null && selection.length() > 0){
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.update(TimingsContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }

        if(count > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        Log.d(TAG, "update: exiting and returning: " + count);
        return count;
    }

    private static UriMatcher buildUriMatcher(){
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        //eg. content://com.jonathanlouis.tasktimer.provider/tasks
        uriMatcher.addURI(CONTENT_AUTHORITY, TasksContract.TABLE_NAME, TASKS);
        uriMatcher.addURI(CONTENT_AUTHORITY, TasksContract.TABLE_NAME + "/#", TASKS_ID);

        uriMatcher.addURI(CONTENT_AUTHORITY, TimingsContract.TABLE_NAME, TIMINGS);
        uriMatcher.addURI(CONTENT_AUTHORITY, TimingsContract.TABLE_NAME + "/#", TIMINGS_ID);

//        uriMatcher.addURI(CONTENT_AUTHORITY, DurationsContract.TABLE_NAME, TASK_DURATION);
//        uriMatcher.addURI(CONTENT_AUTHORITY, DurationsContract.TALBE_NAME + "/#", TASK_DURATION_ID);

        return uriMatcher;
    }
}
