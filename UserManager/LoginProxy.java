package UserManager;

/**
 * Class to validate a login or sign up attempt before calling the actual methods.
 */
public class LoginProxy implements Login {

  public static boolean verifyCredentials(String username, String password) {
    if (isValid(username, password)) {
      return LoginManager.verifyCredentials(username, password);
    } else {
      System.out.println("Invalid username or password.");
      return false;
    }
  }

  public static void saveCredentials(
    String username,
    String password,
    String bio,
    String accountType
  ) {
    if (isValid(username, password)) {
      LoginManager.saveCredentials(username, password, bio, accountType);
    } else {
      System.out.println("Invalid username or password.");
    }
  }

  private static boolean isValid(String username, String password) {
    return (
      username != null &&
      !username.isEmpty() &&
      password != null &&
      !password.isEmpty()
    );
  }
}
