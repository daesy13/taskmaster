package com.daesy.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Insert;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amazonaws.amplify.generated.graphql.CreateTaskMutation;
import com.amazonaws.amplify.generated.graphql.ListTasksQuery;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.daesy.taskmaster.models.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import type.CreateTaskInput;

public class MainActivity extends AppCompatActivity implements MyTaskRecyclerViewAdapter.OnTaskSelectedListener {

    private static final String TAG = "ds.MainActivity";

    static MyDatabase db;
    List<Task> listOfTasks;
    private RecyclerView recyclerView;
    private MyTaskRecyclerViewAdapter myTaskAdapter;
    //AWS
    private AWSAppSyncClient awsAppSyncClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //AWS
        awsAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

        // ***** THIS WILL CLEAR DB*****
//        db.taskDao().deleteAllTask();

        // Recycler view tasks
        this.listOfTasks = new LinkedList<>();

        // BUTTON GO TO ADD TASK
        Button goToAddTaskPage = findViewById(R.id.button);
        goToAddTaskPage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent goToAddTask = new Intent(MainActivity.this, AddTask.class);
                MainActivity.this.startActivity(goToAddTask);


//                runTaskItemCreateMutation(inputText, inputText);
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

//        db = Room.databaseBuilder(getApplicationContext(),MyDatabase.class,"tasks")
//                .allowMainThreadQueries().build();

        this.listOfTasks = new ArrayList<Task>();
//        this.listOfTasks.clear();
        // Adding all the list from the database to the recycler view
//        this.listOfTasks.addAll(this.db.taskDao().getAll());

        //***RECYCLER VIEW SETUP***
        this.recyclerView = findViewById(R.id.taskRecyclerView);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.myTaskAdapter = new MyTaskRecyclerViewAdapter(listOfTasks, this);
        this.recyclerView.setAdapter(this.myTaskAdapter);

        getTaskItems();


        //AWS
//        runTaskItemCreateMutation(inputText, inputText);

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

    public void getTaskItems(){
        awsAppSyncClient.query(ListTasksQuery.builder().build())
                .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
                .enqueue(taskItemsCallBack);
    }


    private GraphQLCall.Callback<ListTasksQuery.Data> taskItemsCallBack = new GraphQLCall.Callback<ListTasksQuery.Data>() {
        @Override
        public void onResponse(@Nonnull Response<ListTasksQuery.Data> response) {
            Log.i("TAG", response.data().listTasks().items().toString());
            if (listOfTasks.size() == 0 || response.data().listTasks().items().size() != listOfTasks.size()) {
                listOfTasks.clear();

                for (ListTasksQuery.Item task : response.data().listTasks().items()) {
                    Task newTask = new Task(task.title(), task.description());
                    listOfTasks.add(newTask);
                }

                // looper let us send an action to the main ui thread
                Handler handlerForMainThread = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message inputMessage) {
                        RecyclerView recyclerView = findViewById(R.id.taskRecyclerView);
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                };
                handlerForMainThread.obtainMessage().sendToTarget();
            }
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e("TAG", e.toString());
        }
    };
}
