package UIManager;

import UserManager.User;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;

public class NormalBio implements Bio {

  public NormalBio() {}

  public JLabel bioMaker(User user) {
    JLabel accountType = new JLabel(user.getType());
    accountType.setFont(new Font("Arial", Font.BOLD, 12));
    accountType.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); // Padding on the sides

    return accountType;
  }
}
