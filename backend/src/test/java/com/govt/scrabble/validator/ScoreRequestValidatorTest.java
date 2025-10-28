package com.govt.scrabble.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.govt.scrabble.model.ScoreRequest;

public class ScoreRequestValidatorTest {

    @Test
    void validate_ThrowsException_WhenRequestIsNull() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> 
            ScoreRequestValidator.validate(null),
            "Should throw IllegalArgumentException when request is null"
        );

        assertEquals("Invalid input: empty word", thrown.getMessage());
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  \t  "})
    void validate_ThrowsException_WhenWordIsNullOrEmptyOrBlank(String word) {
        ScoreRequest request = new ScoreRequest(word);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> 
            ScoreRequestValidator.validate(request),
            "Should throw IllegalArgumentException when word is null, empty, or blank"
        );

        assertEquals("Invalid input: empty word", thrown.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"scrabble!", "word1", "a b c", "test#ing", "123-word"})
    void validate_ThrowsException_WhenWordContainsInvalidCharacters(String invalidWord) {
        ScoreRequest request = new ScoreRequest(invalidWord);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> 
            ScoreRequestValidator.validate(request),
            "Should throw IllegalArgumentException for non-letter/hyphen characters"
        );

        assertEquals("Word must contain only letters A-Z", thrown.getMessage());
    }
}
