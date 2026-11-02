package com.statspeak.model;

import java.util.List;
import java.util.Map;

public class AnalysisResult {
    private final Map<String, Integer> keywordFrequencies;
    private final List<String> mostFrequentKeywords;
    private final double averageWordCount;
    private final int totalWords;
    private final int uniqueKeywords;

    public AnalysisResult(Map<String, Integer> keywordFrequencies,
                          List<String> mostFrequentKeywords,
                          double averageWordCount,
                          int totalWords,
                          int uniqueKeywords) {
        this.keywordFrequencies = keywordFrequencies;
        this.mostFrequentKeywords = mostFrequentKeywords;
        this.averageWordCount = averageWordCount;
        this.totalWords = totalWords;
        this.uniqueKeywords = uniqueKeywords;
    }

    public Map<String, Integer> getKeywordFrequencies() {
        return keywordFrequencies;
    }

    public List<String> getMostFrequentKeywords() {
        return mostFrequentKeywords;
    }

    public double getAverageWordCount() {
        return averageWordCount;
    }

    public int getTotalWords() {
        return totalWords;
    }

    public int getUniqueKeywords() {
        return uniqueKeywords;
    }

    @Override
    public String toString() {
        return "AnalysisResult{totalWords=" + totalWords + ", uniqueKeywords=" + uniqueKeywords +
               ", avgWordsPerEntry=" + String.format("%.1f", averageWordCount) + "}";
    }
}