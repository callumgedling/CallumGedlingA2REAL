package com.example.callumgedlinga2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;


public class EndScreenActivity extends AppCompatActivity {
    TextView scoreView;
    TextView messageView;
    Button postToTwitter;
    GifImageView gifImageView;
    Twitter twitter = TwitterFactory.getSingleton();
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private static final int ONE_SECOND = 1000;
    ConstraintLayout constraintLayout;

    MediaPlayer clapping;

    private double accelerationPreviousValue;

    SwipeListener swipeListener;

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            //initialize axis values
            float xAxis = sensorEvent.values[0];
            float yAxis = sensorEvent.values[1];
            float zAxis = sensorEvent.values[2];

            // assign values to be able to calculate change in acceleration
            double accelerationCurrentValue = Math.sqrt((xAxis * xAxis + yAxis * yAxis + zAxis * zAxis));
            double changeInAcceleration = Math.abs(accelerationCurrentValue - accelerationPreviousValue);
            accelerationPreviousValue = accelerationCurrentValue;

           // check to see if phone is being shook, if less than one required as there is a change upon creation

            if (changeInAcceleration > 0.000004 && changeInAcceleration < 1){
                // if phone is being shaken, display a gif
                gifImageView = findViewById(R.id.chem_reaction);
                gifImageView.setImageResource(R.drawable.chem_reaction);

                //make gif disappear after 3 seconds
                CountDownTimer timer = new CountDownTimer(3 * ONE_SECOND, ONE_SECOND) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }
                    @Override
                    public void onFinish() {
                        gifImageView.setVisibility(View.GONE);
                    }
                };
                timer.start();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_screen);
        scoreView = findViewById(R.id.score);
        messageView = findViewById(R.id.message);
        postToTwitter = findViewById(R.id.post_to_twitter);

        //initialize sensor objects
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        constraintLayout = findViewById(R.id.constraintLayout);
        swipeListener = new SwipeListener(constraintLayout);

        Intent endIntent = getIntent();
        String finalScore = endIntent.getStringExtra("score");
        String userName = endIntent.getStringExtra("userName");


        double scorePercentage = getScorePercentage(finalScore);
        String scoreToShow = getScoreToShow(finalScore);

        addScoreToDB(userName, scorePercentage, scoreToShow);

        scoreView.setText("You scored: " + scoreToShow + "! (swipe left or right to go to the " +
                "home screen)");
        setScoreText(scorePercentage);
    }

    private double getScorePercentage(String finalScore){
        //Remove all letters
        String finalScoreTextRemoved = finalScore.replaceAll("[^0-9]", "");

        //Separate the two numbers
        char firstNumber = finalScoreTextRemoved.charAt(0);
        char secondNumber = finalScoreTextRemoved.charAt(1);
        //Convert the characters to double's and divide them to get the user's score percentage
        double userScore = Character.getNumericValue(firstNumber);
        double totalQuestions = Character.getNumericValue(secondNumber);

        //needed as string editing will result in error on hard mode otherwise
        if (totalQuestions == 1){
            totalQuestions = 10;
        }
        return (userScore / totalQuestions);
    }

    //post the user's score to dummymobile twitter account
    public void postToTwitterClicked(View view) {
        Background.run(() -> {
            try {
                Intent endIntent = getIntent();
                String finalScore = endIntent.getStringExtra("score");
                String userName = endIntent.getStringExtra("userName");
                String difficulty = endIntent.getStringExtra("difficulty");
                String finalScoreTextRemoved = finalScore.replaceAll("[^0-9]", "");
                finalScoreTextRemoved= finalScoreTextRemoved.substring(0, 1) + "/" +
                        finalScoreTextRemoved.substring(1);
                String tweet = userName + " scored: " + finalScoreTextRemoved + " on " + difficulty
                        + " mode! #learningisfun";

                twitter.updateStatus(tweet);
                //would like to toast here but program won't allow me to
            } catch (TwitterException ignored) {
                Toast toast = Toast.makeText(this, "Error posting to Twitter", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //stopping the listener on pause is necessary to conserve battery life
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
    }

    //get string value to present score in highscores page (ie 2/4)
    private String getScoreToShow(String finalScore){
        String finalScoreTextRemoved = finalScore.replaceAll("[^0-9]", "");
        return finalScoreTextRemoved.substring(0, 1) + "/" +
                finalScoreTextRemoved.substring(1);
    }

    //add the necessary values to the database
    private void addScoreToDB(String userName, double scorePercentage, String scoreToShow){

        SQLiteOpenHelper highScoresDatabaseHelper =
                new HighScoresDatabaseHelper(EndScreenActivity.this);
        SQLiteDatabase db = highScoresDatabaseHelper.getWritableDatabase();
        ContentValues scoreValues = new ContentValues();
        scoreValues.put("NAME", userName);
        scoreValues.put("SCORE", scorePercentage);
        scoreValues.put("SHOWN_SCORE", scoreToShow);
        db.insert("HIGH_SCORES_DB", null, scoreValues);
        Toast toast = Toast.makeText(this, "Score added to database!", Toast.LENGTH_SHORT);
        toast.show();
    }

    //fill a textview based on howwell the user did
    private void setScoreText(double scorePercentage){
        if (scorePercentage >= 0.80){
            messageView.setText("Congratulations! You scored very well! (Shake the screen " +
                    "for a surprise)");
            //Add clapping sound affect for user who scores well
            clapping = MediaPlayer.create(EndScreenActivity.this, R.raw.clapping);
            clapping.start();
            System.out.println("score is :" + scorePercentage);
        }

        else if (scorePercentage >= 0.50 && scorePercentage < 0.80){
            messageView.setText("Well done! You passed the quiz (Shake the screen for a surprise)");
        }

        else{
            messageView.setText("Don't lose heart! Study a bit more then try again! (Shake the " +
                    "screen for a surprise)");
        }
    }

    //take the user to the home page if they swipe left or right
    private class SwipeListener implements View.OnTouchListener {
        GestureDetector gestureDetector;

        SwipeListener(View view){
            int threshold = 5;
            int velocity_threshold = 5;

            GestureDetector.SimpleOnGestureListener listener =
                    new GestureDetector.SimpleOnGestureListener(){
                        @Override
                        public boolean onDown(MotionEvent motionEvent){
                            return true;
                        }

                        @Override
                        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                            float xDifference = e2.getX() - e1.getX();
                            float yDifference = e2.getY() - e1.getY();

                            try{
                                if (Math.abs(xDifference) > Math.abs(yDifference)) {
                                    if (Math.abs(xDifference) > threshold
                                            && Math.abs(velocityX) > velocity_threshold) {
                                        if (xDifference > 0) {
                                            Intent intent = new Intent(EndScreenActivity.this, MainActivity.class);
                                            System.out.println("YEWWWWWW");
                                            startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(EndScreenActivity.this, MainActivity.class);
                                            System.out.println("YEWWWWWW");
                                            startActivity(intent);

                                        }
                                        return true;
                                    }
                                }

                            } catch (Exception e){
                                e.printStackTrace();
                            }
                            return false;
                        }
                    };

            gestureDetector = new GestureDetector(listener);

            view.setOnTouchListener(this);
        }


        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {
            return gestureDetector.onTouchEvent(motionEvent);
        }
    }








}