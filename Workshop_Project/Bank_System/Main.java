import java.util.Scanner;
import java.sql.*; // Importing all SQL libraries

public class Main {

    // Main method to start the program
    public void main(String[] args) throws SQLException {

        // Creating a Scanner object to read user input
        Scanner scanner = new Scanner(System.in);

        // Database connection URL
        String url = "jdbc:mysql://localhost:3306/bank";  

        // SQL username
        String sql_user = "root";  

        // SQL password
        String sql_password = "";  

        // Creating an object of SqlDatabase class to use its methods
        SqlDatabase sqlDatabase = new SqlDatabase(); 

        // Establishing a connection to the SQL database
        Connection connection = sqlDatabase.connect(url,sql_user, sql_password); 

        // Infinite loop to continuously prompt the user for input
        while (true){
            // Prompting the user to choose their role
            System.out.print("\nBank Staff (S) or Bank Customer (C) or Exit (E) : ");
            String choice = scanner.nextLine();

            // If the user chooses to act as a bank staff
            if (choice.equalsIgnoreCase("S")) {
                // Creating an object of Bank_Staff class
                Bank_Staff bank_Staff = new Bank_Staff();
                // Calling the bank_Staff method to handle staff operations
                bank_Staff.bank_Staff(connection, scanner, sqlDatabase);
            }
            
            // If the user chooses to act as a bank customer
            else if (choice.equalsIgnoreCase("C")){
                // Creating an object of Bank_Customer class
                Bank_Customer bank_Customer = new Bank_Customer();
                // Calling the bankCustomer method to handle customer operations
                bank_Customer.bankCustomer(connection, scanner, sqlDatabase);
            }

            // If the user chooses to exit the program
            else if (choice.equalsIgnoreCase("E")){
                // Breaking out of the infinite loop to exit the program
                break;
            }

            // If the user enters an invalid choice
            else{
                System.out.println("Invalid choice!");
            }
        }
        // Closing the scanner and connection objects
        scanner.close();
        connection.close();
    }
}

class Bank_Customer{

    // Method to handle customer operations
    public void bankCustomer(Connection connection, Scanner scanner, SqlDatabase sqlDatabase) throws SQLException{

        // Initially, the password is invalid
        boolean pass = false; 

        // Table name for customer details
        String tableName = "customer_details";

        // Creating an object of SecurityCheck class
        SecurityCheck securityCheck = new SecurityCheck();

        // Calling the securityCheck method to verify the customer's login credentials
        String[] login_info = securityCheck.securityCheck(connection, scanner, sqlDatabase, tableName); 

        // Extracting the user ID and password from the login information
        String user_Id = login_info[0];
        pass = Boolean.parseBoolean(login_info[1]);

        // If the password is valid
        if (pass == true) {
            // Loop to continuously prompt the customer for operations
            while (pass) { 
                // Creating an object of BankService class
                BankService bankService = new BankService();
                // Calling the menu method to display the customer's menu
                pass = bankService.menu(scanner, connection, sqlDatabase, user_Id);
            }
        }
        else {
            // If the password is invalid, prompting the customer to try again
            System.out.println("Try to login again");
        }
    }
}

class Bank_Staff{

    // Method to handle staff operations
    public void bank_Staff(Connection connection, Scanner scanner, SqlDatabase sqlDatabase) throws SQLException {
        
        
        // Initially, the password is invalid
        boolean pass = false; 

        // Table name for staff details
        String tableName = "staff_details";

        // Creating an object of SecurityCheck class
        SecurityCheck securityCheck = new SecurityCheck();

        // Calling the securityCheck method to verify the staff's login credentials
        String[] login_info = securityCheck.securityCheck(connection, scanner, sqlDatabase, tableName); 

        // Extracting the user ID and password from the login information
        String user_Id = login_info[0];
        pass = Boolean.parseBoolean(login_info[1]);

        // If the password is valid
        if (pass == true) {
            // Loop to continuously prompt the staff for operations
            while (pass) { 
                // Creating an object of BankStaff class
                BankStaff bankStaff = new BankStaff();
                // Calling the bankStaffMenu method to display the staff's menu
                pass = bankStaff.bankStaffMenu(connection, scanner, sqlDatabase, user_Id);
            }
        }
        else {
            // If the password is invalid, prompting the staff to try again
            System.out.println("Try to login again");
        }

    }
}