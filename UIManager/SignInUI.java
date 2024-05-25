package UIManager;

import UserManager.LoginManager;
import UserManager.LoginProxy;
import UserManager.User;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
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

public class SignInUI extends UIManager {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 575;

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
        headerPanel = createHeaderPanel("Quackstagram");

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
        JPanel photoPanel = new JPanel();
        photoPanel.setLayout(new BorderLayout());
        photoPanel.add(lblPhoto, BorderLayout.CENTER);

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

        btnSignIn = new JButton("Sign-In");
        btnSignIn.addActionListener(this::onSignInClicked);
        btnSignIn.setBackground(new Color(255, 90, 95));
        btnSignIn.setForeground(Color.BLACK);
        btnSignIn.setFocusPainted(false);
        btnSignIn.setBorderPainted(false);
        btnSignIn.setFont(new Font("Arial", Font.BOLD, 14));
        JPanel registerPanel = new JPanel(new BorderLayout());
        registerPanel.setBackground(Color.WHITE);
        registerPanel.add(btnSignIn, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(fieldsPanel, BorderLayout.CENTER);
        add(registerPanel, BorderLayout.SOUTH);

        btnRegisterNow = new JButton("No Account? Register Now");
        btnRegisterNow.addActionListener(this::onRegisterNowClicked);
        btnRegisterNow.setBackground(Color.WHITE);
        btnRegisterNow.setForeground(Color.BLACK);
        btnRegisterNow.setFocusPainted(false);
        btnRegisterNow.setBorderPainted(false);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBackground(Color.white);
        buttonPanel.add(btnSignIn);
        buttonPanel.add(btnRegisterNow);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void onSignInClicked(ActionEvent event) {
        String enteredUsername = txtUsername.getText();
        String enteredPassword = String.valueOf(password.getPassword());
        if (LoginProxy.verifyCredentials(enteredUsername, enteredPassword)) {
            newUser = LoginManager.getUser();
            dispose();
            SwingUtilities.invokeLater(() -> {
                InstagramProfileUI profileUI = new InstagramProfileUI(newUser);
                profileUI.setVisible(true);
            });
        } else {
            System.out.println("Login failed.");
        }
    }

    private void onRegisterNowClicked(ActionEvent event) {
        dispose();
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
