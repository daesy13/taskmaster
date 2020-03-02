package com.daesy.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Bundle;
import android.widget.Toast;

import com.daesy.taskmaster.models.Task;

import java.util.ArrayList;
import java.util.List;

public class AllTask extends AppCompatActivity implements MyTaskRecyclerViewAdapter.OnTaskSelectedListener {

    static MyDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_task);

        db = Room.databaseBuilder(getApplicationContext(),MyDatabase.class,"tasks")
                .allowMainThreadQueries().build();

        //*********** when UNCOMENTING THIS NEXT LINE ALSO UNCOMENT rv.setAdapter(new MyTaskRecy.....)*********
//        List<Task> listOfTasks = db.taskDao().getAll();
        List<Task> items = new ArrayList<>();
        items.add(new Task("test1", "description1"));
        items.add(new Task("test2", "description2"));
        items.add(new Task("test3", "description3"));


        RecyclerView rv = findViewById(R.id.allTaskList);
        rv.setLayoutManager(new LinearLayoutManager(this));
//        rv.setAdapter(new MyTaskRecyclerViewAdapter(listOfTasks, this));
        rv.setAdapter(new MyTaskRecyclerViewAdapter(items, this));
    }
    @Override
    public void onTaskSelected(Task task) {
        Toast t = new Toast(this);
        CharSequence chars = task.body;
        Toast.makeText(getApplicationContext(), chars, Toast.LENGTH_SHORT).show();
    }
}