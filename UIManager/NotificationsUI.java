package UIManager;

import DatabaseManager.DatabaseUploader;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Notifications UI and logic.
 */
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
    // Reuse the header and navigation panel creation methods from the
    // InstagramProfileUI class
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

    List<String[]> likes = null;
    DatabaseUploader db;
    try {
      db = new DatabaseUploader();
      likes = db.getLikesByUserId(currentUsername);
    } catch (ClassNotFoundException | SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    for (String[] like : likes) {
      String likerUserId = like[0];
      String notificationMessage = likerUserId + " liked your picture!";

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

    // Add panels to frame
    add(headerPanel, BorderLayout.NORTH);
    add(scrollPane, BorderLayout.CENTER);
    add(navigationPanel, BorderLayout.SOUTH);
  }
}
