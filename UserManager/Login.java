package UserManager;

public interface Login {
  public static boolean verifyCredentials(String user, String pass) {
    return false;
  }

  public static void saveUserInformation(User user) {}
}
