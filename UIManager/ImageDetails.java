package UIManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import DatabaseManager.UpdateDatabase;

public class ImageDetails {
    private String username;
    private String bio;
    private String timestampString;
    private int likes;

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getTimestampString() { return timestampString; }
    public void setTimestampString(String timestampString) { this.timestampString = timestampString; }
    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    public static ImageDetails fetchImageDetails(String imageId) {
        ImageDetails details = new ImageDetails();
        String query = "SELECT u.username, u.userbio, p.time_stamp, (SELECT COUNT(*) FROM likes WHERE post_id = p.post_id) AS likes " +
                       "FROM posts p " +
                       "JOIN user_info u ON p.owner = u.username " +
                       "WHERE p.post_id = ?";
        try (Connection conn = UpdateDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, imageId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                details.setUsername(rs.getString("username"));
                details.setBio(rs.getString("userbio"));
                details.setTimestampString(rs.getString("time_stamp") != null ? rs.getString("time_stamp") : ""); // Handle potential null
                details.setLikes(rs.getInt("likes"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }
}
