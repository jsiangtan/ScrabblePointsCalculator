package com.govt.scrabble.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.govt.scrabble.entity.ScoreRecord;
import com.govt.scrabble.model.ScoreRequest;
import com.govt.scrabble.model.ScoreResponse;
import com.govt.scrabble.service.ScoreService;
import com.govt.scrabble.validator.ScoreRequestValidator;

@RestController
@RequestMapping("/api")
public class ScoreController {

    private final ScoreService service;

    @Autowired
    public ScoreController(ScoreService service) {
        this.service = service;
    }

    @PostMapping("/score")
    public ScoreResponse score(@RequestBody ScoreRequest request) {
        return service.scoreWord(request.getWord());
    }

    @PostMapping("/score/save")
    public ResponseEntity<Map<String, String>> save(@RequestBody ScoreRequest request) {
        try {
            ScoreRequestValidator.validate(request);
            ScoreResponse scoreRes = service.scoreWord(request.getWord());
            service.saveScore(scoreRes.getWord(), scoreRes.getScore());
            return ResponseEntity.ok(Map.of("message", "Score saved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/score/top")
    public List<ScoreRecord> top(@RequestParam(value = "limit", defaultValue = "10") int limit) {
        return service.topScores(limit);
    }
}
