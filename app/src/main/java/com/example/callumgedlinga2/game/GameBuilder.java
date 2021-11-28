package com.example.callumgedlinga2.game;

import android.graphics.Bitmap;

import com.example.callumgedlinga2.BuildConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameBuilder {
    private final ChemFlashCardManager chemFlashCardManager;

    public GameBuilder(ChemFlashCardManager chemFlashCardManager) {
        this.chemFlashCardManager = chemFlashCardManager;
    }

    public Game create(Difficulty level) {
        int maxQuestions = chemFlashCardManager.count();
        if (BuildConfig.DEBUG && maxQuestions < 10) {
            throw new AssertionError("Not enough flash card images in project assets.");
        }

        //Set the number of questions based on the difficulty
        switch (level) {
            case EASY:
                return new Game(constructQuestions(4));
            case MEDIUM:
                return new Game(constructQuestions(8));
            case HARD:
                return new Game(constructQuestions(10));
        }

        return null;
    }

    private Question[] constructQuestions(int count) {
        Integer[] possibleAnswerIds = generateIndices(count, chemFlashCardManager.count());

        String[] possibleAnswers = new String[count];
        for (int i = 0; i < count; ++i) {
            possibleAnswers[i] = chemFlashCardManager.getAnswer(possibleAnswerIds[i]);
        }

        Integer[] answers = generateIndices(count, count);

        // construct the list of questions to be used
        Question[] questions = new Question[count];
        for (int i = 0; i < count; ++i) {
            int answerId = possibleAnswerIds[answers[i]];
            String answer = chemFlashCardManager.getAnswer(answerId);
            Bitmap image = chemFlashCardManager.getBitmap(answerId);
            Question question = new Question(answer, image, possibleAnswers);
            questions[i] = question;
        }

        return questions;
    }

    private static Integer[] generateIndices(int count, int bound) {

        // randomly select a set of unique indices
        Random random = new Random();
        List<Integer> selections = new ArrayList<>();
        while (selections.size() < count) {
            int selection = random.nextInt(bound);
            if (selections.contains(selection)) continue;

            selections.add(selection);
        }

        // convert the set into a primitive array
        Integer[] results = new Integer[count];
        selections.toArray(results);
        return results;
    }


}
