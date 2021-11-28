package com.example.callumgedlinga2.game;

import android.graphics.Bitmap;

public class Question {
    private final String chemistryQuestion;
    private final Bitmap flashCardImage;
    private final String[] possibleAnswers;

    public Question(String chemistryQuestion, Bitmap flashCardImage,
                    String[] possibleAnswers) {
        this.chemistryQuestion = chemistryQuestion;
        this.flashCardImage = flashCardImage;
        this.possibleAnswers = possibleAnswers;
    }

    public boolean check(String guess) {
        return guess.equals(chemistryQuestion);
    }

    public Bitmap getFlashCardImage() {
        return flashCardImage;
    }

    public String[] getPossibleAnswers() {
        return possibleAnswers;
    }




}
