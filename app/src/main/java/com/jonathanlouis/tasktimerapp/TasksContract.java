package com.jonathanlouis.tasktimerapp;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.jonathanlouis.tasktimerapp.AppProvider.CONTENT_AUTHORITY;
import static com.jonathanlouis.tasktimerapp.AppProvider.CONTENT_AUTHORITY_URI;

public class TasksContract {

    //--fields--
    static final String TABLE_NAME = "tasks";

    /**
     * URI to access the tasks table.
     */
    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;

    /**
     * Static inner class to contain tasks table columns.
     */
    public static class Columns {
        public static final String _ID = BaseColumns._ID;
        public static final String TASKS_NAME = "name";
        public static final String TASKS_DESCRIPTION = "description";
        public static final String TASKS_SORTORDER = "sortOrder";
        public static final String TASKS_CATEGORYID = "categoryID";

        private Columns(){
            //prevent instantiation
        }
    }

    //--methods--
    static long getTaskID(Uri uri){
        return ContentUris.parseId(uri);
    }

    static Uri buildTaskUri(long taskID){
        return ContentUris.withAppendedId(CONTENT_URI, taskID);
    }
}
