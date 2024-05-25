package PostManager;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JLabel;
import java.io.BufferedReader;
import java.io.IOException;

import DatabaseManager.UpdateDatabase;

public class ImageLikesManager {

    private static String currentUser = "";
    private static String timestamp = LocalDateTime
        .now()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    public ImageLikesManager(String currentUser) {
        this.currentUser = currentUser;
    }

    public void handleLikeAction(String imageId, JLabel likesLabel) {
        if (alreadyLiked(imageId, currentUser)) return;
        currentUser = retrieveUser();
        UpdateDatabase.updateLikes(imageId, currentUser);
        updateImageDetails(imageId, likesLabel);
    }

    public boolean alreadyLiked(String imageId, String currentUser) {
        String query = "SELECT 1 FROM likes WHERE post_id = ? AND liker = ?";
        try (Connection conn = UpdateDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, imageId);
            pstmt.setString(2, currentUser);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String retrieveUser() {
        try (BufferedReader userReader = Files.newBufferedReader(
            Paths.get("data", "users.txt")
        )) {
            String line = userReader.readLine();
            if (line != null) {
                currentUser = line.split(":")[0].trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return currentUser;
    }

    public int getLikesCount(String imageId) {
        int likesCount = 0;
        String query = "SELECT COUNT(*) AS likes_count FROM likes WHERE post_id = ?";
        try (Connection conn = UpdateDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, imageId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                likesCount = rs.getInt("likes_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return likesCount;
    }

    public void updateImageDetails(String imageId, JLabel likesLabel) {
        int likes = getLikesCount(imageId);
        likesLabel.setText("Likes: " + likes);
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
}
