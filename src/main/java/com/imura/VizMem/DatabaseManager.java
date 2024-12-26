package com.imura.VizMem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private Connection conn = null;

    DatabaseManager() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/vizmem", "root", "");
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
            System.exit(1);
        }
    }

    public Connection getConnection() {
        return conn;
    }
}
