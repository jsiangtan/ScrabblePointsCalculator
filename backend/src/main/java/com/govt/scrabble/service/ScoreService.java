package com.govt.scrabble.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.govt.scrabble.entity.ScoreRecord;
import com.govt.scrabble.model.ScoreResponse;
import com.govt.scrabble.repository.ScoreRepository;

import jakarta.transaction.Transactional;

@Service
public class ScoreService {

    private final ScoreRepository repository;

    @Autowired
    public ScoreService(ScoreRepository repository) {
        this.repository = repository;
    }

    private static final Map<Character, Integer> SCORES = createScoreMap();

    private static Map<Character, Integer> createScoreMap() {
        Map<Character, Integer> m = new HashMap<>();
        String one = "AEIOULNSTR";
        String two = "DG";
        String three = "BCMP";
        String four = "FHVWY";
        String six = "K";
        String eight = "JX";
        String ten = "QZ";
        for (char c : one.toCharArray()) m.put(c, 1);
        for (char c : two.toCharArray()) m.put(c, 2);
        for (char c : three.toCharArray()) m.put(c, 3);
        for (char c : four.toCharArray()) m.put(c, 4);
        for (char c : six.toCharArray()) m.put(c, 6);
        for (char c : eight.toCharArray()) m.put(c, 8);
        for (char c : ten.toCharArray()) m.put(c, 10);
        return m;
    }

    public ScoreResponse scoreWord(String word) {
        if (word == null) word = "";
        int total = 0;
        List<ScoreResponse.LetterScore> breakdown = new ArrayList<>();
        for (char c : word.toUpperCase(Locale.ROOT).toCharArray()) {
            if (!Character.isLetter(c)) continue;
            int val = SCORES.getOrDefault(c, 0);
            total += val;
            breakdown.add(new ScoreResponse.LetterScore(c, val));
        }
        return new ScoreResponse(word, total, breakdown);
    }

    @Transactional
    public void saveScore(String word, int score) {
        repository.findByWordIgnoreCase(word).ifPresent(existing -> {
                throw new IllegalArgumentException("Record already exists");
            });
        repository.save(new ScoreRecord(word, score));
    }

    public List<ScoreRecord> topScores(int limit) {
        return repository.findAll().stream()
            .sorted(Comparator.comparingInt(ScoreRecord::getScore).reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }
}
