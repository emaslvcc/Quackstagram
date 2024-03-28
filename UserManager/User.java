package UserManager;

import PostManager.Picture;
import java.util.ArrayList;
import java.util.List;

// Represents a user on Quackstagram
public class User {

  private String username;
  private String bio;
  private String password;
  private int postsCount;
  private int followersCount;
  private int followingCount;
  private List<Picture> pictures;
  private String type;

  public User(String username, String bio, String password, String type) {
    this.username = username;
    this.bio = bio;
    this.password = password;
    this.pictures = new ArrayList<>();
    // Initialize counts to 0
    this.postsCount = 0;
    this.followersCount = 0;
    this.followingCount = 0;
    this.type = type;
  }

  public User(String username) {
    this.username = username;
  }

  // Add a picture to the user's profile
  public void addPicture(Picture picture) {
    pictures.add(picture);
    postsCount++;
  }

  // Getter methods for user details
  public String getUsername() {
    return username;
  }

  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public int getPostsCount() {
    return postsCount;
  }

  public String getType() {
    return type;
  }

  public int getFollowersCount() {
    return followersCount;
  }

  public int getFollowingCount() {
    return followingCount;
  }

  public List<Picture> getPictures() {
    return pictures;
  }

  // Setter methods for followers and following counts
  public void setFollowersCount(int followersCount) {
    this.followersCount = followersCount;
  }

  public void setFollowingCount(int followingCount) {
    this.followingCount = followingCount;
  }

  public void setPostCount(int postCount) {
    this.postsCount = postCount;
  }

  // Implement the toString method for saving user information
  @Override
  public String toString() {
    return username + ":" + bio + ":" + password + ":" + type; // Format as needed
  }
}
