
package org.td.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/restaurant_db";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "postgres";

    public Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion à la base de données");
            throw e;
        }
    }
}
