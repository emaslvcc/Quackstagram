package PostManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import javax.swing.JLabel;

import DatabaseManager.UpdateDatabase;

public class ImageCommentsManager {

  private static int count = 0;
  private static Path detailsPath = Paths.get("img", "image_details.txt");
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
    updateImageDetails(imageId, commentsLabel);
    updateNotifications(imageId, comment);
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

  public static String retrieveImageOwner(String imageId) {
    try (BufferedReader reader = Files.newBufferedReader(detailsPath)) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.contains("ImageID: " + imageId)) {
          String[] parts = line.split(", ");
          imageOwner = parts[1].split(": ")[1];
        }
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

  public static void updateImageDetails(String imageId, JLabel commentsLabel) {
    // Read and update image_details.txt
    try (BufferedReader reader = Files.newBufferedReader(detailsPath)) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.contains("ImageID: " + imageId)) {
          String[] parts = line.split(", ");
          imageOwner = parts[1].split(": ")[1];
          int comments = Integer.parseInt(parts[5].split(": ")[1]);
          comments++; // Increment the likes count
          parts[5] = "Comments: " + comments;
          line = String.join(", ", parts);

          // Update the UI
          commentsLabel.setText("Comments: " + comments);
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

  public static void updateNotifications(String imageId, String comment) {
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

  public static void postComment(String comment, String imageId) {
    count++;
    String currentUser = retrieveUser();
    String imageOwner = retrieveImageOwner(imageId);
    String comment_id = imageOwner + "_" + count;
    System.out.println("!!!!!");
    UpdateDatabase.updateComments(comment_id, imageId, comment, currentUser);
  }
}
