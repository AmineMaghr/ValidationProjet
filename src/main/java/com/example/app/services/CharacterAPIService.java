package com.example.app.services;

import com.example.app.entities.Personnage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.FileWriter;
import java.io.FileReader;
import java.util.List;

public class CharacterAPIService {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String exportCharacterAsJSON(Personnage p) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", p.getId());
        obj.addProperty("name", p.getName());
        obj.addProperty("classRole", p.getClassRole());
        obj.addProperty("historyContext", p.getHistoryContext());
        obj.addProperty("abilitiesPowers", p.getAbilitiesPowers());
        obj.addProperty("strength", p.getStrength());
        obj.addProperty("agility", p.getAgility());
        obj.addProperty("magic", p.getMagic());
        obj.addProperty("defense", p.getDefense());
        obj.addProperty("universe", p.getUniverse() != null ? p.getUniverse().getName() : null);
        obj.addProperty("createdAt", p.getCreatedAt() != null ? p.getCreatedAt().toString() : null);
        return gson.toJson(obj);
    }

    public static String exportCharactersAsJSON(List<Personnage> personnages) {
        JsonArray arr = new JsonArray();
        for (Personnage p : personnages) arr.add(gson.fromJson(exportCharacterAsJSON(p), JsonObject.class));
        return gson.toJson(arr);
    }

    /**
     * Save character to JSON file
     */
    public static void saveCharacterToFile(Personnage personnage, String filepath) throws Exception {
        try (FileWriter writer = new FileWriter(filepath)) {
            gson.toJson(personnage, writer);
        }
    }

    /**
     * Load character from JSON file
     */
    public static Personnage loadCharacterFromFile(String filepath) throws Exception {
        try (FileReader reader = new FileReader(filepath)) {
            return gson.fromJson(reader, Personnage.class);
        }
    }

    /**
     * Import JSON string as character object
     */
    public static Personnage importCharacterFromJSON(String jsonString) {
        return gson.fromJson(jsonString, Personnage.class);
    }
}
