package com.example.callumgedlinga2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.callumgedlinga2.game.ChemFlashCardManager;
import com.example.callumgedlinga2.game.Difficulty;
import com.example.callumgedlinga2.game.Game;
import com.example.callumgedlinga2.game.GameBuilder;
import com.example.callumgedlinga2.game.Question;

import java.util.Locale;

public class GameActivity extends AppCompatActivity {
    public static int GAME_REQUEST = 1234;
    private static final int ONE_SECOND = 1000;
    private TextView messageView;
    private TextView scoreView;
    private GridView solutionsView;
    private ImageView flashCardView;

    private ArrayAdapter<String> solutions;
    private Difficulty level;
    private GameBuilder gameBuilder;
    private Game game;
    private Question currentQuestion;

    private CountDownTimer timer;

    public GameActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        String selection = intent.getStringExtra("selection");
        String userName = intent.getStringExtra("userName");
        level = Difficulty.valueOf(selection.toUpperCase());
        String difficulty = level.toString().toLowerCase();

        ChemFlashCardManager chemFlashCardManager = new ChemFlashCardManager(getAssets(),
                "chemFlashCard");
        gameBuilder = new GameBuilder(chemFlashCardManager);


        messageView = findViewById(R.id.time);
        scoreView = findViewById(R.id.score);
        solutionsView = findViewById(R.id.solutions);
        flashCardView = findViewById(R.id.question);

        solutions = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        solutionsView.setAdapter(solutions);

        solutionsView.setOnItemClickListener((parent, view, position, id) -> {
            String guess = (String) solutionsView.getItemAtPosition(position);
            game.updateScore(currentQuestion.check(guess));
            scoreView.setText(game.getScore());

            if (game.isGameOver()) {
                String score = game.getScore();
                timer.cancel();
                Intent endIntent = new Intent(GameActivity.this, EndScreenActivity.class);
                endIntent.putExtra("score", score);
                endIntent.putExtra("userName", userName);
                endIntent.putExtra("difficulty", difficulty);
                startActivity(endIntent);
                solutions.clear();
            } else {
                showNextQuestion();
            }
        });


    }
    @Override
    protected void onResume() {
        super.onResume();
        setupGame();
        scoreView.setText(game.getScore());
        timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }


    private void setupGame() {
        game = gameBuilder.create(level);
        showNextQuestion(); // show the first question

        timer = new CountDownTimer(60 * ONE_SECOND, ONE_SECOND) {
            @Override
            public void onTick(long millisUntilFinished) {
                long timeRemaining = millisUntilFinished / ONE_SECOND;
                messageView.setText(String.format(Locale.getDefault(),
                        "Time remaining: %d", timeRemaining));
            }

            @Override
            public void onFinish() {
                solutions.clear();
                messageView.setText("Game Over!");
            }
        };
    }

    private void showNextQuestion() {
        currentQuestion = game.next();
        flashCardView.setImageBitmap(currentQuestion.getFlashCardImage());
        solutions.clear();
        solutions.addAll(currentQuestion.getPossibleAnswers());
    }

}
