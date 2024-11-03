import java.util.Scanner;
import java.sql.*;

public class BankStaff {

    // Displays the menu for bank staff and performs actions based on user input
    public boolean bankStaffMenu(Connection connection, Scanner scanner, SqlDatabase sqlDatabase, String User_ID) throws SQLException {

        boolean pass = true; // To determine if the user stays logged in

        // Display menu options
        System.out.println("\nSelect a method");
        System.out.println("0. Log Out");
        System.out.println("1. Open a new Account");
        System.out.println("2. View Account");
        System.out.print("Enter your choice: ");

        try {
            String choice = scanner.nextLine(); // Get user input

            switch (choice) {
                case "1": // If user selects to open a new account
                    OpenNewAccount openNewAccount = new OpenNewAccount();
                    openNewAccount.openNewAccount(connection, scanner, sqlDatabase);
                    break;

                case "2": // If user selects to view an account
                    ViewAccount viewAccount = new ViewAccount();
                    viewAccount.viewAccount(connection, scanner, sqlDatabase);
                    break;

                case "0": // If user selects to log out
                    System.out.println("Log Out");
                    pass = false;
                    break;

                default: // If user selects an invalid option
                    System.out.println("Invalid choice");
                    break;
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage()); // Handle any errors that occur
        }

        return pass; // Return whether the user is still logged in
    }
}

class OpenNewAccount {

    // Opens a new account and inserts details into the database
    public void openNewAccount(Connection connection, Scanner scanner, SqlDatabase sqlDatabase) {
        Hashing_Password hashing_Password = new Hashing_Password(); // For password hashing

        // Collect user details for the new account
        System.out.print("\nEnter your User ID: ");
        String User_ID = scanner.nextLine();

        System.out.print("Enter your User password (50 character limit): ");
        String User_Password = scanner.nextLine();
        User_Password = hashing_Password.hashPassword(User_Password); // Hash the password

        System.out.print("Enter your Name: ");
        String User_Name = scanner.nextLine();

        System.out.print("Enter your Parent Name: ");
        String User_Parent_Name = scanner.nextLine();

        System.out.print("Enter your Date Of Birth (yyyy-mm-dd): ");
        String User_DOB = scanner.nextLine();

        System.out.print("Enter your PAN Number: ");
        String User_PAN_Number = scanner.nextLine();

        System.out.print("Enter your Aadhar Number: ");
        String User_Aadhar_Number = scanner.nextLine();

        System.out.print("Enter your Address: ");
        String User_Address = scanner.nextLine();

        System.out.print("Enter your Phone Number: ");
        String User_Phone_Number = scanner.nextLine();

        System.out.print("Enter your Email: ");
        String User_Email = scanner.nextLine();

        System.out.print("Enter your Forgot question: ");
        String User_Forgot_Question = scanner.nextLine();

        System.out.print("Enter your Forgot Answer (50 character limit): ");
        String User_Forgot_Answer = scanner.nextLine();
        User_Forgot_Answer = hashing_Password.hashPassword(User_Forgot_Answer); // Hash the forgot answer

        System.out.print("Enter your Account Number: ");
        String User_Account_Number = scanner.nextLine();

        System.out.print("Enter your Account Type: ");
        String User_Account_Type = scanner.nextLine();

        System.out.print("Enter your Account Opening Date (yyyy-mm-dd): ");
        String User_Account_Opening_Date = scanner.nextLine();

        // Insert the collected details into the customer_details table
        try {
            String query = String.format(
                "Insert into customer_details values('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', 0.0, \"Active\" )",
                User_ID, User_Password, User_Name, User_Parent_Name, User_DOB, User_PAN_Number, User_Aadhar_Number, 
                User_Address, User_Phone_Number, User_Email, User_Forgot_Question, User_Forgot_Answer, 
                User_Account_Number, User_Account_Type, User_Account_Opening_Date
            );
            sqlDatabase.write(query, connection); // Write the query to the database
            System.err.println("\nNew Account created");
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage()); // Handle any errors that occur
        }
    }
}

class ViewAccount {

    // Allows staff to view details of a specific account by entering the Customer ID
    public void viewAccount(Connection connection, Scanner scanner, SqlDatabase sqlDatabase) {

        try {
            // Get the Customer ID for the account to be viewed
            System.out.print("\nEnter Customer ID: ");
            String userID = scanner.nextLine();

            // Retrieve account details from the customer_details table
            String query = "Select * from customer_details where User_ID = \"" + userID + "\" ;";
            ResultSet account_details = sqlDatabase.read(query, connection); // Execute the query

            // Check if any account details were returned
            if (account_details.next()) {
                ResultSetMetaData metaData = account_details.getMetaData();
                int columnCount = metaData.getColumnCount(); // Get the number of columns

                System.out.println("\n"); //prints a new line
                
                // Loop through all columns and print their values
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    String columnValue = account_details.getString(i);
                    if (columnName.equals("User_Password") || columnName.equals("User_Forgot_Answer") || columnName.equals("User_Forgot_Question") ) {
                        continue; // does not print the password and forgot question Answer
                    }
                    else{
                    System.out.println(columnName + ": " + columnValue); // Print column name and value
                    }
                }
            } else {
                System.out.println("\nAccount not found"); // If no account is found with the given Customer ID
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage()); // Handle any errors that occur
        }
    }
}
