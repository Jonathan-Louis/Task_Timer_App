package com.jonathanlouis.tasktimerapp;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

public class DurationsRVAdapter extends RecyclerView.Adapter<DurationsRVAdapter.ViewHolder> {

    private Cursor cursor;
    private final java.text.DateFormat dateFormat;

    public DurationsRVAdapter(Context context, Cursor cursor) {
        this.cursor = cursor;
        this.dateFormat = DateFormat.getDateFormat(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_duration_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if((cursor != null) && (cursor.getCount() != 0)){
            if(!cursor.moveToPosition(position)){
                throw new IllegalStateException("Couldn't move to cursor position " + position);
            }
            String name = cursor.getString(cursor.getColumnIndex(DurationsContract.Columns.DURATIONS_NAME));
            String description = cursor.getString(cursor.getColumnIndex(DurationsContract.Columns.DURATIONS_DESCRIPTION));
            Long startTime = cursor.getLong(cursor.getColumnIndex(DurationsContract.Columns.DURATIONS_START_TIME));
            long totalDuration = cursor.getLong(cursor.getColumnIndex(DurationsContract.Columns.DURATIONS_DURATION));

            holder.name.setText(name);
            if(holder.description != null){
                holder.description.setText(description);
            }

            holder.startDate.setText(dateFormat.format(startTime * 1000));
            holder.duration.setText(formatDuration(totalDuration));
        }
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    private String formatDuration(long duration){
        //duration in seconds convert to hours, mins, secs
        long hours = duration / 3600;
        long remainder = duration - (hours * 3600);
        long mins = remainder / 60;
        long secs = remainder - (mins * 60);

        return String.format(Locale.US, "%03d:%02d:%02d", hours, mins, secs);
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

        final Cursor oldCursor = cursor;
        cursor = newCursor;
        if(newCursor != null){
            notifyDataSetChanged();
        } else {
            notifyItemRangeRemoved(0, getItemCount());
        }

        return oldCursor;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView description;
        TextView startDate;
        TextView duration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.td_name);
            this.description = itemView.findViewById(R.id.td_description);
            this.startDate = itemView.findViewById(R.id.td_start);
            this.duration = itemView.findViewById(R.id.td_duration);
        }
    }

}
