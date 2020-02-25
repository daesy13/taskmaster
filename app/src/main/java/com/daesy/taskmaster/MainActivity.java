package com.daesy.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Insert;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.daesy.taskmaster.models.Task;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MyTaskRecyclerViewAdapter.OnTaskSelectedListener {

    private static final String TAG = "ds.MainActivity";

    static MyDatabase db;
    List<Task> listOfTasks;
    private RecyclerView recyclerView;
    private MyTaskRecyclerViewAdapter myTaskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // ***** THIS WILL CLEAR DB*****
//        db.taskDao().deleteAllTask();

        this.listOfTasks = new LinkedList<>();

        // BUTTON GO TO ADD TASK
        Button goToAddTaskPage = findViewById(R.id.button);
        goToAddTaskPage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent goToAddTask = new Intent(MainActivity.this, AddTask.class);
                MainActivity.this.startActivity(goToAddTask);
            }
        });

        // BUTTON GO TO ALL TASK
        Button goToAllTaskPage = findViewById(R.id.button2);
        goToAllTaskPage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent goToAllTask = new Intent(MainActivity.this, AllTask.class);
                MainActivity.this.startActivity(goToAllTask);
            }
        });

        // BUTTON GO TO SETTINGS
        Button goToSettingsPage = findViewById(R.id.button8);
        goToSettingsPage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent goToSettings = new Intent(MainActivity.this, Settings.class);
                MainActivity.this.startActivity(goToSettings);
            }
        });
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

        db = Room.databaseBuilder(getApplicationContext(),MyDatabase.class,"tasks")
                .allowMainThreadQueries().build();


        this.listOfTasks.clear();
        // Adding all the list from the database to the recycler view
        this.listOfTasks.addAll(this.db.taskDao().getAll());

        //***RECYCLER VIEW SETUP***
        this.recyclerView = findViewById(R.id.taskRecyclerView);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.myTaskAdapter = new MyTaskRecyclerViewAdapter(listOfTasks, this);
        this.recyclerView.setAdapter(this.myTaskAdapter);

        // ***** THIS WILL CLEAR DB*****
//        db.taskDao().deleteAllTask();

        // this triggers recycler view to update
//        this.myTaskAdapter.notifyDataSetChanged();
    }


    // THIS METHODS DEFINE WHATS HAPPENED WHEN A TASK IS CLICKED IN THE RECYCLER VIEW
    @Override
    public void onTaskSelected(Task task){
        Log.i(TAG, "task title:" +  task.getTitle());
        Intent goToDetail = new Intent(this, TaskDetail.class);
        goToDetail.putExtra("taskTitle", task.getTitle());
        goToDetail.putExtra("taskBody", task.getBody());
        goToDetail.putExtra("taskState", task.getState());
        startActivity(goToDetail);
    }


}
