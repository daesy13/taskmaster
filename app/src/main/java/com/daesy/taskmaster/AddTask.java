package com.daesy.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.CreateTaskMutation;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.io.File;
import java.util.UUID;

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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 333 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();

            imagePicked(selectedImage);

        }
    }

    private void imagePicked(Uri uri){
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageURI(uri);

//        uploadWithTransferUtility(uri);
    }


    // AddImage button Activity
    public void addImage(View v){
        Log.d(TAG, "button clicked");

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            Intent i = new Intent(
                    Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(i, 333);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode != 0) {
            return;
        }
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Intent i = new Intent(
                    Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(i, 333);
        }
    }

    // ****************

    public void uploadWithTransferUtility(Uri uri) {

        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(uri,
                filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();

        // String picture path contains the path of selected Image
        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance()))
                        .build();

//        File file = new File(getApplicationContext().getFilesDir(), "sample.txt");
//        try {
//            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
//            writer.append("Howdy World!");
//            writer.close();
//        }
//        catch(Exception e) {
//            Log.e(TAG, e.getMessage());
//        }

        final String uuid = UUID.randomUUID().toString();
        TransferObserver uploadObserver =
                transferUtility.upload(
                        "public/" + uuid,
                        new File(picturePath), CannedAccessControlList.PublicRead);

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
