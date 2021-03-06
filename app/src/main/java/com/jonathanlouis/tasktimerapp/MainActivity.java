package com.jonathanlouis.tasktimerapp;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.jonathanlouis.tasktimerapp.debug.TestData;

public class MainActivity extends AppCompatActivity implements CursorRecyclerViewAdapter.OnTaskClickListener, AddEditFragment.OnSaveClicked, AppDialog.DialogEvents {

    private static final String TAG = "MainActivity";

    private boolean twoPane = false;
    public static final int DIALOG_DELETE_ID = 1;
    public static final int DIALOG_CANCEL_EDIT_ID = 2;
    private static final int DIALOG_CANCEL_EDIT_UP_ID = 3;

    private AlertDialog mDialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        twoPane = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
        Log.d(TAG, "onCreate: twoPane = " + twoPane);

        FragmentManager fragmentManager = getSupportFragmentManager();
        //if add edit fragment exists then we are editing
        Boolean editing = fragmentManager.findFragmentById(R.id.task_details_container) != null;
        Log.d(TAG, "onCreate: editing = " + editing);

        //references for the fragment containers
        View addEditLayout = findViewById(R.id.task_details_container);
        View mainFragment = findViewById(R.id.mainFragment);

        if(twoPane){
            Log.d(TAG, "onCreate: twoPane mode");
            mainFragment.setVisibility(View.VISIBLE);
            addEditLayout.setVisibility(View.VISIBLE);
        } else if(editing){
            Log.d(TAG, "onCreate: single pane editing");
            mainFragment.setVisibility(View.GONE);
            addEditLayout.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "onCreate: single pane, but not editing");
            mainFragment.setVisibility(View.VISIBLE);
            addEditLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if(BuildConfig.DEBUG){
            MenuItem generate = menu.findItem(R.id.menumain_generate);
            generate.setVisible(true);
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mDialog != null && mDialog.isShowing()){
            mDialog.dismiss();
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
                startActivity(new Intent(this, DurationsReport.class));
                break;

            case R.id.menumain_settings:
                break;

            case R.id.menumain_showAbout:
                showAboutDialog();
                break;
                
            case R.id.menumain_generate:
                TestData.generateTestData(getContentResolver());
                break;

            case android.R.id.home:
                Log.d(TAG, "onOptionsItemSelected: home button pressed");
                AddEditFragment fragment = (AddEditFragment) getSupportFragmentManager().findFragmentById(R.id.task_details_container);
                if(fragment.canClose()){
                    return super.onOptionsItemSelected(item);
                } else {
                    showConfirmationDialog(MainActivity.DIALOG_CANCEL_EDIT_UP_ID);
                    return true;
                }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEditClick(@NonNull Task task) {
        taskEditRequest(task);
    }

    @Override
    public void onDeleteClick(@NonNull Task task) {
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

        View addEditLayout = findViewById(R.id.task_details_container);
        View mainFragment = findViewById(R.id.mainFragment);

        //just removed editing fragment, hide edit frame
        if(!twoPane){
            addEditLayout.setVisibility(View.GONE);
            mainFragment.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTaskLongClick(@NonNull Task task) {
        //handled by fragment(method required for interface)
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
            case DIALOG_CANCEL_EDIT_UP_ID:
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
            case DIALOG_CANCEL_EDIT_UP_ID:
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentById(R.id.task_details_container);

                if(fragment != null){
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                    if(twoPane){
                        //quit only if back button was used
                        if(dialogID == DIALOG_CANCEL_EDIT_ID) {
                            finish();
                        }
                    } else {
                        View addEditLayout = findViewById(R.id.task_details_container);
                        View mainFragment = findViewById(R.id.mainFragment);

                        addEditLayout.setVisibility(View.GONE);
                        mainFragment.setVisibility(View.VISIBLE);
                    }
                } else {
                    finish();
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
            showConfirmationDialog(MainActivity.DIALOG_CANCEL_EDIT_ID);
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
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "onClick: called from showAboutDialog");
                if(mDialog != null && mDialog.isShowing()){
                    mDialog.dismiss();
                }
            }
        });

        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(true);

        TextView tv = (TextView) messageView.findViewById(R.id.about_version);
        tv.setText("v" + BuildConfig.VERSION_NAME);

        //creating link for API's below 21
        TextView about_url = (TextView) messageView.findViewById(R.id.about_url);
        if(about_url != null){
            about_url.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String s = ((TextView) v).getText().toString();
                    intent.setData(Uri.parse(s));
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e){
                        Toast.makeText(MainActivity.this, "No browser application found cannot visit world-wide web", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        mDialog.show();
    }

    private void showConfirmationDialog(int dialogID){
        AppDialog appDialog = new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, dialogID);
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.cancelEditDialog_message));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.cancelEditDialog_positive_caption);
        args.putInt(AppDialog.DIALOG_NEGATIVE_RID, R.string.cancelEditDialog_negative_caption);

        appDialog.setArguments(args);
        appDialog.show(getSupportFragmentManager(), null);
    }

    private void taskEditRequest(Task task){
        Log.d(TAG, "taskEditRequest: called");

        AddEditFragment fragment = new AddEditFragment();

        Bundle arguments = new Bundle();
        arguments.putSerializable(Task.class.getSimpleName(), task);
        fragment.setArguments(arguments);

        Log.d(TAG, "taskEditRequest: twoPane mode");
        getSupportFragmentManager().beginTransaction().replace(R.id.task_details_container, fragment).commit();

        if(!twoPane) {
            Log.d(TAG, "taskEditRequest: in single pane mode");
            //hide left main fragment and show right edit frame
            View mainFragment = findViewById(R.id.mainFragment);
            View addEditLayout = findViewById(R.id.task_details_container);

            mainFragment.setVisibility(View.GONE);
            addEditLayout.setVisibility(View.VISIBLE);
        }
    }
}