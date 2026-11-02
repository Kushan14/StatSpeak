package com.statspeak.app;

import com.formdev.flatlaf.FlatLightLaf;
import com.statspeak.ui.StatisticDashboard;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Configure FlatLaf modern UI properties
        try {
            UIManager.put("Component.arc", 12);
            UIManager.put("Button.arc", 12);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("ScrollBar.showButtons", false);
            UIManager.put("ScrollBar.width", 10);
            UIManager.put("TabbedPane.showTabSeparators", true);
            UIManager.put("TabbedPane.tabHeight", 36);
            UIManager.put("TabbedPane.selectedBackground", Color.WHITE);
            UIManager.put("Table.rowHeight", 34);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);
            UIManager.put("Table.gridColor", new Color(241, 245, 249));
            UIManager.put("Table.selectionBackground", new Color(224, 231, 255));
            UIManager.put("Table.selectionForeground", new Color(30, 41, 59));

            FlatLightLaf.setup();
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf look and feel: " + e.getMessage());
        }

        // Launch the Swing UI on the event dispatch thread
        SwingUtilities.invokeLater(() -> {
            StatisticDashboard dashboard = new StatisticDashboard();
            dashboard.setVisible(true);
        });
    }
}
