package DatabaseManager;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class UpdateDatabase {
    

    public static Connection myConn = getConnection();

    public UpdateDatabase() {
        myConn = getConnection();
    }
    public static void main(String[] args) {
        myConn = getConnection();
        //updateDatabase();
    }

    public static Connection getConnection() {
        try {String connectionUrl = "jdbc:mysql://localhost:3306/quack";
        String username = "root";
        String password = "";

        Properties connectionProps = new Properties();
        connectionProps.put("user", username);
        connectionProps.put("password", password);
        Class.forName("com.mysql.cj.jdbc.Driver");
        myConn = DriverManager.getConnection(connectionUrl, connectionProps);

        System.out.println("Connection to Database Established");
        System.out.println(myConn);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return myConn;
    }

    public static void updateComments(String comment_id, String post_id, String comment, String commenter) {
        try  {
            if (myConn == null) {
                myConn = getConnection();
            }
        } catch (Exception e) {

        }
            
        try (Statement stmt = myConn.createStatement()) {
            stmt.executeUpdate("INSERT INTO comments VALUES (" + comment_id + ", " + post_id + ", " + comment + ", " + commenter + ")");
        } catch (SQLException e) {
            UpdateDatabase.printSQLException(e);
        }
    }

    public static void updateLikes() {
        try (Statement stmt = myConn.createStatement()) {
            stmt.executeUpdate("");
        } catch (SQLException e) {
            UpdateDatabase.printSQLException(e);
        }
    }

    public static void updatePosts() {
        try (Statement stmt = myConn.createStatement()) {
            stmt.executeUpdate("");
        } catch (SQLException e) {
            UpdateDatabase.printSQLException(e);
        }
    }

    public static void updateUserFollowing() {
        try (Statement stmt = myConn.createStatement()) {
            stmt.executeUpdate("");
        } catch (SQLException e) {
            UpdateDatabase.printSQLException(e);
        }
    }

    public static void updateUserInfo() {
        try (Statement stmt = myConn.createStatement()) {
            stmt.executeUpdate("");
        } catch (SQLException e) {
            UpdateDatabase.printSQLException(e);
        }
    }
    

    public static void printSQLException(SQLException ex) {
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                if (!ignoreSQLException(((SQLException) e).getSQLState())) {
                    e.printStackTrace(System.err);
                    System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                    System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
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
        return sqlState.equalsIgnoreCase("X0Y32") || sqlState.equalsIgnoreCase("42Y55");
    }
}