package com.govt.scrabble.repository;

import com.govt.scrabble.entity.ScoreRecord;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class ScoreRepositoryTest {
    @Autowired
    private ScoreRepository scoreRepository;

    @BeforeEach
    void setUp() {
        scoreRepository.save(new ScoreRecord("HELLO", 8));
        scoreRepository.save(new ScoreRecord("WORLD", 9));
    }

    @AfterEach
    void delete() {
        scoreRepository.deleteAll();
    }

    @Test
    void findByWordIgnoreCase_ReturnsRecord_WhenWordExistsRegardlessOfCase() {
        Optional<ScoreRecord> resultLower = scoreRepository.findByWordIgnoreCase("hello");
        Optional<ScoreRecord> resultMixed = scoreRepository.findByWordIgnoreCase("HeLLo");

        assertTrue(resultLower.isPresent());
        assertTrue(resultMixed.isPresent());

        ScoreRecord record = resultLower.get();
        assertEquals("HELLO", record.getWord());
        assertEquals(8, record.getScore());
    }

    @Test
    void findByWordIgnoreCase_ReturnsEmpty_WhenWordDoesNotExist() {
        Optional<ScoreRecord> result = scoreRepository.findByWordIgnoreCase("SCRABBLE");
        assertTrue(result.isEmpty());
    }

    @Test
    void findByWordIgnoreCase_DoesNotMatchPartialWords() {
        Optional<ScoreRecord> result = scoreRepository.findByWordIgnoreCase("HELL");
        assertTrue(result.isEmpty());
    }
}
