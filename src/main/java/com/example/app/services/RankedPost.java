package com.example.app.services;

import java.util.Map;

public class RankedPost {

    private final String type;
    private final int id;
    private final String tag;
    private final double score;
    private final Map<String, Double> scoreBreakdown;

    public RankedPost(String type, int id, String tag, double score, Map<String, Double> scoreBreakdown) {
        this.type = type;
        this.id = id;
        this.tag = tag;
        this.score = score;
        this.scoreBreakdown = scoreBreakdown;
    }

    public String getType() { return type; }
    public int getId() { return id; }
    public String getTag() { return tag; }
    public double getScore() { return score; }
    public Map<String, Double> getScoreBreakdown() { return scoreBreakdown; }

    @Override
    public String toString() {
        return "RankedPost{" +
                "type='" + type + '\'' +
                ", id=" + id +
                ", tag='" + tag + '\'' +
                ", score=" + score +
                ", scoreBreakdown=" + scoreBreakdown +
                '}';
    }
}
