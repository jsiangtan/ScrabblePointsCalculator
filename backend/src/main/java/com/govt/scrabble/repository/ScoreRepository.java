package com.govt.scrabble.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.govt.scrabble.entity.ScoreRecord;

@Repository
public interface ScoreRepository extends JpaRepository<ScoreRecord, Long> {
    Optional<ScoreRecord> findByWordIgnoreCase(String word);
}