package UIManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import UserManager.User;

public class ExploreUI extends UIManager {

    private static final int IMAGE_SIZE = 90; // Size for each image in the grid
    private JPanel headerPanel, navigationPanel, mainContentPanel;
    private String pageName = "Explore";

    public ExploreUI() {
        setTitle("Explore");
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        setLocationRelativeTo(null);
        initializeUI();
    }

    public void initializeUI() {
        getContentPane().removeAll(); // Clear existing components
        setLayout(new BorderLayout()); // Reset the layout manager

        headerPanel = createHeaderPanel(pageName);
        navigationPanel = createNavigationPanel(pageName);
        mainContentPanel = createMainContentPanel();

        // Add panels to the frame
        add(headerPanel, BorderLayout.NORTH);
        add(mainContentPanel, BorderLayout.CENTER);
        add(navigationPanel, BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    private JPanel createMainContentPanel() {
        // Create the main content panel with image grid

        // Image Grid
        JPanel imageGridPanel = new JPanel(new GridLayout(0, 3, 1, 1)); // 3 columns, auto rows

        // Load images from the uploaded folder
        File imageDir = new File("img/uploaded");
        if (imageDir.exists() && imageDir.isDirectory()) {
            File[] imageFiles = imageDir.listFiles((dir, name) ->
                name.matches(".*\\.(png|jpg|jpeg)")
            );
            if (imageFiles != null) {
                for (File imageFile : imageFiles) {
                    ImageIcon imageIcon = new ImageIcon(
                        new ImageIcon(imageFile.getPath())
                            .getImage()
                            .getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_SMOOTH)
                    );
                    JLabel imageLabel = new JLabel(imageIcon);
                    imageLabel.addMouseListener(
                        new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                displayImage(imageFile.getPath()); // Call method to display the clicked image
                            }
                        }
                    );
                    imageGridPanel.add(imageLabel);
                }
            }
        }

        JScrollPane scrollPane = new JScrollPane(imageGridPanel);
        scrollPane.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        // Main content panel that holds both the search bar and the image grid
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(
            new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS)
        );
        mainContentPanel.add(scrollPane); // This will stretch to take up remaining space
        return mainContentPanel;
    }

    private void displayImage(String imagePath) {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        // Add the header and navigation panels back
        add(headerPanel, BorderLayout.NORTH);
        add(createNavigationPanel(pageName), BorderLayout.SOUTH);

        // Extract image ID from the imagePath
        String imageId = new File(imagePath).getName().split("\\.")[0];

        // Fetch image details from the database
        ImageDetails details = ImageDetails.fetchImageDetails(imageId);
        String username = details.getUsername();
        String bio = details.getBio();
        String timestampString = details.getTimestampString();
        int likes = details.getLikes();

        // Calculate time since posting
        String timeSincePosting = "Unknown time";
        if (timestampString != null && !timestampString.isEmpty()) {
            LocalDateTime timestamp = LocalDateTime.parse(
                timestampString,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            );
            LocalDateTime now = LocalDateTime.now();
            long days = ChronoUnit.DAYS.between(timestamp, now);
            timeSincePosting = days + " day" + (days != 1 ? "s" : "") + " ago";
        }

        // Back button
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(70, 30));
        backButton.addActionListener(e -> {
            getContentPane().removeAll();
            add(headerPanel, BorderLayout.NORTH);
            add(createMainContentPanel(), BorderLayout.CENTER);
            add(createNavigationPanel(pageName), BorderLayout.SOUTH);
            revalidate();
            repaint();
        });

        // Top panel for username and time since posting
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton usernameLabel = new JButton(username);
        usernameLabel.setPreferredSize(new Dimension(70, 30));
        JLabel timeLabel = new JLabel(timeSincePosting);
        timeLabel.setHorizontalAlignment(JLabel.RIGHT);
        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(usernameLabel, BorderLayout.CENTER);
        topPanel.add(timeLabel, BorderLayout.EAST);

        // Prepare the image for display
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        ImageIcon imageIcon = new ImageIcon(
            new ImageIcon(imagePath)
                .getImage()
                .getScaledInstance(250, -1, Image.SCALE_SMOOTH)
        );
        imageLabel.setIcon(imageIcon);

        // Bottom panel for bio and likes
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JTextArea bioTextArea = new JTextArea(bio);
        bioTextArea.setEditable(false);
        JLabel likesLabel = new JLabel("Likes: " + likes);
        bottomPanel.add(bioTextArea, BorderLayout.CENTER);
        bottomPanel.add(likesLabel, BorderLayout.SOUTH);

        // Adding the components to the frame
        add(topPanel, BorderLayout.NORTH);
        add(imageLabel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Re-add the header and navigation panels
        add(headerPanel, BorderLayout.NORTH);
        add(createNavigationPanel(pageName), BorderLayout.SOUTH);

        final String finalUsername = username;

        usernameLabel.addActionListener(e -> {
            User user = new User(finalUsername); // Assuming User class has a constructor that takes a username
            InstagramProfileUI profileUI = new InstagramProfileUI(user);
            profileUI.setVisible(true);
            dispose(); // Close the current frame
        });

        // Container panel for image and details
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.add(topPanel, BorderLayout.NORTH);
        containerPanel.add(imageLabel, BorderLayout.CENTER);
        containerPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(containerPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}
