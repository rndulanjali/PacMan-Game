/* This class manages SQLite database connections. and initialisation. */

package pacman.db;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:pacman.db";   //Database URL. Database file:'pacman.db' Stored in the project directory.

    public static Connection getConnection() throws SQLException {  //This method returns a database connection.
        return DriverManager.getConnection(DB_URL);                 //Opens a connection to the SQLite database.
    }

    /* Creates the users table if it does not already exist.
    Called at application startup.Purpose:create users table if not exists */
    public static void initialize() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +         //Creates the users table only if missing.
                "username TEXT PRIMARY KEY," +
                "salt TEXT NOT NULL," +
                "passwordHash TEXT NOT NULL," +
                "highScore INTEGER DEFAULT 0," +
                "rememberToken TEXT" +
                ")";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}