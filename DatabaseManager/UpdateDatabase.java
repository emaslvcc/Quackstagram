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
        try {
            String connectionUrl = "jdbc:mysql://localhost:3306/quack";
            String username = "root";
            String password = "";

            Properties connectionProps = new Properties();
            connectionProps.put("user", username);
            connectionProps.put("password", password);
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(connectionUrl, connectionProps);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void updateComments(String time_stamp, String post_id, String comment, String commenter) {
        try  {
            if (myConn == null) {
                myConn = getConnection();
            }
        } catch (Exception e) {
        } 

        try (Statement stmt = myConn.createStatement()) {
            String values =
            "VALUES('" +
            time_stamp +
            "', '" +
            post_id +
            "', '" +
            comment +
            "', '" +
            commenter+
            "')";
            stmt.executeUpdate("INSERT INTO comments " + values);
        } catch (SQLException e) {
            UpdateDatabase.printSQLException(e);
        }
    }

    public static void updateLikes(String imageId, String currentUser) {
        String query = "INSERT INTO likes (post_id, liker) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, imageId);
            pstmt.setString(2, currentUser);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updatePosts(String post_id, String owner, String caption, String time_stamp) {
        try  {
            if (myConn == null) {
                myConn = getConnection();
            }
        } catch (Exception e) {
        } 

        try (Statement stmt = myConn.createStatement()) {
            String values =
            "VALUES('" +
            post_id +
            "', '" +
            owner +
            "', '" +
            caption +
            "', '" +
            time_stamp +
            "')";
            stmt.executeUpdate("INSERT INTO posts " + values);
        } catch (SQLException e) {
            UpdateDatabase.printSQLException(e);
        }
    }

    public static void updateUserFollowing(String username1, String username2, String following_date) {
        try  {
            if (myConn == null) {
                myConn = getConnection();
            }
        } catch (Exception e) {
        } 

        try (Statement stmt = myConn.createStatement()) {
            String values =
            "VALUES('" +
            username1 +
            "', '" +
            username2 +
            "', '" + 
            following_date + 
            "')";
            stmt.executeUpdate("INSERT INTO user_following " + values);
        } catch (SQLException e) {
            UpdateDatabase.printSQLException(e);
        }
    }

    public static void updateUserInfo(String username, String userpass, String userbio, String type_of_account) {
        try  {
            if (myConn == null) {
                myConn = getConnection();
            }
        } catch (Exception e) {
        } 

        try (Statement stmt = myConn.createStatement()) {
            String values =
            "VALUES('" +
            username +
            "', '" +
            userpass +
            "', '" +
            userbio +
            "', '" +
            type_of_account +
            "')";
            stmt.executeUpdate("INSERT INTO user_info " + values);
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