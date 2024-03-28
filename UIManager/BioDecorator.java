package UIManager;

import UserManager.User;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;

/**
 * Responsible for decorating the bio depending on a user's account type.
 */
public class BioDecorator implements Bio {

  public BioDecorator() {}

  public JLabel bioMaker(User user) {
    JLabel accountType = new JLabel(user.getType());
    accountType.setFont(new Font("Arial", Font.BOLD, 12));
    accountType.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); // Padding on the sides

    return accountType;
  }
}
