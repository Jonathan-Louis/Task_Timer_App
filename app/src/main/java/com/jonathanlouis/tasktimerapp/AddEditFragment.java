package com.jonathanlouis.tasktimerapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class AddEditFragment extends Fragment {

    private static final String TAG = "AddEditFragment";

    public enum FragmentEditMode {EDIT, ADD}
    private FragmentEditMode mode;

    private EditText nameTextView;
    private EditText descriptionTextView;
    private EditText sortOrderTextView;
    private Button saveButton;
    private OnSaveClicked saveListener = null;

    interface OnSaveClicked{
        void onSaveClicked();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "onAttach: called");
        super.onAttach(context);

        //activities using this fragment must implement its callback
        Activity activity = getActivity();
        if(!(activity instanceof OnSaveClicked)){
            throw new ClassCastException(activity.getClass().getSimpleName() +
                    " must implement AddEditFragment.OnSaveClicked interface");
        }

        saveListener = (OnSaveClicked) activity;
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: called");
        super.onDetach();

        saveListener = null;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        Log.d(TAG, "AddEditFragment onCreateView: called");

        View view = inflater.inflate(R.layout.fragment_add_edit, container, false);

        nameTextView = view.findViewById(R.id.addedit_name);
        descriptionTextView = view.findViewById(R.id.addedit_description);
        sortOrderTextView = view.findViewById(R.id.addedit_sortorder);
        saveButton = view.findViewById(R.id.addedit_save);

        Bundle arguments = getArguments();

        final Task task;
        if(arguments != null){
            Log.d(TAG, "onCreateView: retrieving task details");

            task = (Task) arguments.getSerializable(Task.class.getSimpleName());

            if(task != null){
                Log.d(TAG, "onCreateView: Task details found, editing...");
                nameTextView.setText(task.getName());
                descriptionTextView.setText(task.getDescription());
                sortOrderTextView.setText(Integer.toString(task.getSortOrder()));
                mode = FragmentEditMode.EDIT;
            } else {
                mode = FragmentEditMode.ADD;
            }
        } else {
            task = null;
            Log.d(TAG, "onCreateView: no arguments, adding new record");
            mode = FragmentEditMode.ADD;
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update database if at least one field has changed
                int so;
                if(sortOrderTextView.length() > 0){
                    so = Integer.parseInt(sortOrderTextView.getText().toString());
                } else {
                    so = 0;
                }

                ContentResolver contentResolver = getActivity().getContentResolver();
                ContentValues contentValues = new ContentValues();

                switch (mode){
                    case EDIT:
                        if(!nameTextView.getText().toString().equals(task.getName())){
                            contentValues.put(TasksContract.Columns.TASKS_NAME, nameTextView.getText().toString());
                        }
                        if(!descriptionTextView.getText().toString().equals(task.getDescription())){
                            contentValues.put(TasksContract.Columns.TASKS_DESCRIPTION, descriptionTextView.getText().toString());
                        }
                        if(so != task.getSortOrder()){
                            contentValues.put(TasksContract.Columns.TASKS_SORTORDER, so);
                        }

                        if(contentValues.size() != 0){
                            Log.d(TAG, "onClick: updating task");
                            contentResolver.update(TasksContract.buildTaskUri(task.getId()), contentValues, null, null);
                        }
                        break;

                    case ADD:
                        if(nameTextView.length() > 0){
                            Log.d(TAG, "onClick: adding new task");

                            contentValues.put(TasksContract.Columns.TASKS_NAME, nameTextView.getText().toString());
                            contentValues.put(TasksContract.Columns.TASKS_DESCRIPTION, descriptionTextView.getText().toString());
                            contentValues.put(TasksContract.Columns.TASKS_SORTORDER, so);

                            contentResolver.insert(TasksContract.CONTENT_URI, contentValues);
                        }
                        break;
                }
                Log.d(TAG, "onClick: done editing");

                if(saveListener != null){
                    saveListener.onSaveClicked();
                }
            }
        });

        Log.d(TAG, "onCreateView: ended");

        // Inflate the layout for this fragment
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Log.d(TAG, "AddEditFragment onViewCreated: called");
        super.onViewCreated(view, savedInstanceState);
    }

    public boolean canClose(){

        return false;
    }
}