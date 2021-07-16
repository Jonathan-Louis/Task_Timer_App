package com.jonathanlouis.tasktimerapp;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.security.InvalidParameterException;

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, CursorRecyclerViewAdapter.OnTaskClickListener {

    private static final String TAG = "MainFragment";

    public static final int LOADER_ID = 0;
    private CursorRecyclerViewAdapter adapter;

    public MainFragment() {
        Log.d(TAG, "MainFragment: starts");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: called");
        super.onActivityCreated(savedInstanceState);

        //activities containing this fragment must implement its callbacks
        Activity activity = getActivity();
        if(!(activity instanceof CursorRecyclerViewAdapter.OnTaskClickListener)){
            throw new ClassCastException(activity.getClass().getSimpleName() +
                    " must implement CursorRecyclerViewAdapter.OnTaskClickListener interface");
        }

        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onEditClick(Task task) {
        Log.d(TAG, "onEditClick: called");
        CursorRecyclerViewAdapter.OnTaskClickListener listener = (CursorRecyclerViewAdapter.OnTaskClickListener) getActivity();

        if(listener != null){
            listener.onEditClick(task);
        }
    }

    @Override
    public void onDeleteClick(Task task) {
        Log.d(TAG, "onDeleteClick: called");
        CursorRecyclerViewAdapter.OnTaskClickListener listener = (CursorRecyclerViewAdapter.OnTaskClickListener) getActivity();

        if(listener != null){
            listener.onDeleteClick(task);
        }
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: called");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.task_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if(adapter == null) {
            adapter = new CursorRecyclerViewAdapter(null, this);
        }

        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: called");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(TAG, "onCreateLoader: Starting with id = " + id);

        String[] projection = {TasksContract.Columns._ID, TasksContract.Columns.TASKS_NAME,
                TasksContract.Columns.TASKS_DESCRIPTION, TasksContract.Columns.TASKS_SORTORDER};
        String sortOrder = TasksContract.Columns.TASKS_SORTORDER + "," + TasksContract.Columns.TASKS_NAME + " COLLATE NOCASE";

        switch (id){
            case LOADER_ID:
                return new CursorLoader(getActivity(), TasksContract.CONTENT_URI, projection, null, null, sortOrder);

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