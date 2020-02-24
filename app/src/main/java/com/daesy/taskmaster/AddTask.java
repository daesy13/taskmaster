package com.daesy.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Database;
import androidx.room.Room;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.daesy.taskmaster.models.Task;


// Reference: https://github.com/codefellows/seattle-java-401d9/blob/master/class-26/pokemon/app/src/main/java/com/ferreirae/pokemon/CatchPokemon.java
public class AddTask extends AppCompatActivity {

    MyDatabase db;

    static String TAGADD = "ds.addTask";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        db = Room.databaseBuilder(getApplicationContext(),MyDatabase.class,"tasks")
                .allowMainThreadQueries().build();

        // log for oncreate success
        Log.w(TAGADD, "we are in onCreate");

        // AddTask button to go to Task Detail
        Button addTaskButton =findViewById(R.id.button3);
        addTaskButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){

                EditText addTaskTitle = findViewById(R.id.editText);
                String taskTitle = addTaskTitle.getText().toString();


                EditText addTaskDetail = findViewById(R.id.editText3);
                String taskDetail = addTaskDetail.getText().toString();

                Task newTask = new Task(taskTitle, taskDetail);

                db.taskDao().addTask(newTask);

                Toast text =Toast.makeText(getApplicationContext(),"Submitted!", Toast.LENGTH_SHORT);
                text.show();

                AddTask.this.finish();
            }
         });
    }

}
