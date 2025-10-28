package com.govt.scrabble.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "scores")
public class ScoreRecord {
    
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String word;

    @Column(nullable = false)
    private int score;

    public ScoreRecord(String word, Integer score) {
        this.word = word;
        this.score = score;
    }
}
