package PostManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JLabel;

/**
 * Manages the function of liking images.
 */
public class ImageLikesManager {

  private static Path detailsPath = Paths.get("img", "image_details.txt");
  private static Path notificationsPath = Paths.get(
    "data",
    "notifications.txt"
  );
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

  public void handleLikeAction(String imageId, JLabel likesLabel) {
    if (alreadyLiked(imageId, currentUser)) return;
    currentUser = retrieveUser();
    // imageOwner = retrieveImageOwner();
    updateImageDetails(imageId, likesLabel);
    updateNotifications(imageId, comment);
  }

  public boolean alreadyLiked(String imageId, String currentUser) {
    try (BufferedReader reader = Files.newBufferedReader(notificationsPath)) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.contains(imageId) && line.contains(currentUser)) return true;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
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

  public void updateImageDetails(String imageId, JLabel likesLabel) {
    updated = false;

    // Clear newContent StringBuilder
    newContent.setLength(0);

    // Read and update image_details.txt
    try (BufferedReader reader = Files.newBufferedReader(detailsPath)) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.contains("ImageID: " + imageId)) {
          String[] parts = line.split(", ");
          imageOwner = parts[1].split(": ")[1];
          int likes = Integer.parseInt(parts[4].split(": ")[1]);
          likes++; // Increment the likes count
          parts[4] = "Likes: " + likes;
          line = String.join(", ", parts);

          // Update the UI
          likesLabel.setText("Likes: " + likes);
          updated = true;
        }
        newContent.append(line).append("\n");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Write updated likes back to image_details.txt
    if (updated) {
      try (BufferedWriter writer = Files.newBufferedWriter(detailsPath)) {
        writer.write(newContent.toString());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void updateNotifications(String imageId, String comment) {
    String notification = String.format(
      "%s; %s; %s; %s\n",
      imageOwner,
      currentUser,
      imageId,
      timestamp
    );
    try (
      BufferedWriter notificationWriter = Files.newBufferedWriter(
        Paths.get("data", "notifications.txt"),
        StandardOpenOption.CREATE,
        StandardOpenOption.APPEND
      )
    ) {
      notificationWriter.write(notification);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
