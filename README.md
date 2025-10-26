Scrabble Points Calculator

Author: Tan Joo Siang

This repo contains two apps:

- **backend**: Spring Boot (Java) REST API that calculates Scrabble scores.
    * Provides endpoints to calculate a word’s score, save scores, and retrieve top scores.
    * **Endpoints:**
        1. `POST /api/score`
           - **Request body:** 
             ```json
             { "word": "HELLO" }
             ```
           - **Response:** 
             ```json
             { 
               "word": "HELLO", 
               "score": 8, 
               "breakdown": [
                 {"letter":"H","value":4}, 
                 {"letter":"E","value":1}, 
                 {"letter":"L","value":1}, 
                 {"letter":"L","value":1}, 
                 {"letter":"O","value":1}
               ] 
             }
             ```
           - Calculates the Scrabble score for a given word.

        2. `POST /api/score/save`
           - **Request body:** 
             ```json
             { "word": "HELLO" }
             ```
           - **Response (success):** 
             ```json
             { "message": "Score saved successfully" }
             ```
           - **Response (error):** 
             ```json
             { "error": "Invalid input: empty word" }
             ```
             or 
             ```json
             { "error": "Record already exists" }
             ```
           - Validates the input, calculates the score, and stores it if it’s not a duplicate.

        3. `GET /api/score/top?limit={n}`
           - **Query param:** `limit` (optional, default `10`)
           - **Response:** A list of top scored words, e.g.:
             ```json
             [
               { "word": "EXCITING", "score": 18 },
               { "word": "HELLO", "score": 8 }
             ]
             ```

- **frontend**: React app (Vite + Ant Design) to enter a word and get its Scrabble score.
    * **Features:**
        1. **Word input tiles**
            - 10 single-character input boxes for entering letters.
            - Automatically moves focus to the next tile as you type.
            - Supports clearing all tiles using the **Reset Tiles** button.

        2. **Live scoring**
            - Calculates the Scrabble score for the current word as you type.
            - Shows a total score and per-letter breakdown (score for each letter).

        3. **Save score**
            - Saves the current word and its score to the backend using **Save Score** button.
            - Displays success or error messages depending on the result.
            - Prevents saving empty words or duplicate scores.

        4. **Top scores**
            - Fetches and displays the top scores from the backend using **View Top Scores** button.
            - Shows a modal with the top 10 scores by default.

---

**Prerequisites:**  Java 17, Gradle (or use the Gradle wrapper), Node 18+ (and npm/yarn/pnpm).

1) Start backend (runs on port 8080):

```bash
cd ScrabblePointsCalculator/backend
./gradlew bootRun
```

2) Start frontend (Vite dev server on port 5173):

```bash
cd ScrabblePointsCalculator/frontend
npm install
npm run dev
```

The frontend calls the backend at http://localhost:8080/api/score
