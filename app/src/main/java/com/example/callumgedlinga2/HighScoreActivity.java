package com.example.callumgedlinga2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.Arrays;

public class HighScoreActivity extends AppCompatActivity {
    public static int HIGH_SCORE_REQUEST = 1234;
    private SQLiteDatabase db;
    private Cursor scoresCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);
        setUpHighScoresView();
    }

    private void setUpHighScoresView() {
        //Populate the list_scores ListView from a cursor
        ListView listNames = findViewById(R.id.list_names);
        ListView listScores = findViewById(R.id.list_scores);
        try{
            SQLiteOpenHelper highscoreDatabaseHelper = new HighScoresDatabaseHelper(this);
            db = highscoreDatabaseHelper.getReadableDatabase();
            scoresCursor = db.query("HIGH_SCORES_DB",
                    new String[] { "_id", "NAME", "SCORE", "SHOWN_SCORE"},
                    null, null, null, null, "SCORE DESC");
            CursorAdapter nameAdapter =
                    new SimpleCursorAdapter(HighScoreActivity.this,
                            android.R.layout.simple_list_item_1, scoresCursor,
                            new String[]{"NAME"},
                            new int[]{android.R.id.text1}, 0);
            CursorAdapter scoreAdapter =
                    new SimpleCursorAdapter(HighScoreActivity.this,
                            android.R.layout.simple_list_item_1, scoresCursor,
                            new String[]{"SHOWN_SCORE"},
                            new int[]{android.R.id.text1}, 0);
            listNames.setAdapter(nameAdapter);
            listScores.setAdapter(scoreAdapter);
        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
            Intent intent = new Intent(HighScoreActivity.this, MainActivity.class);
            startActivity(intent);

        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        scoresCursor.close();
        db.close();
    }

}