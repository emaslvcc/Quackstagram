import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class NotificationsUI extends UIManager {

  private static final int WIDTH = 300;
  private static final int HEIGHT = 500;
  // private static final int NAV_ICON_SIZE = 20; // Size for navigation icons
  private JPanel headerPanel;

  public NotificationsUI() {
    setTitle("Notifications");
    setSize(WIDTH, HEIGHT);
    setMinimumSize(new Dimension(WIDTH, HEIGHT));
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLayout(new BorderLayout());
    initializeUI();
  }

  @Override
  protected void initializeUI() {
    // Reuse the header and navigation panel creation methods from the
    // InstagramProfileUI class
    headerPanel = createHeaderPanel("Notifications");
    JPanel navigationPanel = createNavigationPanel();

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
    String currentUsername = "";
    try (
      BufferedReader reader = Files.newBufferedReader(
        Paths.get("data", "users.txt")
      )
    ) {
      String line = reader.readLine();
      if (line != null) {
        currentUsername = line.split(":")[0].trim();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    try (
      BufferedReader reader = Files.newBufferedReader(
        Paths.get("data", "notifications.txt")
      )
    ) {
      String line;
      while ((line = reader.readLine()) != null) {
        String[] parts = line.split(";");
        if (parts[0].trim().equals(currentUsername)) {
          // Format the notification message
          String userWhoLiked = parts[1].trim();
          String imageId = parts[2].trim();
          String timestamp = parts[3].trim();
          String notificationMessage =
            userWhoLiked +
            " liked your picture - " +
            getElapsedTime(timestamp) +
            " ago";

          // Add the notification to the panel
          JPanel notificationPanel = new JPanel(new BorderLayout());
          notificationPanel.setBorder(
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
          );

          JLabel notificationLabel = new JLabel(notificationMessage);
          notificationPanel.add(notificationLabel, BorderLayout.CENTER);

          // Add profile icon (if available) and timestamp
          // ... (Additional UI components if needed)

          contentPanel.add(notificationPanel);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    // Add panels to frame
    add(headerPanel, BorderLayout.NORTH);
    add(scrollPane, BorderLayout.CENTER);
    add(navigationPanel, BorderLayout.SOUTH);
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

  @Override
  protected JPanel createNavigationPanel() {
    return super.createNavigationPanel();
  }

  @Override
  protected void imageUploadUI() {
    super.imageUploadUI();
  }

  @Override
  protected void openProfileUI() {
    super.openProfileUI();
  }

  @Override
  protected void notificationsUI() {
    super.notificationsUI();
  }

  @Override
  protected void openHomeUI() {
    super.openHomeUI();
  }

  @Override
  protected void exploreUI() {
    super.exploreUI();
  }
}
