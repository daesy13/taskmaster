package com.daesy.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class TaskDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
    }

    // GETTING Title
    @Override
    protected void onResume() {
        super.onResume();

//        String taskTitle = getIntent().getStringExtra("task");
//        TextView title = findViewById(R.id.textView7);
//        title.setText(taskTitle);

        Intent i = getIntent();

        String title = i.getExtras().getString("taskTitle");
        String body = i.getExtras().getString("taskBody");
        String state = i.getExtras().getString("taskState");

        TextView titleView = findViewById(R.id.detailTitle);
        TextView bodyView = findViewById(R.id.detailBody);
        TextView stateView = findViewById(R.id.stateView);


        titleView.setText(title);
        bodyView.setText(body);
        stateView.setText(state);

    }
}
