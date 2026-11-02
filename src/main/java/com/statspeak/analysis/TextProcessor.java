package com.statspeak.analysis;

import com.statspeak.model.FeedbackEntry;
import com.statspeak.model.AnalysisResult;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class TextProcessor {

    private static final Pattern PUNCTUATION_PATTERN = Pattern.compile("[^a-zA-Z\s]");
    private static final Pattern MULTIPLE_SPACES = Pattern.compile("\s+");

    public AnalysisResult analyzeFeedback(List<FeedbackEntry> entries, List<String> stopWords) {
        Map<String, Integer> keywordFrequencies = new HashMap<>();
        List<Integer> wordCountsPerEntry = new ArrayList<>();
        int totalWords = 0;

        for (FeedbackEntry entry : entries) {
            List<String> words = tokenizeAndClean(entry.getFeedbackText(), stopWords);
            int wordCount = words.size();
            wordCountsPerEntry.add(wordCount);
            totalWords += wordCount;

            for (String word : words) {
                keywordFrequencies.put(word, keywordFrequencies.getOrDefault(word, 0) + 1);
            }
        }

        List<String> mostFrequentKeywords = getTopKeywords(keywordFrequencies);
        double averageWordCount = entries.isEmpty() ? 0 : (double) totalWords / entries.size();
        int uniqueKeywords = keywordFrequencies.size();

        return new AnalysisResult(keywordFrequencies, mostFrequentKeywords, averageWordCount,
                                 totalWords, uniqueKeywords);
    }

    public List<String> tokenizeAndClean(String text, List<String> stopWords) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String cleanText = PUNCTUATION_PATTERN.matcher(text).replaceAll(" ").toLowerCase();
        String normalizedText = MULTIPLE_SPACES.matcher(cleanText).replaceAll(" ").trim();

        List<String> words = new ArrayList<>();
        for (String word : normalizedText.split(" ")) {
            if (word.isEmpty()) continue;

            if (!isStopWord(word, stopWords) && !word.isEmpty()) {
                words.add(word);
            }
        }

        return words;
    }

    private boolean isStopWord(String word, List<String> stopWords) {
        return stopWords != null && stopWords.contains(word.toLowerCase());
    }

    private List<String> getTopKeywords(Map<String, Integer> frequencies) {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(frequencies.entrySet());

        entries.sort(Comparator.comparing(Map.Entry<String, Integer>::getValue, Comparator.reverseOrder())
                          .thenComparing(Map.Entry<String, Integer>::getKey));

        List<String> topKeywords = new ArrayList<>();
        int maxDisplay = Math.min(20, entries.size());

        for (int i = 0; i < maxDisplay; i++) {
            topKeywords.add(entries.get(i).getKey() + " (" + entries.get(i).getValue() + ")");
        }

        return topKeywords;
    }

    public List<String> getDefaultStopWords() {
        List<String> defaultStopWords = new ArrayList<>();
        defaultStopWords.add("the");
        defaultStopWords.add("and");
        defaultStopWords.add("is");
        defaultStopWords.add("in");
        defaultStopWords.add("to");
        defaultStopWords.add("a");
        defaultStopWords.add("of");
        defaultStopWords.add("for");
        defaultStopWords.add("that");
        defaultStopWords.add("on");
        defaultStopWords.add("with");
        defaultStopWords.add("was");
        defaultStopWords.add("were");
        defaultStopWords.add("be");
        defaultStopWords.add("have");
        defaultStopWords.add("has");
        defaultStopWords.add("had");
        defaultStopWords.add("but");
        defaultStopWords.add("or");
        defaultStopWords.add("as");
        defaultStopWords.add("at");
        defaultStopWords.add("by");
        defaultStopWords.add("it");
        defaultStopWords.add("its");
        defaultStopWords.add("this");
        defaultStopWords.add("they");
        defaultStopWords.add("them");
        defaultStopWords.add("their");
        defaultStopWords.add("an");
        defaultStopWords.add("are");
        return defaultStopWords;
    }
}