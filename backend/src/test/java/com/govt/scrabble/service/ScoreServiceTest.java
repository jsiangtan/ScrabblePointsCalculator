package com.govt.scrabble.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.govt.scrabble.model.ScoreRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class ScoreServiceTest {
    @Autowired
    private ScoreService scoreService;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${app.storage.path}") 
    private String storagePath;
    
    @BeforeEach
    void resetFile() throws IOException {
        Path testFilePath = Path.of(storagePath);
        
        Path parentDir = testFilePath.getParent();
        if (parentDir != null) {
            Files.createDirectories(parentDir);
        }
        
        Files.writeString(testFilePath, "[]");
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

        Path testFilePath = Path.of(storagePath);
        assertTrue(Files.exists(testFilePath));
        String jsonContent = Files.readString(testFilePath);
        
        List<ScoreRecord> records = objectMapper.readValue(
            jsonContent, 
            objectMapper.getTypeFactory().constructCollectionType(List.class, ScoreRecord.class)
        );
        
        assertEquals(1, records.size());
        assertEquals(word, records.get(0).getWord());
        assertEquals(score, records.get(0).getScore());
    }

    @Test
    void saveScore_ThrowsCorrectMessage_WhenRecordAlreadyExists() throws IOException {
        String word = "DUPE";
        int score = 7; 
        
        writeInitialScores(List.of(new ScoreRecord(word, score)));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            scoreService.saveScore(word, score);
        });

        assertEquals("Record already exists", thrown.getMessage());
    }

    @Test
    void topScores_ReturnsSortedListWithLimit() throws IOException {
        List<ScoreRecord> initialRecords = Arrays.asList(
            new ScoreRecord("LOW", 6),    
            new ScoreRecord("MED", 10),   
            new ScoreRecord("HIGH", 20)
        );
        
        writeInitialScores(initialRecords);
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
    
    private void writeInitialScores(List<ScoreRecord> records) throws IOException {
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(records);
        Files.writeString(Path.of(storagePath), json);
    }
}
