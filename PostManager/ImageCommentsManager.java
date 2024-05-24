package PostManager;

import DatabaseManager.DatabaseUploader;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import javax.swing.JLabel;

/**
 * Manages the function of commenting on images.
 */
public class ImageCommentsManager {

  private static String currentUser = "";
  private static String imageOwner = "";

  public static void handleCommentAction(String imageId, JLabel commentsLabel) {
    currentUser = retrieveUser();
    imageOwner = retrieveImageOwner();
    try {
      updateImageDetails(imageId, commentsLabel);
    } catch (ClassNotFoundException | SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // updateNotifications(imageId, comment);
  }

  public static String retrieveImageOwner() {
    try (
      BufferedReader userReader = Files.newBufferedReader(
        Paths.get("data", "users.txt")
      )
    ) {
      String line = userReader.readLine();
      if (line != null) {
        imageOwner = line.split(":")[1].trim();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return imageOwner;
  }

  public static String retrieveUser() {
    try (
      BufferedReader userReader = Files.newBufferedReader(
        Paths.get("data", "users.txt")
      )
    ) {
      String line = userReader.readLine();
      if (line != null) {
        currentUser = line.split(":")[0].trim();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return currentUser;
  }

  public static void updateImageDetails(String imageId, JLabel commentsLabel)
    throws ClassNotFoundException, SQLException {
    DatabaseUploader db = new DatabaseUploader();
    int comments = db.getCommentCount(imageId);
    commentsLabel.setText("Comments: " + comments);
  }

  public static void postComment(String comment, String imageId)
    throws ClassNotFoundException, SQLException {
    String currentUser = retrieveUser();
    DatabaseUploader db = new DatabaseUploader();
    db.updateComment(currentUser, imageId, comment.replaceAll("[\']", "\'\'"));
  }
}
