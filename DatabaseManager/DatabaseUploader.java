package DatabaseManager;

import PostManager.Post;
import UserManager.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DatabaseUploader {

  public static Connection conn = null;

  public DatabaseUploader() throws ClassNotFoundException, SQLException {
    conn = getConnection();
  }

  public static void main(String[] args)
    throws SQLException, ClassNotFoundException {
    // conn = getConnection();
    // updateMultipleCredentials();
    // updateMultiplePosts();
    // updateMultipleComments();
  }

  public static Connection getConnection()
    throws SQLException, ClassNotFoundException {
    String userName = "root";
    String password = "";
    String db_URL = "jdbc:mysql://localhost:3306/Quackstagram";

    Properties connectionProps = new Properties();
    connectionProps.put("user", userName);
    connectionProps.put("password", password);
    Class.forName("com.mysql.cj.jdbc.Driver");
    conn = DriverManager.getConnection(db_URL, connectionProps);

    System.out.println("Connected to database.");
    return conn;
  }

  public static void updateMultipleComments() {
    try (Statement stmt = conn.createStatement()) {
      Path filePath = Paths.get("data/comments.txt");
      Charset charset = StandardCharsets.UTF_8;

      try (
        BufferedReader bufferedReader = Files.newBufferedReader(
          filePath,
          charset
        )
      ) {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
          String[] separated = line.split("; ");
          String image_owner = separated[0];
          String image_commenter = separated[1];
          String image_id = separated[2];
          String comment = separated[3];
          String values =
            "values('" +
            image_owner +
            "', '" +
            image_commenter +
            "', '" +
            image_id +
            "', '" +
            comment +
            "')";
          stmt.executeUpdate("insert into COMMENTS " + values);
        }
      } catch (IOException ex) {
        System.out.format("I/O error: %s%n", ex);
      }
    } catch (SQLException e) {
      DatabaseUploader.printSQLException(e);
    }
    System.out.println("All comments added to database.");
  }

  public void updateComment(String currentUser, String postId, String comment) {
    String timestamp = LocalDateTime
      .now()
      .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    try (Statement stmt = conn.createStatement()) {
      String values =
        "values('" +
        currentUser +
        "', '" +
        timestamp +
        "', '" +
        postId +
        "', '" +
        comment +
        "')";
      stmt.executeUpdate("insert into COMMENTS " + values);

      // Increment the comment_count in the POSTS table
      String updatePostQuery =
        "UPDATE posts SET comment_count = comment_count + 1 WHERE post_id = ?";
      try (
        PreparedStatement updateStmt = conn.prepareStatement(updatePostQuery)
      ) {
        updateStmt.setString(1, postId);
        updateStmt.executeUpdate();
      }
    } catch (SQLException e) {
      DatabaseUploader.printSQLException(e);
    }
    System.out.println("Comment added to database.");
  }

  public void getComments(
    String postId,
    ArrayList<String> commenterUserIds,
    ArrayList<String> comments
  ) {
    String query =
      "SELECT commenter_user_id, comment FROM comments WHERE post_id = ?";
    try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
      preparedStatement.setString(1, postId);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          commenterUserIds.add(resultSet.getString("commenter_user_id"));
          comments.add(resultSet.getString("comment"));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public int getCommentCount(String postId) {
    int commentCount = 0;
    String query = "SELECT COUNT(*) AS count FROM comments WHERE post_id = ?";
    try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
      preparedStatement.setString(1, postId);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          commentCount = resultSet.getInt("count");
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return commentCount;
  }

  public boolean doesUsernameExist(String username) {
    String query = "SELECT 1 FROM users WHERE username = ?";
    try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
      preparedStatement.setString(1, username);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          System.out.println("Username already exists.");
          return true;
        } else {
          System.out.println("Username does not exist.");
          return false;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public boolean addUser(
    String username,
    String password,
    String bio,
    String accountType
  ) {
    String query =
      "INSERT INTO users (username, pw, bio, account_type) " +
      "VALUES (?, ?, ?, ?)";
    try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
      preparedStatement.setString(1, username);
      preparedStatement.setString(2, password);
      preparedStatement.setString(3, bio);
      preparedStatement.setString(4, accountType);
      int rowsAffected = preparedStatement.executeUpdate();
      if (rowsAffected > 0) {
        System.out.println("User added.");
        return true;
      } else {
        System.out.println("Failed to add user.");
        return false;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean verifyCredentials(String username, String password) {
    String query = "SELECT 1 FROM users WHERE username = ? AND pw = ?";
    try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
      preparedStatement.setString(1, username);
      preparedStatement.setString(2, password);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          System.out.println("Credentials verified");
          return true;
        } else {
          System.out.println("Invalid credentials");
          return false;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public User getUser(String username) {
    String query =
      "SELECT username, pw, bio, account_type FROM users WHERE username = ?";
    try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
      preparedStatement.setString(1, username);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          String retrievedUsername = resultSet.getString("username");
          String retrievedPassword = resultSet.getString("pw");
          String retrievedBio = resultSet.getString("bio");
          String retrievedAccountType = resultSet.getString("account_type");
          System.out.println("User information retrieved.");
          return new User(
            retrievedUsername,
            retrievedPassword,
            retrievedBio,
            retrievedAccountType
          );
        } else {
          System.out.println("User not found");
          return null;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public String getUserBio(String username) {
    String query = "SELECT bio FROM users WHERE username = ?";
    try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
      preparedStatement.setString(1, username);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          String bio = resultSet.getString("bio");
          System.out.println("Bio retrieved.");
          return bio;
        } else {
          return null;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public void like(String liker, String postId) {
    try (Statement stmt = conn.createStatement()) {
      String values = "values('" + liker + "', '" + postId + "')";
      stmt.executeUpdate("insert into LIKES " + values);
      // Increment the comment_count in the POSTS table
      String updatePostQuery =
        "UPDATE posts SET like_count = like_count + 1 WHERE post_id = ?";
      try (
        PreparedStatement updateStmt = conn.prepareStatement(updatePostQuery)
      ) {
        updateStmt.setString(1, postId);
        updateStmt.executeUpdate();
      }
    } catch (SQLException e) {
      DatabaseUploader.printSQLException(e);
    }
    System.out.println("Like added to database.");
  }

  public boolean alreadyLiked(String userId, String postId) {
    String query =
      "SELECT 1 FROM likes WHERE liker_user_id = ? AND post_id = ?";
    try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
      preparedStatement.setString(1, userId);
      preparedStatement.setString(2, postId);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          System.out.println("Already liked.");
          return true;
        } else {
          System.out.println("Not liked.");
          return false;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public List<String[]> getLikesByUserId(String userId) {
    List<String[]> likes = new ArrayList<>();
    String query =
      "SELECT likes.post_id, likes.liker_user_id " +
      "FROM likes " +
      "INNER JOIN posts ON likes.post_id = posts.post_id " +
      "WHERE posts.user_id = ?";
    try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
      preparedStatement.setString(1, userId);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          String likerUserId = resultSet.getString("liker_user_id");
          likes.add(new String[] { likerUserId });
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      // Handle exception (you might want to log this or rethrow as a custom exception)
    }
    System.out.println("Likes retrieved.");
    return likes;
  }

  public void follow(String follower, String following) {
    String timestamp = LocalDateTime
      .now()
      .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    try (Statement stmt = conn.createStatement()) {
      String values =
        "values('" + follower + "', '" + following + "', '" + timestamp + "')";
      stmt.executeUpdate("insert into FOLLOWS " + values);
    } catch (SQLException e) {
      DatabaseUploader.printSQLException(e);
    }
    System.out.println("Follow added to database.");
  }

  public static void updateMultiplePosts() {
    try (Statement stmt = conn.createStatement()) {
      Path filePath = Paths.get("img/image_details.txt");
      Charset charset = StandardCharsets.UTF_8;

      try (
        BufferedReader bufferedReader = Files.newBufferedReader(
          filePath,
          charset
        )
      ) {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
          String[] details = line.split(", ");
          String imageID = "";
          String username = "";
          String caption = "";
          String timestamp = "";
          int likes = 0;
          int comments = 0;

          for (String detail : details) {
            if (detail.startsWith("ImageID: ")) {
              imageID = detail.substring("ImageID: ".length());
            } else if (detail.startsWith("Username: ")) {
              username = detail.substring("Username: ".length());
            } else if (detail.startsWith("Bio: ")) {
              caption = detail.substring("Bio: ".length());
            } else if (detail.startsWith("Timestamp: ")) {
              timestamp = detail.substring("Timestamp: ".length());
            } else if (detail.startsWith("Likes: ")) {
              likes = Integer.parseInt(detail.substring("Likes: ".length()));
            } else if (detail.startsWith("Comments: ")) {
              comments =
                Integer.parseInt(detail.substring("Comments: ".length()));
            }
          }
          String values =
            "values('" +
            imageID +
            "', '" +
            username +
            "', '" +
            caption +
            "', '" +
            likes +
            "', '" +
            comments +
            "', '" +
            timestamp +
            "')";
          stmt.executeUpdate("insert into POSTS " + values);
        }
      } catch (IOException ex) {
        System.out.format("I/O error: %s%n", ex);
      }
    } catch (SQLException e) {
      DatabaseUploader.printSQLException(e);
    }
    System.out.println("All posts added to database.");
  }

  public boolean alreadyFollowed(String follower, String following) {
    String query =
      "SELECT 1 FROM follows WHERE follower_user_id = ? AND following_user_id = ?";
    try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
      preparedStatement.setString(1, follower);
      preparedStatement.setString(2, following);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          System.out.println("Already followed.");
          return true;
        } else {
          System.out.println("Not followed.");
          return false;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public int[] getUserStats(String userId) {
    int[] stats = new int[2];
    String query =
      "SELECT followers_count, following_count FROM users WHERE username = ?";
    try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
      preparedStatement.setString(1, userId);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          stats[0] = resultSet.getInt("followers_count");
          stats[1] = resultSet.getInt("following_count");
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return stats;
  }

  public String getFollowingUserIds(String followerUserId) {
    StringBuilder followingUserIds = new StringBuilder();
    String query =
      "SELECT following_user_id FROM follows WHERE follower_user_id = ?";

    try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
      preparedStatement.setString(1, followerUserId);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          if (followingUserIds.length() > 0) {
            followingUserIds.append(",");
          }
          followingUserIds.append(resultSet.getString("following_user_id"));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return followingUserIds.toString();
  }

  public List<Post> getAllPosts() {
    List<Post> posts = new ArrayList<>();
    String query =
      "SELECT post_id, user_id, caption, like_count, comment_count FROM posts";

    try (
      PreparedStatement preparedStatement = conn.prepareStatement(query);
      ResultSet resultSet = preparedStatement.executeQuery()
    ) {
      while (resultSet.next()) {
        String postId = resultSet.getString("post_id");
        String userId = resultSet.getString("user_id");
        String caption = resultSet.getString("caption");
        int likeCount = resultSet.getInt("like_count");
        int commentCount = resultSet.getInt("comment_count");

        Post post = new Post(postId, userId, caption, likeCount, commentCount);
        posts.add(post);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return posts;
  }

  public String[] getPostDetails(String postId) {
    String query =
      "SELECT user_id, caption, like_count, timestamp FROM posts WHERE post_id = ?";
    String[] postDetails = new String[4];

    try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
      preparedStatement.setString(1, postId);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          postDetails[0] = resultSet.getString("user_id");
          postDetails[1] = resultSet.getString("caption");
          postDetails[2] = resultSet.getString("like_count");
          postDetails[3] = resultSet.getString("timestamp");
        }
      }
    } catch (SQLException e) {
      printSQLException(e);
    }

    return postDetails;
  }

  public void addPost(String postId, String userId, String caption) {
    String timestamp = LocalDateTime
      .now()
      .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    String query =
      "INSERT INTO posts (post_id, user_id, caption, timestamp) VALUES (?, ?, ?, ?)";

    try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
      preparedStatement.setString(1, postId);
      preparedStatement.setString(2, userId);
      preparedStatement.setString(3, caption);
      preparedStatement.setString(4, timestamp);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      printSQLException(e);
    }
    System.out.println("Post added to database.");
  }

  public String getUserIdFromPostId(String postId) {
    String userId = null;
    String query = "SELECT user_id FROM posts WHERE post_id = ?";
    try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
      preparedStatement.setString(1, postId);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          userId = resultSet.getString("user_id");
        }
      }
    } catch (SQLException e) {
      printSQLException(e);
    }
    return userId;
  }

  public int getPostCount(String userId) {
    int postCount = 0;
    String query = "SELECT COUNT(*) AS count FROM posts WHERE user_id = ?";

    try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
      preparedStatement.setString(1, userId);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          postCount = resultSet.getInt("count");
        }
      }
    } catch (SQLException e) {
      printSQLException(e);
    }

    return postCount;
  }

  public static int getFollowersCount(String currentUser) {
    Path followingFilePath = Paths.get("data", "following.txt");
    int followersCount = 0;
    try (
      BufferedReader followingReader = Files.newBufferedReader(
        followingFilePath
      )
    ) {
      String line;
      while ((line = followingReader.readLine()) != null) {
        String[] parts = line.split(":");
        if (parts.length == 2) {
          String username = parts[0].trim();
          String[] followingUsers = parts[1].split(";");
          if (username.equals(currentUser)) {} else {
            for (String followingUser : followingUsers) {
              if (followingUser.trim().equals(currentUser)) {
                followersCount++;
              }
            }
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return followersCount;
  }

  public static int getFollowingCount(String currentUser) {
    Path followingFilePath = Paths.get("data", "following.txt");
    int followingCount = 0;
    try (
      BufferedReader followingReader = Files.newBufferedReader(
        followingFilePath
      )
    ) {
      String line;
      while ((line = followingReader.readLine()) != null) {
        String[] parts = line.split(":");
        if (parts.length == 2) {
          String username = parts[0].trim();
          String[] followingUsers = parts[1].split(";");
          if (username.equals(currentUser)) {
            followingCount = followingUsers.length;
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return followingCount;
  }

  public static void printSQLException(SQLException ex) {
    for (Throwable e : ex) {
      if (e instanceof SQLException) {
        if (ignoreSQLException(((SQLException) e).getSQLState()) == false) {
          e.printStackTrace(System.err);
          System.err.println("SQLState: " + ((SQLException) e).getSQLState());
          System.err.println(
            "Error Code: " + ((SQLException) e).getErrorCode()
          );
          System.err.println("Message: " + e.getMessage());
          Throwable t = ex.getCause();
          while (t != null) {
            System.out.println("Cause: " + t);
            t = t.getCause();
          }
        }
      }
    }
  }

  public static boolean ignoreSQLException(String sqlState) {
    if (sqlState == null) {
      System.out.println("The SQL state is not defined!");
      return false;
    }
    // X0Y32: Jar file already exists in schema
    if (sqlState.equalsIgnoreCase("X0Y32")) return true;
    // 42Y55: Table already exists in schema
    if (sqlState.equalsIgnoreCase("42Y55")) return true;
    return false;
  }
}
