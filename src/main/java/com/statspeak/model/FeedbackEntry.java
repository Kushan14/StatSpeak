package com.statspeak.model;

public class FeedbackEntry {
    private final String feedbackText;
    private final String sourceInfo;

    public FeedbackEntry(String feedbackText, String sourceInfo) {
        this.feedbackText = feedbackText;
        this.sourceInfo = sourceInfo;
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public String getSourceInfo() {
        return sourceInfo;
    }

    @Override
    public String toString() {
        return "FeedbackEntry{sourceInfo='" + sourceInfo + "', feedbackText='" +
               (feedbackText != null ? feedbackText.substring(0, Math.min(50, feedbackText.length())) + "..." : "null") + "'}'";
    }
}