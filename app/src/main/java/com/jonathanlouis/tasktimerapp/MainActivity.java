package com.jonathanlouis.tasktimerapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity implements CursorRecyclerViewAdapter.OnTaskClickListener, AddEditFragment.OnSaveClicked, AppDialog.DialogEvents {

    private static final String TAG = "MainActivity";

    private boolean twoPane = false;
    public static final int DIALOG_DELETE_ID = 1;
    public static final int DIALOG_CANCEL_EDIT_ID = 2;

    private AlertDialog dialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(findViewById(R.id.task_details_container) != null){
            twoPane = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.menumain_addTask:
                taskEditRequest(null);
                break;

            case R.id.menumain_showDurations:
                break;

            case R.id.menumain_settings:
                break;

            case R.id.menumain_showAbout:
                showAboutDialog();
                break;
                
            case R.id.menumain_generate:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEditClick(Task task) {
        taskEditRequest(task);
    }

    @Override
    public void onDeleteClick(Task task) {
        Log.d(TAG, "onDeleteClick: called");
        AppDialog appDialog = new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, DIALOG_DELETE_ID);
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.deletedialog_message, task.getId(), task.getName()));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.deletedialog_positive_caption);
        args.putLong(TasksContract.Columns._ID, task.getId());

        appDialog.setArguments(args);
        appDialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onSaveClicked() {
        Log.d(TAG, "onSaveClicked: called");

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.task_details_container);

        if(fragment != null){
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    @Override
    public void onPositiveResult(int dialogID, Bundle args) {
        Log.d(TAG, "onPositiveResult: called");
        switch (dialogID) {
            case DIALOG_DELETE_ID:
                if(BuildConfig.DEBUG && args.getLong(TasksContract.Columns._ID) == 0){
                    throw new AssertionError("Task ID = 0");
                }
                getContentResolver().delete(TasksContract.buildTaskUri(args.getLong(TasksContract.Columns._ID)), null, null);
                break;
            case DIALOG_CANCEL_EDIT_ID:
                break;
        }
    }

    @Override
    public void onNegativeResult(int dialogID, Bundle args) {
        Log.d(TAG, "onNegativeResult: called");
        switch (dialogID){
            case DIALOG_DELETE_ID:
                break;
            case DIALOG_CANCEL_EDIT_ID:
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentById(R.id.task_details_container);

                if(fragment != null){
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
                break;
        }
    }

    @Override
    public void onDialogCancelled(int dialogID) {
        Log.d(TAG, "onDialogCancelled: called");
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: called");
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddEditFragment fragment = (AddEditFragment) fragmentManager.findFragmentById(R.id.task_details_container);

        if(fragment == null || fragment.canClose()) {
            super.onBackPressed();
        } else {
            //show dialogue to confirm to quit editing
            AppDialog appDialog = new AppDialog();
            Bundle args = new Bundle();
            args.putInt(AppDialog.DIALOG_ID, DIALOG_CANCEL_EDIT_ID);
            args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.cancelEditDialog_message));
            args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.cancelEditDialog_positive_caption);
            args.putInt(AppDialog.DIALOG_NEGATIVE_RID, R.string.cancelEditDialog_negative_caption);

            appDialog.setArguments(args);
            appDialog.show(getSupportFragmentManager(), null);
        }
    }

    @SuppressLint("SetTextI18n")
    public void showAboutDialog(){
        Log.d(TAG, "showAboutDialog: called");
        View messageView = getLayoutInflater().inflate(R.layout.about, null, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setView(messageView);

        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);

        TextView tv = (TextView) messageView.findViewById(R.id.about_version);
        tv.setText("v" + BuildConfig.VERSION_NAME);

        dialog.show();
    }


    private void taskEditRequest(Task task){
        Log.d(TAG, "taskEditRequest: called");
        if(twoPane){
            Log.d(TAG, "taskEditRequest: in two-pane mode(tablet or landscape)");
            AddEditFragment fragment = new AddEditFragment();

            Bundle arguments = new Bundle();
            arguments.putSerializable(Task.class.getSimpleName(), task);
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction().replace(R.id.task_details_container, fragment).commit();
        } else {
            Log.d(TAG, "taskEditRequest: in single pane mode");
            Intent detailIntent = new Intent(this, AddEditActivity.class);
            if (task != null){
                detailIntent.putExtra(Task.class.getSimpleName(), task);
            }
            startActivity(detailIntent);
        }
    }
}