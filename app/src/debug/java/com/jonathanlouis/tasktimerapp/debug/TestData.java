package com.jonathanlouis.tasktimerapp.debug;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.jonathanlouis.tasktimerapp.TasksContract;
import com.jonathanlouis.tasktimerapp.TimingsContract;

import java.util.GregorianCalendar;

/**
 * Generate random data for testing purposes
 */

public class TestData {

    public static void generateTestData(ContentResolver contentResolver){

        final int SECS_IN_DAY = 86400;
        final int LOWER_BOUND_RECORDS = 100;
        final int UPPER_BOUND_RECORDS = 500;
        final int MAX_DURATION = SECS_IN_DAY / 6;

        //get a list of task ids from database
        String[] projection = {TasksContract.Columns._ID};
        Uri uri = TasksContract.CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, projection,null,null, null);

        if((cursor != null) && (cursor.moveToFirst())){
            do{
                long taskID = cursor.getLong(cursor.getColumnIndex(TasksContract.Columns._ID));

                int loopCount = LOWER_BOUND_RECORDS + getRandomInt(UPPER_BOUND_RECORDS - LOWER_BOUND_RECORDS);

                for(int i = 0; i < loopCount; i++) {
                    long randomDate = randomDateTime();

                    //generate random duration between 0 and 4 hours
                    long duration = (long) getRandomInt(MAX_DURATION);

                    //create new TestTiming object
                    TestTiming testTiming = new TestTiming(taskID, randomDate, duration);

                    //save timing record to database
                    saveCurrentTiming(contentResolver, testTiming);
                }
            } while(cursor.moveToNext());
            cursor.close();
        }
    }

    private static int getRandomInt(int max){
        return (int) Math.round(Math.random() * max);
    }

    private static long randomDateTime(){
        //set range of years
        final int startYear = 2017;
        final int endYear = 2021;

        int sec = getRandomInt(59);
        int min = getRandomInt(59);
        int hour = getRandomInt(23);
        int month = getRandomInt(11);
        int year = startYear + getRandomInt(endYear - startYear);

        GregorianCalendar calendar = new GregorianCalendar(year, month, 1);
        int day = 1 + getRandomInt(calendar.getActualMaximum(calendar.DAY_OF_MONTH) - 1);

        calendar.set(year,month,day,hour,min,sec);

        return calendar.getTimeInMillis();
    }

    private static void saveCurrentTiming(ContentResolver contentResolver, TestTiming currentTiming){
        ContentValues values = new ContentValues();
        values.put(TimingsContract.Columns.TIMINGS_TASK_ID, currentTiming.taskID);
        values.put(TimingsContract.Columns.TIMINGS_START_TIME, currentTiming.startTime);
        values.put(TimingsContract.Columns.TIMINGS_DURATION, currentTiming.duration);

        contentResolver.insert(TimingsContract.CONTENT_URI, values);
    }
}
