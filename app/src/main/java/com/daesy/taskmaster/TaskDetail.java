package com.daesy.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class TaskDetail extends AppCompatActivity {

    static String TAG = "ds.TaskDetail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
    }

    // GETTING Title
    @Override
    protected void onResume() {
        super.onResume();

        Intent i = getIntent();

        String title = i.getExtras().getString("taskTitle");
        String body = i.getExtras().getString("taskBody");
        String state = i.getExtras().getString("taskState");

        TextView titleView = findViewById(R.id.detailTitle);
        TextView bodyView = findViewById(R.id.detailBody);
        TextView stateView = findViewById(R.id.stateView);

        titleView.setText(title);
        bodyView.setText(body);
        stateView.setText(state);

        // Retrieve Image
        ImageView imageView = findViewById(R.id.imageDisplay);
        String fileNameText = i.getStringExtra("imageFileName");

//        String urlS3 = " " + fileNameText;
//        Picasso.get().load(urlS3).into(taskImageView);

    }

}
