package com.statspeak;

import com.statspeak.model.AnalysisResult;
import com.statspeak.reports.PdfReportExporter;
import com.statspeak.reports.ReportsExporter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ReportsExporterTest {

    @Test
    void testExportCsvAndTextReport(@TempDir Path tempDir) throws IOException {
        AnalysisResult analysis = new AnalysisResult(
            Map.of("lectures", 5, "exams", 3),
            List.of("lectures (5)", "exams (3)"),
            12.5,
            25,
            2
        );

        ReportsExporter exporter = new ReportsExporter();

        File csvFile = tempDir.resolve("report.csv").toFile();
        exporter.exportCSV(analysis, csvFile);
        assertTrue(csvFile.exists());
        assertTrue(csvFile.length() > 0);

        String csvContent = Files.readString(csvFile.toPath());
        assertTrue(csvContent.contains("Keyword,Frequency"));
        assertTrue(csvContent.contains("lectures,5"));

        File txtFile = tempDir.resolve("report.txt").toFile();
        exporter.exportTextReport(analysis, txtFile);
        assertTrue(txtFile.exists());
        assertTrue(txtFile.length() > 0);

        String txtContent = Files.readString(txtFile.toPath());
        assertTrue(txtContent.contains("STATSPEAK ANALYSIS REPORT"));
        assertTrue(txtContent.contains("Total Words Analyzed: 25"));
    }

    @Test
    void testPdfExport(@TempDir Path tempDir) throws IOException {
        AnalysisResult analysis = new AnalysisResult(
            Map.of("lectures", 5, "exams", 3),
            List.of("lectures (5)", "exams (3)"),
            12.5,
            25,
            2
        );

        PdfReportExporter pdfExporter = new PdfReportExporter();
        File pdfFile = tempDir.resolve("report.pdf").toFile();
        pdfExporter.exportPDF(analysis, pdfFile);

        assertTrue(pdfFile.exists());
        assertTrue(pdfFile.length() > 0);
    }
}
