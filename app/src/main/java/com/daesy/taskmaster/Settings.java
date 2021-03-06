package com.daesy.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button fab = findViewById(R.id.button4);
        fab.setOnClickListener(new View.OnClickListener() {

            // SAVING USERNAME AND REDIRECT TO MAIN ACTIVITY
            @Override
            public void onClick(View v){
                EditText userInput = findViewById(R.id.editText2);
                Snackbar.make(v, "Saved!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                SharedPreferences userName =
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = userName.edit();
                editor.putString("name", userInput.getText().toString());
                editor.apply();

                Intent i = new Intent(Settings.this, MainActivity.class);
                Settings.this.startActivity(i);
            }
        });
    }
}
