package PostManager;

import DatabaseManager.DatabaseUploader;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import javax.swing.JLabel;

/**
 * Manages the function of liking images.
 */
public class ImageLikesManager {

  private String currentUser = "";

  public ImageLikesManager(String currentUser) {
    this.currentUser = currentUser;
  }

  public void handleLikeAction(String imageId, JLabel likesLabel)
    throws ClassNotFoundException, SQLException {
    DatabaseUploader db = new DatabaseUploader();
    if (db.alreadyLiked(currentUser, imageId)) return;
    currentUser = retrieveUser();
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
}
