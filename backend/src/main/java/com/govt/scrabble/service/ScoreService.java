package com.govt.scrabble.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.govt.scrabble.model.ScoreRecord;
import com.govt.scrabble.model.ScoreResponse;

@Service
public class ScoreService {

    @Value("${app.storage.path}")
    private String storagePath;

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

    public void saveScore(String word, int score) {
        try {
            List<ScoreRecord> records = readAll();
            String norm = word == null ? "" : word.trim().toUpperCase();
            boolean exists = records.stream().anyMatch(r -> {
                String rw = r.getWord() == null ? "" : r.getWord().trim().toUpperCase();
                return rw.equals(norm) && r.getScore() == score;
            });
            if (exists) throw new IllegalArgumentException("Record already exists");
            records.add(new ScoreRecord(word, score));
            writeAll(records);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ScoreRecord> topScores(int limit) {
        try {
            List<ScoreRecord> records = readAll();
            return records.stream()
                    .sorted(Comparator.comparingInt(ScoreRecord::getScore).reversed())
                    .limit(limit)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<ScoreRecord> readAll() throws IOException {
        File f = Path.of(storagePath).toFile();
        if (!f.exists()) return new ArrayList<>();
        String json = Files.readString(Path.of(storagePath));
        ObjectMapper om = new ObjectMapper();
        try {
            return om.readValue(json, new TypeReference<List<ScoreRecord>>() {});
        } catch (IOException e) {
            // if parsing fails, return empty list
            return new ArrayList<>();
        }
    }

    private void writeAll(List<ScoreRecord> records) throws IOException {
        File dir = Path.of(storagePath).getParent().toFile();
        if (!dir.exists()) dir.mkdirs();
        ObjectMapper om = new ObjectMapper();
        String json = om.writerWithDefaultPrettyPrinter().writeValueAsString(records);
        Files.writeString(Path.of(storagePath), json);
    }
}
