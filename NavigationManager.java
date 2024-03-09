import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.*;

public class NavigationManager extends JFrame {

  final String iconLocation, homeIcon, searchIcon, addIcon, heartIcon, profileIcon, homeIconSelected, searchIconSelected, addIconSelected, heartIconSelected, profileIconSelected;

  public NavigationManager() {
    iconLocation = "img/icons/";
    homeIcon = iconLocation + "home.png";
    searchIcon = iconLocation + "search.png";
    addIcon = iconLocation + "add.png";
    heartIcon = iconLocation + "heart.png";
    profileIcon = iconLocation + "profile.png";
    homeIconSelected = iconLocation + "homeSelected.png";
    searchIconSelected = iconLocation + "searchSelected.png";
    addIconSelected = iconLocation + "addSelected.png";
    heartIconSelected = iconLocation + "heartSelected.png";
    profileIconSelected = iconLocation + "profileSelected.png";
  }

  public JPanel createNavigationPanel() {
    // Create and return the navigation panel
    // Navigation Bar
    JPanel navigationPanel = new JPanel();

    navigationPanel.setBackground(new Color(249, 249, 249));
    navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.X_AXIS));
    navigationPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    navigationPanel.add(
      IconButtonCreator.createIconButton(homeIcon, "home", e -> openHomeUI())
    );
    navigationPanel.add(Box.createHorizontalGlue());
    navigationPanel.add(
      IconButtonCreator.createIconButton(
        searchIcon,
        "explore",
        e -> exploreUI()
      )
    );
    navigationPanel.add(Box.createHorizontalGlue());
    navigationPanel.add(
      IconButtonCreator.createIconButton(addIcon, "add", e -> imageUploadUI())
    );
    navigationPanel.add(Box.createHorizontalGlue());
    navigationPanel.add(
      IconButtonCreator.createIconButton(
        heartIcon,
        "notification",
        e -> notificationsUI()
      )
    );
    navigationPanel.add(Box.createHorizontalGlue());
    navigationPanel.add(
      IconButtonCreator.createIconButton(
        profileIcon,
        "profile",
        e -> openProfileUI()
      )
    );

    return navigationPanel;
  }

  private void openProfileUI() {
    // Open InstagramProfileUI frame
    this.dispose();
    String loggedInUsername = "";

    // Read the logged-in user's username from users.txt
    try (
      BufferedReader reader = Files.newBufferedReader(
        Paths.get("data", "users.txt")
      )
    ) {
      String line = reader.readLine();
      if (line != null) {
        loggedInUsername = line.split(":")[0].trim();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    User user = new User(loggedInUsername);
    InstagramProfileUI profileUI = new InstagramProfileUI(user);
    profileUI.setVisible(true);
  }

  private void notificationsUI() {
    // Open InstagramProfileUI frame
    this.dispose();
    NotificationsUI notificationsUI = new NotificationsUI();
    notificationsUI.setVisible(true);
  }

  private void imageUploadUI() {
    // Open InstagramProfileUI frame
    this.dispose();
    ImageUploadUI upload = new ImageUploadUI();
    upload.setVisible(true);
  }

  private void openHomeUI() {
    // Open InstagramProfileUI frame
    this.dispose();
    QuakstagramHomeUI homeUI = new QuakstagramHomeUI();
    homeUI.setVisible(true);
  }

  private void exploreUI() {
    // Open InstagramProfileUI frame
    this.dispose();
    ExploreUI explore = new ExploreUI();
    explore.setVisible(true);
  }
}
