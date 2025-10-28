package com.govt.scrabble.service;

import com.govt.scrabble.entity.ScoreRecord;
import com.govt.scrabble.repository.ScoreRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class ScoreServiceTest {
    @Autowired
    private ScoreService scoreService;

    @Autowired
    private ScoreRepository repository;

    @AfterEach
    void delete() {
        repository.deleteAll();
    }

    @ParameterizedTest(name = "Word: {0} should score {1}")
    @CsvSource({
        "SCRABBLE, 14", 
        "QZ, 20",
        "A-B-C, 7",
    })
    void scoreWord_CalculatesCorrectTotalScore(String word, int expectedScore) {
        assertEquals(expectedScore, scoreService.scoreWord(word).getScore());
    }

    @Test
    void saveScore_SavesNewRecordToFile() throws IOException {
        String word = "HELLO";
        int score = 8; 

        scoreService.saveScore(word, score);

        List<ScoreRecord> records = repository.findAll();
        
        assertEquals(1, records.size());
        assertEquals(word, records.get(0).getWord());
        assertEquals(score, records.get(0).getScore());
    }

    @Test
    void saveScore_ThrowsCorrectMessage_WhenRecordAlreadyExists() throws IOException {
        String word = "DUPE";
        int score = 7; 
        
        repository.save(new ScoreRecord(word, score));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            scoreService.saveScore(word, score);
        });

        assertEquals("Record already exists", thrown.getMessage());
    }

    @Test
    @Transactional
    void topScores_ReturnsSortedListWithLimit() throws IOException {
        List<ScoreRecord> initialRecords = Arrays.asList(
            new ScoreRecord("LOW", 6),    
            new ScoreRecord("MED", 10),   
            new ScoreRecord("HIGH", 20)
        );

        repository.saveAll(initialRecords);
        int limit = 2;
        
        List<ScoreRecord> topList = scoreService.topScores(limit);
        
        assertEquals(limit, topList.size());
        
        List<Integer> actualScores = topList.stream().map(ScoreRecord::getScore).collect(Collectors.toList());
        assertEquals(Arrays.asList(20, 10), actualScores);
    }
    
    @Test
    void topScores_ReturnsEmptyList_WhenFileIsClean() {
        List<ScoreRecord> topList = scoreService.topScores(5);
        assertTrue(topList.isEmpty());
    }
}
