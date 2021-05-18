package com.jonathanlouis.tasktimerapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


/**
 * Class for creating all dialog messages for the app.
 */
public class AppDialog extends DialogFragment {
    private static final String TAG = "AppDialog";

    public static final String DIALOG_ID = "id";
    public static final String DIALOG_MESSAGE = "message";
    public static final String DIALOG_POSITIVE_RID = "positive_rid";
    public static final String DIALOG_NEGATIVE_RID = "negative_rid";

    /**
     * The dialog message's callback interface to notify the user selected results.
     * i.e. confirming deletion
     */
    interface DialogEvents {
        void onPositiveResult(int dialogID, Bundle args);
        void onNegativeResult(int dialogID, Bundle args);
        void onDialogCancelled(int dialogID);
    }

    private DialogEvents dialogEvents;

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "onAttach: called from activity: " + context.toString());
        super.onAttach(context);

        //Activities using this fragment must implement the callbacks
        if(!(context instanceof DialogEvents)){
            throw new ClassCastException(context.toString() + " must implement AppDialog.DialogEvents interface.");
        }

        dialogEvents = (DialogEvents) context;
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: called");
        super.onDetach();

        // reset active callbacks interface after activity removed
        dialogEvents = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateDialog: called");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final Bundle arguments = getArguments();
        final int dialogID;
        String messageString;
        int positiveStringID;
        int negativeStringID;

        if(arguments != null){
            dialogID = arguments.getInt(DIALOG_ID);
            messageString = arguments.getString(DIALOG_MESSAGE);

            if(dialogID == 0 || messageString == null){
                throw new IllegalArgumentException("DIALOG_ID &/or DIALOG_MESSAGE not present in the bundle.");
            }

            positiveStringID = arguments.getInt(DIALOG_POSITIVE_RID);
            //set default positive
            if(positiveStringID == 0){
                positiveStringID = R.string.ok;
            }

            negativeStringID = arguments.getInt(DIALOG_NEGATIVE_RID);
            //set default negative
            if(negativeStringID == 0){
                negativeStringID = R.string.cancel;
            }
        } else {
            throw new IllegalArgumentException("Must pass DIALOG_ID and DIALOG_MESSAGE in args bundle.");
        }

        builder.setMessage(messageString)
                .setPositiveButton(positiveStringID, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //callback positive result
                if(dialogEvents != null) {
                    dialogEvents.onPositiveResult(dialogID, arguments);
                }
            }
        }).setNegativeButton(negativeStringID, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //callback negative result
                if(dialogEvents != null) {
                    dialogEvents.onNegativeResult(dialogID, arguments);
                }
            }
        });

        return builder.create();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        Log.d(TAG, "onCancel: called");
        if(dialogEvents != null){
            int dialogID = getArguments().getInt(DIALOG_ID);
            dialogEvents.onDialogCancelled(dialogID);
        }
    }


}
