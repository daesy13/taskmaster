package com.daesy.taskmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amazonaws.amplify.generated.graphql.ListTasksQuery;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.SignOutOptions;
import com.amazonaws.mobile.client.UserState;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration;
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.daesy.taskmaster.models.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;


public class MainActivity extends AppCompatActivity implements MyTaskRecyclerViewAdapter.OnTaskSelectedListener {

    private static final String TAG = "ds.MainActivity";

    static MyDatabase db;
    static String CHANNEL_ID = "111";
    List<Task> listOfTasks;
    private RecyclerView recyclerView;
    private MyTaskRecyclerViewAdapter myTaskAdapter;
    //AWS
    private AWSAppSyncClient awsAppSyncClient;

    //PUSH NOTIFICATION
    private static PinpointManager pinpointManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize PinpointManager
        getPinpointManager(getApplicationContext());

        getApplicationContext().startService(new Intent(getApplicationContext(), TransferService.class));

        //AWS
        awsAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

        //CREATING CHANNEL_ID
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel";
            String description = "description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        //CREATING NOTIFICATION
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("You have a Task")
                .setContentText("Pending")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify((int)(Math.random() * 100.0), builder.build());

        AWSMobileClient.getInstance().initialize(getApplicationContext(), new Callback<UserStateDetails>() {
            @Override
            public void onResult(UserStateDetails userStateDetails) {
//                Log.i(TAG, "AWSMobileClient initialized. User State is " + userStateDetails.getUserState());
//                uploadWithTransferUtility();
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

        //***RECYCLER VIEW SETUP***
        this.recyclerView = findViewById(R.id.taskRecyclerView);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.myTaskAdapter = new MyTaskRecyclerViewAdapter(listOfTasks, this);
        this.recyclerView.setAdapter(this.myTaskAdapter);

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
//        this.listOfTasks.clear();
        // Adding all the list from the database to the recycler view
//        this.listOfTasks.addAll(this.db.taskDao().getAll());
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


    //PUSH NOTIFICATION
    public static PinpointManager getPinpointManager(final Context applicationContext) {
        if (pinpointManager == null) {
            final AWSConfiguration awsConfig = new AWSConfiguration(applicationContext);
            AWSMobileClient.getInstance().initialize(applicationContext, awsConfig, new Callback<UserStateDetails>() {
                @Override
                public void onResult(UserStateDetails userStateDetails) {
                    Log.i("INIT", userStateDetails.getUserState().toString());
                }

                @Override
                public void onError(Exception e) {
                    Log.e("INIT", "Initialization error.", e);
                }
            });

            PinpointConfiguration pinpointConfig = new PinpointConfiguration(
                    applicationContext,
                    AWSMobileClient.getInstance(),
                    awsConfig);

            pinpointManager = new PinpointManager(pinpointConfig);

            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "getInstanceId failed", task.getException());
                                return;
                            }
                            final String token = task.getResult().getToken();
                            Log.d(TAG, "Registering push notifications token: " + token);
                            pinpointManager.getNotificationClient().registerDeviceToken(token);
                        }
                    });
        }
        return pinpointManager;
    }

}
