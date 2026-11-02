package com.statspeak.importdata;

import com.statspeak.model.FeedbackEntry;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVDataImporter {

    public static class ImportResult {
        private final List<FeedbackEntry> entries;
        private final int totalRecordsProcessed;
        private final int skippedRecords;
        private final List<String> parsingIssues;

        public ImportResult(List<FeedbackEntry> entries, int totalRecordsProcessed,
                          int skippedRecords, List<String> parsingIssues) {
            this.entries = entries;
            this.totalRecordsProcessed = totalRecordsProcessed;
            this.skippedRecords = skippedRecords;
            this.parsingIssues = parsingIssues;
        }

        public List<FeedbackEntry> getEntries() {
            return entries;
        }

        public int getTotalRecordsProcessed() {
            return totalRecordsProcessed;
        }

        public int getSkippedRecords() {
            return skippedRecords;
        }

        public List<String> getParsingIssues() {
            return parsingIssues;
        }

        @Override
        public String toString() {
            return "ImportResult{totalRecords=" + totalRecordsProcessed + ", skipped=" + skippedRecords + ", issues=" + parsingIssues.size() + "}";
        }
    }

    public ImportResult importCSV(File csvFile) throws IOException {
        List<FeedbackEntry> entries = new ArrayList<>();
        int totalRecordsProcessed = 0;
        int skippedRecords = 0;
        List<String> parsingIssues = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                totalRecordsProcessed++;

                if (line.trim().isEmpty()) {
                    skippedRecords++;
                    parsingIssues.add("Empty line at line " + lineNumber);
                    continue;
                }

                FeedbackEntry entry = parseLine(line, lineNumber);
                if (entry != null) {
                    entries.add(entry);
                } else {
                    skippedRecords++;
                    parsingIssues.add("Failed to parse line " + lineNumber + ": " + line.substring(0, Math.min(50, line.length())));
                }
            }
        }

        return new ImportResult(entries, totalRecordsProcessed, skippedRecords, parsingIssues);
    }

    private FeedbackEntry parseLine(String line, int lineNumber) {
        String[] parts = line.split(",");

        if (parts.length < 2) {
            return null;
        }

        String sourceInfo = parts[0].trim();
        StringBuilder feedbackText = new StringBuilder();

        for (int i = 1; i < parts.length; i++) {
            if (i > 1) {
                feedbackText.append(" ");
            }
            feedbackText.append(parts[i].trim());
        }

        if (feedbackText.toString().trim().isEmpty()) {
            return null;
        }

        return new FeedbackEntry(feedbackText.toString(), sourceInfo);
    }
}
