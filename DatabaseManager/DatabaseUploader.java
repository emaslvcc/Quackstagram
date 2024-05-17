package DatabaseManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Properties;

public class DatabaseUploader {

  public static Connection conn = null;

  public DatabaseUploader() throws ClassNotFoundException, SQLException {
    conn = getConnection();
  }

  public static void main(String[] args)
    throws SQLException, ClassNotFoundException {
    conn = getConnection();
    // createTable();
    // populateTable();
    updateMultipleComments();
  }

  public static Connection getConnection()
    throws SQLException, ClassNotFoundException {
    String userName = "root";
    String password = "";
    String db_URL = "jdbc:mysql://localhost:3306/Quackstagram";

    Properties connectionProps = new Properties();
    connectionProps.put("user", userName);
    connectionProps.put("password", password);
    Class.forName("com.mysql.cj.jdbc.Driver");
    conn = DriverManager.getConnection(db_URL, connectionProps);

    System.out.println("Connected to database.");
    return conn;
  }

  public static void createTable() throws SQLException {
    String createString =
      "create table if not exists COFFEES (" +
      "COF_NAME varchar(32) NOT NULL, " +
      "SUP_ID int NOT NULL, " +
      "PRICE numeric(10,2) NOT NULL, " +
      "SALES integer NOT NULL, " +
      "TOTAL integer NOT NULL, " +
      "PRIMARY KEY (COF_NAME) " +
      "); ";
    try (Statement stmt = conn.createStatement()) {
      stmt.executeUpdate(createString);
    } catch (SQLException e) {
      DatabaseUploader.printSQLException(e);
    }
  }

  public static void populateTable() throws SQLException {
    try (Statement stmt = conn.createStatement()) {
      stmt.executeUpdate(
        "insert into COFFEES " + "values('Colombian', 00101, 7.99, 0, 0)"
      );
      stmt.executeUpdate(
        "insert into COFFEES " + "values('French_Roast', 00049, 8.99, 0, 0)"
      );
      stmt.executeUpdate(
        "insert into COFFEES " + "values('Espresso', 00150, 9.99, 0, 0)"
      );
      stmt.executeUpdate(
        "insert into COFFEES " + "values('Colombian_Decaf', 00101, 8.99, 0, 0)"
      );
      stmt.executeUpdate(
        "insert into COFFEES " +
        "values('French_Roast_Decaf', 00049, 9.99, 0, 0)"
      );
    } catch (SQLException e) {
      DatabaseUploader.printSQLException(e);
    }
  }

  public static void updateMultipleComments() {
    try (Statement stmt = conn.createStatement()) {
      Path filePath = Paths.get("data/comments.txt");
      Charset charset = StandardCharsets.UTF_8;

      try (
        BufferedReader bufferedReader = Files.newBufferedReader(
          filePath,
          charset
        )
      ) {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
          String[] separated = line.split("; ");
          String image_owner = separated[0];
          String image_commenter = separated[1];
          String image_id = separated[2];
          String comment = separated[3];
          String values =
            "values('" +
            image_owner +
            "', '" +
            image_commenter +
            "', '" +
            image_id +
            "', '" +
            comment +
            "')";
          stmt.executeUpdate("insert into COMMENTS " + values);
        }
      } catch (IOException ex) {
        System.out.format("I/O error: %s%n", ex);
      }
    } catch (SQLException e) {
      DatabaseUploader.printSQLException(e);
    }
    System.out.println("All comments uploaded to database.");
  }

  public void updateComment(
    String imageOwner,
    String currentUser,
    String imageId,
    String comment
  ) {
    try (Statement stmt = conn.createStatement()) {
      String values =
        "values('" +
        imageOwner +
        "', '" +
        currentUser +
        "', '" +
        imageId +
        "', '" +
        comment +
        "')";
      stmt.executeUpdate("insert into COMMENTS " + values);
    } catch (SQLException e) {
      DatabaseUploader.printSQLException(e);
    }
    System.out.println("Comment uploaded to database.");
  }

  public static void updateMultipleCredentials() {
    try (Statement stmt = conn.createStatement()) {
      Path filePath = Paths.get("data/comments.txt");
      Charset charset = StandardCharsets.UTF_8;

      try (
        BufferedReader bufferedReader = Files.newBufferedReader(
          filePath,
          charset
        )
      ) {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
          String[] separated = line.split("; ");
          String image_owner = separated[0];
          String image_commenter = separated[1];
          String image_id = separated[2];
          String comment = separated[3];
          String values =
            "values('" +
            image_owner +
            "', '" +
            image_commenter +
            "', '" +
            image_id +
            "', '" +
            comment +
            "')";
          stmt.executeUpdate("insert into COMMENTS " + values);
        }
      } catch (IOException ex) {
        System.out.format("I/O error: %s%n", ex);
      }
    } catch (SQLException e) {
      DatabaseUploader.printSQLException(e);
    }
  }

  public static void printSQLException(SQLException ex) {
    for (Throwable e : ex) {
      if (e instanceof SQLException) {
        if (ignoreSQLException(((SQLException) e).getSQLState()) == false) {
          e.printStackTrace(System.err);
          System.err.println("SQLState: " + ((SQLException) e).getSQLState());
          System.err.println(
            "Error Code: " + ((SQLException) e).getErrorCode()
          );
          System.err.println("Message: " + e.getMessage());
          Throwable t = ex.getCause();
          while (t != null) {
            System.out.println("Cause: " + t);
            t = t.getCause();
          }
        }
      }
    }
  }

  public static boolean ignoreSQLException(String sqlState) {
    if (sqlState == null) {
      System.out.println("The SQL state is not defined!");
      return false;
    }
    // X0Y32: Jar file already exists in schema
    if (sqlState.equalsIgnoreCase("X0Y32")) return true;
    // 42Y55: Table already exists in schema
    if (sqlState.equalsIgnoreCase("42Y55")) return true;
    return false;
  }
}
