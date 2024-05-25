package UserManager;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRelationshipManager {

  private Connection getConnection() throws SQLException {
    String connectionUrl = "jdbc:mysql://localhost:3306/quack";
    String username = "root";
    String password = "";
    return DriverManager.getConnection(connectionUrl, username, password);
  }

  // Method to follow a user
  public void followUser(String follower, String followed, Timestamp followDate) {
    if (!isAlreadyFollowing(follower, followed)) {
      try (Connection conn = getConnection()) {
        String query = "INSERT INTO user_following (username1, username2, following_date) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
          stmt.setString(1, follower);
          stmt.setString(2, followed);
          stmt.setTimestamp(3, followDate);
          stmt.executeUpdate();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  // Method to check if a user is already following another user
  private boolean isAlreadyFollowing(String follower, String followed) {
    boolean isFollowing = false;
    try (Connection conn = getConnection()) {
      String query = "SELECT * FROM user_following WHERE username1 = ? AND username2 = ?";
      try (PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, follower);
        stmt.setString(2, followed);
        try (ResultSet rs = stmt.executeQuery()) {
          if (rs.next()) {
            isFollowing = true;
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return isFollowing;
  }

  // Method to get the list of followers for a user
  public List<String> getFollowers(String username) {
    List<String> followers = new ArrayList<>();
    try (Connection conn = getConnection()) {
      String query = "SELECT username1 FROM user_following WHERE username2 = ?";
      try (PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, username);
        try (ResultSet rs = stmt.executeQuery()) {
          while (rs.next()) {
            followers.add(rs.getString("username1"));
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return followers;
  }

  // Method to get the list of users a user is following
  public List<String> getFollowing(String username) {
    List<String> following = new ArrayList<>();
    try (Connection conn = getConnection()) {
      String query = "SELECT username2 FROM user_following WHERE username1 = ?";
      try (PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, username);
        try (ResultSet rs = stmt.executeQuery()) {
          while (rs.next()) {
            following.add(rs.getString("username2"));
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return following;
  }
}
