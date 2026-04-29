package com.example.app.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class mydatab {
 private final   String url="jdbc:mysql://localhost:3306/midgar";
private   final   String user ="root";
 private   final String pws ="";

private Connection connection;
private static mydatab instance;
private mydatab(){
    try {
        connection= DriverManager.getConnection(url,user,pws);
        System.out.println("connecter a la base de données");
    } catch (SQLException e) {
        System.out.println(e.getMessage());    }
}
public static mydatab getInstance(){
    if (instance==null){
     instance= new mydatab();

    }
    return instance;
}

    public Connection getConnection() {
        return connection;
    }
}