package com.jonathanlouis.tasktimerapp;

import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class CursorRecyclerViewAdapter extends RecyclerView.Adapter<CursorRecyclerViewAdapter.TaskViewHolder> {
    private static final String TAG = "CursorRecyclerViewAdapt";
    private Cursor cursor;
    private OnTaskClickListener listener;

    interface OnTaskClickListener{
        void onEditClick(@NonNull Task task);
        void onDeleteClick(@NonNull Task task);
        void onTaskLongClick(@NonNull Task task);
    }

    public CursorRecyclerViewAdapter(Cursor cursor, OnTaskClickListener listener) {
        Log.d(TAG, "CursorRecyclerViewAdapter: constructor called");
        this.cursor = cursor;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        Log.d(TAG, "onCreateViewHolder: called");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_items, parent, false);
        return  new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
//        Log.d(TAG, "onBindViewHolder: called");

        if(cursor == null || cursor.getCount() == 0){
            Log.d(TAG, "onBindViewHolder: providing instructions");
            holder.name.setText(R.string.instructions_heading);
            holder.description.setText(R.string.instructions);
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        } else {
            if(!cursor.moveToPosition(position)){
                throw new IllegalStateException("Couldn't move cursor to position: " + position);
            }

            final Task task = new Task(cursor.getLong(cursor.getColumnIndex(TasksContract.Columns._ID)),
                            cursor.getString(cursor.getColumnIndex(TasksContract.Columns.TASKS_NAME)),
                            cursor.getString(cursor.getColumnIndex(TasksContract.Columns.TASKS_DESCRIPTION)),
                            cursor.getInt(cursor.getColumnIndex(TasksContract.Columns.TASKS_SORTORDER)));

            holder.name.setText(task.getName());
            holder.description.setText(task.getDescription());

            holder.editButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);

            View.OnClickListener buttonListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Log.d(TAG, "onClick: starts");
                    switch (view.getId()){
                        case R.id.tli_edit:
                            if(listener != null) {
                                listener.onEditClick(task);
                            }
                            break;

                        case R.id.tli_delete:
                            if(listener != null) {
                                listener.onDeleteClick(task);
                            }
                            break;

                        default:
                            Log.d(TAG, "onClick: found unexpected button id.");
                    }

//                    Log.d(TAG, "onClick: button with id " + view.getId() + " clicked.");
//                    Log.d(TAG, "onClick: task name is " + task.getName());
                }
            };

            View.OnLongClickListener buttonLongListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.d(TAG, "onLongClick: called");
                    if(listener != null){
                        listener.onTaskLongClick(task);
                        return true;
                    }

                    return false;
                }
            };

            holder.editButton.setOnClickListener(buttonListener);
            holder.deleteButton.setOnClickListener(buttonListener);
            holder.itemView.setOnLongClickListener(buttonLongListener);
        }
    }

    @Override
    public int getItemCount() {
//        Log.d(TAG, "getItemCount: called");
        if(cursor == null || cursor.getCount() == 0){
            return 1;
        }

        return cursor.getCount();
    }

    /**
     * Swap in a new Cursor and return the old Cursor
     * The returned old Cursor is <em>not</em> closed.
     *
     * @param newCursor
     * @return Returns the previously set Cursor or null if there wasn't one.
     * If the given new Cursor is the same instance as the previously set Cursor,
     * null is also returned.
     */
    Cursor swapCursor(Cursor newCursor){
        if(newCursor == cursor){
            return null;
        }

        int numItems = getItemCount();

        final Cursor oldCursor = cursor;
        cursor = newCursor;
        if(newCursor != null){
            notifyDataSetChanged();
        } else {
            notifyItemRangeRemoved(0, numItems);
        }

        return oldCursor;
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder{
//        private static final String TAG = "TaskViewHolder";

        TextView name;
        TextView description;
        ImageButton editButton;
        ImageButton deleteButton;
        View itemView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
//            Log.d(TAG, "TaskViewHolder: constructor called");
            this.name = itemView.findViewById(R.id.tli_name);
            this.description = itemView.findViewById(R.id.tli_description);
            this.editButton = itemView.findViewById(R.id.tli_edit);
            this.deleteButton = itemView.findViewById(R.id.tli_delete);
            this.itemView = itemView;
        }
    }
}
