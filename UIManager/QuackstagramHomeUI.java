package UIManager;

import PostManager.ImageLikesManager;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class QuackstagramHomeUI extends UIManager {

  private static final int IMAGE_WIDTH = WIDTH - 100; // Width for the image posts
  private static final int IMAGE_HEIGHT = 150; // Height for the image posts
  private static final Color LIKE_BUTTON_COLOR = new Color(255, 90, 95); // Color for the like button

  private CardLayout cardLayout;
  private JPanel cardPanel, homePanel, imageViewPanel, headerPanel;
  public JLabel commentLabel;

  private String pageName = "Quackstagram";

  public QuackstagramHomeUI() {
    setTitle("Quackstagram Home");
    setSize(WIDTH, HEIGHT);
    setMinimumSize(new Dimension(WIDTH, HEIGHT));
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLayout(new BorderLayout());
    setResizable(false);
    setLocationRelativeTo(null);

    cardLayout = new CardLayout();
    cardPanel = new JPanel(cardLayout);

    homePanel = new JPanel(new BorderLayout());
    imageViewPanel = new JPanel(new BorderLayout());

    initializeUI();

    cardPanel.add(homePanel, "Home");
    cardPanel.add(imageViewPanel, "ImageView");

    add(cardPanel, BorderLayout.CENTER);
    cardLayout.show(cardPanel, "Home"); // Start with the home view

    // Header Panel
    headerPanel = createHeaderPanel(pageName);
    add(headerPanel, BorderLayout.NORTH);
    // Navigation Bar
    JPanel navigationPanel = createNavigationPanel(pageName);
    add(navigationPanel, BorderLayout.SOUTH);
  }

  @Override
  protected void initializeUI() {
    // Content Scroll Panel
    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); // Vertical box layout
    JScrollPane scrollPane = new JScrollPane(contentPanel);
    scrollPane.setHorizontalScrollBarPolicy(
      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
    ); // Never allow
    // horizontal scrolling
    String[][] sampleData = createSampleData();
    populateContentPanel(contentPanel, sampleData);
    add(scrollPane, BorderLayout.CENTER);

    // Set up the home panel

    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

    homePanel.add(scrollPane, BorderLayout.CENTER);
  }

  private void populateContentPanel(JPanel panel, String[][] sampleData) {
    for (String[] postData : sampleData) {
      JPanel itemPanel = new JPanel();
      itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
      itemPanel.setBackground(Color.WHITE); // Set the background color for the item panel
      itemPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      itemPanel.setAlignmentX(CENTER_ALIGNMENT);
      JLabel nameLabel = new JLabel(postData[0]);
      nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

      // Crop the image to the fixed size
      JLabel imageLabel = new JLabel();
      imageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
      imageLabel.setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
      imageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Add border to image label
      String imageId = new File(postData[4]).getName().split("\\.")[0];

      System.out.println(postData[4]);
      ImageIcon imageIcon = new ImageIcon(
        new ImageIcon(postData[4])
          .getImage()
          .getScaledInstance(250, -1, Image.SCALE_SMOOTH)
      );
      imageLabel.setIcon(imageIcon);

      JLabel descriptionLabel = new JLabel(postData[1]);
      descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

      JLabel likesLabel = new JLabel(postData[2]);
      likesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

      JButton likeButton = new JButton("❤");
      likeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
      likeButton.setBackground(LIKE_BUTTON_COLOR); // Set the background color for the like button
      likeButton.setOpaque(true);
      likeButton.setBorderPainted(false); // Remove border
      likeButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            ImageLikesManager likesManager = new ImageLikesManager(imageId);
            likesManager.handleLikeAction(imageId, likesLabel);
          }
        }
      );

      commentLabel = new JLabel(postData[3]);
      commentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

      JButton commentButton = new JButton("☁");
      commentButton.setAlignmentX(Component.LEFT_ALIGNMENT);
      commentButton.setBackground(Color.GRAY);
      commentButton.setOpaque(true);
      commentButton.setBorderPainted(false);
      commentButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            CommentsUI comments = new CommentsUI(imageId);
            comments.saveDetails(imageId);
            comments.setVisible(true);
          }
        }
      );

      itemPanel.add(nameLabel);
      itemPanel.add(imageLabel);
      itemPanel.add(descriptionLabel);
      itemPanel.add(likesLabel);
      itemPanel.add(likeButton);
      itemPanel.add(commentLabel);
      itemPanel.add(commentButton);

      panel.add(itemPanel);

      // Make the image clickable
      imageLabel.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            displayImage(postData); // Call a method to switch to the image view
          }
        }
      );

      // Grey spacing panel
      JPanel spacingPanel = new JPanel();
      spacingPanel.setPreferredSize(new Dimension(WIDTH - 10, 5)); // Set the height for spacing
      spacingPanel.setBackground(new Color(230, 230, 230)); // Grey color for spacing
      panel.add(spacingPanel);
    }
  }

  private String[][] createSampleData() {
    String currentUser = "";
    try (
      BufferedReader reader = Files.newBufferedReader(
        Paths.get("data", "users.txt")
      )
    ) {
      String line = reader.readLine();
      if (line != null) {
        currentUser = line.split(":")[0].trim();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    String followedUsers = "";
    try (
      BufferedReader reader = Files.newBufferedReader(
        Paths.get("data", "following.txt")
      )
    ) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.startsWith(currentUser + ":")) {
          followedUsers = line.split(":")[1].trim();
          break;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Temporary structure to hold the data
    String[][] tempData = new String[100][]; // Assuming a maximum of 100 posts for simplicity
    int count = 0;

    try (
      BufferedReader reader = Files.newBufferedReader(
        Paths.get("img", "image_details.txt")
      )
    ) {
      String line;
      while ((line = reader.readLine()) != null && count < tempData.length) {
        String[] details = line.split(", ");
        String imagePoster = details[1].split(": ")[1];
        if (followedUsers.contains(imagePoster)) {
          String imagePath =
            "img/uploaded/" + details[0].split(": ")[1] + ".png"; // Assuming PNG format
          String description = details[2].split(": ")[1];
          String likes = "Likes: " + details[4].split(": ")[1];
          String comments = "Comments: " + details[5].split(": ")[1];

          tempData[count++] =
            new String[] {
              imagePoster,
              description,
              likes,
              comments,
              imagePath,
            };
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Transfer the data to the final array
    String[][] sampleData = new String[count][];
    System.arraycopy(tempData, 0, sampleData, 0, count);

    return sampleData;
  }

  private void displayImage(String[] postData) {
    imageViewPanel.removeAll(); // Clear previous content

    String imageId = new File(postData[3]).getName().split("\\.")[0];
    JLabel likesLabel = new JLabel(postData[2]); // Update this line

    // Display the image
    JLabel fullSizeImageLabel = new JLabel();
    fullSizeImageLabel.setHorizontalAlignment(JLabel.CENTER);

    ImageIcon imageIcon = new ImageIcon(
      new ImageIcon(postData[4])
        .getImage()
        .getScaledInstance(250, -1, Image.SCALE_SMOOTH)
    );
    fullSizeImageLabel.setIcon(imageIcon);

    // User Info
    JPanel userPanel = new JPanel();
    userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
    JLabel userName = new JLabel(postData[0]);
    userName.setFont(new Font("Arial", Font.BOLD, 18));
    userPanel.add(userName); // User Name

    JButton likeButton = new JButton("❤");
    likeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
    likeButton.setBackground(LIKE_BUTTON_COLOR); // Set the background color for the like button
    likeButton.setOpaque(true);
    likeButton.setBorderPainted(false); // Remove border
    likeButton.addActionListener(
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          ImageLikesManager likesManager = new ImageLikesManager(imageId);
          likesManager.handleLikeAction(imageId, likesLabel); // Update this line
          refreshDisplayImage(postData, imageId); // Refresh the view
        }
      }
    );

    // Information panel at the bottom
    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
    infoPanel.add(new JLabel(postData[1])); // Description
    infoPanel.add(new JLabel(postData[2])); // Likes
    infoPanel.add(likeButton);

    imageViewPanel.add(fullSizeImageLabel, BorderLayout.CENTER);
    imageViewPanel.add(infoPanel, BorderLayout.SOUTH);
    imageViewPanel.add(userPanel, BorderLayout.NORTH);

    imageViewPanel.revalidate();
    imageViewPanel.repaint();

    cardLayout.show(cardPanel, "ImageView"); // Switch to the image view
  }

  private void refreshDisplayImage(String[] postData, String imageId) {
    // Read updated likes count from image_details.txt
    try (
      BufferedReader reader = Files.newBufferedReader(
        Paths.get("img", "image_details.txt")
      )
    ) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.contains("ImageID: " + imageId)) {
          String likes = line.split(", ")[4].split(": ")[1];
          String comments = line.split(", ")[5].split(": ")[1];
          postData[2] = "Likes: " + likes;
          postData[3] = "Comments: " + comments;
          break;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    // Call displayImage with updated postData
    displayImage(postData);
  }
}
