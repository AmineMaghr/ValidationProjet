package com.example.app.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {
    private final   String url="jdbc:mysql://localhost:3306/midgar_3";
    private   final   String user ="root";
    private   final String pws ="";

    private static Connection connection;
    private static MyDatabase instance;
    private MyDatabase(){
        try {
            connection= DriverManager.getConnection(url,user,pws);
            System.out.println("connecter a la base de données");
        } catch (SQLException e) {
            System.out.println(e.getMessage());    }
    }
    public static MyDatabase getInstance(){
        if (instance==null){
            instance= new MyDatabase();

        }
        return instance;
    }

    public static Connection getConnection() {
        return connection;
    }
}
