package com.daesy.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

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
    }
}
