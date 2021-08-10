package com.jonathanlouis.tasktimerapp;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DurationsReport extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, DatePickerDialog.OnDateSetListener, AppDialog.DialogEvents {

    private static final String TAG = "DurationsReport";

    private static final int LOADER_ID = 1;

    private static final String SELECTION_PARAM = "SELECTION";
    private static final String SELECTION_ARGS_PARAM = "SELECTION_ARGS";
    private static final String SORT_ORDER_PARAM = "SORT_ORDER";

    public static final String CURRENT_DATE = "CURRENT_DATE";
    public static final String DISPLAY_WEEK = "DISPLAY_WEEK";

    public static final int DIALOG_FILTER = 1;
    public static final int DIALOG_DELETE = 2;

    private Bundle args = new Bundle();
    private boolean displayWeek = true;

    private DurationsRVAdapter adapter;

    private final GregorianCalendar gcCalender = new GregorianCalendar();

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

        if(savedInstanceState != null){
            long timeInMillis = savedInstanceState.getLong(CURRENT_DATE, 0);
            //time will be 0 when initially called
            if(timeInMillis != 0){
                gcCalender.setTimeInMillis(timeInMillis);
                gcCalender.clear(GregorianCalendar.HOUR_OF_DAY);
                gcCalender.clear(GregorianCalendar.MINUTE);
                gcCalender.clear(GregorianCalendar.SECOND);
            }

            displayWeek = savedInstanceState.getBoolean(DISPLAY_WEEK, true);
        }
        applyFilter();

        RecyclerView recyclerView = findViewById(R.id.td_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //create an empty adapter to use
        if(adapter == null){
            adapter = new DurationsRVAdapter(this, null);
        }
        recyclerView.setAdapter(adapter);

        LoaderManager.getInstance(this).initLoader(LOADER_ID, args, this);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(CURRENT_DATE, gcCalender.getTimeInMillis());
        outState.putBoolean(DISPLAY_WEEK, displayWeek);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_report, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.rm_filter_period:
                displayWeek = !displayWeek;
                applyFilter();
                invalidateOptionsMenu();  //redraws changed menu items
                LoaderManager.getInstance(this).restartLoader(LOADER_ID, args, this);
                return true;

            case R.id.rm_filter_date:
                showDatePickerDialog(getString(R.string.date_title_filter), DIALOG_FILTER);
                return true;

            case R.id.rm_delete:
                showDatePickerDialog(getString(R.string.date_title_delete), DIALOG_DELETE);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.rm_filter_period);

        if(item != null){
            //switch between week and day icon
            if(displayWeek){
                item.setIcon(R.drawable.ic_baseline_filter_1_24);
                item.setTitle(R.string.rm_title_filter_day);
            } else {
                item.setIcon(R.drawable.ic_baseline_filter_7_24);
                item.setTitle(R.string.rm_title_filter_week);
            }
        }

        return super.onPrepareOptionsMenu(menu);
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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Log.d(TAG, "onDateSet: called");
        int dialogID = (int) view.getTag();
        switch (dialogID){
            case DIALOG_FILTER:
                gcCalender.set(year, month, dayOfMonth, 0, 0, 0);
                applyFilter();
                LoaderManager.getInstance(this).restartLoader(LOADER_ID, args, this);
                break;

            case DIALOG_DELETE:
                gcCalender.set(year, month, dayOfMonth, 0, 0, 0);
                String fromDate = DateFormat.getDateFormat(this).format(gcCalender.getTimeInMillis());
                AppDialog dialog = new AppDialog();
                Bundle arguments = new Bundle();
                arguments.putInt(AppDialog.DIALOG_ID, 1);
                arguments.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.delete_timings_message, fromDate));
                dialog.setArguments(arguments);
                dialog.show(getSupportFragmentManager(), null);
                break;

            default:
                throw new IllegalArgumentException("Invalid dialog id for DatePickerDialog: " + dialogID);
        }
    }

    @Override
    public void onPositiveResult(int dialogID, Bundle args) {
        Log.d(TAG, "onPositiveResult: called");
        deleteRecords();
    }

    @Override
    public void onNegativeResult(int dialogID, Bundle args) {
        //Empty -- needed for dialog interface callbacks
    }

    @Override
    public void onDialogCancelled(int dialogID) {
        //Empty -- needed for dialog interface callbacks
    }

    private void applyFilter(){
        Log.d(TAG, "applyFilter: called");

        if(displayWeek){
            Date currentCalenderDate = gcCalender.getTime();
            int dayOfWeek = gcCalender.get(GregorianCalendar.DAY_OF_WEEK);
            int weekStart = gcCalender.getFirstDayOfWeek();

            Log.d(TAG, "applyFilter: first day of week is " + weekStart);
            Log.d(TAG, "applyFilter: day of week is " + dayOfWeek);
            Log.d(TAG, "applyFilter: date is " + gcCalender.getTime());

            //calculate week start and end dates
            gcCalender.set(GregorianCalendar.DAY_OF_WEEK, weekStart);

            String startDate = String.format(Locale.US, "%4d-%02d-%02d",
                    gcCalender.get(GregorianCalendar.YEAR),
                    gcCalender.get(GregorianCalendar.MONTH) + 1,
                    gcCalender.get(GregorianCalendar.DAY_OF_MONTH));

            gcCalender.add(GregorianCalendar.DATE, 6);

            String endDate = String.format(Locale.US, "%4d-%02d-%02d",
                    gcCalender.get(GregorianCalendar.YEAR),
                    gcCalender.get(GregorianCalendar.MONTH) + 1,
                    gcCalender.get(GregorianCalendar.DAY_OF_MONTH));

            String[] selectionArgs = new String[]{startDate, endDate};

            //put gcCalender back to time before changes
            gcCalender.setTime(currentCalenderDate);

            Log.d(TAG, "applyFilter: (7) start date is " + startDate + ", end date is " + endDate);
            args.putString(SELECTION_PARAM, "StartDate BETWEEN ? and ?");
            args.putStringArray(SELECTION_ARGS_PARAM, selectionArgs);

        } else {
            String startDate = String.format(Locale.US, "%4d-%02d-%02d",
                    gcCalender.get(GregorianCalendar.YEAR),
                    gcCalender.get(GregorianCalendar.MONTH) + 1,
                    gcCalender.get(GregorianCalendar.DAY_OF_MONTH));

            String[] selectionArgs = new String[]{startDate};
            Log.d(TAG, "applyFilter: (1) start date = " + startDate);
            args.putString(SELECTION_PARAM, "StartDate = ?");
            args.putStringArray(SELECTION_ARGS_PARAM, selectionArgs);
        }
    }

    private void showDatePickerDialog(String title, int dialogID){
        Log.d(TAG, "showDatePickerDialog: called");
        DialogFragment dialogFragment = new DatePickerFragment();

        Bundle arguments = new Bundle();
        arguments.putInt(DatePickerFragment.DATE_PICKER_ID, dialogID);
        arguments.putString(DatePickerFragment.DATE_PICKER_TITLE, title);
        arguments.putSerializable(DatePickerFragment.DATE_PICKER_DATE, gcCalender.getTime());

        dialogFragment.setArguments(arguments);
        dialogFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void deleteRecords(){
        Log.d(TAG, "deleteRecords: called");

        long longDate = gcCalender.getTimeInMillis() / 1000;

        String[] selectionArgs = new String[]{Long.toString(longDate)};
        String selection = TimingsContract.Columns.TIMINGS_START_TIME + " < ?";

        Log.d(TAG, "deleteRecords: deleting records prior to " + longDate);

        ContentResolver contentResolver = getContentResolver();
        contentResolver.delete(TimingsContract.CONTENT_URI, selection, selectionArgs);

        applyFilter();
        LoaderManager.getInstance(this).restartLoader(LOADER_ID, args, this);
    }
}