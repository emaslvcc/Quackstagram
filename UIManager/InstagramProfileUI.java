package UIManager;

import UserManager.User;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import DatabaseManager.UpdateDatabase;

public class InstagramProfileUI extends UIManager {

    private static final int PROFILE_IMAGE_SIZE = 80; // Adjusted size for the profile image to match UI
    private static final int GRID_IMAGE_SIZE = 90; // Static size for grid images
    private JPanel contentPanel; // Panel to display the image grid or the clicked image
    private JPanel headerPanel; // Panel for the header
    private JPanel navigationPanel; // Panel for the navigation
    private User currentUser; // User object to store the current user's information
    private String pageName = "Profile";

    public InstagramProfileUI(User user) {
        this.currentUser = user;
        // Initialize counts
        fetchUserStatistics();

        setTitle("DACS Profile");
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        setLocationRelativeTo(null);
        contentPanel = new JPanel();
        headerPanel = createProfileHeaderPanel(); // Initialize header panel
        navigationPanel = createNavigationPanel(pageName); // Initialize navigation panel

        initializeUI();
    }

    public InstagramProfileUI() {
        setTitle("DACS Profile");
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        contentPanel = new JPanel();
        headerPanel = createProfileHeaderPanel(); // Initialize header panel
        navigationPanel = createNavigationPanel(pageName); // Initialize navigation panel
        initializeUI();
    }

    @Override
    protected void initializeUI() {
        getContentPane().removeAll(); // Clear existing components

        // Re-add the header and navigation panels
        add(headerPanel, BorderLayout.NORTH);
        add(navigationPanel, BorderLayout.SOUTH);

        // Initialize the image grid
        initializeImageGrid();

        revalidate();
        repaint();
    }

    private void fetchUserStatistics() {
        try (Connection conn = UpdateDatabase.getConnection()) {
            // Fetch image count
            String imageCountQuery = "SELECT COUNT(*) FROM posts WHERE owner = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(imageCountQuery)) {
                pstmt.setString(1, currentUser.getUsername());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    currentUser.setPostCount(rs.getInt(1));
                }
            }

            // Fetch followers count
            String followersCountQuery = "SELECT COUNT(*) FROM user_following WHERE username2 = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(followersCountQuery)) {
                pstmt.setString(1, currentUser.getUsername());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    currentUser.setFollowersCount(rs.getInt(1));
                }
            }

            // Fetch following count
            String followingCountQuery = "SELECT COUNT(*) FROM user_following WHERE username1 = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(followingCountQuery)) {
                pstmt.setString(1, currentUser.getUsername());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    currentUser.setFollowingCount(rs.getInt(1));
                }
            }

            // Fetch bio
            String bioQuery = "SELECT userbio FROM user_info WHERE username = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(bioQuery)) {
                pstmt.setString(1, currentUser.getUsername());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    currentUser.setBio(rs.getString(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected JPanel createProfileHeaderPanel() {
        boolean isCurrentUser = false;
        String loggedInUsername = "";

        // Read the logged-in user's username from users.txt
        try (BufferedReader reader = Files.newBufferedReader(
                Paths.get("data", "users.txt"))) {
            String line = reader.readLine();
            if (line != null) {
                loggedInUsername = line.split(":")[0].trim();
                isCurrentUser = loggedInUsername.equals(currentUser.getUsername());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Header Panel
        JPanel headerPanel = new JPanel();
        try (Stream<String> lines = Files.lines(Paths.get("data", "users.txt"))) {
            isCurrentUser = lines.anyMatch(line -> line.startsWith(currentUser.getUsername() + ":"));
        } catch (IOException e) {
            e.printStackTrace(); // Log or handle the exception as appropriate
        }

        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.GRAY);

        // Top Part of the Header (Profile Image, Stats, Follow Button)
        JPanel topHeaderPanel = new JPanel(new BorderLayout(10, 0));
        topHeaderPanel.setBackground(new Color(249, 249, 249));

        // Profile image
        ImageIcon profileIcon = new ImageIcon(
                new ImageIcon("img/storage/profile/" + currentUser.getUsername() + ".png")
                        .getImage()
                        .getScaledInstance(
                                PROFILE_IMAGE_SIZE,
                                PROFILE_IMAGE_SIZE,
                                Image.SCALE_SMOOTH
                        )
        );
        JLabel profileImage = new JLabel(profileIcon);
        profileImage.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topHeaderPanel.add(profileImage, BorderLayout.WEST);

        // Stats Panel
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        statsPanel.setBackground(new Color(249, 249, 249));
        statsPanel.add(
                createStatLabel(Integer.toString(currentUser.getPostsCount()), "Posts")
        );
        statsPanel.add(
                createStatLabel(
                        Integer.toString(currentUser.getFollowersCount()),
                        "Followers"
                )
        );
        statsPanel.add(
                createStatLabel(
                        Integer.toString(currentUser.getFollowingCount()),
                        "Following"
                )
        );
        statsPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 0)); // Add some vertical padding

        // Follow or Edit Profile Button
        JButton followButton;
        if (isCurrentUser) {
            followButton = new JButton("Edit Profile");
        } else {
            followButton = new JButton("Follow");
            updateFollowButtonState(followButton, loggedInUsername, currentUser.getUsername());
            followButton.addActionListener(e -> {
                handleFollowAction(currentUser.getUsername());
                followButton.setText("Following");
            });
        }

        followButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        followButton.setFont(new Font("Arial", Font.BOLD, 12));
        followButton.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, followButton.getMinimumSize().height)
        );
        followButton.setBackground(new Color(225, 228, 232)); // A soft, appealing color that complements the UI
        followButton.setForeground(Color.BLACK);
        followButton.setOpaque(true);
        followButton.setBorderPainted(false);
        followButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Add some vertical padding

        // Add Stats and Follow Button to a combined Panel
        JPanel statsFollowPanel = new JPanel();
        statsFollowPanel.setLayout(
                new BoxLayout(statsFollowPanel, BoxLayout.Y_AXIS)
        );
        statsFollowPanel.add(statsPanel);
        statsFollowPanel.add(followButton);
        topHeaderPanel.add(statsFollowPanel, BorderLayout.CENTER);

        headerPanel.add(topHeaderPanel);

        // Profile Name and Bio Panel
        JPanel profileNameAndBioPanel = new JPanel();
        profileNameAndBioPanel.setLayout(new BorderLayout());
        profileNameAndBioPanel.setBackground(new Color(249, 249, 249));

        JLabel profileNameLabel = new JLabel(currentUser.getUsername());
        profileNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        profileNameLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10)); // Padding on the sides

        JTextArea profileBio = new JTextArea(currentUser.getBio());
        profileBio.setEditable(false);
        profileBio.setFont(new Font("Arial", Font.PLAIN, 12));
        profileBio.setBackground(new Color(249, 249, 249));
        profileBio.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); // Padding on the sides

        JLabel accountType = new JLabel(); // Placeholder for account type

        profileNameAndBioPanel.add(profileNameLabel, BorderLayout.NORTH);
        profileNameAndBioPanel.add(profileBio, BorderLayout.CENTER);
        profileNameAndBioPanel.add(accountType, BorderLayout.SOUTH);

        headerPanel.add(profileNameAndBioPanel);

        return headerPanel;
    }

    private void updateFollowButtonState(JButton followButton, String loggedInUsername, String usernameToFollow) {
        String query = "SELECT 1 FROM user_following WHERE username1 = ? AND username2 = ?";
        try (Connection conn = UpdateDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, loggedInUsername);
            pstmt.setString(2, usernameToFollow);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                followButton.setText("Following");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleFollowAction(String usernameToFollow) {
        Path usersFilePath = Paths.get("data", "users.txt");
        String currentUserUsername = "";

        try {
            // Read the current user's username from users.txt
            try (BufferedReader reader = Files.newBufferedReader(usersFilePath)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(":");
                    currentUserUsername = parts[0];
                }
            }

            // If currentUserUsername is not empty, update the database
            if (!currentUserUsername.isEmpty()) {
                // Get the current timestamp and format it as a string
                String currentTimestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault()).format(Instant.now());
                UpdateDatabase.updateUserFollowing(currentUserUsername, usernameToFollow, currentTimestamp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeImageGrid() {
        contentPanel.removeAll(); // Clear existing content
        contentPanel.setLayout(new GridLayout(0, 3, 5, 5)); // Grid layout for image grid

        Path imageDir = Paths.get("img", "uploaded");
        try (Stream<Path> paths = Files.list(imageDir)) {
            paths.filter(path ->
                    path.getFileName().toString().startsWith(currentUser.getUsername() + "_")
            ).forEach(path -> {
                ImageIcon imageIcon = new ImageIcon(
                        new ImageIcon(path.toString())
                                .getImage()
                                .getScaledInstance(
                                        GRID_IMAGE_SIZE,
                                        GRID_IMAGE_SIZE,
                                        Image.SCALE_SMOOTH
                                )
                );
                JLabel imageLabel = new JLabel(imageIcon);
                imageLabel.addMouseListener(
                        new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                displayImage(imageIcon); // Call method to display the clicked image
                            }
                        }
                );
                contentPanel.add(imageLabel);
            });
        } catch (IOException ex) {
            ex.printStackTrace();
            // Handle exception (e.g., show a message or log)
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        add(scrollPane, BorderLayout.CENTER); // Add the scroll pane to the center

        revalidate();
        repaint();
    }

    private void displayImage(ImageIcon imageIcon) {
        contentPanel.removeAll(); // Remove existing content
        contentPanel.setLayout(new BorderLayout()); // Change layout for image display

        JLabel fullSizeImageLabel = new JLabel(imageIcon);
        fullSizeImageLabel.setHorizontalAlignment(JLabel.CENTER);
        contentPanel.add(fullSizeImageLabel, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            getContentPane().removeAll(); // Remove all components from the frame
            initializeUI(); // Re-initialize the UI
        });
        contentPanel.add(backButton, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    private JLabel createStatLabel(String number, String text) {
        JLabel label = new JLabel(
                "<html><div style='text-align: center;'>" +
                        number +
                        "<br/>" +
                        text +
                        "</div></html>",
                SwingConstants.CENTER
        );
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(Color.BLACK);
        return label;
    }
}
