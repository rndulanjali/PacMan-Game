/* This class handles all database operations related to users.
This pattern is called:DAO (Data Access Object)
This class depends on DatabaseManager for connection handling. */

package pacman.auth;

import pacman.db.DatabaseManager; //responsible for database connections.
import java.sql.*;                //to interact with the SQLite database.
import java.util.ArrayList;       //Used to store multiple users.
import java.util.List;

public class UserRepository {
    public UserRepository() {
        DatabaseManager.initialize(); //Initialize Database. This ensure table exists
    }

    //This method saves a new user in the database.
    public boolean save(User user) {
        String sql = "INSERT INTO users(username, salt, passwordHash, highScore, rememberToken) VALUES(?,?,?,?,?)";
        try (Connection conn = DatabaseManager.getConnection();      //Gets a connection to the database.Database file: pacman.db
             PreparedStatement pstmt = conn.prepareStatement(sql)) { //PreparedStatement allows safe SQL execution.Benefits:
                                                                     //              1) prevents SQL injection, 2)better performance
        	pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getSalt());//Replace second ? with salt.
            pstmt.setString(3, user.getPasswordHash());
            pstmt.setInt(4, user.getHighScore());
            pstmt.setString(5, null);//New user initially has no remember-me token.
            pstmt.executeUpdate();//insert new row into database
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //Finds a user using the username.
    public User find(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";   //Retrieve user row from database.
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery(); //Runs the query and returns results.
            if (rs.next()) {                     //Check if Result Exists
                User user = new User(            //Creates a User object using database values.
                    rs.getString("username"),
                    rs.getString("salt"),
                    rs.getString("passwordHash")
                );
                user.setHighScore(rs.getInt("highScore"));//Assigns the stored high score.
                return user; //Return the found user.
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    
    //Updates the high score if the new score is higher.
    public void updateHighScore(String username, int score) {
        String sql = "UPDATE users SET highScore = ? WHERE username = ? AND highScore < ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, score);
            pstmt.setString(2, username);
            pstmt.setInt(3, score);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    //Updates the remember-me token for a user.
    public void updateRememberToken(String username, String token) {
        String sql = "UPDATE users SET rememberToken = ? WHERE username = ?";//Stores the remember-me login token.
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, token);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Finds a user by remember-me token. Used for auto login feature
    public User findByRememberToken(String token) {
        String sql = "SELECT * FROM users WHERE rememberToken = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, token);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User(
                    rs.getString("username"),
                    rs.getString("salt"),
                    rs.getString("passwordHash")
                );
                user.setHighScore(rs.getInt("highScore"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Retrieves the top users by high score.
    public List<User> getTopScores(int limit) {  //Returns top players.
        List<User> list = new ArrayList<>();  //Stores users in a list.
        String sql = "SELECT username, highScore FROM users ORDER BY highScore DESC LIMIT ?";   //sort scores highest -> lowest
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                User user = new User(rs.getString("username"), "", "");
                user.setHighScore(rs.getInt("highScore"));
                list.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;  //Return List. This list can be used to display: leaderboard
    }
}