package UserManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class that manages logging in and adding new users.
 */
public class LoginManager implements Login {

  private static User newUser;

  public static User getUser() {
    return newUser;
  }

  public static void saveUserInformation(User user) {
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
