package com.jonathanlouis.tasktimerapp;

import android.util.Log;

import java.io.Serializable;
import java.util.Date;

/**
 * Simple timing object
 * Sets its start time when created
 * calculates how long since the created time when duration is called
 *
 */

class Timing implements Serializable {

    private static long serialVersionUID = 20210717L;
    private static final String TAG = "Timing";

    private long id;
    private Task task;
    private long startTime;
    private long duration;

    public Timing(Task task) {
        this.task = task;
        //initialize start time to now and duration to 0
        Date currentTime = new Date();
        startTime = currentTime.getTime() / 1000;
        duration = 0L;
    }

    long getId() {
        return id;
    }

    void setId(long id) {
        this.id = id;
    }

    Task getTask() {
        return task;
    }

    void setTask(Task task) {
        this.task = task;
    }

    long getStartTime() {
        return startTime;
    }

    void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    long getDuration() {
        return duration;
    }

    void setDuration() {
        //calculate duration from start time to call time
        Date currentTime = new Date();
        duration = (currentTime.getTime() / 1000) - startTime;
        Log.d(TAG, "setDuration: " + task.getName() + " == start time: " + startTime + " | duration: " + duration);
    }
}
