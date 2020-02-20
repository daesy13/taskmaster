package com.daesy.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static MyDatabase db;
    List<Task> listOfTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // GO TO ADD TASK
        Button goToAddTaskPage = findViewById(R.id.button);
        goToAddTaskPage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent goToAddTask = new Intent(MainActivity.this, AddTask.class);
                MainActivity.this.startActivity(goToAddTask);
            }
        });

        // GO TO ALL TASK
        Button goToAllTaskPage = findViewById(R.id.button2);
        goToAllTaskPage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent goToAllTask = new Intent(MainActivity.this, AllTask.class);
                MainActivity.this.startActivity(goToAllTask);
            }
        });

        // GO TO SETTINGS
        Button goToSettingsPage = findViewById(R.id.button8);
        goToSettingsPage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent goToSettings = new Intent(MainActivity.this, Settings.class);
                MainActivity.this.startActivity(goToSettings);
            }
        });

//        // FIRST TASK GO TO DETAIL PAGE
//        Button firstGoToDetailPage = findViewById(R.id.button5);
//        firstGoToDetailPage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent goToSettings = new Intent(MainActivity.this, TaskDetail.class);
//                MainActivity.this.startActivity(goToSettings);
//
//                TextView titleOne = findViewById(R.id.button5);
//
//                SharedPreferences taskOne =
//                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                SharedPreferences.Editor editor = taskOne.edit();
//                editor.putString("task", titleOne.getText().toString());
//                editor.apply();
//            }
//        });
//
//        // SECOND TASK GO TO DETAIL PAGE
//        Button secondGoToDetailPage = findViewById(R.id.button6);
//        secondGoToDetailPage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent goToSettings = new Intent(MainActivity.this, TaskDetail.class);
//                MainActivity.this.startActivity(goToSettings);
//
//                TextView titleTwo = findViewById(R.id.button6);
//
//                SharedPreferences taskTwo =
//                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                SharedPreferences.Editor editor = taskTwo.edit();
//                editor.putString("task", titleTwo.getText().toString());
//                editor.apply();
//            }
//        });
//
//        // THIRD TASK GO TO DETAIL PAGE
//        Button thirdGoToDetailPage = findViewById(R.id.button7);
//        thirdGoToDetailPage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent goToSettings = new Intent(MainActivity.this, TaskDetail.class);
//                MainActivity.this.startActivity(goToSettings);
//
//                TextView titleThree = findViewById(R.id.button7);
//
//                SharedPreferences taskThree =
//                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                SharedPreferences.Editor editor = taskThree.edit();
//                editor.putString("task", titleThree.getText().toString());
//                editor.apply();
//            }
//        });
    }

    // GETTING USERNAME
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences userName =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String name = userName.getString("name", "default");

        if (name != "default") {
            TextView greeting = findViewById(R.id.textView3);
            greeting.setText(name + "'s Tasks");
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        db = Room.databaseBuilder(getApplicationContext(), MyDatabase.class, "task").allowMainThreadQueries().build();

    }
}
