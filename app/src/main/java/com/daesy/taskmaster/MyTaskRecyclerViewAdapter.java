package com.daesy.taskmaster;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daesy.taskmaster.fragments.TaskFragment.OnListFragmentInteractionListener;
import com.daesy.taskmaster.models.Task;

import java.util.List;

public class MyTaskRecyclerViewAdapter extends RecyclerView.Adapter<MyTaskRecyclerViewAdapter.ViewHolder> {

    private final List<Task> tasks;
    private final OnTaskSelectedListener listener;

    public MyTaskRecyclerViewAdapter(List<Task> items, OnTaskSelectedListener listener) {
        tasks = items;
        this.listener = listener;
    }

    // Creates a new row
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_task, parent, false);
        final ViewHolder holder = new ViewHolder(view);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onTaskSelected(holder.task);
            }
        });
        return holder;
    }


    // Given the holder and the position index, fill in that view with the right data for that position
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Task taskAtPosition = this.tasks.get(position);
        holder.task = taskAtPosition;
        holder.taskTitle.setText(taskAtPosition.getTitle());
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Task task;
        TextView taskTitle;


        public ViewHolder(View view) {
            super(view);
            this.taskTitle = itemView.findViewById(R.id.task_title);
        }

    }

    public interface OnTaskSelectedListener{
        void onTaskSelected(Task task);
    }
}