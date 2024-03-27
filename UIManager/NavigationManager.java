package UIManager;

// For future work, to add reactive navigation icons

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.*;

public class NavigationManager extends JFrame {

  final String iconLocation, homeIcon, searchIcon, addIcon, heartIcon, profileIcon, homeIconSelected, searchIconSelected, addIconSelected, heartIconSelected, profileIconSelected;

  public NavigationManager() {
    iconLocation = "img/icons/";
    homeIcon = iconLocation + "home.png";
    searchIcon = iconLocation + "search.png";
    addIcon = iconLocation + "add.png";
    heartIcon = iconLocation + "heart.png";
    profileIcon = iconLocation + "profile.png";
    homeIconSelected = iconLocation + "homeSelected.png";
    searchIconSelected = iconLocation + "searchSelected.png";
    addIconSelected = iconLocation + "addSelected.png";
    heartIconSelected = iconLocation + "heartSelected.png";
    profileIconSelected = iconLocation + "profileSelected.png";
  }
}
