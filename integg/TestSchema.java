import java.sql.*;

public class TestSchema {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/midgar_3?serverTimezone=UTC";
        try (Connection conn = DriverManager.getConnection(url, "root", "")) {
            ResultSet rs = conn.createStatement().executeQuery("SHOW TABLES");
            while (rs.next()) {
                System.out.println("TABLE: " + rs.getString(1));
            }
            System.out.println("---");
            String[] tables = {"universes", "universe", "oeuvres", "oeuvre", "personnages", "personnage", "artefacts", "artefact"};
            for (String t : tables) {
                try {
                    ResultSet crs = conn.createStatement().executeQuery("SHOW COLUMNS FROM " + t);
                    System.out.print("COLUMNS FOR " + t + ": ");
                    while(crs.next()) {
                        System.out.print(crs.getString("Field") + " ");
                    }
                    System.out.println();
                } catch(Exception e) {
                    // ignore
                }
            }
        } catch (Exception e) {}
    }
}