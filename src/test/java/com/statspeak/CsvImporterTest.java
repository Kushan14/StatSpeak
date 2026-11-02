package com.statspeak;

import com.statspeak.importdata.CSVDataImporter;
import com.statspeak.model.FeedbackEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CsvImporterTest {

    @Test
    void testImportValidCSV(@TempDir Path tempDir) throws IOException {
        File csvFile = tempDir.resolve("test_feedback.csv").toFile();
        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.write("CS101,Great class and excellent lectures\n");
            writer.write("MATH202,Exams were challenging but fair\n");
        }

        CSVDataImporter importer = new CSVDataImporter();
        CSVDataImporter.ImportResult result = importer.importCSV(csvFile);

        assertEquals(2, result.getEntries().size());
        assertEquals(2, result.getTotalRecordsProcessed());
        assertEquals(0, result.getSkippedRecords());

        FeedbackEntry first = result.getEntries().get(0);
        assertEquals("CS101", first.getSourceInfo());
        assertEquals("Great class and excellent lectures", first.getFeedbackText());
    }

    @Test
    void testImportWithEmptyLines(@TempDir Path tempDir) throws IOException {
        File csvFile = tempDir.resolve("test_empty.csv").toFile();
        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.write("CS101,Good material\n");
            writer.write("\n");
            writer.write("PHYS101,Lab was fun\n");
        }

        CSVDataImporter importer = new CSVDataImporter();
        CSVDataImporter.ImportResult result = importer.importCSV(csvFile);

        assertEquals(2, result.getEntries().size());
        assertEquals(3, result.getTotalRecordsProcessed());
        assertEquals(1, result.getSkippedRecords());
    }
}
