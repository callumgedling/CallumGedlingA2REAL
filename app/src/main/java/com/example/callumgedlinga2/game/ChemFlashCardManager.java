package com.example.callumgedlinga2.game;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

public class ChemFlashCardManager {
    private final String assetPath;
    private String[] flashCardAnswers;
    private final AssetManager assetManager;

    public ChemFlashCardManager(AssetManager assetManager, String assetPath) {
        this.assetPath = assetPath;
        this.assetManager = assetManager;
        try {
            flashCardAnswers = assetManager.list(assetPath);

        } catch (IOException e) {
            flashCardAnswers = null;
        }
    }

    public int count() {
        return flashCardAnswers.length;
    }

    public String getAnswer(int i) {
        return flashCardAnswers[i]
                .replaceAll("\\.JPG", "")
                .replaceAll("-", " ")
                .trim();
    }

    Bitmap getBitmap(int i) {
        try {
            String path = assetPath + "/" + flashCardAnswers[i];
            InputStream stream = assetManager.open(path);
            Bitmap result = BitmapFactory.decodeStream(stream);
            stream.close();
            return result;
        } catch (IOException e) {
            System.out.println("Error caused by: " + e);
            return null;
        }
    }
}

