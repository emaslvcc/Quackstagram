package UIManager;

import DatabaseManager.DatabaseUploader;
import UserManager.LoginManager;
import UserManager.LoginProxy;
import UserManager.User;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Sign in page and logic.
 */
public class SignInUI extends UIManager {

  private static final int WIDTH = 300;
  private static final int HEIGHT = 575; // height diff for mac and windows

  private JTextField txtUsername;
  private JPasswordField password;
  private JButton btnSignIn, btnRegisterNow;
  private JLabel lblPhoto;
  public User newUser;
  private JPanel headerPanel;

  public SignInUI() {
    setTitle("Quackstagram");
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
    headerPanel = createHeaderPanel("Quackstagram");

    // Profile picture placeholder without border
    lblPhoto = new JLabel();
    lblPhoto.setPreferredSize(new Dimension(250, 250));
    lblPhoto.setHorizontalAlignment(JLabel.CENTER);
    lblPhoto.setVerticalAlignment(JLabel.CENTER);
    lblPhoto.setIcon(
      new ImageIcon(
        new ImageIcon("img/logos/logo.png")
          .getImage()
          .getScaledInstance(250, 250, Image.SCALE_SMOOTH)
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
    JLabel usernameLabel = new JLabel("Username:");
    JLabel passwordLabel = new JLabel("Password:");

    txtUsername = new JTextField(15);
    password = new JPasswordField(15);

    usernamePanel.add(usernameLabel);
    usernamePanel.add(txtUsername);

    passwordPanel.add(passwordLabel);
    passwordPanel.add(password);

    fieldsPanel.add(Box.createVerticalStrut(10));
    fieldsPanel.add(photoPanel);
    fieldsPanel.add(Box.createVerticalStrut(10));
    fieldsPanel.add(usernamePanel);
    fieldsPanel.add(Box.createVerticalStrut(10));
    fieldsPanel.add(passwordPanel);
    fieldsPanel.add(Box.createVerticalStrut(10));

    // Register button with black text
    btnSignIn = new JButton("Sign-In");
    btnSignIn.addActionListener(e -> {
      try {
        onSignInClicked(e);
      } catch (ClassNotFoundException | SQLException a) {
        // TODO Auto-generated catch block
        a.printStackTrace();
      }
    });
    btnSignIn.setBackground(new Color(255, 90, 95)); // Use a red color that matches the mockup
    btnSignIn.setForeground(Color.BLACK); // Set the text color to black
    btnSignIn.setFocusPainted(false);
    btnSignIn.setBorderPainted(false);
    btnSignIn.setFont(new Font("Arial", Font.BOLD, 14));
    JPanel registerPanel = new JPanel(new BorderLayout()); // Panel to contain the register button
    registerPanel.setBackground(Color.WHITE); // Background for the panel
    registerPanel.add(btnSignIn, BorderLayout.CENTER);

    // Adding components to the frame
    add(headerPanel, BorderLayout.NORTH);
    add(fieldsPanel, BorderLayout.CENTER);
    add(registerPanel, BorderLayout.SOUTH);

    // New button for navigating to SignUpUI
    btnRegisterNow = new JButton("No Account? Register Now");
    btnRegisterNow.addActionListener(this::onRegisterNowClicked);
    btnRegisterNow.setBackground(Color.WHITE); // Set a different color for distinction
    btnRegisterNow.setForeground(Color.BLACK);
    btnRegisterNow.setFocusPainted(false);
    btnRegisterNow.setBorderPainted(false);

    // Panel to hold both buttons
    JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10)); // Grid layout with 1 row, 2 columns
    buttonPanel.setBackground(Color.white);
    buttonPanel.add(btnSignIn);
    buttonPanel.add(btnRegisterNow);

    // Adding the button panel to the frame
    add(buttonPanel, BorderLayout.SOUTH);
  }

  private void onSignInClicked(ActionEvent event)
    throws ClassNotFoundException, SQLException {
    String enteredUsername = txtUsername.getText();
    String enteredPassword = String.valueOf(password.getPassword());
    DatabaseUploader db = new DatabaseUploader();
    // System.out.println(enteredUsername + " <-> " + enteredPassword);
    if (db.verifyCredentials(enteredUsername, enteredPassword)) {
      System.out.println("Login Success.");
      newUser = db.getUser(enteredUsername);
      LoginManager.saveUserInformation(newUser);

      // Close the SignUpUI frame
      dispose();

      // Open the SignInUI frame
      SwingUtilities.invokeLater(() -> {
        InstagramProfileUI profileUI;
        try {
          profileUI = new InstagramProfileUI(newUser);
          profileUI.setVisible(true);
        } catch (ClassNotFoundException | SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      });
    } else {
      System.out.println("Login Failed.");
    }
  }

  private void onRegisterNowClicked(ActionEvent event) {
    // Close the SignInUI frame
    dispose();

    // Open the SignUpUI frame
    SwingUtilities.invokeLater(() -> {
      SignUpUI signUpFrame = new SignUpUI();
      signUpFrame.setVisible(true);
    });
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      SignInUI frame = new SignInUI();
      frame.setVisible(true);
    });
  }
}
