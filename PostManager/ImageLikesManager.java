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
 * Manages the function of liking images.
 */
public class ImageLikesManager {

  private static StringBuilder newContent = new StringBuilder();
  private static boolean updated = false;
  private String currentUser = "";
  private static String imageOwner = "";
  private static String comment = "";
  private static String timestamp = LocalDateTime
    .now()
    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

  public ImageLikesManager(String currentUser) {
    this.currentUser = currentUser;
  }

  public void handleLikeAction(String imageId, JLabel likesLabel)
    throws ClassNotFoundException, SQLException {
    DatabaseUploader db = new DatabaseUploader();
    if (db.alreadyLiked(currentUser, imageId)) return;
    currentUser = retrieveUser();
    // imageOwner = retrieveImageOwner();
    // updateImageDetails(imageId, likesLabel);
    // updateNotifications(imageId, comment);
    db.like(currentUser, imageId);
  }

  public String retrieveUser() {
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
  // public void updateNotifications(String imageId, String comment) {
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
}
