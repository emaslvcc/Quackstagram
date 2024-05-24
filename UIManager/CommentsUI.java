package UIManager;

import DatabaseManager.DatabaseUploader;
import PostManager.ImageCommentsManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.*;

/**
 * Responsible for comment functionality.
 */
public class CommentsUI extends JFrame {

  private JTextField newCommentField;
  private JPanel commentsPanel;
  private JPanel newCommentsPanel;
  private JButton postButton;
  private JLabel commentsLabel;

  private ArrayList<String> user = new ArrayList<>();
  private ArrayList<String> comments = new ArrayList<>();

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
          try {
            ImageCommentsManager.postComment(comment, imageId);
          } catch (ClassNotFoundException | SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
          }
          try {
            saveDetails(imageId);
          } catch (ClassNotFoundException | SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
          }
          // updateFile(imageId);
        }
      }
    );

    JScrollPane scrollPane = new JScrollPane(commentsPanel);
    getContentPane().add(scrollPane, BorderLayout.CENTER);
    getContentPane().add(newCommentsPanel, BorderLayout.SOUTH);
  }

  public void saveDetails(String imageId)
    throws ClassNotFoundException, SQLException {
    user.clear();
    comments.clear();

    DatabaseUploader db = new DatabaseUploader();
    db.getComments(imageId, user, comments);
    displayComments(user, comments);
  }

  public void displayComments(
    ArrayList<String> user,
    ArrayList<String> comments
  ) {
    StringBuilder string = new StringBuilder("<html>");
    for (int i = 0; i < user.size(); i++) {
      string
        .append(user.get(i))
        .append(": ")
        .append(comments.get(i))
        .append("<br>");
    }
    string.append("</html>");
    commentsLabel.setText(string.toString());
  }
}
