import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    public static void main(String[] args) {
        String connectionUrl = "jdbc:mysql://localhost:3306/quack";
        String username = "root";
        String password = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection myCon = DriverManager.getConnection(connectionUrl, username, password);

            // Perform database operations
            createTable(myCon);

            myCon.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createTable(Connection myCon) throws SQLException {
        String createString =
            "CREATE TABLE IF NOT EXISTS COFFEES (" +
            "COF_NAME VARCHAR(32) NOT NULL, " +
            "SUP_ID INT NOT NULL, " +
            "PRICE NUMERIC(10,2) NOT NULL, " +
            "SALES INT NOT NULL, " +
            "TOTAL INT NOT NULL, " +
            "PRIMARY KEY (COF_NAME) " +
            ");";
        try (Statement stmt = myCon.createStatement()) {
            stmt.executeUpdate(createString);
        } catch (SQLException e) {
            printSQLException(e);
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