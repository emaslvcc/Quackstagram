package UserManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import DatabaseManager.UpdateDatabase;

public class LoginManager implements Login {

    private static User newUser;

    public static void saveCredentials(String username, String password, String bio, String accountType) {
        String query = "INSERT INTO user_info (username, userpass, userbio, type_of_account) VALUES (?, ?, ?, ?)";
        try (Connection conn = UpdateDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, bio);
            pstmt.setString(4, accountType);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean verifyCredentials(String username, String password) {
        String query = "SELECT * FROM user_info WHERE username = ? AND userpass = ?";
        try (Connection conn = UpdateDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String bio = rs.getString("userbio");
                String accountType = rs.getString("type_of_account");
                newUser = new User(username, bio, password, accountType); 
                saveUserInformation(newUser);  // Ensure the current user is saved to users.txt
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static User getUser() {
        return newUser;
    }

    private static void saveUserInformation(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/users.txt", false))) {
            writer.write(user.toString()); // Implement a suitable toString method in User class
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
