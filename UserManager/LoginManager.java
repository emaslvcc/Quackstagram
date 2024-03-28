package UserManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class LoginManager {

  private static User newUser;

  public static void saveCredentials(
    String username,
    String password,
    String bio
  ) {
    try (
      BufferedWriter writer = new BufferedWriter(
        new FileWriter("data/credentials.txt", true)
      )
    ) {
      writer.write(username + ":" + password + ":" + bio + ":user");
      writer.newLine();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static boolean verifyCredentials(String username, String password) {
    try (
      BufferedReader reader = new BufferedReader(
        new FileReader("data/credentials.txt")
      )
    ) {
      String line;
      while ((line = reader.readLine()) != null) {
        String[] credentials = line.split(":");
        if (
          credentials[0].equals(username) && credentials[1].equals(password)
        ) {
          String bio = credentials[2];
          // Create User object and save information
          newUser = new User(username, bio, password); // Assuming User constructor takes these parameters
          saveUserInformation(newUser);

          return true;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }

  public static User getUser() {
    return newUser;
  }

  private static void saveUserInformation(User user) {
    try (
      BufferedWriter writer = new BufferedWriter(
        new FileWriter("data/users.txt", false)
      )
    ) {
      writer.write(user.toString()); // Implement a suitable toString method in User class
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
