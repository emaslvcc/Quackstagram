package UserManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for managing the relationships between users.
 */
public class UserRelationshipManager {

  private final String followersFilePath = "data/followers.txt";

  /**
   * Method to let a user follow another.
   *
   * @param follower Current user
   * @param followed User to follow
   * @throws IOException
   */
  public void followUser(String follower, String followed) throws IOException {
    if (!isAlreadyFollowing(follower, followed)) {
      try (
        BufferedWriter writer = new BufferedWriter(
          new FileWriter(followersFilePath, true)
        )
      ) {
        writer.write(follower + ":" + followed);
        writer.newLine();
      }
    }
  }

  /**
   * Method to check if a user is following another.
   *
   * @param follower Current user
   * @param followed User to follow
   * @return true or false if a user is following another already
   * @throws IOException
   */
  private boolean isAlreadyFollowing(String follower, String followed)
    throws IOException {
    try (
      BufferedReader reader = new BufferedReader(
        new FileReader(followersFilePath)
      )
    ) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.equals(follower + ":" + followed)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Method to get the list of a user's followers
   *
   * @param username user to check
   * @return returns all the users they follow
   * @throws IOException
   */
  public List<String> getFollowers(String username) throws IOException {
    List<String> followers = new ArrayList<>();
    try (
      BufferedReader reader = new BufferedReader(
        new FileReader(followersFilePath)
      )
    ) {
      String line;
      while ((line = reader.readLine()) != null) {
        String[] parts = line.split(":");
        if (parts[1].equals(username)) {
          followers.add(parts[0]);
        }
      }
    }
    return followers;
  }

  /**
   * Method to get the list of users a user is following.
   *
   * @param username
   * @return
   * @throws IOException
   */
  public List<String> getFollowing(String username) throws IOException {
    List<String> following = new ArrayList<>();
    try (
      BufferedReader reader = new BufferedReader(
        new FileReader(followersFilePath)
      )
    ) {
      String line;
      while ((line = reader.readLine()) != null) {
        String[] parts = line.split(":");
        if (parts[0].equals(username)) {
          following.add(parts[1]);
        }
      }
    }
    return following;
  }
}
