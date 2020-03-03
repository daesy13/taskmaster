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
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.SignOutOptions;
import com.amazonaws.mobile.client.UserState;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.daesy.taskmaster.models.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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

        getApplicationContext().startService(new Intent(getApplicationContext(), TransferService.class));

        //AWS
        awsAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

        AWSMobileClient.getInstance().initialize(getApplicationContext(), new Callback<UserStateDetails>() {
            @Override
            public void onResult(UserStateDetails userStateDetails) {
                Log.i(TAG, "AWSMobileClient initialized. User State is " + userStateDetails.getUserState());
                uploadWithTransferUtility();
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Initialization error.", e);
            }
        });

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

        // LOG OUT BUTTON
        Button logOutButton = findViewById(R.id.Logout);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AWSMobileClient.getInstance().signOut(SignOutOptions.builder().signOutGlobally(true).build(), new Callback<Void>() {
                    @Override
                    public void onResult(final Void result) {
                        AWSMobileClient.getInstance().showSignIn(MainActivity.this, new Callback<UserStateDetails>() {
                            @Override
                            public void onResult(UserStateDetails result) {
                                Log.d(TAG, "onResult: " + result.getUserState());
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.d(TAG, "onError", e);
                            }
                        });
                        Log.d(TAG, "sign out");
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "sign out error", e);
                    }
                });
            }
        });


        // LOGIN ON PAGE LOAD
        AWSMobileClient.getInstance().initialize(getApplicationContext(), new Callback<UserStateDetails>() {

                @Override
                public void onResult(UserStateDetails userStateDetails) {
                    Log.i("INIT", "onResult: " + userStateDetails.getUserState());
                    if(userStateDetails.getUserState().equals(UserState.SIGNED_OUT)){
                        // 'this' refers the the current active activity
                        AWSMobileClient.getInstance().showSignIn(MainActivity.this, new Callback<UserStateDetails>() {
                            @Override
                            public void onResult(UserStateDetails result) {
                                Log.d(TAG, "onResult: " + result.getUserState());
//                                if(result.getUserState().equals(UserState.SIGNED_IN)){
//                                    uploadWithTransferUtility();
//                                }


                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e(TAG, "onError: ", e);
                            }
                        });
                    }

                }


                @Override
                public void onError(Exception e) {
                    Log.e("INIT", "Initialization error.", e);
                }

            }
        );

    }

    // GETTING USERNAME
    @Override
    protected void onResume() {
        super.onResume();

//        SharedPreferences userName =
//                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        String name = userName.getString("name", "default");

        // get data from AWS
        getTaskItems();


        TextView greeting = findViewById(R.id.textView3);
        String username = AWSMobileClient.getInstance().getUsername();
        greeting.setText(username + "'s Tasks");



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

//        getTaskItems();


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


    public void uploadWithTransferUtility() {

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance()))
                        .build();

        File file = new File(getApplicationContext().getFilesDir(), "sample.txt");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.append("Howdy World!");
            writer.close();
        }
        catch(Exception e) {
            Log.e(TAG, e.getMessage());
        }

        TransferObserver uploadObserver =
                transferUtility.upload(
                        "public/sample.txt",
                        new File(getApplicationContext().getFilesDir(),"sample.txt"));

        // Attach a listener to the observer to get state update and progress notifications
        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    // Handle a completed upload.
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int)percentDonef;

                Log.d(TAG, "ID:" + id + " bytesCurrent: " + bytesCurrent
                        + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                // Handle errors
            }

        });

        // If you prefer to poll for the data, instead of attaching a
        // listener, check for the state and progress in the observer.
        if (TransferState.COMPLETED == uploadObserver.getState()) {
            // Handle a completed upload.
        }

        Log.d(TAG, "Bytes Transferred: " + uploadObserver.getBytesTransferred());
        Log.d(TAG, "Bytes Total: " + uploadObserver.getBytesTotal());
    }
}
