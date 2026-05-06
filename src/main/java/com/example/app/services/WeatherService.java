package com.example.app.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Fetches real-time weather via Open-Meteo (free, no API key required).
 * Step 1 – Geocode city name → lat/lon via Open-Meteo Geocoding API.
 * Step 2 – Get current weather code + stats via Open-Meteo Forecast API.
 * Maps WMO weather codes → WeatherCondition with combat stat multipliers.
 */
public class WeatherService {

    private static final String GEO_URL =
        "https://geocoding-api.open-meteo.com/v1/search?name=%s&count=1&language=fr&format=json";
    private static final String WEATHER_URL =
        "https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s"
        + "&current=weather_code,temperature_2m,wind_speed_10m,precipitation&wind_speed_unit=kmh&timezone=auto";

    private static final Gson gson = new Gson();

    // ── Weather Conditions ────────────────────────────────────────────────────
    public enum WeatherCondition {
        CLEAR      ("☀️ Ciel Dégagé",      "#f1c40f", 1.00, 1.00, 1.00,
                    "Conditions parfaites — aucun modificateur"),
        CLOUDY     ("☁️ Couvert",           "#95a5a6", 0.95, 1.00, 1.05,
                    "Ciel lourd — Défense +5%"),
        DRIZZLE    ("🌦️ Bruine",           "#74b9ff", 0.95, 0.90, 1.05,
                    "Sol légèrement humide — Agilité -10%, Défense +5%"),
        FOG        ("🌫️ Brouillard Épais", "#dfe6e9", 0.88, 0.75, 1.00,
                    "Visibilité nulle — Agilité -25%, 20% chance de rater l'attaque"),
        RAIN       ("🌧️ Pluie",            "#3498db", 0.90, 0.85, 1.10,
                    "Sol glissant — Agilité -15%, Magie +10% (eau conduit la magie)"),
        STORM      ("⛈️ Orage Électrique", "#8e44ad", 1.25, 0.80, 0.90,
                    "Foudre divine — Magie ×1.25, Défense -10% (armures conductrices)"),
        SNOW       ("❄️ Blizzard",         "#a8e6cf", 0.82, 0.65, 1.25,
                    "Terrain gelé — Agilité -35%, Défense +25% (gel des blessures)"),
        WIND       ("🌬️ Vents Violents",   "#fd79a8", 0.88, 0.82, 1.00,
                    "Rafales déstabilisantes — Attaque -12%, Agilité -18%");

        public final String label, color, effect;
        /** Multiplier on total attack output */
        public final double atkMult;
        /** Multiplier on agility contribution to attack */
        public final double agilityMult;
        /** Multiplier on defense score */
        public final double defenseMult;

        WeatherCondition(String label, String color,
                         double atkMult, double agilityMult, double defenseMult, String effect) {
            this.label = label; this.color = color; this.effect = effect;
            this.atkMult = atkMult; this.agilityMult = agilityMult; this.defenseMult = defenseMult;
        }

        /** True if this condition applies a random miss chance during combat. */
        public boolean hasMissChance() { return this == FOG; }
        /** Miss probability (0.0–1.0). */
        public double missChance()     { return this == FOG ? 0.20 : 0.0; }
    }

    // ── Arena Weather DTO ────────────────────────────────────────────────────
    public static class ArenaWeather {
        public final String cityName;
        public final WeatherCondition condition;
        public final double temperature;   // °C
        public final double windSpeed;     // km/h
        public final double precipitation; // mm

        public ArenaWeather(String cityName, WeatherCondition condition,
                            double temperature, double windSpeed, double precipitation) {
            this.cityName      = cityName;
            this.condition     = condition;
            this.temperature   = temperature;
            this.windSpeed     = windSpeed;
            this.precipitation = precipitation;
        }

        /** Neutral default — used before the player loads a city. */
        public static ArenaWeather defaultWeather() {
            return new ArenaWeather("Arène Neutre", WeatherCondition.CLEAR, 20.0, 5.0, 0.0);
        }
    }

    // ── Public API ────────────────────────────────────────────────────────────
    /**
     * Fetches current weather for {@code cityName}.
     * Throws Exception with a user-readable message if the city is not found
     * or the network is unavailable.
     */
    public static ArenaWeather fetchWeather(String cityName) throws Exception {
        // 1) Geocode
        String geoUrl = String.format(GEO_URL,
            URLEncoder.encode(cityName.trim(), StandardCharsets.UTF_8));
        String geoJson = httpGet(geoUrl);
        JsonObject geoObj = gson.fromJson(geoJson, JsonObject.class);

        if (!geoObj.has("results") || geoObj.getAsJsonArray("results").size() == 0)
            throw new Exception("Ville introuvable : \"" + cityName + "\"");

        JsonObject loc       = geoObj.getAsJsonArray("results").get(0).getAsJsonObject();
        double lat           = loc.get("latitude").getAsDouble();
        double lon           = loc.get("longitude").getAsDouble();
        String resolvedName  = loc.get("name").getAsString();
        // Append country if available
        if (loc.has("country")) resolvedName += ", " + loc.get("country").getAsString();

        // 2) Fetch weather
        String weatherUrl  = String.format(WEATHER_URL, lat, lon);
        String weatherJson = httpGet(weatherUrl);
        JsonObject weatherObj = gson.fromJson(weatherJson, JsonObject.class);
        JsonObject current    = weatherObj.getAsJsonObject("current");

        int    weatherCode  = current.get("weather_code").getAsInt();
        double temperature  = current.get("temperature_2m").getAsDouble();
        double windSpeed    = current.get("wind_speed_10m").getAsDouble();
        double precipitation= current.get("precipitation").getAsDouble();

        WeatherCondition condition = mapCode(weatherCode, windSpeed);
        return new ArenaWeather(resolvedName, condition, temperature, windSpeed, precipitation);
    }

    // ── WMO Code Mapping ─────────────────────────────────────────────────────
    private static WeatherCondition mapCode(int code, double windKmh) {
        WeatherCondition base;
        if      (code == 0)          base = WeatherCondition.CLEAR;
        else if (code <= 3)          base = WeatherCondition.CLOUDY;
        else if (code <= 48)         base = WeatherCondition.FOG;
        else if (code <= 57)         base = WeatherCondition.DRIZZLE;
        else if (code <= 67)         base = WeatherCondition.RAIN;
        else if (code <= 77)         base = WeatherCondition.SNOW;
        else if (code <= 82)         base = WeatherCondition.RAIN;
        else if (code <= 86)         base = WeatherCondition.SNOW;
        else                         base = WeatherCondition.STORM; // 95-99 thunderstorm / hail

        // Very high wind on a clear/cloudy day → override to WIND
        if (windKmh > 45 && (base == WeatherCondition.CLEAR || base == WeatherCondition.CLOUDY))
            base = WeatherCondition.WIND;

        return base;
    }

    // ── HTTP Helper ───────────────────────────────────────────────────────────
    private static String httpGet(String urlStr) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "CullingGamesApp/1.0");
        conn.setConnectTimeout(10_000);
        conn.setReadTimeout(15_000);
        conn.connect();
        try (InputStream is = conn.getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
