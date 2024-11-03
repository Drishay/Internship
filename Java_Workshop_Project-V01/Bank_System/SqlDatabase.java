import java.sql.*; // Import all SQL libraries

public class SqlDatabase{
    
    // Method to establish a connection with the database
    public Connection connect(String url, String sql_user, String sql_password) {
        Connection connection = null;
        try {
            // Attempt to establish a connection with the database
            connection = DriverManager.getConnection(url, sql_user, sql_password);
            System.out.println("Connection established " + connection);
        }
        
        catch (Exception e) {
            // If connection fails, print the error message and exit the program
            System.out.println("Connection not established due to " + e);
            System.exit(0); // If connection not established, the program will stop.
        }
        return connection;
    }


    // Method to execute a read query on the database
    public ResultSet read(String query, Connection connection) throws SQLException {

        ResultSet resultSet = null;
        try {
            // Create a statement object to execute the query
            Statement statement = connection.createStatement();
            // Execute the query and store the result in resultSet
            resultSet = statement.executeQuery(query); // Represents the result set of a query which is executed

        }
        catch(SQLException e){
            // If an error occurs during query execution, print the error message
            System.out.println("Error: " + e);
        }

        return resultSet; // After reading is done, resultSet will be returned
    }


    // Method to execute a write query on the database
    public void write (String query, Connection connection) throws SQLException {
        try {
            // Create a statement object to execute the query
            Statement statement = connection.createStatement();
            // Execute the query to update the database
            statement.executeUpdate(query);
        }
        catch(SQLException e){
            // If an error occurs during query execution, print the error message
            System.out.println("Error: " + e);
        }

    }

}