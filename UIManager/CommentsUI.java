package UIManager;

import PostManager.ImageCommentsManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.swing.*;

public class CommentsUI extends JFrame {

  private JTextField newCommentField;
  private JPanel commentsPanel;
  private JPanel newCommentsPanel;
  private JButton postButton;
  private JLabel commentsLabel;
  private static Path commentsPath = Paths.get("data", "comments.txt");
  private static Path detailsPath = Paths.get("img", "image_details.txt");

  private static StringBuilder newContent = new StringBuilder();
  private static boolean updated = false;
  private static int commentsCount;

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
          ImageCommentsManager.postComment(comment, imageId);
          saveDetails(imageId);
          updateFile(imageId);
        }
      }
    );

    JScrollPane scrollPane = new JScrollPane(commentsPanel);
    getContentPane().add(scrollPane, BorderLayout.CENTER);
    getContentPane().add(newCommentsPanel, BorderLayout.SOUTH);
  }

  public void saveDetails(String imageId) {
    user.clear();
    comments.clear();

    try (BufferedReader reader = Files.newBufferedReader(commentsPath)) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.contains(imageId)) {
          String[] part = line.split("; ");
          if (part.length >= 4) {
            if (line.contains(imageId)) {
              user.add(part[1]);
              comments.add(part[3]);
            }
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
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

  public void updateFile(String imageId) {
    updated = false;
    newContent.setLength(0);

    try (BufferedReader reader = Files.newBufferedReader(detailsPath)) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.contains("ImageID: " + imageId)) {
          String[] parts = line.split(", ");
          commentsCount = Integer.parseInt(parts[5].split(": ")[1]);
          commentsCount++; // Increment the comment count
          parts[5] = "Comments: " + commentsCount;
          line = String.join(", ", parts);

          // Update the UI
          updated = true;
        }
        newContent.append(line).append("\n");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Write updated likes back to image_details.txt
    if (updated) {
      try (BufferedWriter writer = Files.newBufferedWriter(detailsPath)) {
        writer.write(newContent.toString());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
