package com.statspeak.reports;

import com.statspeak.model.AnalysisResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class ReportsExporter {

    public void exportCSV(AnalysisResult analysis, File outputFile) throws IOException {
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write("Keyword,Frequency\n");

            for (Map.Entry<String, Integer> entry : analysis.getKeywordFrequencies().entrySet()) {
                writer.write(escapeCSV(entry.getKey()) + "," + entry.getValue() + "\n");
            }
        }
    }

    public void exportTextReport(AnalysisResult analysis, File outputFile) throws IOException {
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write("===============================================\n");
            writer.write("            STATSPEAK ANALYSIS REPORT           \n");
            writer.write("===============================================\n\n");

            writer.write("Summary Statistics:\n");
            writer.write("-------------------\n");
            writer.write("Total Words Analyzed: " + analysis.getTotalWords() + "\n");
            writer.write("Unique Keywords: " + analysis.getUniqueKeywords() + "\n");
            writer.write("Average Words/Entry: " + String.format("%.2f", analysis.getAverageWordCount()) + "\n\n");

            writer.write("Top Most Frequent Keywords:\n");
            writer.write("---------------------------\n");
            int rank = 1;
            for (String kw : analysis.getMostFrequentKeywords()) {
                writer.write(String.format("%2d. %s\n", rank++, kw));
            }

            writer.write("\n===============================================\n");
        }
    }

    private String escapeCSV(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
