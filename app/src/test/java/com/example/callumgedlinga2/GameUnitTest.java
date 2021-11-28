package com.example.callumgedlinga2;

import org.junit.Test;

import com.example.callumgedlinga2.game.ChemFlashCardManager;
import com.example.callumgedlinga2.game.Difficulty;
import com.example.callumgedlinga2.game.Game;
import com.example.callumgedlinga2.game.Question;
import com.example.callumgedlinga2.game.GameBuilder;

import static org.junit.Assert.*;

public class GameUnitTest {
    @Test
    public void checkQuestion(){
        String[] possibleAnswers = {"Atom", "Proton", "Electron"};
        Question question = new Question("Atom", null, possibleAnswers);

        assertTrue("check failed to work for correct guess",question.check("Atom"));
        assertFalse("check failed to work for incorrect guess", question.check(("Proton")));
    }


    @Test
    public void testGame(){
        Question[] questions = new Question[3];
        String[] answers = {"atom", "Proton", "Electron"};
        for (int i = 0; i < 3; i ++){
            questions[i] = new Question(answers[i], null, answers);
        }

        Game game = new Game(questions);

        while (!game.isGameOver()){
            Question question = game.next();
            game.updateScore(question.check("atom"));
        }
        assertEquals("Score: 1/3", game.getScore());
    }

}