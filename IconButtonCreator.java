import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class IconButtonCreator {
    private static final int NAV_ICON_SIZE = 20; // Size for navigation icons


    public static JButton createIconButton (String iconPath, String buttonType, ActionListener actionListener) {
        ImageIcon iconOriginal = new ImageIcon(iconPath);
        Image iconScaled = iconOriginal.getImage().getScaledInstance(NAV_ICON_SIZE, NAV_ICON_SIZE, Image.SCALE_SMOOTH);
        JButton button = new JButton(new ImageIcon(iconScaled));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        button.addActionListener(actionListener);
        return button;
    }
}
