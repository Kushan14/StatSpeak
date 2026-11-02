package com.statspeak.ui;

import com.statspeak.analysis.TextProcessor;
import com.statspeak.importdata.CSVDataImporter;
import com.statspeak.model.FeedbackEntry;
import com.statspeak.model.AnalysisResult;
import com.statspeak.reports.ReportsExporter;
import com.statspeak.visualization.ChartRenderer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class StatisticDashboard extends JFrame {

    private JLabel statusLabel;
    private JLabel fileLabel;
    private JTable rankingsTable;
    private JTable rawDataTable;
    private ResultsTableModel rankingsTableModel;
    private RawDataTableModel rawDataTableModel;
    private JPanel chartPanel;
    private JButton importButton, analyzeButton, exportButton;

    private JLabel kpiTotalRecords, kpiTotalWords, kpiUniqueKeywords, kpiTopKeyword;

    private List<FeedbackEntry> loadedEntries;
    private AnalysisResult currentAnalysis;

    public StatisticDashboard() {
        setTitle("StatSpeak — Student Feedback Quantifier");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1240, 840);
        setMinimumSize(new Dimension(1000, 700));
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(248, 250, 252)); // Slate 50

        loadedEntries = new java.util.ArrayList<>();

        // 1. Header Toolbar
        add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. Main Tabbed Content Area
        add(createMainContent(), BorderLayout.CENTER);

        // 3. Modern Status Bar
        add(createStatusBar(), BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    private JPanel createHeaderPanel() {
        JPanel headerWrapper = new JPanel(new BorderLayout(0, 10));
        headerWrapper.setBackground(Color.WHITE);
        headerWrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
            new EmptyBorder(16, 24, 16, 24)
        ));

        // Left Branding
        JPanel brandPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        brandPanel.setOpaque(false);

        JLabel appTitle = new JLabel("StatSpeak");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        appTitle.setForeground(new Color(15, 23, 42)); // Slate 900

        JLabel appSubtitle = new JLabel("Student Feedback Quantifier & Sentiment Analytics");
        appSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        appSubtitle.setForeground(new Color(100, 116, 139)); // Slate 500

        brandPanel.add(appTitle);
        brandPanel.add(appSubtitle);
        headerWrapper.add(brandPanel, BorderLayout.WEST);

        // Right Actions Toolbar
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);

        importButton = new JButton("Import CSV");
        importButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        importButton.setPreferredSize(new Dimension(130, 38));
        importButton.putClientProperty("JButton.buttonType", "roundRect");
        importButton.addActionListener(this::handleImport);

        analyzeButton = new JButton("Run Analysis");
        analyzeButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        analyzeButton.setPreferredSize(new Dimension(140, 38));
        analyzeButton.setBackground(new Color(79, 70, 229)); // Indigo 600
        analyzeButton.setForeground(Color.WHITE);
        analyzeButton.putClientProperty("JButton.buttonType", "roundRect");
        analyzeButton.setEnabled(false);
        analyzeButton.addActionListener(this::handleAnalyze);

        exportButton = new JButton("Export Reports");
        exportButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        exportButton.setPreferredSize(new Dimension(140, 38));
        exportButton.putClientProperty("JButton.buttonType", "roundRect");
        exportButton.setEnabled(false);
        exportButton.addActionListener(this::handleExport);

        actionsPanel.add(importButton);
        actionsPanel.add(analyzeButton);
        actionsPanel.add(exportButton);
        headerWrapper.add(actionsPanel, BorderLayout.EAST);

        // Bottom File Indicator
        fileLabel = new JLabel("No file currently loaded — click 'Import CSV' to get started");
        fileLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        fileLabel.setForeground(new Color(100, 116, 139));
        headerWrapper.add(fileLabel, BorderLayout.SOUTH);

        return headerWrapper;
    }

    private JPanel createMainContent() {
        JPanel main = new JPanel(new BorderLayout(0, 16));
        main.setBackground(new Color(248, 250, 252));
        main.setBorder(new EmptyBorder(16, 24, 16, 24));

        // 1. KPI Cards Row
        main.add(createKPIPanel(), BorderLayout.NORTH);

        // 2. Tabbed Content Container
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.setBackground(Color.WHITE);

        // Tab 1: Keyword Rankings
        rankingsTableModel = new ResultsTableModel();
        rankingsTable = createStyledTable(rankingsTableModel);
        setupRankingsColumns();
        JScrollPane rankingsScroll = new JScrollPane(rankingsTable);
        rankingsScroll.setBorder(BorderFactory.createEmptyBorder());
        tabs.addTab("  Keyword Rankings  ", rankingsScroll);

        // Tab 2: Visual Charts
        chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (currentAnalysis != null) {
                    ChartRenderer renderer = new ChartRenderer();
                    BufferedImage img = renderer.createHorizontalBarChart(currentAnalysis, getWidth(), getHeight());
                    g.drawImage(img, 0, 0, null);
                } else {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                    g2d.setColor(new Color(148, 163, 184));
                    g2d.drawString("Import student feedback and click 'Run Analysis' to generate charts.", 40, 60);
                }
            }
        };
        chartPanel.setBackground(Color.WHITE);
        tabs.addTab("  Visual Charts  ", chartPanel);

        // Tab 3: Raw Feedback Records
        rawDataTableModel = new RawDataTableModel();
        rawDataTable = createStyledTable(rawDataTableModel);
        setupRawDataColumns();
        JScrollPane rawScroll = new JScrollPane(rawDataTable);
        rawScroll.setBorder(BorderFactory.createEmptyBorder());
        tabs.addTab("  Raw Feedback Records  ", rawScroll);

        main.add(tabs, BorderLayout.CENTER);
        return main;
    }

    private JPanel createKPIPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 16, 0));
        panel.setOpaque(false);

        kpiTotalRecords = createStatCard("TOTAL RECORDS", "0", new Color(59, 130, 246));
        kpiTotalWords = createStatCard("TOTAL WORDS", "0", new Color(16, 185, 129));
        kpiUniqueKeywords = createStatCard("UNIQUE KEYWORDS", "0", new Color(245, 158, 11));
        kpiTopKeyword = createStatCard("TOP KEYWORD", "N/A", new Color(139, 92, 246));

        panel.add(kpiTotalRecords);
        panel.add(kpiTotalWords);
        panel.add(kpiUniqueKeywords);
        panel.add(kpiTopKeyword);
        return panel;
    }

    private JLabel createStatCard(String title, String value, Color accentColor) {
        JLabel card = new JLabel();
        card.setOpaque(true);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(14, 18, 14, 18)
            )
        ));
        updateCardText(card, title, value);
        return card;
    }

    private void updateCardText(JLabel card, String title, String value) {
        card.setText("<html><div style='padding: 2px;'>" +
                     "<span style='color: #64748B; font-size: 10px; font-weight: bold; letter-spacing: 1px; font-family: Segoe UI;'>" + title + "</span><br/>" +
                     "<span style='color: #0F172A; font-size: 20px; font-weight: bold; font-family: Segoe UI;'>" + value + "</span>" +
                     "</div></html>");
    }

    private JTable createStyledTable(AbstractTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(36);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowGrid(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(241, 245, 249));
        table.setSelectionBackground(new Color(224, 231, 255));
        table.setSelectionForeground(new Color(15, 23, 42));

        // Header Styling
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(248, 250, 252));
        table.getTableHeader().setForeground(new Color(71, 85, 105));
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        return table;
    }

    private void setupRankingsColumns() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        rankingsTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        rankingsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        rankingsTable.getColumnModel().getColumn(1).setPreferredWidth(300);

        rankingsTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        rankingsTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        rankingsTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        rankingsTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
    }

    private void setupRawDataColumns() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        rawDataTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        rawDataTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        rawDataTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        rawDataTable.getColumnModel().getColumn(2).setPreferredWidth(600);
    }

    private JPanel createStatusBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        JLabel dot = new JLabel("\u2022"); // Unicode bullet
        dot.setForeground(new Color(34, 197, 94)); // Emerald green
        panel.add(dot);

        statusLabel = new JLabel("System Ready. Please import a student feedback CSV to begin.");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(71, 85, 105));
        panel.add(statusLabel);

        return panel;
    }

    private void handleImport(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Student Feedback CSV File");

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            importCSV(selectedFile);
        }
    }

    private void importCSV(File csvFile) {
        importButton.setEnabled(false);
        importButton.setText("Importing...");

        SwingUtilities.invokeLater(() -> {
            try {
                CSVDataImporter importer = new CSVDataImporter();
                CSVDataImporter.ImportResult result = importer.importCSV(csvFile);

                loadedEntries = result.getEntries();
                fileLabel.setText("Active File: " + csvFile.getAbsolutePath() + " (" + loadedEntries.size() + " records)");

                updateStatus("Imported " + loadedEntries.size() + " records from " + csvFile.getName() + " (" + result.getSkippedRecords() + " skipped).");

                analyzeButton.setEnabled(!loadedEntries.isEmpty());
                exportButton.setEnabled(false);
                currentAnalysis = null;

                rawDataTableModel.updateData(loadedEntries);
                rankingsTableModel.updateData(null);
                updateKPIs(loadedEntries.size(), 0, 0, "—");
                chartPanel.repaint();

            } catch (Exception ex) {
                updateStatus("Error importing file: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, "Failed to import CSV file:\n" + ex.getMessage(),
                                              "Import Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                importButton.setEnabled(true);
                importButton.setText("Import CSV");
            }
        });
    }

    private void handleAnalyze(ActionEvent e) {
        if (loadedEntries == null || loadedEntries.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please import feedback records first.", "No Data", JOptionPane.WARNING_MESSAGE);
            return;
        }

        analyzeButton.setEnabled(false);
        analyzeButton.setText("Analyzing...");

        SwingUtilities.invokeLater(() -> {
            try {
                TextProcessor processor = new TextProcessor();
                List<String> stopWords = processor.getDefaultStopWords();
                currentAnalysis = processor.analyzeFeedback(loadedEntries, stopWords);

                rankingsTableModel.updateData(currentAnalysis);
                chartPanel.repaint();

                int totalWords = currentAnalysis.getTotalWords();
                int uniqueWords = currentAnalysis.getUniqueKeywords();
                String topKw = "—";
                List<String> topList = currentAnalysis.getMostFrequentKeywords();
                if (!topList.isEmpty()) {
                    topKw = topList.get(0);
                    Integer count = currentAnalysis.getKeywordFrequencies().get(topKw);
                    if (count != null) {
                        topKw = topKw + " (" + count + ")";
                    }
                }

                updateKPIs(loadedEntries.size(), totalWords, uniqueWords, topKw);
                updateStatus("Analysis complete! Found " + uniqueWords + " unique keywords across " + totalWords + " words.");
                exportButton.setEnabled(true);

            } catch (Exception ex) {
                updateStatus("Error during analysis: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, "Analysis failed:\n" + ex.getMessage(),
                                              "Analysis Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                analyzeButton.setEnabled(true);
                analyzeButton.setText("Run Analysis");
            }
        });
    }

    private void handleExport(ActionEvent e) {
        if (currentAnalysis == null) {
            updateStatus("No analysis results available to export.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Analysis Report");
        fileChooser.setSelectedFile(new File("statspeak_report.csv"));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File exportFile = fileChooser.getSelectedFile();
            exportReports(exportFile);
        }
    }

    private void exportReports(File file) {
        exportButton.setEnabled(false);
        exportButton.setText("Exporting...");

        SwingUtilities.invokeLater(() -> {
            try {
                ReportsExporter exporter = new ReportsExporter();
                exporter.exportCSV(currentAnalysis, file);

                File txtFile = new File(file.getAbsolutePath().replaceAll("(?i)\\.csv$", "") + "_report.txt");
                exporter.exportTextReport(currentAnalysis, txtFile);

                updateStatus("Reports exported successfully to " + file.getName() + " and " + txtFile.getName());
                JOptionPane.showMessageDialog(this, "Reports successfully exported!\n\n" +
                                              "• CSV: " + file.getAbsolutePath() + "\n" +
                                              "• Text: " + txtFile.getAbsolutePath(),
                                              "Export Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                updateStatus("Error exporting reports: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, "Export failed:\n" + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                exportButton.setEnabled(true);
                exportButton.setText("Export Reports");
            }
        });
    }

    private void updateStatus(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        statusLabel.setText("[" + timestamp + "] " + message);
    }

    private void updateKPIs(int records, int words, int unique, String topKw) {
        updateCardText(kpiTotalRecords, "TOTAL RECORDS", String.valueOf(records));
        updateCardText(kpiTotalWords, "TOTAL WORDS", String.valueOf(words));
        updateCardText(kpiUniqueKeywords, "UNIQUE KEYWORDS", String.valueOf(unique));
        updateCardText(kpiTopKeyword, "TOP KEYWORD", topKw);
    }

    private class ResultsTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Rank", "Keyword", "Frequency", "Percentage"};
        private Object[][] data = new Object[0][4];

        public void updateData(AnalysisResult analysis) {
            if (analysis == null || analysis.getKeywordFrequencies() == null) {
                data = new Object[0][4];
            } else {
                Map<String, Integer> freqMap = analysis.getKeywordFrequencies();
                int totalWords = analysis.getTotalWords();

                List<Map.Entry<String, Integer>> sortedEntries = new java.util.ArrayList<>(freqMap.entrySet());
                sortedEntries.sort((a, b) -> b.getValue().compareTo(a.getValue()));

                data = new Object[sortedEntries.size()][4];
                for (int i = 0; i < sortedEntries.size(); i++) {
                    Map.Entry<String, Integer> entry = sortedEntries.get(i);
                    data[i][0] = "#" + (i + 1);
                    data[i][1] = entry.getKey();
                    data[i][2] = entry.getValue();
                    double pct = totalWords > 0 ? (entry.getValue() * 100.0) / totalWords : 0.0;
                    data[i][3] = String.format("%.2f%%", pct);
                }
            }
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return data.length; }
        @Override public int getColumnCount() { return columnNames.length; }
        @Override public String getColumnName(int col) { return columnNames[col]; }
        @Override public Object getValueAt(int row, int col) { return data[row][col]; }
        @Override public boolean isCellEditable(int r, int c) { return false; }
    }

    private class RawDataTableModel extends AbstractTableModel {
        private final String[] columnNames = {"#", "Course / Source Info", "Feedback Text"};
        private List<FeedbackEntry> entries = new java.util.ArrayList<>();

        public void updateData(List<FeedbackEntry> entries) {
            this.entries = entries != null ? entries : new java.util.ArrayList<>();
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return entries.size(); }
        @Override public int getColumnCount() { return columnNames.length; }
        @Override public String getColumnName(int col) { return columnNames[col]; }
        @Override public Object getValueAt(int row, int col) {
            FeedbackEntry entry = entries.get(row);
            if (col == 0) return row + 1;
            if (col == 1) return entry.getSourceInfo();
            return entry.getFeedbackText();
        }
        @Override public boolean isCellEditable(int r, int c) { return false; }
    }
}
