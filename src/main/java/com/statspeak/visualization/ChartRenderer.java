package com.statspeak.visualization;

import com.statspeak.model.AnalysisResult;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class ChartRenderer {

    public BufferedImage createHorizontalBarChart(AnalysisResult analysis, int width, int height) {
        int leftMargin = 120;
        int rightMargin = 80;
        int topMargin = 70;
        int bottomMargin = 50;

        int chartWidth = Math.max(100, width - leftMargin - rightMargin);
        int chartHeight = Math.max(100, height - topMargin - bottomMargin);

        BufferedImage image = new BufferedImage(Math.max(200, width), Math.max(200, height), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // Header Title
        g2d.setColor(new Color(30, 41, 59));
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2d.drawString("Top Keyword Frequency Distribution", 35, 38);

        g2d.setColor(new Color(100, 116, 139));
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2d.drawString("Bar length represents relative occurrences in analyzed student feedback", 35, 54);

        if (analysis == null || analysis.getKeywordFrequencies() == null || analysis.getKeywordFrequencies().isEmpty()) {
            g2d.setColor(new Color(148, 163, 184));
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            g2d.drawString("No keyword data available to display.", leftMargin, height / 2);
            g2d.dispose();
            return image;
        }

        Map<String, Integer> frequencies = analysis.getKeywordFrequencies();
        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(frequencies.entrySet());
        sortedList.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        int maxFrequency = sortedList.get(0).getValue();
        if (maxFrequency <= 0) maxFrequency = 1;

        // Draw soft vertical grid lines
        g2d.setColor(new Color(241, 245, 249));
        for (int i = 0; i <= 4; i++) {
            int gridX = leftMargin + (chartWidth * i / 4);
            g2d.drawLine(gridX, topMargin, gridX, topMargin + chartHeight);
            
            int freqVal = (int) Math.round((double) maxFrequency * i / 4);
            g2d.setColor(new Color(148, 163, 184));
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            String valStr = String.valueOf(freqVal);
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(valStr, gridX - (fm.stringWidth(valStr) / 2), topMargin + chartHeight + 20);
            g2d.setColor(new Color(241, 245, 249));
        }

        // Render Bars
        int barHeight = 26;
        int spacing = 12;
        int y = topMargin + 10;

        for (Map.Entry<String, Integer> entry : sortedList) {
            if (y + barHeight > topMargin + chartHeight) break;

            int val = entry.getValue();
            int barWidth = Math.max(6, (int) (((double) val / maxFrequency) * chartWidth));

            // Bar Gradient: Modern Indigo to Violet
            GradientPaint gradient = new GradientPaint(
                leftMargin, y, new Color(79, 70, 229),
                leftMargin + barWidth, y, new Color(124, 58, 237)
            );
            g2d.setPaint(gradient);
            g2d.fill(new RoundRectangle2D.Float(leftMargin, y, barWidth, barHeight, 10, 10));

            // Keyword Label on the Left
            g2d.setColor(new Color(51, 65, 85));
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
            FontMetrics labelFm = g2d.getFontMetrics();
            String kw = entry.getKey();
            int labelWidth = labelFm.stringWidth(kw);
            g2d.drawString(kw, leftMargin - labelWidth - 12, y + 18);

            // Frequency Badge Pill on the Right
            g2d.setColor(new Color(241, 245, 249));
            g2d.fillRoundRect(leftMargin + barWidth + 8, y + 3, 36, 20, 8, 8);
            
            g2d.setColor(new Color(79, 70, 229));
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 11));
            String countStr = String.valueOf(val);
            FontMetrics countFm = g2d.getFontMetrics();
            g2d.drawString(countStr, leftMargin + barWidth + 8 + (36 - countFm.stringWidth(countStr)) / 2, y + 17);

            y += barHeight + spacing;
        }

        g2d.dispose();
        return image;
    }

    public void saveChartToFile(BufferedImage chart, String filePath) throws IOException {
        File outputFile = new File(filePath);
        ImageIO.write(chart, "PNG", outputFile);
    }
}
