package com.statspeak.reports;

import com.statspeak.model.AnalysisResult;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PdfReportExporter {

    public void exportPDF(AnalysisResult analysis, File outputFile) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("StatSpeak - Student Feedback Analysis Report");
                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 710);
                contentStream.showText("Total Words Analyzed: " + analysis.getTotalWords());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Unique Keywords: " + analysis.getUniqueKeywords());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Average Words per Entry: " + String.format("%.2f", analysis.getAverageWordCount()));
                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 630);
                contentStream.showText("Top Keywords:");
                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 11);
                int yOffset = 600;
                List<String> topKeywords = analysis.getMostFrequentKeywords();
                int limit = Math.min(15, topKeywords.size());

                for (int i = 0; i < limit; i++) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(60, yOffset);
                    contentStream.showText((i + 1) + ". " + topKeywords.get(i));
                    contentStream.endText();
                    yOffset -= 18;
                }
            }

            document.save(outputFile);
        }
    }
}
