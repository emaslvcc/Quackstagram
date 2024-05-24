package PostManager;

import DatabaseManager.DatabaseUploader;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JLabel;

/**
 * Manages the function of commenting on images.
 */
public class ImageCommentsManager {

  private static StringBuilder newContent = new StringBuilder();
  private static boolean updated = false;
  private static String currentUser = "";
  private static String imageOwner = "";
  private static String comment = "";
  private static String timestamp = LocalDateTime
    .now()
    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

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

  // public static String retrieveImageOwner(String imageId) {
  //   try (BufferedReader reader = Files.newBufferedReader(detailsPath)) {
  //     String line;
  //     while ((line = reader.readLine()) != null) {
  //       if (line.contains("ImageID: " + imageId)) {
  //         String[] parts = line.split(", ");
  //         imageOwner = parts[1].split(": ")[1];
  //       }
  //     }
  //   } catch (IOException e) {
  //     e.printStackTrace();
  //   }
  //   return imageOwner;
  // }

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
    updated = true;
  }

  // public static void updateNotifications(String imageId, String comment) {
  //   String notification = String.format(
  //     "%s; %s; %s; %s\n",
  //     imageOwner,
  //     currentUser,
  //     imageId,
  //     timestamp
  //   );
  //   try (
  //     BufferedWriter notificationWriter = Files.newBufferedWriter(
  //       Paths.get("data", "notifications.txt"),
  //       StandardOpenOption.CREATE,
  //       StandardOpenOption.APPEND
  //     )
  //   ) {
  //     notificationWriter.write(notification);
  //   } catch (IOException e) {
  //     e.printStackTrace();
  //   }
  // }

  public static void postComment(String comment, String imageId)
    throws ClassNotFoundException, SQLException {
    String currentUser = retrieveUser();
    DatabaseUploader db = new DatabaseUploader();
    db.updateComment(currentUser, imageId, comment.replaceAll("[\']", "\'\'"));
  }
}
