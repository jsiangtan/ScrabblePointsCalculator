package com.govt.scrabble.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.govt.scrabble.model.ScoreRecord;
import com.govt.scrabble.model.ScoreRequest;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ScoreControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static Path tempStorageFile;

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) throws IOException {
        tempStorageFile = Files.createTempFile("scores", ".json");

        // write initial records to the temp file
        ObjectMapper mapper = new ObjectMapper();
        List<ScoreRecord> records = List.of(
            new ScoreRecord("HELLO", 8),
            new ScoreRecord("EXCITING", 18)
        );
        String json = mapper.writeValueAsString(records);
        Files.writeString(tempStorageFile, json);

        registry.add("app.storage.path", () -> tempStorageFile.toAbsolutePath().toString());
    }

    @AfterAll
    static void cleanup() throws IOException {
        Files.deleteIfExists(tempStorageFile);
    }
    
    @Test
    void testScoreWord_ReturnsScoreResponse() throws Exception {
        ScoreRequest request = new ScoreRequest("HELLO");

        mockMvc.perform(post("/api/score")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.word").value("HELLO"))
                .andExpect(jsonPath("$.score").value(8));
    }

    @Test
    void testSaveScore_Success() throws Exception {
        ScoreRequest request = new ScoreRequest("WORLD");

        mockMvc.perform(post("/api/score/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Score saved successfully"));
    }

    @Test
    void testSaveScore_InvalidInput_ReturnsBadRequest() throws Exception {
        ScoreRequest request = new ScoreRequest("");

        mockMvc.perform(post("/api/score/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid input: empty word"));
    }

    @Test
    void testSaveScore_DuplicatedRecord_ReturnsBadRequest() throws Exception {
        ScoreRequest request = new ScoreRequest("HELLO");

        mockMvc.perform(post("/api/score/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Record already exists"));
    }

    @Test
    void testTopScores_ReturnsList() throws Exception {
        String json = mockMvc.perform(get("/api/score/top"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        List<ScoreRecord> records = objectMapper.readValue(
            json, new TypeReference<List<ScoreRecord>>() {}
        );    

        assertEquals("EXCITING", records.get(0).getWord());
        assertEquals(18, records.get(0).getScore());
        assertEquals("HELLO", records.get(1).getWord());
        assertEquals(8, records.get(1).getScore());
    }

    @Test
    void testTopScores_Limit_1_ReturnsList() throws Exception {
        String json = mockMvc.perform(get("/api/score/top?limit=1"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        List<ScoreRecord> records = objectMapper.readValue(
            json, new TypeReference<List<ScoreRecord>>() {}
        );    

        assertTrue(records.size() == 1);
        assertEquals("EXCITING", records.get(0).getWord());
        assertEquals(18, records.get(0).getScore());
    }
}
