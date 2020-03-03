package com.daesy.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Database;
import androidx.room.Room;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.CreateTaskMutation;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.daesy.taskmaster.models.Task;

import javax.annotation.Nonnull;

import type.CreateTaskInput;


// Reference: https://github.com/codefellows/seattle-java-401d9/blob/master/class-26/pokemon/app/src/main/java/com/ferreirae/pokemon/CatchPokemon.java
public class AddTask extends AppCompatActivity {

    MyDatabase db;

    static String TAG = "ds.addTask";

    private AWSAppSyncClient awsAppSyncClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        //AWS
        awsAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

        db = Room.databaseBuilder(getApplicationContext(), MyDatabase.class, "tasks")
                .allowMainThreadQueries().build();

        // log for oncreate success
        Log.w(TAG, "we are in onCreate");

        // AddTask button to go to Task Detail
        Button addTaskButton = findViewById(R.id.button3);
        addTaskButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                createTask();
                Toast text = Toast.makeText(getApplicationContext(), "Submitted!", Toast.LENGTH_SHORT);
                text.show();
            }
        });
    }

    public void createTask(){

        EditText addTaskTitle = findViewById(R.id.editText);
        String taskTitle = addTaskTitle.getText().toString();


        EditText addTaskDetail = findViewById(R.id.editText3);
        String taskDetail = addTaskDetail.getText().toString();

        CreateTaskInput createTaskInput = CreateTaskInput.builder()
                .title(taskTitle)
                .description(taskDetail)
                .status("NEW")
                .build();

        awsAppSyncClient.mutate(CreateTaskMutation.builder().input(createTaskInput).build())
            .enqueue(new GraphQLCall.Callback<CreateTaskMutation.Data>() {
                @Override
                public void onResponse(@Nonnull Response<CreateTaskMutation.Data> response) {
                    Log.i(TAG,response.data().toString());
                    AddTask.this.startActivity(new Intent(AddTask.this, MainActivity.class));
                }

                @Override
                public void onFailure(@Nonnull ApolloException e) {
                    Log.i(TAG,"Error!");

                }
            });




//                Task newTask = new Task(taskTitle, taskDetail);
//
//                db.taskDao().addTask(newTask);
    }

}
