package com.govt.scrabble.validator;

import com.govt.scrabble.model.ScoreRequest;


public class ScoreRequestValidator {

    public static void validate(ScoreRequest request) {
        if (request == null || request.getWord() == null || request.getWord().trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid input: empty word");
        }

        if (!request.getWord().matches("^[A-Za-z-]+$")) {
            throw new IllegalArgumentException("Word must contain only letters A-Z");
        }
    }
}