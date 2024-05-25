package UIManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.*;
import DatabaseManager.UpdateDatabase;

public class CommentsUI extends JFrame {

    private JTextField newCommentField;
    private JPanel commentsPanel;
    private JPanel newCommentsPanel;
    private JButton postButton;
    private JLabel commentsLabel;

    public CommentsUI(String imageId) {
        setTitle("Comment Viewer");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        commentsPanel = new JPanel();
        newCommentsPanel = new JPanel();
        commentsLabel = new JLabel();
        commentsLabel.setPreferredSize(new Dimension(350, 250));

        postButton = new JButton("Post");
        newCommentField = new JTextField();
        newCommentField.setEditable(true);
        newCommentField.setPreferredSize(new Dimension(300, 30));

        commentsPanel.add(commentsLabel);
        newCommentsPanel.add(newCommentField);
        newCommentsPanel.add(postButton);

        postButton.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String comment = newCommentField.getText();
                    postComment(comment, imageId);
                    updateComments(imageId);
                }
            }
        );

        JScrollPane scrollPane = new JScrollPane(commentsPanel);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(newCommentsPanel, BorderLayout.SOUTH);

        updateComments(imageId);
    }

    public void updateComments(String imageId) {
        ArrayList<String[]> commentsData = fetchComments(imageId);
        displayComments(commentsData);
    }

    public ArrayList<String[]> fetchComments(String imageId) {
        ArrayList<String[]> commentsData = new ArrayList<>();
        String query = "SELECT commenter, comment FROM comments WHERE post_id = ?";
        try (Connection conn = UpdateDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, imageId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String[] commentData = {rs.getString("commenter"), rs.getString("comment")};
                commentsData.add(commentData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commentsData;
    }

    public void postComment(String comment, String imageId) {
        String currentUser = retrieveUser();
        String query = "INSERT INTO comments (time_stamp, post_id, comment, commenter) VALUES (?, ?, ?, ?)";
        try (Connection conn = UpdateDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            pstmt.setString(2, imageId);
            pstmt.setString(3, comment);
            pstmt.setString(4, currentUser);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void displayComments(ArrayList<String[]> commentsData) {
        StringBuilder string = new StringBuilder("<html>");
        for (String[] comment : commentsData) {
            string.append(comment[0]).append(": ").append(comment[1]).append("<br>");
        }
        string.append("</html>");
        commentsLabel.setText(string.toString());
    }

    public String retrieveUser() {
        try (BufferedReader userReader = Files.newBufferedReader(
            Paths.get("data", "users.txt")
        )) {
            String line = userReader.readLine();
            if (line != null) {
                return line.split(":")[0].trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
