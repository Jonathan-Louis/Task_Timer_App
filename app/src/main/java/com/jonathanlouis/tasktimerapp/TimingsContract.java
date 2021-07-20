package com.jonathanlouis.tasktimerapp;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.jonathanlouis.tasktimerapp.AppProvider.CONTENT_AUTHORITY;
import static com.jonathanlouis.tasktimerapp.AppProvider.CONTENT_AUTHORITY_URI;

public class TimingsContract {
    
    //--fields--
    static final String TABLE_NAME = "timings";

    /**
     * URI to access the timings table.
     */
    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;

    /**
     * Static inner class to contain timings table columns.
     */
    public static class Columns {
        public static final String _ID = BaseColumns._ID;
        public static final String TIMINGS_TASK_ID = "taskID";
        public static final String TIMINGS_START_TIME = "startTime";
        public static final String TIMINGS_DURATION = "duration";

        private Columns(){
            //prevent instantiation
        }
    }

    //--methods--
    public static long getTimingID(Uri uri){
        return ContentUris.parseId(uri);
    }

    public static Uri buildTimingUri(long timingID){
        return ContentUris.withAppendedId(CONTENT_URI, timingID);
    }
}
