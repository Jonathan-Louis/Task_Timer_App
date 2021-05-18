package com.jonathanlouis.tasktimerapp;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

public class AddEditActivity extends AppCompatActivity implements AddEditFragment.OnSaveClicked, AppDialog.DialogEvents {

    private static final String TAG = "AddEditActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "AddEditActivity onCreate: called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AddEditFragment fragment = new AddEditFragment();

        Bundle arguments = getIntent().getExtras();
        fragment.setArguments(arguments);

        getSupportFragmentManager().beginTransaction().replace(R.id.add_edit_framelayout, fragment).commit();
    }

    @Override
    public void onSaveClicked() {
        Log.d(TAG, "onSaveClicked: called");

        finish();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: called");

        FragmentManager fragmentManager = getSupportFragmentManager();
        AddEditFragment fragment = (AddEditFragment) fragmentManager.findFragmentById(R.id.add_edit_framelayout);

        if(fragment.canClose()){
            super.onBackPressed();
        } else {
            //show dialogue to confirm to quit editing
           showConfirmationDialog();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: called");
        switch (item.getItemId()){
            case android.R.id.home:
                Log.d(TAG, "onOptionsItemSelected: home button pressed");
                AddEditFragment fragment = (AddEditFragment) getSupportFragmentManager().findFragmentById(R.id.add_edit_framelayout);
                if(fragment.canClose()){
                    return super.onOptionsItemSelected(item);
                } else {
                    showConfirmationDialog();
                    return true;
                }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showConfirmationDialog(){
        AppDialog appDialog = new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, MainActivity.DIALOG_CANCEL_EDIT_ID);
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.cancelEditDialog_message));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.cancelEditDialog_positive_caption);
        args.putInt(AppDialog.DIALOG_NEGATIVE_RID, R.string.cancelEditDialog_negative_caption);

        appDialog.setArguments(args);
        appDialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onPositiveResult(int dialogID, Bundle args) {
        Log.d(TAG, "onPositiveResult: called");
    }

    @Override
    public void onNegativeResult(int dialogID, Bundle args) {
        Log.d(TAG, "onNegativeResult: called");
        finish();
    }

    @Override
    public void onDialogCancelled(int dialogID) {
        Log.d(TAG, "onDialogCancelled: called");
    }
}