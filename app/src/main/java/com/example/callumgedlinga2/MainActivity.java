package com.example.callumgedlinga2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    public String userName;
    public EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView chemistry_image = findViewById(R.id.chemistry_image);
        chemistry_image.setImageResource(R.drawable.chem);
        name = findViewById(R.id.name);


    }

    public void playClicked(View view){
        if (name.length() == 0){
            name.setError("Please enter a name :)");
        }
        else{
            userName = name.getText().toString();
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            Intent settings = getIntent();
            String selection = settings.getStringExtra("selection");

            //if else statement needed to allow game to run if user has not selected a difficulty
            //use easy as default in this case
            if (selection == null){
                intent.putExtra("selection", "easy");
            }
            else{
                intent.putExtra("selection", selection);
            }

            intent.putExtra("userName", userName);
            startActivity(intent);
        }
    }

    //Take user to settings screen if settings button pressed
    public void settingsClicked(View view){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, SettingsActivity.SETTINGS_REQUEST);
    }

    //take user to highscores screen if highscores button clicked
    public void highScoreClicked(View view){
        Intent intent = new Intent(this, HighScoreActivity.class);
        startActivityForResult(intent, HighScoreActivity.HIGH_SCORE_REQUEST);
    }

    //Set edit text to empty if player clicks on it
    public void nameSelected(View view){
        name.setText("");
    }

}