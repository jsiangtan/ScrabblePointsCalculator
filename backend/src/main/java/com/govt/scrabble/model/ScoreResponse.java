package com.govt.scrabble.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreResponse {
    private String word;
    private int score;
    private List<LetterScore> breakdown;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LetterScore {
        private char letter;
        private int value;
    }
}
