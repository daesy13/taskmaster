package com.daesy.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.EditText;
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

        String taskTitle = getIntent().getStringExtra("task");
        TextView title = findViewById(R.id.textView7);
        title.setText(taskTitle);

//        SharedPreferences taskOne =
//                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        String taskOneTitle = taskOne.getString("task", "default");
//
//        SharedPreferences taskTwo =
//                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        String taskTwoTitle = taskTwo.getString("task", "default");
//
//        SharedPreferences taskThree =
//                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        String taskThreeTitle = taskThree.getString("task", "default");
//
//        if (taskOneTitle != "default") {
//            TextView title = findViewById(R.id.textView7);
//            title.setText(taskOneTitle);
//        }
//        else if (taskTwoTitle != "default") {
//            TextView title = findViewById(R.id.textView7);
//            title.setText(taskTwoTitle);
//        }
//        else if (taskThreeTitle != "default") {
//            TextView title = findViewById(R.id.textView7);
//            title.setText(taskThreeTitle);
//        }
    }

}
