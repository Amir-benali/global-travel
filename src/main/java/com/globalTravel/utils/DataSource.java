package com.globalTravel.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {
    private Connection connection;
    private static DataSource instance;

    private final String URL = "jdbc:mysql://localhost:3306/globaltraveldb";
    private final String USERNAME = "root";
    private final String PASSWORD = "";

    private DataSource() {
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Connected to database");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static DataSource getInstance() {
        if(instance == null)
            instance = new DataSource();
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
