package UIManager;

import UserManager.LoginProxy;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Sign up page and logic.
 */
public class SignUpUI extends UIManager {

  private static final int WIDTH = 300;
  private static final int HEIGHT = 500;

  private JTextField txtUsername, txtBio;
  private JButton btnRegister, btnUploadPhoto, btnSignIn;
  private JPasswordField password;
  private JLabel lblPhoto;
  private final String credentialsFilePath = "data/credentials.txt";
  private final String profilePhotoStoragePath = "img/storage/profile/";
  private String accountType = "Member";

  public SignUpUI() {
    setTitle("Quackstagram - Register");
    setSize(WIDTH, HEIGHT);
    setMinimumSize(new Dimension(WIDTH, HEIGHT));
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLayout(new BorderLayout(10, 10));
    setResizable(false);
    setLocationRelativeTo(null);
    initializeUI();
  }

  protected void initializeUI() {
    // Header with the Register label
    JPanel headerPanel = createHeaderPanel("Quackstagram");

    // Profile picture placeholder without border
    lblPhoto = new JLabel();
    lblPhoto.setPreferredSize(new Dimension(80, 80));
    lblPhoto.setHorizontalAlignment(JLabel.CENTER);
    lblPhoto.setVerticalAlignment(JLabel.CENTER);
    lblPhoto.setIcon(
      new ImageIcon(
        new ImageIcon("img/logos/logo.png")
          .getImage()
          .getScaledInstance(80, 80, Image.SCALE_SMOOTH)
      )
    );
    JPanel photoPanel = new JPanel(); // Use a panel to center the photo label
    photoPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    photoPanel.add(lblPhoto);

    // Text fields panel
    JPanel fieldsPanel = new JPanel();
    fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
    fieldsPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

    JPanel usernamePanel = new JPanel();
    JPanel passwordPanel = new JPanel();
    JPanel bioPanel = new JPanel();
    JLabel usernameLabel = new JLabel("Username:");
    JLabel passwordLabel = new JLabel("Password:");
    JLabel bioLabel = new JLabel("          Bio:");

    txtUsername = new JTextField(10);
    password = new JPasswordField(10);
    txtBio = new JTextField(10);

    usernamePanel.add(usernameLabel);
    usernamePanel.add(txtUsername);

    passwordPanel.add(passwordLabel);
    passwordPanel.add(password);

    bioPanel.add(bioLabel);
    bioPanel.add(txtBio);

    JCheckBox accountTypeCheckBox = new JCheckBox("Business                 ");
    // accountTypeCheckBox.setHorizontalTextPosition(SwingConstants.RIGHT);
    accountTypeCheckBox.addItemListener(
      new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (e.getStateChange() == ItemEvent.SELECTED) {
            accountType = "Business";
          }
        }
      }
    );

    fieldsPanel.add(Box.createVerticalStrut(10));
    fieldsPanel.add(photoPanel);
    fieldsPanel.add(Box.createVerticalStrut(10));
    fieldsPanel.add(usernamePanel);
    fieldsPanel.add(Box.createVerticalStrut(10));
    fieldsPanel.add(passwordPanel);
    fieldsPanel.add(Box.createVerticalStrut(10));
    fieldsPanel.add(bioPanel);
    fieldsPanel.add(accountTypeCheckBox);
    btnUploadPhoto = new JButton("Upload Photo");

    btnUploadPhoto.addActionListener(
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          handleProfilePictureUpload();
        }
      }
    );
    JPanel photoUploadPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    photoUploadPanel.add(btnUploadPhoto);
    fieldsPanel.add(photoUploadPanel);

    // Register button with black text
    btnRegister = new JButton("Register");
    btnRegister.addActionListener(this::onRegisterClicked);
    btnRegister.setBackground(new Color(255, 90, 95)); // Use a red color that matches the mockup
    btnRegister.setForeground(Color.BLACK); // Set the text color to black
    btnRegister.setFocusPainted(false);
    btnRegister.setBorderPainted(false);
    btnRegister.setFont(new Font("Arial", Font.BOLD, 14));
    JPanel registerPanel = new JPanel(new BorderLayout()); // Panel to contain the register button
    registerPanel.setBackground(Color.WHITE); // Background for the panel
    registerPanel.add(btnRegister, BorderLayout.CENTER);

    // Adding components to the frame
    add(headerPanel, BorderLayout.NORTH);
    add(fieldsPanel, BorderLayout.CENTER);
    add(registerPanel, BorderLayout.SOUTH);
    // Adding the sign in button to the register panel or another suitable panel
    btnSignIn = new JButton("Already have an account? Sign In");
    btnSignIn.addActionListener(
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          openSignInUI();
        }
      }
    );
    registerPanel.add(btnSignIn, BorderLayout.SOUTH);
  }

  private void onRegisterClicked(ActionEvent event) {
    String username = txtUsername.getText();
    String passwordToSave = String.valueOf(password.getPassword());
    String bio = txtBio.getText();

    if (doesUsernameExist(username)) {
      JOptionPane.showMessageDialog(
        this,
        "Username already exists. Please choose a different username.",
        "Error",
        JOptionPane.ERROR_MESSAGE
      );
    } else {
      LoginProxy.saveCredentials(username, passwordToSave, bio, accountType);
      handleProfilePictureUpload();
      dispose();

      // Open the SignInUI frame
      SwingUtilities.invokeLater(() -> {
        SignInUI signInFrame = new SignInUI();
        signInFrame.setVisible(true);
      });
    }
  }

  private boolean doesUsernameExist(String username) {
    try (
      BufferedReader reader = new BufferedReader(
        new FileReader(credentialsFilePath)
      )
    ) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.startsWith(username + ":")) {
          return true;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }

  // Method to handle profile picture upload
  private void handleProfilePictureUpload() {
    JFileChooser fileChooser = new JFileChooser();
    FileNameExtensionFilter filter = new FileNameExtensionFilter(
      "Image files",
      ImageIO.getReaderFileSuffixes()
    );
    fileChooser.setFileFilter(filter);
    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      File selectedFile = fileChooser.getSelectedFile();
      saveProfilePicture(selectedFile, txtUsername.getText());
    }
  }

  private void saveProfilePicture(File file, String username) {
    try {
      BufferedImage image = ImageIO.read(file);
      File outputFile = new File(profilePhotoStoragePath + username + ".png");
      ImageIO.write(image, "png", outputFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void openSignInUI() {
    // Close the SignUpUI frame
    dispose();

    // Open the SignInUI frame
    SwingUtilities.invokeLater(() -> {
      SignInUI signInFrame = new SignInUI();
      signInFrame.setVisible(true);
    });
  }
}
