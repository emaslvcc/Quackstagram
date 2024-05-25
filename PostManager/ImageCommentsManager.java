package PostManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JLabel;
import DatabaseManager.UpdateDatabase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ImageCommentsManager {
  private static String currentUser = "";
  private static String imageOwner = "";
  private static String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

  public static void handleCommentAction(String imageId, JLabel commentsLabel) {
    currentUser = retrieveUser();
    imageOwner = retrieveImageOwner();
    updateImageDetails(imageId, commentsLabel);
  }

  public static String retrieveImageOwner() {
    try (BufferedReader userReader = Files.newBufferedReader(Paths.get("data", "users.txt"))) {
      String line = userReader.readLine();
      if (line != null) {
        imageOwner = line.split(":")[1].trim();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return imageOwner;
  }

  public static String retrieveImageOwner(String imageId) {
    String owner = "";
    String query = "SELECT owner FROM posts WHERE post_id = ?";
    try (Connection conn = UpdateDatabase.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        pstmt.setString(1, imageId);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            owner = rs.getString("owner");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return owner;
  }

  public static String retrieveUser() {
    try (BufferedReader userReader = Files.newBufferedReader(Paths.get("data", "users.txt"))) {
      String line = userReader.readLine();
      if (line != null) {
        currentUser = line.split(":")[0].trim();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return currentUser;
  }

  // Method to update comments count for a post
  public static void updateImageDetails(String imageId, JLabel commentsLabel) {
    String query = "UPDATE posts SET comments = comments + 1 WHERE post_id = ?";
    try (Connection conn = UpdateDatabase.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(query)) {
        pstmt.setString(1, imageId);
        int affectedRows = pstmt.executeUpdate();
        if (affectedRows > 0) {
          // Fetch the updated comments count to update the UI
          query = "SELECT comments FROM posts WHERE post_id = ?";
          try (PreparedStatement pstmt2 = conn.prepareStatement(query)) {
              pstmt2.setString(1, imageId);
              ResultSet rs = pstmt2.executeQuery();
              if (rs.next()) {
                  int comments = rs.getInt("comments");
                  commentsLabel.setText("Comments: " + comments);
              }
          }
        }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void postComment(String comment, String imageId) {
      String time_stamp = timestamp;
      String currentUser = retrieveUser();
      UpdateDatabase.updateComments(time_stamp, imageId, comment, currentUser);
  }
}
