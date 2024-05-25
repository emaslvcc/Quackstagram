package UIManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import DatabaseManager.UpdateDatabase;

public class NotificationsUI extends UIManager {

    private JPanel headerPanel;
    private String pageName = "Notifications";

    public NotificationsUI() {
        setTitle("Notifications");
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        setLocationRelativeTo(null);
        initializeUI();
    }

    @Override
    protected void initializeUI() {
        // Reuse the header and navigation panel creation methods from the InstagramProfileUI class
        headerPanel = createHeaderPanel(pageName);
        JPanel navigationPanel = createNavigationPanel(pageName);

        // Content Panel for notifications
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        // Read the current username from users.txt
        String currentUsername = readCurrentUsername();

        // Fetch and display notifications from the database
        fetchAndDisplayNotifications(contentPanel, currentUsername);

        // Add panels to frame
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(navigationPanel, BorderLayout.SOUTH);
    }

    private String readCurrentUsername() {
        String currentUsername = "";
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("data", "users.txt"))) {
            String line = reader.readLine();
            if (line != null) {
                currentUsername = line.split(":")[0].trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return currentUsername;
    }

    private void fetchAndDisplayNotifications(JPanel contentPanel, String currentUsername) {
        // Fetch likes notifications
        String likesQuery = "SELECT liker, post_id FROM likes WHERE post_id IN (SELECT post_id FROM posts WHERE owner = ?)";
        try (Connection conn = UpdateDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(likesQuery)) {
            pstmt.setString(1, currentUsername);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String liker = rs.getString("liker");
                String postId = rs.getString("post_id");
                String notificationMessage = liker + " liked your post.";
                addNotificationToPanel(contentPanel, notificationMessage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Fetch comments notifications
        String commentsQuery = "SELECT commenter, post_id, time_stamp FROM comments WHERE post_id IN (SELECT post_id FROM posts WHERE owner = ?)";
        try (Connection conn = UpdateDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(commentsQuery)) {
            pstmt.setString(1, currentUsername);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String commenter = rs.getString("commenter");
                String postId = rs.getString("post_id");
                String timestamp = rs.getString("time_stamp");
                String notificationMessage = commenter + " commented on your post - " + getElapsedTime(timestamp) + " ago.";
                addNotificationToPanel(contentPanel, notificationMessage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Fetch following notifications
        String followingQuery = "SELECT username1 FROM user_following WHERE username2 = ?";
        try (Connection conn = UpdateDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(followingQuery)) {
            pstmt.setString(1, currentUsername);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String follower = rs.getString("username1");
                String notificationMessage = follower + " started following you.";
                addNotificationToPanel(contentPanel, notificationMessage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addNotificationToPanel(JPanel contentPanel, String notificationMessage) {
        JPanel notificationPanel = new JPanel(new BorderLayout());
        notificationPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JLabel notificationLabel = new JLabel(notificationMessage);
        notificationPanel.add(notificationLabel, BorderLayout.CENTER);
        contentPanel.add(notificationPanel);
    }

    private String getElapsedTime(String timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "yyyy-MM-dd HH:mm:ss"
        );
        LocalDateTime timeOfNotification = LocalDateTime.parse(
                timestamp,
                formatter
        );
        LocalDateTime currentTime = LocalDateTime.now();

        long daysBetween = ChronoUnit.DAYS.between(timeOfNotification, currentTime);
        long minutesBetween =
                ChronoUnit.MINUTES.between(timeOfNotification, currentTime) % 60;

        StringBuilder timeElapsed = new StringBuilder();
        if (daysBetween > 0) {
            timeElapsed
                    .append(daysBetween)
                    .append(" day")
                    .append(daysBetween > 1 ? "s" : "");
        }
        if (minutesBetween > 0) {
            if (daysBetween > 0) {
                timeElapsed.append(" and ");
            }
            timeElapsed
                    .append(minutesBetween)
                    .append(" minute")
                    .append(minutesBetween > 1 ? "s" : "");
        }
        return timeElapsed.toString();
    }
}
