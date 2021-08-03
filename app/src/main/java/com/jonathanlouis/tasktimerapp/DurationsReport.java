package com.jonathanlouis.tasktimerapp;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.security.InvalidParameterException;
import java.util.GregorianCalendar;

public class DurationsReport extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "DurationsReport";

    private static final int LOADER_ID = 1;

    private static final String SELECTION_PARAM = "SELECTION";
    private static final String SELECTION_ARGS_PARAM = "SELECTION_ARGS";
    private static final String SORT_ORDER_PARAM = "SORT_ORDER";

    public static final String CURRENT_DATE = "CURRENT_DATE";
    public static final String DISPLAY_WEEK = "DISPLAY_WEEK";

    private Bundle args = new Bundle();
    private boolean displayWeek = true;

    private DurationsRVAdapter adapter;

    private final GregorianCalendar gcCalander = new GregorianCalendar();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_durations_report);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true); //show up button in action bar
        }

        RecyclerView recyclerView = findViewById(R.id.td_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //create an empty adapter to use
        if(adapter == null){
            adapter = new DurationsRVAdapter(this, null);
        }
        recyclerView.setAdapter(adapter);

        LoaderManager.getInstance(this).initLoader(LOADER_ID, args, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch(id){
            case LOADER_ID:
                String[] projection = {BaseColumns._ID, DurationsContract.Columns.DURATIONS_NAME, DurationsContract.Columns.DURATIONS_DESCRIPTION,
                                        DurationsContract.Columns.DURATIONS_START_TIME, DurationsContract.Columns.DURATIONS_START_DATE,
                                        DurationsContract.Columns.DURATIONS_DURATION};

                String selection = null;
                String[] selectionArgs = null;
                String sortOrder = null;

                if(args != null){
                    selection = args.getString(SELECTION_PARAM);
                    selectionArgs = args.getStringArray(SELECTION_ARGS_PARAM);
                    sortOrder = args.getString(SORT_ORDER_PARAM);
                }

                return new CursorLoader(this, DurationsContract.CONTENT_URI, projection, selection, selectionArgs, sortOrder);

            default:
                throw new InvalidParameterException(TAG + ".onCreateLoader called with invalid id: " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished: called");
        adapter.swapCursor(data);

        int count = adapter.getItemCount();

        Log.d(TAG, "onLoadFinished: count = " + count);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: called");
        adapter.swapCursor(null);
    }
}