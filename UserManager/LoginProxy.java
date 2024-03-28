package UserManager;

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
    String bio
  ) {
    if (isValid(username, password)) {
      LoginManager.saveCredentials(username, password, bio);
    } else {
      System.out.println("Invalid username or password.");
    }
  }

  private static boolean isValid(String username, String password) {
    // Implement your security checks here
    // For example, check if username and password meet certain criteria
    return (
      username != null &&
      !username.isEmpty() &&
      password != null &&
      !password.isEmpty()
    );
  }
}
