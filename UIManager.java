import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class UIManager extends JFrame {

  // Path to folder that contains the navigation bar icons
  final String iconLocation = "img/icons/";

  // File names of the navigation bar icons
  final String homeIcon = iconLocation + "home.png";
  final String searchIcon = iconLocation + "search.png";
  final String addIcon = iconLocation + "add.png";
  final String heartIcon = iconLocation + "heart.png";
  final String profileIcon = iconLocation + "profile.png";
  final String homeIconSelected = iconLocation + "homeSelected.png";
  final String searchIconSelected = iconLocation + "searchSelected.png";
  final String addIconSelected = iconLocation + "addSelected.png";
  final String heartIconSelected = iconLocation + "heartSelected.png";
  final String profileIconSelected = iconLocation + "profileSelected.png";

  // Headers for creating the correct navigation bar
  final String homePage = "Quackstagram";
  final String searchPage = "Explore";
  final String addPage = "Upload Image";
  final String notificationPage = "Notifications";
  final String profilePage = "Profile";

  // Dimensions of the main windows
  static final int WIDTH = 300;
  static final int HEIGHT = 500;

  protected abstract void initializeUI();

  // Create a header panel with any string as input
  protected JPanel createHeaderPanel(String header) {
    JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    headerPanel.setBackground(new Color(51, 51, 51)); // Set a darker background for the header
    JLabel lblRegister = new JLabel(header);
    lblRegister.setFont(new Font("Arial", Font.BOLD, 16));
    lblRegister.setForeground(Color.WHITE); // Set the text color to white
    headerPanel.add(lblRegister);
    headerPanel.setPreferredSize(new Dimension(WIDTH, 40)); // Give the header a fixed height
    return headerPanel;
  }

  // Create a navigation panel where the current page's icon is in another colour
  protected JPanel createNavigationPanel(String selectedPage) {
    String home, search, add, heart, profile;
    home = homeIcon;
    search = searchIcon;
    add = addIcon;
    heart = heartIcon;
    profile = profileIcon;
    switch (selectedPage) {
      case homePage:
        home = homeIconSelected;
        break;
      case searchPage:
        search = searchIconSelected;
        break;
      case addPage:
        add = addIconSelected;
        break;
      case notificationPage:
        heart = heartIconSelected;
        break;
      case profilePage:
        profile = profileIconSelected;
        break;
      default:
        System.out.println("No page selected.");
        break;
    }

    // Navigation bar JPanel setup
    JPanel navigationPanel = new JPanel();
    navigationPanel.setBackground(new Color(249, 249, 249));
    navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.X_AXIS));
    navigationPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    // Adding an icon and action for each button
    navigationPanel.add(
      IconButtonCreator.createIconButton(home, "home", e -> openHomeUI())
    );
    navigationPanel.add(Box.createHorizontalGlue());
    navigationPanel.add(
      IconButtonCreator.createIconButton(search, "explore", e -> exploreUI())
    );
    navigationPanel.add(Box.createHorizontalGlue());
    navigationPanel.add(
      IconButtonCreator.createIconButton(add, "add", e -> imageUploadUI())
    );
    navigationPanel.add(Box.createHorizontalGlue());
    navigationPanel.add(
      IconButtonCreator.createIconButton(
        heart,
        "notification",
        e -> notificationsUI()
      )
    );
    navigationPanel.add(Box.createHorizontalGlue());
    navigationPanel.add(
      IconButtonCreator.createIconButton(
        profile,
        "profile",
        e -> openProfileUI()
      )
    );

    return navigationPanel;
  }

  protected void openProfileUI() {
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

  protected void notificationsUI() {
    // Open InstagramProfileUI frame
    this.dispose();
    NotificationsUI notificationsUI = new NotificationsUI();
    notificationsUI.setVisible(true);
  }

  protected void openHomeUI() {
    // Open InstagramProfileUI frame
    this.dispose();
    QuackstagramHomeUI homeUI = new QuackstagramHomeUI();
    homeUI.setVisible(true);
  }

  protected void exploreUI() {
    // Open InstagramProfileUI frame
    this.dispose();
    ExploreUI explore = new ExploreUI();
    explore.setVisible(true);
  }

  protected void imageUploadUI() {
    // Open InstagramProfileUI frame
    this.dispose();
    ImageUploadUI upload = new ImageUploadUI();
    upload.setVisible(true);
  }
}
