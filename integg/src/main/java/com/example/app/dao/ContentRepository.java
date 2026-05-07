package com.example.app.dao;

import com.example.app.controllers.DiscoverController.ContentItem;
import com.example.app.utils.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ContentRepository {

    public List<ContentItem> getAllUniverses() {
        return fetchContent("universe", "SELECT id, name, short_description as description, themes as tags, created_at FROM universe", "🌌");
    }

    public List<ContentItem> getAllOeuvres() {
        return fetchContent("oeuvre", "SELECT id, title as name, description, type as tags, created_at FROM oeuvres", "📖");
    }

    public List<ContentItem> getAllPersonnages() {
        return fetchContent("personnage", "SELECT id, name, history_context as description, class_role as tags, created_at FROM personnage", "🧍");
    }

    public List<ContentItem> getAllArtefacts() {
        return fetchContent("artefact", "SELECT id, name, origins as description, type as tags, created_at FROM artefacts", "⚔️");
    }

    public List<ContentItem> getAllContent() {
        List<ContentItem> all = new ArrayList<>();
        all.addAll(getAllUniverses());
        all.addAll(getAllOeuvres());
        all.addAll(getAllPersonnages());
        all.addAll(getAllArtefacts());
        return all;
    }

    private List<ContentItem> fetchContent(String type, String sql, String emoji) {
        List<ContentItem> items = new ArrayList<>();
        try {
             Connection conn = MyDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                String tags = rs.getString("tags");
                if (tags == null) tags = "";

                Timestamp createdAt = rs.getTimestamp("created_at");
                long date = (createdAt != null) ? createdAt.getTime() : System.currentTimeMillis();

                List<String> parsedGenres = new ArrayList<>();
                List<String> parsedThemes = new ArrayList<>();

                String lowerTags = tags.toLowerCase();
                String[] allGenres = {"highfantasy", "darkfantasy", "scifi", "urban"};
                for (String g : allGenres) {
                    if (lowerTags.contains(g)) {
                        parsedGenres.add(g);
                    }
                }

                String[] allThemes = {"magie", "guerre", "politique", "nature", "mystere", "aventure"};
                for (String t : allThemes) {
                    if (lowerTags.contains(t)) {
                        parsedThemes.add(t.substring(0, 1).toUpperCase() + t.substring(1));
                    }
                }

                ContentItem item = new ContentItem(
                        id, type, name, description, tags, 
                        parsedGenres, parsedThemes, date, 
                        0, 0, 0, 0, emoji, "admin", null
                );
                items.add(item);
            }
            
            rs.close();
            ps.close();

        } catch (Exception e) {
            System.err.println("Error fetching " + type + ": " + e.getMessage());
        }
        return items;
    }
}
