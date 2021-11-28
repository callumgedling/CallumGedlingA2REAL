package com.example.callumgedlinga2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

public class SettingsActivity extends AppCompatActivity {
    public static int SETTINGS_REQUEST = 1234;
    private Spinner difficulty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Button home = findViewById(R.id.home);
        difficulty = findViewById(R.id.difficulty);

        //change the difficulty on button pressed
        home.setOnClickListener(v -> {
            String selection = difficulty.getSelectedItem().toString();

            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.putExtra("selection", selection);
            startActivity(intent);
        });
    }


}