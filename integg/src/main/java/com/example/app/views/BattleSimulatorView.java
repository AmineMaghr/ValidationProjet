package com.example.app.views;

import com.example.app.entities.Personnage;
import com.example.app.services.ChatbotService;
import com.example.app.services.ImageGenerationService;
import com.example.app.services.PersonnageService;
import com.example.app.services.WeatherService;
import com.example.app.services.WeatherService.ArenaWeather;
import com.example.app.services.WeatherService.WeatherCondition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.*;
import java.util.stream.Collectors;

public class BattleSimulatorView extends VBox {

    // ── Colors ────────────────────────────────────────────────────────────────
    private static final String PRIMARY  = "#18E3A4";
    private static final String DANGER   = "#e74c3c";
    private static final String GOLD     = "#f1c40f";
    private static final String BLUE     = "#3498db";
    private static final String BG_MAIN  = "#1A1F1E";
    private static final String BG_DARK  = "#0D0F0F";
    private static final String BG_CARD  = "#0a0c0b";
    private static final String TEXT_PRI = "#E6FFF6";
    private static final String TEXT_SEC = "#B0B9B6";

    // ── Arena Phases ──────────────────────────────────────────────────────────
    enum ArenaPhase {
        BLOODBATH ("💀  BAIN DE SANG  —  La mêlée s'ouvre",    "#e74c3c", 1.5),
        SURVIVAL  ("🌿  SURVIE  —  L'arène se resserre",        "#27ae60", 1.0),
        FINAL_DUEL("⚡  DUEL FINAL  —  Que le meilleur gagne", "#f1c40f", 2.2);

        final String label, color; final double dmgMult;
        ArenaPhase(String l, String c, double m) { label=l; color=c; dmgMult=m; }
    }

    // ── Fighter Wrapper ───────────────────────────────────────────────────────
    static class Fighter {
        final Personnage p;
        int hp, maxHp;
        boolean alive = true;
        String allianceId;
        boolean allianceBroken = false;

        Fighter(Personnage p) {
            this.p     = p;
            this.maxHp = 60 + p.getDefense() * 2;
            this.hp    = maxHp;
            this.allianceId = (p.getUniverse() != null && p.getUniverse().getName() != null)
                ? p.getUniverse().getName() : "Solo_" + p.getId();
        }

        /**
         * Damage dealt to target, modified by arena phase AND current weather.
         * Weather affects: attack output (atkMult), agility contribution (agilityMult),
         * and target's effective defense (defenseMult).
         */
        int dealDamage(Fighter target, ArenaPhase phase, Random rng, ArenaWeather weather) {
            WeatherCondition w = weather.condition;
            // Agility contribution is scaled by weather.agilityMult (rain/snow reduce footing)
            double atk  = (p.getStrength() * 0.45
                        + p.getMagic()    * 0.30
                        + p.getAgility()  * 0.15 * w.agilityMult)
                        * w.atkMult;
            // Weather.defenseMult can raise defense (snow → thick cover) or lower it (storm → conductivity)
            double def  = target.p.getDefense() * 0.25 * w.defenseMult;
            double luck = 0.60 + rng.nextDouble() * 0.75;
            return (int) Math.max(5, (atk - def) * luck * phase.dmgMult);
        }

        double hpPercent() { return (double) hp / maxHp; }
    }

    // ── Arena Events ──────────────────────────────────────────────────────────
    private static final String[] ARENA_EVENTS = {
        "une tempête de feu ravage le secteur nord",
        "le sol s'effondre, révélant des pièges anciens",
        "des pluies acides s'abattent sur l'arène entière",
        "une arme légendaire apparaît au centre de la carte",
        "les Organisateurs lâchent des mutants sur la zone",
        "le brouillard de guerre aveugle tous les combattants",
        "une explosion de magie noire affaiblit les plus forts",
        "des drones Sponsor larguent des soins au hasard",
        "un séisme secoue le secteur est — rochers en chute libre",
        "une vague de poison force les combattants à avancer",
        "les règles changent : la magie est amplifiée ×2 ce round",
        "l'arène rétrécit — la zone sûre se réduit de moitié",
        "les Organisateurs déclenchent un Feast au Cornucopia",
        "une liaison satellite révèle la position de chaque tribut",
        "les eaux montent — les terrains bas sont inondés"
    };

    // ── UI State ──────────────────────────────────────────────────────────────
    private final List<Personnage>       allPersonnages;
    private final Set<Personnage>        selected     = new LinkedHashSet<>();
    private final Map<Integer, ProgressBar> hpBars   = new LinkedHashMap<>();
    private final Map<Integer, Label>    hpLabels     = new LinkedHashMap<>();
    private final Map<Integer, VBox>     statusCards  = new LinkedHashMap<>();

    private FlowPane   selectionPane, statusGrid;
    private VBox       feedBox;
    private ScrollPane feedScroll;
    private Button     launchBtn;
    private Label      selectedCount, phaseLabel, roundLabel;
    private ImageView  winnerImage;

    /** Live weather — starts neutral, updated when user loads a city. */
    private ArenaWeather currentWeather = ArenaWeather.defaultWeather();

    // ── Constructor ───────────────────────────────────────────────────────────
    public BattleSimulatorView() {
        this.setStyle("-fx-background-color: " + BG_DARK + ";");
        this.getChildren().add(new HeaderView());
        allPersonnages = load();

        VBox content = new VBox();
        content.setStyle("-fx-background-color: " + BG_DARK + ";");
        content.getChildren().addAll(buildHero(), buildBody());

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + BG_DARK + "; -fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        this.getChildren().add(scroll);
    }

    private List<Personnage> load() {
        try { return new PersonnageService().select(); }
        catch (Exception e) { return new ArrayList<>(); }
    }

    // ── Hero Banner ───────────────────────────────────────────────────────────
    private VBox buildHero() {
        VBox hero = new VBox(8);
        hero.setAlignment(Pos.CENTER);
        hero.setPadding(new Insets(40, 40, 20, 40));
        hero.setStyle("-fx-background-color: linear-gradient(to bottom, #030303, " + BG_DARK + ");");

        Label title = new Label("⚔️   CULLING GAMES");
        title.setFont(Font.font("System", FontWeight.BOLD, 54));
        title.setStyle("-fx-text-fill: " + PRIMARY + "; -fx-effect: dropshadow(gaussian, #18E3A4, 28, 0.7, 0, 0);");

        phaseLabel = new Label("Sélectionnez vos combattants · 2 à 8 tributs");
        phaseLabel.setStyle("-fx-text-fill: " + TEXT_SEC + "; -fx-font-size: 15px;");

        roundLabel = new Label("");
        roundLabel.setStyle("-fx-text-fill: " + GOLD + "; -fx-font-weight: bold; -fx-font-size: 20px;");

        winnerImage = new ImageView();
        winnerImage.setFitWidth(640);
        winnerImage.setFitHeight(230);
        winnerImage.setPreserveRatio(false);
        winnerImage.setVisible(false);

        hero.getChildren().addAll(title, phaseLabel, roundLabel, winnerImage);
        return hero;
    }

    // ── Body ──────────────────────────────────────────────────────────────────
    private HBox buildBody() {
        HBox body = new HBox(25);
        body.setPadding(new Insets(25));
        body.setAlignment(Pos.TOP_LEFT);
        body.getChildren().addAll(buildSelectionPanel(), buildArenaPanel());
        return body;
    }

    // ── LEFT: Tribute Selection + Weather ────────────────────────────────────
    private VBox buildSelectionPanel() {
        VBox panel = new VBox(15);
        panel.setPrefWidth(400);
        panel.setMinWidth(360);
        panel.setStyle("-fx-background-color: " + BG_MAIN + "; -fx-background-radius: 16px; -fx-padding: 20;");

        Label lbl = new Label("Tributs");
        lbl.setStyle("-fx-text-fill: " + PRIMARY + "; -fx-font-weight: bold; -fx-font-size: 20px;");

        selectedCount = new Label("Choisissez 2 à 8 combattants");
        selectedCount.setStyle("-fx-text-fill: " + TEXT_SEC + "; -fx-font-size: 12px;");

        selectionPane = new FlowPane(10, 10);
        selectionPane.setPrefWrapLength(375);

        if (allPersonnages.isEmpty()) {
            Label empty = new Label("Aucun personnage.\nCréez-en d'abord!");
            empty.setStyle("-fx-text-fill: " + TEXT_SEC + ";");
            selectionPane.getChildren().add(empty);
        } else {
            for (Personnage p : allPersonnages) selectionPane.getChildren().add(buildTributeCard(p));
        }

        // ── Weather Panel ─────────────────────────────────────────────────
        Separator wSep = new Separator();
        wSep.setStyle("-fx-background-color: " + PRIMARY + "44;");

        Label wTitle = new Label("🌍  Météo de l'Arène");
        wTitle.setStyle("-fx-text-fill: " + PRIMARY + "; -fx-font-weight: bold; -fx-font-size: 15px;");

        TextField cityField = new TextField();
        cityField.setPromptText("Entrez une ville (ex: Paris, Tokyo, London...)");
        cityField.setStyle("-fx-background-color: " + BG_DARK + "; -fx-text-fill: " + TEXT_PRI
            + "; -fx-prompt-text-fill: gray; -fx-border-color: " + PRIMARY + "66;"
            + " -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 8;");

        Button wBtn = new Button("🌦️  Charger");
        wBtn.setStyle("-fx-background-color: " + BLUE + "; -fx-text-fill: white; -fx-font-weight: bold;"
            + " -fx-background-radius: 8px; -fx-padding: 8 14; -fx-cursor: hand;");

        HBox cityRow = new HBox(8, cityField, wBtn);
        HBox.setHgrow(cityField, Priority.ALWAYS);

        Label wStatus = new Label("☀️  Météo neutre — entrez une ville pour activer les effets météo");
        wStatus.setStyle("-fx-text-fill: " + TEXT_SEC + "; -fx-font-size: 11px;");
        wStatus.setWrapText(true);

        // Live weather card (replaced after each load)
        VBox weatherCardHolder = new VBox();
        weatherCardHolder.getChildren().add(buildWeatherCard(currentWeather));

        wBtn.setOnAction(e -> {
            String city = cityField.getText().trim();
            if (city.isEmpty()) return;
            wBtn.setText("⏳");
            wBtn.setDisable(true);
            wStatus.setText("🔍  Récupération de la météo pour « " + city + " »…");
            new Thread(() -> {
                try {
                    ArenaWeather w = WeatherService.fetchWeather(city);
                    Platform.runLater(() -> {
                        currentWeather = w;
                        weatherCardHolder.getChildren().setAll(buildWeatherCard(w));
                        wStatus.setText("✅  Météo chargée — les effets sont actifs!");
                        wBtn.setText("🌦️  Charger");
                        wBtn.setDisable(false);
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        wStatus.setText("❌  Ville introuvable — vérifiez l'orthographe");
                        wBtn.setText("🌦️  Charger");
                        wBtn.setDisable(false);
                    });
                }
            }).start();
        });
        // Also trigger on Enter key
        cityField.setOnAction(e -> wBtn.fire());

        launchBtn = new Button("⚔️   OUVRIR LES JEUX");
        launchBtn.setPrefWidth(Double.MAX_VALUE);
        launchBtn.setDisable(true);
        launchBtn.setStyle(btnStyle(false));
        launchBtn.setOnAction(e -> startTournament());

        panel.getChildren().addAll(
            lbl, selectedCount, selectionPane,
            wSep, wTitle, cityRow, wStatus, weatherCardHolder,
            launchBtn
        );
        return panel;
    }

    /** Compact weather info card shown in the selection panel. */
    private VBox buildWeatherCard(ArenaWeather w) {
        WeatherCondition c = w.condition;
        VBox card = new VBox(5);
        card.setPadding(new Insets(12));
        card.setStyle("-fx-background-color: " + c.color + "18; -fx-border-color: " + c.color + "88;"
            + " -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label condLbl = new Label(c.label);
        condLbl.setStyle("-fx-text-fill: " + c.color + "; -fx-font-size: 17px; -fx-font-weight: bold;");

        Label cityLbl = new Label("📍 " + w.cityName + "   🌡️ " + (int) w.temperature + "°C");
        cityLbl.setStyle("-fx-text-fill: " + TEXT_PRI + "; -fx-font-size: 12px;");

        Label statsLbl = new Label("💨 " + (int) w.windSpeed + " km/h   🌧️ " + w.precipitation + " mm");
        statsLbl.setStyle("-fx-text-fill: " + TEXT_SEC + "; -fx-font-size: 11px;");

        // Stat modifier preview
        String modStr = buildModStr(c);
        Label modLbl = new Label("⚡ " + c.effect);
        modLbl.setStyle("-fx-text-fill: " + c.color + "; -fx-font-size: 11px; -fx-font-style: italic;");
        modLbl.setWrapText(true);

        card.getChildren().addAll(condLbl, cityLbl, statsLbl, modLbl);
        if (!modStr.isEmpty()) {
            Label rawMod = new Label(modStr);
            rawMod.setStyle("-fx-text-fill: " + TEXT_SEC + "; -fx-font-size: 10px;");
            card.getChildren().add(rawMod);
        }
        return card;
    }

    private String buildModStr(WeatherCondition c) {
        List<String> parts = new ArrayList<>();
        if (c.atkMult   != 1.0) parts.add("ATK " + pct(c.atkMult));
        if (c.agilityMult != 1.0) parts.add("AGI " + pct(c.agilityMult));
        if (c.defenseMult != 1.0) parts.add("DEF " + pct(c.defenseMult));
        if (c.hasMissChance()) parts.add("MISS " + (int)(c.missChance()*100) + "%");
        return parts.isEmpty() ? "" : String.join("  ·  ", parts);
    }

    private String pct(double mult) {
        int delta = (int) Math.round((mult - 1.0) * 100);
        return (delta > 0 ? "+" : "") + delta + "%";
    }

    private VBox buildTributeCard(Personnage p) {
        VBox card = new VBox(4);
        card.setPadding(new Insets(8));
        card.setPrefWidth(110);
        card.setStyle(cardStyle(false));

        ImageView img = buildPortrait(p, 95, 95);

        Label name = new Label(p.getName());
        name.setStyle("-fx-text-fill: " + TEXT_PRI + "; -fx-font-weight: bold; -fx-font-size: 10px;");
        name.setWrapText(true);

        String uniName = p.getUniverse() != null ? p.getUniverse().getName() : "Sans univers";
        Label uni = new Label("🌍 " + uniName);
        uni.setStyle("-fx-text-fill: " + BLUE + "; -fx-font-size: 9px;");
        uni.setWrapText(true);

        int avgPow = (p.getStrength() + p.getAgility() + p.getMagic() + p.getDefense()) / 4;
        int hp     = 60 + p.getDefense() * 2;
        Label stats = new Label("⚡" + avgPow + " avg  ❤️" + hp);
        stats.setStyle("-fx-text-fill: " + GOLD + "; -fx-font-size: 9px;");

        card.getChildren().addAll(img, name, uni, stats);
        card.setOnMouseClicked(e -> toggleSelection(p, card));
        return card;
    }

    private void toggleSelection(Personnage p, VBox card) {
        if (selected.contains(p)) {
            selected.remove(p);
            card.setStyle(cardStyle(false));
            card.setEffect(null);
        } else {
            if (selected.size() >= 8) return;
            selected.add(p);
            card.setStyle(cardStyle(true));
            card.setEffect(new DropShadow(10, Color.web(PRIMARY)));
        }
        int n = selected.size();
        selectedCount.setText(n + " sélectionné(s)"
            + (n < 2 ? " — min 2 requis" : n == 8 ? " — maximum" : " ✓"));
        launchBtn.setDisable(n < 2);
        launchBtn.setStyle(btnStyle(n >= 2));
    }

    // ── RIGHT: Arena Panel ────────────────────────────────────────────────────
    private VBox buildArenaPanel() {
        VBox panel = new VBox(15);
        panel.setPrefWidth(760);
        panel.setStyle("-fx-background-color: " + BG_MAIN + "; -fx-background-radius: 16px; -fx-padding: 20;");
        VBox.setVgrow(panel, Priority.ALWAYS);

        Label lbl = new Label("🏟️   Arène en Direct");
        lbl.setStyle("-fx-text-fill: " + PRIMARY + "; -fx-font-weight: bold; -fx-font-size: 20px;");

        statusGrid = new FlowPane(10, 10);
        statusGrid.setPrefWrapLength(730);
        statusGrid.setPadding(new Insets(12));
        statusGrid.setStyle("-fx-background-color: " + BG_CARD + "; -fx-background-radius: 12px;");
        statusGrid.setMinHeight(70);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: " + PRIMARY + "44;");

        feedBox = new VBox(8);
        feedBox.setPadding(new Insets(10));
        feedBox.getChildren().add(txt("Le tournoi n'a pas encore commencé...", TEXT_SEC, false));

        feedScroll = new ScrollPane(feedBox);
        feedScroll.setFitToWidth(true);
        feedScroll.setPrefHeight(430);
        feedScroll.setStyle("-fx-background: " + BG_CARD + "; -fx-background-color: " + BG_CARD
            + "; -fx-border-color: " + PRIMARY + "33; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        VBox.setVgrow(feedScroll, Priority.ALWAYS);

        panel.getChildren().addAll(lbl, statusGrid, sep, feedScroll);
        return panel;
    }

    // ── Tournament Start ──────────────────────────────────────────────────────
    private void startTournament() {
        launchBtn.setDisable(true);
        launchBtn.setText("⏳  Les jeux ont commencé...");
        feedBox.getChildren().clear();
        statusGrid.getChildren().clear();
        hpBars.clear(); hpLabels.clear(); statusCards.clear();
        winnerImage.setVisible(false);

        List<Fighter> fighters = selected.stream()
            .map(Fighter::new)
            .collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(fighters);

        for (Fighter f : fighters) buildStatusCard(f);

        // Announce participants
        feed("🔔  " + fighters.size() + " tributs entrent dans l'arène!", PRIMARY, true);
        feed(fighters.stream().map(f -> f.p.getName()).collect(Collectors.joining("  ·  ")), TEXT_SEC, false);
        feedDivider();

        // Announce alliances
        fighters.stream()
            .filter(f -> !f.allianceId.startsWith("Solo_"))
            .collect(Collectors.groupingBy(f -> f.allianceId))
            .forEach((uni, members) -> {
                if (members.size() > 1)
                    feed("🤝  Alliance [" + uni + "] : "
                        + members.stream().map(f -> f.p.getName()).collect(Collectors.joining(" & ")),
                        BLUE, false);
            });
        feedDivider();

        // ── Weather announcement ──────────────────────────────────────────
        ArenaWeather w = currentWeather;
        feed("🌍  CONDITIONS MÉTÉO : " + w.condition.label + "  —  " + w.cityName,
            w.condition.color, true);
        feed("   " + w.condition.effect, w.condition.color, false);
        if (w.condition != WeatherCondition.CLEAR) {
            feed("   🌡️ " + (int) w.temperature + "°C   💨 " + (int) w.windSpeed + " km/h   🌧️ " + w.precipitation + " mm",
                TEXT_SEC, false);
        }
        feedDivider();

        new Thread(new Task<Void>() {
            @Override protected Void call() { runCombat(fighters); return null; }
        }).start();
    }

    // ── Build Live Status Card ────────────────────────────────────────────────
    private void buildStatusCard(Fighter f) {
        VBox card = new VBox(3);
        card.setPadding(new Insets(6));
        card.setPrefWidth(100);
        card.setStyle("-fx-background-color: #111; -fx-background-radius: 8px;");

        ImageView img = buildPortrait(f.p, 86, 86);

        Label name = new Label(f.p.getName());
        name.setStyle("-fx-text-fill: " + TEXT_PRI + "; -fx-font-size: 10px; -fx-font-weight: bold;");
        name.setWrapText(true);

        ProgressBar pb = new ProgressBar(1.0);
        pb.setPrefWidth(Double.MAX_VALUE);
        pb.setPrefHeight(7);
        pb.setStyle("-fx-accent: #27ae60; -fx-control-inner-background: #333;");

        Label hpLbl = new Label(f.hp + "/" + f.maxHp);
        hpLbl.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 9px;");

        card.getChildren().addAll(img, name, pb, hpLbl);
        hpBars.put(f.p.getId(), pb);
        hpLabels.put(f.p.getId(), hpLbl);
        statusCards.put(f.p.getId(), card);

        Platform.runLater(() -> statusGrid.getChildren().add(card));
    }

    // ── Combat Engine ─────────────────────────────────────────────────────────
    private void runCombat(List<Fighter> fighters) {
        Random rng   = new Random();
        int    round = 1;
        List<String> allEliminated = new ArrayList<>();
        List<String> allEvents     = new ArrayList<>();
        ArenaWeather weather = currentWeather; // snapshot at start

        while (aliveCount(fighters) > 1) {
            ArenaPhase phase = phase(round, aliveCount(fighters));
            final int r = round;
            Platform.runLater(() -> {
                feedDivider();
                roundLabel.setText("ROUND  " + r);
                phaseLabel.setText(phase.label);
                phaseLabel.setStyle("-fx-text-fill: " + phase.color + "; -fx-font-weight: bold; -fx-font-size: 15px;");
            });
            feed("⚔️   " + phase.label + "  —  ROUND " + r, phase.color, true);

            // Weather reminder in final duel
            if (phase == ArenaPhase.FINAL_DUEL && weather.condition != WeatherCondition.CLEAR) {
                feed("🌍  " + weather.condition.label + " persiste — " + weather.condition.effect,
                    weather.condition.color, false);
            }
            pause(800);

            // Arena event
            String event = ARENA_EVENTS[rng.nextInt(ARENA_EVENTS.length)];
            allEvents.add(event);
            feed("🌪️   Événement : " + event, "#f39c12", false);
            pause(900);

            // Break alliances if no enemies remain
            checkAndBreakAlliances(fighters);
            pause(300);

            // Each alive fighter attacks a target
            List<Fighter> alive = alive(fighters);
            // We track per-attacker results for individual miss messages
            Map<Fighter, Integer> damageMap = new LinkedHashMap<>();

            for (Fighter attacker : alive) {
                Fighter target = pickTarget(attacker, alive, rng, phase);
                if (target == null) continue;

                // Fog miss chance — attacker-side, visible in feed
                if (weather.condition.hasMissChance()
                        && rng.nextDouble() < weather.condition.missChance()) {
                    feed("💨  " + attacker.p.getName() + " rate son attaque — le brouillard le désorie!",
                        "#95a5a6", false);
                    pause(200);
                    continue;
                }

                int dmg = attacker.dealDamage(target, phase, rng, weather);
                damageMap.merge(target, dmg, Integer::sum);
            }

            // Apply damage + collect eliminations
            List<Fighter> eliminated = new ArrayList<>();
            damageMap.forEach((target, totalDmg) -> {
                target.hp = Math.max(0, target.hp - totalDmg);
                if (target.hp <= 0) { target.alive = false; eliminated.add(target); }
            });

            // Update HP bars
            for (Fighter f : alive) refreshStatusCard(f);
            pause(600);

            // Show damage report
            for (Map.Entry<Fighter, Integer> entry : damageMap.entrySet()) {
                Fighter t   = entry.getKey();
                int     dmg = entry.getValue();
                boolean dead = !t.alive;
                feed((dead ? "💀  " : "🗡️  ") + t.p.getName()
                    + (dead ? "  —  ÉLIMINÉ!" : "  perd " + dmg + " HP  (reste " + t.hp + "/" + t.maxHp + ")"),
                    dead ? DANGER : TEXT_SEC, dead);
                pause(350);
            }

            // Cannon fire
            for (Fighter e : eliminated) {
                allEliminated.add(e.p.getName());
                feed("💥  COUP DE CANON  —  " + e.p.getName() + " est tombé!", DANGER, true);
                pause(500);
            }

            int remaining = aliveCount(fighters);
            feed("✅  " + remaining + " survivant(s) : "
                + alive(fighters).stream().map(f -> f.p.getName()).collect(Collectors.joining(", ")),
                PRIMARY, false);

            round++;
            pause(2200);
        }

        // ── Winner ────────────────────────────────────────────────────────
        Fighter winner = alive(fighters).get(0);
        feed("", GOLD, false);
        feed("🏆  VAINQUEUR : " + winner.p.getName().toUpperCase() + "!", GOLD, true);
        feed("Le dernier survivant des Culling Games.", TEXT_SEC, false);
        if (weather.condition != WeatherCondition.CLEAR)
            feed("🌍  Il a triomphé sous " + weather.condition.label + " — les éléments étaient contre lui!", weather.condition.color, false);

        Platform.runLater(() -> {
            phaseLabel.setText("🏆  " + winner.p.getName() + " a survécu!");
            phaseLabel.setStyle("-fx-text-fill: " + GOLD + "; -fx-font-weight: bold; -fx-font-size: 17px;");
            roundLabel.setText("");
        });

        // Winner AI portrait
        String imgUrl = ImageGenerationService.generateWinnerPortraitURL(
            winner.p.getName(), winner.p.getClassRole() != null ? winner.p.getClassRole() : "guerrier");
        new Thread(() -> {
            try {
                Image img = new Image(imgUrl, true);
                Platform.runLater(() -> { winnerImage.setImage(img); winnerImage.setVisible(true); });
            } catch (Exception ignored) {}
        }).start();

        pause(1200);

        // Gemini narration
        feed("📜  Gemini narre l'épopée complète...", TEXT_SEC, false);
        List<String> participantNames = fighters.stream().map(f -> f.p.getName()).collect(Collectors.toList());
        // Inject weather context into the narration prompt via the participants list suffix
        List<String> participantsWithWeather = new ArrayList<>(participantNames);
        participantsWithWeather.add(0, "[Météo: " + weather.condition.label + " à " + weather.cityName + " — " + weather.condition.effect + "]");

        String narration = ChatbotService.narrateFullTournament(
            participantsWithWeather, new ArrayList<>(allEliminated), new ArrayList<>(allEvents), winner.p.getName());

        Platform.runLater(() -> {
            feedDivider();
            feed("📜  CHRONIQUES DES CULLING GAMES", PRIMARY, true);
            for (String line : narration.split("\n")) {
                if (!line.isBlank()) {
                    boolean header = line.startsWith("⚔") || line.startsWith("==")
                        || line.contains("ROUND") || line.contains("🏆") || line.contains("BAIN");
                    feed(line, header ? GOLD : TEXT_SEC, header);
                }
            }
            launchBtn.setText("⚔️   NOUVEAU TOURNOI");
            launchBtn.setDisable(false);
            launchBtn.setStyle(btnStyle(true));
        });
    }

    // ── Alliance Logic ────────────────────────────────────────────────────────
    private void checkAndBreakAlliances(List<Fighter> fighters) {
        List<Fighter> alive = alive(fighters);
        boolean hasEnemies = alive.stream().anyMatch(a ->
            alive.stream().anyMatch(b -> b != a && !b.allianceId.equals(a.allianceId)));

        if (!hasEnemies) {
            boolean broke = false;
            for (Fighter f : alive) {
                if (!f.allianceBroken) {
                    f.allianceBroken = true;
                    String old = f.allianceId;
                    f.allianceId = "Broken_" + f.p.getId();
                    if (!broke) {
                        feed("💔  L'alliance [" + old + "] se brise — chacun pour soi!", DANGER, true);
                        broke = true;
                    }
                }
            }
            pause(700);
        }
    }

    // ── Target Picking ────────────────────────────────────────────────────────
    private Fighter pickTarget(Fighter attacker, List<Fighter> alive, Random rng, ArenaPhase phase) {
        List<Fighter> enemies = alive.stream()
            .filter(f -> f != attacker && !f.allianceId.equals(attacker.allianceId))
            .collect(Collectors.toList());

        if (!enemies.isEmpty()) {
            return phase == ArenaPhase.BLOODBATH
                ? enemies.get(rng.nextInt(enemies.size()))
                : enemies.stream().min(Comparator.comparingInt(f -> f.hp)).orElse(null);
        }
        return alive.stream().filter(f -> f != attacker)
            .min(Comparator.comparingInt(f -> f.hp)).orElse(null);
    }

    // ── HP Card Refresh ───────────────────────────────────────────────────────
    private void refreshStatusCard(Fighter f) {
        final double pct  = f.hpPercent();
        final int    hp   = f.hp;
        final int    max  = f.maxHp;
        final boolean dead = !f.alive;
        final int id = f.p.getId();
        Platform.runLater(() -> {
            ProgressBar pb   = hpBars.get(id);
            Label       lbl  = hpLabels.get(id);
            VBox        card = statusCards.get(id);
            if (pb != null) {
                pb.setProgress(dead ? 0 : pct);
                String c = dead ? "#444" : pct > 0.6 ? "#27ae60" : pct > 0.3 ? "#f39c12" : DANGER;
                pb.setStyle("-fx-accent: " + c + "; -fx-control-inner-background: #333;");
            }
            if (lbl != null) {
                lbl.setText(dead ? "💀 Éliminé" : hp + "/" + max);
                lbl.setStyle("-fx-text-fill: " + (dead ? "#555" : pct > 0.3 ? "#27ae60" : DANGER) + "; -fx-font-size: 9px;");
            }
            if (card != null && dead) {
                card.setStyle("-fx-background-color: #1a0000; -fx-background-radius: 8px; -fx-opacity: 0.45;");
                card.setEffect(new javafx.scene.effect.ColorAdjust(0, -1, -0.2, 0));
            }
        });
    }

    // ── Phase Determination ───────────────────────────────────────────────────
    private ArenaPhase phase(int round, int alive) {
        if (round == 1)  return ArenaPhase.BLOODBATH;
        if (alive <= 3)  return ArenaPhase.FINAL_DUEL;
        return ArenaPhase.SURVIVAL;
    }

    // ── Util ──────────────────────────────────────────────────────────────────
    private int           aliveCount(List<Fighter> f) { return (int) f.stream().filter(x -> x.alive).count(); }
    private List<Fighter> alive(List<Fighter> f)      { return f.stream().filter(x -> x.alive).collect(Collectors.toList()); }
    private void          pause(long ms)              { try { Thread.sleep(ms); } catch (InterruptedException ignored) {} }

    private void feed(String text, String color, boolean bold) {
        Platform.runLater(() -> {
            feedBox.getChildren().add(txt(text, color, bold));
            feedScroll.setVvalue(1.0);
        });
    }

    private void feedDivider() {
        Platform.runLater(() -> {
            Separator s = new Separator();
            s.setStyle("-fx-background-color: " + PRIMARY + "33;");
            feedBox.getChildren().add(s);
            feedScroll.setVvalue(1.0);
        });
    }

    private Text txt(String text, String color, boolean bold) {
        Text t = new Text(text);
        t.setFill(Color.web(color));
        t.setWrappingWidth(700);
        t.setFont(bold ? Font.font("System", FontWeight.BOLD, 13) : Font.font("System", 12));
        return t;
    }

    private ImageView buildPortrait(Personnage p, int w, int h) {
        ImageView iv = new ImageView();
        iv.setFitWidth(w); iv.setFitHeight(h); iv.setPreserveRatio(false);
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(w, h);
        clip.setArcWidth(10); clip.setArcHeight(10); iv.setClip(clip);
        if (p.getPortraitImage() != null && p.getPortraitImage().length > 0) {
            try { iv.setImage(new Image(new java.io.ByteArrayInputStream(p.getPortraitImage()))); return iv; }
            catch (Exception ignored) {}
        }
        loadDicebear(iv, p.getName());
        return iv;
    }

    private void loadDicebear(ImageView iv, String name) {
        new Thread(() -> {
            try {
                Image img = new Image("https://api.dicebear.com/7.x/adventurer/svg?seed=" + name, true);
                Platform.runLater(() -> iv.setImage(img));
            } catch (Exception ignored) {}
        }).start();
    }

    private String cardStyle(boolean sel) {
        return sel
            ? "-fx-background-color: " + PRIMARY + "22; -fx-border-color: " + PRIMARY + "; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-cursor: hand;"
            : "-fx-background-color: " + BG_DARK + "; -fx-background-radius: 10px; -fx-cursor: hand;";
    }

    private String btnStyle(boolean active) {
        return active
            ? "-fx-background-color: " + DANGER + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-background-radius: 12px; -fx-padding: 14 20; -fx-cursor: hand;"
            : "-fx-background-color: #252525; -fx-text-fill: #555; -fx-font-size: 15px; -fx-background-radius: 12px; -fx-padding: 14 20;";
    }
}
