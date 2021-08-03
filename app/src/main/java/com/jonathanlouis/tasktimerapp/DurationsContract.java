package com.jonathanlouis.tasktimerapp;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.jonathanlouis.tasktimerapp.AppProvider.CONTENT_AUTHORITY;
import static com.jonathanlouis.tasktimerapp.AppProvider.CONTENT_AUTHORITY_URI;

public class DurationsContract {

    public static final String VIEW_NAME = "vwTaskDurations";

    /**
     * URI to access the durations view.
     */
    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, VIEW_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + VIEW_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + VIEW_NAME;

    /**
     * Static inner class to contain durations views columns.
     */
    public static class Columns {
        public static final String _ID = BaseColumns._ID;
        public static final String DURATIONS_NAME = TasksContract.Columns.TASKS_NAME;
        public static final String DURATIONS_DESCRIPTION = TasksContract.Columns.TASKS_DESCRIPTION;
        public static final String DURATIONS_START_TIME = TimingsContract.Columns.TIMINGS_START_TIME;
        public static final String DURATIONS_START_DATE = "StartDate";
        public static final String DURATIONS_DURATION = TimingsContract.Columns.TIMINGS_DURATION;

        private Columns(){
            //prevent instantiation
        }
    }

    //--methods--
    public static long getDurationsID(Uri uri){
        return ContentUris.parseId(uri);
    }
}
