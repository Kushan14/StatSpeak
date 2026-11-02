package com.statspeak;

import com.statspeak.analysis.TextProcessor;
import com.statspeak.model.AnalysisResult;
import com.statspeak.model.FeedbackEntry;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TextProcessorTest {

    @Test
    void testTokenizeAndClean() {
        TextProcessor processor = new TextProcessor();
        List<String> stopWords = processor.getDefaultStopWords();

        List<String> tokens = processor.tokenizeAndClean("The course was GREAT and very clear!", stopWords);

        assertTrue(tokens.contains("course"));
        assertTrue(tokens.contains("great"));
        assertTrue(tokens.contains("clear"));
        assertFalse(tokens.contains("the"));
        assertFalse(tokens.contains("was"));
        assertFalse(tokens.contains("and"));
    }

    @Test
    void testAnalyzeFeedback() {
        TextProcessor processor = new TextProcessor();
        List<String> stopWords = processor.getDefaultStopWords();

        List<FeedbackEntry> entries = List.of(
            new FeedbackEntry("Great course and great professor", "CS101"),
            new FeedbackEntry("Good course with clear material", "MATH101")
        );

        AnalysisResult result = processor.analyzeFeedback(entries, stopWords);

        assertNotNull(result);
        assertTrue(result.getTotalWords() > 0);
        assertTrue(result.getUniqueKeywords() > 0);
        assertEquals(2, result.getKeywordFrequencies().get("great"));
        assertEquals(1, result.getKeywordFrequencies().get("good"));
    }
}
