package com.example.app.services;

/**
 * Holds the display data for a single post (oeuvre, artefact, personnage, or universe).
 * Mirrors the {{ postDetails.* }} variables used in the Twig template.
 */
public class PostDetails {

    private final String typeLabel;
    private final String title;
    private final String description;

    public PostDetails(String typeLabel, String title, String description) {
        this.typeLabel   = typeLabel;
        this.title       = title;
        this.description = description;
    }

    /** e.g. "Œuvre", "Artefact", "Personnage", "Univers" */
    public String getTypeLabel()   { return typeLabel; }

    public String getTitle()       { return title; }

    /** May be null — mirrors |default('') in Twig. */
    public String getDescription() { return description; }
}